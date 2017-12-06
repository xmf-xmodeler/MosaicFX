package tool.console;

import java.util.Collections;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xos.Message;
import xos.Value;

public class AutoCompleteBox extends Dialog<String> {

	Vector<Suggestion> labels = new Vector<Suggestion>();
	TextField searchField;
	ListView<String> listOfSuggestions; 
	boolean searchFieldInitialised = false;
	String result = null;
	String oldKey = "";
	boolean warning = false;
	
	public AutoCompleteBox(Stage owner, Message message) {
		super();
//		super(owner);
		initModality(Modality.WINDOW_MODAL);
		
	    Value[] pairs = message.args[0].values;
	    for (Value value : pairs) {
	      Value[] pair = value.values;
	      String label = pair[1].strValue();
	      Suggestion newSuggestion = new Suggestion(label);
	      if(!labels.contains(newSuggestion)) labels.add(newSuggestion);
	    }
	    
//	    if(labels.size() <= 0) {
//	    	labels.add(new Suggestion("Aardvark"));
//	    	labels.add(new Suggestion("Bee"));
//	    	labels.add(new Suggestion("Cat"));
//	    	labels.add(new Suggestion("Dog"));
//	    	labels.add(new Suggestion("Elephant"));
//	    	labels.add(new Suggestion("Fox"));
//	    	labels.add(new Suggestion("Mouse"));
//	    	labels.add(new Suggestion("Unicorn"));
//	    	labels.add(new Suggestion("Wolpertinger"));
//	    	labels.add(new Suggestion("Yeti"));
//	    }
	    
	}

	
	public String show(int x, int y) {
		if(labels.size() <= 0) {return "";}
		
//        Shell parent = getParent();
//        Shell shell = new Shell(parent, SWT.RESIZE | SWT.APPLICATION_MODAL);//SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
//        shell.setText("getText()");
//        shell.setSize(150, 200);
//        shell.setLocation(displayPoint.x, displayPoint.y-250);
//        
//        shell.setLayout(new GridLayout(1, false));
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

        searchField = new TextField();
        searchField.setText("Search here...");
//        searchField.setForeground(new Color(Display.getCurrent (), 100, 100, 100));
//        GridData gridData = new GridData();
//		gridData.horizontalAlignment = SWT.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		searchField.setLayoutData(gridData);
        grid.add(searchField, 0, 0);
//		searchField.adaddKeyListener(new MySearchListener());
//		searchField.textProperty().addListener(new MySearchListener());
		searchField.setOnKeyPressed(new MySearchListener());
		
        listOfSuggestions = new ListView<String>();
//		gridData = new GridData();
//		gridData.horizontalAlignment = SWT.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.verticalAlignment = SWT.FILL;
//		gridData.grabExcessVerticalSpace = true;
//		listOfSuggestions.setLayoutData(gridData);
        grid.add(listOfSuggestions, 0, 1);
//		listOfSuggestions.addMouseListener(new MyListListener());
		listOfSuggestions.setOnMouseClicked(new MyListListener());
		
		
		addAllToListSortedBy("");
        
		getDialogPane().setContent(grid);
		
		searchField.requestFocus();
//		Platform.runLater(() -> searchField.requestFocus());
//		
//		Platform.runLater(() -> 
		System.err.println("show and wait...");
		showAndWait();
		System.err.println("waited: result = " + result);
		
//        shell.open();
//        Display display = parent.getDisplay();
//        while (!shell.isDisposed() && result == null) {
////        	System.err.println("sleeping: " + result);
//            if (!display.readAndDispatch()) display.sleep();
//        }
//        if(result != null) shell.dispose();
        return result==null?"":result;
	}

	private void addAllToListSortedBy(String key) {
		listOfSuggestions.setItems(FXCollections.observableArrayList());
		Suggestion.key = key;
		Collections.sort(labels);
		for(int i = 0; i < labels.size(); i++) {
			if(labels.get(i).likelihood > .1) listOfSuggestions.getItems().add(labels.get(i).text);// + " (" + labels.get(i).likelihood + " " + labels.get(i).lastKey + ")");
		}
		
		if (listOfSuggestions.getItems().size() > 0) {
			listOfSuggestions.getSelectionModel().select(0);
      		warning = false;
		} else {
			if(warning) {
				result = searchField.getText();
			} else {
				warning = true;
			}
		}
	}

	private static class Suggestion implements Comparable<Suggestion> {
		final String text;
		Double likelihood = 1.;
		String lastKey;
		static String key;
		
		private Suggestion(String text) {
			this.text = text;
		}
		
		@Override
		public int compareTo(Suggestion that) {
			calculateLikelihood();
			that.calculateLikelihood();
			int c = -this.likelihood.compareTo(that.likelihood);
			if(c != 0) return c;
			return this.text.compareToIgnoreCase(that.text);
		}

		private void  calculateLikelihood() {
			if (key != lastKey) {
				likelihood = 0.;
				if (text.toLowerCase().contains(key.toLowerCase()))
					likelihood += .5;
				if (text.toLowerCase().startsWith(key.toLowerCase()))
					likelihood += .5;
				lastKey = key;
			} 
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Suggestion other = (Suggestion) obj;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}
	}
	
	private class MySearchListener implements EventHandler<KeyEvent> {


		@Override
		public void handle(KeyEvent e) {
			if(!searchFieldInitialised) {
//				searchField.setForeground(new Color(Display.getCurrent (), 0, 0, 0));
				searchField.setText("");
				searchFieldInitialised = true;
			}
//			System.err.println(e.keyCode);
			if(e.getCode() == KeyCode.UP) {
				int index = listOfSuggestions.getSelectionModel().getSelectedIndex();
				index--;
				if(index >= 0 && index < listOfSuggestions.getItems().size()) {
					listOfSuggestions.getSelectionModel().select(index);
				}
			} else
			if(e.getCode() == KeyCode.DOWN) {
				int index = listOfSuggestions.getSelectionModel().getSelectedIndex();
				index++;
				if(index >= 0 && index < listOfSuggestions.getItems().size()) {
					listOfSuggestions.getSelectionModel().select(index);
				}
			} else
			if(e.getCode() == KeyCode.ENTER ) {
//					|| e.getCode() == KeyCode.) { // RETURN 
			    if(listOfSuggestions.getSelectionModel().getSelectedIndex() != -1) {
			    	result = listOfSuggestions.getSelectionModel().getSelectedItem();
			    } else {
			    	result = searchField.getText();
			    	close();
			    }
			}  else
			if(searchFieldInitialised) {
				if(!oldKey.equals(searchField.getText())) {
					oldKey = searchField.getText();
					addAllToListSortedBy(oldKey);
//					searchField.setForeground(new Color(Display.getCurrent (), warning?255:0, 0, 0));
				}
			}
		}
	}
	
	private class MyListListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent e) {
			System.err.println("list: " + listOfSuggestions.getSelectionModel().getSelectedIndex());
		    if(listOfSuggestions.getSelectionModel().getSelectedIndex() != -1) {
		    	result = listOfSuggestions.getSelectionModel().getSelectedItem();
		    	System.err.println("close();");
		    	Platform.runLater(() -> close());
		    }
		}
	}
}
