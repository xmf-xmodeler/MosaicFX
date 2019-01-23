package tool.clients.editors.texteditor;

import javafx.scene.canvas.GraphicsContext;

public abstract class Tool {

  public abstract void paint(GraphicsContext gc, int x, int y, int width, int height);
  
  public abstract String toolTip();

  public abstract void click(TextEditor editor);

  public abstract void rightClick(int x,int y);

}
