package tool.clients.editors.texteditor;

public class Tooltip {

  String tooltip;
  int    charStart;
  int    charEnd;

  public Tooltip(String tooltip, int charStart, int charEnd) {
    super();
    this.tooltip = tooltip;
    this.charStart = charStart;
    this.charEnd = charEnd;
  }

  public String getTooltip() {
    return tooltip;
  }

  public int getCharStart() {
    return charStart;
  }

  public int getCharEnd() {
    return charEnd;
  }

  public boolean near(int charOffset) {
    return charStart <= charOffset && charOffset <= charEnd;
  }
}
