package tool.clients.editors;

import java.io.PrintStream;

import org.eclipse.swt.widgets.Control;
import org.w3c.dom.Node;

import xos.Value;

public interface ITextEditor {

  void addLineHighlight(int line);

  void addMultilineRule(String id, String start, String end, int red, int green, int blue);

  void addWordRule(String id, String text, int red, int green, int blue);

  void clearHighlights();

  String getString();

  void inflate(Node newTextEditor);

  Control getText();

  void varDec(int charStart, int charEnd, int decStart, int decEnd);

  void setRendering(boolean state);

  void unboundVar(String name, int charStart, int charEnd);

  void clearErrors();

  void setDirty(boolean b);

  String getLabel();

  void syntaxError(int pos, String error);

  void setString(String strValue);

  void showLine(int line);

  void writeXML(PrintStream out, boolean b, String text, String toolTipText);

  void setTooltip(String tooltip, int charStart, int charEnd);

  void ast(String tooltip, int charStart, int charEnd);

  void terminates(String end, String start);

  void setSignature(Value[] entries);

  void action(String name, Value[] args, int charStart, int charEnd);

}
