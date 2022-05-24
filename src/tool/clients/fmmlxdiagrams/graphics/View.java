package tool.clients.fmmlxdiagrams.graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;

public interface View {
	Canvas getCanvas();
	Affine getCanvasTransform();
	void findObject();
}
