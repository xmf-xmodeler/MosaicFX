package tool.clients.fmmlxdiagrams;

import javafx.scene.canvas.GraphicsContext;

public interface NodeElement {

	public void paintOn(GraphicsContext g, double xOffset, double yOffset, FmmlxDiagram diagram);

}
