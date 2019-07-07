package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOfDialogResult;


public class ChangeOfDialog extends CustomDialog<ChangeOfDialogResult>{
	
	private FmmlxObject object;
	private final FmmlxDiagram diagram; 
	private DialogPane dialogPane;
	
	private Label selectedObjectLabel;
	private Label currentOf;
	private Label newOf;
	private Label errorLabel;
	private TextField selectedObjectTextField;
	private TextField currentOfTextField;
	private ComboBox<FmmlxObject>newOfComboBox;
	

	public ChangeOfDialog(FmmlxDiagram diagram, FmmlxObject object) {
		super();
		this.object=object;
		this.diagram=diagram;
		
		dialogPane= getDialogPane();
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		addElementToLayout();

		dialogPane.setContent(grid);
		
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new ChangeOfDialogResult(object, object.getOf(), newOfComboBox.getSelectionModel().getSelectedItem());
			}
			return null;
		});
	}


	private boolean validateUserInput() {
		
		if (newOfComboBox.getSelectionModel().isEmpty()) {
			errorLabel.setText(StringValue.LabelAndHeaderTitle.selectNewOf);
			return false;
		}
		
		return true;
	}


	private void addElementToLayout() {
		
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeOf);
		
		selectedObjectLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);
		currentOf = new Label("Current Of");
		newOf = new Label("New Of");
		errorLabel = getErrorLabel();
		
		selectedObjectTextField= new TextField();
		selectedObjectTextField.setText(object.getName());
		selectedObjectTextField.setDisable(true);
		currentOfTextField = new TextField();
		
		for(FmmlxObject fmmlxObject : diagram.getObjects()) {
			if (object.getOf()==fmmlxObject.getId()) {
				currentOfTextField.setText(fmmlxObject.getName());
			}
		}
		
		currentOfTextField.setDisable(true);
		
		newOfComboBox = new ComboBox<FmmlxObject>();
		//TODO make a method getAllPossibleOfList in FmmlxDiagram.class
		//ObservableList<FmmlxObject> allPossibleOf = diagram.getAllPossibleOfList();
		//newOfComboBox = initializeComboBox(allPossibleOf);
		
		newOfComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentOf, 0, 1);
		grid.add(currentOfTextField, 1, 1);
		grid.add(newOf, 0, 2);
		grid.add(newOfComboBox, 1, 2);
		grid.add(errorLabel,0,3);
	}

}
