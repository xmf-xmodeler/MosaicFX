package tool.clients.graphviz;

import java.util.Vector;

public abstract class Box extends Element {

  int         border;
  int         cellBorder;
  int         cellSpacing;
  int         cellPadding;

  Vector<Row> rows = new Vector<Row>();

  public void add(Row row) {
    rows.add(row);
  }

  public Row addRow() {
    Row row = new Row();
    rows.add(row);
    return row;
  }

  public Row lastRow() {
    return rows.lastElement();
  }

  public Text addText(String text) {
    Text t = new Text(text);
    lastRow().add(t);
    return t;
  }

  public Menu addMenu(String text, String... options) {
    Menu menu = new Menu(text, options);
    lastRow().add(menu);
    return menu;
  }

  public boolean isEmpty() {
    for (Row row : rows)
      if (!row.isEmpty()) return false;
    return true;
  }

  public void remove(Element element) {
    for (Row row : rows)
      row.remove(element);
  }

  public VBox addVBox() {
    VBox vbox = new VBox();
    lastRow().add(vbox);
    return vbox;
  }

  public int getBorder() {
    return border;
  }

  public void setBorder(int border) {
    this.border = border;
  }

  public int getCellBorder() {
    return cellBorder;
  }

  public void setCellBorder(int cellBorder) {
    this.cellBorder = cellBorder;
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

  public String getDotSource() {
    String s = "<table BORDER=\"" + getBorder() + "\" CELLBORDER=\"" + getCellBorder() + "\" CELLSPACING=\"" + getCellSpacing() + "\" CELLPADDING=\"" + getCellPadding() + "\">";
    for (Row row : rows)
      s = s + row.getDotSource();
    return s + "</table>";
  }

}
