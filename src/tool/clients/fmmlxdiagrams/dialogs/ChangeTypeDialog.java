package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeTypeDialogResult;


public class ChangeTypeDialog extends CustomDialog<ChangeTypeDialogResult>{
	private FmmlxObject object;
	private final PropertyType type;
	private DialogPane dialogPane;
	
	//For all
	private Label classLabel;
	private Label currentTypeLabel;
	private Label newTypeLabel;
	
	private TextField classTextField;
	private TextField currentTypeTextField;
	private ComboBox<String> newTypeComboBox;
	
	//For Attribute
	private Label selectAttributeLabel;
	private ComboBox<String> selectAttributeComboBox;
	
	//For Operation
	private Label selectOperationLabel;
	private ComboBox<String> selectOperationComboBox;
	
	//For Association
	private Label selectAssociationLabel;
	private ComboBox<String> selectAssociationComboBox;

	public ChangeTypeDialog(FmmlxObject object, PropertyType type) {
		super();
		this.object = object;
		this.type = type;
		
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent(type);
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
				
				case Attribute:
					setResultChangeTypeAttribute(dlgBtn);
					break;
				case Operation:
					setResultChangeTypeOperation(dlgBtn);
					break;
				case Association:
					setResultChangeTypeAssociation(dlgBtn);
					break;
				default:
					System.err.println("AddDialogResult: No matching content type!");	
				}
			}
			return null;
		});
	}

	private void setResultChangeTypeAssociation(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
	}

	private void setResultChangeTypeOperation(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
	}

	private void setResultChangeTypeAttribute(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
	}

	private boolean validateUserInput() {
		switch (type) {
		case Attribute:
			return validateChangeTypeAttribute();
		case Operation:
			return validateChangeTypeOperation();
		case Association:
			return validateChangeTypeAssociation();
		default:
			System.err.println("ChangeTypeDialog: No matching content type!");	
		}
	return false;
	}

	private boolean validateChangeTypeAssociation() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateChangeTypeOperation() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validateChangeTypeAttribute() {
		// TODO Auto-generated method stub
		return false;
	}

	private void layoutContent(PropertyType type) {
		classLabel = new Label("Class");
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		currentTypeLabel = new Label("Current Type");
		currentTypeTextField = new TextField();
		currentTypeTextField.setDisable(true);

		newTypeLabel = new Label("Select New Type!");
		newTypeComboBox = new ComboBox<String>();
		
		newTypeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(currentTypeLabel, 0, 2);
		grid.add(currentTypeTextField, 1, 2);
		grid.add(newTypeLabel, 0, 3);
		grid.add(newTypeComboBox, 1, 3);
		switch (type) {
		case Attribute:
			dialogPane.setHeaderText("Change Attribute Type");
			layoutContentChangeTypeAttribute();
			break;
		case Association:
			dialogPane.setHeaderText("Change Association Type");
			layoutContentChangeTypeAssoiation();
			break;
		case Operation:
			dialogPane.setHeaderText("Change Operation Type");
			layoutContentChangeTypeOperation();
		default:
			System.err.println("ChangeTypeDialog: No matching content type!");	
			break;
		}
		
	}

	private void layoutContentChangeTypeAttribute() {
		selectAttributeLabel = new Label("Select Attribute");
		selectAttributeComboBox = new ComboBox<String>();
		
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
	}

	private void layoutContentChangeTypeAssoiation() {
		selectAssociationLabel = new Label("Select Association");
		selectAssociationComboBox = new ComboBox<String>();
		
		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		
	}

	private void layoutContentChangeTypeOperation() {
		selectOperationLabel = new Label("Select Operation");
		selectOperationComboBox = new ComboBox<String>();
		
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectOperationLabel, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		
	}

}
