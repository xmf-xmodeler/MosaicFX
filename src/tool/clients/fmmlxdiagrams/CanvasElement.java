package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;

public interface CanvasElement {

	void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram);

}
