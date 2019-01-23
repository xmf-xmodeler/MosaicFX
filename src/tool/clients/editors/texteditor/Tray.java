package tool.clients.editors.texteditor;

import java.util.Vector;

public class Tray {

  private static int TOOL_WIDTH  = 10;
  private static int TOOL_HEIGHT = 10;
  private static int SEPARATE = 5;

  Vector<Tool>       tools       = new Vector<Tool>();

  public void addTool(Tool tool) {
    tools.add(tool);
  }

//  public void paint(GraphicsContext gc, int width, int height) {
//    int trayWidth = TOOL_WIDTH * tools.size() + (tools.size() * SEPARATE);
//    int x = width - trayWidth;
//    int y = height - TOOL_HEIGHT;
//    for (Tool tool : tools) {
//      tool.paint(gc, x, y, TOOL_WIDTH, TOOL_HEIGHT);
//      x += TOOL_WIDTH + SEPARATE;
//    }
//  }

  public Tool selectTool(int x0, int y0, int width, int height) {
    int trayWidth = TOOL_WIDTH * tools.size() + (tools.size() * SEPARATE);
    int x = width - trayWidth;
    int y = height - TOOL_HEIGHT;
    for (Tool tool : tools) {
      if (x0 >= x && x0 <= (x+TOOL_WIDTH) && y0 >= y && y0 <= (y + TOOL_HEIGHT))
        return tool;
      else x += TOOL_WIDTH + SEPARATE;
    }
    return null;
  }

}
