package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class FmmlxDiagram {

	enum MouseMode {
		NONE, DROP_MODE
	};

	SplitPane mainView;
	final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();
	private transient Vector<FmmlxObject> selectedObjects = new Vector<>();
	private final Palette palette;
	private transient boolean objectsMoved = false;
	MouseMode mouseMode = MouseMode.NONE;

	Point2D canvasRawSize = new Point2D(1200, 800);
	double zoom = 1.;
	Affine transformFX;

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
//		palette = new Palette(this);
//		palette.init(this);
		canvas = new Canvas(canvasRawSize.getX(), canvasRawSize.getY());
		palette = new Palette(this);
		ScrollPane scroller = new ScrollPane(palette);
		// scroller.setMinWidth(200);
		scroller.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroller.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		ScrollPane scrollerCanvas = new ScrollPane(canvas);

		mainView.getItems().addAll(scroller, scrollerCanvas);
		mainView.setDividerPosition(0, 0.23);
		transformFX = new Affine();
		
//		mainView.setDividerPosition(0, 0.2);

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
			//System.out.println("++++++" + canvasRawSize);
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
		Vector<FmmlxObject> objectsToBePainted = new Vector<>(objects);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}
		g.strokeRect(0, 0, 5, 5);
	}

	private void mousePressed(MouseEvent e) {
		Point2D p = scale(e);
		if (isLeftClick(e) && mouseMode == MouseMode.DROP_MODE) {

		}
		if (isMiddleClick(e)) {
			selectedObjects.addAll(objects);
		} else {
			FmmlxObject hitObject = getElementAt(p.getX(), p.getY());

			selectedObjects.clear();
			if (hitObject != null)
				selectedObjects.add(hitObject);
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

	public javafx.geometry.Point2D scale(javafx.scene.input.MouseEvent event) {
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
		this.zoom = Math.min(4, Math.max(zoom, 1./8));
		
		transformFX = new Affine();
		transformFX.appendScale(zoom, zoom);
		resizeCanvas();
	}
}
