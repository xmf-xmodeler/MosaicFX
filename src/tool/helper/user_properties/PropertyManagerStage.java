package tool.helper.user_properties;

import java.io.File;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.helper.IconGenerator;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;
import tool.xmodeler.didactic_ml.UserDataProcessor;

public class PropertyManagerStage extends Stage {

	public PropertyManagerStage() {
		VBox root = new VBox();
		Scene scene = new Scene(root);
		setTitle("Preferences");
		getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		setWidth(450);
		setResizable(false);
		setScene(scene);
		initModality(Modality.APPLICATION_MODAL);
		addTabs(root);
	}

	private void addTabs(VBox root) {
		TabPane tabPane = new TabPane();
		root.getChildren().add(tabPane);
		Tab directoriesTab = new Tab("Directories");
		Tab userInterfaceTab = new Tab("UserInterface");
		Tab didacticMLTab = createDidacticMlTab();
		tabPane.getTabs().addAll(directoriesTab, userInterfaceTab , didacticMLTab);
		buildDirectoriesTab(directoriesTab);
		buildUserInterfaceTab(userInterfaceTab);
	}

	private Tab createDidacticMlTab() {
		Tab tab = new Tab();
		tab.setText("DidacticMl");
		Button button = new Button("Reset user statistics");
		button.setOnAction(this::showDeleteStatsDialog);
		tab.setContent(button);
		return tab;
	}
	
