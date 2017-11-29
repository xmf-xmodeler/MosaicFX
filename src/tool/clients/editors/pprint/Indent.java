package tool.clients.editors.pprint;

public class Indent extends PPrint {

  PPrint pprint;

  public Indent(PPrint pprint) {
    super();
    this.pprint = pprint;
  }

  public String toString(int indent) {
    return pprint.toString(indent + 2);
  }
}
