package tool.clients.graphviz;

import java.util.Vector;

public class Row {

  int             cellSpacing;
  int             cellPadding;
  Vector<Element> elements = new Vector<Element>();

  public Row(Element... elements) {
    for (Element e : elements)
      this.elements.addElement(e);
  }

  public void remove(Element element) {
    elements.remove(element);
  }

  public String getDotSource() {
    if (isEmpty()) return "";
    String s = "<tr>";
    for (Element element : elements)
      s = s + "<td CELLSPACING=\"" + getCellSpacing() + "\" CELLPADDING=\"" + getCellPadding() + "\">" + element.getDotSource() + "</td>";
    return s + "</tr>";
  }

  public void add(Element e) {
    elements.add(e);
  }

  public int getCellSpacing() {
    return cellSpacing;
  }

  public void setCellSpacing(int cellSpacing) {
    this.cellSpacing = cellSpacing;
  }

  public int getCellPadding() {
    return cellPadding;
  }

  public void setCellPadding(int cellPadding) {
    this.cellPadding = cellPadding;
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

}
