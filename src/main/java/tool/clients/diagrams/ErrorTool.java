package tool.clients.diagrams;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ErrorTool extends TrayTool {

  boolean                    isError = false;

  public void paint(GraphicsContext gc, int x, int y, int width, int height) {
//    Color fg = gc.get
//    Paint bg = gc.getFill();
	gc.setFill(isError ? Color.RED : Color.GREEN);
	gc.setStroke(Color.BLACK);
	gc.fillOval(x, y, width, height);
//    gc.setForeground(fg);
//    gc.setBackground(bg);
  }

  public String toolTip() {
    return !isError ? "no errors" : "error: click to view";
  }

  public void click(Diagram diagram) {
    diagram.selectError();
  }

  public void error(Diagram diagram) {
    isError = true;
  }

  public void clear() {
    isError = false;
  }

  public void rightClick(int x, int y) {

  }

  public void setError(boolean isError) {
    this.isError = isError;
  }

}
