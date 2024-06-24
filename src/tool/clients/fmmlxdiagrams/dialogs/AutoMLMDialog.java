package tool.clients.fmmlxdiagrams.dialogs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.communication.java_to_python.PythonFunction;
import tool.communication.java_to_python.PythonRequestWrapper;

import tool.helper.persistence.StartupModelLoader;

public class AutoMLMDialog extends Dialog {

	private GridPane grid;
	private AbstractPackageViewer diagram;

	private Label lblChooseInputModel;
	private Label lblPromotionCategory;

	private ComboBox<String> cmbPromotionCategory;
	private ComboBox<String> cmbExampleModel;

	private Button butAdjustPromotionOptions;
	private Button butOpenInputModel;
	private Button butPerfomModelLifting;

	public AutoMLMDialog(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		this.setTitle("AutoMLM");

		this.grid = new GridPane();

		getDialogPane().setContent(buildGridPane());
		getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	}

	private GridPane buildGridPane() {

		buildGrid();

		// initian definitions of elements
		lblPromotionCategory = new Label("Promotion Category:");
		lblChooseInputModel = new Label("Choose Input Model:");

		cmbPromotionCategory = new ComboBox<String>();
		cmbExampleModel = new ComboBox<String>();

		butAdjustPromotionOptions = new Button("Adjust Promotion Options");
		butOpenInputModel = new Button("Open Input Model");
		butPerfomModelLifting = new Button("Perform Model Deepening");

		// style adaptments
		lblPromotionCategory.setMaxWidth(Double.MAX_VALUE);
		lblChooseInputModel.setMaxWidth(Double.MAX_VALUE);

		cmbPromotionCategory.setMaxWidth(Double.MAX_VALUE);
		cmbExampleModel.setMaxWidth(Double.MAX_VALUE);

		butAdjustPromotionOptions.setMaxWidth(Double.MAX_VALUE);
		butOpenInputModel.setMaxWidth(Double.MAX_VALUE);
		butPerfomModelLifting.setMaxWidth(Double.MAX_VALUE);

		// grid placements
		grid.add(lblPromotionCategory, 0, 1);
		grid.add(cmbPromotionCategory, 1, 1, 2, 1);

		grid.add(lblChooseInputModel, 0, 2);
		grid.add(cmbExampleModel, 1, 2, 2, 1);

		grid.add(butAdjustPromotionOptions, 0, 3);
		grid.add(butOpenInputModel, 1, 3);
		grid.add(butPerfomModelLifting, 2, 3);

		fillComboBoxCategory();

		// functions based on pressing or changing
		cmbPromotionCategory.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			fillComboBoxModel(newValue);
		});

		butAdjustPromotionOptions.setOnAction(e -> {
			butAdjustPromotionOptionsPressed();
		});

		butOpenInputModel.setOnAction(e -> {
			butOpenInputModelPressed();
		});

		butPerfomModelLifting.setOnAction(e -> {
			butPerfomModelLiftingPressed();
		});

		return grid;
	}

	private boolean isInputValid(String category, String model) {
		if (category == null || category == "") {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("No category selected");
			alert.setContentText("To open a model, a category must be selected!");
			alert.showAndWait();
			return false;
		}

		if (model == null || model == "") {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("No Input Model Selected");
			alert.setContentText("To open a model, a model must be selected!");
			alert.showAndWait();
			return false;
		}

		return true;
	}

	private String getPath(String category, String model) {
		// get correct file path of the diagram to be promoted
		File parentFile = new File("AutoMLM\\ExampleModels");

		// filter in parent directory for the correct sub directory
		String[] list = parentFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String[] parts = name.split("_");
				return category.equals(parts[1]);
			}
		});

		String path = "AutoMLM\\ExampleModels\\" + list[0] + "\\" + model;
		return path;
	}

	private void butPerfomModelLiftingPressed() {
		String category = cmbPromotionCategory.getValue();
		String model = cmbExampleModel.getValue();

		if (!isInputValid(category, model))
			return;

		if (model == "Use Current Model") {
			// TODO promotion process from current model
			// has to be saved and then the path can be used
			// path =
			// PropertyManager.getProperty(UserProperty.RECENTLY_SAVED_MODEL_DIR.toString());
			// sendToPython(path);
			return;
		}

		String path = getPath(category, model);

		String[] args = { category, path };
		PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.PROMOTE_DIAGRAM, args);
		wrapper.execute();
		String newPath = (String) wrapper.getResponse();

		new StartupModelLoader().loadModelsFromPath(newPath);

		String projectName = getProjectNameFromFile(newPath);
		String diagramName = getDiagramNameFromFile(newPath);

		// raise alert to ensure model is loaded
		Alert alert = new Alert(AlertType.NONE);
		alert.setTitle("Diagram Promotion complete");
		alert.setContentText("The selected diagram can now be opened");
		alert.getButtonTypes().add(ButtonType.OK);

		// Display the alert
		Optional<ButtonType> result = alert.showAndWait();
		// wait for result of alert box; after that diagram can be opened
		if (result.isPresent()) {
			if (result.get().equals(ButtonType.OK)) {
				diagram.getComm().openDiagram(projectName, diagramName);
			}
		}

	}

	private void butOpenInputModelPressed() {

		String category = cmbPromotionCategory.getValue();
		String model = cmbExampleModel.getValue();

		if (!isInputValid(category, model))
			return;

		if (model == "Use Current Model") {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Diagram already open");
			alert.setContentText("The current diagram is already open");

			// Display the alert
			alert.showAndWait();
			return;
		}

		String path = getPath(category, model);

		// load the model into the StartUpModelLoader
		new StartupModelLoader().loadModelsFromPath(path);

		// get the project and diagram name from python
		String projectName = getProjectNameFromFile(path);
		String diagramName = getDiagramNameFromFile(path);

		// raise alert to ensure model is loaded
		Alert alert = new Alert(AlertType.NONE);
		alert.setTitle("Diagram can be opened");
		alert.setContentText("The selected diagram can now be opened");
		alert.getButtonTypes().add(ButtonType.OK);

		// Display the alert
		Optional<ButtonType> result = alert.showAndWait();
		// wait for result of alert box; after that diagram can be opened
		if (result.isPresent()) {
			if (result.get().equals(ButtonType.OK)) {
				diagram.getComm().openDiagram(projectName, diagramName);
			}
		}

	}

	private void butAdjustPromotionOptionsPressed() {
		// TODO Extendend window to customize settings, or perform only single
		// operations
	}

	private String getProjectNameFromFile(String path) {
		String[] args = { path };
		try {
			PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.GETPROJECTNAME, args);
			wrapper.execute();
			String projectName = (String) wrapper.getResponse();
			return projectName;
		} catch (Exception e) {
			System.err.println("Der Projektname des files konnte nicht erkannt werden");
			return "";
		}
		

		
	}

	private String getDiagramNameFromFile(String path) {
		String[] args = { path };
		try {
			PythonRequestWrapper wrapper2 = new PythonRequestWrapper(PythonFunction.GETDIAGRAMNAME, args);
			wrapper2.execute();
			String diagramName = (String) wrapper2.getResponse();
			return diagramName;
		} catch (Exception e) {
			System.err.println("Der Projektname des files konnte nicht erkannt werden");
			return "";
		}
		

		

	}

	// basic grid pane settings
	private void buildGrid() {
		// same width for columns
		ColumnConstraints col = new ColumnConstraints();
		col.setPercentWidth(33.33);

		// Add the ColumnConstraints to the GridPane
		grid.getColumnConstraints().addAll(col, col, col);

		// margins for grid
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setMinWidth(550);
		grid.setMaxWidth(550);

	}

	private void fillComboBoxCategory() {
		File file = new File("AutoMLM\\ExampleModels");
		String[] directories = file.list();

		for (String directory : directories) {

			String nameOfCategory = directory.substring(3);
			cmbPromotionCategory.getItems().add(nameOfCategory);
		}

	}

	private void fillComboBoxModel(String newValue) {

		// get parent file
		File parentFile = new File("AutoMLM\\ExampleModels");

		// filter in parent directory for the correct sub directory
		String[] list = parentFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				String[] parts = name.split("_");
				return newValue.equals(parts[1]);

			}
		});

		// new path
		File file = new File("AutoMLM\\ExampleModels\\" + list[0]);

		// list all files in the directory
		String[] files = file.list();
		// add standard option
		cmbExampleModel.getItems().setAll("Use Current Model");
		// add demo files
		cmbExampleModel.getItems().addAll(files);
	}

}