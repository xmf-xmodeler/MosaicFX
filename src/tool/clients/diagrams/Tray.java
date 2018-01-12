package tool.clients.diagrams;

import java.util.Vector;

import org.eclipse.swt.graphics.GC;

import javafx.scene.canvas.GraphicsContext;

public class Tray {

  private static int TOOL_WIDTH  = 10;
  private static int TOOL_HEIGHT = 10;
  private static int SEPARATE    = 5;

  Vector<TrayTool>   tools       = new Vector<TrayTool>();

  public void addTool(TrayTool tool) {
    tools.add(tool);
  }

  public void paint(GraphicsContext gc, int width, int height) {
    int trayWidth = TOOL_WIDTH * tools.size() + (tools.size() * SEPARATE);
    int x = width - trayWidth;
    int y = height - TOOL_HEIGHT;
    for (TrayTool tool : tools) {
//    	System.err.println("Tray::paint()");
        tool.paint(gc, x, y, TOOL_WIDTH, TOOL_HEIGHT);
      x += TOOL_WIDTH + SEPARATE;
    }
  }

  public TrayTool selectTool(int x0, int y0, int width, int height) {
    int trayWidth = TOOL_WIDTH * tools.size() + (tools.size() * SEPARATE);
    int x = width - trayWidth;
    int y = height - TOOL_HEIGHT;
    for (TrayTool tool : tools) {
      if (x0 >= x && x0 <= (x + TOOL_WIDTH) && y0 >= y && y0 <= (y + TOOL_HEIGHT))
        return tool;
      else x += TOOL_WIDTH + SEPARATE;
    }
    return null;
  }

}
