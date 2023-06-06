package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Optional;
import java.util.Vector;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
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

	private Label lblClasses = new Label("Classes");
	private ListView<FmmlxObject> lvClasses = new ListView<>();

	private Label lblAssociations = new Label("Associations");
	private ListView<FmmlxAssociation> lvAssociations = new ListView<>();

	private Label lblSelectedClasses = new Label("Selected Classes");
	private ListView<FmmlxObject> selectedLVClasses = new ListView<>();

	private Label lblSelectedAssociations = new Label("Selected Associations");
	private ListView<FmmlxAssociation> selectedLVAssociations = new ListView<>();

	private Label lblDistance = new Label("Distance");
	private TextField textDistance = new TextField();

	private Label lblHeight = new Label("Height");
	private TextField textHeight = new TextField();

	private Label lblRoot = new Label("Root");
	private TextField textRoot = new TextField();

	private Label lblPathIcon = new Label("Path to Icon");
	private TextField textPathIcon = new TextField();

	private Label lblTitleGUI = new Label("Titel of Standard GUI");
	private TextField textTitleGUI = new TextField();

	private Vector<FmmlxObject> listClasses = new Vector<>();
	private Vector<FmmlxAssociation> listAssociations = new Vector<>();

	public AddStandardUIDialog(final AbstractPackageViewer diagram) {
		super();
		this.diagram = diagram;
		
		

		dialog = getDialogPane();

		this.getDialogPane().getButtonTypes().add(okButtonType);
		this.getDialogPane().getButtonTypes().add(cancelButtonType);

		// initial load of LV
		fillListViews();
		// listeners
		initContent();
		// layout
		layoutContent();
		// doppelclick moves items
		setTableDoubleclickAction(true);
		// save
		addOKButtonListener();

	}

	private void fillListViews() {
		
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lvAssociations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedLVClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		selectedLVAssociations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// nur Objekte von CommonClass kommen in die Liste
		// ggf. Anpassungen notwendig wenn multilevel ?
		for (FmmlxObject o : this.diagram.getObjects()) {
			if (o.getMetaClassName().equals("CommonClass"))
				listClasses.add(o);
		}
		lvClasses.getItems().addAll(listClasses);
		
		listAssociations.addAll(this.diagram.getAssociations());	
		
		FmmlxAssociation assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "refersToStateOf");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "derivedFrom");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "isChild");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "isParent");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "composedOf");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "representedAs");
		listAssociations.remove(assoc);
		assoc  = diagram.getAssociationByPath(diagram.getPackagePath() + "::" + "uses");
		listAssociations.remove(assoc);
		
		
		lvAssociations.getItems().addAll(listAssociations);
	}

	private void initContent() {
		// listeners to avoid wrong input
		textDistance.textProperty().addListener((observable, oldValue, newValue) -> {

			if (textDistance.getText().equals("") && textRoot.getText().equals("") && textHeight.getText().equals("")) {
				setTableDoubleclickAction(true);
			} else {
				setTableDoubleclickAction(false);
			}
		});
		
		textHeight.textProperty().addListener((observable, oldValue, newValue) -> {

			if (textDistance.getText().equals("") && textRoot.getText().equals("") && textHeight.getText().equals("")) {
				setTableDoubleclickAction(true);
			} else {
				setTableDoubleclickAction(false);
			}
		});
		
		textRoot.textProperty().addListener((observable, oldValue, newValue) -> {

			if (textDistance.getText().equals("") && textRoot.getText().equals("") && textHeight.getText().equals("")) {
				setTableDoubleclickAction(true);
			} else {
				setTableDoubleclickAction(false);
			}
		});
		
		selectedLVAssociations.getItems().addListener(new ListChangeListener<FmmlxAssociation>() {
		    @Override
		    public void onChanged(ListChangeListener.Change change) {
		        // if assocs or classes are choosen no single selection can take place - ensure no wrong inputs
		        if (selectedLVAssociations.getItems().isEmpty() && selectedLVClasses.getItems().isEmpty()) {
		        	textDistance.setEditable(true);
		        	textRoot.setEditable(true);
		        	textHeight.setEditable(true);
		        } else {
		        	textDistance.setEditable(false);
		        	textRoot.setEditable(false);
		        	textHeight.setEditable(false);
		        } 	
		    }
		});
		
		selectedLVClasses.getItems().addListener(new ListChangeListener<FmmlxObject>() {
		    @Override
		    public void onChanged(ListChangeListener.Change change) {
		        // if assocs or classes are choosen no single selection can take place - ensure no wrong inputs
		        if (selectedLVAssociations.getItems().isEmpty() && selectedLVClasses.getItems().isEmpty()) {
		        	textDistance.setEditable(true);
		        	textRoot.setEditable(true);
		        	textHeight.setEditable(true);
		        	
		        } else {
		        	textDistance.setEditable(false);
		        	textRoot.setEditable(false);
		        	textHeight.setEditable(false);
		        	
		        } 	
		    }
		});
	}
	
	private void layoutContent() {

		grid.add(lblClasses, 0, 0, 1, 1);
		grid.add(lvClasses, 0, 1, 1, 1);
		grid.add(lblAssociations, 1, 0, 1, 1);
		grid.add(lvAssociations, 1, 1, 1, 1);

		grid.add(lblSelectedClasses, 2, 0, 1, 1);
		grid.add(selectedLVClasses, 2, 1, 1, 1);
		grid.add(lblSelectedAssociations, 3, 0, 1, 1);
		grid.add(selectedLVAssociations, 3, 1, 1, 1);

		grid.add(lblDistance, 0, 2, 1, 1);
		grid.add(textDistance, 1, 2, 1, 1);

		grid.add(lblHeight, 0, 3, 1, 1);
		grid.add(textHeight, 1, 3, 1, 1);

		grid.add(lblRoot, 0, 4, 1, 1);
		grid.add(textRoot, 1, 4, 1, 1);

		grid.add(lblPathIcon, 0, 5, 1, 1);
		grid.add(textPathIcon, 1, 5, 1, 1);

		grid.add(lblTitleGUI, 0, 6, 1, 1);
		grid.add(textTitleGUI, 1, 6, 1, 1);

		grid.setPadding(new Insets(5));

		dialog.setHeaderText("Generate CustomUI");
		dialog.setContent(grid);
	}

	private void addOKButtonListener() {
		
		// TBD: abfangen von fehlerhaften eingaben
		
		setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {

				try {

					Result result = new Result(textDistance.getText(), textHeight.getText(), textRoot.getText(),
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

	// double click moves to selected and vice versa
	private void setTableDoubleclickAction(boolean editable) {
		
		// doppelclick no effect
		if (!editable) {
			selectedLVClasses.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
				}
			});
			lvClasses.setOnMouseClicked(e -> {
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
					lvClasses.getItems().add(selectedItem);
					selectedLVClasses.getItems().remove(selectedItem);
				}
			});
			lvClasses.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					FmmlxObject selectedItem = lvClasses.getSelectionModel().getSelectedItem();
					selectedLVClasses.getItems().add(selectedItem);
					lvClasses.getItems().remove(selectedItem);
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
		public String root;
		public String pathIcon;
		public String titleGUI;
		public Vector<FmmlxObject> selectedObjects = new Vector<>();
		public Vector<FmmlxAssociation> selectedAssociations = new Vector<>();

		public Result(String distance, String height, String root, String pathIcon, String titleGUI,
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

			this.root = root;
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