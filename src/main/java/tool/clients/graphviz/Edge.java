package tool.clients.graphviz;

public class Edge {

  String source;
  String target;
  int    penwidth  = 2;
  String arrowhead = "";
  String label = "";

  public Edge(String source, String target) {
    super();
    this.source = source;
    this.target = target;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public int getPenwidth() {
    return penwidth;
  }

  public void setPenwidth(int penwidth) {
    this.penwidth = penwidth;
  }

  public String getArrowhead() {
    return arrowhead;
  }

  public void setArrowhead(String arrowhead) {
    this.arrowhead = arrowhead;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDotSource() {
    return "\"" + source + "\"->\"" + target + "\"[label=\"" + label + "\",arrowhead=\"" + arrowhead + "\",penwidth=\"" + penwidth + "\"];";
  }
}
