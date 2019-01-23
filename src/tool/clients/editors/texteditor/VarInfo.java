package tool.clients.editors.texteditor;

public class VarInfo {

  int varStart;
  int varEnd;
  int decStart;
  int decEnd;

  public VarInfo(int varStart, int varEnd, int decStart, int decEnd) {
    super();
    this.varStart = varStart;
    this.varEnd = varEnd;
    this.decStart = decStart;
    this.decEnd = decEnd;
  }

  public int getVarStart() {
    return varStart;
  }

  public int getVarEnd() {
    return varEnd;
  }

  public int getDecStart() {
    return decStart;
  }

  public int getDecEnd() {
    return decEnd;
  }

  public String toString() {
    return "VarInfo(" + varStart + "," + varEnd + "," + decStart + "," + decEnd + ")";
  }

}
