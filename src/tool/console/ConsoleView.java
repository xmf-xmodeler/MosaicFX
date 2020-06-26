package tool.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

//import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tool.clients.consoleInterface.EscapeHandler;
import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;


public class ConsoleView {

  protected static final int FONT_INC        = 2;
  protected static final int MAX_FONT_HEIGHT = 30;
  protected static final int MIN_FONT_HEIGHT = 4;

  public static void setEscapeHandler(EscapeHandler handler) {
    escape = handler;
  }

  static EscapeHandler escape          = null;
  History              history         = new History();
  int                  inputStart      = 0;
  Color                backgroundColor = new Color(.1,.1,.1,1.);
  int                  waterMark       = 1000;
  PrintStream          out             = null;
  Object               overflowLock    = new Object();
  AutoComplete         autoComplete    = AutoComplete.newDefault();// de-activated for now for better usability
  Region				region = null;
  private final ScrollPane scrollpane; 
  private final TextArea textArea;
  
  Stage owner;
  
  
  public ConsoleView(Stage owner) {
	scrollpane = new ScrollPane();
	textArea = new TextArea();
	textArea.setPrefHeight(600);
	scrollpane.setContent(textArea);
	scrollpane.setFitToWidth(true);
	scrollpane.setFitToHeight(true);
	this.owner=owner;
	textArea.setWrapText(true);
	addVerifyListener(textArea);
    setFont(textArea.getFont().getSize());
  }
  
