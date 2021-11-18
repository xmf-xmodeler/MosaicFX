package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class RemoveDialog<Property extends FmmlxProperty> extends CustomDialog<RemoveDialog<Property>.Result> {

	private DialogPane dialogPane;
	private FmmlxObject object;

	private Label selectObjectLabel;
	private TextField selectObjectLabelTextField;
	private ComboBox<Property> propertyBox;
	private Label selectPropertyLabel;

	private final PropertyType propertyType;
	
	public RemoveDialog(FmmlxObject object, PropertyType propertyType) {
		super();
		this.propertyType = propertyType;
		this.object = object;
		dialogPane = getDialogPane();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		
		if(propertyType == PropertyType.Class) 
			addElementsToGridForClassOnly();
		else
			addElementsToGrid();
		
		dialogPane.setContent(flow);


		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				if(propertyType == PropertyType.Class)
					return new Result(object, null);
				else return new Result(object, propertyBox.getSelectionModel().getSelectedItem());
			}
			return null;
		});
	}


	@SuppressWarnings("unchecked")
	private void addElementsToGrid() {
		dialogPane.setHeaderText("Remove " + propertyType.toString());
		selectObjectLabel = new Label("Selected Object");
		selectPropertyLabel = new Label("Select " + propertyType.toString());

		selectObjectLabelTextField = new TextField();
		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
		
		ObservableList<Property> list = null;
		
		if(propertyType == PropertyType.Attribute)   list = (ObservableList<Property>) FXCollections.observableList(object.getOwnAttributes());
		if(propertyType == PropertyType.Association) list = (ObservableList<Property>) FXCollections.observableList(object.getAllRelatedAssociations());
		if(propertyType == PropertyType.Operation)   list = (ObservableList<Property>) FXCollections.observableList(object.getOwnOperations());
		if(propertyType == PropertyType.Constraint)  list = (ObservableList<Property>) FXCollections.observableList(object.getConstraints());

		propertyBox = initializeComboBox(list);
		propertyBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);
		grid.add(selectPropertyLabel, 0, 1);
		grid.add(propertyBox, 1, 1);
	}

	private void addElementsToGridForClassOnly() {
		dialogPane.setHeaderText("Remove Class");

		selectObjectLabel = new Label("Selected Object");
		selectObjectLabelTextField = new javafx.scene.control.TextField();

		grid.add(selectObjectLabel, 0, 0);
		grid.add(selectObjectLabelTextField, 1, 0);

		selectObjectLabelTextField.setPrefWidth(COLUMN_WIDTH);

		selectObjectLabelTextField.setText(object.getName());
		selectObjectLabelTextField.setDisable(true);
	}

	private boolean validateUserInput() {
		if(propertyType == PropertyType.Class)
			return true;
		return propertyBox.getSelectionModel().getSelectedItem() != null;
	}

	public void setSelected(Property property) {
		propertyBox.getSelectionModel().select(property);
	}
	
	public class Result {
		
		public final FmmlxObject object;
		public final Property property;
		
		public Result(FmmlxObject object, Property property) {
			this.object=object;
			this.property=property;
		}
	}
}
