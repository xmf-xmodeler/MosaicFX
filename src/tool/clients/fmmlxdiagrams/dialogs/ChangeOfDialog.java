package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOfDialogResult;

import java.util.ArrayList;
import java.util.Vector;


public class ChangeOfDialog extends CustomDialog<ChangeOfDialogResult>{
	
	private FmmlxObject object;
	private final FmmlxDiagram diagram; 
	private DialogPane dialogPane;
	
	private Label selectedObjectLabel;
	private Label currentOf;
	private Label newOf;
	private TextField selectedObjectTextField;
	private TextField currentOfTextField;
	private ComboBox<String>newOfComboBox;
	
	private Vector<FmmlxObject> ofList;
	private ArrayList<String> newOfList;
	

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
				//TODO
			}
			return null;
		});
		
		
		
		// TODO Auto-generated constructor stub
	}


	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}


	private void addElementToLayout() {
		
		dialogPane.setHeaderText("Change Of");
		
		selectedObjectLabel = new Label("Selected Object");
		currentOf = new Label("Current Of");
		newOf = new Label("New Of");
		
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
		newOfComboBox = new ComboBox<String>();
		
		newOfComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentOf, 0, 1);
		grid.add(currentOfTextField, 1, 1);
		grid.add(newOf, 0, 2);
		grid.add(newOfComboBox, 1, 2);
		// TODO Auto-generated method stub
		
	}

}