  private void setFont(double size) {
	    try {
	        Font f = Font.loadFont(new FileInputStream(new File("resources/fonts/DejaVuSansMono.ttf")), size);
	        textArea.setFont(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
}

public Node getView() {
		return scrollpane;
  }

  public final void setFont(String fileName, String name) {
//    int oldHeight = fontData == null ? 13 : fontData.getHeight();
//    FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
//    this.fontData = fontData[0];
//    // File root = getB
//    // URL url = ConsoleView.class.getResource(fileName);
//    XModeler.getXModeler().getDisplay().loadFont(fileName);
//    this.fontData.setName(name);
//    this.fontData.setHeight(oldHeight);
//    text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
  }

  public void addCommand(TextArea textArea, String command) {
//    int length = text.getCharCount() - inputStart;
    textArea.replaceText(inputStart, textArea.getText().length(), command);
    goToEnd();
  }

  boolean fontToggleTest = false;

  private static class Diff {
	  final int left;
	  final int right;
	  final String textPlus;
	  final String textMinus;
	  
	private Diff(int left, int right, 
			  final String textPlus,
			  final String textMinus) {
		super();
		this.left = left;
		this.right = right;
		this.textPlus = textPlus;
		this.textMinus = textMinus;
	}

	@Override
	public String toString() {
		return "Diff "
	            + left + "/" + right 
				+ " +(" + textPlus 
				+ ") -(" + textMinus + ")";
	}	  
  }
  
  private Diff compare(String oldValue, String newValue) {
	  int left = 0;
	  int right = 0;
	  while(     oldValue.length()>left 
			  && newValue.length()>left
			  && oldValue.charAt(left) == newValue.charAt(left) ) left++;
	  while(     oldValue.length()>right 
			  && newValue.length()>right
			  && left<oldValue.length()-right
			  && left<newValue.length()-right
			  && oldValue.charAt(oldValue.length()-right-1) == newValue.charAt(newValue.length()-right-1)) right++;
	  return new Diff(
			  left, 
			  right, 
			  newValue.substring(left, newValue.length()-right), 
			  oldValue.substring(left, oldValue.length()-right));
  }
  
  private transient boolean listenerActive = true;
  
  public void addVerifyListener(final TextArea textArea) {

	textArea.textProperty().addListener(new ChangeListener<String>() {
		public void changed(
				final ObservableValue<? extends String> observable, 
				final String oldValue, final String newValue) {
		  if(!listenerActive) {
			listenerActive = true; return;
		  }	 
		  Diff diff = compare(oldValue, newValue);
	      int start 		= diff.left;
	      int end   		= oldValue.length()-diff.right;
	      String diffText   = diff.textPlus;
//	      System.err.println(diff);
        if (start < inputStart || end < inputStart) {
        	/* if cursor not at end, 
        	 *   then revert change, 
        	 *        move cursor to end, 
        	 *        append diff there
        	 */
        	listenerActive = false;
        	textArea.setText(oldValue);
        	textArea.appendText(diffText);
        	
        	positionCaret(textArea.getText().length());
        	
        } else {
        	/* Do nothing */
		}
        
        if(newValue.startsWith(oldValue)) {
        	if(".".equals(diff.textPlus)) {
	            // Display options based on the type of the input.
	            String content = textArea.getText();
	            if (textArea.getCaretPosition() >= inputStart) {
	                String command = content.substring(inputStart);
	                WorkbenchClient.theClient().dotConsole(command.substring(0, command.length()-1));
	            }
        	}
        }
     }
    });
	
	
	
	textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
	    @Override
	    public void handle(KeyEvent keyEvent) {
	    	
        if (overwriting(keyEvent.getCharacter())) {
          try {
        	positionCaret(textArea.getCaretPosition() + 1);
          } catch (Exception err) {
            System.err.println("something's wrong with a caret");
            err.printStackTrace();
          }
          revertInput();
        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
          if (escape != null) escape.interrupt();
          revertInput();
        } else if (keyEvent.getCode() == KeyCode.UP) {
          String command = recallFromHistoryForward();
          if (command != "") addCommand(textArea, command);
          revertInput();
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
          String command = recallFromHistoryBackward();
          if (command != "") addCommand(textArea, command);
          revertInput();
        } else if (keyEvent.getCode() == KeyCode.PLUS && keyEvent.isControlDown()) {
        	setFont(textArea.getFont().getSize() + FONT_INC);
//          fontData.setHeight(Math.min(fontData.getHeight() + FONT_INC, MAX_FONT_HEIGHT));
//          text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//          revertInput();
        } else if (keyEvent.getCode() == KeyCode.MINUS && keyEvent.isControlDown()) {
        	setFont(textArea.getFont().getSize() - FONT_INC);
//          fontData.setHeight(Math.max(MIN_FONT_HEIGHT, fontData.getHeight() - FONT_INC));
//          text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//          revertInput();
//        } else if (e.keyCode == 't' && ((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
//          fontToggleTest = !fontToggleTest;
//          if (fontToggleTest)
//            setFont("fonts/DejaVuSans.ttf", "DejaVu Sans");
//          else setFont("fonts/DejaVuSansMono.ttf", "DejaVu Sans Mono");
//          revertInput();
//        } else if (e.keyCode == 'a' && ((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
//          autoComplete.toggleMainSwitch();
//          showStatus();
//          revertInput();
//        } else if (e.keyCode == '/' && ((e.stateMask & SWT.CTRL) == SWT.CTRL) && ((e.stateMask & SWT.SHIFT) == SWT.SHIFT)) {
//          help();
        } else if (keyEvent.getCode() == KeyCode.ENTER)  {
          String output = pushToHistory(textArea);
          if (out != null) {
            out.print(output + "\r");
            out.flush();
          }
          revertInput();
          goToEnd();
          inputStart = textArea.getText().length();
        } else if (keyEvent.getCharacter().charAt(0) == '.' /*|| e.keyCode == ' ' && ((e.stateMask & SWT.CTRL) == SWT.CTRL)*/ && autoComplete.isDisplayOptions()) {
          // Display options based on the type of the input.
          String content = textArea.getText();
          if (textArea.getCaretPosition() >= inputStart) {
          String command = content.substring(inputStart);
            WorkbenchClient.theClient().dotConsole(command);
          }
        } else if (keyEvent.getCharacter().charAt(0) == ':' && autoComplete.isColonAddPath()) {
          // We might have a :: where there is a path to the left ...
          String content = textArea.getText();
          String command = content.substring(inputStart);
          if (command.endsWith(":")) {
            WorkbenchClient.theClient().nameLookup(command.substring(0, command.length() - 1));
          }
        } else if (keyEvent.getCharacter().charAt(0) == '>' && autoComplete.isRightArrowFillPatterns()) {
          // We might have a -> and can fill in the standard patterns ...
          String content = textArea.getText();
          String command = content.substring(inputStart);
          if (command.endsWith("-")) {
            completeArrow();
          }
        } else if (keyEvent.getCharacter().charAt(0) == '{' && autoComplete.isSquareStartCollection()) {
          // Are we starting a collection?
          String content = textArea.getText();
          String command = content.substring(inputStart);
          if (command.endsWith("Set") || command.endsWith("Seq")) {
            insert("{}");
            backup(1);
            revertInput();
          }
        } else if (keyEvent.getCharacter().charAt(0) == '(' && autoComplete.isNineAddParenthesis()) {
          // Insert the corresponding parenthesis...
          insert("()");
          backup(1);
          revertInput();
        } else if (keyEvent.getCharacter().charAt(0) == '\"' && autoComplete.isApostropheAddQuotes()) {
            // Insert the corresponding close string...
          insert("\"\"");
          backup(1);
          revertInput();
        } else if (keyEvent.getCode() == KeyCode.DIGIT0 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #000000;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT1 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #ff0000;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT2 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #ffff00;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT3 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #00ff00;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT4 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #00ffff;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT5 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #0000ff;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT6 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #ff00ff;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT7 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #cccccc;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT8 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #88ff00;"); 
        } else if (keyEvent.getCode() == KeyCode.DIGIT9 && keyEvent.isControlDown()) { textArea.setStyle("-fx-text-fill: #cccccc;"); 
        } else prepareTopLevelCommand();
      }
    });
	        	
//
//    ConsoleLineStyler consoleLineStyper = new ConsoleLineStyler();
//    text.addLineStyleListener(consoleLineStyper);
  }
  
