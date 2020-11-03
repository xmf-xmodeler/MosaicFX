package tool.clients.fmmlxdiagrams.dialogs.association;

import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeTargetDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ChangeTargetDialog extends CustomDialog<ChangeTargetDialogResult>{
	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	
	private Label classLabel;
	private Label selectAssociationLabel;
	private Label currentTarget;
	private Label newTarget;
	
	private TextField classTextField;
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	private TextField currentTargetTextField;
	private ComboBox<FmmlxObject> newTargetComboBox;

	public ChangeTargetDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		super();
		this.type=type;
		this.object=object;
		this.objects=diagram.getObjects();
		
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
				
				//TODO current targetID still 0, implement get current TargetId
				return new ChangeTargetDialogResult(type, object, selectAssociationComboBox.getSelectionModel().getSelectedItem(), "oldName",
						newTargetComboBox.getSelectionModel().getSelectedItem().getName());
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAssociation);
			return false;
		}else if(newTargetComboBox.getSelectionModel().getSelectedItem()==null ||
				newTargetComboBox.getSelectionModel().getSelectedItem().getName().equals(currentTargetTextField.getText())) {
			errorLabel.setText(StringValue.ErrorMessage.selectNewTarget);
			return false;
		}
		return true;
	}

	private void layoutContent() {
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeAssociationTarget);
		classLabel = new Label(StringValue.LabelAndHeaderTitle.aClass);
		selectAssociationLabel = new Label(StringValue.LabelAndHeaderTitle.selectAssociation);
		currentTarget = new Label("Current Target");
		newTarget = new Label("New Target");
		
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		selectAssociationComboBox = new ComboBox<>();
		currentTargetTextField = new TextField();
		currentTargetTextField.setDisable(true);
		newTargetComboBox = new ComboBox<>();
		
		newTargetComboBox.setPrefWidth(COLUMN_WIDTH);
		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		grid.add(currentTarget, 0, 2);
		grid.add(currentTargetTextField, 1, 2);
		grid.add(newTarget, 0, 3);
		grid.add(newTargetComboBox, 1, 3);
	}

}
