package tool.clients.editors;

import java.util.ArrayList;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchWindow extends Stage {
	InlineCssTextArea textArea;
	TextField searchField;
	int searchIndex = 0;
	ArrayList<Integer> results = new ArrayList<Integer>();
	int resultIndex = 0;
	Label resultLabel;

	static double GAP = 10;

	public SearchWindow(InlineCssTextArea textArea, Stage parent) {

		resultLabel = new Label("Result: 0 of 0");
		this.textArea = textArea;
		searchField = new TextField();
		Button searchButton = new Button("Search");

		/*
		 * Search for given keyword
		 */
		searchButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				results.clear();
				searchIndex = 0;
				String text = textArea.getText();
				textArea.setStyle(0, textArea.getText().length(), "-rtfx-background-color:white");
				String searchString = searchField.getText();
				if (!searchString.equals("")) {
					int index = text.indexOf(searchString, searchIndex);
					if (index != -1) {

						// set cursor to first result
						textArea.displaceCaret(index);

						// highlighting all result
						while ((index = text.indexOf(searchString, searchIndex)) != -1) {
							results.add(index);
							textArea.setStyle(index, index + searchString.length(), "-rtfx-background-color:yellow");
							searchIndex = index + searchString.length();
						}
						textArea.requestFocus();
						//parent.toFront();
					}
				}
				updateLabel();
				
			}
		});
		Button nextButton = new Button("Next");
		nextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!results.isEmpty()) {
					resultIndex = ++resultIndex % results.size();
					textArea.displaceCaret(results.get(resultIndex));
					updateLabel();
					textArea.requestFocus();
					//parent.toFront();
				}
			}
		});
		
		Button prevButton = new Button("Previous");
		prevButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!results.isEmpty()) {
					resultIndex = (resultIndex + results.size() -1) % results.size();
					textArea.displaceCaret(results.get(resultIndex));
					updateLabel();
					textArea.requestFocus();
					//parent.toFront();
				}
			}
		});
		HBox hbox = new HBox(prevButton, nextButton);
		hbox.setSpacing(GAP);
		hbox.setAlignment(Pos.CENTER);
		VBox vbox = new VBox(searchField, resultLabel, searchButton, hbox);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(GAP, GAP, GAP, GAP));
		vbox.setSpacing(GAP);
		Scene scene = new Scene(vbox);
		setScene(scene);
	}

	public void updateLabel() {
		if (!results.isEmpty())
			resultLabel.setText("Result: " + (resultIndex+1) + " of " + results.size());
		else
			resultLabel.setText("Result: 0 of 0");
	}

}