		private void revertInput() {
			System.err.println("revert input?");	
		}
		
		private void positionCaret(int pos) {
    	Platform.runLater( new Runnable() {
    	    @Override
    	    public void run() {
    	        textArea.positionCaret(pos);
    	    }
    	});
	}

  private void help() {
    // Show the commands...
    String s = "Console controls: ";
    s = s + "autocomplete (ctrl-a) = " + autoComplete + ", ";
    s = s + "zoom (ctrl+ ctrl-) " + ", ";
    s = s + "previous command (uparrow) ,";
    s = s + "next command (downarrow)";
    XModeler.showMessage("Console Commands", s);
  }

  private void showStatus() {
    String status = "AutoComplete = " + autoComplete;
    NotifierDialog.notify("Console Status", status, NotificationType.values()[2]);
  }

  public void appendText(String string) {
      synchronized (overflowLock) {
        if (PropertyManager.getProperty("LOG_XMF_OUTPUT", false)) System.err.println(string);
    	textArea.appendText(string);
//    	textArea.setText(textArea.getText() + string);
    }
  }

  public void prepareTopLevelCommand() { //TODO
//    if (textArea.getText().length() == textArea.getCaretPosition() && !textArea.getText().endsWith(";")) {
//      listenerActive = false;
//      insert(";");
//      backup(1);
//    }
  }

  public void insert(String string) {
    synchronized (overflowLock) {
    	
    	textArea.insertText(textArea.getCaretPosition(), string);
    	positionCaret(textArea.getCaretPosition() + string.length());

//      text.setCaretOffset(text.getCaretOffset() + string.length());
    }
  }

//  public void createPartControl(Composite parent) {
//  }

  public void dispose() {
    // textFont.dispose();
//    backgroundColor.dispose();
//    foregroundColor.dispose();
  }

  public PrintStream getOutput() {
    return out;
  }

  public void getPreferences() {
    // if (textFont != null) textFont.dispose();
//    if (backgroundColor != null) backgroundColor.dispose();
//    if (foregroundColor != null) foregroundColor.dispose();
  }

  public void goToEnd() {
	  positionCaret(textArea.getText().length());
  }

  public boolean overwriting(String c) {
//    int end = text.getCharCount();
//    int caret = text.getCaretOffset();
//    return caret < end && c.startsWith(text.getText().charAt(caret)+""); /*Todo: check if ok*/
	  /*TODO*/ return false;
  }

  public void backup(int backup) {
	  positionCaret(textArea.getText().length() - backup);
  }

	public void processInput(String input) {
		
		if (region==null) {  //Set backgroundColor and fontColor of the console
			try {
				region=(Region)textArea.lookup(".content");
				region.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
				textArea.setStyle("-fx-text-fill: #cccccc;");
			} catch(Exception e) {}
		}
		
		
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			appendText(input);
			goToEnd();
			inputStart = textArea.getText().length();
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

//  public void propertyChange(PropertyChangeEvent event) {
//    getPreferences();
//  }

  public String pushToHistory(TextArea text) {
//    StyledTextContent content = text.getText();
    String content = text.getText();
//    String command = content.substring(inputStart, content.length() - inputStart - 1);
    String command = content.substring(inputStart, content.length());
    history.add(command);
    return command;
  }

  public String recallFromHistoryBackward() {
    return history.getNext();
  }

  public String recallFromHistoryForward() {
    return history.getPrevious();
  }

  public void registerAsListener() {
  }

  public void registerWithPreferences() {
  }

  public void setOutput(PrintStream out) {
    this.out = out;
  }

  public void writeHistory(PrintStream out) {
    for (String command : history)
      out.print("<Command text='" + XModeler.encodeXmlAttribute(command) + "'/>");
  }

  public void addCommand(String command) {
    history.add(command);
  }
  
    private void addQuickCompleteItem(ContextMenu menu, String text) {
    	MenuItem item = new MenuItem("->" + text);
    	item.setOnAction(new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent e) {
    	    	insert(text);
    	    }
    	});
    }

  	private void completeArrow() {
  		final ContextMenu contextMenu = new ContextMenu();
  		addQuickCompleteItem(contextMenu, "asSeq");
  		addQuickCompleteItem(contextMenu, "asSet");
  		addQuickCompleteItem(contextMenu, "collect(element | exp)");
  		addQuickCompleteItem(contextMenu, "exists(element | condition)");
  		addQuickCompleteItem(contextMenu, "forall(element | condition)");
  		addQuickCompleteItem(contextMenu, "isEmpty");
  		addQuickCompleteItem(contextMenu, "reject(element | condition)");
  		addQuickCompleteItem(contextMenu, "select(element | condition)");
  		addQuickCompleteItem(contextMenu, "size");
  		
 
//    Point p = text.getCaret().getLocation();
//    Point displayPoint = text.toDisplay(p);
//    contextMenu.setLocation(displayPoint);
    
    contextMenu.show(XModeler.getStage());
  }

    public void dot(final Message message) {
    	Platform.runLater(() -> dot2(message));
    }
    
	public void dot2(final Message message) {
		try {
			String insertText = new AutoCompleteBox(owner, message).show(100, 100);
			if (insertText != null)
				insert(insertText);
		} catch (Throwable t) {
			t.printStackTrace();
		}
    }

	public void namespace(final Message message) {
		System.err.println("namespace...");
        try {
            for(int i = 0; i < message.args[0].values.length;i++) {
              String name = message.args[0].values[i].strValue();
              message.args[0].values[i].values = new Value[] {new Value(name),new Value(name)};
            }
            String insertText = new AutoCompleteBox(owner, message).show(100,100);
            if (insertText != null) insert(insertText);
          } catch (Throwable t) {
            t.printStackTrace();
          }
	}

