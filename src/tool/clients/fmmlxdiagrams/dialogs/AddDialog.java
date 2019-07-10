package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.AddDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class AddDialog extends CustomDialog<AddDialogResult> {
	private DialogPane dialogPane;
	private FmmlxObject object;
	private FmmlxDiagram diagram;
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
	private Label ownerLabel;
	private Label bodyLabel;

	private ComboBox<String> ownerComboBox;
	private TextArea bodyTextArea;

	//For add Association
	private Label targetLabel;
	private ComboBox<String> targetComboBox;


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

		setValidation();
		setResult();

	}

	private void setValidation() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				switch (type) {
					case Class:
						setResultAddMetaClass(dlgBtn);
						break;
					case Attribute:
						setResultAddAttribute(dlgBtn);
						break;
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
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}

	}

	private void setResultAddAttribute(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO AddAttributeDialogResult and Multiplicity Result
			/*return new AddAttributeDialogResult(
					classId,
					nameTextField.getText(), 
					levelComboBox.getSelectionModel().getSelectedItem(),
					typeComboBox.getSelectionModel().getSelectedItem(), multiplicityResult);*/
		}
	}

	private void setResultAddMetaClass(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
	}

	private void layoutContent() {
		switch (type) {
			case Class:
				generateLayoutAddMetaClass();
				break;
			case Attribute:
				generateLayoutAddAttribute();
				break;
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

	private void generateLayoutAddMetaClass() {
		// TODO Auto-generated method stub

	}

	private void generateLayoutAddAssociation() {
		dialogPane.setHeaderText("Add Association");
		classLabel = new Label("Class");
		nameLabel = new Label("Name");
		typeLabel = new Label("Type");
		targetLabel = new Label("Target");
		levelLabel = new Label("Level");
		multiplicityLabel = new Label("Multiplicity");

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		nameTextField = new TextField();
		typeComboBox = new ComboBox<>();
		targetComboBox = new ComboBox<String>();
		levelComboBox = new ComboBox<>(LevelList.levelList);
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

	private void generateLayoutAddAttribute() {
		dialogPane.setHeaderText("Add Attribute");
		nameLabel = new Label("Name");
		classLabel = new Label("Class");
		levelLabel = new Label("Level");
		typeLabel = new Label("Type");
		multiplicityLabel = new Label("Multiplicity");
		//classList = getAllClassList();

		String[] types = new String[]{"Integer", "String", "Boolean", "Double", "Float"};
		typesArray = Arrays.asList(types);
		ObservableList<String> typeList = FXCollections.observableArrayList(typesArray);

		nameTextField = new TextField();
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		levelComboBox = new ComboBox<>(LevelList.levelList);
		typeComboBox = new ComboBox<>(typeList);
		multiplicityButton = new Button();
		//multiplicityButton.setText("Add / Edit Multiplicity");
		multiplicityButton.setText(multiplicity.getClass().getSimpleName().toString());
		multiplicityButton.setOnAction(e -> {
			new MultiplicityDialog(multiplicity).showAndWait();
			/*if successful multiplicity = result;
			 *multiplicityButton.setText(multiplicity.toString());*/
		});
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityButton.setPrefWidth(COLUMN_WIDTH);

		grid.add(nameLabel, 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(levelLabel, 0, 2);
		grid.add(levelComboBox, 1, 2);
		grid.add(typeLabel, 0, 3);
		grid.add(typeComboBox, 1, 3);
		grid.add(multiplicityLabel, 0, 4);
		grid.add(multiplicityButton, 1, 4);
	}

	private ObservableList<String> getAllClassList() {
		ArrayList<String> resultStrings = new ArrayList<String>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				resultStrings.add(object.getName());
			}
		}
		ObservableList<String> result = FXCollections.observableArrayList(resultStrings);
		return result;
	}


	private boolean validateUserInput() {

		switch (type) {
			case Class:
				return validateAddMetaClass();
			case Attribute:
				return validateAddAttribute();
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
		if (!InputChecker.getInstance().validateName(nameTextField.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidName);
			return false;
		} else if (!InputChecker.getInstance().operationNameIsAvailable(nameTextField.getText(), object)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.nameAlreadyUsed);
			return false;
		} else if (levelComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectLevel);
			return false;
		} else if (!InputChecker.getInstance().levelIsValid(levelComboBox.getSelectionModel().getSelectedItem(), object.getLevel() - 1)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.pleaseSelectAnotherLevel);
			return false;
		} else if (typeComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectType);
			return false;
		} else if (isNullOrEmpty(bodyTextArea.getText())) {
			errorLabel.setText(StringValueDialog.ErrorMessage.operationBodyEmpty);
			return false;
		}
		return true;
	}

	private boolean validateAddAttribute() {
		Label errorLabel = getErrorLabel();

		String name = nameTextField.getText();

		if (isNullOrEmpty(name)) {
			errorLabel.setText("Enter valid name!");
			return false;
		} else if (nameAlreadyUsed()) {
			errorLabel.setText("Name already used");
			return false;
		}
		errorLabel.setText("");
		if (levelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText("Select Level!");
			return false;
		}
		errorLabel.setText("");
		if (typeComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText("Select Type!");
			return false;
		}
		errorLabel.setText("");

		return true;

	}

	private boolean nameAlreadyUsed() {
		for (FmmlxAttribute attribute : object.getOwnAttributes()) {
			if (nameTextField.getText().equals(attribute.getName())) return true;
		}
		for (FmmlxAttribute attribute : object.getOtherAttributes()) {
			if (nameTextField.getText().equals(attribute.getName())) return true;

		}
		return false;
	}


	private boolean validateAddMetaClass() {
		// TODO Auto-generated method stub
		return false;
	}


}