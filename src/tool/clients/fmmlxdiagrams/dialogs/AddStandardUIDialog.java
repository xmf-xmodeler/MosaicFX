package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Optional;
import java.util.Vector;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.CanvasElement;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;

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
	private ListView<FmmlxAssociation> lvAssociations = new ListView<>();

	private Label lblSelectedClasses = new Label("Selected Classes");
	private ListView<FmmlxObject> selectedLVClasses = new ListView<>();

	private Label lblSelectedAssociations = new Label("Selected Associations");
	private ListView<FmmlxAssociation> selectedLVAssociations = new ListView<>();

	private Label lblRoot = new Label("Root(s)");
	private ListView<FmmlxObject> lvRoot = new ListView<>();

	private CheckBox checkDistance = new CheckBox("find associated classes recursively");
	private Label lblDistance = new Label("Distance");
	private TextField textDistance = new TextField();

	private CheckBox checkHeight = new CheckBox("find related meta classes recursively");
	private Label lblHeight = new Label("Height");
	private TextField textHeight = new TextField();

	private Label lblPathGUI = new Label("Path to GUI");
	private TextField textPathGUI = new TextField();

	private Label lblPathIcon = new Label("Path to Icon");
	private TextField textPathIcon = new TextField();

	private Label lblTitleGUI = new Label("Titel of Standard GUI");
	private TextField textTitleGUI = new TextField();

	private Vector<FmmlxObject> listClasses = new Vector<>();
	private Vector<FmmlxAssociation> listAssociations = new Vector<>();

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

		dialog.setMaxWidth(320);
		dialog.minWidth(320);
		dialog.prefWidth(320);
		
		// TODO effect of checkmark when transmitting result
		// TODO add support for selecting paths
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
			}
			catch (Exception e) {
				System.err.println(e);
				continue;
			}
		}
		
		for (FmmlxObject object : selectedObjects) {
			listClasses.add(object);
			for (FmmlxAssociation assoc : object.getAllRelatedAssociations()) {
				if (assoc.getSourceNode().equals(object) && !listAssociations.contains(assoc))
					listAssociations.add(assoc);
			}
		}

		selectedLVAssociations.getItems().addAll(listAssociations);
		selectedLVClasses.getItems().addAll(selectedObjects);

	}

	private void layoutContent() {

		// basic grid
		grid.setPrefSize(300, 150);
		grid.setMinSize(300, 150);

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10));
		// grid.autosize();

		grid.add(lblTitleGUI, 0, 0, 1, 1);
		grid.add(textTitleGUI, 1, 0, 1, 1);

		grid.add(lblPathIcon, 0, 1, 1, 1);
		grid.add(textPathIcon, 1, 1, 1, 1);

		grid.add(lblPathGUI, 0, 2, 1, 1);
		grid.add(textPathGUI, 1, 2, 1, 1);

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

		advancedGrid.add(checkHeight, 0, 4, 2, 1);
		advancedGrid.add(lblHeight, 0, 5, 1, 1);
		advancedGrid.add(textHeight, 1, 5, 1, 1);

		titledPane.setContent(advancedGrid);
		grid.add(titledPane, 0, 3, 2, 1);

		dialog.setContent(grid);
	}

	private void initContent() {
		// TODO add editable status when distance checkmark is set
		
		titledPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
			if (isNowExpanded) {
				// resize
				this.setWidth(700);
				this.setHeight(700);

			} else {
				// resize back
				this.setWidth(320);
				this.setHeight(250);
			}
		});
	}

	private void addOKButtonListener() {

		// TBD: abfangen von fehlerhaften eingaben

		setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {

				try {

					Result result = new Result(textDistance.getText(), textHeight.getText(), lvRoot.getItems(),
							textPathIcon.getText(), textTitleGUI.getText(), selectedLVClasses.getItems(),
							selectedLVAssociations.getItems());
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
					FmmlxAssociation selectedItem = lvAssociations.getSelectionModel().getSelectedItem();
					selectedLVAssociations.getItems().add(selectedItem);
					lvAssociations.getItems().remove(selectedItem);
				}
			});
			selectedLVAssociations.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					FmmlxAssociation selectedItem = selectedLVAssociations.getSelectionModel().getSelectedItem();
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
		public String titleGUI;
		public Vector<FmmlxObject> selectedObjects = new Vector<>();
		public Vector<FmmlxAssociation> selectedAssociations = new Vector<>();

		public Result(String distance, String height, ObservableList<FmmlxObject> root, String pathIcon, String titleGUI,
				ObservableList<FmmlxObject> selectedObjects, ObservableList<FmmlxAssociation> selectedAssociations) {

			try {
				this.distance = Integer.parseInt(distance);
			} catch (Exception e) {
				this.distance = 0;
			}

			try {
				Integer.parseInt(height);
			} catch (Exception e) {
				this.height = 0;
			}

			if (!root.isEmpty()) {
				for (FmmlxObject o : root) {
					this.root.add(o);
				}
			}
			
			this.pathIcon = pathIcon;
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