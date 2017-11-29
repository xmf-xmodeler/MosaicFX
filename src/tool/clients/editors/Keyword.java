package tool.clients.editors;

import tool.clients.editors.pprint.PPrint;

public class Keyword {

  String description;
  PPrint pprint;

  public Keyword(String description, PPrint pprint) {
    super();
    this.description = description;
    this.pprint = pprint;
  }

  public String toString(int indent) {
    return pprint.toString(indent);
  }

}
