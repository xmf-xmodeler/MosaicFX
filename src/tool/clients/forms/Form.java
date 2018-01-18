package tool.clients.forms;

import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import tool.clients.EventHandler;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class Form {

//  static Font                   labelFont               = new Font(XModeler.getXModeler().getDisplay(), new FontData("Courier New", 12, SWT.NONE));
//  final static Color            normalBackgroundColor   = new Color(org.eclipse.swt.widgets.Display.getCurrent(), 255, 255, 255);
//  final static Color            disabledBackgroundColor = new Color(org.eclipse.swt.widgets.Display.getCurrent(), 222, 221, 220);
//  final static Color            modifiedBackgroundColor = new Color(org.eclipse.swt.widgets.Display.getCurrent(), 255, 205, 194);                                           // RGB(221,171,160)
//  final static int              RIGHT_BUTTON            = 3;

  private String                        id;
  
  private ScrollPane                    form;
  private AnchorPane                          root;
  
  private Hashtable<String, TextField>        textFields          = new Hashtable<String, TextField>();
  private Hashtable<String, Label>            labels              = new Hashtable<String, Label>();
//  private Hashtable<String, ListView<String>> lists               = new Hashtable<String, ListView<String>>();
  private Hashtable<String, List> lists               = new Hashtable<String, List>();
  private Hashtable<String, TextArea>         boxes               = new Hashtable<String, TextArea>();
  private Hashtable<String, ComboBox<String>>         combos              = new Hashtable<String, ComboBox<String>>();
  private Hashtable<String, CheckBox>     	  checks              = new Hashtable<String, CheckBox>();
  private Hashtable<String, Button>           buttons             = new Hashtable<String, Button>();
  private Hashtable<String, TreeView<String>>         trees               = new Hashtable<String, TreeView<String>>();
  private Hashtable<String, TreeItem<String>>         items               = new Hashtable<String, TreeItem<String>>();
  private Hashtable<String, String>           images              = new Hashtable<String, String>();

//  private int                   TEXTFIELDHEIGHT         = 20;

  public Form(Tab parent, String id) {
	System.err.println("new Form(): " + id + " on " + parent);
    this.id = id;
    form = new ScrollPane();
    root = new AnchorPane();
    form.setContent(root);
    form.setFitToHeight(true);
    form.setFitToWidth(true); 
    
    parent.setContent(form);
    
  }  
  
    @Override
	public String toString() {
		return "Form [id=" + id + " " + root.getHeight() + " of " +form.getHeight() + "]";
	}
  
  ///////////////////////// SETUP ( ADD NEW ELEMENTS) //////////////////////////
  

public void newButton(String parentId, String id, String label, int x, int y, int width, int height) {
	    if (this.id.equals(parentId)) {
	      Button button = new Button();
	      
	      javafx.event.EventHandler<ActionEvent> eh = new javafx.event.EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	        if (event.getSource() instanceof Button) {
	    	        	Button es = (Button) event.getSource();
	    	            if (button == es) {
	    	            	System.err.println("buttonPressed: " + id);
	    	                String id = getId(button);
	    	                EventHandler handler = FormsClient.theClient().getHandler();
	    	                Message message = handler.newMessage("buttonPressed", 1);
	    	                message.args[0] = new Value(id);
	    	                handler.raiseEvent(message);
	    	            }
	    	        }
	    	    }
	    	};

	      button.setOnAction(eh);
	      
	      if (label.startsWith("Maximi")) {
	        width += 16;
	        x -= 16;
	      }
	      AnchorPane.setLeftAnchor(button, x*1.);
	      AnchorPane.setTopAnchor(button, y*1.);
	      button.setText(label);
	      buttons.put(id, button);
	      root.getChildren().add(button);
	    }
	  }

	  public void newCheckBox(String parentId, final String id, int x, int y, boolean checked) {
	    if (this.id.equals(parentId)) {
	      final CheckBox box = new CheckBox();
	      	      
	      javafx.event.EventHandler<ActionEvent> eh = new javafx.event.EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	        if (event.getSource() instanceof CheckBox) {
	    	            CheckBox chk = (CheckBox) event.getSource();
	    	            if (box == chk) {
	    	            	xmf_setSelection(id, chk.isSelected());
	    	            }
	    	        }
	    	    }
	    	};

	      box.setOnAction(eh);
	      
	      AnchorPane.setLeftAnchor(box, x*1.);
	      AnchorPane.setTopAnchor(box, y*1.);
	      box.setSelected(checked);
	      box.setText("");
	      checks.put(id, box);
	      root.getChildren().add(box);
	    }
	  }

	  public void newComboBox(String parentId, String id, int x, int y, int width, int height) {
	    if (this.id.equals(parentId)) {
	      ComboBox<String> combo = new ComboBox<String>();
	      AnchorPane.setLeftAnchor(combo, x*1.);
	      AnchorPane.setTopAnchor(combo, y*1.);
//	      combo.setSize(150, 25);
//	      combo.setFont(FormsClient.formLabelFont);
	      combos.put(id, combo);
	      root.getChildren().add(combo);
	    }
	  }

	public void newList(String parentId, final String id, final int x, final int y, final int width, final int height) {
		if (this.id.equals(parentId)) {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
//				ListView<String> list = new ListView<String>();
				List list = new List(id, root, x, y, width, height);
				lists.put(id, list);
//			    AnchorPane.setLeftAnchor(list, x*1.);
//			    AnchorPane.setTopAnchor(list, y*1.);
//				root.getChildren().add(list);
                l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	  }

	  public void newNodeWithIcon(String parentId, String nodeId, String text, boolean editable, String icon, int index) {
	    if (trees.containsKey(parentId))
	      addRootNodeWithIcon(parentId, nodeId, text, editable, false, icon, index);
	    else addNodeWithIcon(parentId, nodeId, text, editable, false, icon, index);
	  }

	  public void newText(String id, String string, int x, int y) {

	    if (string.trim().isEmpty()) return;
	    Label text = new Label();
	    text.setText(string);
	    AnchorPane.setLeftAnchor(text, x*1.);
	    AnchorPane.setTopAnchor(text, y*1.);
//	    text.addMouseListener(this);
	    labels.put(id, text);
//	    form_OLD.setMinSize(content_OLD.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	    root.getChildren().add(text);
	  }

	  public void newTextBox(String parentId, String id, int x, int y, int width, int height, boolean editable) {
	    if (this.id.equals(parentId)) {
	      final TextArea text = new TextArea();
//	      text.setFont(FormsClient.formLabelFont);
	      AnchorPane.setLeftAnchor(text, x*1.);
	      AnchorPane.setTopAnchor(text, y*1.);
//	      text.setLocation(x, y + 10); // wrong y value received in the one and only call
//	      text.setSize(width, height);
	      text.setEditable(editable);
//	      text.addMouseListener(this);
//	      { // set Monospace Font
//	        FontData[] fontData = org.eclipse.swt.widgets.Display.getDefault().getSystemFont().getFontData();
//	        XModeler.getXModeler().getDisplay().loadFont("dejavu/DejaVuSansMono.ttf");
//	        fontData[0].setName("DejaVu Sans Mono");
//	        text.setFont(new Font(XModeler.getXModeler().getDisplay(), fontData));
//	        System.err.println("Set font: " + id + "/" + text);
//	      }
//	      text.addModifyListener(new ModifyListener() {
//	        @Override
//	        public void modifyText(ModifyEvent m) {
//	          text.setBackground(modifiedBackgroundColor);
//	        }
//	      });
	      boxes.put(id, text);
		  root.getChildren().add(text);
	    }
	  }

	  public void newTextField(final String id, int x, int y, int width, int height, boolean editable) {
	    final TextField text = new TextField();
	    text.setEditable(editable);
	    
//	    text.setBounds(x, y, width, (TEXTFIELDHEIGHT * XModeler.getDeviceZoomPercent()) / 100 + 1);
	    // text.setLocation(x, y);
//	    text.addMouseListener(this);
	    textFields.put(id, text);
	    AnchorPane.setLeftAnchor(text, x*1.);
	    AnchorPane.setTopAnchor(text, y*1.);
		root.getChildren().add(text);
		
		text.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean wasFocused, Boolean isFocused) {
		        if (!isFocused) {
		            System.out.println("Textfield out focus");
		            textChangedEvent(id, text.getText());
		        }
		    }
		});
		
        text.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
	  		@Override public void handle(MouseEvent click) {
	  		    if (click.getClickCount() == 2) {
	  		    	System.err.println("text.setOnMouseClicked: " + click.getClickCount());
	  		    	doubleClick(text);
	      }}});
		
