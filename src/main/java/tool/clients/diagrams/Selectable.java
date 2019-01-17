package tool.clients.diagrams;

import javafx.geometry.Side;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;

public interface Selectable {

  void paintSelected(GraphicsContext gc, int x, int y);

  void moveEvent(int minX, int maxX, int minY, int maxY);

  void moveBy(int dx, int dy);

  ContextMenu rightClick(javafx.scene.Node anchor, Side side, int x, int y);

  void deselect();
  
  void select();

}
