package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;

public class RemoveAttributDialog extends CustomDialog<MetaClassDialogResult>{
	
	private GridPane gridPane;
	
	private Label selectObjectLabel;
	private Label selectionForStrategies;
	
	private ComboBox<String> selectObjectLabelComboBox;
	private ComboBox<String> selectionForStrategiesComboBox;
	
	
	public RemoveAttributDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Remove Attribute");
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		gridPane = initializeGrid();
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
		
		selectObjectLabel = new Label("Select Object");
		selectionForStrategies = new Label("Selection for Strategies");
		
		selectObjectLabelComboBox = new ComboBox<>();
		selectionForStrategiesComboBox = new ComboBox<>();
		
		selectObjectLabelComboBox.setPrefWidth(COLUMN_WIDTH);
		selectionForStrategiesComboBox.setPrefWidth(COLUMN_WIDTH);
		
		gridPane.add(selectObjectLabel, 0, 0);
		gridPane.add(selectObjectLabelComboBox, 1, 0);
		gridPane.add(selectionForStrategies, 0, 1);
		gridPane.add(selectionForStrategiesComboBox, 1, 1);
		
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	

}
