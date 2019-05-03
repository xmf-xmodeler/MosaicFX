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
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import tool.clients.fmmlxdiagrams.menus.ObjectContextMenu;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;

public class FmmlxDiagram {

	SplitPane mainView;
	final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();
	private Vector<Edge> edges = new Vector<>();

	private transient Vector<FmmlxObject> selectedObjects = new Vector<>();
	private final Palette palette;
	private DefaultContextMenu defaultContextMenu;
	private ObjectContextMenu objectContextMenu;
	private DiagramActions actions;
	private transient boolean objectsMoved = false;

	public Vector<FmmlxObject> fetchObjects() { // TODO Ask
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
		for(FmmlxObject object : objects) {
			if(object.getId() == id)
				return object;
		}
		return null;
	}

	private Point2D canvasRawSize = new Point2D(1200, 800);
	private double zoom = 1.;
	private Affine transformFX;

	private ScrollPane scrollerCanvas;

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		actions = new DiagramActions(this);
		palette = new Palette(actions);
		scrollerCanvas = new ScrollPane(canvas);
		mainView.setOrientation(Orientation.VERTICAL);
		mainView.getItems().addAll(palette, scrollerCanvas);
		transformFX = new Affine();

		defaultContextMenu = new DefaultContextMenu(actions);

		canvas.setOnMousePressed((e) -> {
			mousePressed(e);
		});
		canvas.setOnMouseDragged((e) -> {
			mouseDragged(e);
		});
		canvas.setOnMouseReleased((e) -> {
			mouseReleased(e);
		});

//		Runnable task = () -> { fetchDiagramData(); };
		new Thread(() -> {
			fetchDiagramData();
		}).start();

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
		if(objects.size() >= 2) {
			Edge e = new Edge(objects.get(0), objects.get(1));
			edges.add(e);
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

	// temp:
	int render = 0;

	public void redraw() {
		if (render == 0) {
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
	}

	private void mousePressed(MouseEvent e) {
		Point2D p = scale(e);

		if (objectContextMenu != null && objectContextMenu.isShowing()) {
			objectContextMenu.hide();
		}
		if (defaultContextMenu != null && defaultContextMenu.isShowing()) {
			defaultContextMenu.hide();
		}

		if (isMiddleClick(e)) {
			selectedObjects.addAll(objects);
		} else {
			FmmlxObject hitObject = getElementAt(p.getX(), p.getY());
			if (e.isControlDown()) {
				if (selectedObjects.contains(selectedObjects)) {
					selectedObjects.remove(hitObject);
				} else {
					selectedObjects.add(hitObject);
				}
			} else {
				if (!selectedObjects.contains(hitObject)) {
					selectedObjects.clear();
					if (hitObject != null)
						selectedObjects.add(hitObject);
				}
			}
		}

		if (isRightClick(e)) {
			FmmlxObject hitObject = getElementAt(p.getX(), p.getY());

			if (hitObject != null) {
				objectContextMenu = new ObjectContextMenu(hitObject, actions);
				objectContextMenu.show(scrollerCanvas, Side.LEFT, p.getX(), p.getY());
			} else {
				defaultContextMenu.show(scrollerCanvas, Side.LEFT, p.getX(), p.getY());
			}
		}
//		if(hitObject != null) {
//			selectedObjects 
//		}
		for (FmmlxObject o : selectedObjects) {
			o.mouseMoveOffsetX = p.getX() - o.x;
			o.mouseMoveOffsetY = p.getY() - o.y;
		}
		redraw();
	}

	private void mouseDragged(MouseEvent e) {
		Point2D p = scale(e);
		for (FmmlxObject o : selectedObjects) {
			o.x = (int) (p.getX() - o.mouseMoveOffsetX);
			o.y = (int) (p.getY() - o.mouseMoveOffsetY);
		}
		objectsMoved = true;
		redraw();
	}

	private void mouseReleased(MouseEvent e) {
		if (objectsMoved) {
			for (FmmlxObject o : selectedObjects) {
				comm.sendCurrentPosition(o);
			}
		}
		objectsMoved = false;
		resizeCanvas();
		redraw();
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

	public boolean isSelected(FmmlxObject fmmlxObject) {
		return selectedObjects.contains(fmmlxObject);
	}

	public void updateDiagram() {
		new Thread(() -> {
			fetchDiagramData();
		}).start();
	}

	public void addMetaClass(String name, int level, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		comm.addMetaClass(name, level, parents, isAbstract, x, y);
	}

	public int getTestClassId() {
		return objects.firstElement().id;
	}

	public void addInstance(int testClassId, String name, Vector<Integer> parents, boolean isAbstract, int x, int y) {
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