//	    Listener listener = new Listener() {
//	      public void handleEvent(Event event) {
//	        switch (event.type) {
//	          case SWT.FocusOut:
//	            textChangedEvent(id, text.getText());
//	            break;
//	          case SWT.Traverse:
//	            switch (event.detail) {
//	              case SWT.TRAVERSE_RETURN:
//	              case SWT.TRAVERSE_ESCAPE:
//	                textChangedEvent(id, text.getText());
//	                break;
//	            }
//	        }
//	      }
//	    };
//	    text.addListener(SWT.FocusOut, listener);
//	    text.addListener(SWT.Verify, listener);
//	    text.addListener(SWT.Traverse, listener);
	  }

	  public void newTree(String parentId, String id, int x, int y, int width, int height, boolean editable) {
	    if (this.id.equals(parentId)) {
	      TreeView<String> tree = new TreeView<String>();
	      tree.setPrefSize(width, height);
	      AnchorPane.setLeftAnchor(tree, x*1.);
	      AnchorPane.setTopAnchor(tree, y*1.);
		  root.getChildren().add(tree);
	      trees.put(id, tree);
	      
	      tree.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
	  		@Override public void handle(MouseEvent click) {
	  		    if (click.getClickCount() == 2 && click.getButton() == MouseButton.PRIMARY) {
	  		    	doubleClick(tree.getSelectionModel().getSelectedItem());
	  		    } else if (click.getClickCount() == 1 && click.getButton() == MouseButton.SECONDARY){ 
	  		    	TreeItem<String> item = tree.getSelectionModel().getSelectedItem();
	  		    	if(item == null) return;
	  		    	String itemId = getId(item);
	  		    	if(itemId == null) return;
	  		    	MenuClient.popup(itemId, tree, Side.RIGHT, (int)click.getSceneX(), (int)click.getSceneY());
	  		 }
	  		}});}

	  }
	  
  public void addComboItem(String comboId, String value) {
    if (combos.containsKey(comboId)) {
      combos.get(comboId).getItems().add(value);
    }
  } 
  
  public void addListItem(String parentId, String id, String value) {
    for (String listId : lists.keySet()) {
        if (listId.equals(parentId)) lists.get(listId).add(id, value);
//        if (listId.equals(parentId)) lists.get(listId).add(id, value);
    }
  }
  
  private void addNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable, final boolean expanded, final String icon, final int index) {
    if (items.containsKey(parentId)) {
      TreeItem<String> parent = items.get(parentId);
      String iconFile = "icons/" + icon;
	  ImageView image = new ImageView(new Image(new File(iconFile).toURI().toString()));
      TreeItem<String> item = new TreeItem<String>(text, image);
      images.put(nodeId, icon);
      items.put(nodeId, item);
      item.setExpanded(expanded);
      parent.getChildren().add((index == -1) ? parent.getChildren().size() : index, item);
      
    } //else System.err.println("Cannot find node " + parentId);
  }  
  
  private void addRootNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable, final boolean expanded, final String icon, final int index) {
	    String iconFile = "icons/" + icon;
	    ImageView image = new ImageView(new Image(new File(iconFile).toURI().toString()));
	    TreeView<String> tree = trees.get(parentId);
	    TreeItem<String> item = new TreeItem<String>(text, image);
	    images.put(nodeId, icon);
	    items.put(nodeId, item);
	    item.setExpanded(expanded);
	    tree.setRoot(item);
	  }
  
  ///////////////////////// UI ELEMENT VALUE SETTERS ////////////////////////// 
  
  public void check(String id) {
    for (String bid : checks.keySet())
      if (bid.equals(id) && !checks.get(id).isSelected()) checks.get(id).setSelected(true);
  }
  
  public void uncheck(String id) {
    for (String bid : checks.keySet())
      if (bid.equals(id) && checks.get(id).isSelected()) checks.get(id).setSelected(false);
  }

  public void setSelection(String comboId, int index) {
    for (String id : combos.keySet()) {
      if (id.equals(comboId)) {
        combos.get(id).getSelectionModel().select(index);
      }
    }
  }

  public void setText(String id, String string) {
    if (textFields.containsKey(id)) {
      TextField text = textFields.get(id);
      text.setText(string);
    }
    if (boxes.containsKey(id)) {
      TextArea text = boxes.get(id);
      text.setText(string);
//      text.setBackground(normalBackgroundColor);
      // text.pack();
    }
    if (items.containsKey(id)) {
      TreeItem<String> item = items.get(id);
      item.setValue(string);
    }
  }

  
  ///////////////////////// OTHER STUFF ////////////////////////// 
  

  public void clear() {
	System.err.println("# # # clear all");
    labels.clear();
    textFields.clear();
    lists.clear();
    boxes.clear();
    combos.clear();
    checks.clear();
    buttons.clear();
    trees.clear();
    items.clear();
    images.clear();
    
    root.getChildren().clear();
//    for (Control child : content_OLD.getChildren())
//      child.dispose();
  }

  public void clear(String id) {
    if (getId().equals(id))
      clear();
    else {
      if (lists.containsKey(id)) {
        List l = lists.get(id);
        l.clear();
      }
    }
  }

  private void iHaventImplementedItYet() {
	new RuntimeException("I haven't implemented that yet:").printStackTrace();
}

	private void doubleClick(TreeItem<String> item) {
		String id = getId(item);
		Message m = FormsClient.theClient().getHandler().newMessage("doubleSelected", 1);
		m.args[0] = new Value(id);
		FormsClient.theClient().getHandler().raiseEvent(m);
	}

	private void doubleClick(TextField item) {
		String id = getId(item);
		Message m = FormsClient.theClient().getHandler().newMessage("doubleSelected", 1);
		try {
			m.args[0] = new Value(id);
			FormsClient.theClient().getHandler().raiseEvent(m);
		} catch (Exception e) {
			System.err.println("Double click into nowhere detected...");
		}
	}

  public Hashtable<String, TextArea> getBoxes() {
    return boxes;
  }

  public Hashtable<String, Button> getButtons() {
    return buttons;
  }

  public Hashtable<String, CheckBox> getChecks() {
    return checks;
  }

  public Hashtable<String, ComboBox<String>> getCombos() {
    return combos;
  }
