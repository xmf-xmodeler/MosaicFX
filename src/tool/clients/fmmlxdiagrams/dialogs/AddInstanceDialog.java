package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.Vector;

public class AddInstanceDialog extends CustomDialog<AddInstanceDialog.Result> {

	private final AbstractPackageViewer diagram;
	private FmmlxObject selectedObject;
	private TextField nameTextField;
	private ListView<FmmlxObject> parentListView;
	private ComboBox<FmmlxObject> ofComboBox;
	private LevelBox levelBox;
	private CheckBox abstractCheckBox;
	private CheckBox singletonCheckBox;
	private ObservableList<FmmlxObject> parentList;
	private final Vector<FmmlxObject> objects;

	public AddInstanceDialog(final AbstractPackageViewer diagram, FmmlxObject object) {
		super();

		DialogPane dialog = getDialogPane();
		this.diagram = diagram;
		this.selectedObject = object;
		this.objects = diagram.getObjectsReadOnly();

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
		
		singletonCheckBox = new CheckBox();
		if (selectedClass != null && selectedClass.getLevel().getMaxLevel() == 1) {
		singletonCheckBox.setDisable(true);	
		}
		parentListView = initializeListView(parentList, SelectionMode.MULTIPLE);

		levelBox = new LevelBox(selectedClass.getLevel().minusOne());
		levelBox.levelTextField.setEditable(selectedClass.getLevel().isContingentLevelClass());

		ofComboBox = (ComboBox<FmmlxObject>) initializeComboBox(ofList);
		ofComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedObject = newValue;
				createAndSetParentList();
			}
		});

		ofComboBox.getSelectionModel().selectedItemProperty().addListener((a,b,newClass) -> {
			levelBox.levelTextField.setText(newClass.getLevel().minusOne().toString());
			levelBox.levelTextField.setEditable(newClass.getLevel().isContingentLevelClass());
//			if(level.isContingentLevelClass()) {
//				levelBox.getItems().clear();
//				Integer newLevel = level.getMinLevel() - 1;
//				levelBox.getItems().add(""+newLevel);
//				levelBox.getSelectionModel().select(0);
//				levelBox.setEditable(false);
//			} else if(level.isFixedLevelClass()){
//				levelBox.getItems().clear();
//				levelBox.getItems().add("-1");
//				levelBox.setEditable(true);
//			} else {
//				levelBox.getItems().clear();
//				levelBox.setEditable(false);
//			}
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
		if(!diagram.isUMLMode()) {	//hide for uml diagrams
		grid.add(new Label(StringValue.LabelAndHeaderTitle.level), 0, 2);
		grid.add(levelBox, 1, 2);
		grid.add(new Label("Singleton"), 0, 4);
		grid.add(singletonCheckBox, 1, 4);
		}
		grid.add(new Label(StringValue.LabelAndHeaderTitle.abstractBig), 0, 3);
		grid.add(abstractCheckBox, 1, 3);
		grid.add(new Label(StringValue.LabelAndHeaderTitle.parent), 0, 5);
		grid.add(parentListView, 1, 5);
	}

	private void setInstanceName(FmmlxObject c) {
		nameTextField.setText(c.getAvailableInstanceName());
		
	}

	private void createAndSetParentList() {
		if (selectedObject != null) {
			parentList = diagram.getAllPossibleParents(selectedObject.getLevel().getMinLevel() - 1);
			parentListView.setItems(parentList);
		}
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
		    	Level level = levelBox.getLevel();
		    	if(level != null) {
		    		if(!diagram.isUMLMode()) {
		    		return new Result(nameTextField.getText(), level, 
					parentListView.getSelectionModel().getSelectedItems(), 
					selectedObject.getPath(),
					abstractCheckBox.isSelected(),
					singletonCheckBox.isSelected());
		    		}
		    		else {
			    		return new Result(nameTextField.getText(), new Level(0,0), 
								parentListView.getSelectionModel().getSelectedItems(), 
								selectedObject.getPath(),
								abstractCheckBox.isSelected(),
								false);
		    		}
		    	}
			}
			return null;
		});
	}

	private ObservableList<FmmlxObject> getAllOfList() {
		ArrayList<FmmlxObject> resultOf = new ArrayList<>();
		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.isClass()) {
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

		if (!InputChecker.isValidIdentifier(name)) {
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
//		if(ofComboBox.isDisabled()) return true;
		Label errorLabel = getErrorLabel();

//		if (ofComboBox.getSelectionModel().getSelectedIndex() == -1) {
		if(this.selectedObject == null) {
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
		public final Level level;
		public final ObservableList<FmmlxObject> parents;
		public final String ofPath;
		public final boolean isAbstract;
		public final boolean isSingleton;

		public Result(String name, Level level, ObservableList<FmmlxObject> parents, String ofPath,
									   boolean isAbstract, boolean isSingleton) {
			this.name = name;
			this.level = level;
			this.parents = parents;
			this.ofPath = ofPath;
			this.isAbstract = isAbstract;
			this.isSingleton = isSingleton;
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

		public String getOfPath() {
			return ofPath;
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
