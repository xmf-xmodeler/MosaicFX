package tool.console;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import xos.Message;
import xos.Value;

import java.util.Collections;
import java.util.Vector;

public class AutoCompleteBox extends Dialog<String> {

	Vector<Suggestion> labels = new Vector<>();
	TextField searchField;
	ListView<String> listOfSuggestions; 
	boolean searchFieldInitialised = false;
	String result = null;
	String oldKey = "";
	boolean warning = false;
	
	public AutoCompleteBox(Stage owner, Message message) {
		super();
		initModality(Modality.WINDOW_MODAL);
		initOwner(owner);
		
	    Value[] pairs = message.args[0].values;
	    for (Value value : pairs) {
	      Value[] pair = value.values;
	      String label = pair[1].strValue();
	      Suggestion newSuggestion = new Suggestion(label);
	      if(!labels.contains(newSuggestion)) labels.add(newSuggestion);
	    }
	    
	    //Add FAKE Close Button
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        javafx.scene.Node closeButton = getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);

        getDialogPane().addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if (KeyCode.ESCAPE == event.getCode()) {
				close();
			}
		});

		getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
			if (KeyCode.BACK_SPACE == event.getCode()) {
				if(searchField.getText().equals("")){
					close();
				}
			}
		});
	}

	public String show(int x, int y) {
		if(labels.size() <= 0) {return "";}
	
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));

        searchField = new TextField();
        searchField.setText("Search here...");
        grid.add(searchField, 0, 0);
		searchField.setOnKeyPressed(new MySearchListener_Pressed());
		searchField.setOnKeyReleased(new MySearchListener_Released());
		
        listOfSuggestions = new ListView<>();
        grid.add(listOfSuggestions, 0, 1);
		listOfSuggestions.setOnMouseClicked(new MyListListener());
		
		addAllToListSortedBy("");
        
		getDialogPane().setContent(grid);
		getDialogPane().toFront();
		
		searchField.requestFocus();


		showAndWait();

        return result==null?"":result;
	}

	private void addAllToListSortedBy(String key) {
		listOfSuggestions.setItems(FXCollections.observableArrayList());
		Suggestion.key = key;
		Collections.sort(labels);
		for (Suggestion label : labels) {
			if (label.likelihood > .1)
				listOfSuggestions.getItems().add(label.text);// + " (" + labels.get(i).likelihood + " " + labels.get(i).lastKey + ")");
		}
		
		if (listOfSuggestions.getItems().size() > 0) {
			listOfSuggestions.getSelectionModel().select(0);
      		warning = false;
		} else {
			if(warning) {
				result = searchField.getText();
				close();
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
			if (!key.equals(lastKey)) {
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
				return other.text == null;
			} else return text.equals(other.text);
		}
	}
	
	private class MySearchListener_Released implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent e) {
			if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN) {
				int index = listOfSuggestions.getSelectionModel().getSelectedIndex();
				index += e.getCode() == KeyCode.UP ? -1 : 1;
				if(index >= 0 && index < listOfSuggestions.getItems().size()) {
					listOfSuggestions.getSelectionModel().select(index);
					searchField.setText(listOfSuggestions.getItems().get(index));
					Platform.runLater(() -> searchField.positionCaret(searchField.getText().length()));
				}
			} else
			if(e.getCode() == KeyCode.ENTER ) {
			    if(listOfSuggestions.getSelectionModel().getSelectedIndex() != -1) {
			    	result = listOfSuggestions.getSelectionModel().getSelectedItem();
			    } else {
			    	result = searchField.getText();
			    }
			    close();
			}  else
			if(searchFieldInitialised) {
				addAllToListSortedBy(searchField.getText());
			}
			
			Paint bgColor = Paint.valueOf(listOfSuggestions.getItems().size() == 0 ? "FF0000" : "FFFFFF");
			searchField.setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
		}
	}
	
	private class MySearchListener_Pressed implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent e) {
			if(!searchFieldInitialised) {
				searchField.setText("");
				searchFieldInitialised = true;
			}
		}
	}
	
	private class MyListListener implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent e) {
		    if(listOfSuggestions.getSelectionModel().getSelectedIndex() != -1 && e.getClickCount() == 2) {
		    	result = listOfSuggestions.getSelectionModel().getSelectedItem();
		    	close();
		    }
		}
	}
}