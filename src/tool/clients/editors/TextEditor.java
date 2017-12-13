package tool.clients.editors;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.Bullet;
//import org.eclipse.swt.custom.CTabFolder;
//import org.eclipse.swt.custom.ExtendedModifyEvent;
//import org.eclipse.swt.custom.ExtendedModifyListener;
//import org.eclipse.swt.custom.LineBackgroundEvent;
//import org.eclipse.swt.custom.LineBackgroundListener;
//import org.eclipse.swt.custom.PaintObjectEvent;
//import org.eclipse.swt.custom.PaintObjectListener;
//import org.eclipse.swt.custom.ST;
//import org.eclipse.swt.custom.StyleRange;
//import org.eclipse.swt.custom.StyledText;
//import org.eclipse.swt.custom.VerifyKeyListener;
//import org.eclipse.swt.dnd.Clipboard;
//import org.eclipse.swt.dnd.ImageTransfer;
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseListener;
//import org.eclipse.swt.events.MouseWheelListener;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.events.VerifyEvent;
//import org.eclipse.swt.events.VerifyListener;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.graphics.FontData;
//import org.eclipse.swt.graphics.GC;
//import org.eclipse.swt.graphics.GlyphMetrics;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.swt.graphics.ImageLoader;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import tool.clients.editors.pprint.Indent;
import tool.clients.editors.pprint.Literal;
import tool.clients.editors.pprint.NewLine;
import tool.clients.editors.pprint.PPrint;
import tool.clients.editors.pprint.Seq;
import tool.clients.editors.pprint.Space;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class TextEditor implements ITextEditor{

  InlineCssTextArea							textArea;
  VirtualizedScrollPane<InlineCssTextArea>					virtualizedScrollPane;					
  
  String                             id;
  String                             label;
  int 								 fontsize = 10;
//  StyledText                         text;
//  FontData                           fontData;                                                // = new FontData("Courier", 12, SWT.NO);
  Hashtable<String, PPrint>          atTable       = new Hashtable<String, PPrint>();
  Hashtable<String, Vector<Keyword>> keyTable      = new Hashtable<String, Vector<Keyword>>();
  Vector<WordRule>                   wordRules     = new Vector<WordRule>();
  Vector<Integer>                    highlights    = new Vector<Integer>();
//  Image[]                            images        = new Image[] {};
//  Image                              selectedImage = null;
  int[]                              offsets       = new int[] {};
  boolean                            lineNumbers   = true;
  boolean                            dirty         = false;
  boolean                            autoComplete  = true;//false;
//  char                               lastChar      = '\0';
  boolean                               lastCharIsLetter      = false;
  int                                syntaxDirty   = 0;                                       // counter for delaying syntax highlighting update

  private boolean                    syntaxBusy;

  public TextEditor(String id, String label, TabPane parent, boolean editable, boolean lineNumbers, String s) {
    this.id = id;
    this.lineNumbers = lineNumbers;
    this.label = label;
    
    textArea = new InlineCssTextArea(s);
    virtualizedScrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
    textArea.setEditable(editable);    
    //font currently not supported
    textArea.setStyle("-fx-font-size:"+fontsize+"pt;");
    
    
    textArea.plainTextChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) 
    .subscribe(change -> {
        if (!dirty) {
            Message message = EditorClient.theClient().getHandler().newMessage("textDirty", 2);
            message.args[0] = new Value(getId());
            message.args[1] = new Value(true);
            EditorClient.theClient().getHandler().raiseEvent(message);

            dirty = true;
          }
        if (autoComplete){
        	checkKeywords();
        }
        if (autoComplete && change.getInserted().length() == 1 && change.getInserted().charAt(0)=='@') {
        	at(); 
        }
        int start = change.getPosition();
        int length = change.getNetLength();
        if (length > 0) addStylesQueueRequest(start, length, textArea.getText());
        syntaxDirty++;
        Platform.runLater(()->{
        	syntaxDirty--;
        	if (syntaxDirty == 0) {
        		addStyles();
        	}	
        });
    
    });
    
    textArea.setOnMouseClicked(e->{
    	if(e.isControlDown()){
    		MenuClient.popup(id, textArea, (new Double(e.getScreenX())).intValue(), (new Double(e.getScreenY())).intValue());
    	}
    });
    
    textArea.setOnContextMenuRequested(e->{
    	MenuClient.popup(id, textArea, (new Double(e.getScreenX())).intValue(), (new Double(e.getScreenY())).intValue());
    });
    
    textArea.addEventFilter(ScrollEvent.ANY, e->{
    	if(e.isControlDown()){
    		if(e.getDeltaY()>1){
    			fontsize += 2;
    		}else{
    		if (fontsize > 6) {
    				fontsize -= 2;
    			}
    		}
    		textArea.setStyle("-fx-font-size:"+fontsize+"pt;");
    		e.consume();
    	}
   
    });
    
    textArea.setOnKeyReleased(e->{
    	if ((e.isControlDown() || e.isMetaDown()) && (e.getCode() == KeyCode.PLUS)) {
    		fontsize += 2;
    		textArea.setStyle("-fx-font-size:"+fontsize+"pt;");
    		e.consume();
        }
        if ((e.isControlDown() || e.isMetaDown()) && (e.getCode() == KeyCode.MINUS)) {
        	fontsize -= 2;
        	textArea.setStyle("-fx-font-size:"+fontsize+"pt;");
        	e.consume();
        }
        if ((e.isControlDown() || e.isMetaDown()) && ((e.getCode() == KeyCode.DIGIT0)||(e.getCode() == KeyCode.NUMPAD0)) ) {
        	fontsize = 10;
        	textArea.setStyle("-fx-font-size:"+fontsize+"pt;");
        	e.consume();
        }
          if ((e.isControlDown() || e.isMetaDown()) && (e.getCode() == KeyCode.F)) {
            FindUtil.show3(textArea);
            e.consume();
          }
          if ((e.isControlDown() || e.isMetaDown()) && (e.getCode() == KeyCode.S)) {
            save();
            e.consume();
          }
          if ((e.isControlDown() || e.isMetaDown()) && (e.getCode() == KeyCode.L)) {
        	TextEditor.this.lineNumbers = !TextEditor.this.lineNumbers;
            addLines();
            e.consume();
          }
//          if (isCntrl(e) && (e.keyCode == 'v')) {
//            Display display = XModeler.getXModeler().getDisplay();
//            Clipboard clipboard = new Clipboard(display);
//            ImageData imageData = (ImageData) clipboard.getContents(ImageTransfer.getInstance());
//            if (imageData != null) {
//              e.doit = false;
//              insertImage(imageData, text.getCaretOffset());
//            }
//          }
          
          
          
          if ((e.getCode() == KeyCode.AT) ){//&& autoComplete) {
        	System.out.println(e.getCharacter());  
            if(!at()){
            	e.consume();
            }
          }
          
          if (!e.isConsumed()) lastCharIsLetter = e.getCode().isLetterKey();
          
                   
          
    });
    
    populateAt();
    populateKeywords();
    addCommentWordRule();
    
//    text = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
//    text.setEditable(editable);
//    text.setText(s);
//    FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
//    this.fontData = fontData[0];
//    XModeler.getXModeler().getDisplay().loadFont("dejavu/DejaVuSansMono.ttf");
//    this.fontData.setName("DejaVu Sans Mono");
//    text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//    Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
//    text.setBackground(bg);
//    text.addExtendedModifyListener(this);
//    text.addVerifyKeyListener(this);
//    text.addMouseListener(this);
//    text.addMouseWheelListener(this);
//    text.addLineBackgroundListener(this);
//    text.addVerifyListener(this);
//    text.addPaintObjectListener(this);
//    text.addSelectionListener(this);  
//    GC gc = new GC(text);
//    gc.setTextAntialias(SWT.ON);
//    populateAt();
//    populateKeywords();
//	included out of the box    
//    new UndoRedoImpl(text);
//    addCommentWordRule();
  }

  private PPrint _class() {
    return new Seq(new Indent(new Seq(new Literal("@Class"), new Space(), new Literal("name"), new Space(), new Literal("extends"), new Space(), new Literal("Object"), new NewLine(), attribute(), new NewLine(), operation())), new NewLine(), new Literal("end"));
  }

  private PPrint _for() {
    return new Seq(new Indent(new Seq(new Literal("@For"), new Space(), new Literal("name"), new Space(), new Literal("in"), new Space(), new Literal("collection"), new Space(), new Literal("do"), new NewLine(), new Literal("body"))), new NewLine(), new Literal("end"));
  }

  private PPrint _package() {
    return new Seq(new Indent(new Seq(new Literal("@Package"), new Space(), new Literal("name"), new NewLine(), _class())), new NewLine(), new Literal("end"));
  }

  private PPrint _try() {
    return new Seq(new Indent(new Seq(new NewLine(), new Literal("body"))), new NewLine(), new Indent(new Seq(new Literal("catch(exception)"), new NewLine(), new Literal("handler"))), new NewLine(), new Literal("end"));
  }

  private void addCommentWordRule() {
    // This should be done by XMF really. Add comment as the first multiline rule...
    addMultilineRule(getId(), "//", "\n", 120, 120, 120);
    wordRules.addElement(new NumberWordRule());
  }

