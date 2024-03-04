package tool.clients.fmmlxdiagrams.dialogs;

import java.io.File;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.communication.java_to_python.PythonFunction;
import tool.communication.java_to_python.PythonRequestWrapper;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;

/*
 * FH 19.02.2024
 * Implementation of the AutoMLM gui, formerly solely in python. GUI is now in java, functionality is still and will remain in python.
 */
public class AutoMLMDialog extends Dialog {

	private AbstractPackageViewer diagram;

	private GridPane grid = new GridPane();

	private String path = "";

	private Button buttonSelectFile;
	private Button buttonExecute;
	private Button buttonUseCurrent;

	private ComboBox<String> comboboxCaseSelector;
	private ComboBox<String> comboboxTechnologySelector;

	private Label lblFilePath;
	private Label lblHeader;
	private Label lblSubHeader;
	private Label lblCaseSelector;
	private Label lblTechnologySelector;

	private FileChooser fileChooser;

	public AutoMLMDialog(AbstractPackageViewer diagram) {
		this.diagram = diagram;

		ButtonType close = ButtonType.CLOSE;
		getDialogPane().getButtonTypes().add(close);

		grid = buildGridPane();
		getDialogPane().setContent(grid);
	}

	// creates the grid pane and its objects
	private GridPane buildGridPane() {

		buildGrid();

		lblFilePath = new Label();

		// add header
		lblHeader = new Label("MLM Automatic Construction");
		lblHeader.setFont(new Font("Arial", 24));
		grid.add(lblHeader, 0, 0, 3, 1);
		lblSubHeader = new Label("Case Navigator");
		lblSubHeader.setFont(new Font("Arial", 18));
		grid.add(lblSubHeader, 0, 1, 3, 1);

		// file path label stays blank (for now)
		lblFilePath.setText("");
		grid.add(lblFilePath, 0, 5, 3, 1);

		// add combobox caseSelector
		comboboxCaseSelector = new ComboBox<String>();
		comboboxCaseSelector.setMaxWidth(Double.MAX_VALUE);
		comboboxCaseSelector.getItems().addAll("Classification", "Generalization");
		lblCaseSelector = new Label("Select Case");
		grid.add(lblCaseSelector, 0, 2);
		grid.add(comboboxCaseSelector, 1, 2, 2, 1);

		// add combobox comboboxTechnologySelector
		comboboxTechnologySelector = new ComboBox<String>();
		comboboxTechnologySelector.setMaxWidth(Double.MAX_VALUE);
		comboboxTechnologySelector.getItems().addAll("Formal Concept Analysis", "Clustering");
		lblTechnologySelector = new Label("Select Technology");
		grid.add(lblTechnologySelector, 0, 3);
		grid.add(comboboxTechnologySelector, 1, 3, 2, 1);

		// add filechooser
		buttonSelectFile = new Button("Select File");
		buttonSelectFile.setMaxWidth(Double.MAX_VALUE);
		grid.add(buttonSelectFile, 0, 4);
		buttonSelectFile.setOnAction(e -> {
			buttonSelectFilePressed();
		});

		// add button execute
		buttonExecute = new Button("Execute");
		buttonExecute.setMaxWidth(Double.MAX_VALUE);
		grid.add(buttonExecute, 1, 4);
		buttonExecute.setOnAction(e -> {
			buttonExecutePressed();
		});

		// use current file button
		buttonUseCurrent = new Button("Use Current File");
		buttonUseCurrent.setMaxWidth(Double.MAX_VALUE);
		grid.add(buttonUseCurrent, 2, 4);
		buttonUseCurrent.setOnAction(e -> {
			buttonUseCurrentPressed();
		});

		// placeholder for filepath
		grid.add(new Label(""), 0, 5, 3, 1);

		return grid;
	}

	// basic grid pane settings
	private void buildGrid() {
		ColumnConstraints col = new ColumnConstraints();
		col.setPercentWidth(33.33);

		// Add the ColumnConstraints to the GridPane
		grid.getColumnConstraints().addAll(col, col, col);

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setMinWidth(300);
		grid.setMaxWidth(300);

	}

	// func for button selectFile
	private void buttonSelectFilePressed() {
		Pane pane = new Pane();
		Scene scene = new Scene(pane);
		Stage stage = new Stage();
		stage.setScene(scene);

		stage.setTitle("Select file");
		stage.setWidth(800);
		stage.setHeight(400);

		fileChooser = new FileChooser();
		fileChooser.setTitle("Select location of fxml");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML File", "*.xml"),
				new ExtensionFilter("FXML File", "*.fxml"));

		File file = fileChooser.showSaveDialog(stage);
		if (file == null) {
			path = "";
		} else {
			path = file.getPath();
		}

		// split path at every \
		String[] pathParts = path.split("\\\\");
		lblFilePath.setText("Selected file: " + pathParts[pathParts.length - 1]);
	}

	// func for button execute
	private void buttonExecutePressed() {
		if (path == "")
			return;

		String selectedCase = comboboxCaseSelector.getValue();
		String selectedTechnology = comboboxTechnologySelector.getValue();

		if (selectedCase != null) {
			if (selectedTechnology != null) {
				// TBD only send here to python
			} else {
				// TBD that no technology has been selected
			}
		} else {
			// TBD alert that no case has been selected
		}

		sendToPython(this.path);
	}

	// func for button use current file
	private void buttonUseCurrentPressed() {
		// save diagram
		Alert alert = new Alert(AlertType.NONE);
		alert.setTitle("Use Current Diagram");
		alert.setContentText("Has the current diagram been saved and should be used for the transformation process?");
		alert.getButtonTypes().add(ButtonType.YES);
		alert.getButtonTypes().add(ButtonType.NO);

		// Display the alert and wait for it
		Optional<ButtonType> result = alert.showAndWait();
		// wait for result of alert box
		if (result.isPresent()) {
			if (result.get().equals(ButtonType.YES)) {
				// continue with last saved diagram
				// get path of last saved model
				path = PropertyManager.getProperty(UserProperty.RECENTLY_SAVED_MODEL_DIR.toString());
				sendToPython(path);
			} else {
				// close dialog
				return;
			}
		}
	}

	// sent to python for analysis
	private void sendToPython(String path) {
		String[] args = { path };
		// it is possible that an empty or wrong path is transmitted, this has to be
		// catched
		PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.CALL_EXECUTION, args);
		try {
			wrapper.execute();
		} catch (Exception e) {
			System.err.println("Error with path: " + path);
			System.err.println(e.toString());
		}
	}
}
