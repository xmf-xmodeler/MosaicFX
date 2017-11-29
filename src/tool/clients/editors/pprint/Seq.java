package tool.clients.editors.pprint;

public class Seq extends PPrint {

  PPrint[] pprints;

  public Seq(PPrint... pprints) {
    super();
    this.pprints = pprints;
  }

  public String toString(int indent) {
    String s = "";
    for (PPrint p : pprints)
      s = s + p.toString(indent);
    return s;
  }

}
