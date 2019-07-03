package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOwnerDialogResult;

import java.util.ArrayList;
import java.util.Vector;

public class ChangeOwnerDialog extends CustomDialog<ChangeOwnerDialogResult>{
	
	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	private ObservableList<String> ownerList;
	
	private Label classLabel;
	private Label currentOwnerLabel;
	private Label newOwnerLabel;
	
	private TextField classNameTextfield;
	private TextField currentOwnerTextField;
	private ComboBox<String> newOwnerComboBox;
	
	
	public ChangeOwnerDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		super();
		this.object = object;
		this.type = type;
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
		//TODO
	}


	private void setResultAddAttribute(ButtonType dlgBtn) {
		//TODO
		
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
		ownerList =getAllOwnerList();
		classLabel = new Label("Class");
		currentOwnerLabel = new Label("Current Owner");
		newOwnerLabel = new Label("New Owner");
		
		classNameTextfield = new TextField();
		classNameTextfield.setText(object.getName());
		classNameTextfield.setDisable(true);
		currentOwnerTextField =  new TextField();
		currentOwnerTextField.setText(object.getName());
		currentOwnerTextField.setDisable(true);
		newOwnerComboBox = new ComboBox<String>(ownerList);
		
		newOwnerComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classNameTextfield, 1, 0);
		grid.add(currentOwnerLabel, 0, 2);
		grid.add(currentOwnerTextField, 1, 2);
		grid.add(newOwnerLabel, 0, 3);
		grid.add(newOwnerComboBox, 1, 3);
		switch (type) {		
		case Attribute:
			dialogPane.setHeaderText("Change Attribute Owner");
			Label selectAttribute= new Label("Select Attribute");
			ComboBox<String> selectAttributeComboBox = new ComboBox<String>();
			selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
			grid.add(selectAttribute, 0, 1);
			grid.add(selectAttributeComboBox, 1, 1);
			break;
		case Operation:
			dialogPane.setHeaderText("Change Operation Owner");
			Label selectOperation= new Label("Select Operation");
			ComboBox<String> selectOperationComboBox = new ComboBox<String>();
			selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);
			grid.add(selectOperation, 0, 1);
			grid.add(selectOperationComboBox, 1, 1);
			break;
		default:
			System.err.println("AddDialog: No matching content type!");	
		}
		
	}


	private ObservableList<String> getAllOwnerList() {
		ArrayList<String> resultStrings = new ArrayList<String>();
		if (!objects.isEmpty()) {
			for (FmmlxObject object :objects) {
				if (object.getLevel()!=0) {
					resultStrings.add(object.getName());
				}
			}
		}

		ObservableList<String> result = FXCollections.observableArrayList(resultStrings);
		return result;
	}

}
