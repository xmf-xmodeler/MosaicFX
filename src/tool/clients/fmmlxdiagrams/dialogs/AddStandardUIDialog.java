package tool.clients.fmmlxdiagrams.dialogs;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.CanvasElement;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.customui.DefaultUIModelGenerator;

// dialog for automatic instantiating standard gui
public class AddStandardUIDialog extends Dialog<AddStandardUIDialog.Result> {

	private final AbstractPackageViewer diagram;
	private DialogPane dialog;
	private ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
	private ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	private GridPane grid = new GridPane();
	private TitledPane titledPane = new TitledPane();
	private GridPane advancedGrid = new GridPane();

	private Label lblAssociations = new Label("Associations");
	private ListView<String> lvAssociations = new ListView<>();

	private Label lblSelectedClasses = new Label("Selected Classes");
	private ListView<FmmlxObject> selectedLVClasses = new ListView<>();

	private Label lblSelectedAssociations = new Label("Selected Associations");
	private ListView<String> selectedLVAssociations = new ListView<>();

	private Label lblRoot = new Label("Head(s)");
	private ListView<FmmlxObject> lvRoot = new ListView<>();

	private CheckBox checkDistance = new CheckBox("find associated classes recursively");
	private Label lblDistance = new Label("Distance");
	private TextField textDistance = new TextField();

	private CheckBox checkHeight = new CheckBox("find related meta classes recursively");
	private Label lblHeight = new Label("Height");
	private TextField textHeight = new TextField();

	private Label lblPathGUI = new Label("Path to GUI");
	private TextField textPathGUI = new TextField();
	private Button buttonPathGUI = new Button("Select path");

	private Label lblPathIcon = new Label("Path to Icon");
	private TextField textPathIcon = new TextField();
	private Button buttonPathIcon = new Button("Select path");

	private Label lblTitleGUI = new Label("Title of Standard GUI");
	private TextField textTitleGUI = new TextField();

	private Vector<FmmlxObject> listClasses = new Vector<>();
	private Vector<FmmlxAssociation> listAssociations = new Vector<>();
	private Vector<String> prettyAssocNames = new Vector<>();
	private HashMap<String, FmmlxAssociation> prettyStringMap = new HashMap<>();

	private int windowWidthCollapsed = 380;

	public AddStandardUIDialog(AbstractPackageViewer diagram, Vector<CanvasElement> selectedObjects) {

		super();
		this.diagram = diagram;

		dialog = getDialogPane();

		this.getDialogPane().getButtonTypes().add(okButtonType);
		this.getDialogPane().getButtonTypes().add(cancelButtonType);

		this.setTitle("Generate Standard UI");

		// initial load of LV
		fillListViews(selectedObjects);
		// layout
		layoutContent();
		// Behavior of collapsing advanced menu
		initContent();
		setTableDoubleclickAction(true);
		// save
		addOKButtonListener();

		dialog.setMaxWidth(windowWidthCollapsed);
		dialog.minWidth(windowWidthCollapsed);
		dialog.prefWidth(windowWidthCollapsed);

	}

	private void generateUI() {
		// If model needed for gui is not avaiable instantiate it first

		DefaultUIModelGenerator defaultGenerator = new DefaultUIModelGenerator(diagram);
		defaultGenerator.generateUIModel();

	}

	private void fillListViews(Vector<CanvasElement> canvasElements) {

		lvRoot.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lvAssociations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedLVClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedLVAssociations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// nur Objekte von CommonClass kommen in die Liste
		// ggf. Anpassungen notwendig wenn multilevel ?

		Vector<FmmlxObject> selectedObjects = new Vector<>();

		for (CanvasElement element : canvasElements) {
			try {
				selectedObjects.add((FmmlxObject) element);
			} catch (Exception e) {
				System.err.println(e);
				continue;
			}
		}

		for (FmmlxObject object : selectedObjects) {
			listClasses.add(object);
			for (FmmlxAssociation assoc : object.getAllRelatedAssociations()) {
				if (assoc.getSourceNode().equals(object) && selectedObjects.contains(assoc.getTargetNode())
						&& !listAssociations.contains(assoc))
					listAssociations.add(assoc);
			}
		}

		// pretty Strings for associations

		this.prettyStringMap.clear();

		for (FmmlxAssociation assoc : listAssociations) {
			String pretty = assoc.getSourceNode().getName() + "--" + assoc.getName() + "->"
					+ assoc.getTargetNode().getName();
			this.prettyStringMap.put(pretty, assoc);
			this.prettyAssocNames.add(pretty);
		}

		selectedLVAssociations.getItems().addAll(prettyAssocNames);
		selectedLVClasses.getItems().addAll(selectedObjects);

	}

