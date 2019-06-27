package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.results.RemoveDialogResult;

import java.util.ArrayList;
import java.util.Vector;

import com.ibm.icu.text.SelectFormat;

public class RemoveDialog extends CustomDialog<RemoveDialogResult> {

	private DialogPane dialogPane;
	private final DialogType type;
	private FmmlxObject object;
	private final FmmlxDiagram diagram;
	
	//For All

	private Label selectObjectLabel;
	private Label selectionForStrategies;
	private TextField selectObjectLabelTextField;
	private ComboBox<String> selectionForStrategiesComboBox;
	
	//For Association
	private Label selectAssociationLabel;
	private ComboBox<String> selectAssociationComboBox;
	

	private Vector<FmmlxAttribute> attributes;
	private Vector<FmmlxOperation> operations;

	private ArrayList<String> attributeList;
	private ArrayList<String> operationList;


	public RemoveDialog(final FmmlxDiagram diagram, FmmlxObject object, DialogType type) {
		super();
		this.type = type;
		this.diagram = diagram;
		this.object = object;
		dialogPane = getDialogPane();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();

		dialogPane.setContent(grid);


		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
			}
			return null;
		});
	}


	private void addElementToGrid() {

		switch (type) {
			case Class:
				removeClass();
				break;
			case Attribute:
				removeAttribute();
				break;
			case Association:
				removeAssoiation();
				break;
			case Operation:
				removeOperation();
			default:
				break;
		}
	}

	private void removeAssoiation() {
		// TODO Auto-generated method stub
		dialogPane.setHeaderText("Remove Association");
		selectObjectLabel = new Label("Selected Object");
		selectAssociationLabel = new Label("Select Association");
		selectionForStrategies = new Label("Selection for Strategies");
		
		selectObjectLabelTextField = new TextField();
		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
		selectAssociationComboBox = new ComboBox<String>();
		selectionForStrategiesComboBox = new ComboBox<String>();
		
		selectAssociationComboBox.setPrefWidth(COLUMN_WIDTH);
		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectAssociationLabel, 0, 1);
		grid.add(selectAssociationComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

	}


	private void removeOperation() {
		dialogPane.setHeaderText("Remove Operation");

		attributeList = new ArrayList<String>();

		selectObjectLabel = new Label("Selected Object");
		Label selectOperation = new Label("Select Operation");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new TextField();
		ComboBox<String> selectOperationComboBox = new ComboBox<String>();
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectOperation, 0, 1);
		grid.add(selectOperationComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);
		selectOperationComboBox.setPrefWidth(COLUMN_WIDTH);

		operations = object.getOwnOperations();

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);

		for (FmmlxOperation fmmlxOperation : operations) {
			//TODO operationList.add(fmmlxOperation.getName());
		}
		//selectOperationComboBox.getItems().setAll(operationList);

	}


	private void removeAttribute() {
		dialogPane.setHeaderText("Remove Attribute");

		attributeList = new ArrayList<String>();

		selectObjectLabel = new Label("Selected Object");
		Label selectAttribute = new Label("Select Attribute");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new javafx.scene.control.TextField();
		ComboBox<String> selectAttributeComboBox = new ComboBox<String>();
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectAttribute, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(selectionForStrategies, 0, 2);
		grid.add(selectionForStrategiesComboBox, 1, 2);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());


		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);

		for (FmmlxAttribute fmmlxAttribute : attributes) {
			attributeList.add(fmmlxAttribute.getName());
		}
		selectAttributeComboBox.getItems().setAll(attributeList);

	}


	private void removeClass() {
		dialogPane.setHeaderText("Remove Class");

		selectObjectLabel = new Label("Selected Object");
		selectionForStrategies = new Label("Selection for Strategies");
		selectObjectLabelTextField = new javafx.scene.control.TextField();
		selectionForStrategiesComboBox = new ComboBox<>();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectionForStrategies, 0, 1);
		grid.add(selectionForStrategiesComboBox, 1, 1);

		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);

	}


	private boolean validateUserInput() {

		switch (type) {
			case Class:
				return validateRemoveClass();
			case Attribute:
				return validateRemoveAttribute();
			case Operation:
				return validateRemoveOperation();
			case Association:
				return validateRemoveAssociation();
		}
		return true;

	}


	private boolean validateRemoveAssociation() {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean validateRemoveOperation() {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean validateRemoveAttribute() {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean validateRemoveClass() {
		// TODO Auto-generated method stub
		return false;
	}


}