//Currently not supported  
//  public void addImage(Image image, int offset) {
//    StyleRange style = new StyleRange();
//    style.start = offset;
//    style.length = 1;
//    Rectangle rect = image.getBounds();
//    style.metrics = new GlyphMetrics(rect.height, 0, rect.width);
//    text.setStyleRange(style);
//  }
//
//  private void addImages(String s) {
//    // Images are encoded using #i[byte,byte,...] format. They start at the offset
//    // in the string that they will occur in the text...
//    int stringIndex = 0;
//    int offset = 0;
//    while (stringIndex < s.length()) {
//      if (s.startsWith("#i[", stringIndex)) {
//        int end = s.indexOf("]", stringIndex);
//        String byteString = s.substring(stringIndex + 3, end);
//        String[] byteStrings = byteString.split(",");
//        byte[] bytes = new byte[byteStrings.length];
//        for (int i = 0; i < bytes.length; i++) {
//          bytes[i] = Byte.parseByte(byteStrings[i]);
//        }
//        ImageLoader loader = new ImageLoader();
//        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
//        ImageData data = loader.load(in)[0];
//        insertImage(data, offset);
//        stringIndex = end + 1;
//      } else stringIndex++;
//      offset++;
//    }
//  }

  private void addKeyword(String keyword, String description, PPrint pprint) {
    if (!keyTable.containsKey(keyword)) keyTable.put(keyword, new Vector<Keyword>());
    Vector<Keyword> keys = keyTable.get(keyword);
    keys.add(new Keyword(description, pprint));
  }

  public void addLineHighlight(int line) {
//    highlights.add(text.getOffsetAtLine(line - 1));
//    text.redraw();
	  
  	  String s = textArea.getText();
  	  int index = -1;
  	  
  	  for(int i=1; i< line;i++){
  		  index = s.indexOf("\n",index+1);  
  	  }
  	  index += 1;
  	  int lineEnd = s.indexOf("\n",index+1);
  	  textArea.setStyle(index, lineEnd, "-fx-highlight-fill:rgb(192, 192, 192);");
  }

  public void addLines() {
	  if (lineNumbers) {
		  textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
	    } else {
	    	textArea.setParagraphGraphicFactory(null);
	    }
//    if (lineNumbers) {
//      int maxLine = text.getLineCount();
//      int lineCountWidth = Math.max(String.valueOf(maxLine).length(), 3);
//      StyleRange style = new StyleRange(0, text.getTextLimit(), EditorClient.GREY, EditorClient.WHITE);
//      style.metrics = new GlyphMetrics(0, 0, lineCountWidth * 8 + 5);
//      Bullet bullet = new Bullet(ST.BULLET_NUMBER, style);
//      text.setLineBullet(0, text.getLineCount(), null);
//      text.setLineBullet(0, text.getLineCount(), bullet);
//      text.setLineIndent(0, text.getLineCount(), lineCountWidth + 10);
//    } else {
//      text.setLineBullet(0, text.getLineCount(), null);
//      text.setLineIndent(0, text.getLineCount(), 10);
//    }
  }

  public void addMultilineRule(String id, String start, String end, int red, int green, int blue) {
    if (getId().equals(id)) {
      wordRules.add(new MultiLineRule(start, end, Color.rgb(red, green, blue)));
    }
  }

  private void addStyles() {
    addStylesQueueRequest(0, textArea.getText().length(), textArea.getText());
    // System.err.println("addStyles START");
    // if (text.getCharCount() > 0) {
    // final StyleRange[] styleRanges = styleRanges();
    // System.err.println("addStyles MITTE");
    // Display.getCurrent().asyncExec(new Runnable() {
    // @Override
    // public void run() {
    // text.replaceStyleRanges(0, text.getCharCount() - 1, styleRanges);
    // }
    // });
    // }
    // System.err.println("addStyles ENDE");
  }

  // private void addStyles(int i, int length, String s) {
  // int start = backupToPossibleStyleStart(i);
  // int end = start + length + (i - start);
  // StyleRange[] ranges = styleRange(start, end, s);
  // for (StyleRange range : ranges)
  // end = Math.max(end, range.start + range.length);
  // if (ranges.length > 0 && s.length() > 0) text.replaceStyleRanges(start, end - start, ranges);
  // }

  private void addStylesQueueRequest(int start, int length, String s) {
    // System.err.println(start + " ---> " + length);
    int startNew = backupToPossibleStyleStart(start);
    length = length + start - startNew;
    start = startNew;
    // System.err.println(start + " ---> " + length);
    if (syntaxBusy) {
      // System.err.println("syntaxBusy, request queued");
      styleQueue.add(new StyleQueueItem(start, length, s));
    } else {
      // Todo: if syntax is not busy but a request got stuck
      // System.err.println("request not queued");
      if (styleQueue.size() != 0) System.err.println("A request got stuck");
      syntaxBusy = true;
      addStylesNew(start, length, s);
    }
  }

  private class StyleQueueItem {
    final int    start;
    final int    length;
    final String s;

    public StyleQueueItem(int start, int length, String s) {
      super();
      this.start = start;
      this.length = length;
      this.s = s;
    }
  }

  private Vector<StyleQueueItem> styleQueue = new Vector<StyleQueueItem>();

  private void addStylesNew(final int start, final int length, final String s) {
    // System.err.println("\n");
//    RuntimeException err = new RuntimeException();
    // StackTraceElement el = err.getStackTrace()[1];

//    final Display display = Display.getCurrent();
    if (textArea.getText().length() > 0) {
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          final StyleSpans<String> styleSpans = styleRange(start, start + length, s);	
//          final StyleRange[] styleRanges = styleRange(start, start + length, s);
          if (styleSpans != null && styleSpans.length() > 0) {
        	Platform.runLater(()->{
        		textArea.setStyleSpans(0, styleSpans);
        	});  
//            display.asyncExec(new Runnable() {
//              @Override
//              public void run() {
//                try {
//                  text.replaceStyleRanges(start, length, styleRanges);
//                } catch (Exception e) {
//                }
//              }
//            });
          }
          if (styleQueue.size() != 0) {
            final StyleQueueItem next = styleQueue.remove(0);
            // System.err.println("request unqueued");
            Platform.runLater(()->{
            	addStylesNew(next.start, next.length, next.s);
        	});
//            display.asyncExec(new Runnable() {
//              @Override
//              public void run() {
//                addStylesNew(next.start, next.length, next.s);
//              }
//            });
          } else {
            syntaxBusy = false;
          }
        }
      });

      thread.start();
    } else {
      syntaxBusy = false;
    }
  }

  public void addWordRule(String id, String text, int red, int green, int blue) {
    if (getId().equals(id)) wordRules.add(new WordRule(text, Color.rgb(red, green, blue)));
  }

  private boolean at() {
    // The user typed an '@'. Offer up the options in the atTable and
    // return false if nothing is selected. Insert the choice and return
    // true if selected...
    if (!atTable.isEmpty()) {
      final boolean[] result = new boolean[] { false };
      ContextMenu cm = getAtPopup(result);
      System.out.println(cm.getItems());
      System.out.println(textArea.getCaretBounds().get().getMaxX());
      System.out.println(textArea.getCaretBounds().get().getMaxY());
      cm.setAutoHide(true);
//      Point p = text.getCaret().getLocation();
//      Point displayPoint = text.toDisplay(p);
//      menu.setLocation(displayPoint);
      cm.show(textArea, textArea.getCaretBounds().get().getMaxX(),textArea.getCaretBounds().get().getMaxY());
//      menu.setVisible(true);
//      while (!menu.isDisposed() && menu.isVisible()) {
//        if (!Display.getCurrent().readAndDispatch()) Display.getCurrent().sleep();
//      }
//      menu.dispose();
      return result[0];
    } else return false;
  }

  private PPrint attribute() {
    return new Seq(new Literal("@Attribute"), new Space(), new Literal("name"), new Space(), new Literal(":"), new Space(), new Literal("Type"), new Space(), new Literal("end"));
  }

  private int backupToPossibleStyleStart(int start) {
    start -= 10;
    if (start < 0) return 0;
    int checkStart = start-1;
    StyleSpans<String> spans = textArea.getStyleSpans(start, start);
    spans.getStyleSpan(0);
        
    if(spans.getStyleSpan(0) == textArea.getStyleSpans(checkStart, checkStart).getStyleSpan(0) ){
    	while(spans.getStyleSpan(0) == textArea.getStyleSpans(checkStart, checkStart).getStyleSpan(0) ){
    		checkStart--;
    	}
    	return checkStart+1;
    }
//    StyleRange[] ranges = text.getStyleRanges();
//    // System.err.println("start: " + start);
//    for (int i = 0; i < ranges.length; i++) {
//      // System.err.println("rangeStart: " + ranges[i].start);
//      // System.err.println("rangeEnd: " + (ranges[i].start + ranges[i].length));
//      if (ranges[i].start <= start && ranges[i].start + ranges[i].length >= start) return ranges[i].start;
//    }
//    // System.err.println("findStartFailed");

    String s = textArea.getText();
    while (start > 0 && s.charAt(start) != ' ') // isKeywordChar2(s.charAt(start)))
      start--;
    return start;
  }

  private void checkKeywords() {
    if (lastCharIsLetter) {
      Vector<Keyword> keys = getKeysAtCurrentPosition();
      if (keys != null) key(keys);
    }
  }

  public void clearHighlights() {
    highlights.clear();
  }

  private PPrint constructor() {
    return new Literal("@Constructor(slots) ! end");
  }

  private boolean containsImages(String s) {
    return s.contains("#i[");
  }

  private PPrint contextClass() {
    return new Indent(new Seq(new Space(), new Literal("Root"), new NewLine(), _class()));
  }

  private PPrint contextOperation() {
    return new Indent(new Seq(new Space(), new Literal("Root"), new NewLine(), operation()));
  }

  private PPrint contextPackage() {
    return new Indent(new Seq(new Space(), new Literal("Root"), new NewLine(), _package()));
  }

  private String filterImages(String s) {
    // Return a string with the images removed...
    StringBuffer b = new StringBuffer();
    int offset = 0;
    while (offset < s.length()) {
      if (s.startsWith("#i[", offset)) {
        offset = s.indexOf("]", offset) + 1;
      } else b.append(s.charAt(offset++));
    }
    return new String(b);
  }

  private PPrint format() {
    return new Literal("(stdout, \"\",Seq{})");
  }

  protected ContextMenu getAtPopup(final boolean[] result) {
    // Offer the @ options. If selected then insert the text
    // including the '@' and set the result...
	ContextMenu cm = new ContextMenu();   
//    Menu menu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
    for (final String name : atTable.keySet()) {
      MenuItem item = new MenuItem(name);
//      item.setText(name);
      item.setOnAction(e->{
    	  PPrint pprint = atTable.get(name);
          int indent = getCurrentIndent();
          String s = pprint.toString(indent);
          textArea.deleteText(textArea.getCaretPosition()-1, textArea.getCaretPosition());
          textArea.insertText(textArea.getCaretPosition(), s);
//          textArea.moveTo(textArea.getCaretPosition() + s.length());
          result[0] = true;
      });
      cm.getItems().add(item);
//      item.addSelectionListener(new SelectionListener() {
//        public void widgetDefaultSelected(SelectionEvent event) {
//        }
//
//        public void widgetSelected(SelectionEvent event) {
//          PPrint pprint = atTable.get(name);
//          int indent = getCurrentIndent();
//          String s = pprint.toString(indent);
//          text.insert(s);
//          text.setCaretOffset(text.getCaretOffset() + s.length());
//          result[0] = true;
//        }
//      });
    }
    return cm;
  }

  private int getCurrentIndent() {
    String s = textArea.getText();
    int start = 0;//text.getOffsetAtLine(text.getLineAtOffset(text.getCaretOffset()));
    int indent = 0;
    for (int i = start; i < s.length() && s.charAt(i) == ' '; i++)
      indent++;
    return indent;
  }

  public String getId() {
    return id;
  }

