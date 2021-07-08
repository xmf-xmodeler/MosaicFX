package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.FileChooser;
import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.FmmlxPalette;
import tool.clients.fmmlxdiagrams.newpalette.NewFmmlxPalette;
import tool.clients.serializer.FmmlxDeserializer;
import tool.clients.serializer.XmlManager;
import tool.clients.xmlManipulator.XmlHandler;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;


public class FmmlxDiagram extends AbstractPackageViewer{

	enum MouseMode {
		MULTISELECT, STANDARD, DRAW_EDGE
	}

	public static final boolean SHOW_MENUITEMS_IN_DEVELOPMENT = false;

	// The elements which the diagram consists of GUI-wise
	private SplitPane pane;
	private SplitPane mainView;
	private Canvas canvas;
	private ScrollPane scrollerCanvas;
	private VBox vBox;
	private Menu menu;
	private MenuBar menuBar;
	private MenuItem loadXML;
	private MenuItem saveXML;
	// The communication to the xmf and other actions

	// The elements representing the model which is displayed in the GUI

	private Vector<DiagramEdgeLabel<?>> labels = new Vector<>();


	// Temporary variables storing the current state of user interactions
	private transient Vector<CanvasElement> selectedObjects = new Vector<>();
	private ContextMenu activeContextMenu;
	public transient boolean objectsMoved = false;
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
	public static final Font FONT;

	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;
	private boolean showGetterAndSetter = true;
	private boolean showDerivedOperations=true;
	private boolean showDerivedAttributes=true;
	@Override protected boolean loadOnlyVisibleObjects() { return true; }	

	public final String diagramName;
	private final FmmlxPalette newFmmlxPalette;
	private String filePath;


	public String updateID = null;

	String edgeCreationType = null;
	String nodeCreationType = null;

	public LevelColorScheme levelColorScheme = new LevelColorScheme.FixedBlueLevelColorScheme();

	public final static FmmlxDiagram NullDiagram = new FmmlxDiagram();

