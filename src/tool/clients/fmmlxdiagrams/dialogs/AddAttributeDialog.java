package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.association.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.*;


public class AddAttributeDialog extends CustomDialog<AddAttributeDialogResult> {

	private Label nameLabel;
	private Label classLabel;
	private Label levelLabel;
	private Label typeLabel;
	private Label multiplicityLabel;
	private Label displayMultiplicityLabel;

	private TextField nameTextField;
	private TextField classTextField;
	private ComboBox<String> levelComboBox;
	private ComboBox<String> typeComboBox;

	private FmmlxObject selectedObject;
	private Button multiplicityButton;
	private Multiplicity multiplicity = Multiplicity.MANDATORY;
	
	private Vector<String> types;

	public AddAttributeDialog(final AbstractPackageViewer diagram) {
		this(diagram, null);
	}

	public AddAttributeDialog(final AbstractPackageViewer diagram, FmmlxObject selectedObject) {
		super();

		types = diagram.getAvailableTypes();

		DialogPane dialogPane = getDialogPane();
		this.selectedObject = selectedObject;

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();

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

				return new AddAttributeDialogResult(
						selectedObject.getName(),
						nameTextField.getText(),
						getIntLevel(),
						getComboBoxStringValue(typeComboBox),
						multiplicity);
			}
			return null;
		});

	}


	private Integer getIntLevel() {
		try{
			return Integer.parseInt(levelComboBox.getSelectionModel().getSelectedItem());
		} catch (Exception e) {
			return null;
		}
	}

	private boolean validateUserInput() {
		if (!validateName()) {
			return false;
		}
		if (!validateLevel()) {
			return false;
		}
		return validateType();
	}


	private boolean validateType() {
		Label errorLabel = getErrorLabel();

		if (getComboBoxStringValue(typeComboBox) == null || getComboBoxStringValue(typeComboBox).length() < 1) {
			errorLabel.setText(StringValue.ErrorMessage.selectType);
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private boolean validateLevel() {
		Label errorLabel = getErrorLabel();
		
		if (getIntLevel() == null) {
			errorLabel.setText(StringValue.ErrorMessage.selectLevel);
			return false;
		}
		errorLabel.setText("");
		return true;
	}


	private boolean validateName() {
		Label errorLabel = getErrorLabel();
		String name = nameTextField.getText();

		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidName);
			return false;
//		} else if (!InputChecker.getInstance().attributeNameIsAvailable(name, selectedObject)) {
//			errorLabel.setText(StringValue.ErrorMessage.nameAlreadyUsed);
//			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void addElementToGrid() {
		nameLabel = new Label(StringValue.LabelAndHeaderTitle.name);
		classLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);
		levelLabel = new Label(StringValue.LabelAndHeaderTitle.level);
		typeLabel = new Label(StringValue.LabelAndHeaderTitle.type);
		multiplicityLabel = new Label(StringValue.LabelAndHeaderTitle.Multiplicity);

		ObservableList<String> typeList = FXCollections.observableArrayList(types);

		nameTextField = new TextField();
		classTextField = new TextField();
		classTextField.setText(selectedObject.getName());
		classTextField.setDisable(true);
//		levelComboBox = new ComboBox<>(AllValueList.getLevelInterval(selectedObject));
//		levelComboBox.setConverter(new IntegerStringConverter());
		levelComboBox = new ComboBox<>();
		for(int i = selectedObject.getLevel() -1 ; i >= 0; i--) {
			levelComboBox.getItems().add(""+i);
		}
		levelComboBox.getSelectionModel().selectLast();
		if(selectedObject.getLevel() == -1) levelComboBox.setEditable(true);
		typeComboBox = new ComboBox<>(typeList);
		typeComboBox.setEditable(true);
		multiplicityButton = new Button();
		multiplicityButton.setText(multiplicity.getClass().getSimpleName());
		multiplicityButton.setOnAction(e -> {
			showMultiplicityDialog();
		});
		displayMultiplicityLabel = new Label(multiplicity.toString());

		classTextField.setPrefWidth(COLUMN_WIDTH);
		levelComboBox.setPrefWidth(COLUMN_WIDTH);
		typeComboBox.setPrefWidth(COLUMN_WIDTH);
		multiplicityButton.setPrefWidth(COLUMN_WIDTH);
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		labelNode.add(nameLabel);
		labelNode.add(classLabel);
		labelNode.add(levelLabel);
		labelNode.add(typeLabel);
		labelNode.add(multiplicityLabel);
		
		editorNode.add(nameTextField);
		editorNode.add(classTextField);
		editorNode.add(levelComboBox);
		editorNode.add(typeComboBox);
		editorNode.add(multiplicityButton);
		editorNode.add(displayMultiplicityLabel);
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(editorNode, 1);
		
	}

	private void showMultiplicityDialog() {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<MultiplicityDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			MultiplicityDialogResult result = opt.get();

			multiplicity = result.convertToMultiplicity();
			displayMultiplicityLabel.setText(multiplicity.toString());
		}
	}
}
