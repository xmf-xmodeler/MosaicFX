package tool.clients.fmmlxdiagrams.graphics.wizard;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.View;

public class ConcreteSyntaxIcon extends Pane implements View {
	
	private Canvas canvas; 
	private Affine affine;		

	public ConcreteSyntaxIcon(final NodeGroup group, int size) {

		canvas = new Canvas(size,size);
		getChildren().add(canvas);

		canvas.widthProperty().bind(this.widthProperty());
		canvas.heightProperty().bind(this.heightProperty());
		
		setMinWidth(size);
		setMinHeight(size);
		setMaxWidth(size);
		setMaxHeight(size);
		affine = group.getZoomViewTransform(size,size);

		group.paintOn(this, false);
		
		ChangeListener<Number> canvasChangeListener = (obs, oldVal, newVal) -> {
//			Affine a = group.getZoomViewTransform(canvas.canvas);
//			canvas.affine = a;
			affine = group.getZoomViewTransform(size,size);
			group.paintOn(this, false);
		};
		
		canvas.widthProperty().addListener(canvasChangeListener);
		canvas.heightProperty().addListener(canvasChangeListener);
	}
	
	@Override public Canvas getCanvas() { return canvas; }
	@Override public Affine getCanvasTransform() { return affine; }
	@Override public void centerObject(FmmlxObject affectedObject) {}

}	