//
//  public ScrolledComposite getForm() {
//    return form_OLD;
//  }
//
  public String getId() {
    return id;
  }

  private String getId(Button b) {
    for (String id : buttons.keySet())
      if (buttons.get(id) == b) return id;
    return null;
  }
//
//  private String getId(ComboBox<String> c) {
//    for (String id : combos.keySet())
//      if (combos.get(id) == c) return id;
//    return null;
//  }
//
//  private String getId(TextArea item) {
//    for (String id : boxes.keySet())
//      if (boxes.get(id) == item) return id;
//    return null;
//  }
//
  private String getId(TextField item) {
    for (String id : textFields.keySet())
      if (textFields.get(id) == item) return id;
    return null;
  }

  private String getId(TreeItem<String> item) {
    for (String id : items.keySet())
      if (items.get(id) == item) return id;
    return null;
  }

  public Hashtable<String, TreeItem<String>> getItems() {
    return items;
  }

  public Hashtable<String, Label> getLabels() {
    return labels;
  }

  public Hashtable<String, List> getLists() {
    return lists;
  }

  public Hashtable<String, TextField> getTextFields() {
    return textFields;
  }

  public Hashtable<String, TreeView<String>> getTrees() {
    return trees;
  }
//
//  private boolean isCommand(MouseEvent event) {
//    return (event.stateMask & SWT.COMMAND) != 0;
//  }
//
//  private boolean isRightClick(MouseEvent event) {
//    return event.button == RIGHT_BUTTON;
//  }

	public void move(String id, int x, int y) {

//		  System.err.println("move +");
//		CountDownLatch l = new CountDownLatch(1);
//		Platform.runLater(() -> {
			if (combos.containsKey(id)) {
				AnchorPane.setLeftAnchor(combos.get(id), 1. * x);
				AnchorPane.setTopAnchor(combos.get(id), 1. * y);
				return;
			}
			if (textFields.containsKey(id)) {
				AnchorPane.setLeftAnchor(textFields.get(id), 1. * x);
				AnchorPane.setTopAnchor(textFields.get(id), 1. * y);
				return;
			}
			if (labels.containsKey(id)) {
				AnchorPane.setLeftAnchor(labels.get(id), 1. * x);
				AnchorPane.setTopAnchor(labels.get(id), 1. * y);
				return;
			}
			if (checks.containsKey(id)) {
				AnchorPane.setLeftAnchor(checks.get(id), 1. * x);
				AnchorPane.setTopAnchor(checks.get(id), 1. * y);
				return;
			}
			if (buttons.containsKey(id)) {
				AnchorPane.setLeftAnchor(buttons.get(id), 1. * x);
				AnchorPane.setTopAnchor(buttons.get(id), 1. * y);
				return;
			}
			if (boxes.containsKey(id)) {
				AnchorPane.setLeftAnchor(boxes.get(id), 1. * x);
				AnchorPane.setTopAnchor(boxes.get(id), 1. * y);
				return;
			}
			if (trees.containsKey(id)) {
				AnchorPane.setLeftAnchor(trees.get(id), 1. * x);
				AnchorPane.setTopAnchor(trees.get(id), 1. * y);
				return;
			}
			if (lists.containsKey(id))
				throw new RuntimeException("The move()-operation for List is not yet implemented...");
			if (items.containsKey(id))
				throw new RuntimeException("The move()-operation for TreeItem is not yet implemented...");
			if (images.containsKey(id))
				throw new RuntimeException("The move()-operation for String/Image is not yet implemented...");
			System.err.println("move: " + id);
			// throw new RuntimeException("The move()-operation for this type of
			// Display is not yet implemented...");

			// These Displays need to be added:
			// Hashtable<String, List> lists = new Hashtable<String, List>();
			// Hashtable<String, TreeItem> items = new Hashtable<String,
			// TreeItem>();
			// Hashtable<String, String> images = new Hashtable<String,
			// String>();
//			l.countDown();
//		});
//		try {
//			l.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

//		  System.err.println("move -");
	}

  public void setSize(String id, double width, double height) {
//	  System.err.println("setSize +");
//		CountDownLatch l = new CountDownLatch(1);
//		Platform.runLater(() -> {
        if (combos.containsKey(id)) {
          combos.get(id).setPrefSize(width, height);
          return;
        }
        if (textFields.containsKey(id)) {
          textFields.get(id).setPrefSize(width, height);
          return;
        }
        if (labels.containsKey(id)) {
          labels.get(id).setPrefSize(width, height);
          return;
        }
        if (checks.containsKey(id)) {
          checks.get(id).setPrefSize(width, height);
          return;
        }
        if (buttons.containsKey(id)) {
          buttons.get(id).setPrefSize(width, height);
          return;
        }
        if (boxes.containsKey(id)) {
          boxes.get(id).setPrefSize(width, height);
          return;
        }
        if (trees.containsKey(id)) {
          trees.get(id).setPrefSize(width, height);
          return;
        }
        if (lists.containsKey(id)) throw new RuntimeException("The setSize()-operation for List is not yet implemented...");
        if (items.containsKey(id)) throw new RuntimeException("The setSize()-operation for TreeItem is not yet implemented...");
        if (images.containsKey(id)) throw new RuntimeException("The setSize()-operation for String/Image is not yet implemented...");
        throw new RuntimeException("The setSize()-operation for this type of Display is not yet implemented...");

        // These Displays need to be added:
        // Hashtable<String, List> lists = new Hashtable<String, List>();
        // Hashtable<String, TreeItem> items = new Hashtable<String, TreeItem>();
        // Hashtable<String, String> images = new Hashtable<String, String>();
//        l.countDown();
//	    });
//	    try {
//	     l.await();
//	    } catch (InterruptedException e) {
//	     e.printStackTrace();
//	    }
//
//		  System.err.println("setSize -");
	  }

