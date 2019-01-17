package tool.clients.dialogs;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import tool.clients.Client;
import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.xmodeler.PropertyManager;
//import tool.clients.dialogs.notifier.NotificationType;
//import tool.clients.dialogs.notifier.NotifierDialog;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class DialogsClient extends Client {

	  static DialogsClient theClient;

	  static Cursor        cursor = null;
	  static File 		   lastFile = new File(System.getProperty("user.home"));
	
//  public static String chooseFont() {
//    final String[] result = new String[1];
//    DialogsClient.theClient().runOnDisplay(new Runnable() {
//      public void run() {
//        FontDialog dialog = new FontDialog(XModeler.getXModeler());
//        FontData data = dialog.open();
//        if (data != null)
//          result[0] = data.toString();
//        else result[0] = "";
//      }
//    });
//    return result[0];
//  }

  private static boolean containedInDefault(String string, String[] defaults) {
    for (int i = 0; i < defaults.length; i++) {
      String def = defaults[i];
      if (def.equals(string)) return true;
    }
    return false;
  }

  private static int countAllOptions(Object[] declaredOptions) {
    int count = 0;
    for (int i = 0; i < declaredOptions.length; i++) {
      String option = (String) declaredOptions[i];
      if (option.startsWith("!")) count++;
    }
    return count;
  }

  private static Value[] getResultArray(Object[] strings, String[] defaults) {
    Value[] values = new Value[strings.length];
    for (int i = 0; i < strings.length; i++) {
      String string = (String) strings[i];
      if (containedInDefault(string, defaults)) string = "!" + string;
      values[i] = new Value(string);
    }
    return values;
  }

  public static Value newInputDialog(final String title, final String message, final String value) {
    final String[] result = new String[] { null };
	 CountDownLatch l = new CountDownLatch(1);
//    DialogsClient.theClient().runOnDisplay(
    Platform.runLater(()->{
//    		new Runnable() {
//      public void run() {
          final TextInputDialog inputDlg = new TextInputDialog(value);
          inputDlg.setTitle(title);
          inputDlg.setContentText(message);
          inputDlg.setHeaderText(title);
           inputDlg.showAndWait();
    	  
//    	InputDialog dialog = new InputDialog(XModeler.getXModeler(), title, message, value, null);
//        dialog.open();
        //if (dialog.getValue() != null && !dialog.getValue().equals(""))
           
        result[0] = inputDlg.getResult(); //dialog.getValue();
        l.countDown();
//      }
    });
    try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    return result[0] != null 
    		? new Value(result[0]) 
    		: new Value(false);
  }

  private static String[] objectsToStrings(Value[] options) {
    if (options == null) {
      return new String[0];
    } else {
      String[] stringOptions = new String[options.length];
      for (int i = 0; i < options.length; i++) {
        stringOptions[i] = (options[i]).strValue();
      }
      return stringOptions;
    }
  }

//  public static Value openMultiSelectionDialog(String title, String message, Value[] options) {
//    String[] stringOptions = objectsToStrings(options);
//    String[] allOptions = processAllOptions(stringOptions);
//    String[] defaultOptions = processDefaultOptions(stringOptions);
//    Shell shell = XModeler.getXModeler();
//    ListDialog ld = new ListDialog(shell);
//    ld.setInput(allOptions);
//    ld.setContentProvider(new ArrayContentProvider());
//    ld.setLabelProvider(new LabelProvider());
//    ld.setMessage(message);
//    ld.setTitle(title);
//    ld.setInitialSelections(defaultOptions);
//    if (ld.open() != SWT.CANCEL) {
//      Object[] result = ld.getResult();
//      if (result != null && result.length > 0) return new Value(getResultArray(result, defaultOptions));
//    }
//    return new Value("-1");
//  }
//
//  public static Value openSelectionDialog(String title, String message, Value[] options) {
//    String[] stringOptions = objectsToStrings(options);
//    String[] allOptions = processAllOptions(stringOptions);
//    String[] defaultOptions = processDefaultOptions(stringOptions);
//    Shell shell = XModeler.getXModeler();
//    ListDialog ld = new ListDialog(shell);
//    ld.setInput(allOptions);
//    ld.setContentProvider(new ArrayContentProvider());
//    ld.setLabelProvider(new LabelProvider());
//    ld.setMessage(message);
//    ld.setTitle(title);
//    ld.setInitialSelections(defaultOptions);
//    if (ld.open() != SWT.CANCEL) {
//      Object[] result = ld.getResult();
//      if (result != null && result.length > 0) return getResultArray(result, defaultOptions)[0];
//    }
//    return new Value("");
//  }

