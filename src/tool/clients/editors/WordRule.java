package tool.clients.editors;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import tool.xmodeler.XModeler;

public class WordRule {

  String word;
  Color  color;

  public WordRule(String word, Color color) {
    super();
    this.word = word;
    this.color = color;
  }

  public void writeXML(PrintStream out) {
    out.print("<WordRule word='" + XModeler.encodeXmlAttribute(word) + "' red='" + color.getRed() + "' green='" + color.getGreen() + "' blue='" + color.getBlue() + "'/>");
  }

  public StyleRange match(String s, int i, int prevChar) {
    if (canStartKeyword(prevChar, word.charAt(0)) && s.startsWith(word, i)) {
      int length = word.length();
      int nextchar = getNextChar(s, i, length);
      if (canStartKeyword(word.charAt(length - 1), nextchar)) {
        StyleRange style = new StyleRange();
        style.start = i;
        style.length = length;
        style.fontStyle = SWT.UNDERLINE_SINGLE;
        style.foreground = color;
        return style;
      } else return null;
    } else return null;
  }

  protected int getNextChar(String s, int i, int length) {
    int position = i + length;
    if (s.length() > position) return s.charAt(position);
    return -1;
  }

  public boolean canStartKeyword(int prevChar, int keyChar) {
    return !(Character.isLetterOrDigit(prevChar) && Character.isLetterOrDigit(keyChar));
  }

  public String toString() {
    return "WordRule(" + word + "," + color + ")";
  }

  public boolean starts(char c) {
    return word.charAt(0) == c;
  }

  public String getWord() {
    return word;
  }

  public Color getColor() {
    return color;
  }

}