//  public void mouseDoubleClick(MouseEvent event) {
//    Widget widget = event.widget;
//    if (widget instanceof Tree) {
//      Tree tree = (Tree) widget;
//      if (tree.getSelectionCount() == 1) {
//        TreeItem item = tree.getSelection()[0];
//        doubleClick(item);
//      }
//    }
//    if (widget instanceof Text) {
//      Text text = (Text) widget;
//      doubleClick(text);
//    }
//  }

//  public void mouseDown(MouseEvent event) {
//    String id = null;
//    boolean isRightClick = isRightClick(event);
//    boolean isCommand = isCommand(event);
//
//    Widget w = event.widget;
//    if (w instanceof StyledText) id = getId((StyledText) w);
//    if (w instanceof Text) id = getId((Text) w);
//    if (w instanceof Tree) {
//      Tree tree = (Tree) w;
//      if (tree.getSelectionCount() == 1) {
//        TreeItem item = tree.getSelection()[0];
//        id = getId(item);
//      }
//    }
//    if (id != null) {
//      if (isRightClick || isCommand)
//        MenuClient.popup(id, event.x, event.y);
//      else select(id);
//    }
//  }

  private void select(String id) {
    EventHandler handler = FormsClient.theClient().getHandler();
    Message m = FormsClient.theClient().getHandler().newMessage("selected", 1);
    m.args[0] = new Value(id);
    handler.raiseEvent(m);
  }

  public void maximiseToCanvas(String id) {
    TreeView<String> tree = trees.get(id);
    if (tree != null) {
//      org.eclipse.swt.graphics.Point parentSize = tree.getParent().w
//      tree.setSize(parentSize);
    	double GAP = 2.;
	      AnchorPane.setLeftAnchor(tree, GAP);
	      AnchorPane.setTopAnchor(tree, GAP);
	      AnchorPane.setRightAnchor(tree, GAP);
	      AnchorPane.setBottomAnchor(tree, GAP);

    }
  }
