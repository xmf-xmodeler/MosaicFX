package tool.helper;

import java.util.Vector;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.CanvasElement;
import tool.xmodeler.XModeler;

public class DrawingApplication extends Application {

	private SplitPane splitPane;
	private Canvas canvas;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;
	private Separator separator;
	private Point2D lastClicked = new Point2D(0, 0);
	private Point2D currentPoint = new Point2D(0,0);
	private Point2D lastMoved = new Point2D(0, 0);
	private final String PATH = "m 6.769531,0 c -2.318,0 -4.203125,1.886125 -4.203125,4.203125 0,2.318 1.885125,4.203125 4.203125,4.203125 2.318,0 4.204125,"
			+ "-1.884125 4.203125,-4.203125 C 10.972656,1.886125 9.086531,0 6.769531,0 Z m 0,0.875 c 1.835,0 3.329125,1.493125 3.328125,3.328125 0,1.836 -1.493125,3.330078"
			+ " -3.328125,3.330078 -1.836,0 -3.328125,-1.495078 -3.328125,-3.330078 0,-1.835 1.492125,-3.328125 3.328125,-3.328125 z m 18.56836,0.605468 -0.734375,0.603516 "
			+ "-2.652344,13.265625 0.734375,0.898438 h 8.365234 l 0.002,1.373046 h -9.353515 v 0.75 3.585938 h -2.498047 c -8.17e-4,-1.185082 -0.733767,-2.186411 -1.705078,"
			+ "-2.824219 -0.971853,-0.638164 -2.224409,-0.988281 -3.59375,-0.988281 h -3.310547 v -3.998047 c 0,-2.913215 -2.382886,-5.296875 -5.296875,-5.296875 -2.913215,0 "
			+ "-5.292969,2.384495 -5.292969,5.296875 V 26.09375 L 0,26.255859 v 0.0059 c 0,2.886675 2.353719,5.254819 5.28125,5.294922 h 0.00586 7.560547 v 12.546875 h 5.296875"
			+ " 5.294922 v -0.992187 c 0,-1.185285 -0.729639,-2.18575 -1.699219,-2.824219 -0.722051,-0.475471 -1.629647,-0.728768 -2.59375,-0.859375 l -0.0098,-8.861328 c "
			+ "0,-2.913215 -2.383939,-5.294922 -5.296875,-5.294922 h -3.248047 v -2.320313 h 3.25 v 1.160157 H 26.037075 V 43.476603 H 30.33981 V 24.111328 h 6.455078 v "
			+ "-2.154297 h -0.654297 v -4.335938 h -1.636719 v -6.580078 c 0,-0.665732 -0.461295,-1.138835 -1.033203,-1.421875 L 34.917935,2.378906 34.18356,1.480468 Z m "
			+ "0.002,0.75 h 8.84375 l -0.511719,2.556641 -2.152344,10.710938 h -8.833984 z m 0.755859,0.921875 -2.285156,11.423828 h 6.964844 L 33.058594,3.152343 Z M"
			+ " 5.294922,9.841797 c 2.378,0 4.304687,1.927687 4.304687,4.304687 v 4.990234 h 4.302735 c 2.387998,0 4.305032,1.262215 4.30664,2.820313 h -4.367187 v "
			+ "0.002 H 9.599609 v 4.304687 h 4.240235 c 2.377,0 4.304687,1.925735 4.304687,4.302735 l 0.01172,9.722656 c 2.377,0 4.291016,1.264266 4.291016,2.822266"
			+ " H 18.144531 13.839844 V 30.564453 H 5.294922 c -2.409,-0.033 -4.302735,-1.947735 -4.302735,-4.302735 L 0.994141,26.099609 V 14.146484 c 0,-2.377 1.923781,"
			+ "-4.304689 4.300781,-4.304687 z m 28.039062,0.425781 c 0.247415,0.174087 0.419922,0.447454 0.419922,0.773437 v 6.580078 h -1.894531 z m -10.884765,8.103515 h "
			+ "12.941406 v 3.585938 H 22.449219 Z m 1.160156,1.292969 v 0.5 0.5 h 4.220703 v -0.5 -0.5 z";
	private double zoom=1.0;
	private double rotate=0;
	private Vector<MyPath> paths = new Vector<MyPath>();
	private Affine currentAffine = new Affine();
	Affine aCanvas = new Affine();
	
