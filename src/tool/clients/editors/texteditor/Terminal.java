package tool.clients.editors.texteditor;

public class Terminal {

  String end;
  String start;

  public Terminal(String end, String start) {
    super();
    this.end = end;
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public String getStart() {
    return start;
  }

  public boolean terminates(String s, int index) {
    return at(end, s, index - end.length());
  }

  public boolean starts(String s, int index) {
    return at(start, s, index);
  }

  private boolean at(String token, String s, int index) {
    boolean at = s.startsWith(token, index);
    boolean pre = isLegal(s, index - 1) ? !identChar(s, index - 1) : true;
    boolean post = isLegal(s, index + token.length()) ? !identChar(s, index + token.length()) : true;
    return at && pre && post;
  }

  private boolean identChar(String s, int i) {
    boolean isAlpha = Character.isAlphabetic(s.charAt(i));
    boolean isNumber = Character.isDigit(s.charAt(i));
    boolean isUnderscore = s.charAt(i) == '_';
    return isAlpha || isNumber || isUnderscore;
  }

  private boolean isLegal(String s, int i) {
    return i >= 0 && i < s.length();
  }
}
