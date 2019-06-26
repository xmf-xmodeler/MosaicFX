package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOwnerDialogResult;

public class ChangeOwnerDialog extends CustomDialog<ChangeOwnerDialogResult>{
	
	private DialogPane dialogPane;
	private final DialogType type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;
	
	private Label classLabel;
	private Label currentOwneLabel;
	private Label newOwnerLabel;
	
	private TextField classNameTextfield;
	private TextField currentOwnerTextField;
	private ComboBox<String> newOwnerComboBox;
	
	
	public ChangeOwnerDialog(FmmlxDiagram diagram, FmmlxObject object, DialogType type) {
		super();
		this.diagram= diagram;
		this.object = object;
		this.type = type;
		
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
				
				case Attribute:
					setResultAddAttribute(dlgBtn);
					break;
				case Operation:
					SetResultAddOperation(dlgBtn);
					break;
				default:
					System.err.println("AddDialogResult: No matching content type!");	
				}
			}
			return null;
		});
		
	}


	private void SetResultAddOperation(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
	}


	private void setResultAddAttribute(ButtonType dlgBtn) {
		if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
			//TODO
		}
		
	}


	private boolean validateUserInput() {
		switch (type) {
			case Attribute:
				return validateAddAttribute();
			case Operation:
				return validateAddOperation();
			default:
				System.err.println("AddDialog: No matching content type!");	
		}
		return false;
	}


	private boolean validateAddOperation() {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean validateAddAttribute() {
		// TODO Auto-generated method stub
		return false;
	}


	private void layoutContent() {
		classLabel = new Label("Class");
		currentOwneLabel = new Label("Current Owner");
		newOwnerLabel = new Label("New Owner");
		
		classNameTextfield = new TextField();
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		currentOwnerTextField =  new TextField();
		currentOwnerTextField.setText(object.getName());
		currentOwnerTextField.setDisable(true);
		newOwnerComboBox = new ComboBox<String>();
		
		newOwnerComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);
		grid.add(currentOwneLabel, 0, 1);
		grid.add(currentOwnerTextField, 1, 1);
		grid.add(newOwnerLabel, 0, 2);
		grid.add(newOwnerComboBox, 1, 2);
		switch (type) {		
		case Attribute:
			dialogPane.setHeaderText("Change Attribute Owner");
			break;
		case Operation:
			dialogPane.setHeaderText("Change Operation Owner");
			break;
		default:
			System.err.println("AddDialog: No matching content type!");	
		}
		
	}

}
