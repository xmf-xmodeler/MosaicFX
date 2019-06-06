package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeLevelDialogResult;

import java.util.ArrayList;
import java.util.Vector;


public class ChangeLevelDialog extends CustomDialog<ChangeLevelDialogResult> {

	private final String type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	private DialogPane dialogPane;

	private Label objectLabel;
	private Label objectLevelLabel;
	private TextField objectNameTextField;
	private TextField objectLevelTextField;


	public ChangeLevelDialog(FmmlxDiagram diagram, FmmlxObject object, String type) {
		// TODO Auto-generated constructor stub
		super();
		this.diagram = diagram;
		this.type = type;
		this.object = object;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);

		setResult(validateUserInput());
	}

	private void setResult(boolean userInputIsValid) {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				// TODO Auto-generated method stub
			}
			return null;
		});
	}

	private boolean validateUserInput() {

		switch (type) {
			case "class":
				return validateClassLevelChange();
			case "attribute":
				return validateAttributeLevelChange();
			case "operation":
				return validateOperationLevelChange();
			case "association":
				return validateAssociationLevelChange();
		}
		return true;
	}

	private boolean validateAssociationLevelChange() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateOperationLevelChange() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateAttributeLevelChange() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateClassLevelChange() {
		// TODO Auto-generated method stub
		return false;
	}

	private void layoutContent() {
		// TODO Auto-generated method stub
		objectLabel = new Label("Class");
		objectNameTextField = new TextField();
		objectLevelLabel = new Label("Current Level");
		objectLevelTextField = new TextField();
		objectNameTextField.setText(object.getName());
		objectNameTextField.setDisable(true);


		grid.add(objectLabel, 0, 0);
		grid.add(objectNameTextField, 1, 0);


		switch (type) {
			case "class":
				changeClassLevel();
				break;
			case "attribute":
				changeAttributeLevel();
				break;
			case "operation":
				changeOperationLevel();
				break;
			case "association":
				changeAssociationLevel();
			default:
				System.err.println("ChangeNameDialog: No matching content type!");
		}

	}

	private void changeAssociationLevel() {
		dialogPane.setHeaderText("Change Association Level");
		// TODO Auto-generated method stub

	}

	private void changeOperationLevel() {
		dialogPane.setHeaderText("Change Operation Level");

		ArrayList<String> operationList = new ArrayList<String>();

		Label selectOperationLabel = new Label("Select Operation");
		Label currentLevelLabel = new Label("Current Level");
		Label newLevelLabel = new Label("Select New Level");

		ComboBox<String> selectOperationComboBox = new ComboBox<String>();
		TextField currentLevelTextField = new TextField();
		currentLevelTextField.setDisable(true);
		ComboBox<String> newLevelComboBox = new ComboBox<String>();

		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);

		Vector<FmmlxOperation> operations;
		operations = object.getOwnOperations();

		// TODO Auto-generated method stub

	}

	private void changeAttributeLevel() {
		dialogPane.setHeaderText("Change Attribute Level");

		ArrayList<String> attributeList = new ArrayList<String>();

		Label selectAttributeLabel = new Label("Select Attribute");
		Label currentLevelLabel = new Label("Current Level");
		Label newLevelLabel = new Label("select New Level");

		ComboBox<String> selectAttributeComboBox = new ComboBox<String>();
		TextField currentLevelTextField = new TextField();
		currentLevelTextField.setDisable(true);
		ComboBox<String> newLevelComboBox = new ComboBox<String>();

		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(currentLevelLabel, 0, 2);
		grid.add(currentLevelTextField, 1, 2);
		grid.add(newLevelLabel, 0, 3);
		grid.add(newLevelComboBox, 1, 3);

		Vector<FmmlxAttribute> attributes;
		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());


		for (FmmlxAttribute fmmlxAttribute : attributes) {
			attributeList.add(fmmlxAttribute.getName());
		}
		// TODO Auto-generated method stub

	}

	private void changeClassLevel() {
		dialogPane.setHeaderText("Change Class Level");
		objectLevelTextField.setText(object.getLevel() + "");
		objectLevelTextField.setDisable(true);

		Label selectLevelLabel = new Label("Select New Level");

		ComboBox<String> newLevelComboBox = new ComboBox<String>();

		newLevelComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(objectLevelLabel, 0, 1);
		grid.add(objectLevelTextField, 1, 1);
		grid.add(selectLevelLabel, 0, 2);
		grid.add(newLevelComboBox, 1, 2);

		// TODO Auto-generated method stub
	}

}
