package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import tool.clients.fmmlxdiagrams.Palette;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.NewFmmlxPalette;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class FmmlxDiagram {

	enum MouseMode {
		MULTISELECT, STANDARD, DRAW_EDGE
	}
	
	public static final boolean SHOW_MENUITEMS_IN_DEVELOPMENT = false;
	
	// The elements which the diagram consists of GUI-wis
	private SplitPane pane;
	private SplitPane mainView;
	private Canvas canvas;
	private ScrollPane scrollerCanvas;
	
	// The communication to the xmf and other actions
	private final FmmlxDiagramCommunicator comm;
	private DiagramActions actions;
	
	// The elements representing the model which is displayed in the GUI
	private Vector<FmmlxObject> objects = new Vector<>();
	private Vector<Edge> edges = new Vector<>();
	private Vector<DiagramEdgeLabel> labels = new Vector<>();
	private Vector<FmmlxEnum> enums = new Vector<>();
	
	// Temporary variables storing the current state of user interactions
	private transient Vector<CanvasElement> selectedObjects = new Vector<>();
	private ContextMenu activeContextMenu;
	private transient boolean objectsMoved = false;
	private transient PropertyType drawEdgeType = null;
	private transient Point2D lastPoint;
	private transient Point2D currentPoint;
	private transient MouseMode mouseMode = MouseMode.STANDARD;
	private transient FmmlxObject newEdgeSource;
	private transient FmmlxProperty lastHitProperty = null;
	private boolean diagramRequiresUpdate = false;
	
	// The state of the canvas is stored here:
	private Point2D canvasRawSize = new Point2D(1200, 800);
	private double zoom = 1.;
	private Affine transformFX = new Affine();
	private static Font font;
	private static Font fontKursiv;
	private static Font paletteFont;
	private static Font paletteFontKursiv;
	
	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;
	
	private final int diagramID;
	private transient boolean suppressRedraw;
	private final NewFmmlxPalette newFmmlxPalette;
	private String packagePath = null;
	
	public String updateID = null;
	
	String edgeCreationType = null;
	String nodeCreationType = null;


	FmmlxDiagram(FmmlxDiagramCommunicator comm, int diagramID, String label, String packagePath) {
		this.comm = comm;
		this.diagramID = diagramID;
		this.packagePath = packagePath;
		System.out.println("packagePath: " + packagePath);
		
		pane = new SplitPane();
		mainView = new SplitPane();
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		actions = new DiagramActions(this);
		Palette palette = new Palette(actions);
		newFmmlxPalette = new NewFmmlxPalette(this);
		scrollerCanvas = new ScrollPane(canvas);
		pane.setOrientation(Orientation.HORIZONTAL);
		pane.setDividerPosition(0, 0.25);
		mainView.setOrientation(Orientation.VERTICAL);
		mainView.getItems().addAll(palette, scrollerCanvas);
		mainView.setDividerPosition(0, 0.2);
		
		pane.getItems().addAll(newFmmlxPalette.getToolBar(), mainView);

		canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseDragged(this::mouseDragged);
		canvas.setOnMouseReleased(this::mouseReleased);
		canvas.setOnMouseMoved(this::mouseMoved);
		canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);

		new Thread(this::fetchDiagramData).start();

		try {
			font = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono.ttf"), 14);
			fontKursiv = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono-Oblique.ttf"), 14);
			paletteFont = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSans.ttf"), 12);
			paletteFontKursiv =Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono-Oblique.ttf"), 12);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//redraw();
		
		java.util.Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				redraw();
			}
		}, 25, 25);
		
	}

	// Only used to set the mouse pointer. Find a better solution
	@Deprecated
	public Canvas getCanvas() {
		return canvas;
	}

	public Vector<FmmlxEnum> getEnums() {
		return enums;
	}
	
	public void deselectPalette() {
		edgeCreationType = null;
		nodeCreationType = null;
		newFmmlxPalette.clearSelection();
	}
	
	public NewFmmlxPalette getPalette() {
		return newFmmlxPalette;
	}
	
	
	public void setEdgeCreationType(String edgeCreationType) {
		this.edgeCreationType = edgeCreationType;
		this.nodeCreationType= null;
	}
	
	public void setNodeCreationType(String nodeCreationType) {
		this.nodeCreationType = nodeCreationType;
		this.edgeCreationType = null;
	}
	