//  private Image getImageAt(int offset) {
//    for (int i = 0; i < images.length; i++) {
//      if (offsets[i] == offset) return images[i];
//    }
//    return null;
//  }

  private Vector<Keyword> getKeysAtCurrentPosition() {
    int index = textArea.getCaretPosition();
    String s = textArea.getText();
    for (String key : keyTable.keySet()) {
      int match = s.indexOf(key, index - key.length());
      if (match != -1 && match == index - key.length()+1) return keyTable.get(key);
    }
    return null;
  }

  public String getLabel() {
    return label;
  }

//  public int getOffset(Image image) {
//    for (int i = 0; i < offsets.length; i++)
//      if (images[i] == image) return offsets[i];
//    return -1;
//  }

  public String getString() {
    // Called to get the content of the editor.
    // If the text contains embedded controls then these need to be
    // encoded...
//    if (images.length > 0) {
//      StringBuffer s = new StringBuffer();
//      String t = text.getText();
//      int offset = 0;
//      while (offset < t.length()) {
//        if (getImageAt(offset) != null) {
//          Image image = getImageAt(offset);
//          ImageData data = image.getImageData();
//          s.append("#i[");
//          ByteArrayOutputStream out = new ByteArrayOutputStream();
//          ImageLoader loader = new ImageLoader();
//          loader.data = new ImageData[] { data };
//          loader.save(out, SWT.IMAGE_PNG);
//          byte[] bytes = out.toByteArray();
//          for (int i = 0; i < bytes.length; i++) {
//            byte b = bytes[i];
//            s.append(Byte.toString(b));
//            if (i < bytes.length - 1) s.append(",");
//          }
//          s.append("]");
//        } else s.append(t.charAt(offset));
//        offset++;
//      }
//      return new String(s);
//    } else 
    	return textArea.getText();
  }

