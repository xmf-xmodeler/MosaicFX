package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.Vector;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialog.Result> {

	private final AbstractPackageViewer diagram;
	private FmmlxObject selectedObject;
	private TextField nameTextField;
	private ListView<FmmlxObject> parentListView;
	private ComboBox<FmmlxObject> ofComboBox;
	private ComboBox<String> levelBox;
	private CheckBox abstractCheckBox;
	private ObservableList<FmmlxObject> parentList;
	private final Vector<FmmlxObject> objects;

	public AddInstanceDialog(final AbstractPackageViewer diagram, FmmlxObject object) {
		super();

		DialogPane dialog = getDialogPane();
		this.diagram = diagram;
		this.selectedObject = object;
		this.objects = diagram.getObjects();

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent(object);
		dialog.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();
	}

	private void layoutContent(FmmlxObject selectedClass) {

		ObservableList<FmmlxObject> ofList = getAllOfList();
		nameTextField = new TextField();
		abstractCheckBox = new CheckBox();
		parentListView = initializeListView(parentList, SelectionMode.MULTIPLE);

		levelBox = new ComboBox<>();
		
		ofComboBox = (ComboBox<FmmlxObject>) initializeComboBox(ofList);
		ofComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedObject = newValue;
				createAndSetParentList();
			}
		});

		ofComboBox.getSelectionModel().selectedItemProperty().addListener((a,b,newClass) -> {
			int level = newClass.getLevel();
			if(level > 0) {
				levelBox.getItems().clear();
				Integer newLevel = level - 1;
				levelBox.getItems().add(""+newLevel);
				levelBox.getSelectionModel().select(0);
				levelBox.setEditable(false);
			} else if(level == -1){
				levelBox.getItems().clear();
				levelBox.getItems().add("-1");
				levelBox.setEditable(true);
			} else {
				levelBox.getItems().clear();
				levelBox.setEditable(false);
			}
		});
		
		if (selectedClass != null) {
			setOf(selectedClass);
			createAndSetParentList();
			ofComboBox.setDisable(true);
			setInstanceName(selectedClass);
		}
		ofComboBox.setPrefWidth(COLUMN_WIDTH);
		


		grid.add(new Label(StringValue.LabelAndHeaderTitle.name), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.of), 0, 1);
		grid.add(ofComboBox, 1, 1);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.level), 0, 2);
		grid.add(levelBox, 1, 2);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.abstractBig), 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.parent), 0, 4);
		grid.add(parentListView, 1, 4);
	}

	private void setInstanceName(FmmlxObject c) {
		nameTextField.setText(c.getAvailableInstanceName());
		
	}

	private void createAndSetParentList() {
		if (selectedObject != null) {
			parentList = diagram.getAllPossibleParents(selectedObject.getLevel() - 1);
			parentListView.setItems(parentList);
		}
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				Integer level = -1;
				try{level = Integer.parseInt(levelBox.getSelectionModel().getSelectedItem()); } catch (Exception e) {}
				return new Result(nameTextField.getText(), level, 
						parentListView.getSelectionModel().getSelectedItems(), selectedObject.getName(),
						abstractCheckBox.isSelected());
			}
			return null;
		});
	}

	private ObservableList<FmmlxObject> getAllOfList() {
		ArrayList<FmmlxObject> resultOf = new ArrayList<>();
		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					resultOf.add(object);
				}
			}
		}

		return FXCollections.observableArrayList(resultOf);
	}


	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!ofSelected()) {
			return false;
		}
		return validateCircularDependecies();
	}

	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean ofSelected() {
		Label errorLabel = getErrorLabel();

		if (ofComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValue.ErrorMessage.selectOf);
			return false;
		}
		errorLabel.setText("");
		return true;
	}


	private void setOf(FmmlxObject selectedObject) {

		ofComboBox.setValue(selectedObject);

		ofComboBox.setEditable(false);
	}


	private boolean validateCircularDependecies() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public class Result {

		public final String name;
		public final int level;
		public final ObservableList<FmmlxObject> parents;
		public final String ofName;
		public final boolean isAbstract;

		public Result(String name, int level, ObservableList<FmmlxObject> parents, String ofName,
									   boolean isAbstract) {
			this.name = name;
			this.level = level;
			this.parents = parents;
			this.ofName = ofName;
			this.isAbstract = isAbstract;
		}

		public Vector<String> getParentPaths() {
			Vector<String> parentPaths = new Vector<>();

			if (!parents.isEmpty()) {
				for (FmmlxObject o : parents) {
					parentPaths.add(o.getPath());
				}
			}
			return parentPaths;
		}

		public String getOfName() {
			return ofName;
		}

		public Vector<String> getParentNames() {
			Vector<String> parentnames = new Vector<>();

			if (!parents.isEmpty()) {
				for (FmmlxObject o : parents) {
					parentnames.add(o.getName());
				}
			}
			return parentnames;
		}
	}
}
