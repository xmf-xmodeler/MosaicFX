package tool.clients.editors;

import java.util.ArrayList;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SearchWindow extends Stage {
	InlineCssTextArea textArea;
	TextField searchField;
	int searchIndex = 0;
	ArrayList<Integer> results = new ArrayList<Integer>();
	int resultIndex = 0;
	Label resultLabel;
	String searchString = "";
	CheckBox caseSensitiveCheckBox;
	Stage parent;

	static double GAP = 10;

	public SearchWindow(InlineCssTextArea textArea, Stage parent) {

		setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				closeStage();
			}
		});
		this.parent = parent;
		getIcons().add(new Image("file:src/resources/gif/shell/mosaic32.gif"));
		setTitle("Find");
		resultLabel = new Label("Result: 0 of 0");
		this.textArea = textArea;
		searchField = new TextField();
		/*
		 * CheckBox for case sensitivity
		 */
		caseSensitiveCheckBox = new CheckBox("Case sensitive");

		Button searchButton = new Button("Find");

		/*
		 * Search for given keyword
		 */
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				findString();
			}
		});
		Button nextButton = new Button("Next");
		nextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				nextResult();
			}
		});

		Button prevButton = new Button("Previous");
		prevButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				previousResult();
			}
		});

		Button closeButton = new Button("Close");
		closeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				closeStage();
			}
		});

		setAlwaysOnTop(true);
		HBox hbox = new HBox(new Label("Find: "), searchField);
		hbox.setSpacing(GAP);
		hbox.setAlignment(Pos.CENTER);
		HBox hbox2 = new HBox(prevButton, nextButton);
		hbox2.setSpacing(GAP);
		hbox2.setAlignment(Pos.CENTER);
		VBox vbox = new VBox(hbox, resultLabel, searchButton, hbox2, caseSensitiveCheckBox, closeButton);
		vbox.setAlignment(Pos.CENTER);
		closeButton.setAlignment(Pos.CENTER_RIGHT);
		vbox.setPadding(new Insets(GAP, GAP, GAP, GAP));
		vbox.setSpacing(GAP);
		Scene scene = new Scene(vbox);
		setScene(scene);
	}

	public void findString() {

		restoreStyle();
		searchIndex = 0;
		resultIndex = 0;
		String text = textArea.getText();
		searchString = searchField.getText();
		if (!searchString.equals("")) {

			/*
			 * Check for case sensitivity
			 */
			if (!caseSensitiveCheckBox.selectedProperty().get()) {
				searchString = searchString.toUpperCase();
				text = text.toUpperCase();
			}

			int index = text.indexOf(searchString, searchIndex);
			if (index != -1) {

				// set cursor to first result
				textArea.displaceCaret(index);

				// highlighting all result
				while ((index = text.indexOf(searchString, searchIndex)) != -1) {
					results.add(index);
					textArea.setStyle(index, index + searchString.length(),
							textArea.getStyleAtPosition(index).concat(";-rtfx-background-color:yellow"));
					searchIndex = index + searchString.length();
				}
				textArea.requestFocus();
				parent.toFront();

			}
		}
		updateLabel();
	}

	public void nextResult() {
		if (!results.isEmpty()) {
			resultIndex = ++resultIndex % results.size();
			textArea.displaceCaret(results.get(resultIndex));
			updateLabel();
			textArea.requestFocus();
			parent.toFront();
		}
	}

	public void previousResult() {
		if (!results.isEmpty()) {
			resultIndex = (resultIndex + results.size() - 1) % results.size();
			textArea.displaceCaret(results.get(resultIndex));
			updateLabel();
			textArea.requestFocus();
			parent.toFront();
		}
	}

	public void updateLabel() {
		if (!results.isEmpty())
			resultLabel.setText("Result: " + (resultIndex + 1) + " of " + results.size());
		else
			resultLabel.setText("Result: 0 of 0");
	}

	public void restoreStyle() {
		/*
		 * restore style of previous search results
		 */
		while (!results.isEmpty()) {
			int index = results.get(0);
			String style = textArea.getStyleAtPosition(index);
			style = style.replaceAll(";-rtfx-background-color:yellow", "");
			textArea.setStyle(index, index + searchString.length(), style);
			results.remove(0);
		}
	}

	public void closeStage() {
		restoreStyle();
		close();
	}

}
