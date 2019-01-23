package tool.clients.editors.pprint;

public class Literal extends PPrint {

  String string;

  public Literal(String string) {
    super();
    this.string = string;
  }

  public String toString(int indent) {
    return string;
  }

}
