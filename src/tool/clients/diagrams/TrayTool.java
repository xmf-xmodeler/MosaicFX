package tool.clients.diagrams;

import javafx.scene.canvas.GraphicsContext;

public abstract class TrayTool {

  public abstract void paint(GraphicsContext gc, int x, int y, int width, int height);

  public abstract String toolTip();

  public abstract void click(Diagram diagram);

  public abstract void rightClick(int x, int y);

}
