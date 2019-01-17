package tool.clients.graphviz;

public class Text extends Element {

  String s;

  public Text(String s) {
    super();
    this.s = s;
  }

  public String getDotSource() {
    return s;
  }

}
