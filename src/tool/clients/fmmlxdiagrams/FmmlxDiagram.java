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
		STANDARD, MULTISELECT;
	}

	private SplitPane mainView;
	private final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();
	private Vector<Edge> edges = new Vector<>();

	private transient Vector<Selectable> selectedObjects = new Vector<>();
	//	private DefaultContextMenu defaultContextMenu;
//	private ObjectContextMenu objectContextMenu;
	private ContextMenu activeContextMenu;
	private DiagramActions actions;
	private transient boolean objectsMoved = false;
	private Point2D lastPoint;
	private Point2D currentPoint;
	private MouseMode mode = MouseMode.STANDARD;
	private Font font;

//	public Vector<FmmlxObject> fetchObjects() {
//		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
//		objects.clear(); // to be replaced when updating instead of loading form scratch
//		objects.addAll(fetchedObjects);
//		for (FmmlxObject o : objects) {
//			o.fetchDataDefinitions(comm);
//		}
//		for (FmmlxObject o : objects) {
//			o.fetchDataValues(comm);
//		}
//		return objects;
//	}

	public Vector<FmmlxObject> getObjects() {
		return new Vector<FmmlxObject>(objects); // read-only
	}

	public FmmlxObject getObjectById(int id) {
		for (FmmlxObject object : objects) {
			if (object.getId() == id)
				return object;
		}
		return null;
	}

	private Point2D canvasRawSize = new Point2D(1200, 800);

	private double zoom = 1.;
	private Affine transformFX;
	private ScrollPane scrollerCanvas;

	FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		actions = new DiagramActions(this);
		Palette palette = new Palette(actions);
		scrollerCanvas = new ScrollPane(canvas);
		mainView.setOrientation(Orientation.VERTICAL);
		mainView.getItems().addAll(palette, scrollerCanvas);
		transformFX = new Affine();