	static{
		FONT = Font.font(Font.getDefault().getFamily(), FontPosture.REGULAR, 14);
//		try {
//
//	//		font = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono.ttf"), 14);
////			fontKursiv = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono-Oblique.ttf"), 14);
//	//		paletteFont = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSans.ttf"), 12);
////			paletteFontKursiv =Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono-Oblique.ttf"), 12);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private FmmlxDiagram() {
		super(null,-1,null);
//		this.comm = null;
		this.newFmmlxPalette = null;
		this.diagramName = null;
	}

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, int diagramID, String name, String packagePath) {
//		this.comm = comm;
		super(comm,diagramID,packagePath);
		this.diagramName = name;
		vBox = new VBox();
		menu = new Menu("Save & Load");
		menuBar = new MenuBar();
		loadXML = new MenuItem("Load Diagram via XML");
		saveXML = new MenuItem("Save Diagram as XML");
		saveXML.setOnAction(e->saveXMLAction());
		loadXML.setOnAction(e->loadXMLAction());
		menu.getItems().addAll(loadXML, saveXML);
		menuBar.getMenus().add(menu);
		pane = new SplitPane();
		vBox.getChildren().addAll(menuBar, pane);
		mainView = new SplitPane();
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		Palette palette = new Palette(this);
		newFmmlxPalette = new FmmlxPalette(this);
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

		java.util.Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				redraw();
			}
		}, 25, 25);
	}

	private void loadXMLAction() {
		ChoiceDialog<String> dialog = new ChoiceDialog<>();
		dialog.setTitle("Load XML File");
		dialog.setHeaderText(null);
		dialog.setContentText("Choose one of your saved XML files:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    
		}
		
		File tempDirectory = new File("");
		tempDirectory=new File(tempDirectory.toURI()).getParentFile();
		//boolean exists = tempDirectory.exists();
		
		String dirLocation = tempDirectory.toString();
		System.err.println("Directory: " + tempDirectory.toString());
        try {
            List<File> files = Files.list(Paths.get(dirLocation))
                                    .filter(Files::isRegularFile)
                                    .filter(path -> path.toString().endsWith(".xml"))
                                    .map(Path::toFile)
                                    .collect(Collectors.toList());
           
            files.forEach(System.err::println);
            
        } catch (IOException e) {
            // Error while reading the directory
        }

	}

	private Object saveXMLAction() {
		// TODO Auto-generated method stub
		return null;
	}

	// Only used to set the mouse pointer. Find a better solution
	@Deprecated
	public Canvas getCanvas() {
		return canvas;
	}

	public void deselectPalette() {
		edgeCreationType = null;
		nodeCreationType = null;
		//newFmmlxPalette.clearSelection();
	}
	
	public FmmlxPalette getPalette() {
		return newFmmlxPalette;
	}


	public void setEdgeCreationType(String edgeCreationType) {
		this.edgeCreationType = edgeCreationType;
		this.nodeCreationType= null;
		getCanvas().setCursor(Cursor.CROSSHAIR);
	}

	public void setNodeCreationType(String nodeCreationType) {
		this.nodeCreationType = nodeCreationType;
		this.edgeCreationType = null;
		getCanvas().setCursor(Cursor.CROSSHAIR);
	}
	
	public String getEdgeCreationType() {
		return edgeCreationType;
	}
	
	public String getNodeCreationType() {
		return nodeCreationType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	// This operation resets the size of the canvas when needed
	private void resizeCanvas() {
		try {
			double maxRight = canvasRawSize.getX();
			double maxBottom = canvasRawSize.getY();

			for (FmmlxObject object : objects) {
				maxRight = Math.max(maxRight, object.getRightX());
				maxBottom = Math.max(maxBottom, object.getBottomY());
			}
			for (Edge<?> edge : edges) {
				maxRight = Math.max(maxRight, edge.getMaxX());
				maxBottom = Math.max(maxBottom, edge.getMaxY());
			}
			canvasRawSize = new Point2D(maxRight, maxBottom);
			Point2D canvasScreenSize = transformFX.transform(canvasRawSize);
			canvas.setWidth(Math.min(4096, canvasScreenSize.getX() + 5));
			canvas.setHeight(Math.min(4096, canvasScreenSize.getY() + 5));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void savePNG(){
		double zoom = this.zoom;
//		System.err.println("current zoom: " + zoom);
		setMaxZoom();
//		System.err.println("max zoom: " + getZoom());

	    FileChooser fileChooser = new FileChooser();

	    String initalDirectory = PropertyManager.getProperty("fileDialogPath", "");
    	if (!initalDirectory.equals("")) {
    		File dir = new File(initalDirectory);
    		if(dir.exists()) {
    			fileChooser.setInitialDirectory(dir);
    		}
    	}

    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
    	fileChooser.setTitle("Save Diagram to PNG");
	    File file = fileChooser.showSaveDialog(XModeler.getStage());

	    if(file != null){
	        WritableImage wi = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
	        try {
	        	ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(null,wi),null),"png",file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    setZoom(zoom);
	}
	// Only used to set the diagram into the tab. Find a better solution
	@Deprecated
	public VBox getView() {
		return vBox;
	}

	private void updateDiagramLater() {
		diagramRequiresUpdate = true;
	}

	public void redraw() {
		if (fetchingData) {
			return;}

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
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (objects.size() <= 0) {return;}
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
		if (mouseMode == MouseMode.DRAW_EDGE && lastPoint != null && currentPoint != null) {
			g.strokeLine(lastPoint.getX(), lastPoint.getY(), currentPoint.getX(), currentPoint.getY());
		}
	}

	////////////////////////////////////////////////////////////////////
	////						MouseListener						////
	////////////////////////////////////////////////////////////////////

	private void mousePressed(MouseEvent e) {
		if(fetchingData) return;
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
				((Edge<?>) selectedObjects.firstElement()).setPointAtToBeMoved(p);

			}
			mouseDraggedStandard(p);
		}
	}

	private transient CanvasElement lastElementUnderMouse = null;

	private void mouseMoved(MouseEvent e) {
		Point2D p = scale(e);
		if (mouseMode == MouseMode.DRAW_EDGE) {
			storeCurrentPoint(p.getX(), p.getY());
			redraw();
		}

		CanvasElement elementUnderMouse = getElementAt(p.getX(), p.getY());
		if(elementUnderMouse != lastElementUnderMouse) {
			lastElementUnderMouse = elementUnderMouse;
			for (FmmlxObject o : objects)
				o.unHighlight();
			for (Edge<?> edge : edges)
				edge.unHighlight();
			for (DiagramEdgeLabel<?> l : labels)
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
				for(Edge<?> e : edges) {
					if(e.isSourceNode(o) || e.isTargetNode(o)) e.align();
				}
			} else if (s instanceof DiagramEdgeLabel) {
				DiagramEdgeLabel<?> o = (DiagramEdgeLabel<?>) s;
//				if(o.getReferenceX()+o.getRelativeX()<0.0 && o.getReferenceY()+o.getRelativeY()<0.0){
//					s.moveTo(0.5, 0.5, this);
//				} else if (o.getReferenceX()+o.getRelativeX()<0.0){
//					s.moveTo(0.5, p.getY() - o.getMouseMoveOffsetY(), this);
//				} else if (o.getReferenceY()+o.getRelativeY()<0.0){
//					s.moveTo(p.getX() - o.getMouseMoveOffsetX(), 0.5, this);
//				} else {
//					s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
//				}

				s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
			} else { // must be edge
				s.moveTo(p.getX(), p.getY(), this);
			}
		objectsMoved = true;

		for(Edge<?> e : edges) {e.align();}

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

		for(Edge<?> edge : edges) {
			edge.dropPoint(this);
		}

		triggerOverallReLayout();

		resizeCanvas();
		if(diagramRequiresUpdate) {
			diagramRequiresUpdate = false;
			updateDiagram();
		}
		redraw();
	}

	public void triggerOverallReLayout() {
		for(int i = 0; i < 3; i++) {
			for(FmmlxObject o : objects) {
				o.layout(this);
			}
			for(Edge<?> edge : edges) {
				edge.align();
				edge.layoutLabels(this);
			}
		}
	}

	private void mouseReleasedStandard() {
		for (Edge<?> e : edges) e.removeRedundantPoints();

		if (objectsMoved) {
			for (CanvasElement s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					comm.sendCurrentPosition(this.getID(), o.getPath(), (int)Math.round(o.getX()), (int)Math.round(o.getY()), o.hidden);
					for(Edge<?> e : edges) {
						if(e.isSourceNode(o) || e.isTargetNode(o)) {
							comm.sendCurrentPositions(this.getID(), e);
						}
					}
				} else if (s instanceof Edge) {
					comm.sendCurrentPositions(this.getID(), (Edge<?>) s);
				} else if (s instanceof DiagramEdgeLabel) {
					DiagramEdgeLabel<?> del = (DiagramEdgeLabel<?>) s;
					del.owner.updatePosition(del);
					comm.storeLabelInfo(this, del);
				}
		}
		objectsMoved = false;

	}

	private boolean isLeftButton(MouseEvent e) {return e.getButton() == MouseButton.PRIMARY;}

	private boolean isRightButton(MouseEvent e) {return e.getButton() == MouseButton.SECONDARY;}

//	private boolean isMiddleClick(MouseEvent e) {return e.getButton() == MouseButton.MIDDLE;}

	private CanvasElement getElementAt(double x, double y) {
		for (FmmlxObject o : new Vector<>(objects))
			if (o.isHit(x, y))
				return o;
		for (Edge<?> e : new Vector<>(edges))
			if (e.isHit(x, y))
				return e;
		for (DiagramEdgeLabel<?> l : new Vector<>(labels))
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
                        setStandardMouseMode();
						break;
					case AssociationInstance:
					{
						final FmmlxObject obj1 = newEdgeSource;
						final FmmlxObject obj2 = newEdgeTarget;
						Platform.runLater(() -> {
							actions.addAssociationInstance(obj1, obj2,null);
							updateDiagramLater();
							setStandardMouseMode();
						});
						break;
					}
					case Delegation:
					{
						final FmmlxObject delegateFrom = newEdgeSource;
						final FmmlxObject delegateTo = newEdgeTarget;
						Platform.runLater(() -> {
							actions.setDelegation(delegateFrom, delegateTo);
//							updateDiagramLater();
						});
						break;
					}
					case RoleFiller:
					{
						final FmmlxObject delegateFrom = newEdgeSource;
						final FmmlxObject delegateTo = newEdgeTarget;
						Platform.runLater(() -> {
							actions.setRoleFiller(delegateFrom, delegateTo);
//							updateDiagramLater();
						});
						break;
					}
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
						actions.addAssociationInstance(newEdgeSource, null,null);
						break;
					case Delegation:
					case RoleFiller:
						mouseMode = MouseMode.STANDARD;
						// no dialog if clicked into the void: actions.addDelegation(newEdgeSource, null);
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
			if (edgeCreationType.equals("association")) {
				//hitObject = getElementAt(p.getX(), p.getY());
				//System.out.println("asso");
				if(hitObject instanceof FmmlxObject) {		
					setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.Association);
					canvas.setCursor(Cursor.DEFAULT);

				}
			} else if (edgeCreationType.equals("associationInstance")) {
				//System.out.println("instance");
				if(hitObject instanceof FmmlxObject) {
					setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.AssociationInstance);
					canvas.setCursor(Cursor.DEFAULT);
				}
			} else if (edgeCreationType.equals("delegation")) {
				if(hitObject instanceof FmmlxObject) {
					setDrawEdgeMode((FmmlxObject)hitObject, PropertyType.Delegation);
					canvas.setCursor(Cursor.DEFAULT);
				}
			}
		} else {
			if (nodeCreationType.equals("MetaClass")) {
				actions.addMetaClassDialog(e);
			} else {
				actions.addInstanceDialog(getObjectByPath((nodeCreationType)),e);
			}
			canvas.setCursor(Cursor.DEFAULT);
			deselectAll();
		}
	}

	private void highlightElementAt(CanvasElement hitObject, Point2D p) {
		for (CanvasElement object : objects) {
			object.highlightElementAt(null);
		}
		for (Edge<?> edge : edges) {
			edge.highlightElementAt(null);
		}
		hitObject.highlightElementAt(p);
	}

	private void handlePressedOnNodeElement(Point2D p, CanvasElement hitObject) {
		if (hitObject instanceof FmmlxObject) {
			FmmlxObject obj = (FmmlxObject) hitObject;
			Point2D relativePoint = new Point2D(p.getX() - obj.getX(), p.getY() - obj.getY());
			lastHitProperty = obj.handlePressedOnNodeElement(relativePoint, this);
		}
	}

	private void handleDoubleClickOnNodeElement(Point2D p, CanvasElement hitObject) {
		if (hitObject instanceof FmmlxObject) {
			FmmlxObject obj = (FmmlxObject) hitObject;
			Point2D relativePoint = new Point2D(p.getX() - obj.getX(), p.getY() - obj.getY());
			obj.performDoubleClickAction(relativePoint);
		} else if (hitObject instanceof DiagramEdgeLabel) {
			DiagramEdgeLabel<?> l = (DiagramEdgeLabel<?>) hitObject;
			l.performAction();
		}
	}

	private void handleRightClick(MouseEvent e) {
		Point2D p = scale(e);
		CanvasElement hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
			if (hitObject instanceof FmmlxObject) {
				activeContextMenu = hitObject.getContextMenu(this, p);
			} else if (hitObject instanceof Edge) {
				activeContextMenu = hitObject.getContextMenu(this, p);
			}
			if (!selectedObjects.contains(hitObject)) {
				deselectAll();
				selectedObjects.add(hitObject);
			}
		} else {
			activeContextMenu = new DefaultContextMenu(this);
		}
		showContextMenu(e);
	}

	private void handleScroll(ScrollEvent e) {
		if (e.isControlDown()) {
			double delta = e.getDeltaY();
			if (delta > 0) {
				zoomIn();
			} else {
				zoomOut();
			}
		}
	}

	private final double ZOOM_STEP = Math.sqrt(2);

	public void zoomIn() {
		setZoom(Math.min(2, getZoom() * ZOOM_STEP));
		redraw();
	}

	public void zoomOut() {
		setZoom(getZoom() / ZOOM_STEP);
		redraw();
	}

	public void zoomOne() {
		setZoom(1.);
		redraw();
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

	public void setDrawEdgeMode(FmmlxObject source, PropertyType type) {
		setSelectedObject(source);
		setDrawEdgeMouseMode(type, source);
		storeLastClick(source.getCenterX(), source.getCenterY());
		deselectAll();
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

	@Override public void setSelectedObjectAndProperty(FmmlxObject o, FmmlxProperty p) { setSelectedObject(o); }

	private void select(FmmlxObject o) {
		if (!selectedObjects.contains(o)) {
			selectedObjects.add(o);
		}
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

	public void setMaxZoom(){
		double maxRight = 1;
		double maxBottom = 1;

		for (FmmlxObject object : objects) {
			maxRight = Math.max(maxRight, object.getRightX());
			maxBottom = Math.max(maxBottom, object.getBottomY());
		}
		for (Edge<?> edge : edges) {
			maxRight = Math.max(maxRight, edge.getMaxX());
			maxBottom = Math.max(maxBottom, edge.getMaxY());
		}

		double maxXzoom = 4096. / maxRight;
		double maxYzoom = 4096. / maxBottom;

		setZoom(Math.min(10,Math.min(maxXzoom, maxYzoom)));
	}

	public void setZoom(double zoom) {
		this.zoom = Math.min(10, Math.max(zoom, 1. / 8));

		transformFX = new Affine();
		transformFX.appendScale(zoom, zoom);
		resizeCanvas();
	}

	@Deprecated
	//needs filter
	/**
	 * Calculates the height of the text. Because that depends of the font size and the screen resolution
	 * @return the text height
	 */
	public static double calculateTextHeight() {
		Text t = new Text("TestText");
		t.setFont(FONT);
		return t.getLayoutBounds().getHeight();
	}

	public static double calculateTextWidth(String text) {
		Text t = new Text(text);
		t.setFont(FONT);
		return t.getLayoutBounds().getWidth();
	}

	// TODO: delete and use method with level
	public ObservableList<FmmlxObject> getPossibleAssociationEnds() {
		ArrayList<FmmlxObject> objectList = new ArrayList<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					objectList.add(object);
				}
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	public void addLabel(DiagramEdgeLabel<?> diagramLabel) {
		Integer index = null;
		for(int i = 0; i < labels.size(); i++) {
			DiagramEdgeLabel<?> label = labels.get(i);
			if(label.owner == diagramLabel.owner && label.localID == diagramLabel.localID) {
				index = i;
			}
		}
		if(index == null) {
			labels.add(diagramLabel);
		} else {
			labels.set(index, diagramLabel);
		}


	}


	////////////////////////////////////////////////////////////////////
	////					Messages to XMF							////
	////////////////////////////////////////////////////////////////////


	// Some useful methods for queries:

	public String getDiagramLabel() {
		return diagramName;
	}

	public InheritanceEdge getInheritanceEdge(FmmlxObject child, FmmlxObject parent) {
		for(Edge<?> e : edges) {
			if(e instanceof InheritanceEdge) {
				InheritanceEdge i = (InheritanceEdge) e;
				if(i.isSourceNode(child) && i.isTargetNode(parent)) return i;
			}
		}
		return null;
	}

	public Vector<DiagramEdgeLabel<?>> getLabels() {
		return new Vector<>(labels); // read-only
	}

	public Vector<FmmlxObject> getObjectsByLevel(int level){
		Vector<FmmlxObject> result = new Vector<>();

		for (FmmlxObject object : objects) {
			if (object.getLevel()==level) {
				result.add(object);
			}
		}
		return result;
	}

//	public FmmlxAssociation getAssociationById(int id) {
//		for (Edge tmp : edges) {
//			if (tmp.getId() == id)
//				return (FmmlxAssociation) tmp;
//		}
//		return null;
//	}

	public Object getAllMetaClass() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : getObjects()) {
			if (object.getLevel() != 0) {
				result.add(object);
			}
		}
		return result;
	}

	public void setCursor(Cursor c) {
		canvas.setCursor(c);
	}

	public void setShowOperations(boolean show) {this.showOperations = show;}
	public void setShowOperationValues(boolean show) {this.showOperationValues = show;}
	public void setShowSlots(boolean show) {this.showSlots = show;}
	public void setShowGettersAndSetters(boolean show) {this.showGetterAndSetter = show;}
	public void setShowDerivedOperations(boolean show) {this.showDerivedOperations = show;}
	public void setShowDerivedAttributes(boolean show) {this.showDerivedAttributes = show;}

	public boolean isShowOperations() {return this.showOperations;}
	public boolean isShowOperationValues() {return this.showOperationValues;}
	public boolean isShowSlots() {return this.showSlots;}
	public boolean isShowGetterAndSetter() {return this.showGetterAndSetter;}
	public boolean isShowDerivedOperations() {return this.showDerivedOperations;}
	public boolean isShowDerivedAttributes() {return this.showDerivedAttributes;}

	public Vector<String> getEnumItems(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e.getItems();
		}
		return null;
	}

	public ObservableList<FmmlxEnum> getEnumsObservableList() {
		ArrayList<FmmlxEnum> objectList = new ArrayList<>();

		if (!enums.isEmpty()) {
			for (FmmlxEnum fmmlxEnum : enums) {
				objectList.add(fmmlxEnum);
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	public synchronized void updateEnums() {
		try {
			enums.clear();
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
		Vector<Point2D> result = new Vector<>();
		for(Edge<?> e : new Vector<>(edges)) {
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



	public void setShowOperations(CheckBox box) {
		boolean show = box.isSelected();
		setShowOperations(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowOperations(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowGettersAndSetters(CheckBox box) {
		boolean show = box.isSelected();
		setShowGettersAndSetters(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowGettersAndSetters(show);
		}
		triggerOverallReLayout();
		redraw();		
	}


	public void setShowOperationValues(CheckBox box) {
		boolean show = box.isSelected();
		setShowOperationValues(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowOperationValues(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowSlots(CheckBox box) {
		boolean show = box.isSelected();
		setShowSlots(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowSlots(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowDerivedOperations(CheckBox box) {
		boolean show = box.isSelected();
		setShowDerivedOperations(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowDerivedOperations(show);
		}
		triggerOverallReLayout();
		redraw();

	}

	public void setShowDerivedAttributes(CheckBox box) {
		boolean show=box.isSelected();
		setShowDerivedAttributes(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowDerivedAttributes(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	@Override
	protected void clearDiagram_specific() {
		labels.clear();
	}

	@Override
	protected void fetchDiagramDataSpecific() throws TimeOutException {
//		levelColorScheme = new LevelColorScheme.RedLevelColorScheme(objects);
		for(FmmlxObject o : objects) {
			o.layout(this);
		}

		triggerOverallReLayout();
		resizeCanvas();
	}

	@Override
	protected void fetchDiagramDataSpecific2() {
		newFmmlxPalette.update();

		if(filePath !=null && filePath.length()>0){
			if(justLoaded){
				justLoaded = false;
				FmmlxDeserializer deserializer = new FmmlxDeserializer(new XmlManager(filePath));
				org.w3c.dom.Node positionInfo = getComm().getPositionInfo(getID());
				if(positionInfo != null) {
					deserializer.alignElements(this, (org.w3c.dom.Element) positionInfo);
					triggerOverallReLayout();
				}
				redraw();
				updateDiagram();
			}
		} else {		
			Issue nextIssue = null;
			for(int i = 0; i < issues.size() && nextIssue == null; i++) {
				if(issues.get(i).isSoluble()) nextIssue = issues.get(i);
			}
	
			if(nextIssue != null) {
				nextIssue.performResolveAction(this);
			}
		}
	}

	@Override
	protected void updateViewerStatusInGUI(ViewerStatus newStatus) {
		// TODO Auto-generated method stub

	}

	public void paintToSvg(XmlHandler xmlHandler){
		Vector<CanvasElement> objectsToBePainted = new Vector<>();
		objectsToBePainted.addAll(objects);
		objectsToBePainted.addAll(labels);
		objectsToBePainted.addAll(edges);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objects) {
			o.updatePortOder();
		}
		for(CanvasElement c : objectsToBePainted){
			c.paintToSvg(xmlHandler, 0, 0, this);
		}

	}

}
