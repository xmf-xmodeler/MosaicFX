package tool.clients.fmmlxdiagrams.graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public interface View {
	Canvas getCanvas();
	Affine getCanvasTransform();
	void centerObject();
	void centerObject(FmmlxObject affectedObject);
}
