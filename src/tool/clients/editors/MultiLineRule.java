package tool.clients.editors;

import java.io.PrintStream;

import org.fxmisc.richtext.model.StyleSpan;

import javafx.scene.paint.Color;
import tool.xmodeler.XModeler;

public class MultiLineRule extends WordRule {

  String end;

  public MultiLineRule(String word, String end, Color color) {
    super(word, color);
    this.end = end;
  }

  public void writeXML(PrintStream out) {
    out.print("<MultiLineRule word='" + XModeler.encodeXmlAttribute(word) + "' end='" + XModeler.encodeXmlAttribute(end) + "' red='" + color.getRed() + "' green='" + color.getGreen() + "' blue='" + color.getBlue() + "'/>");
  }

//  public StyleRange match(String s, int i, int prevChar) {
  public StyleSpan<String> match(String s, int i, int prevChar) {
		String sEscaped = s.replace("\\\\", "xx"); // for "..." to ignore \" 
		sEscaped = sEscaped.replace("\\\"", "xx"); // for "..." to ignore \\ 
    if (canStartKeyword(prevChar, word.charAt(0)) && sEscaped.startsWith(word, i) && sEscaped.indexOf(end, i + 1) >= 0) {
      StyleSpan<String> style = new StyleSpan<String>("-fx-fill:rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")", (sEscaped.indexOf(end, i + 1) - i) + end.length());
//      StyleRange style = new StyleRange();
//      style.start = i;
//      style.length = (sEscaped.indexOf(end, i + 1) - i) + end.length();
//      style.fontStyle = SWT.UNDERLINE_SINGLE;
//      style.foreground = color;
      return style;
    } else return null;
  }

  public String toString() {
    return "Multiline(" + word + "," + end + "," + color + ")";
  }

  public String getEnd() {
    return end;
  }

}