	 private void showDeleteStatsDialog(javafx.event.ActionEvent event) {
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.setTitle("Delete statistics");
	        alert.setHeaderText(null);
	        alert.setContentText("If you confirm the deletion all information about finished Learning Units will be deleted.");
	        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
	        Optional<ButtonType> result = alert.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.OK) {
	        	UserDataProcessor.resetTestStatistics();
	        }	        
	    }

	private void buildDirectoriesTab(Tab directoriesTab) {
		String savedModlesPath = PropertyManager.getProperty(UserProperty.MODELS_DIR.toString());

		GridPane saveTabContentGrid = new GridPane();
		formatGrid(saveTabContentGrid);

	// currently not needed see comment in Constructor of ControlCenter
	//	Label checkboxLabel = new Label("Load Models by StartUp?");
	//	CheckBox loadModels = new CheckBox();
	//	loadModels.setSelected(Boolean.valueOf(PropertyManager.getProperty(UserProperty.LOAD_MODELS_BY_STARTUP.toString())));
	//	loadModels.setOnAction(e -> PropertyManager.setProperty(UserProperty.LOAD_MODELS_BY_STARTUP.toString(), String.valueOf(loadModels.isSelected())));
		
		Label saveLabel = new Label("Model Directory ");
		Label currentFolderPath = new Label((savedModlesPath == null) ? "Choose File" : savedModlesPath);
		currentFolderPath.setPrefWidth(250);
		currentFolderPath.setMaxWidth(250);

		Button infoButton = JavaFxButtonAuxilary.createButtonWithPicture("", this::showInfoDialog, "resources/gif/img/about.gif");
		Button chooseSaveDirectory = JavaFxButtonAuxilary.createButtonWithPicture("", e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (savedModlesPath != null) {
				File file = new File(savedModlesPath);
				if (file.exists()) {
					directoryChooser.setInitialDirectory(file);					
				}
			}
			try {
				Optional<String> result = Optional.ofNullable(directoryChooser.showDialog(this).toString());
				if (result.isPresent() && result.get() != null) {
					PropertyManager.setProperty(UserProperty.MODELS_DIR.toString(), result.get());
					currentFolderPath.setText(result.get());}				
			} catch (NullPointerException e1) {
				// Nothing to do, this exception is raised, when you escape the directorychooser
			}
		}, "resources/gif/Package.gif");
		
	//	saveTabContentGrid.add(checkboxLabel, 0, 0, 3, 1);
	//	saveTabContentGrid.add(loadModels, 3, 0);
		saveTabContentGrid.add(saveLabel, 0, 1);
		saveTabContentGrid.add(infoButton, 1, 1);
		saveTabContentGrid.add(currentFolderPath, 2, 1);
		saveTabContentGrid.add(chooseSaveDirectory, 3, 1);
		directoriesTab.setContent(saveTabContentGrid);
	}

	private void showInfoDialog(ActionEvent actionEvent) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("Set path to saved models");
		alert.setContentText(
				"All files in the path and all subfolders with .xml extension are tried to be loaded by the start of XModeler."
						+ "\n All Folders which name start with \".\" are not included.");
		alert.showAndWait();
	}

	private void buildUserInterfaceTab(Tab userInterfaceTab) {
		VBox userInterfaceTabContent = new VBox();
		GridPane userInterfaceAppearanceGrid = buildUserInterfaceAppearanceGrid();
		userInterfaceTabContent.getChildren().addAll(userInterfaceAppearanceGrid);
		userInterfaceTab.setContent(userInterfaceTabContent);
	}

	private GridPane buildUserInterfaceAppearanceGrid() {
		GridPane userInterfaceAppearanceGrid = new GridPane();
		formatGrid(userInterfaceAppearanceGrid);

		Label header = new Label(
				"User Interface appearance (confirm changes on enter)" + "\n Changes are applied on restart!");
		header.setStyle("-fx-font-weight: bold");

		Label toolX = new Label("Screen_X: ");
		TextField toolXField = new TextField(PropertyManager.getProperty("toolX"));
		toolXField.setMaxWidth(80);
		toolXField.setOnAction(e -> PropertyManager.setProperty("toolX", toolXField.getCharacters().toString()));

		Label toolY = new Label("Screen_Y: ");
		TextField toolYField = new TextField(PropertyManager.getProperty("toolY"));
		toolYField.setMaxWidth(80);
		toolYField.setOnAction(e -> PropertyManager.setProperty("toolY", toolYField.getCharacters().toString()));

		Label toolWidth = new Label("Tool Width: ");
		TextField toolWidthField = new TextField(PropertyManager.getProperty("toolWidth"));
		toolWidthField.setMaxWidth(80);
		toolWidthField.setOnAction(e -> PropertyManager.setProperty("toolWidth", toolWidthField.getCharacters().toString()));

		Label toolHeight = new Label("Tool Height: ");
		TextField toolHeightField = new TextField(PropertyManager.getProperty("toolHeight"));
		toolHeightField.setMaxWidth(80);
		toolHeightField.setOnAction(e -> PropertyManager.setProperty("toolHeight", toolHeightField.getCharacters().toString()));

		Separator separator = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		
		Label closingLabel  = new Label("Show warning on application closing?");
		CheckBox closingCheckBox = new CheckBox();
		closingCheckBox.setSelected(Boolean.valueOf(PropertyManager.getProperty(UserProperty.APPLICATION_CLOSING_WARNING.toString())));
		closingCheckBox.setOnAction(e -> PropertyManager.setProperty(UserProperty.APPLICATION_CLOSING_WARNING.toString(), String.valueOf(closingCheckBox.isSelected())));

		userInterfaceAppearanceGrid.add(header, 0, 0);
		userInterfaceAppearanceGrid.add(toolX, 0, 1);
		userInterfaceAppearanceGrid.add(toolXField, 0, 1);
		GridPane.setHalignment(toolXField, HPos.LEFT);
		GridPane.setHalignment(toolXField, HPos.RIGHT);
		userInterfaceAppearanceGrid.add(toolY, 0, 2);
		userInterfaceAppearanceGrid.add(toolYField, 0, 2);
		GridPane.setHalignment(toolYField, HPos.RIGHT);
		userInterfaceAppearanceGrid.add(toolWidth, 0, 3);
		userInterfaceAppearanceGrid.add(toolWidthField, 0, 3);
		GridPane.setHalignment(toolWidthField, HPos.RIGHT);
		userInterfaceAppearanceGrid.add(toolHeight, 0, 4);
		userInterfaceAppearanceGrid.add(toolHeightField, 0, 4);
		GridPane.setHalignment(toolHeightField, HPos.RIGHT);
		userInterfaceAppearanceGrid.add(separator, 0, 5);
		userInterfaceAppearanceGrid.add(closingLabel, 0, 6,2,1);
		userInterfaceAppearanceGrid.add(closingCheckBox, 2, 6);
		return userInterfaceAppearanceGrid;
	}

	private void formatGrid(GridPane grid) {
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setVgap(5);
		grid.setHgap(5);
	}
}