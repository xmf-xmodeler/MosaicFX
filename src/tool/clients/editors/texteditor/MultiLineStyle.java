package tool.clients.editors.texteditor;

import tool.clients.editors.EditorClient;

public class MultiLineStyle {

  int   start;
  int   end;
//  Color color;

  public MultiLineStyle(int start, int end, Object color) {
    super();
    this.start = start;
    this.end = end;
//    this.color = color;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

//  public Color getColor() {
//    return color;
//  }

//  public StyleRange getStyle(int end) {
//    return new StyleRange(start, end, color, EditorClient.WHITE);
//  }

//  public String toString() {
//    return "MultiLineStyle(" + start + "," + end + "," + color + ")";
//  }

}
