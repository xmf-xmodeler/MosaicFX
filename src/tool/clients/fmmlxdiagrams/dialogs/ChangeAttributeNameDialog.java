package tool.clients.fmmlxdiagrams.dialogs;
import java.util.Vector;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeAttributeNameDialogResult;


public class ChangeAttributeNameDialog extends CustomDialog<ChangeAttributeNameDialogResult>{
	
	private Label attributeNameLabel ;
	private Label newAttributeNameLabel;
	
	private ComboBox<String> attributeComboBox; 
	private TextField newAttributeNameTextField;
	private Vector<FmmlxObject> objects;
	private Vector<FmmlxAttribute> attributes;
	
	public ChangeAttributeNameDialog(FmmlxDiagram diagram, Integer classID) {
		super();
		DialogPane dialogPane = getDialogPane();
		objects = diagram.getObjects();
		dialogPane.isResizable();
		dialogPane.setHeaderText("Change Attribute Name");
		
		for (FmmlxObject object : objects) {
			if (object.getId()==classID) {
				this.attributes = object.getAttributes();
			}
		}
		
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		addElementToGrid(classID);
		dialogPane.setContent(flow);
		
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
	
	private void addElementToGrid(int classID) {
		attributeNameLabel = new Label("choose Attribute");
		attributeComboBox = new ComboBox<String>();
		
		newAttributeNameLabel = new Label("New Name");
		newAttributeNameTextField = new TextField();
		
		attributeComboBox.setPrefWidth(COLUMN_WIDTH);
		attributeNameLabel.setPrefWidth(COLUMN_WIDTH);
		
		
		grid.add(attributeNameLabel, 0, 0);
		grid.add(attributeComboBox, 1, 0);
		grid.add(newAttributeNameLabel, 0, 1);
		grid.add(newAttributeNameTextField, 1, 1);
	}
	
	private boolean validateUserInput() {
		if (!attributeIsChoosen()) {
			return false;
		}
		if (!nameIsValid()) {
			return false;
		}
		return true;
	}

	private boolean nameIsValid() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean attributeIsChoosen() {
		// TODO Auto-generated method stub
		return false;
	}

}
