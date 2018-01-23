package tool.clients.editors.texteditor;

import java.io.PrintStream;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tool.clients.editors.EditorClient;
import tool.clients.editors.FindUtil;
import tool.clients.editors.ITextEditor;
import tool.clients.editors.UndoRedoImpl;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class TextEditor implements  /*KeyListener, VerifyListener, VerifyKeyListener, MouseMoveListener, MouseListener, MouseWheelListener, LineBackgroundListener, ExtendedModifyListener, PaintObjectListener, SelectionListener, LineStyleListener, PaintListener, MouseTrackListener, */ITextEditor {

  private static final int   ZOOM              = 2;
  private static final int   MAX_FONT_SIZE     = 40;
  private static final int   MIN_FONT_SIZE     = 2;
  private static final int   LEFT_BUTTON       = 1;
  private static final int   MIDDLE_BUTTON     = 2;
  private static final int   RIGHT_BUTTON      = 3;
  private static final int   TRAY_PAD          = 5;
  private static final int   SYNTAX_DELAY      = 2000;
  private static final int   SYNTAX_INC        = 200;
//  private static final Color RED               = Display.getDefault().getSystemColor(SWT.COLOR_RED);
//  private static final Color BLACK             = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
//  private static final Color BRACKET_HIGHLIGHT = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
//  private static final Color VAR_DEC           = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);

//  public static void drawArrow(GC gc, int x1, int y1, int x2, int y2, double arrowLength, double arrowAngle, Color arrowColor) {
//    double theta = Math.atan2(y2 - y1, x2 - x1);
//    double offset = (arrowLength - 2) * Math.cos(arrowAngle);
//    Color cf = gc.getForeground();
//    Color cb = gc.getBackground();
//    gc.setForeground(arrowColor);
//    gc.setBackground(arrowColor);
//    gc.drawLine(x1, y1, (int) (x2 - offset * Math.cos(theta)), (int) (y2 - offset * Math.sin(theta)));
//    Path path = new Path(gc.getDevice());
//    path.moveTo((float) (x2 - arrowLength * Math.cos(theta - arrowAngle)), (float) (y2 - arrowLength * Math.sin(theta - arrowAngle)));
//    path.lineTo((float) x2, (float) y2);
//    path.lineTo((float) (x2 - arrowLength * Math.cos(theta + arrowAngle)), (float) (y2 - arrowLength * Math.sin(theta + arrowAngle)));
//    path.close();
//    gc.fillPath(path);
//    path.dispose();
//    gc.setForeground(cf);
//    gc.setBackground(cb);
//  }
//
//  static boolean isCloseBracket(char c) {
//    return c == ')' || c == ']' || c == '}';
//  }
//
//  static boolean isCntrl(MouseEvent e) {
//    return (e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND;
//  }
//
//  static boolean isCntrl(VerifyEvent e) {
//    return (e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND;
//  }
//
//  static boolean isCommand(MouseEvent event) {
//    return (event.stateMask & SWT.COMMAND) != 0;
//  }

  String                  id;
  String                  label;
//  StyledText              text;
//  FontData                fontData;
  DefaultToolTip          toolTip;
  Signature               signature      = new Signature();
  LineStyler              lineStyler     = new LineStyler(this);
  Vector<Integer>         highlights     = new Vector<Integer>();
  Vector<ErrorListener>   errorListeners = new Vector<ErrorListener>();
  Vector<FileError>       errors         = new Vector<FileError>();
  Vector<VarInfo>         varInfo        = new Vector<VarInfo>();
  Vector<Tooltip>         tooltips       = new Vector<Tooltip>();
  Vector<Terminal>        terminals      = new Vector<Terminal>();
  Vector<Action>          actions        = new Vector<Action>();
  Stack<Vector<Terminal>> tStack         = new Stack<Vector<Terminal>>();
  int[]                   terminal       = new int[] { -1, -1, -1, -1 };
  AST                     ast            = null;
  AST                     hover          = null;
  VarInfo                 mouseOverVar   = null;
  Tray                    tray           = new Tray();
  Timer                   syntaxTimer    = new Timer(SYNTAX_DELAY, SYNTAX_INC, () -> sendTextChanged(), () -> timerIncrement());
  CheckSyntax             checkSyntax    = new CheckSyntax(this);
  int[]                   offsets        = new int[] {};
  boolean                 dirty          = false;
  boolean                 checkingSyntax = true;
  boolean                 rendering      = true;
  char                    lastChar       = '\0';
  int                     flashBracket   = -1;
  boolean                 flashBracketOn = false;
  int                     errorPosition  = -1;
  String                  errorMessage   = "";

	public TextEditor(String id, String label, Object parent, boolean editable, boolean lineNumbers, String s) {
		this.id = id;
		// lineStyler.setLineNumbers(lineNumbers);
		this.label = label;
		createTray();
		// createText(parent, editable, s);
		// new UndoRedoImpl(text);
	}

  private void addErrorListener(ErrorListener listener) {
    errorListeners.add(listener);
  }

  public void addLineHighlight(int line) {
//    highlights.add(text.getOffsetAtLine(line - 1));
//    redraw();
  }

  public void addMultilineRule(String id, String start, String end, int red, int green, int blue) {
    if (getId().equals(id)) {
      lineStyler.addMultilineRule(id, start, end, red, green, blue);
    }
  }

  public void addWordRule(String id, String text, int red, int green, int blue) {
    if (getId().equals(id)) lineStyler.addWordRule(id, text, red, green, blue);
  }

  public void ast(String tooltip, int charStart, int charEnd) {
//    ast.add(new AST(text, tooltip, charStart, charEnd));
  }

  private void cancelToolTip() {
    if (toolTip != null) toolTip.deactivate();
    toolTip = null;
  }

//  private void checkBracket(ExtendedModifyEvent event) {
//
//    // Check to see if the inserted text is a close bracket. If so then
//    // try to flash the corresponding open bracket...
//
//    if (event.length == 1) {
//      String s = text.getText();
//      char closing = s.charAt(event.start);
//      if (isCloseBracket(closing)) {
//        char opening = getOpening(closing);
//        int i = event.start - 1;
//        int nesting = 0;
//        boolean found = false;
//        while (i >= 0 && !found) {
//          char c = s.charAt(i);
//          if (c == closing) {
//            nesting++;
//            i--;
//          } else if (c == opening) {
//            if (nesting == 0)
//              found = true;
//            else {
//              nesting--;
//              i--;
//            }
//          } else i--;
//        }
//        if (found) {
//          flashBracket = i;
//        } else flashBracket = -1;
//      } else flashBracket = -1;
//    } else flashBracket = -1;
//  }
//
//  private void checkNewline(VerifyEvent e) {
//    if (e.text.equals("\n")) {
//      int indent = getCurrentIndent();
//      char[] chars = new char[indent + 1];
//      chars[0] = '\n';
//      for (int i = 0; i < indent; i++)
//        chars[i + 1] = ' ';
//      e.text = new String(chars);
//    }
//  }
//
//  private void checkTerminals(ExtendedModifyEvent event) {
//    tStack.clear();
//    terminal[0] = -1;
//    terminal[1] = -1;
//    terminal[2] = -1;
//    terminal[3] = -1;
//    int index = event.start + 1;
//    String s = text.getText();
//    Vector<Terminal> terminates = isTerminator(s, index);
//    if (terminates != null) {
//      String end = terminates.elementAt(0).getEnd();
//      terminal[2] = index - end.length();
//      terminal[3] = index;
//      tStack.push(terminates);
//      index--;
//      while (index >= 0 && !tStack.isEmpty()) {
//        terminates = isTerminator(s, index);
//        if (terminates != null) tStack.push(terminates);
//        int terminalLength = starts(s, index);
//        if (terminalLength >= 0) {
//          terminates = tStack.pop();
//          if (tStack.isEmpty()) {
//            terminal[0] = index;
//            terminal[1] = index + terminalLength;
//          } else index--;
//        } else index--;
//      }
//    }
//  }

  public void clearErrors() {
    errorPosition = -1;
    errorMessage = "";
    for (ErrorListener l : errorListeners) {
      l.clear();
    }
    errors.clear();
//    redraw();
//    ast = new AST(text, "", 0, text.getText().length());
  }

  public void clearHighlights() {
    highlights.clear();
  }

//  private void click(MouseEvent event) {
//    int x = event.x;
//    int y = event.y;
//    Tool tool = selectTool(x, y);
//    if (tool != null) {
//      tool.click(this);
//      redraw();
//    } else signature.click(this, event);
//  }

//  private void createText(CTabFolder parent, boolean editable, String s) {
//    text = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
//    Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
//    FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
//    this.fontData = fontData[0];
//    XModeler.getXModeler().getDisplay().loadFont("dejavu/DejaVuSansMono.ttf");
//    this.fontData.setName("DejaVu Sans Mono");
//    text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//    GC gc = new GC(text);
//    gc.setTextAntialias(SWT.ON);
//    text.setText(s);
//    text.setBackground(bg);
//    text.addLineStyleListener(this);
//    text.setEditable(editable);
//    text.addMouseMoveListener(this);
//    text.addMouseTrackListener(this);
//    text.addPaintListener(this);
//    text.addExtendedModifyListener(this);
//    text.addVerifyKeyListener(this);
//    text.addMouseListener(this);
//    text.addMouseWheelListener(this);
//    text.addLineBackgroundListener(this);
//    text.addVerifyListener(this);
//    text.addPaintObjectListener(this);
//    text.addSelectionListener(this);
//  }

  private void createTray() {
    ErrorTool errors = new ErrorTool();
    ShowSignature showSignature = new ShowSignature(this);
    addErrorListener(errors);
    tray.addTool(checkSyntax);
    tray.addTool(errors);
    tray.addTool(syntaxTimer);
    tray.addTool(showSignature);
  }

//  private boolean errorToolTip(int x, int y) {
//    if (mouseNearParseError(x, y)) {
//      org.eclipse.swt.graphics.Point p = text.getLocationAtOffset(errorPosition);
//      setToolTip(p.x, p.y, errorMessage);
//      return true;
//    } else {
//      for (FileError e : errors) {
//        org.eclipse.swt.graphics.Point p = text.getLocationAtOffset(e.getStart());
//        int x2 = p.x;
//        int y2 = p.y;
//        int dx = x - x2;
//        int dy = y - y2;
//        double distance = Math.sqrt((dx * dx) + (dy * dy));
//        if (distance < 20) {
//          p = text.getLocationAtOffset(e.getStart());
//          setToolTip(p.x, p.y, e.getMessage());
//          return true;
//        }
//      }
//      return false;
//    }
//  }

//  private boolean generalToolTip(int x, int y) {
//    for (Tooltip t : tooltips) {
//      Point p = text.getLocationAtOffset(t.getCharStart());
//      int x2 = p.x;
//      int y2 = p.y;
//      int dx = x - x2;
//      int dy = y - y2;
//      double distance = Math.sqrt((dx * dx) + (dy * dy));
//      if (distance < 20) {
//        setToolTip(x, y, t.getTooltip());
//        return true;
//      }
//    }
//    return false;
//  }

//  private int getCurrentIndent() {
////    String s = text.getText();
////    int start = text.getOffsetAtLine(text.getLineAtOffset(text.getCaretOffset()));
//    int indent = 0;
//    for (int i = start; i < s.length() && s.charAt(i) == ' '; i++)
//      indent++;
//    return indent;
//  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

//  public int getLineCount() {
//    return text.getLineCount();
//  }

  private char getOpening(char closing) {
    if (closing == ')')
      return '(';
    else if (closing == '}')
      return '{';
    else if (closing == ']')
      return '[';
    else throw new Error("unknown closing bracket " + closing);
  }

//  public String getString() {
//    return text.getText();
//  }
//
//  public StyledText getText() {
//    return text;
//  }

//  private void hover(int x, int y) {
//    try {
//      int index = text.getOffsetAtLocation(new Point(x, y));
//      hover = ast.find(index);
//    } catch (Exception e) {
//      hover = null;
//    }
//  }

  public void inflate(Node textEditor) {
    NodeList children = textEditor.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateElement(children.item(i));
  }

  private void inflateElement(Node item) {
    if (item.getNodeName().equals("WordRule"))
      inflateWordRule(item);
    else if (item.getNodeName().equals("MultiLineRule")) inflateMultiLineRule(item);
  }

  private void inflateMultiLineRule(Node item) {
    String word = XModeler.attributeValue(item, "word");
    String end = XModeler.attributeValue(item, "end");
    int red = Integer.parseInt(XModeler.attributeValue(item, "red"));
    int green = Integer.parseInt(XModeler.attributeValue(item, "green"));
    int blue = Integer.parseInt(XModeler.attributeValue(item, "blue"));
    addMultilineRule(getId(), word, end, red, green, blue);
  }

  private void inflateWordRule(Node item) {
    String word = XModeler.attributeValue(item, "word");
    int red = Integer.parseInt(XModeler.attributeValue(item, "red"));
    int green = Integer.parseInt(XModeler.attributeValue(item, "green"));
    int blue = Integer.parseInt(XModeler.attributeValue(item, "blue"));
    addWordRule(getId(), word, red, green, blue);
  }

  private boolean isAlpha(char c) {
    return 'a' <= c && c <= 'z';
  }

  public boolean isCheckingSyntax() {
    return checkingSyntax;
  }

  public boolean isDirty() {
    return dirty;
  }

//  public boolean isLeft(MouseEvent event) {
//    return event.button == 1;
//  }

  public boolean isRendering() {
    return rendering;
  }

//  private boolean isRightClick(MouseEvent event) {
//    return event.button == RIGHT_BUTTON || isCntrl(event);
//  }

  private Vector<Terminal> isTerminator(String s, int index) {
    Vector<Terminal> terminates = null;
    for (Terminal t : terminals) {
      if (t.terminates(s, index)) {
        if (terminates == null) terminates = new Stack<Terminal>();
        terminates.add(t);
      }
    }
    return terminates;
  }

//  public void keyPressed(KeyEvent arg0) {
//    ast = new AST(text, "", 0, text.getText().length());
//  }
//
//  public void keyReleased(KeyEvent arg0) {
//
//  }
//
//  public void lineGetBackground(LineBackgroundEvent event) {
//    if (highlights.contains(event.lineOffset)) event.lineBackground = EditorClient.LINE_HIGHLIGHT;
//  }
//
//  public void lineGetStyle(LineStyleEvent event) {
//    lineStyler.lineGetStyle(event);
//  }
//
//  public void modifyText(ExtendedModifyEvent event) {
//    lineStyler.clearCache();
//    varInfo.clear();
//    tooltips.clear();
//    signature.clear();
//    actions.clear();
//    clearErrors();
//    checkBracket(event);
//    checkTerminals(event);
//    if (!dirty) {
//      Message message = EditorClient.theClient().getHandler().newMessage("textDirty", 2);
//      message.args[0] = new Value(getId());
//      message.args[1] = new Value(true);
//      EditorClient.theClient().getHandler().raiseEvent(message);
//      dirty = true;
//    }
//    if (checkingSyntax) syntaxTimer.ping();
//  }
//
//  public void mouseDoubleClick(MouseEvent event) {
//
//  }
//
//  public void mouseDown(MouseEvent event) {
//    cancelToolTip();
//    if (isRightClick(event) || isCommand(event)) {
//      rightClick(event);
//    } else click(event);
//  }
//
//  public void mouseEnter(MouseEvent arg0) {
//  }
//
//  public void mouseExit(MouseEvent arg0) {
//
//  }
//
//  public void mouseHover(MouseEvent event) {
//
//  }
//
//  public void mouseMove(MouseEvent event) {
//    int x = event.x;
//    int y = event.y;
//    toolTip(x, y);
//    setMouseOverVar(x, y);
//    hover(x, y);
//    mouseOverVar = selectVarInfo(x, y);
//    if (mouseOverVar != null || hover != null || signature.mouseOver(x, y)) redraw();
//  }

//  private boolean mouseNearParseError(int x1, int y1) {
//    if (errorPosition >= 0) {
//      org.eclipse.swt.graphics.Point p = text.getLocationAtOffset(errorPosition);
//      int x2 = p.x;
//      int y2 = p.y;
//      int dx = x1 - x2;
//      int dy = y1 - y2;
//      double distance = Math.sqrt((dx * dx) + (dy * dy));
//      return distance < 20;
//    } else return false;
//  }

//  public void mouseScrolled(MouseEvent e) {
//    if (isCntrl(e) && (e.count > 0)) {
//      fontData.setHeight(Math.min(fontData.getHeight() + ZOOM, MAX_FONT_SIZE));
//      text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//    }
//    if (isCntrl(e) && (e.count < 0)) {
//      fontData.setHeight(Math.max(MIN_FONT_SIZE, fontData.getHeight() - ZOOM));
//      text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//    }
//  }

//  public void mouseUp(MouseEvent event) {
//
//  }

//  private void paintBracket(GC gc) {
//    if (flashBracket >= 0) {
//      if (flashBracketOn) {
//        Point p = text.getLocationAtOffset(flashBracket);
//        Color c = gc.getBackground();
//        int width = gc.getFontMetrics().getAverageCharWidth();
//        int height = gc.getFontMetrics().getHeight();
//        gc.setBackground(BRACKET_HIGHLIGHT);
//        gc.drawRectangle(p.x, p.y, width, height);
//        flashBracketOn = false;
//        gc.setBackground(c);
//      } else flashBracketOn = true;
//    }
//  }

//  public void paintControl(PaintEvent event) {
//    paintSyntaxError(event.gc);
//    paintErrors(event.gc);
//    paintVar(event.gc);
//    paintTray(event.gc);
//    paintBracket(event.gc);
//    paintHover(event.gc);
//    paintTerminal(event.gc);
//    paintSignature(event.gc);
//  }

//  private void paintErrors(GC gc) {
//    try {
//      int bottomIndex = JFaceTextUtil.getPartialBottomIndex(text);
//      int topIndex = JFaceTextUtil.getPartialTopIndex(text);
//      int height = text.getFont().getFontData()[0].getHeight();
//      Color c = gc.getForeground();
//      gc.setForeground(RED);
//      for (FileError e : errors) {
//        int start = e.getStart();
//        int end = e.getEnd();
//        if (validOffset(start) && validOffset(end)) {
//          int line = text.getLineAtOffset(start);
//          if (line >= topIndex && line <= bottomIndex) {
//            org.eclipse.swt.graphics.Point pStart = text.getLocationAtOffset(start);
//            org.eclipse.swt.graphics.Point pEnd = text.getLocationAtOffset(end);
//            gc.drawLine(pStart.x, pStart.y + height, pEnd.x, pEnd.y + height);
//          }
//        }
//      }
//      gc.setForeground(c);
//    } catch (Exception e) {
//    }
//  }
//
//  private void paintHover(GC gc) {
//    if (hover != null) {
//      hover.paint(gc);
//    }
//  }
//
//  public void paintObject(PaintObjectEvent event) {
//  }
//
//  private void paintSignature(GC gc) {
//    try {
//      int topIndex = JFaceTextUtil.getPartialTopIndex(text);
//      Point p = text.getLocationAtOffset(topIndex);
//      Rectangle r = text.getClientArea();
//      if (signature.isVisible()) {
//        signature.paint(r.width, gc);
//      }
//    } catch (Exception e) {
//    }
//  }
//
//  private void paintSyntaxError(GC gc) {
//    if (errorPosition >= 0 && errorPosition < text.getText().length()) {
//      org.eclipse.swt.graphics.Point p = text.getLocationAtOffset(errorPosition);
//      int height = text.getFont().getFontData()[0].getHeight();
//      Color c = gc.getForeground();
//      gc.setForeground(RED);
//      gc.drawLine(p.x - 10, p.y + height, p.x + 100, p.y + height);
//      gc.setForeground(c);
//    }
//  }
//
//  private void paintTerminal(GC gc) {
//    if (terminal[0] >= 0) {
//      paintTerminal(gc, terminal[0], terminal[1]);
//      paintTerminal(gc, terminal[2], terminal[3]);
//    }
//  }
//
//  private void paintTerminal(GC gc, int start, int end) {
//    Point pStart = text.getLocationAtOffset(start);
//    int height = gc.getFontMetrics().getHeight();
//    Point p = gc.textExtent(text.getText().substring(start, end));
//    int alpha = gc.getAlpha();
//    gc.setAlpha(50);
//    gc.fillRectangle(pStart.x, pStart.y, p.x, height);
//    gc.setAlpha(alpha);
//  }
//
//  private void paintTray(GC gc) {
//    ScrollBar bar = text.getVerticalBar();
//    Rectangle r = text.getClientArea();
//    int width = r.width;
//    int height = r.height;
//    if (bar != null) width -= bar.getSize().x;
//    height -= text.getBorderWidth();
//    height -= text.getParent().getBorderWidth();
//    height -= TRAY_PAD;
//    tray.paint(gc, width, height);
//  }
//
//  private void paintVar(GC gc) {
//    if (mouseOverVar != null) {
//      int varOffset = mouseOverVar.getVarStart();
//      int decOffset = mouseOverVar.getDecStart();
//      if (validOffset(varOffset) && validOffset(decOffset)) {
//        Point pStart = text.getLocationAtOffset(varOffset);
//        Point pEnd = text.getLocationAtOffset(decOffset);
//        int height = text.getFont().getFontData()[0].getHeight() / 2;
//        Color c = gc.getForeground();
//        drawArrow(gc, pStart.x, pStart.y + height, pEnd.x, pEnd.y + height, 7, 125, VAR_DEC);
//        gc.setForeground(c);
//      }
//    }
//  }
//
//  void redraw() {
//    if (rendering) text.redraw();
//  }

//  public void rightClick(MouseEvent event) {
//    int x = event.x;
//    int y = event.y;
//    Tool tool = selectTool(x, y);
//    if (tool == null) {
//      Action action = getAction(x, y);
//      if (action == null) {
//        if (mouseOverVar != null) {
//          text.setSelection(mouseOverVar.getDecStart());
//        } else MenuClient.popup(id, x, y);
//      } else action.perform(x, y);
//    } else tool.rightClick(x, y);
//  }
//
//  private Action getAction(int x, int y) {
//    try {
//      int offset = text.getOffsetAtLocation(new Point(x, y));
//      for (Action action : actions) {
//        if (action.containsOffset(offset)) return action;
//      }
//    } catch (Exception e) {
//    }
//    return null;
//  }

  public void save() {
    Message message = EditorClient.theClient().getHandler().newMessage("saveText", 2);
    message.args[0] = new Value(getId());
    message.args[1] = new Value(getString());
    EditorClient.theClient().getHandler().raiseEvent(message);
  }

//  public void scrollToError() {
//    if (errorPosition >= 0 && errorPosition <= text.getText().length()) {
//      int selection = Math.max(0, errorPosition - 100);
//      text.setSelection(selection);
//      selection = Math.min(text.getText().length(), errorPosition + 100);
//      text.setSelection(selection);
//      text.setCaretOffset(errorPosition);
//    } else {
//      if (errors.size() > 0) {
//        int selection = Math.max(0, errors.get(0).getStart() - 100);
//        text.setSelection(selection);
//        selection = Math.min(text.getText().length(), errors.get(0).getStart() + 100);
//        text.setSelection(selection);
//        text.setCaretOffset(errors.get(0).getStart());
//      }
//    }
//  }

//  private Tool selectTool(int x, int y) {
//    ScrollBar bar = text.getVerticalBar();
//    Rectangle r = text.getClientArea();
//    int width = r.width;
//    int height = r.height;
//    if (bar != null) width -= bar.getSize().x;
//    height -= text.getBorderWidth();
//    height -= text.getParent().getBorderWidth();
//    height -= TRAY_PAD;
//    return tray.selectTool(x, y, width, height);
//  }

//  private VarInfo selectVarInfo(int x, int y) {
//    try {
//      int location = text.getOffsetAtLocation(new Point(x, y));
//      for (VarInfo info : varInfo) {
//        int distance = Math.abs(info.getVarStart() - location);
//        if (distance < 3) return info;
//      }
//      return null;
//    } catch (IllegalArgumentException e) {
//      return null;
//    }
//
//  }

  private void sendTextChanged() {
//    varInfo.clear();
//    XModeler.getXModeler().getDisplay().syncExec(new Runnable() {
//      public void run() {
//        try {
//          ast = new AST(text, "", 0, text.getText().length());
//          Message message = EditorClient.theClient().getHandler().newMessage("textChanged", 2);
//          message.args[0] = new Value(getId());
//          message.args[1] = new Value(text.getText());
//          EditorClient.theClient().getHandler().raiseEvent(message);
//        } catch (Throwable t) {
//          t.printStackTrace();
//        }
//      }
//    });
  }

  public void setCheckingSyntax(boolean checkingSyntax) {
    this.checkingSyntax = checkingSyntax;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  private void setMouseOverVar(int x, int y) {
//    boolean isOverVar = mouseOverVar != null;
//    mouseOverVar = selectVarInfo(x, y);
//    if (mouseOverVar != null || isOverVar) redraw();
  }

  public void setRendering(boolean rendering) {
//    this.rendering = rendering;
//    redraw();
  }

  public void setSelection(int index) {
//    if (text.getText().length() > index) text.setSelection(index);
  }

  public void setSignature(Value[] entries) {
    for (Value value : entries) {
      signature.add(toSigEntry(value));
    }
  }

  public void setString(String text) {
    setText(text);
  }

  public void setText(String s) {
//    // We do not want to fire a dirty event at this point because
//    // this should only be called to initialize the content or to
//    // refresh the content...
//    dirty = true;
//    text.setText(s);
//    dirty = false;
//    lineStyler.clearCache();
//    varInfo.clear();
//    redraw();
  }

  public void setTooltip(String tooltip, int charStart, int charEnd) {
    tooltips.add(new Tooltip(tooltip, charStart, charEnd));
  }

  private void setToolTip(int x, int y, String message) {
//    if (toolTip == null) {
//      toolTip = new DefaultToolTip(text, ToolTip.NO_RECREATE, false);
//    }
//    if (toolTip != null) {
//      toolTip.setHideDelay(2000);
//      toolTip.setText(message);
//    }
  }

  public void showLine(int line) {
//    text.setCaretOffset(text.getOffsetAtLine(line));
//    redraw();
  }

  private int starts(String s, int index) {
    if (tStack.isEmpty())
      return -1;
    else {
      for (Terminal t : tStack.peek()) {
        if (t.starts(s, index)) return t.getStart().length();
      }
      Vector<Terminal> ts = tStack.pop();
      int starts = starts(s, index);
      if (starts >= 0)
        return starts;
      else {
        tStack.push(ts);
        return starts;
      }
    }
  }

  public void syntaxError(int pos, String error) {
//    this.errorPosition = Math.min(pos - 10, text.getText().length() - 1);
//    this.errorMessage = error;
//    for (ErrorListener listener : errorListeners) {
//      listener.error(this);
//    }
//    errors.clear();
//    redraw();
  }

  public void terminates(String end, String start) {
    terminals.add(new Terminal(end, start));
  }

  private void timerIncrement() {
    XModeler.getXModeler().getDisplay().syncExec(new Runnable() {
      public void run() {
        try {
//          redraw();
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    });
  }

//  private void toolTip(int x, int y) {
////    if (!(errorToolTip(x, y) || trayToolTip(x, y) || generalToolTip(x, y))) cancelToolTip();
//  }

  private SignatureEntry toSigEntry(Value value) {
    Value[] entry = value.values;
    int charStart = entry[0].intValue;
    String shortLabel = entry[1].strValue();
    String longLabel = entry[2].strValue();
    Value[] children = entry[3].values;
    SignatureEntry sigEntry = new SignatureEntry(charStart, shortLabel, longLabel);
    for (Value v : children) {
      sigEntry.add(toSigEntry(v));
    }
    return sigEntry;
  }

//  private boolean trayToolTip(int x, int y) {
//    Tool tool = selectTool(x, y);
//    if (tool != null) {
//      setToolTip(x, y, tool.toolTip());
//      return true;
//    } else return false;
//  }

  public void unboundVar(String name, int charStart, int charEnd) {
    errors.add(new FileError(charStart, charEnd, name + " is unbound"));
    for (ErrorListener listener : errorListeners) {
      listener.error(this);
    }
  }

//  private boolean validOffset(int c) {
//    return c >= 0 && c <= text.getText().length();
//  }

  public void varDec(int charStart, int charEnd, int decStart, int decEnd) {
    varInfo.add(new VarInfo(charStart, charEnd, decStart, decEnd));
  }

//  public void verifyKey(VerifyEvent e) {
//    if (isCntrl(e) && (e.keyCode == '=')) {
//      fontData.setHeight(Math.min(fontData.getHeight() + ZOOM, MAX_FONT_SIZE));
//      text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == '-')) {
//      fontData.setHeight(Math.max(MIN_FONT_SIZE, fontData.getHeight() - ZOOM));
//      text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == 'f')) {
//      FindUtil.show(XModeler.getXModeler(), text);
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == 's')) {
//      save();
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == 'l')) {
//      lineStyler.toggleLineNumbers();
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == 'v')) {
//      Display display = XModeler.getXModeler().getDisplay();
//      Clipboard clipboard = new Clipboard(display);
//      ImageData imageData = (ImageData) clipboard.getContents(ImageTransfer.getInstance());
//    }
//    if (e.doit) lastChar = e.character;
//  }

//  public void verifyText(VerifyEvent e) {
//    checkNewline(e);
//  }
//
//  public void widgetDefaultSelected(SelectionEvent event) {
//  }
//
//  public void widgetSelected(SelectionEvent arg0) {
//
//  }

  public void writeXML(PrintStream out, boolean isSelected, String label, String toolTip) {
    out.print("<TextEditor id='" + getId() + "' selected='" + isSelected + "'");
//    out.print(" text='" + XModeler.encodeXmlAttribute(text.getText()) + "'");
//    out.print(" lineNumbers='" + lineStyler.getLineNumbers() + "'");
    out.print(" label='" + label + "'");
    out.print(" toolTip='" + toolTip + "'");
//    out.print(" editable='" + text.getEditable() + "'>");
    out.print("</TextEditor>");
  }

  public boolean isShowingSignature() {
    return signature.isVisible();
  }

  public void setShowingSignature(boolean b) {
    signature.setIsVisible(b);
  }

  public void action(String name, Value[] args, int charStart, int charEnd) {
    Object[] values = new Object[args.length];
    for (int i = 0; i < values.length; i++) {
      switch (args[i].type) {
        case Value.INT:
          values[i] = args[i].intValue;
          break;
        case Value.BOOL:
          values[i] = args[i].boolValue;
          break;
        case Value.STRING:
          values[i] = args[i].strValue();
          break;
        case Value.FLOAT:
          values[i] = args[i].floatValue;
          break;
        case Value.BYTE:
          values[i] = args[i].intValue;
          break;
        case Value.NEG:
          values[i] = -args[i].intValue;
          break;
        default:
          throw new Error("unknown action arg type: " + args[i]);
      }
    }
    actions.add(new Action(name, values, charStart, charEnd));
  }

@Override
public String getString() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public javafx.scene.Node getText() {
	// TODO Auto-generated method stub
	return null;
}
}