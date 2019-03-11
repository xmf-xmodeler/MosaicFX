package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class FmmlxDiagram {
	
	SplitPane mainView;
	final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();
	private transient Vector<FmmlxObject> selectedObjects = new Vector<>();
	private final Palette palette;
	private transient boolean objectsMoved = false;

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
//		palette = new Palette(this);
//		palette.init(this);
		canvas = new Canvas(1200, 800);
		palette = new Palette(this);
		ScrollPane scroller = new ScrollPane(palette);
		scroller.setMinSize(200, 800);
		mainView.getItems().addAll(scroller, canvas);
//		mainView.setDividerPosition(0, 0.2);

		canvas.setOnMousePressed((e) -> {mousePressed(e);});
		canvas.setOnMouseDragged((e) -> {mouseDragged(e);});
		canvas.setOnMouseReleased((e) -> {mouseReleased(e);});
		
//		Runnable task = () -> { fetchDiagramData(); };
		new Thread(() -> { fetchDiagramData(); }).start();

		redraw();
	}

	private void fetchDiagramData() {
		Vector<FmmlxObject> fetchedObjects = comm.getAllObjects();
		objects.clear(); // to be replaced when updating instead of loading form scratch
		objects.addAll(fetchedObjects);
		for(FmmlxObject o : objects) {
//			comm.fetchAttributes(o);
			o.fetchData(comm);
		}
		redraw();
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
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(Color.BLACK);
		Vector<FmmlxObject> objectsToBePainted = new Vector<>(objects);
		Collections.reverse(objectsToBePainted);
		for(FmmlxObject o : objectsToBePainted) {
			o.paintOn(g, xOffset, yOffset, this);
		}
		g.strokeRect(0, 0, 5, 5);
	}
	
	private void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseButton.MIDDLE) {
			selectedObjects.addAll(objects);
		} else {
			FmmlxObject hitObject = getElementAt(e.getX(), e.getY());
			
			selectedObjects.clear();
			if(hitObject != null)selectedObjects.add(hitObject);
		}
//		if(hitObject != null) {
//			selectedObjects 
//		}
		for(FmmlxObject o : selectedObjects) {
			o.mouseMoveOffsetX = e.getX() - o.x;
			o.mouseMoveOffsetY = e.getY() - o.y;
		}
		redraw();
	}
	
	private void mouseDragged(MouseEvent e) {
		for(FmmlxObject o : selectedObjects) {
			o.x = (int) (e.getX() - o.mouseMoveOffsetX);
			o.y = (int) (e.getY() - o.mouseMoveOffsetY);
		}
		objectsMoved = true;
		redraw();
	}
	
	private void mouseReleased(MouseEvent e) {
		if(objectsMoved) {
			for(FmmlxObject o : selectedObjects) {
			comm.sendCurrentPosition(o);
			}
		}
		objectsMoved = false;
	}

	private FmmlxObject getElementAt(double x, double y) {
		for(FmmlxObject o : objects)
			if(o.isHit(x,y)) return o;
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
		comm.addMetaClass(name, level, parents, isAbstract, x,y);
	}

	public int getTestClassId() {
		return objects.firstElement().id;
	}

	public void addInstance(int testClassId, String name, Vector<Integer> parents, boolean isAbstract, int x, int y) {
		comm.addInstance(testClassId, name, parents, isAbstract, x,y);
	}

}