//
  public void changesMade(String id, boolean made) {
	  System.err.println("changesMade//iHaventImplementedItYet();");
//    StyledText text = boxes.get(id);
//    if (text != null) {
//      text.setBackground(made ? modifiedBackgroundColor : normalBackgroundColor);
//    }
  }

//  private void selected(Button b) {
//    String id = getId(b);
//    EventHandler handler = FormsClient.theClient().getHandler();
//    Message message = handler.newMessage("buttonPressed", 1);
//    message.args[0] = new Value(id);
//    handler.raiseEvent(message);
//  }
//
//  private void selected(CCombo c) {
//    String id = getId(c);
//    EventHandler handler = FormsClient.theClient().getHandler();
//    Message message = handler.newMessage("comboBoxSelection", 2);
//    message.args[0] = new Value(id);
//    message.args[1] = new Value(c.getItem(c.getSelectionIndex()));
//    handler.raiseEvent(message);
//  }
//
  private void xmf_setSelection(String id, boolean state) {
    EventHandler handler = FormsClient.theClient().getHandler();
    Message message = handler.newMessage("setBoolean", 2);
    message.args[0] = new Value(id);
    message.args[1] = new Value(state);
    handler.raiseEvent(message);
  }


  public void textChangedEvent(String id, String text) {
    Message message = FormsClient.theClient().getHandler().newMessage("textChanged", 2);
    message.args[0] = new Value(id);
    message.args[1] = new Value(text);
    FormsClient.theClient().getHandler().raiseEvent(message);
  }