//		defaultContextMenu = new DefaultContextMenu(actions);

		canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseDragged(this::mouseDragged);
		canvas.setOnMouseReleased(this::mouseReleased);
		canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);

		new Thread(this::fetchDiagramData).start();

		try {
			font = Font.loadFont(new FileInputStream("resources/fonts/DejaVuSansMono.ttf"), 14);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		redraw();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	private void fetchDiagramData() {
		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
		objects.clear(); // to be replaced when updating instead of loading form scratch
		objects.addAll(fetchedObjects);
		Vector<Edge> fetchedEdges = comm.getAllAssociations();
		edges.clear(); // to be replaced when updating instead of loading form scratch
		edges.addAll(fetchedEdges);
		for (FmmlxObject o : objects) {
			o.fetchDataDefinitions(comm);
		}
		for (FmmlxObject o : objects) {
			o.fetchDataValues(comm);
		}
//		if (objects.size() >= 2) {
//			Edge e = new Edge(-1, objects.get(0), objects.get(1), null, this);
//			edges.add(e);
//		}
		resizeCanvas();
		redraw();
	}

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

	public SplitPane getView() {
		return mainView;
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
		objectsToBePainted.addAll(edges);
		Collections.reverse(objectsToBePainted);
		for (CanvasElement o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}
		g.strokeRect(0, 0, 5, 5);

		drawMultiSelectRect(g);
	}

	private void drawMultiSelectRect(GraphicsContext g) {
		if (mode == MouseMode.MULTISELECT) {
			double x = Math.min(lastPoint.getX(), currentPoint.getX());
			double y = Math.min(lastPoint.getY(), currentPoint.getY());

			g.strokeRect(x, y, Math.abs(currentPoint.getX() - lastPoint.getX()), Math.abs(currentPoint.getY() - lastPoint.getY()));
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
//		FmmlxObject hitObject = getElementAt(p.getX(), p.getY());

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

	private void mouseDraggedStandard(Point2D p) {
//		if (hitObject != null) {
		for (Selectable s : selectedObjects)
			if (s instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) s;
//				o.setX((int) (p.getX() - o.mouseMoveOffsetX));
//				o.setY((int) (p.getY() - o.mouseMoveOffsetY));
				s.moveTo(p.getX() - o.mouseMoveOffsetX, p.getY() - o.mouseMoveOffsetY, this);
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
		resizeCanvas();
		redraw();
	}

	private void mouseReleasedStandard() {
		if (objectsMoved) {
			for (Selectable s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					comm.sendCurrentPosition(o);
				} else if (s instanceof FmmlxAssociation) {
					FmmlxAssociation a = (FmmlxAssociation) s;
					comm.sendCurrentPositions(a);
				}
		}
		objectsMoved = false;

	}

	private boolean isLeftClick(MouseEvent e) {
		return e.getButton() == MouseButton.PRIMARY;
	}

	private boolean isRightClick(MouseEvent e) {
		return e.getButton() == MouseButton.SECONDARY;
	}

	private boolean isMiddleClick(MouseEvent e) {
		return e.getButton() == MouseButton.MIDDLE;
	}

	private Selectable getElementAt(double x, double y) {
		for (FmmlxObject o : objects)
			if (o.isHit(x, y))
				return o;
		for (Edge e : edges) {
			System.err.println("Checking Edge " + e);
			if (e.isHit(x, y))
				return e;
		}
		return null;
	}

	private void handleLeftPressed(MouseEvent e) {
		Point2D p = scale(e);

		Selectable hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
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
				}
			}
			if (e.getClickCount() == 2) {
				handleClickOnNodeElement(p, hitObject);
			}
		} else {
			deselectAll();
		}


		if (selectedObjects.contains(hitObject)) {
			mode = MouseMode.STANDARD;
		} else {
			mode = MouseMode.MULTISELECT;
			storeLastClick(p.getX(), p.getY());
			storeCurrentPoint(p.getX(), p.getY());
		}

	}

	private void handleClickOnNodeElement(Point2D p, Selectable hitObject) {
		NodeBox hitNodeBox = null;
		Point2D relativePoint = new Point2D(
				p.getX() - ((FmmlxObject) hitObject).getX(),
				p.getY() - ((FmmlxObject) hitObject).getY());

		// Checking NodeBoxes
		for (NodeElement element : ((FmmlxObject) hitObject).getNodes()) {
			if (element.isHit(relativePoint.getX(), relativePoint.getY()) && element instanceof NodeBox) {
				if (((NodeBox) element).getElementType() != PropertyType.Selection && ((NodeBox) element).getElementType() != PropertyType.OperationValue) {
					hitNodeBox = (NodeBox) element;
				}
			}
		}
		if (hitNodeBox != null) {
			for (NodeElement nodeLabel : hitNodeBox.nodeElements) {
				if (nodeLabel.isHit(relativePoint.getX(), relativePoint.getY() - hitNodeBox.y) && nodeLabel instanceof NodeLabel) {
					FmmlxProperty hitProperty = ((NodeLabel) nodeLabel).getActionObject();
					if (hitNodeBox.getElementType() == PropertyType.Slot) {
						actions.changeSlotValue((FmmlxObject) hitObject, (FmmlxSlot) hitProperty);
					} else {
						actions.changeNameDialog((FmmlxObject) hitObject, hitNodeBox.getElementType(), hitProperty);
					}
				}
			}
		}
	}

	private void handleRightClick(MouseEvent e) {
		Point2D p = scale(e);

		Selectable hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
			activeContextMenu = hitObject.getContextMenu(actions);
			activeContextMenu.show(scrollerCanvas, Side.LEFT, e.getX(), e.getY());
		} else {
			activeContextMenu = new DefaultContextMenu(actions);
			activeContextMenu.show(scrollerCanvas, Side.LEFT, e.getX(), e.getY());
		}
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


	////////////////////////////////////////////////////////////////////
	private void clearContextMenus() {
		if (activeContextMenu != null && activeContextMenu.isShowing()) {
			activeContextMenu.hide();
		}
	}

	private void storeLastClick(double x, double y) {
		lastPoint = new Point2D(x, y);
	}

	private void storeCurrentPoint(double x, double y) {
		currentPoint = new Point2D(x, y);
	}

	private void setMouseOffset(Point2D p) {
		for (Selectable s : selectedObjects)
			if (s instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) s;
				o.mouseMoveOffsetX = p.getX() - o.getX();
				o.mouseMoveOffsetY = p.getY() - o.getY();
			}
	}

	public boolean isSelected(Selectable element) {
		return selectedObjects.contains(element);
	}

	private void deselectAll() {
		selectedObjects.clear();
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
			if (rec.contains(o.getX(), o.getY())) {
				select(o);
			}
		}
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

	public Vector<Edge> getEdges() {
		return edges;
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
	
	public ObservableList<FmmlxObject> getAllPossibleOf() {
		// TODO Auto-generated method stub
		return null;
	}


	////////////////////////////////////////////////////////////////////

	////					Messages to XMF							////
	////////////////////////////////////////////////////////////////////

	public void addAttribute(int classID, String name, int level, String type, Multiplicity multi) {
		comm.addAttribute(classID, name, level, type, multi);

	}

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		comm.addMetaClass(name, level, parents, isAbstract, x, y);
	}

	public void addNewInstance(int of, String name, int level, Vector<Integer> parents, boolean isAbstract, int x,
							   int y) {
		comm.addNewInstance(of, name, level, parents, isAbstract, x, y);
	}

	public void changeClassName(ChangeNameDialogResult res) {
		comm.changeClassName(res.getObjectId(), res.getNewName());
	}

	public void changeOperationName(ChangeNameDialogResult res) {
		comm.changeOperationName(res.getObjectId(), res.getOldName(), res.getNewName());
	}

	public void changeAttributeName(ChangeNameDialogResult res) {
		comm.changeAttributeName(res.getObjectId(), res.getOldName(), res.getNewName());
	}

	public void changeAssociationName(ChangeNameDialogResult result) {
		comm.changeAssociationName(result.getObjectId(), result.getOldName(), result.getNewName());

	}

	public void changeClassLevel(ChangeLevelDialogResult result) {
		comm.changeClassLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());

	}

	public void changeAttributeLevel(ChangeLevelDialogResult result) {
		comm.changeAttributeLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());

	}

	public void changeAssociationLevel(ChangeLevelDialogResult result) {
		comm.changeAssociationLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());

	}

	public void changeOperationLevel(ChangeLevelDialogResult result) {
		comm.changeOperationLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());

	}

	public void changeSlotValue(ChangeSlotValueDialogResult result) {
		comm.changeSlotValue(result.getObject().getId(), result.getSlot().getName(), result.getNewValue());
	}

	public void removeAttribute(FmmlxObject c, FmmlxAttribute a, Integer strategy) {
		comm.removeAttribute(c.getId(), a.getName(), 0);

	}

	public void changeOf(ChangeOfDialogResult result) {
		comm.changeOf(result.getObjectId(), result.getOldOfId(), result.getNewOfId());
	}

	public void removeClass(RemoveDialogResult result) {
		comm.removeClass(result.getObject().getId(), 0);

	}

	public void removeOperation(RemoveDialogResult result) {
		comm.removeOperation(result.getObject().getId(), result.getOperation().getName(), 0);

	}

	public void removeAttribute(RemoveDialogResult result) {
		comm.removeAttribute(result.getObject().getId(), result.getAttribute().getName(), 0);

	}

	public void removeAssociation(RemoveDialogResult result) {
		comm.removeAssociation(result.getObject().getId(), result.getAssociation().getName(), 0);

	}

	public void addOperation(AddDialogResult result) {
		comm.addOperation(result.getObjectId(), result.getOperationName(), result.getLevel(), result.getOperationType(), result.getBody());
		// TODO Auto-generated method stub

	}

	public void addAssociation(AddDialogResult result) {
		// TODO Auto-generated method stub

	}

	public void changeAttributeOwner(ChangeOwnerDialogResult result) {
		comm.changeAttributeOwner(result.getObject().getId(), result.getNewOwnerID());
	}

	public void changeOperationOwner(ChangeOwnerDialogResult result) {
		comm.changeOperationOwner(result.getObject().getId(), result.getNewOwnerID());
	}

	public void changeParent(ChangeParentDialogResult result) {
		comm.changeParent(result.getObject().getId(), result.getCurrentParentIds(), result.getNewParentIds());
	}

	public void changeTypeAttribute(ChangeTypeDialogResult result) {
		comm.changeAttributeType(result.getObject().getId(), result.getAttribute().getName(),
				result.getOldType(), result.getNewType());
	}

	public void changeTypeOperation(ChangeTypeDialogResult result) {
		comm.changeOperationType(result.getObject().getId(), result.getOperation().getName(),
				result.getOldType(), result.getNewType());
	}

	public void changeTypeAssociation(ChangeTypeDialogResult result) {
		comm.changeAssociationType(result.getObject().getId(), result.getAssociation().getName(),
				result.getOldType(), result.getNewType());
	}

	public void changeTargetAssociation(ChangeTargetDialogResult result) {
		// TODO Auto-generated method stub
	}

	public void changeMulitiplicityAttribute(ChangeMultiplicityDialogResult result) {
		// TODO Auto-generated method stub
	}

	public void changeBody(ChangeBodyDialogResult result) {
		// TODO Auto-generated method stub
	}

	public void checkOperationBody(String text) {
		comm.checkOperationBody(text);
	}

}
