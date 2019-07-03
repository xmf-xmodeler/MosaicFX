package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeMultiplicityDialogResult;

public class ChangeMultiplicityDialog extends CustomDialog<ChangeMultiplicityDialogResult> {
	
	private final PropertyType type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	private DialogPane dialogPane;
	
	private Label objectLabel;
	private Label selectAttributeLabel;
	private Label minimumLabel;
	private Label maximumLabel;
	private Label orderedLabel;
	private Label allowDuplicatesLabel;
	
	
	private TextField objectTextField;
	private ComboBox<String> selectAttributeComboBox;
	private ComboBox<Integer> minimumComboBox;
	private ComboBox<Integer> maximumComboBox;
	private CheckBox orderedCheckBox;
	private CheckBox allowDuplicatesCheckBox;
	
	public ChangeMultiplicityDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		this.type=type;
		this.diagram=diagram;
		this.object=object;
		// TODO Auto-generated constructor stub
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);

		setResult();
		
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}

	private void layoutContent() {
		dialogPane.setHeaderText("Change Multiplicity");
		
		objectLabel = new Label("Class");
		selectAttributeLabel = new Label("Select Attribute");
		objectTextField = new TextField();
		objectTextField.setText(object.getName());
		objectTextField.setDisable(true);
		
		selectAttributeLabel= new Label("Select Attribute");
		selectAttributeComboBox = new ComboBox<String>();
		
		minimumLabel = new Label("Minimum");
		minimumComboBox = new ComboBox<Integer>();
		
		maximumLabel = new Label("Maximum");
		maximumComboBox = new ComboBox<Integer>();
		
		orderedLabel = new Label("Ordered");
		orderedCheckBox = new CheckBox();
		
		allowDuplicatesLabel = new Label("Allow Duplicates");
		allowDuplicatesCheckBox = new CheckBox();
		
		selectAttributeComboBox.setPrefWidth(COLUMN_WIDTH);
		minimumComboBox.setPrefWidth(COLUMN_WIDTH);
		maximumComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(objectLabel, 0, 0);
		grid.add(objectTextField, 1, 0);
		grid.add(selectAttributeLabel, 0, 1);
		grid.add(selectAttributeComboBox, 1, 1);
		grid.add(minimumLabel, 0, 2);
		grid.add(minimumComboBox, 1, 2);
		grid.add(maximumLabel, 0, 3);
		grid.add(maximumComboBox, 1, 3);
		grid.add(orderedLabel, 0, 4);
		grid.add(orderedCheckBox, 1, 4);
		grid.add(allowDuplicatesLabel, 0, 5);
		grid.add(allowDuplicatesCheckBox, 1, 5);
		
	}

}
