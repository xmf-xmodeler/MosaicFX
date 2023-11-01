package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Level;

public class CreateMetaClassDialog extends CustomDialog<CreateMetaClassDialog.Result> {

	private AbstractPackageViewer diagram;
	private ObservableList<FmmlxObject> possibleParents;

	private TextField nameTextField;
	private LevelBox levelComboBox;
	private ListView<FmmlxObject> parentListView;
	private CheckBox abstractCheckbox;
	private CheckBox singletonCheckbox;

	public CreateMetaClassDialog(AbstractPackageViewer diagram) {
		super();
		this.diagram = diagram;

		DialogPane dialog = getDialogPane();
		dialog.setHeaderText("New MetaClass");

		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent();

		dialog.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();
	}

	private void layoutContent() {
		Label nameLabel = new Label("Name");
		Label levelLabel = new Label("Level");
		Label abstractLabel = new Label("Abstract");
		Label singletonLabel = new Label("Singleton");
		Label parentLabel = new Label("Parent");

		nameTextField = new TextField();
		parentListView = initializeListView(possibleParents, SelectionMode.MULTIPLE);
		levelComboBox = new LevelBox();
		levelComboBox.setLevelListener(level -> {
			try {
				possibleParents = diagram.getAllPossibleParents(level);
				parentListView.setItems(possibleParents);
				parentListView.setDisable(false);
				if (possibleParents.isEmpty()) {
					parentListView.setDisable(true);
				}
			} catch (Exception ex) {
				//ex.printStackTrace();
			}
		});
		abstractCheckbox = new CheckBox();
		singletonCheckbox = new CheckBox();

		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		

		grid.add(nameLabel, 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(levelLabel, 0, 1);
		grid.add(levelComboBox, 1, 1);
		grid.add(abstractLabel, 0, 2);
		grid.add(abstractCheckbox, 1, 2);
		grid.add(singletonLabel, 0, 3);
		grid.add(singletonCheckbox, 1, 3);
		grid.add(parentLabel, 0, 4);
		grid.add(parentListView, 1, 4);
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new Result(nameTextField.getText(),
						levelComboBox.getLevel(), 
						abstractCheckbox.isSelected(), 
						singletonCheckbox.isSelected(), 
						parentListView.getSelectionModel().getSelectedItems());
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		String name = nameTextField.getText();

		Label errorLabel = getErrorLabel();
		
		if (!InputChecker.isValidIdentifier(name)) {	
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (!InputChecker.getInstance().classNameIsAvailable(name, diagram)) {
			errorLabel.setText("Name already used");
			return false;
//		} else if (getComboBoxIntegerValue(levelComboBox) == null) {
//			errorLabel.setText("Enter level as integer!");
//			return false;
		}
		try { Level.parseLevel(levelComboBox.levelTextField.getText()); }
		catch(Level.UnparseableException le) {
			errorLabel.setText(le.getMessage());
			return false;
		}
		return true;
	}
	
	public class Result {
		public final String name;
		public final Level level;
		public final boolean isAbstract;
		public final boolean isSingleton;
		private final ObservableList<FmmlxObject> parent;

		private Result(String name, Level level, boolean isAbstract, boolean isSingleton, ObservableList<FmmlxObject> parent) {
			this.name = name;
			this.level = level;
			this.isAbstract = isAbstract;
			this.isSingleton = isSingleton;
			this.parent = parent;
		}

		public Vector<String> getParentPaths() {
			Vector<String> parentPaths = new Vector<>();

			if (parent.size() > 0) {
				for (FmmlxObject object : parent) {
					parentPaths.add(object.getPath());
				}
			}
			return parentPaths;
		}

		public Vector<String> getParentNames() {
			Vector<String> parentNames = new Vector<>();

			if (parent.size() > 0) {
				for (FmmlxObject object : parent) {
					parentNames.add(object.getName());
				}
			}
			return parentNames;
		}
	}
}
