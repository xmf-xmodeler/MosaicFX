package tool.clients.graphviz;

public class Node {

  String  id;
  Element label = null;
  String  doc   = "";
  String  shape = "none";
  String  style = "solid";
  String  URL   = "";

  public Node(String id) {
    this.id = id;
  }

  public String getShape() {
    return shape;
  }

  public void setShape(String shape) {
    this.shape = shape;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String uRL) {
    URL = uRL;
  }

  public String getDoc() {
    return doc;
  }

  public void setDoc(String doc) {
    this.doc = doc;
  }

  public String getId() {
    return id;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getDotSource() {
    if (label != null)
      return "\"" + id + "\"" + "[URL=\"" + getURL() + "\",tooltip=\"" + docAsHTML() + "\",shape=" + getShape() + ",style=" + getStyle() + ",label=<" + label.getDotSource() + ">];";
    else return "\"" + id + "\";";
  }

  private String docAsHTML() {
    return doc.replace("\n", "&#10;");
  }

  public Element getLabel() {
    return label;
  }

  public Text addText(String text) {
    Text t = new Text(text);
    label = t;
    return t;
  }

  public Menu addMenu(String text, String... options) {
    Menu menu = new Menu(text, options);
    label = menu;
    return menu;
  }

  public VBox addVBox() {
    VBox vbox = new VBox();
    label = vbox;
    return vbox;
  }

}
