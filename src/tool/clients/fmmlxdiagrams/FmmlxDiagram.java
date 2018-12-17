package tool.clients.fmmlxdiagrams;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Color;

public class FmmlxDiagram {
	
	SplitPane mainView;
	final FmmlxDiagramCommunicator comm;
	private Canvas canvas;
	private Vector<FmmlxObject> objects = new Vector<>();

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, String label) {
		this.comm = comm;
		mainView = new SplitPane();
//		palette = new Palette(this);
//		palette.init(this);
		canvas = new Canvas(800, 600);
//		scroller = new ScrollPane(canvas);
		mainView.getItems().addAll(new Canvas(200, 600), canvas);
		mainView.setDividerPosition(0, 0.2);
		
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
		g.setFill(Color.BLACK);
		for(FmmlxObject o : objects) {
			o.paintOn(g, xOffset, yOffset);
		}
		g.strokeRect(0, 0, 5, 5);
	}

}
