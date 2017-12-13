package tool.clients.editors.texteditor;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
//import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.widgets.Display;

import javafx.scene.paint.Color;
import tool.clients.editors.EditorClient;
import tool.clients.editors.MultiLineRule;
import tool.clients.editors.WordRule;
import tool.xmodeler.XModeler;

public class LineStyler {

  private static final Color       BLACK          = Color.rgb(0,0,0);

  TextEditor                       editor;
  boolean                          lineNumbers    = true;
  Vector<WordRule>                 wordRules      = new Vector<WordRule>();
  Vector<MultiLineRule>            multiLineRules = new Vector<MultiLineRule>();
  Hashtable<Integer, StyleRange[]> cache          = new Hashtable<Integer, StyleRange[]>();

  public LineStyler(TextEditor editor) {
    super();
    this.editor = editor;
  }

  public void addMultilineRule(String id, String start, String end, int red, int green, int blue) {
    multiLineRules.add(new MultiLineRule(start, end, Color.rgb(red, green, blue)));
  }

  public void addWordRule(String id, String text, int red, int green, int blue) {
    wordRules.add(new WordRule(text, Color.rgb(red, green, blue)));
  }

  public void clearCache(int line, boolean isNewline) {
    if (cache.containsKey(line)) cache.remove(line);
    if (isNewline && editor.getText().getLineCount() > line) {
      clearCache(line + 1, isNewline);
    }
  }

  private Bullet getBullet(int line) {
    if (lineNumbers) {
      int maxLine = editor.getLineCount();
      int lineCountWidth = Math.max(String.valueOf(maxLine).length(), 3);
      StyleRange style = new StyleRange(0, editor.getText().getTextLimit(), EditorClient.GREY, EditorClient.WHITE);
      style.metrics = new GlyphMetrics(0, 0, lineCountWidth * 8 + 5);
      return new Bullet(ST.BULLET_NUMBER, style);
    } else return null;
  }

  private java.util.List<StyleRange> lineCreateStyle(LineStyleEvent event, int start, int end) {
    java.util.List<StyleRange> styles = new java.util.ArrayList<StyleRange>();
    String s = editor.getText().getText();
    for (int i = start; i < end; i++) {
      int prevChar = (event.lineOffset + i == 0) ? '\n' : s.charAt(event.lineOffset + i - 1);
      for (WordRule rule : wordRules) {
        StyleRange style = rule.match(s, event.lineOffset + i, prevChar);
        if (style != null) {
          styles.add(style);
          i += style.length;
          break;
        }
      }
    }
    return styles;
  }

  public void oldlineGetStyle(LineStyleEvent event) {
    // MultiLineStyle mls = getMultiLineStyle(event.lineOffset);
    int line = editor.getText().getLineAtOffset(event.lineOffset);
    event.bullet = getBullet(line);
    event.bulletIndex = line;
    if (cache.containsKey(line)) {
      event.styles = cache.get(line);
    } else {
      java.util.List<StyleRange> list = lineCreateStyle(event, 0, event.lineText.length());
      StyleRange[] ranges = list.toArray(new StyleRange[0]);
      cache.put(line, ranges);
      event.styles = ranges;
    }
  }

  public void lineGetStyle(LineStyleEvent event) {
    int line = editor.getText().getLineAtOffset(event.lineOffset);
    int originalLine = line;
    String s = editor.getText().getText();
    event.bullet = getBullet(line);
    event.bulletIndex = line;
    if (cache.containsKey(line)) {
      event.styles = cache.get(line);
    } else {
      java.util.List<StyleRange> list = new java.util.ArrayList<StyleRange>();
      boolean done = false;
      int index = event.lineOffset;
      while (!done) {
        for (MultiLineRule rule : multiLineRules) {
          if (s.startsWith(rule.getWord(), index)) {
            int start = index++;
            boolean multiLineDone = false;
            while (!multiLineDone) {
              if (s.startsWith(rule.getEnd(), index) || index >= s.length()) {
                multiLineDone = true;
                StyleRange style = new StyleRange();
                style.start = start;
                style.length = (index + rule.getEnd().length()) - start;
                style.fontStyle = SWT.UNDERLINE_SINGLE;
                style.foreground = rule.getColor();
                list.add(style);
                index += rule.getEnd().length();
              } else if (s.charAt(index) == '\n') {
                StyleRange style = new StyleRange();
                style.start = start;
                style.length = (index + rule.getEnd().length()) - start;
                style.fontStyle = SWT.UNDERLINE_SINGLE;
                style.foreground = rule.getColor();
                list.add(style);
                cache.put(line, list.toArray(new StyleRange[0]));
                list = new java.util.ArrayList<StyleRange>();
                line++;
                index++;
              } else index++;
            }
            break;
          }
        }
        int prevChar = (index == 0) || index > s.length() ? ' ' : s.charAt(index - 1);
        for (WordRule rule : wordRules) {
          StyleRange style = rule.match(s, index, prevChar);
          if (style != null) {
            list.add(style);
            index += style.length - 1;
            break;
          }
        }
        if (index >= s.length() || s.charAt(index) == '\n')
          done = true;
        else index++;
      }
      cache.put(line, list.toArray(new StyleRange[0]));
      event.styles = cache.get(line);
    }
  }

  public void clearCache() {
    cache.clear();
  }

  public void setLineNumbers(boolean lineNumbers) {
    this.lineNumbers = lineNumbers;
  }

  public void toggleLineNumbers() {
    lineNumbers = !lineNumbers;
  }

  public boolean getLineNumbers() {
    return lineNumbers;
  }

}