	private void layoutContent() {

		// basic grid
		grid.setPrefSize(windowWidthCollapsed, 150);
		grid.setMinSize(windowWidthCollapsed, 150);

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10));

		grid.add(lblTitleGUI, 0, 0, 1, 1);
		grid.add(textTitleGUI, 1, 0, 1, 1);

		grid.add(lblPathIcon, 0, 1, 1, 1);
		grid.add(textPathIcon, 1, 1, 1, 1);
		grid.add(buttonPathIcon, 2, 1, 1, 1);
		textPathIcon.setEditable(false);

		grid.add(lblPathGUI, 0, 2, 1, 1);
		grid.add(textPathGUI, 1, 2, 1, 1);
		grid.add(buttonPathGUI, 2, 2, 1, 1);
		textPathGUI.setEditable(false);

		// advanced grid
		titledPane.setText("Advanced");
		titledPane.setExpanded(false);

		advancedGrid.setPadding(new Insets(10));
		advancedGrid.setHgap(10);
		advancedGrid.setVgap(10);

		advancedGrid.add(selectedLVClasses, 0, 0, 1, 1);
		advancedGrid.add(lblSelectedClasses, 0, 1, 1, 1);

		advancedGrid.add(lvAssociations, 1, 0, 1, 1);
		advancedGrid.add(lblAssociations, 1, 1, 1, 1);

		advancedGrid.add(lvRoot, 2, 0, 1, 1);
		advancedGrid.add(lblRoot, 2, 1, 1, 1);

		advancedGrid.add(selectedLVAssociations, 3, 0, 1, 1);
		advancedGrid.add(lblSelectedAssociations, 3, 1, 1, 1);

		advancedGrid.add(checkDistance, 0, 2, 2, 1);
		advancedGrid.add(lblDistance, 0, 3, 1, 1);
		advancedGrid.add(textDistance, 1, 3, 1, 1);

		advancedGrid.add(checkHeight, 2, 2, 2, 1);
		advancedGrid.add(lblHeight, 2, 3, 1, 1);
		advancedGrid.add(textHeight, 3, 3, 1, 1);

		titledPane.setContent(advancedGrid);
		grid.add(titledPane, 0, 3, 3, 1);

		dialog.setContent(grid);
	}

	private void initContent() {

		textDistance.setEditable(false);
		textHeight.setEditable(false);

		// checkmark sets editable
		checkDistance.selectedProperty().addListener((obs, oldValue, newValue) -> {
			textDistance.setEditable(newValue);
		});
		checkHeight.selectedProperty().addListener((obs, oldValue, newValue) -> {
			textHeight.setEditable(newValue);
		});

		// dynamic size of window
		titledPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
			if (isNowExpanded) {
				// resize
				this.setWidth(800);
				this.setHeight(700);

			} else {
				// resize back
				this.setWidth(windowWidthCollapsed + 20);
				this.setHeight(250);
			}
		});

		// file chooser for icon path
		buttonPathIcon.setOnAction(e -> {

			Pane pane = new Pane();
			Scene scene = new Scene(pane);
			Stage stage = new Stage();
			stage.setScene(scene);

			stage.setTitle("Select Path for Icon of Standard GUI");
			stage.setWidth(800);
			stage.setHeight(400);

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select location of icon ");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PNG Image", "*.png"),
					new ExtensionFilter("All Files", "*.*"));

			File file = fileChooser.showSaveDialog(stage);
			String path = "";

			if (file != null) {
				path = file.getPath();
			} else {
				path = "";
			}

			textPathIcon.setText(path);

		});

		// file chooser for gui path
		buttonPathGUI.setOnAction(e -> {

			Pane pane = new Pane();
			Scene scene = new Scene(pane);
			Stage stage = new Stage();
			stage.setScene(scene);

			stage.setTitle("Select Path for extraction of Standard GUI");
			stage.setWidth(800);
			stage.setHeight(400);

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select location for saving the extraction of Standard GUI");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JavaFX as XML", "*.fxml"),
					new ExtensionFilter("All Files", "*.*"));

			File file = fileChooser.showSaveDialog(stage);
			String path = "";

			if (file != null) {
				path = file.getPath();
			} else {
				path = "";
			}

			textPathGUI.setText(path);

		});

	}

	private void addOKButtonListener() {

		// TODO: abfangen von fehlerhaften eingaben

		setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {

				try {

					if (!checkDistance.isSelected())
						textDistance.setText("0");
					if (!checkHeight.isSelected())
						textHeight.setText("-1");

					// convert pretty strings to associations

					Vector<FmmlxAssociation> asscos = new Vector<>();

					for (String pretty : selectedLVAssociations.getItems()) {

						asscos.add(prettyStringMap.get(pretty));

					}

					Result result = new Result(textDistance.getText(), textHeight.getText(), lvRoot.getItems(),
							textPathIcon.getText(), textPathGUI.getText(), textTitleGUI.getText(),
							selectedLVClasses.getItems(), asscos);
					return result;
				} catch (Exception e) {
					System.err.println(e);
					// tbd Add dialog window, wrong input
				}
			}
			return null;
		});
	}

	public void showDialog() {
		Optional<Result> result = showAndWait();
		if (result.isPresent()) {
			new DiagramActions(diagram).instantiateGUI(result);
		}
	}

	private void setTableDoubleclickAction(boolean editable) {

		// doppelclick no effect
		if (!editable) {
			selectedLVClasses.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
				}
			});
			lvRoot.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
				}
			});
			lvAssociations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
				}
			});
			selectedLVAssociations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
				}
			});

		} else {
			// doppelclick moves between selected and not selected listViews
			selectedLVClasses.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					FmmlxObject selectedItem = selectedLVClasses.getSelectionModel().getSelectedItem();
					lvRoot.getItems().add(selectedItem);
					selectedLVClasses.getItems().remove(selectedItem);
				}
			});
			lvRoot.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					FmmlxObject selectedItem = lvRoot.getSelectionModel().getSelectedItem();
					selectedLVClasses.getItems().add(selectedItem);
					lvRoot.getItems().remove(selectedItem);
				}
			});
			lvAssociations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					String selectedItem = lvAssociations.getSelectionModel().getSelectedItem();
					selectedLVAssociations.getItems().add(selectedItem);
					lvAssociations.getItems().remove(selectedItem);
				}
			});
			selectedLVAssociations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					String selectedItem = selectedLVAssociations.getSelectionModel().getSelectedItem();
					lvAssociations.getItems().add(selectedItem);
					selectedLVAssociations.getItems().remove(selectedItem);
				}
			});
		}

	}

	// inner class to store result
	public class Result {

		public int distance;
		public int height;
		public Vector<FmmlxObject> root = new Vector<>();
		public String pathIcon;
		public String pathGUI;
		public String titleGUI;
		public Vector<FmmlxObject> selectedObjects = new Vector<>();
		public Vector<FmmlxAssociation> selectedAssociations = new Vector<>();

		public Result(String distance, String height, ObservableList<FmmlxObject> root, String pathIcon, String pathGUI,
				String titleGUI, ObservableList<FmmlxObject> selectedObjects,
				Vector<FmmlxAssociation> selectedAssociations) {

			try {
				this.distance = Integer.parseInt(distance);
			} catch (Exception e) {
				System.err.println(e + "Distance could not be parsed");
				this.distance = 0;
			}

			try {
				this.height = Integer.parseInt(height);
			} catch (Exception e) {
				this.height = -1;
				System.err.println(e + "Height could not be parsed");
			}

			if (!root.isEmpty()) {
				for (FmmlxObject o : root) {
					this.root.add(o);
				}
			}

			this.pathIcon = pathIcon;
			this.pathGUI = pathGUI;
			this.titleGUI = titleGUI;

			if (!selectedObjects.isEmpty()) {
				for (FmmlxObject o : selectedObjects) {
					this.selectedObjects.add(o);
				}
			}

			if (!selectedAssociations.isEmpty()) {
				for (FmmlxAssociation o : selectedAssociations) {
					this.selectedAssociations.add(o);
				}
			}

		}

	}
}