//	public void setEnums(Vector<FmmlxEnum> enums) {
//	}

	private synchronized void fetchDiagramData() {
		if(suppressRedraw) {
			System.err.println("\talready fetching diagram data");
			return;
		}
		suppressRedraw = true;
		try {
			
	//		System.err.println("suppressRedraw");
			
			objects.clear();
			edges.clear();
			labels.clear();
			enums.clear();
	
			Vector<FmmlxObject> fetchedObjects = comm.getAllObjects(this);
			objects.addAll(fetchedObjects);
			
			for(FmmlxObject o : objects) {
				o.fetchDataDefinitions(comm);
			}
			
			for(FmmlxObject o : objects) {
				o.fetchDataValues(comm);
				o.layout(this);
			}
			
			Vector<Edge> fetchedEdges = comm.getAllAssociations(this);
			fetchedEdges.addAll(comm.getAllAssociationsInstances(this));
	
			edges.addAll(fetchedEdges);
			edges.addAll(comm.getAllInheritanceEdges(this));
			
			enums = comm.fetchAllEnums(this);
			
			triggerOverallReLayout();
			
			resizeCanvas();
			
	//		System.err.println("allowRedraw");
	//		redraw();
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
		suppressRedraw = false;
		redraw();

		newFmmlxPalette.clearAllGroup();
		newFmmlxPalette.populate();
	}

	// This operation resets the size of the canvas when needed
	private void resizeCanvas() {
		try {
			double maxRight = canvasRawSize.getX();
			double maxBottom = canvasRawSize.getY();

			for (FmmlxObject object : objects) {
				maxRight = Math.max(maxRight, object.getMaxRight());
				maxBottom = Math.max(maxBottom, object.getMaxBottom());
			}
			canvasRawSize = new Point2D(maxRight, maxBottom);
			Point2D canvasScreenSize = transformFX.transform(canvasRawSize);
			canvas.setWidth(Math.min(4096, canvasScreenSize.getX() + 5));
			canvas.setHeight(Math.min(4096, canvasScreenSize.getY() + 5));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Only used to set the diagram into the tab. Find a better solution
	@Deprecated
	public SplitPane getView() {
		return pane;
	}
	
	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

	private void updateDiagramLater() {
		diagramRequiresUpdate = true;
	}
	
	public void redraw() {
		System.out.println("redraw");
		if (suppressRedraw) {
			return;}
		if (objects.size() <= 0) {return;}
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			// we are on the right Thread already:
			paintOn(canvas.getGraphicsContext2D(), 0, 0);
		} else { // create a new Thread
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				paintOn(canvas.getGraphicsContext2D(), 0, 0);
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	

	private void paintOn(GraphicsContext g, int xOffset, int yOffset) {
		g.setTransform(new Affine());
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(Color.BLACK);
		g.setTransform(transformFX);
		Vector<CanvasElement> objectsToBePainted = new Vector<>();
		objectsToBePainted.addAll(objects);
		objectsToBePainted.addAll(labels);
		objectsToBePainted.addAll(edges);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objects) {
			o.updatePortOder();
		}
		for (CanvasElement o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}
		drawMultiSelectRect(g);
		drawNewEdgeLine(g);
	}

	private void drawMultiSelectRect(GraphicsContext g) {
		if (mouseMode == MouseMode.MULTISELECT) {
			double x = Math.min(lastPoint.getX(), currentPoint.getX());
			double y = Math.min(lastPoint.getY(), currentPoint.getY());

			g.strokeRect(x, y, Math.abs(currentPoint.getX() - lastPoint.getX()), Math.abs(currentPoint.getY() - lastPoint.getY()));
		}
	}

	private void drawNewEdgeLine(GraphicsContext g) {
		if (mouseMode == MouseMode.DRAW_EDGE) {
			g.strokeLine(lastPoint.getX(), lastPoint.getY(), currentPoint.getX(), currentPoint.getY());
		}
	}

	////////////////////////////////////////////////////////////////////
	////						MouseListener						////
	////////////////////////////////////////////////////////////////////
	private void mousePressed(MouseEvent e) {
		if(suppressRedraw) return; 
        suppressRedraw = false;
		Point2D p = scale(e);
		clearContextMenus();

//		if (isMiddleClick(e)) {
//			selectedObjects.addAll(objects);
//		}
		if (isLeftButton(e)) {
			handleLeftPressed(e);
		}
		if (isRightButton(e)) {
			handleRightClick(e);
		}
		setMouseOffset(p);
		//redraw();
	}

	private void mouseDragged(MouseEvent e) {
		Point2D p = scale(e);

		if (mouseMode == MouseMode.MULTISELECT) {
			storeCurrentPoint(p.getX(), p.getY());
			redraw();
		}
		if (mouseMode == MouseMode.STANDARD) {
			if (selectedObjects.size() == 1 && selectedObjects.firstElement() instanceof Edge) {
				((Edge) selectedObjects.firstElement()).setPointAtToBeMoved(p);

			}
			mouseDraggedStandard(p);
		}
	}

	private transient CanvasElement lastElementUnderMouse = null;
	
	private void mouseMoved(MouseEvent e) {
		Point2D p = scale(e);;
		if (mouseMode == MouseMode.DRAW_EDGE) {
			storeCurrentPoint(p.getX(), p.getY());
			redraw();
		}
		
		CanvasElement elementUnderMouse = getElementAt(p.getX(), p.getY());
		if(elementUnderMouse != lastElementUnderMouse) {
			lastElementUnderMouse = elementUnderMouse;
			for (FmmlxObject o : objects)
				o.unHighlight();
			for (Edge edge : edges)
				edge.unHighlight();
			for (DiagramEdgeLabel l : labels)
				l.unHighlight();
		}
		
		if(elementUnderMouse != null) elementUnderMouse.highlightElementAt(p);
		
	}

	private void mouseDraggedStandard(Point2D p) {
//		if (hitObject != null) {
		for (CanvasElement s : selectedObjects)
			if (s instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) s;
				s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
				for(Edge e : edges) {
					if(e.isStartNode(o) || e.isEndNode(o)) e.align();
				}
			} else if (s instanceof DiagramEdgeLabel) {
				DiagramEdgeLabel o = (DiagramEdgeLabel) s;
				s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
			} else { // must be edge
				s.moveTo(p.getX(), p.getY(), this);
			}
		objectsMoved = true;
        
		for(Edge e : edges) {e.align();}

		redraw();
		
	}

	private void mouseReleased(MouseEvent e) {
		if(mouseMode == MouseMode.MULTISELECT) {
			handleMultiSelect();
		}
		if(mouseMode == MouseMode.STANDARD) {
			mouseReleasedStandard();
		} 
		if(mouseMode!=MouseMode.DRAW_EDGE) {
			mouseMode=MouseMode.STANDARD;
		}
		
		for(Edge edge : edges) {
			edge.dropPoint();
		}
		
		triggerOverallReLayout();

		resizeCanvas();
		if(diagramRequiresUpdate) {
			diagramRequiresUpdate = false;
			updateDiagram();
		}
		//redraw();
	}

	private void triggerOverallReLayout() {
		for(int i = 0; i < 3; i++) {
			for(FmmlxObject o : objects) {
				o.layout(this);
			}
			for(Edge edge : edges) {
				edge.align();
			}
		}
	}

	private void mouseReleasedStandard() {
		for (Edge e : edges) e.removeRedundantPoints();
		
		if (objectsMoved) {
			for (CanvasElement s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					comm.sendCurrentPosition(this, o);
					for(Edge e : edges) {
						if(e.isStartNode(o) || e.isEndNode(o)) {
							comm.sendCurrentPositions(this, e);
						}
					}
				} else if (s instanceof Edge) {

					comm.sendCurrentPositions(this, (Edge) s);
				} else if (s instanceof DiagramEdgeLabel) {
					comm.storeLabelInfo(this, (DiagramEdgeLabel) s);
				}
		}
		objectsMoved = false;

	}

	private boolean isLeftButton(MouseEvent e) {return e.getButton() == MouseButton.PRIMARY;}
	private boolean isRightButton(MouseEvent e) {return e.getButton() == MouseButton.SECONDARY;}
//	private boolean isMiddleClick(MouseEvent e) {return e.getButton() == MouseButton.MIDDLE;}

	private CanvasElement getElementAt(double x, double y) {
		for (FmmlxObject o : objects)
			if (o.isHit(x, y))
				return o;
		for (Edge e : edges)
			if (e.isHit(x, y))
				return e;
		for (DiagramEdgeLabel l : labels)
			if (l.isHit(x, y))
				return l;
		
		return null;
	}
	
	private void handleLeftPressedDefault(MouseEvent e, CanvasElement hitObject) {
		Point2D p = scale(e);

		if (hitObject != null) {
			if (mouseMode == MouseMode.DRAW_EDGE) {
				mouseMode = MouseMode.STANDARD;
				FmmlxObject newEdgeTarget = hitObject instanceof FmmlxObject ? (FmmlxObject) hitObject : null;
				switch (drawEdgeType) {
					case Association:
						actions.addAssociationDialog(newEdgeSource, newEdgeTarget);
						break;
					case AssociationInstance:
						final FmmlxObject obj1 = newEdgeSource;
						final FmmlxObject obj2 = newEdgeTarget;
						Platform.runLater(() -> {
							actions.addAssociationInstance(obj1, obj2);
							updateDiagramLater();
						});						
						break;
					default:
						break;
				}
				deselectAll();
			}

			if (e.isControlDown()) {
				if (selectedObjects.contains(hitObject)) {
					selectedObjects.remove(hitObject);
				} else {
					selectedObjects.add(hitObject);
				}
			} else {
				if (!selectedObjects.contains(hitObject)) {
					selectedObjects.clear();
					selectedObjects.add(hitObject);
					highlightElementAt(hitObject, p);
				}
			}
			handlePressedOnNodeElement(p, hitObject);

			if (e.getClickCount() == 2) {
				handleDoubleClickOnNodeElement(p, hitObject);
			}
		} else {
			if (mouseMode == MouseMode.DRAW_EDGE) {
				switch (drawEdgeType) {
					case Association:
						mouseMode = MouseMode.STANDARD;
						actions.addAssociationDialog(newEdgeSource, null);
						break;
					case AssociationInstance:
						mouseMode = MouseMode.STANDARD;
						actions.addAssociationInstance(newEdgeSource, null);
						break;
					default:
						break;
				}
			} else {	
				mouseMode = MouseMode.MULTISELECT;
				storeLastClick(p.getX(), p.getY());
				storeCurrentPoint(p.getX(), p.getY());
			}
		}		
	}

	private void handleLeftPressed(MouseEvent e) {
		Point2D p = scale(e);
		CanvasElement hitObject = getElementAt(p.getX(), p.getY());
		
		if (nodeCreationType == null && edgeCreationType == null) {
			handleLeftPressedDefault(e, hitObject);		
			
		} else if (edgeCreationType != null) {			
			if (edgeCreationType=="association") {
				hitObject = getElementAt(p.getX(), p.getY());
				if(hitObject instanceof FmmlxObject) {		
					actions.setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.Association);
				}
			} else if (edgeCreationType=="associationInstance") {
				if(hitObject instanceof FmmlxObject) {
					actions.setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.AssociationInstance);
				}
			}
		} else if (nodeCreationType != null) {
			if (nodeCreationType=="metaClass") {
				actions.addMetaClassDialog(e);
				deselectAll();
			} else {
				actions.addInstanceDialog(getObjectById(Integer.parseInt(nodeCreationType)),e);
				deselectAll();
			}
		}
	}

	private void highlightElementAt(CanvasElement hitObject, Point2D p) {
		for (CanvasElement object : objects) {
			object.highlightElementAt(null);
		}
		for (Edge object : edges) {
			object.highlightElementAt(null);
		}
		hitObject.highlightElementAt(p);
	}

	private void handlePressedOnNodeElement(Point2D p, CanvasElement hitObject) {
		if (hitObject instanceof FmmlxObject) {
			FmmlxObject obj = (FmmlxObject) hitObject;
			Point2D relativePoint = new Point2D(p.getX() - obj.getX(), p.getY() - obj.getY());
			lastHitProperty = obj.handlePressedOnNodeElement(relativePoint);
		}
	}

	private void handleDoubleClickOnNodeElement(Point2D p, CanvasElement hitObject) {
		if (hitObject != null && hitObject instanceof FmmlxObject) {
			FmmlxObject obj = (FmmlxObject) hitObject;
			Point2D relativePoint = new Point2D(p.getX() - obj.getX(), p.getY() - obj.getY());
			obj.performDoubleClickAction(relativePoint);
		} else if (hitObject instanceof DiagramEdgeLabel) {
			DiagramEdgeLabel l = (DiagramEdgeLabel) hitObject;
			l.performAction();
		}
	}

	private void handleRightClick(MouseEvent e) {
		Point2D p = scale(e);
		CanvasElement hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
			if (hitObject instanceof FmmlxObject) {
				activeContextMenu = hitObject.getContextMenu(actions);
			} else if (hitObject instanceof Edge) {
				activeContextMenu = hitObject.getContextMenu(actions);
			}
			if (!selectedObjects.contains(hitObject)) {
				deselectAll();
				selectedObjects.add(hitObject);
			}
		} else {
			activeContextMenu = new DefaultContextMenu(actions);
		}
		showContextMenu(e);
	}


	private void handleScroll(ScrollEvent e) {
		if (e.isControlDown()) {
			double delta = e.getDeltaY();
			if (delta > 0) {
				actions.zoomIn();
			} else {
				actions.zoomOut();
			}
		}
	}

	/* Setters for MouseMode */

	public void setDrawEdgeMouseMode(PropertyType type, FmmlxObject newEdgeSource) {
		drawEdgeType = type;
		mouseMode = MouseMode.DRAW_EDGE;
		this.newEdgeSource = newEdgeSource;
	}

	public void setStandardMouseMode() {
		mouseMode = MouseMode.STANDARD;
	}

	////////////////////////////////////////////////////////////////////
	private void clearContextMenus() {
		if (activeContextMenu != null && activeContextMenu.isShowing()) {
			activeContextMenu.hide();
		}
	}

	private void showContextMenu(MouseEvent p) {
		if (activeContextMenu != null) {
			activeContextMenu.show(canvas, Side.LEFT, p.getX(), p.getY());
		}
	}

	public void storeLastClick(double x, double y) {
		lastPoint = new Point2D(x, y);
	}

	private void storeCurrentPoint(double x, double y) {
		currentPoint = new Point2D(x, y);
	}

	private void setMouseOffset(Point2D p) {
		for (CanvasElement s : selectedObjects)
			s.setOffsetAndStoreLastValidPosition(p);
	}

	public boolean isSelected(CanvasElement element) {
		return selectedObjects.contains(element);
	}

	void deselectAll() {
		deselectPalette();
		selectedObjects.clear();
//		if (lastHitLabel != null) {
//			lastHitLabel.setDeselected();
//			lastHitLabel = null;
//		}
	}

	public void setSelectedObject(FmmlxObject source) {
		deselectAll();
		selectedObjects.add(source);
	}

	private void select(FmmlxObject o) {
		if (!selectedObjects.contains(o)) {
			selectedObjects.add(o);
		}
	}

	public void updateDiagram() {
		new Thread(this::fetchDiagramData).start();
	}

	private void handleMultiSelect() {
		double x = Math.min(lastPoint.getX(), currentPoint.getX());
		double y = Math.min(lastPoint.getY(), currentPoint.getY());
		double w = Math.abs(currentPoint.getX() - lastPoint.getX());
		double h = Math.abs(currentPoint.getY() - lastPoint.getY());

		Rectangle rec = new Rectangle(x, y, w, h);
		deselectAll();
		for (FmmlxObject o : objects) {
			if (isObjectContained(rec, o)) {
				select(o);
			}
		}

		mouseMode = MouseMode.STANDARD;
	}

	private boolean isObjectContained(Rectangle rec, FmmlxObject object) {
		return rec.contains(object.getX(), object.getY())
				&& rec.contains(
				object.getX() + object.getWidth(),
				object.getY() + object.getHeight());
	}

	public Point2D scale(MouseEvent event) {
		Affine i;
		try {
			i = transformFX.createInverse();
			return i.transform(event.getX(), event.getY());
		} catch (NonInvertibleTransformException e) {
			e.printStackTrace();
			return new javafx.geometry.Point2D(event.getX(), event.getY());
		}
	}

	public FmmlxProperty getSelectedProperty() {
		return lastHitProperty;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = Math.min(4, Math.max(zoom, 1. / 8));

		transformFX = new Affine();
		transformFX.appendScale(zoom, zoom);
		resizeCanvas();
	}

	public Font getFont() {
		return font;
	}
	
	public Font getFontKursiv() {
		return fontKursiv;
	}
	
	public Font getPaletteFont() {
		return paletteFont;
	}
	
	public Font getPaletteFontKursiv() {
		return paletteFontKursiv;
	}

	
	public Vector<FmmlxLink> getAssociationInstance(){
		Vector<FmmlxLink> result = new Vector<FmmlxLink>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxLink) {
				result.add((FmmlxLink) tmp);
			}
		}
		return result; // read-only
	}
	
	@Deprecated
	//needs filter
	public Vector<FmmlxAssociation> getAssociations() {
		Vector<FmmlxAssociation> result = new Vector<FmmlxAssociation>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				result.add((FmmlxAssociation) tmp);
			}
		} 
		return result; // read-only
	}

	/**
	 * Calculates the height of the text. Because that depends of the font size and the screen resolution
	 * @return the text height
	 */
	public static double calculateTextHeight() {
		Text t = new Text("TestText");
		t.setFont(font);
		return t.getLayoutBounds().getHeight();
	}

	public static double calculateTextWidth(String text) {
		Text t = new Text(text);
		t.setFont(font);
		return t.getLayoutBounds().getWidth();
	}

	// TODO: delete and use method with level
	public ObservableList<FmmlxObject> getAllPossibleParentList() {
		ArrayList<FmmlxObject> objectList = new ArrayList<FmmlxObject>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					objectList.add(object);
				}
			}
		}
		ObservableList<FmmlxObject> result = FXCollections.observableArrayList(objectList);
		return result;
	}

	public ObservableList<FmmlxObject> getAllPossibleParents(Integer level) {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (level != 0 && object.getLevel() == level) {
					objectList.add(object);
				}
			}
		}
		ObservableList<FmmlxObject> result = FXCollections.observableArrayList(objectList);
		return result;
	}
	
	public void addLabel(DiagramEdgeLabel diagramLabel) {
		labels.add(diagramLabel);
	}
	
	public Vector<FmmlxAssociation> findAssociations(FmmlxObject source, FmmlxObject target) {
		Vector<FmmlxAssociation> result = new Vector<FmmlxAssociation>();
		for (Edge e : edges)
			if (e instanceof FmmlxAssociation) {
				FmmlxAssociation association = (FmmlxAssociation) e;
				if (association.doObjectsFit(source, target)) result.add(association);
			}
		return result;
	}
	
	////////////////////////////////////////////////////////////////////
	////					Messages to XMF							////
	////////////////////////////////////////////////////////////////////


	// Some useful methods for queries:
	
	public Vector<FmmlxObject> getObjects() {
		return new Vector<FmmlxObject>(objects); // read-only
	}
	
	public Vector<Edge> getEdges() {
		return new Vector<Edge>(edges); // read-only
	}
	
	public Vector<DiagramEdgeLabel> getLabels() {
		return new Vector<DiagramEdgeLabel>(labels); // read-only
	}

	public FmmlxObject getObjectById(int id) {
		for (FmmlxObject object : objects) {
			if (object.getId() == id)
				return object;
		}
		return null;
	}
	
	public Vector<FmmlxObject> getObjectsByLevel(int level){
		Vector<FmmlxObject> result = new Vector<FmmlxObject>();
		
		for (FmmlxObject object : objects) {
			if (object.getLevel()==level) {
				result.add(object);
			}
		}
		return result;
	}
	
	public Edge getAssociationById(int id) {
		for (Edge tmp : edges) {
			if (tmp.getId() == id)
				return tmp;
		}
		return null;
	}
	
	public Object getAllMetaClass() {
		Vector<FmmlxObject> result = new Vector<FmmlxObject>();
		for (FmmlxObject object : getObjects()) {
			if (object.getLevel() != 0) {
				result.add(object);
			}
		}
		return result;
	}
	
	public Vector<FmmlxAssociation> getRelatedAssociationByObject(FmmlxObject object) {
		Vector<FmmlxAssociation> result = new Vector<FmmlxAssociation>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxAssociation) {
				if (tmp.sourceNode.getId() == object.getId() || tmp.targetNode.getId() == object.getId()) {
					result.add((FmmlxAssociation) tmp);
				}
			}
		}
		return result;
	}
	
	public boolean isNameAvailable(String t) {
		for (FmmlxObject o : objects) if (o.getName().equals(t)) return false;
		return true;
	}

	public void setCursor(Cursor c) {
		canvas.setCursor(c);
	}
	
	public void setShowOperations(boolean show) {this.showOperations = show;}	
	public void setShowOperationValues(boolean show) {this.showOperationValues = show;}	
	public void setShowSlots(boolean show) {this.showSlots = show;}
	
	public boolean isShowOperations() {return this.showOperations;}	
	public boolean isShowOperationValues() {return this.showOperationValues;}	
	public boolean isShowSlots() {return this.showSlots;}

	public int getID() {
		return diagramID;
	}

	public Vector<String> getAvailableTypes() {
		Vector<String> types = new Vector<String>();
		types.add("Boolean");
		types.add("Integer");
		types.add("Float");
		types.add("String");
		for(FmmlxEnum e : enums) {
			types.add(e.getName());
		}
		return types;
	}

	public boolean isEnum(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return true;
		}
		return false;
	}

	public Vector<String> getEnumItems(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e.getItems();
		}
		return null;
	}

	public synchronized void updateEnums() {
		try {
			enums.clear();
	
			Vector<FmmlxObject> fetchedObjects = comm.getAllObjects(this);
			objects.addAll(fetchedObjects);
			
			Vector<Edge> fetchedEdges = comm.getAllAssociations(this);
			fetchedEdges.addAll(comm.getAllAssociationsInstances(this));
	
			edges.addAll(fetchedEdges);
			
			enums = comm.fetchAllEnums(this); }
		catch (TimeOutException e) {
			e.printStackTrace();
		}
	}

	public FmmlxEnum getEnum(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e;
		}
		return null;
	}
	public int getMaxLevel() {
		int level = 0;
		for (FmmlxObject tmp : objects) {
			if(tmp.getLevel()>level) {
				level=tmp.getLevel();
			}
		}
		return level;
	}

	public Vector<Point2D> findEdgeIntersections(Point2D a, Point2D b) { // only interested in a-b horizontal crossing c-d vertical
		Vector<Point2D> result = new Vector<Point2D>();
		for(Edge e : edges) {
			if(e.isVisible()) {
				Vector<Point2D> otherPoints = e.getAllPoints();
				for(int i = 0; i < otherPoints.size()-1; i++) {
					Point2D c = otherPoints.get(i);
					Point2D d = otherPoints.get(i+1);
					if(a != c && b != d && a != d && b != c) {
						if(a.getY() == b.getY()) { // possibly redundant
							if(c.getX() == d.getX()) {
								// check for intersection
								if((c.getY() < a.getY()) != (d.getY() < a.getY())) { // if c and d are on different sides of a/b (y)
									if((a.getX() < c.getX()) != (b.getX() < c.getX())) { // if a and b are on different sides of c/d (x)
										result.add(new Point2D(c.getX(), a.getY()));
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public InheritanceEdge getInheritanceEdge(FmmlxObject child, FmmlxObject parent) {
		for(Edge e : edges) {
			if(e instanceof InheritanceEdge) {
				InheritanceEdge i = (InheritanceEdge) e;
				if(i.isStartNode(child) && i.isEndNode(parent)) return i;
			}
		}
		return null;
	}

	public DiagramActions getActions() {
		return actions;
	}
	
	public String convertPath2Short(String typePath) {
		String[] prefixes = new String[]{packagePath, "Root::XCore", "Root"};
			for(String prefix : prefixes) {
				if(typePath.startsWith(prefix)) {
					return typePath.substring(prefix.length()+2);
				}
			}
		return typePath;
	}
}