//  public void widgetSelected(SelectionEvent event) {
//    Widget w = event.widget;
//    if (w instanceof Button) {
//      Button b = (Button) w;
//      selected(b);
//    }
//    if (w instanceof CCombo) {
//      CCombo c = (CCombo) w;
//      selected(c);
//    }
//  }

  public void writeXML(PrintStream out, boolean selected, String formLabel) {
//	  iHaventImplementedItYet();
    out.print("<Form id='" + getId() + "' selected='" + selected + "' label='" + XModeler.encodeXmlAttribute(formLabel) + "'>");
    for (String id : textFields.keySet()) {
      TextField field = textFields.get(id);
      out.print("<TextField id='" + id + "'");
      out.print(" string='" + XModeler.encodeXmlAttribute(field.getText()) + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(field).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(field).intValue() + "'");
      out.print(" width='" + (int)field.getWidth() + "'");
      out.print(" height='" + (int)field.getHeight() + "'");
      out.print(" editable='" + field.isEditable() + "'/>");
    }
    for (String id : labels.keySet()) {
      Label label = labels.get(id);
      out.print("<Label id='" + id + "'");
      out.print(" string='" + XModeler.encodeXmlAttribute(label.getText()) + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(label).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(label).intValue() + "'/>");
    }
    for (List list : lists.values())
      list.writeXML(out);
    out.print("</Form>");
    for (String id : boxes.keySet()) {
      TextArea box = boxes.get(id);
      out.print("<TextBox id='" + id + "'");
      out.print(" string='" + XModeler.encodeXmlAttribute(box.getText()) + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(box).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(box).intValue() + "'");
      out.print(" width='" + (int)box.getWidth() + "'");
      out.print(" height='" + (int)box.getHeight() + "'");
      out.print(" editable='" + box.isEditable() + "'/>");
    }
    for (String id : combos.keySet()) {
      ComboBox<String> combo = combos.get(id);
      out.print("<Combo id='" + id + "'");
      out.print(" string='" + XModeler.encodeXmlAttribute(combo.getValue()) + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(combo).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(combo).intValue() + "'");
      out.print(" width='" + (int)combo.getWidth() + "'");
      out.print(" height='" + (int)combo.getHeight() + "'");
      out.print(" editable='" + combo.isEditable() + "'>");
      for (String value : combo.getItems())
        out.print("<Item item='" + XModeler.encodeXmlAttribute(value) + "'/>");
      out.print("</Combo>");
    }
    for (String id : checks.keySet()) {
      out.print("<Check id='" + id + "'");
      out.print(" checked='" + checks.get(id).isSelected() + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(checks.get(id)).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(checks.get(id)).intValue() + "'");
      out.print(" text='" + XModeler.encodeXmlAttribute(checks.get(id).getText()) + "'/>");
    }
    for (String id : buttons.keySet()) {
      out.print("<Button id='" + id + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(buttons.get(id)).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(buttons.get(id)) + "'");
      out.print(" width='" + (int)buttons.get(id).getWidth() + "'");
      out.print(" height='" + (int)buttons.get(id).getHeight() + "'");
      out.print(" text='" + XModeler.encodeXmlAttribute(buttons.get(id).getText()) + "'/>");
    }
    for (String id : trees.keySet()) {
      TreeView<String> tree = trees.get(id);
      out.print("<Tree id='" + id + "'");
      out.print(" x='" + AnchorPane.getLeftAnchor(trees.get(id)).intValue() + "'");
      out.print(" y='" + AnchorPane.getTopAnchor(trees.get(id)).intValue() + "'");
      out.print(" width='" + (int)trees.get(id).getWidth() + "'");
      out.print(" height='" + (int)trees.get(id).getHeight() + "'");
      out.print(" editable='true'>");
      writeXMLTreeItem(tree.getRoot(), out);
      out.print("</Tree>");
    }
  }

  private void writeXMLTreeItem(TreeItem<String> item, PrintStream out) {
	  
//	    for (TreeItem item : children) {
	      String id = null;
	      for (String itemId : items.keySet()) {
	        if (items.get(itemId) == item) id = itemId;
	      }
	      if (id == null) System.err.println("error: cannot find tree item " + item);
	      String icon = images.get(id);
	      out.print("<Item id='" + id + "' "
	      		+ "text='" + XModeler.encodeXmlAttribute(item.getValue()) + "' "
	      		+ "image='" + icon + "' "
	      		+ "expanded='" + item.isExpanded() + "'>");
	      for (TreeItem<String> child : item.getChildren()) {
	    	  writeXMLTreeItem(child, out);
	      }
	      out.print("</Item>");
//	    }
	  }
  
  