//  public StyledText getText() {
//    return text;
//  }

  public  javafx.scene.Node getText() {
	    return virtualizedScrollPane;
	  }
  
  private PPrint ifThen() {
    return new Seq(new Space(), new Literal("exp"), new Space(), new NewLine(), new Literal("then"), new Space(), new Literal("exp"), new NewLine(), new Literal("end"));
  }

  private PPrint ifThenElse() {
    return new Seq(new Space(), new Literal("exp"), new Space(), new NewLine(), new Literal("then"), new Space(), new Literal("exp"), new NewLine(), new Literal("else"), new Space(), new Literal("exp"), new NewLine(), new Literal("end"));
  }

  public void inflate(Node textEditor) {
    NodeList children = textEditor.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateElement(children.item(i));
    EditorClient.theClient().runOnDisplay(new Runnable() {
      public void run() {
        addLines();
        addStyles();
      }
    });
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

//  private void insertImage(ImageData imageData, int offset) {
//    Display display = XModeler.getXModeler().getDisplay();
//    Image image = new Image(display, imageData);
//    try {
//      text.replaceTextRange(offset, 0, "\uFFFC");
//      int index = 0;
//      while (index < offsets.length) {
//        if (offsets[index] == -1 && images[index] == null) break;
//        index++;
//      }
//      if (index == offsets.length) {
//        int[] tmpOffsets = new int[index + 1];
//        System.arraycopy(offsets, 0, tmpOffsets, 0, offsets.length);
//        offsets = tmpOffsets;
//        Image[] tmpImages = new Image[index + 1];
//        System.arraycopy(images, 0, tmpImages, 0, images.length);
//        images = tmpImages;
//      }
//      offsets[index] = offset;
//      images[index] = image;
//      addImage(image, offset);
//    } catch (Exception e1) {
//      e1.printStackTrace();
//    }
//  }

//  private boolean isAlpha(char c) {
//    return 'a' <= c && c <= 'z';
//  }

  // private boolean isNumber(char c) {
  // return '0' <= c && c <= '9';
  // }

  // private boolean isAlphaChar(char c) {
  // return isLowerAlphaChar(c) || isUpperAlphaChar(c);
  // }

//  private boolean isCommand(MouseEvent event) {
//    return (event.stateMask & SWT.COMMAND) != 0;
//  }

  public boolean isDirty() {
    return dirty;
  }

  // private boolean isKeywordChar(char c) {
  // return isAlphaChar(c) || c == '_';
  // }
  //
  // private boolean isKeywordChar2(char c) {
  // return isAlphaChar(c) || isNumber(c) || c == '_' || c == '.' || c == '-' ;
  // }

//  public boolean isLeft(MouseEvent event) {
//    return event.button == 1;
//  }

  // private boolean isLowerAlphaChar(char c) {
  // return 'a' <= c && 'z' >= c;
  // }

//  private boolean isRightClick(MouseEvent event) {
//    return event.button == RIGHT_BUTTON || isCntrl(event);
//  }

  // private boolean isUpperAlphaChar(char c) {
  // return 'A' <= c && 'Z' >= c;
  // }

  private void key(Vector<Keyword> keys) {
    if (keys.size() == 1) {
      String s = keys.elementAt(0).toString(getCurrentIndent());
      autoComplete = false;
      textArea.insertText(textArea.getCaretPosition(), s);
      autoComplete = true;
      textArea.moveTo(textArea.getCaretPosition()+ s.length());
//      text.setCaretOffset(text.getCaretOffset() + s.length());
    }
    if (keys.size() > 1) {
    	ContextMenu cm = new ContextMenu();
//      Menu menu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
      for (final Keyword keyword : keys) {
        MenuItem item = new MenuItem(keyword.description);
//        item.setText(keyword.description);
        item.setOnAction(e->{
        	String s = keyword.toString(getCurrentIndent());
            autoComplete = false;
            textArea.insertText(textArea.getCaretPosition(), s);
            autoComplete = true;
        });
        cm.getItems().add(item);
//        item.addSelectionListener(new SelectionListener() {
//          public void widgetDefaultSelected(SelectionEvent event) {
//          }
//
//          public void widgetSelected(SelectionEvent event) {
//            String s = keyword.toString(getCurrentIndent());
//            autoComplete = false;
//            text.insert(s);
//            autoComplete = true;
//            text.setCaretOffset(text.getCaretOffset() + s.length());
//          }
//        });
      }
      cm.show(textArea, textArea.getCaretBounds().get().getMaxX(), textArea.getCaretBounds().get().getMaxY());
//      menu.setVisible(true);
//      while (!menu.isDisposed() && menu.isVisible()) {
//        if (!Display.getCurrent().readAndDispatch()) Display.getCurrent().sleep();
//      }
//      menu.dispose();
    }
  }

  private PPrint letMultipleSequentialBindings() {
    return new Seq(new Space(), new Indent(new Indent(new Seq(new Literal("name = exp then"), new NewLine(), new Literal("name = exp")))), new NewLine(), new Literal("in"), new Space(), new Literal("body"), new NewLine(), new Literal("end"));
  }

  private PPrint letSingleBinding() {
    return new Seq(new Space(), new Literal("name = exp"), new NewLine(), new Literal("in"), new Space(), new Literal("body"), new NewLine(), new Literal("end"));
  }

//  public void lineGetBackground(LineBackgroundEvent event) {
//    if (highlights.contains(event.lineOffset)) event.lineBackground = EditorClient.LINE_HIGHLIGHT;
//  }

//  public void modifyText(ExtendedModifyEvent event) {
//    if (!dirty) {
//      Message message = EditorClient.theClient().getHandler().newMessage("textDirty", 2);
//      message.args[0] = new Value(getId());
//      message.args[1] = new Value(true);
//      EditorClient.theClient().getHandler().raiseEvent(message);
//
//      dirty = true;
//    }
//    // System.err.println("start: " + event.start);
//    // System.err.println("length: " + event.length);
//    int start = event.start;
//    int length = event.length;
//    if (length > 0) addStylesQueueRequest(start, length, text.getText());
////    if (autoComplete) checkKeywords();
//
//    // addStylesNew(start, length, s);
//
////    syntaxDirty++;
////    Display.getCurrent().timerExec(3000, new Runnable() {
////      @Override
////      public void run() {
////        syntaxDirty--;
////        if (syntaxDirty == 0) {
////          addStyles();
////        }
////      }
////    });
//
//    addLines();
//  }

//  public void mouseDoubleClick(MouseEvent event) {
//
//  }

//  public void mouseDown(MouseEvent event) {
//    if (isRightClick(event) || isCommand(event)) {
//      rightClick(event);
//    }
//  }

//  public void mouseUp(MouseEvent event) {
//
//  }

//  @Override
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

//  private void newline(int indent) {
//    text.insert("\n");
//    try {
//      text.setCaretOffset(text.getCaretOffset() + 1);
//      for (int i = 0; i < indent; i++)
//        text.insert(" ");
//      text.setCaretOffset(text.getCaretOffset() + indent);
//    } catch (IllegalArgumentException iae) {
//      System.err.println("This exception caused the program to freeze.\n Whatever went wrong, now it does not freeze for this reason anymore.");
//      iae.printStackTrace();
//    }
//  }

  private PPrint operation() {
    return new Seq(new Literal("@Operation name(args)"), new Indent(new Seq(new NewLine(), new Literal("body"))), new NewLine(), new Literal("end"));
  }

//  public void paintObject(PaintObjectEvent event) {
//    GC gc = event.gc;
//    StyleRange style = event.style;
//    int start = style.start;
//    for (int i = 0; i < offsets.length; i++) {
//      int offset = offsets[i];
//      if (start == offset) {
//        Image image = images[i];
//        int x = event.x;
//        int y = event.y + event.ascent - style.metrics.ascent;
//        gc.drawImage(image, x, y);
//      }
//    }
//  }

  private void populateAt() {
    atTable.put("Attribute", attribute());
    atTable.put("Class", _class());
    atTable.put("Constructor", constructor());
    atTable.put("For", _for());
    atTable.put("Operation", operation());
    atTable.put("TypeCase", typeCase());
    atTable.put("WithOpenFile (in)", withOpenFileIn());
    atTable.put("WithOpenFile (out)", withOpenFileOut());
  }

  private void populateKeywords() {
    addKeyword("context", "context (class)", contextClass());
    addKeyword("context", "context (operation)", contextOperation());
    addKeyword("if", "if (then)", ifThen());
    addKeyword("if", "if (then else)", ifThenElse());
    addKeyword("format", "format", format());
    addKeyword("let", "let (multiple sequential bindings)", letMultipleSequentialBindings());
    addKeyword("let", "let (single binding)", letSingleBinding());
    addKeyword("try", "try", _try());
    addKeyword("context", "context (package)", contextPackage());
  }

//  public void rightClick(MouseEvent event) {
//    MenuClient.popup(id, event.x, event.y);
//  }

  public void save() {
    Message message = EditorClient.theClient().getHandler().newMessage("saveText", 2);
    message.args[0] = new Value(getId());
    message.args[1] = new Value(getString());
    EditorClient.theClient().getHandler().raiseEvent(message);
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  public void setString(String text) {
    // Called on a possibly encoded text...
    if (containsImages(text)) {
      setText(filterImages(text));
      // Fool the system into thinking that it has already informed
      // the server that the editor is dirty...
//      dirty = true;
//      addImages(text);
//      dirty = false;
    } else setText(text);
  }

  public void setText(String s) {
    // We do not want to fire a dirty event at this point because
    // this should only be called to initialize the content or to
    // refresh the content...
    dirty = true;
    textArea.replaceText(s);
    dirty = false;
    addLines();
  }

  public void showLine(int line) {
  	  String s = textArea.getText();
  	  int index = -1;
  	  
  	  for(int i=1; i< line;i++){
  		  index = s.indexOf("\n",index+1);  
  	  }
  	  index += 1;
  	  textArea.requestFocus();
  	  textArea.moveTo(index);
	  
//    text.setCaretOffset(text.getOffsetAtLine(line));
//    text.redraw();
  }

  private StyleSpans<String> styleRange(int start, int end, String s) {
//  private StyleRange[] styleRange(int start, int end, String s) {
	  StyleSpansBuilder<String> sb = new StyleSpansBuilder<String>();  
//	java.util.List<StyleRange> ranges = new java.util.ArrayList<StyleRange>();
    // String s = text.getText();
	StyleSpan<String> defaultStyle = new StyleSpan<String>("", 1);
	  
    int prevChar = -1;
    for (int i = start; i < end; i++) {
      StyleSpan<String> style = null;
      for (WordRule wordRule : wordRules) {
        style = wordRule.match(s, i, prevChar);
        if (style != null) {
        	sb.add(style);
//          ranges.add(style);
          i = i + style.getLength() - 1;
          break;
        }
      }
      if (style == null) {
    	sb.add(defaultStyle);  
//        StyleRange defaultStyle = new StyleRange();
//        defaultStyle.start = i;
//        defaultStyle.length = 1;
//        defaultStyle.fontStyle = SWT.UNDERLINE_SINGLE;
//        defaultStyle.foreground = new Color(Display.getCurrent(), 0, 0, 0);
//        ranges.add(defaultStyle);
      }
      prevChar = s.charAt(i);
    }
    return sb.create();
  }

  // private StyleRange[] styleRanges() {
  // return styleRange(0, text.getText().length(), text.getText());
  // }

  private PPrint typeCase() {
    return new Seq(new Indent(new Seq(new Literal("@TypeCase(exp)"), new NewLine(), new Indent(new Seq(new Literal("exp"), new Space(), new Literal("do"), new NewLine(), new Literal("exp"))), new NewLine(), new Literal("end"), new NewLine(), new Literal("else"), new Space(), new Literal("exp"))), new NewLine(), new Literal("end"));
  }

//  private boolean isCntrl(VerifyEvent e) {
//    return (e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND;
//  }
//
//  private boolean isCntrl(MouseEvent e) {
//    return (e.stateMask & SWT.CTRL) == SWT.CTRL || (e.stateMask & SWT.COMMAND) == SWT.COMMAND;
//  }

//  public void verifyKey(VerifyEvent e) {
//    if (isCntrl(e) && (e.keyCode == '=')) {
//      if (selectedImage != null)
//        growSelectedImage();
//      else {
//        fontData.setHeight(Math.min(fontData.getHeight() + ZOOM, MAX_FONT_SIZE));
//        text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//        e.doit = false;
//      }
//    }
//    if (isCntrl(e) && (e.keyCode == '-')) {
//      if (selectedImage != null)
//        shrinkSelectedImage();
//      else {
//        fontData.setHeight(Math.max(MIN_FONT_SIZE, fontData.getHeight() - ZOOM));
//        text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//        e.doit = false;
//      }
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
//      lineNumbers = !lineNumbers;
//      addLines();
//      e.doit = false;
//    }
//    if (isCntrl(e) && (e.keyCode == 'v')) {
//      Display display = XModeler.getXModeler().getDisplay();
//      Clipboard clipboard = new Clipboard(display);
//      ImageData imageData = (ImageData) clipboard.getContents(ImageTransfer.getInstance());
//      if (imageData != null) {
//        e.doit = false;
//        insertImage(imageData, text.getCaretOffset());
//      }
//    }
//    if (e.character == '@' && autoComplete) {
//      e.doit = !at();
//    }
//    //propably not required anymore do to JavaFX
////    if (e.character == '\r') {
////      newline(getCurrentIndent());
////      e.doit = false;
////    }
//    if (e.doit) lastChar = e.character;
//  }

//  private void growSelectedImage() {
//    ImageData data = selectedImage.getImageData();
//    int width = data.width;
//    int height = data.height;
//    Image image = resize(selectedImage, (int) (width * 1.2), (int) (height * 1.2));
//    for (int i = 0; i < images.length; i++) {
//      if (images[i] == selectedImage) {
//        images[i] = image;
//        addImage(image, offsets[i]);
//        selectedImage.dispose();
//        selectedImage = image;
//      }
//    }
//  }

//  private Image resize(Image image, int width, int height) {
//    Image scaled = new Image(Display.getDefault(), width, height);
//    GC gc = new GC(scaled);
//    gc.setAntialias(SWT.ON);
//    gc.setInterpolation(SWT.HIGH);
//    gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
//    gc.dispose();
//    return scaled;
//  }

//  private void shrinkSelectedImage() {
//    ImageData data = selectedImage.getImageData();
//    int width = data.width;
//    int height = data.height;
//    Image image = resize(selectedImage, (int) (width * 0.8), (int) (height * 0.8));
//    for (int i = 0; i < images.length; i++) {
//      if (images[i] == selectedImage) {
//        images[i] = image;
//        addImage(image, offsets[i]);
//        selectedImage.dispose();
//        selectedImage = image;
//      }
//    }
//  }

//  public void verifyText(VerifyEvent e) {
//    int start = e.start;
//    int replaceCharCount = e.end - e.start;
//    int newCharCount = e.text.length();
//    for (int i = 0; i < offsets.length; i++) {
//      int offset = offsets[i];
//      if (start <= offset && offset < start + replaceCharCount) {
//        // this image is being deleted from the text
//        if (images[i] != null && !images[i].isDisposed()) {
//          images[i].dispose();
//          images[i] = null;
//        }
//        offset = -1;
//      }
//      if (offset != -1 && offset >= start) offset += newCharCount - replaceCharCount;
//      offsets[i] = offset;
//    }
//  }

  private PPrint withOpenFileIn() {
    return new Seq(new Indent(new Seq(new Literal("@WithOpenFile(fin <- filename)"), new NewLine(), new Literal("body"))), new NewLine(), new Literal("end"));
  }

  private PPrint withOpenFileOut() {
    return new Seq(new Indent(new Seq(new Literal("@WithOpenFile(fout -> filename)"), new NewLine(), new Literal("body"))), new NewLine(), new Literal("end"));
  }

  public void writeXML(PrintStream out, boolean isSelected, String label, String toolTip) {
    out.print("<TextEditor id='" + getId() + "' selected='" + isSelected + "'");
    out.print(" text='" + XModeler.encodeXmlAttribute(textArea.getText()) + "'");
    out.print(" lineNumbers='" + lineNumbers + "'");
    out.print(" label='" + label + "'");
    out.print(" toolTip='" + toolTip + "'");
    out.print(" editable='" + textArea.isEditable() + "'>");
    // out.print(" fontHeight='" + fontData.getHeight() + "'>");
    for (WordRule rule : wordRules)
      rule.writeXML(out);
    out.print("</TextEditor>");
  }

//  public void widgetDefaultSelected(SelectionEvent event) {
//  }

//  public void widgetSelected(SelectionEvent event) {
//    try {
//      selectedImage = getImageAt(text.getSelection().x);
//    } catch (Exception e) {
//      // Silently fail where things go wrong.
//    }
//  }

  public void varDec(int charStart, int charEnd, int decStart, int decEnd) {
    
  }

  public void setRendering(boolean state) {
  }

  public void unboundVar(String name, int charStart, int charEnd) {
    
  }

  public void syntaxError(int pos, String error) {
    
  }

  public void clearErrors() {
    
  }
  public void setTooltip(String tooltip, int charStart, int charEnd) {
  }

  public void ast(String tooltip, int charStart, int charEnd) {
  }

  public void terminates(String end, String start) {
    
  }

  public void setSignature(Value[] entries) {
    
  }

  public void action(String name, Value[] args,int charStart,int charEnd) {
    
  }
}