//  public static String[] orderingDialog(final String title, final String question, final String[] strings) {
//    final Object[] result = new Object[] { null };
//    DialogsClient.theClient().runOnDisplay(new Runnable() {
//      public void run() {
//        OrderingDialog d = new OrderingDialog(XModeler.getXModeler(), title, question, strings);
//        if (d.open() != SWT.CANCEL)
//          result[0] = d.getChoice();
//        else result[0] = new String[0];
//      }
//    });
//    return (String[]) result[0];
//  }

  private static String[] processAllOptions(String[] declaredOptions) {
    if (declaredOptions == null) {
      return new String[0];
    } else {
      int oi = 0;
      String[] options = new String[declaredOptions.length];
      for (int i = 0; i < declaredOptions.length; i++) {
        String option = declaredOptions[i];
        if (option.startsWith("!"))
          options[oi++] = option.substring(1, option.length());
        else options[oi++] = declaredOptions[i];
      }
      return options;
    }
  }

  private static String[] processDefaultOptions(String[] declaredOptions) {
    if (declaredOptions == null) {
      return new String[0];
    } else {
      int oi = 0;
      String[] setOptions = new String[countAllOptions(declaredOptions)];
      for (int i = 0; i < declaredOptions.length; i++) {
        String option = declaredOptions[i];
        if (option.startsWith("!")) setOptions[oi++] = option.substring(1, option.length());
      }
      return setOptions;
    }
  }

  public static DialogsClient theClient() {
    return theClient;
  }

  public DialogsClient() {
	    super("com.ceteva.dialogs");
    theClient = this;
  }

  public Value callMessage(Message message) {
	if (message.hasName("newColorDialog") && message.arity == 4)
	  return colorDialog(message); 
	else if (message.hasName("newQuestionDialog"))
      return newQuestionDialog(message); 
    else if (message.hasName("newQuestionDialogYesNoCancel"))
        return newQuestionDialogYesNoCancel(message); 
    else if (message.hasName("newQuestionDialogYesOnly"))
        return newQuestionDialogYesOnly(message); 
    else if (message.hasName("newDirectoryDialog"))
      return newDirectoryDialog(message); 
    else if (message.hasName("newFileDialog"))
      return newFileDialog(message); 
    else if (message.hasName("newInputDialog"))
      return newInputDialog(message); 
    else if (message.hasName("newSelectionDialog"))
      return selectionDialog(message); 
    else if (message.hasName("newConfirmDialog"))
        return newConfirmDialog(message); 
    else if (message.hasName("newTreeDialog"))
        return simpleTreeDialog(message); 
    else if (message.hasName("newPropertyDialog")) {
    	return newPropertyDialog();}
    else return super.callMessage(message);
  }

  private Value newPropertyDialog() {
	  
	  		
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) { 
			// we are on the right Thread already:
			XModeler.getPropertyManager().getInterface();
		} else { // create a new Thread
//			System.err.println("Calling redraw from " + Thread.currentThread());
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
//				System.err.println("Doing redraw for " + Thread.currentThread());
				XModeler.getPropertyManager().getInterface();
	    		l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Value("");
  }
  
  
	private void newBusyDialog(final Message message) {
		// runOnDisplay(new Runnable() {
		// public void run() {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			// Value id = message.args[0];
			Value info = message.args[1];
			// Value ignore = message.args[2];
			XModeler.showBusyInformation(info.strValue());
			// Cursor busy = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);

			cursor = XModeler.getStage().getScene().getCursor(); // XModeler.getXModeler().getCursor();
			XModeler.getStage().getScene().setCursor(Cursor.WAIT);
			// XModeler.getXModeler().setCursor(busy);
			l.countDown();
			// }
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

  private Value newDirectoryDialog(final Message message) {
    final Value[] result = new Value[1];
	CountDownLatch l = new CountDownLatch(1);
//    runOnDisplay(
    	Platform.runLater(()-> {
    	  String path = message.args[0].strValue();  
    	  
    	  DirectoryChooser directoryChooser = new DirectoryChooser();
    	  //PropertyManager pm = XModeler.getPropertyManager();
    	  //String pmPath = pm.getStringProperty("directoryDialogPath", path);
    	  
    	  File initDirectory = new File(path);
    	  if (initDirectory.exists()){
    		  directoryChooser.setInitialDirectory(initDirectory);
    	  }else{
    		  directoryChooser.setInitialDirectory(lastFile.getParentFile());
    	  }
          File selectedDirectory = 
                  directoryChooser.showDialog(XModeler.getStage());
          
          if(selectedDirectory == null){
        	  result[0] = new Value("");
          }else{
        	  lastFile = selectedDirectory;
        	  //pm.setStringProperty("directoryDialogPath", selectedDirectory.getParent());
        	  result[0] = new Value(selectedDirectory.getAbsolutePath());
          }  
        l.countDown();
//        DirectoryDialog dialog = new DirectoryDialog(XModeler.getXModeler());
//        if (new File(path).exists()) dialog.setFilterPath(path);
//        path = dialog.open();
//        path = path == null ? "" : path;
//        result[0] = new Value(path);
    });
    try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    return result[0];
  }

  private Value newFileDialog(final Message message) {
    final Value[] result = new Value[] { new Value("") };
	   CountDownLatch l = new CountDownLatch(1);
//    runOnDisplay(
     Platform.runLater(()->{
//    		new Runnable() {
//      public void run() {
//        try {
          String type = message.args[0].strValue();
          String path = message.args[1].strValue();
          String pattern = message.args[2].strValue();
          String def = message.args[3].strValue();
          
          
          final FileChooser fileChooser = new FileChooser();
      	
				fileChooser.setTitle("Select the file");
				//System.err.println("type: "+ type + "; path: " + path+ "; pattern: " + pattern+ "; def: "+ def);
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(pattern, pattern);
				fileChooser.getExtensionFilters().add(filter);
				fileChooser.setSelectedExtensionFilter(filter);
				
				PropertyManager pm = XModeler.getPropertyManager();
				String pmPath = pm.getStringProperty("fileDialogPath", path);
				
				File initFile = new File(pmPath);
				fileChooser.setInitialFileName(def);
				if (initFile.exists()) {
					fileChooser.setInitialDirectory(initFile);
				}else{
					fileChooser.setInitialDirectory(lastFile.getParentFile());
				}
				File file;
				if (type.equals("open")) {
					file = fileChooser.showOpenDialog(XModeler.getStage());
				} else {
					file = fileChooser.showSaveDialog(XModeler.getStage());
				}

				if (file != null) {
					lastFile = file;
					pm.setStringProperty("fileDialogPath", file.getParent());
					result[0] = new Value(file.getAbsolutePath());
				} else {
					result[0] = new Value("");
				}
				l.countDown();
//          FileDialog dialog = new FileDialog(XModeler.getXModeler(), type.equals("open") ? SWT.OPEN : SWT.SAVE);
//          dialog.setFilterExtensions(new String[] { pattern });
//          dialog.setFileName(def);
//          if (new File(path).exists()) dialog.setFilterPath(path);
//          path = dialog.open();
//          path = path == null ? "" : path;
//          result[0] = new Value(path);
//        } catch (Exception e) {
//          e.printStackTrace(System.err);
//        }
//      }
    });
//    System.err.println(message);
     try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    return result[0];
  }

  private Value newInputDialog(Message message) {
    String title = message.args[0].strValue();
    String command = message.args[1].strValue();
    String value = message.args[2].strValue();
    return newInputDialog(title, command, value);
  }

  private void newMessageDialog(final Message message) {
	  //TODO reimplement notify
//    runOnDisplay(   new Runnable() {
//    public void run() {
	    CountDownLatch l = new CountDownLatch(1);
		   Platform.runLater(()->{
//        Value id = message.args[0];
        Value info = message.args[1];
//        Alert alert = new Alert(AlertType.CONFIRMATION, info.strValue(), ButtonType.OK);
//        alert.showAndWait();
        NotifierDialog.notify("Message", info.strValue(), NotificationType.INFO);
        l.countDown();

//      }
    });
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void newWarningDialog(final Message message) {
	  //TODO reimplement notify
//	    runOnDisplay(new Runnable() {
//      public void run() {
	  CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() ->{
//	        Value id = message.args[0];
	        Value info = message.args[1];
	        Alert alert = new Alert(AlertType.WARNING, info.strValue(), ButtonType.OK);
	        alert.showAndWait();
	        l.countDown();
	        NotifierDialog.notify("Warning", info.strValue(), NotificationType.WARN);
//	      }
	    });
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	  }
  
  private void newErrorDialog(final Message message) {
	  //TODO reimplement notify
//	    runOnDisplay(new Runnable() {
//      public void run() {
	  	CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() ->{	
//	        Value id = message.args[0];
	        Value info = message.args[1];
	        Alert alert = new Alert(AlertType.ERROR, info.strValue(), ButtonType.OK);
	        alert.showAndWait();
	        l.countDown();
	        NotifierDialog.notify("Error", info.strValue(), NotificationType.ERROR);
//	      }
	    });
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	  }
  
  private Value newQuestionDialog(final Message message) {
    final Value[] values = new Value[1];
    
    CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{	
// runOnDisplay(
// 		new Runnable() { public void run() {
 	        Value question = message.args[0];
 	        
     Alert alert = new Alert(AlertType.CONFIRMATION, question.strValue(), ButtonType.YES, ButtonType.NO);
     alert.setTitle("Confirm");
     alert.showAndWait();
     
     if(alert.getResult() == ButtonType.YES){
     	values[0] = new Value("Yes");
     }else if(alert.getResult() == ButtonType.NO){
     	values[0] = new Value("No");
     }
     l.countDown();

//        Value defaultResponse = message.args[1];
//        Value icon = message.args[2];
//        values[0] = new Value(MessageDialog.openQuestion(XModeler.getXModeler(), "Question", question.strValue()) ? "Yes" : "No");
//      }
    });
	 try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    return values[0];
  }

  private Value newQuestionDialogYesOnly(final Message message) {
	  CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{
		    	  Value question = message.args[0];
			   
		    	  Alert alert = new Alert(AlertType.INFORMATION, question.strValue(), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		    	  alert.setTitle("Question");
		    	  alert.showAndWait();

		    	  l.countDown();
		      });
	  try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	  
//	  runOnDisplay(new Runnable() {
//	      public void run() {
//	        Value question = message.args[0];
//	        new MessageDialog(XModeler.getXModeler(), "Question", null, question.strValue(), MessageDialog.INFORMATION, 
//	    			new String[]{
//	    					"OK"},0).open();
//	      }});
	    return new Value("VOID");
	  }
  
  private Value newQuestionDialogYesNoCancel(final Message message) {
	   final Value[] values = new Value[1];
//	    runOnDisplay(
	   CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{	   
	        Value question = message.args[0];
//	        Value defaultResponse = message.args[1];
//	        Value icon = message.args[2];
//	        MessageDialog md = new MessageDialog(XModeler.getXModeler(), "Question", null, question.strValue(), MessageDialog.QUESTION_WITH_CANCEL, 
//	    			new String[]{
//	    				"Yes", 
//	    				"No", 
//	    				"Cancel"},
//	    			0
//	    	);
	        Alert alert = new Alert(AlertType.CONFIRMATION, question.strValue(), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
	        alert.setTitle("Question");
	        alert.showAndWait();
	        
	        if(alert.getResult() == ButtonType.YES){
	        	values[0] = new Value("Yes");
	        }else if(alert.getResult() == ButtonType.NO){
	        	values[0] = new Value("No");
	        }else {
	        	values[0] = new Value("");
	        }
	        l.countDown();
//	    	switch(md.open()) {
//	    	case 0: 
//		        values[0] = new Value("Yes");
//	    		break;
//	    	case 1:
//		        values[0] = new Value("No");
//	    		break;
//	    	case 2:
//		        values[0] = new Value("");
//	    		break;
//	    	}	        
	    });
	    try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    return values[0];
	}

	private Value newConfirmDialog(final Message message) {
	    final Value[] values = new Value[1];
	    CountDownLatch l = new CountDownLatch(1);
		   Platform.runLater(	()->{
//	    runOnDisplay(
	    	String question = message.args[0].strValue();
	    	
	        Alert alert = new Alert(AlertType.CONFIRMATION, question, ButtonType.YES, ButtonType.NO);
	        alert.setTitle("Confirm");
	        alert.showAndWait();
	        
	        if(alert.getResult() == ButtonType.YES){
	        	values[0] = new Value("Yes");
	        }else if(alert.getResult() == ButtonType.NO){
	        	values[0] = new Value("No");
	        }
	        l.countDown();
//	    	boolean reply = MessageDialog.openQuestion(XModeler.getXModeler(), "Confirm", question);
//	    	if (reply)
//	    		values[0] = new Value("Yes");
//	    	else
//	    		values[0] = new Value("No");
	    	});
		   try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return values[0];
	}

  private void newTextAreaDialog(Message message) {
//    String id = message.args[0].strValue();
//    String type = message.args[1].strValue();
    final String title = message.args[2].strValue();
    final String info = message.args[3].strValue();
//    runOnDisplay(new Runnable() {
//      public void run() {
//        MessageDialog dialog = new MessageDialog(XModeler.getXModeler(), title, null, info, MessageDialog.INFORMATION, new String[] { "OK" }, 0);
//        dialog.open();
//      }
    CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{
		   Alert alert = new Alert(AlertType.CONFIRMATION, info, ButtonType.OK);
	        alert.setTitle(title);
	        alert.showAndWait();
	        l.countDown();
    });
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  private void noLongerBusy(final Message message) {
//    runOnDisplay(	new Runnable() {
//      public void run() {
	  CountDownLatch l = new CountDownLatch(1);
	   Platform.runLater(()->{	
//        Value id = message.args[0];
        XModeler.removeBusyInformation();
        XModeler.getStage().getScene().setCursor(cursor);
//        XModeler.getXModeler().setCursor(cursor);
        cursor = null;
        l.countDown();
//      }
    });
	  try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
  }

  public boolean processMessage(Message message) {
    return false;
  }

  public Value selectionDialog(Message message) {
    final boolean multi = message.args[0].boolValue;
    final String title = message.args[1].strValue();
    final String message_ = message.args[2].strValue();
    final Value[] options = message.args[3].values;
    final Value[] result = new Value[1];
    
    String[] stringOptions = objectsToStrings(options);
    String[] allOptions = processAllOptions(stringOptions);
    String[] defaultOptions = processDefaultOptions(stringOptions);

    System.err.println("options: " + Arrays.toString(options));
    System.err.println("stringOptions: " + Arrays.toString(stringOptions));
    System.err.println("allOptions: " + Arrays.toString(allOptions));
    System.err.println("defaultOptions: " + Arrays.toString(defaultOptions));
    
//    runOnDisplay(new Runnable() {
//      public void run() {
//    	if (multi)
//          result[0] = openMultiSelectionDialog(title, message_, options);
//        else result[0] = openSelectionDialog(title, message_, options);
//      }
//    });
    CountDownLatch l = new CountDownLatch(1);
    Platform.runLater(()->{
        SelectionDialog sd = new SelectionDialog(title, message_, multi, allOptions, defaultOptions);
        Optional<String[]> dialogResult = sd.showAndWait();
        
        if(dialogResult.isPresent()){
        	Value[] resultRaw = getResultArray(dialogResult.get(), defaultOptions); 
        	if (multi){
        		result[0] = new Value(resultRaw);
        	}else {
            	result[0] = resultRaw[0];
          	}
        }else{
        	if (multi){
        		result[0] = new Value("-1");
        	}else {
            	result[0] = new Value("");
          	}
        }
        l.countDown();
    });
	try {
		l.await();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	return result[0];
  }

	/**
	 * Builds the tree.
	 *
	 * @param tree the tree
	 * @param expand the expand
	 * @param disable the disable
	 * @param select the select
	 * @return the tree element
	 */
	public TreeElement buildTree(Value[] tree, Vector<TreeElement> expand, Vector<TreeElement> disable,
			Vector<TreeElement> select) {
		TreeElement root = new TreeElement(null, "Root");
		buildTree(root, tree, expand, disable, select);
		return root;
	}

	/**
	 * Builds the tree.
	 *
	 * @param parent the parent
	 * @param tree the tree
	 * @param expand the expand
	 * @param disable the disable
	 * @param select the select
	 */
	public void buildTree(TreeElement parent, Value[] tree, Vector<TreeElement> expand,
			Vector<TreeElement> disable, Vector<TreeElement> select) {
		// A

		// xmf.multiSelectTreeDialog("BOB",
		// Seq{"1","&",Seq{Seq{"2",Seq{Seq{"3",Seq{}}}}} } ,Seq{"1"},null);

		String name = tree[0].strValue();
		// if(stringContains(name,'*'))
		// name = stringRemove(name,'*');
		TreeElement branch = new TreeElement(parent, name);
		;
		parent.addChild(branch);
		if (tree.length > 1) {
			Value[] children = null;

			if (tree[1].type == Value.VECTOR) {
				children = tree[1].values;
			} else {

				// Extra information is encoded in the second element
				// - '&' indicates that the node should be disabled
				// - '*' indicates that the node should be expanded

				String encoding = tree[1].strValue();
				if (stringContains(encoding, '&')) {
					disable.addElement(branch);
				}
				if (stringContains(encoding, '*')) {
					expand.addElement(branch);
				}
				if (stringContains(encoding, '^')) {
					select.addElement(branch);
				}
				children = tree[2].values;
			}

			// Recursively build the tree

			for (int i = 0; i < children.length; i++) {
				buildTree(branch, children[i].values, expand, disable, select);
			}
		}
	}

	/**
	 * Multi tree dialog.
	 *
	 * @param message the message
	 * @return the value
	 */
// still requires to use "runOnDisplay" (see other methods):
/*	public Value multiTreeDialog(Message message) {
		 String title = message.args[0].strValue();
		 Value[] tree = message.args[1].values;
		 Vector expand = new Vector();
		 Vector disable = new Vector();
		 Vector select = new Vector();
		 TreeElement root = buildTree(tree, expand, disable, select);
		 MultiSelectionTreeDialog treeDialog = new MultiSelectionTreeDialog(XModeler.getXModeler(), new LabelProvider(), new TreeElementProvider());
		 treeDialog.setTitle(title);
		 treeDialog.setInput(root);
		 treeDialog.create();
		 treeDialog.expandTree(expand);
		 treeDialog.disableNodes(disable);
		 treeDialog.selectNodes(select);
		 int returncode = treeDialog.open();
		 if (returncode != 1) {
		 Object[] result = treeDialog.getResult();
		 if (result != null) {
		 Value[] values = new Value[result.length];
		 for (int i = 0; i < result.length; i++) {
		 TreeElement te = (TreeElement) result[i];
		 Vector path = new Vector();
		 te.getPath(path);
		 Value[] value = new Value[path.size()];
		 for (int z = path.size(); z > 0; z--) {
		 String s = (String) path.elementAt(z - 1);
		 value[path.size() - z] = new Value(s);
		 }
		 values[i] = new Value(value);
		 }
		 return new Value(values);
		 }
		 }
		return new Value("");
	}*/

	/**
	 * Simple tree dialog.
	 *
	 * @param message the message
	 * @return the value
	 */
	public Value simpleTreeDialog(Message message) {
		final String title = message.args[0].strValue();
		Value[] tree = message.args[1].values;
		final Vector<TreeElement> expand = new Vector<TreeElement>();
		Vector<TreeElement> disable = new Vector<TreeElement>();
		Vector<TreeElement> selected = new Vector<TreeElement>();
		final TreeElement root = buildTree(tree, expand, disable, selected);
	    final Value[] result = new Value[] { new Value("") };

		 CountDownLatch l = new CountDownLatch(1);
		   Platform.runLater(()->{	
			   TreeDialog treeDialog = new TreeDialog(root, title, expand, disable, selected);
			   Optional<TreeElement> dialogResult = treeDialog.showAndWait();
		        
		        if(dialogResult.isPresent()){
		        	TreeElement te = dialogResult.get();
		        	Vector<String> path = new Vector<String>();
					te.getPath(path);
					Value[] value = new Value[path.size()];
					for (int i = path.size(); i > 0; i--) {
						String s = (String) path.elementAt(i - 1);
						value[path.size() - i] = new Value(s);
					}
					result[0] = new Value(value);
		        }
		        l.countDown();
		   });
		   try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		DialogsClient.theClient().runOnDisplay(new Runnable() {
//			public void run() {
//				TreeDialog treeDialog = new TreeDialog(XModeler.getXModeler(),	new LabelProvider(), new TreeElementProvider());
//				treeDialog.setTitle(title);
//				treeDialog.setInput(root);
//				treeDialog.create();
//				treeDialog.expandTree(expand);
//				int returncode = treeDialog.open();
//				//result[0] = new Value("");
//				if (returncode != 1) {
//					Object[] res = treeDialog.getResult();
//					if (res.length > 0) {
//						TreeElement te = (TreeElement) res[0];
//						Vector<String> path = new Vector<String>();
//						te.getPath(path);
//						Value[] value = new Value[path.size()];
//						for (int i = path.size(); i > 0; i--) {
//							String s = (String) path.elementAt(i - 1);
//							value[path.size() - i] = new Value(s);
//						}
//						result[0] = new Value(value);
//					}
//				}
//			}
//		});
		return result[0];
	}

  public void sendMessage(final Message message) {
    if (message.hasName("newBusyDialog"))
      newBusyDialog(message);
    else if (message.hasName("noLongerBusy"))
      noLongerBusy(message);
    else if (message.hasName("newMessageDialog"))
      newMessageDialog(message);
    else if (message.hasName("newWarningDialog"))
    	newWarningDialog(message);
    else if (message.hasName("newErrorDialog"))
        newErrorDialog(message);
    else if (message.hasName("newTextAreaDialog"))
      newTextAreaDialog(message);
    else super.sendMessage(message);
  }

	/**
	 * String contains.
	 *
	 * @param string the string
	 * @param c the c
	 * @return true, if successful
	 */
	protected boolean stringContains(String string, char c) {
		for (int i = 0; i < string.length(); i++) {
			char sc = string.charAt(i);
			if (sc == c)
				return true;
		}
		return false;
	}
	
	/**
	 * Color dialog.
	 *
	 * @param message the message
	 * @return the value
	 */
	public Value colorDialog(final Message message) {
	    final Value[] result = new Value[1];
//	    DialogsClient.theClient().runOnDisplay(new Runnable() {
//		    public void run() {
	    CountDownLatch l = new CountDownLatch(1);
	    Platform.runLater(()->{
		    	String text = message.args[0].strValue();
				int red = message.args[1].intValue;
				int green = message.args[2].intValue;
				int blue = message.args[3].intValue;
				
				ColorSelectionDialog csd = new ColorSelectionDialog(text, red, green, blue);
				Optional<Color> resultsRaw = csd.showAndWait();
		        
		        if(resultsRaw.isPresent()){
		        	Value[] color = new Value[3];
					color[0] = new Value((new Double(resultsRaw.get().getRed()*255)).intValue());
					color[1] = new Value((new Double(resultsRaw.get().getGreen()*255)).intValue());
					color[2] = new Value((new Double(resultsRaw.get().getBlue()*255)).intValue());
					result[0] = new  Value(color);
		        	
		        }else{
		        	Value[] color = new Value[3];
					color[0] = new Value(-1);
					color[1] = new Value(-1);
					color[2] = new Value(-1);
					result[0] = new Value(color);
		        }
				l.countDown();
//				ColorDialog dialog = new ColorDialog(XModeler.getXModeler());
//				dialog.setText(text);
//				if (red > 0 && green > 0 && blue > 0)
//					dialog.setRGB(new RGB(red, green, blue));
//				RGB choosen = dialog.open();
//				if (choosen != null) {
//					Value[] color = new Value[3];
//					color[0] = new Value(choosen.red);
//					color[1] = new Value(choosen.green);
//					color[2] = new Value(choosen.blue);
//					result[0] = new  Value(color);
//				} else {
//					Value[] color = new Value[3];
//					color[0] = new Value(-1);
//					color[1] = new Value(-1);
//					color[2] = new Value(-1);
//					result[0] = new Value(color);
//				}
//			}
	    });
	    try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    return result[0];
	}

}