//  private void writeXMLTreeItems(TreeItem<String> children, PrintStream out) {
//    for (TreeItem item : children) {
//      String id = null;
//      for (String itemId : items.keySet()) {
//        if (items.get(itemId) == item) id = itemId;
//      }
//      if (id == null) System.err.println("error: cannot find tree item " + item);
//      String icon = images.get(id);
//      out.print("<Item id='" + id + "' text='" + XModeler.encodeXmlAttribute(item.getText()) + "' image='" + icon + "' expanded='" + item.getExpanded() + "'>");
//      writeXMLTreeItems(item.getItems(), out);
//      out.print("</Item>");
//    }
//  }

  public String getText(String id) {
    if (boxes.containsKey(id))
      return boxes.get(id).getText();
    else return null;
  }

  public void removeItem(String id) {
	  iHaventImplementedItYet();
//    if (items.containsKey(id)) {
//      TreeItem ti = items.get(id);
//      ti.dispose();
//    }
  }

  public void delete(String id) {
	  iHaventImplementedItYet();
//    FormsClient.theClient().runOnDisplay(new Runnable() {
//      public void run() {
//        if (combos.containsKey(id)) {
//          combos.get(id).dispose();
//          combos.remove(id);
//        } else if (textFields.containsKey(id)) {
//          textFields.get(id).dispose();
//          textFields.remove(id);
//        } else if (labels.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (checks.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (buttons.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (boxes.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (trees.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (lists.containsKey(id)) {
//          lists.get(id).list.dispose();
//          lists.remove(id);
//        } else if (items.containsKey(id)) {
//          items.get(id).dispose();
//          items.remove(id);
//        } else if (images.containsKey(id)) {
//          images.remove(id);
//        } else System.err.println("Cannot delete " + id);
//      }
//    });
  }
}