//  public Menu getDotPopup(Message message) {
//    HashSet<String> labels = new HashSet<String>();
//    Value[] pairs = message.args[0].values;
//    for (Value value : pairs) {
//      Value[] pair = value.values;
//      String type = pair[0].strValue();
//      String label = pair[1].strValue();
//      labels.add(label);
//    }
//    Vector<String> sortedLabels = new Vector<String>(labels);
//    Collections.sort(sortedLabels);
//    if (labels.size() < 26)
//      return getShortDotPopup(sortedLabels);
//    else return getLongDotPopup(sortedLabels);
//  }

//  private Menu getLongDotPopup(Vector<String> labels) {
//    Menu mainMenu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
//    for (int i = 0; i < 26; i++) {
//      char c = (char) ('a' + i);
//      MenuItem nestedItem = new MenuItem(mainMenu, SWT.CASCADE);
//      nestedItem.setText("" + c);
//      Menu menu = new Menu(mainMenu);
//      nestedItem.setMenu(menu);
//      for (final String label : labels) {
//        if (label.charAt(0) == c || label.charAt(0) == (c + 26)) {
//          MenuItem item = new MenuItem(menu, SWT.NONE);
//          item.addSelectionListener(new SelectionListener() {
//            public void widgetDefaultSelected(SelectionEvent event) {
//            }
//
//            public void widgetSelected(SelectionEvent event) {
//              insert(label);
//            }
//          });
//          item.setText(label);
//        }
//      }
//      if (menu.getItemCount() == 0) nestedItem.dispose();
//    }
//    return mainMenu;
//  }

//  private Menu getShortDotPopup(Vector<String> labels) {
//    Menu menu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
//    for (final String label : labels) {
//      MenuItem item = new MenuItem(menu, SWT.NONE);
//      item.addSelectionListener(new SelectionListener() {
//        public void widgetDefaultSelected(SelectionEvent event) {
//        }
//
//        public void widgetSelected(SelectionEvent event) {
//          insert(label);
//        }
//      });
//      item.setText(label);
//    }
//    return menu;
//  }

//  private Menu getNameSpacePopup(Message message) {
//    HashSet<String> unsortedNames = new HashSet<String>();
//    Value[] names = message.args[0].values;
//    for (Value name : names) {
//      unsortedNames.add(name.strValue());
//    }
//    Vector<String> sortedNames = new Vector<String>(unsortedNames);
//    Collections.sort(sortedNames);
//    Menu menu = new Menu(XModeler.getXModeler(), SWT.POP_UP);
//    for (final String label : sortedNames) {
//      MenuItem item = new MenuItem(menu, SWT.NONE);
//      item.addSelectionListener(new SelectionListener() {
//        public void widgetDefaultSelected(SelectionEvent event) {
//        }
//
//        public void widgetSelected(SelectionEvent event) {
//          insert(label);
//        }
//      });
//      item.setText(label);
//    }
//    return menu;
//  }

//  public void namespaceOld(final Message message) {
//    // Replaced to be consistent with the '.' dialog...
//    XModeler.getXModeler().getDisplay().syncExec(new Runnable() {
//      public void run() {
//        try {
//          Menu menu = getNameSpacePopup(message);
//          Point p = text.getCaret().getLocation();
//          Point displayPoint = text.toDisplay(p);
//          menu.setLocation(displayPoint);
//          menu.setVisible(true);
//        } catch (Throwable t) {
//          t.printStackTrace();
//        }
//      }
//    });
//  }


}