package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
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
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.results.*;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class FmmlxDiagram {

	enum MouseMode {
		MULTISELECT, STANDARD, DRAW_EDGE
	}
	
	// The elements which the diagram consists of GUI-wise
	private SplitPane mainView;
	private Canvas canvas;
	private ScrollPane scrollerCanvas;
	
	// The communication to the xmf and other actions
	private final FmmlxDiagramCommunicator comm;
	private DiagramActions actions;
	
	// The elements representing the model which is displayed in the GUI
	private Vector<FmmlxObject> objects = new Vector<>();
	private Vector<Edge> edges = new Vector<>();
	private Vector<DiagramLabel> labels = new Vector<>();
	
	// Temporary variables storing the current state of user interactions
	private transient Vector<CanvasElement> selectedObjects = new Vector<>();
	private ContextMenu activeContextMenu;
	private transient boolean objectsMoved = false;
	private transient PropertyType drawEdgeType = null;
	private Point2D lastPoint;
	private Point2D currentPoint;
	private MouseMode mode = MouseMode.STANDARD;
	private FmmlxObject newEdgeTarget;
	private NodeLabel lastHitLabel = null;
	private boolean diagramRequiresUpdate = false;
	
	// The state of the canvas is stored here:
	private Point2D canvasRawSize = new Point2D(1200, 800);
	private double zoom = 1.;
	private Affine transformFX = new Affine();
	private Font font;

	FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		actions = new DiagramActions(this);
		Palette palette = new Palette(actions);
		scrollerCanvas = new ScrollPane(canvas);
		mainView.setOrientation(Orientation.VERTICAL);
		mainView.getItems().addAll(palette, scrollerCanvas);

		canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseDragged(this::mouseDragged);
		canvas.setOnMouseReleased(this::mouseReleased);
		canvas.setOnMouseMoved(this::mouseMoved);
		canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);

		new Thread(this::fetchDiagramData).start();

		try {
			font = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono.ttf"), 14);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		redraw();
	}

	// Only used to set the mouse pointer. Find a better solution
	@Deprecated
	public Canvas getCanvas() {
		return canvas;
	}

	private void fetchDiagramData() {
		objects.clear();
		edges.clear();
		labels.clear();
		
		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
		objects.addAll(fetchedObjects);
		
		Vector<Edge> fetchedEdges = comm.getAllAssociations();
		fetchedEdges.addAll(comm.getAllAssociationsInstances());

		edges.addAll(fetchedEdges);
		
		for (FmmlxObject o : objects) {
			o.fetchDataDefinitions(comm);
		}
		
		for (FmmlxObject o : objects) {
			o.fetchDataValues(comm);
			o.layout(this);
		}
		
		resizeCanvas();
		redraw();
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
			canvas.setWidth(canvasScreenSize.getX() + 5);
			canvas.setHeight(canvasScreenSize.getY() + 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Only used to set the diagram into the tab. Find a better solution
	@Deprecated
	public SplitPane getView() {
		return mainView;
	}
	
	public FmmlxDiagramCommunicator getComm() {
		return comm;
	}

    private void updateDiagramLater() {
		diagramRequiresUpdate = true;
	}
	
	public void redraw() {
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
		for (CanvasElement o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}

		drawMultiSelectRect(g);
		drawNewEdgeLine(g);
	}

	private void drawMultiSelectRect(GraphicsContext g) {
		if (mode == MouseMode.MULTISELECT) {
			double x = Math.min(lastPoint.getX(), currentPoint.getX());
			double y = Math.min(lastPoint.getY(), currentPoint.getY());

			g.strokeRect(x, y, Math.abs(currentPoint.getX() - lastPoint.getX()), Math.abs(currentPoint.getY() - lastPoint.getY()));
		}
	}

	private void drawNewEdgeLine(GraphicsContext g) {
		if (mode == MouseMode.DRAW_EDGE) {
			g.strokeLine(lastPoint.getX(), lastPoint.getY(), currentPoint.getX(), currentPoint.getY());
		}
	}

	////////////////////////////////////////////////////////////////////
	////						MouseListener						////
	////////////////////////////////////////////////////////////////////
	private void mousePressed(MouseEvent e) {
		Point2D p = scale(e);
		clearContextMenus();

		if (isMiddleClick(e)) {
			selectedObjects.addAll(objects);
		}
		if (isLeftClick(e)) {
			handleLeftPressed(e);
		}
		if (isRightClick(e)) {
			handleRightClick(e);
		}
		setMouseOffset(p);
		redraw();
	}

	private void mouseDragged(MouseEvent e) {
		Point2D p = scale(e);

		if (mode == MouseMode.MULTISELECT) {
			storeCurrentPoint(p.getX(), p.getY());
			redraw();
		}
		if (mode == MouseMode.STANDARD) {
			if (selectedObjects.size() == 1 && selectedObjects.firstElement() instanceof Edge) {
				((Edge) selectedObjects.firstElement()).setPointAtToBeMoved(p);

			}
			mouseDraggedStandard(p);
		}
	}

	private void mouseMoved(MouseEvent e) {
		Point2D p = scale(e);

		if (mode == MouseMode.DRAW_EDGE) {
			storeCurrentPoint(p.getX(), p.getY());
			redraw();
		}
	}

	private void mouseDraggedStandard(Point2D p) {
//		if (hitObject != null) {
		for (CanvasElement s : selectedObjects)
			if (s instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) s;
				s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
			} else if (s instanceof DiagramLabel) {
				DiagramLabel o = (DiagramLabel) s;
				s.moveTo(p.getX() - o.getMouseMoveOffsetX(), p.getY() - o.getMouseMoveOffsetY(), this);
			} else { // must be edge
				s.moveTo(p.getX(), p.getY(), this);
			}
		objectsMoved = true;
		redraw();
//		} else {
//			mode = MouseMode.MULTISELECT;
//			storeLastClick(p.getX(), p.getY());
//		}
	}

	private void mouseReleased(MouseEvent e) {
		if (isMiddleClick(e)) {
			selectedObjects.clear();
		} else {
			if (mode == MouseMode.MULTISELECT) {
				handleMultiSelect();
			}
			if (mode == MouseMode.STANDARD) {
				mouseReleasedStandard();
			}

			mode = MouseMode.STANDARD;
			for (Edge edge : edges) {
				edge.dropPoint();
			}
		}
		resizeCanvas();
		if(diagramRequiresUpdate) {
			diagramRequiresUpdate = false;
			updateDiagram();
		}
		redraw();
	}

	private void mouseReleasedStandard() {
		if (objectsMoved) {
			for (CanvasElement s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					comm.sendCurrentPosition(o);
					for(Edge e : edges) {
						if(e.isStartNode(o) || e.isEndNode(o)) {
							comm.sendCurrentPositions(e);
						}
					}
				} else if (s instanceof Edge) {
//					FmmlxAssociation a = (FmmlxAssociation) s;
					comm.sendCurrentPositions((Edge) s);
				} else if (s instanceof DiagramLabel) {
					comm.storeLabelInfo((DiagramLabel) s);
				}
		}
		objectsMoved = false;

	}

	private boolean isLeftClick(MouseEvent e) {return e.getButton() == MouseButton.PRIMARY;}
	private boolean isRightClick(MouseEvent e) {return e.getButton() == MouseButton.SECONDARY;}
	private boolean isMiddleClick(MouseEvent e) {return e.getButton() == MouseButton.MIDDLE;}

	private CanvasElement getElementAt(double x, double y) {
		for (FmmlxObject o : objects)
			if (o.isHit(x, y))
				return o;
		for (Edge e : edges)
			if (e.isHit(x, y))
				return e;
		for (DiagramLabel l : labels)
			if (l.isHit(x, y))
				return l;
		
		return null;
	}

	private void handleLeftPressed(MouseEvent e) {
		Point2D p = scale(e);

		CanvasElement hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
			if (mode == MouseMode.DRAW_EDGE && newEdgeTarget == null) {
				newEdgeTarget = hitObject instanceof FmmlxObject ? (FmmlxObject) hitObject : null;
				switch (drawEdgeType) {
					case Association:
						actions.addAssociationDialog((FmmlxObject) selectedObjects.get(0), newEdgeTarget);
						break;
					case AssociationInstance:
						final FmmlxObject obj1 = (FmmlxObject) selectedObjects.get(0);
						final FmmlxObject obj2 = newEdgeTarget;
						Platform.runLater(() -> {
							actions.addAssociationInstance(obj1, obj2);
							updateDiagramLater();
						});						
						break;
					default:
						break;
				}
				newEdgeTarget = null;
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
			handleClickOnNodeElement(p, hitObject);

			if (e.getClickCount() == 2) {
				handleDoubleClickOnNodeElement(p, hitObject);
			}
		} else {
			if (mode == MouseMode.DRAW_EDGE) {
				switch (drawEdgeType) {
					case Association:
						actions.addAssociationDialog((FmmlxObject) selectedObjects.get(0), null);
						break;
					case AssociationInstance:
						actions.addAssociationInstance((FmmlxObject) selectedObjects.get(0), null);
						break;
					default:
						break;
				}
			} else {
				mode = MouseMode.MULTISELECT;
				storeLastClick(p.getX(), p.getY());
				storeCurrentPoint(p.getX(), p.getY());
			}
			deselectAll();
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

	private void handleClickOnNodeElement(Point2D p, CanvasElement hitObject) {
		if (hitObject instanceof FmmlxObject) {
			Point2D relativePoint = getRelativePointToNodeBox(hitObject, p);
			NodeBox hitNodeBox = getHitNodeBox((FmmlxObject) hitObject, relativePoint);
			if (hitNodeBox != null) {
				NodeLabel hitLabel = getHitLabel(hitNodeBox, relativePoint);
				if (hitLabel != null) {
					if (lastHitLabel != null) {
						lastHitLabel.setDeselected();
					}
					if (hitLabel.getActionObject().getPropertyType() != PropertyType.Class) {
						lastHitLabel = hitLabel;
						lastHitLabel.setSelected();
					}
				}
			}
		}
	}

	private void handleDoubleClickOnNodeElement(Point2D p, CanvasElement hitObject) {
		NodeBox hitNodeBox = null;
		if (hitObject instanceof FmmlxObject) {
			Point2D relativePoint = new Point2D(
					p.getX() - ((FmmlxObject) hitObject).getX(),
					p.getY() - ((FmmlxObject) hitObject).getY());

			// Checking NodeBoxes
			hitNodeBox = getHitNodeBox((FmmlxObject) hitObject, relativePoint);
			if (hitNodeBox != null) {
				FmmlxProperty hitProperty = getHitProperty(hitNodeBox, relativePoint);
				if (hitNodeBox.getElementType() == PropertyType.Slot && hitProperty != null) {
					actions.changeSlotValue((FmmlxObject) hitObject, (FmmlxSlot) hitProperty);
				} else {
					if (hitNodeBox.getElementType() != PropertyType.Slot)
						actions.changeNameDialog((FmmlxObject) hitObject, hitNodeBox.getElementType(), hitProperty);
				}
			}
		} else if (hitObject instanceof DiagramLabel) {
			DiagramLabel l = (DiagramLabel) hitObject;
			l.performAction();
		}
	}

	private NodeBox getHitNodeBox(FmmlxObject hitObject, Point2D relativePoint) {
		for (NodeElement element : hitObject.getNodes()) {
			if (element.isHit(relativePoint.getX(), relativePoint.getY()) && element instanceof NodeBox) {
				if (((NodeBox) element).getElementType() != PropertyType.Selection && ((NodeBox) element).getElementType() != PropertyType.OperationValue) {
					return (NodeBox) element;
				}
			}
		}
		return null;
	}

	private FmmlxProperty getHitProperty(NodeBox nodeBox, Point2D p) {
		for (NodeElement nodeLabel : nodeBox.nodeElements) {
			if (nodeLabel.isHit(p.getX(), p.getY() - nodeBox.y) && nodeLabel instanceof NodeLabel) {
				return ((NodeLabel) nodeLabel).getActionObject();
			}
		}
		return null;
	}

	private NodeLabel getHitLabel(NodeBox nodeBox, Point2D p) {
		for (NodeElement nodeLabel : nodeBox.nodeElements) {
			if (nodeLabel.isHit(p.getX(), p.getY() - nodeBox.y) && nodeLabel instanceof NodeLabel) {
				return ((NodeLabel) nodeLabel);
			}
		}
		return null;
	}

	private Point2D getRelativePointToNodeBox(CanvasElement hitObject, Point2D p) {
		return new Point2D(
				p.getX() - ((FmmlxObject) hitObject).getX(),
				p.getY() - ((FmmlxObject) hitObject).getY());
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

	public void setDrawEdgeMouseMode(PropertyType type) {
		drawEdgeType = type;
		mode = MouseMode.DRAW_EDGE;
	}

	public void setStandardMouseMode() {
		mode = MouseMode.STANDARD;
	}

	////////////////////////////////////////////////////////////////////
	private void clearContextMenus() {
		if (activeContextMenu != null && activeContextMenu.isShowing()) {
			activeContextMenu.hide();
		}
	}

	private void showContextMenu(MouseEvent p) {
		if (activeContextMenu != null) {
			activeContextMenu.show(scrollerCanvas, Side.LEFT, p.getX(), p.getY());
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
		selectedObjects.clear();
		if (lastHitLabel != null) {
			lastHitLabel.setDeselected();
			lastHitLabel = null;
		}
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

		mode = MouseMode.STANDARD;
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
		if (lastHitLabel != null) {
			return lastHitLabel.getActionObject();
		}
		return null;
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

	
	public Vector<FmmlxAssociationInstance> getAssociationInstance(){
		Vector<FmmlxAssociationInstance> result = new Vector<FmmlxAssociationInstance>();
		for (Edge tmp : edges) {
			if (tmp instanceof FmmlxAssociationInstance) {
				result.add((FmmlxAssociationInstance) tmp);
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

	public double calculateTextHeight() {
		Text t = new Text("TestText");
		t.setFont(font);
		return t.getLayoutBounds().getHeight();
	}

	public double calculateTextWidth(String text) {
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
	
	public void addLabel(DiagramLabel diagramLabel) {
		labels.add(diagramLabel);
	}

	////////////////////////////////////////////////////////////////////
	////					Messages to XMF							////
	////////////////////////////////////////////////////////////////////
	
	// to be migrated to Communicator
	@Deprecated
	public void changeClassName(ChangeNameDialogResult res) {
		comm.changeClassName(res.getObjectId(), res.getNewName());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeOperationName(ChangeNameDialogResult res) {
		comm.changeOperationName(res.getObjectId(), res.getOldName(), res.getNewName());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeAttributeName(ChangeNameDialogResult res) {
		comm.changeAttributeName(res.getObjectId(), res.getOldName(), res.getNewName());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeAssociationName(ChangeNameDialogResult result) {
		comm.changeAssociationName(result.getObjectId(), result.getOldName(), result.getNewName());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeClassLevel(ChangeLevelDialogResult result) {
		comm.changeClassLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeAttributeLevel(ChangeLevelDialogResult result) {
		comm.changeAttributeLevel(result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeAssociationLevel(ChangeLevelDialogResult result) {
		comm.changeAssociationLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeOperationLevel(ChangeLevelDialogResult result) {
		comm.changeOperationLevel(result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeSlotValue(ChangeSlotValueDialogResult result) {
		comm.changeSlotValue(result.getObject().getId(), result.getSlot().getName(), result.getNewValue());
	}
	
	// to be migrated to Communicator
	@Deprecated
	public void changeOf(ChangeOfDialogResult result) {
		comm.changeOf(result.getObjectId(), result.getOldOfId(), result.getNewOfId());
	}

	// to be migrated to Communicator
	@Deprecated
	public void removeClass(RemoveDialogResult result) {
		comm.removeClass(result.getObject().getId(), 0);
	}

	// to be migrated to Communicator
	@Deprecated
	public void removeOperation(RemoveDialogResult result) {
		comm.removeOperation(result.getObject().getId(), result.getOperation().getName(), 0);

	}

	// to be migrated to Communicator
	@Deprecated
	public void removeAttribute(RemoveDialogResult result) {
		comm.removeAttribute(result.getObject().getId(), result.getAttribute().getName(), 0);
	}

	// to be migrated to Communicator
	@Deprecated
	public void removeAssociation(RemoveDialogResult result) {
		comm.removeAssociation(result.getAssociation().getId(), 0);
	}

	// to be migrated to Communicator
	@Deprecated
	public void addOperation(AddDialogResult result) {
//		comm.addOperation(result.getObjectId(), result.getOperationName(), result.getLevel(), result.getOperationType(), result.getBody());
		comm.addOperation2(result.getObjectId(), result.getLevel(), result.getBody());
	}

	// to be migrated to Communicator
	@Deprecated
	public void addAssociation(AddAssociationDialogResult result) {
		comm.addAssociation(
				result.getSource().id, result.getTarget().id,
				result.getIdentifierSource(), result.getIdentifierTarget(),
				result.getDisplayNameSource(), result.getDisplayNameTarget(),
				result.getMultiplicitySource(), result.getMultiplicityTarget(),
				result.getInstLevelSource(), result.getInstLevelTarget()
		);
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeAttributeOwner(ChangeOwnerDialogResult result) {
		comm.changeAttributeOwner(result.getObject().getId(), result.getNewOwnerID());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeOperationOwner(ChangeOwnerDialogResult result) {
		comm.changeOperationOwner(result.getObject().getId(), result.getNewOwnerID());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeParent(ChangeParentDialogResult result) {
		comm.changeParent(result.getObject().getId(), result.getCurrentParentIds(), result.getNewParentIds());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeTypeAttribute(ChangeTypeDialogResult result) {
		comm.changeAttributeType(result.getObject().getId(), result.getAttribute().getName(),
				result.getOldType(), result.getNewType());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeTypeOperation(ChangeTypeDialogResult result) {
		comm.changeOperationType(result.getObject().getId(), result.getOperation().getName(),
				result.getOldType(), result.getNewType());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeTypeAssociation(ChangeTypeDialogResult result) {
		comm.changeAssociationType(result.getObject().getId(), result.getAssociation().getName(),
				result.getOldType(), result.getNewType());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeTargetAssociation(ChangeTargetDialogResult result) {
		comm.changeTargetAssociation(result.getObject().getId(), result.getAssociationName(), result.getOldTargetID(), result.getNewTargetID());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeMulitiplicityAttribute(MultiplicityDialogResult result) {
		comm.changeMultiplicityAttribute(result.getObject().getId(), result.getSelectedAttribute().getName(), result.convertToMultiplicity());
	}

	// to be migrated to Communicator
	@Deprecated
	public void changeBody(ChangeBodyDialogResult result) {
		comm.changeOperationBody(result.getObject().getId(), result.getSelectedItem().getName(), result.getBody());
	}
	
	// to be migrated to Communicator
	@Deprecated
	public void editAssociation(EditAssociationDialogResult result) {

		comm.editAssociation(result.getSelectedAssociation().getId(),
				result.getSource(), result.getTarget(),
				result.getNewInstLevelSource(), result.getNewInstLevelTarget(),
				result.getNewDisplayNameSource(), result.getNewDisplayNameTarget(),
				result.getNewIdentifierSource(), result.getNewIdentifierTarget(),
				result.getMultiplicitySource(), result.getMultiplicityTarget());

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

	// Some useful methods for queries:
	

	public Vector<FmmlxObject> getObjects() {
		return new Vector<FmmlxObject>(objects); // read-only
	}
	
	public Vector<Edge> getEdges() {
		return new Vector<Edge>(edges); // read-only
	}
	
	public Vector<DiagramLabel> getLabels() {
		return new Vector<DiagramLabel>(labels); // read-only
	}

	public FmmlxObject getObjectById(int id) {
		for (FmmlxObject object : objects) {
			if (object.getId() == id)
				return object;
		}
		return null;
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
				if (tmp.startNode.getId() == object.getId() || tmp.endNode.getId() == object.getId()) {
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
}
