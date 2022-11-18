package tool.helper;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MyPath {
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
	Vector<MyPath> paths = new Vector<MyPath>();
	
	public Affine myTransform = new Affine();
	
	public void draw(GraphicsContext gc, Affine aCanvas, Point2D lastMoved) {
		Affine aObject = new Affine(aCanvas);
		aObject.append(myTransform);
		gc.setTransform(aObject);
		gc.beginPath();
		gc.appendSVGPath(PATH);
		gc.setFill(gc.isPointInPath(lastMoved.getX(), lastMoved.getY())?Color.DARKGREEN:Color.DARKRED);
		gc.fill();
		gc.closePath();
		
		
		gc.fillText(myTransform.transform(new Point2D(0,0))+" ", 0, 0);
					
		for(MyPath myPath:paths) {
			myPath.draw(gc, aObject, lastMoved);
		}
	}

	public void action(Point2D lastMoved, GraphicsContext gc, Affine aCanvas) {
		boolean hit = false;
		Affine aObject = new Affine(aCanvas);
		aObject.append(myTransform);
		gc.setTransform(aObject);
		gc.beginPath();
		gc.appendSVGPath(PATH);
		hit=gc.isPointInPath(lastMoved.getX(), lastMoved.getY());
		gc.closePath();
		if (hit) {
			myTransform.appendRotation(90,18,22);
		} else {
			for(MyPath myPath:paths) {
				myPath.action(lastMoved,gc,aObject);
			}
		}
		
	}
	
	void moveTransform(Affine affine) {
		myTransform.prepend(affine);
	}
	
}
