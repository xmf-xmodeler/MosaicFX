package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import tool.clients.diagrams.EdgePainter;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

public class FmmlxDiagram {

	enum MouseMode {
		STANDARD, MULTISELECT
	}

	private SplitPane mainView;
	private final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();

	private transient Vector<FmmlxObject> selectedObjects = new Vector<>();
	private DefaultContextMenu defaultContextMenu;
	private ObjectContextMenu objectContextMenu;
	private DiagramActions actions;
	private transient boolean objectsMoved = false;
	private Point2D lastPoint;
	private Point2D actualPoint;
	private MouseMode mode = MouseMode.STANDARD;

	public Vector<FmmlxObject> fetchObjects() {
		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
		objects.clear(); // to be replaced when updating instead of loading form scratch
		objects.addAll(fetchedObjects);
		for (FmmlxObject o : objects) {
//			comm.fetchAttributes(o);
			o.fetchData(comm);
		}
		return objects;
	}

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

		defaultContextMenu = new DefaultContextMenu(actions);

		canvas.setOnMousePressed(this::mousePressed);
		canvas.setOnMouseDragged(this::mouseDragged);
		canvas.setOnMouseReleased(this::mouseReleased);

		new Thread(this::fetchDiagramData).start();

		redraw();
	}

	public Canvas getCanvas() {
		return canvas;
	}

	private void fetchDiagramData() {
		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
		objects.clear(); // to be replaced when updating instead of loading form scratch
		objects.addAll(fetchedObjects);
		for (FmmlxObject o : objects) {
//			comm.fetchAttributes(o);
			o.fetchData(comm);
		}
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
//				checkSize();
			paintOn(canvas.getGraphicsContext2D(), 0, 0);
		} else { // create a new Thread
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
//					checkSize();
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
		Vector<FmmlxObject> objectsToBePainted = new Vector<>(objects);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}
		g.strokeRect(0, 0, 5, 5);

		drawMultiSelectRect(g);
	}

	private void drawMultiSelectRect(GraphicsContext g) {
		if (mode == MouseMode.MULTISELECT) {
			double x = Math.min(lastPoint.getX(), actualPoint.getX());
			double y = Math.min(lastPoint.getY(), actualPoint.getY());

			g.strokeRect(x, y, Math.abs(actualPoint.getX() - lastPoint.getX()), Math.abs(actualPoint.getY() - lastPoint.getY()));
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
			handleLeftClick(e);
		}
		if (isRightClick(e)) {
			handleRightClick(e);
		}
		setMouseOffset(p);
		redraw();
	}

	private void mouseDragged(MouseEvent e) {
		Point2D p = scale(e);
		FmmlxObject hitObject = getElementAt(p.getX(), p.getY());

		if (mode == MouseMode.MULTISELECT) {
			storeActualPoint(p.getX(), p.getY());
			redraw();
		}
		if (mode == MouseMode.STANDARD) {
			mouseDraggedStandard(p, hitObject);
		}
	}

	private void mouseDraggedStandard(Point2D p, FmmlxObject hitObject) {
		if (hitObject != null) {
			for (FmmlxObject o : selectedObjects) {
				o.setX((int) (p.getX() - o.mouseMoveOffsetX));
				o.setY((int) (p.getY() - o.mouseMoveOffsetY));
			}
			objectsMoved = true;
			redraw();
		} else {
			mode = MouseMode.MULTISELECT;
			storeLastClick(p.getX(), p.getY());
		}
	}

	private void mouseReleased(MouseEvent e) {
		if (mode == MouseMode.MULTISELECT) {
			handleMultiSelect();
		}
		if (mode == MouseMode.STANDARD) {
			mouseReleasedStandard();
		}
		mode = MouseMode.STANDARD;
		resizeCanvas();
		redraw();
	}

	private void mouseReleasedStandard() {
		if (objectsMoved) {
			for (FmmlxObject o : selectedObjects) {
				comm.sendCurrentPosition(o);
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

	private FmmlxObject getElementAt(double x, double y) {
		for (FmmlxObject o : objects)
			if (o.isHit(x, y))
				return o;
		return null;
	}

	private void handleLeftClick(MouseEvent e) {
		Point2D p = scale(e);

		FmmlxObject hitObject = getElementAt(p.getX(), p.getY());
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
		} else {
			deselectAll();
		}
	}

	private void handleRightClick(MouseEvent e) {
		Point2D p = scale(e);

		FmmlxObject hitObject = getElementAt(p.getX(), p.getY());
		if (hitObject != null) {
			objectContextMenu = new ObjectContextMenu(hitObject, actions);
			objectContextMenu.show(scrollerCanvas, Side.LEFT, p.getX(), p.getY());
		} else {
			defaultContextMenu.show(scrollerCanvas, Side.LEFT, p.getX(), p.getY());
		}
	}

	////////////////////////////////////////////////////////////////////

	private void clearContextMenus() {
		if (objectContextMenu != null && objectContextMenu.isShowing()) {
			objectContextMenu.hide();
		}
		if (defaultContextMenu != null && defaultContextMenu.isShowing()) {
			defaultContextMenu.hide();
		}
	}

	private void storeLastClick(double x, double y) {
		lastPoint = new Point2D(x, y);
	}

	private void storeActualPoint(double x, double y) {
		actualPoint = new Point2D(x, y);
	}

	private void setMouseOffset(Point2D p) {
		for (FmmlxObject o : selectedObjects) {
			o.mouseMoveOffsetX = p.getX() - o.getX();
			o.mouseMoveOffsetY = p.getY() - o.getY();
		}
	}


	public boolean isSelected(FmmlxObject fmmlxObject) {
		return selectedObjects.contains(fmmlxObject);
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
		double x = Math.min(lastPoint.getX(), actualPoint.getX());
		double y = Math.min(lastPoint.getY(), actualPoint.getY());
		double w = Math.abs(actualPoint.getX() - lastPoint.getX());
		double h = Math.abs(actualPoint.getY() - lastPoint.getY());

		Rectangle rec = new Rectangle(x, y, w, h);
		deselectAll();
		for (FmmlxObject o : objects) {
			if (rec.contains(o.getX(), o.getY())) {
				select(o);
			}
		}
	}

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		comm.addMetaClass(name, level, parents, isAbstract, x, y);
	}

	public int getTestClassId() {
		return objects.firstElement().id;
	}

	public void addInstance(int testClassId, String name, Vector<Integer> parents, boolean isAbstract, int x,
							int y) {
		comm.addInstance(testClassId, name, parents, isAbstract, x, y);
	}

	public void addNewInstance(int of, String name, int level, Vector<String> parents, boolean isAbstract, int x,
							   int y) {
		comm.addNewInstance(of, name, level, parents, isAbstract, x, y);
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
}
