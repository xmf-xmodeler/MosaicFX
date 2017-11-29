package tool.clients.editors.pprint;

public class NewLine extends PPrint {

  public String toString(int indent) {
    String s = "\n";
    for (int i = 0; i < indent; i++)
      s = s + " ";
    return s;
  }

}
