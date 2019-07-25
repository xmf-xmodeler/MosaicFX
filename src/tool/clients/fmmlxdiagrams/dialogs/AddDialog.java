package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.AddDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class AddDialog extends CustomDialog<AddDialogResult> {
	private DialogPane dialogPane;
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	private PropertyType type;
	//For All
	private Label classLabel; //except add class
	private Label nameLabel;
	private Label typeLabel;
	private Label levelLabel;
	private Label multiplicityLabel; //just for add attribute and association

	private TextField classTextField; //except add class
	private TextField nameTextField;
	private ComboBox<String> typeComboBox;
	private ComboBox<Integer> levelComboBox;
	private Button multiplicityButton; //just for add attribute and association


	//For add Attribute
	ObservableList<String> classList;
	private List<String> typesArray;

	//For add operation
	private Label bodyLabel;

	private ComboBox<String> ownerComboBox;
	private TextArea bodyTextArea;

	//For add Association
	private Label targetLabel;
	private ComboBox<FmmlxObject> targetComboBox;


	private Multiplicity multiplicity = Multiplicity.OPTIONAL;

	public AddDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type2) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.type = type2;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();

	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				switch (type) {
					case Operation:
						return new AddDialogResult(object, nameTextField.getText(),
								levelComboBox.getSelectionModel().getSelectedItem(),
								typeComboBox.getSelectionModel().getSelectedItem(),
								bodyTextArea.getText());
					case Association:
						setResultAddAssociation(dlgBtn);
						break;
					default:
						System.err.println("AddDialogResult: No matching content type!");
				}
			}
			return null;
		});
	}

	private void setResultAddAssociation(ButtonType dlgBtn) {
		// TODO Auto-generated method stub
	}


	private void layoutContent() {
		switch (type) {
			case Operation:
				generateLayoutAddOperation();
				break;
			case Association:
				generateLayoutAddAssociation();
				break;
			default:
				System.err.println("AddDialog: No matching content type!");
		}

	}

	//generate Layout Content


	private void generateLayoutAddAssociation() {
		objects = diagram.getObjects();

		ObservableList<FmmlxObject> targetList;
		targetList = FXCollections.observableList(objects);

		dialogPane.setHeaderText("Add Association");
		classLabel = new Label("Class");
		nameLabel = new Label("Name");
		typeLabel = new Label("Type");
		targetLabel = new Label("Target");
		levelLabel = new Label("Level");
		multiplicityLabel = new Label("Multiplicity");

		String[] types = new String[]{"Integer", "String", "Boolean", "Double", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> typeList = FXCollections.observableArrayList(typesArray);

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		nameTextField = new TextField();
		typeComboBox = new ComboBox<>(typeList);
		typeComboBox.setEditable(true);
		targetComboBox = (ComboBox<FmmlxObject>) initializeComboBox(targetList);
		levelComboBox = new ComboBox<>(LevelList.getLevelInterval(object));
		multiplicityButton = new Button();
		multiplicityButton.setText(multiplicity.getClass().getSimpleName().toString());

		targetComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(nameLabel, 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(typeLabel, 0, 2);
		grid.add(typeComboBox, 1, 2);
		grid.add(targetLabel, 0, 3);
		grid.add(targetComboBox, 1, 3);
		grid.add(levelLabel, 0, 4);
		grid.add(levelComboBox, 1, 4);
		grid.add(multiplicityLabel, 0, 5);
		grid.add(multiplicityButton, 1, 5);

	}

	private void generateLayoutAddOperation() {
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.newOperation);

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		nameTextField = new TextField();

		typeComboBox = new ComboBox<>(ElementList.elementList);
		typeComboBox.getSelectionModel().select("Element");
		levelComboBox = new ComboBox<>(LevelList.generateLevelListToThreshold(0, object.getLevel()));
		bodyTextArea = new TextArea(StringValueDialog.OperationStringValues.emptyOperation);
		Button checkSyntaxButton = new Button(StringValueDialog.LabelAndHeaderTitle.checkSyntax);
		checkSyntaxButton.setOnAction(event -> AddDialog.this.checkBodySyntax());
		checkSyntaxButton.setPrefWidth(COLUMN_WIDTH * 0.5);
		Button defaultOperationButton = new Button(StringValueDialog.LabelAndHeaderTitle.defaultOperation);
		defaultOperationButton.setOnAction(event -> AddDialog.this.resetOperationBody());
		defaultOperationButton.setPrefWidth(COLUMN_WIDTH * 0.5);

		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);


		grid.add(new Label(StringValueDialog.LabelAndHeaderTitle.aClass), 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(new Label(StringValueDialog.LabelAndHeaderTitle.name), 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(new Label(StringValueDialog.LabelAndHeaderTitle.type), 0, 2);
		grid.add(typeComboBox, 1, 2);
		grid.add(new Label(StringValueDialog.LabelAndHeaderTitle.level), 0, 3);
		grid.add(levelComboBox, 1, 3);
		grid.add(new Label(StringValueDialog.LabelAndHeaderTitle.body), 0, 4);
		grid.add(bodyTextArea, 1, 4, 1, 4);
		grid.add(checkSyntaxButton, 0, 4);
		grid.add(defaultOperationButton, 0, 5);
	}

	private void checkBodySyntax() {
		if (!isNullOrEmpty(bodyTextArea.getText()) && !bodyTextArea.getText().contentEquals(StringValueDialog.OperationStringValues.emptyOperation)) {
			diagram.checkOperationBody(bodyTextArea.getText());
		}
	}

	private void resetOperationBody() {
		bodyTextArea.setText(StringValueDialog.OperationStringValues.emptyOperation);
	}


	private boolean validateUserInput() {

		switch (type) {
			case Operation:
				return validateAddOperation();
			case Association:
				return validateAddAssociation();
			default:
				System.err.println("AddDialog: No matching content type!");
		}
		return false;
	}

	//Validate user Input to each Add Dialog

	private boolean validateAddAssociation() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateAddOperation() {

		String name = nameTextField.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().operationNameIsAvailable(name, object)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		} else if (getComboBoxStringValue(typeComboBox) == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectType);
			return false;
		} else if (levelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectLevel);
			return false;
		} else if (bodyTextArea.getText().equals("")) {
			errorLabel.setText(StringValueDialog.ErrorMessage.inputBody);
			return false;
		}
		return true;
	}
}