	public DrawingApplication() {

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		splitPane = new SplitPane();

		canvas = new Canvas(1500, 1000);
		
		button1 = new Button("+");
		button1.setOnAction(e -> {
			zoom *= 1.33;
			aCanvas = new Affine();
			aCanvas.appendScale(zoom, zoom);
			repaint();
		});
		button2 = new Button("100%");
		button2.setOnAction(e -> {
			zoom = 1.0;
			aCanvas = new Affine();
			aCanvas.appendScale(zoom, zoom);
			repaint();
		});
		button3 = new Button("-");
		button3.setOnAction(e -> {
			zoom /= 1.33;
			aCanvas = new Affine();
			aCanvas.appendScale(zoom, zoom);
			repaint();
		});
		button4 = new Button("Rotate");
		button4.setOnAction(e-> {
			rotate += 45;
		});
		button5 = new Button("???");
		button6 = new Button("?????");
		separator = new Separator();

		VBox leftControl = new VBox(button1, button2, button3, separator, button4, button5, button6);
		VBox rightControl = new VBox(canvas);

		MyPath path1 = new MyPath();
		path1.myTransform.appendTranslation(100,100);
		paths.add(path1);
		
		MyPath path2 = new MyPath();
		path2.myTransform.prependTranslation(100,100);
		path2.myTransform.prependScale(.5,.5);
		path1.paths.add(path2);
		
		Scene scene = new Scene(splitPane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("DrawingApp");
		
		SVGPath svgPath = new SVGPath();
		svgPath.setContent(PATH);
		
		
		splitPane.getItems().addAll(leftControl, rightControl);
		primaryStage.show();
		repaint();
		canvas.setOnMouseClicked(e -> mouseClicked(new Point2D(e.getX(), e.getY())));
		canvas.setOnMouseMoved(e -> mouseMoved(new Point2D(e.getX(), e.getY())));
		canvas.setOnMouseDragged(e-> mouseDragged(new Point2D(e.getX(),e.getY())));
		canvas.setOnMouseReleased(e->handleMouseRelease(e));
		canvas.setOnMousePressed(e -> handlePressedLeftMouse(e));
	}

	private void mouseClicked(Point2D p) {
//		
//		Affine aCanvas = new Affine();
//		aCanvas.appendScale(zoom, zoom);
//		for(MyPath myPath:paths) {
//			myPath.action(p,canvas.getGraphicsContext2D(),aCanvas);
//		}
//		
//		repaint();
	}
	
	private void mouseMoved(Point2D p) {
		lastMoved = p;
		repaint();	
	}
	
	private void handlePressedLeftMouse(MouseEvent e) {
		lastClicked = new Point2D(e.getX(),e.getY());
		repaint();	
	}
		
	private void handleMouseRelease(MouseEvent e) {
		lastClicked = new Point2D(0,0);
		currentPoint = new Point2D(0,0);
		try {
			Affine a = new Affine(aCanvas);
			a.prepend(currentAffine);
			a.prepend(aCanvas.createInverse());
			paths.firstElement().moveTransform(a);
		} catch (NonInvertibleTransformException e1) {
			e1.printStackTrace();
		}
		
		currentAffine = new Affine();
		repaint();
	}
		
	private void mouseDragged(Point2D p) {
		currentPoint = p;
		currentAffine = new Affine(Transform.translate(p.getX() - lastClicked.getX(), p.getY() - lastClicked.getY()));
		repaint();
	}

	private void repaint() {
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setTransform(new Affine());
		gc.clearRect(0, 0, 1500, 1000);
		
//		gc.setTransform(aCanvas);
		
		for(MyPath myPath:paths) {
			Affine aObject = new Affine(aCanvas);
			aObject.prepend(currentAffine);
			myPath.draw(gc, aObject, lastMoved);
		}
		
		gc.setTransform(new Affine());
		gc.setFill(Color.BLACK);
		gc.fillText("p="+lastMoved, lastMoved.getX(), lastMoved.getY());
		try {
			Point2D q = aCanvas.inverseTransform(lastMoved);
			gc.fillText("q="+q, lastMoved.getX(), lastMoved.getY()+15);
		} catch (NonInvertibleTransformException e) {
			e.printStackTrace();
		}
		gc.strokeLine(currentPoint.getX(), currentPoint.getY(), lastClicked.getX(), lastClicked.getY());
		
	}
}
