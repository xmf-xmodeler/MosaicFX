package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ChangeMultiplicityDialog extends CustomDialog<MultiplicityDialogResult> {

	private FmmlxObject object;

	private DialogPane dialogPane;

	private Label objectLabel;
	private Label selectAttributeLabel;
	private Label minimumLabel;
	private Label maximumLabel;
	private Label orderedLabel;
	private Label allowDuplicatesLabel;


	private TextField objectTextField;
	private ComboBox<FmmlxAttribute> selectAttributeComboBox;
	private ComboBox<String> minimumComboBox;
	private ComboBox<String> maximumComboBox;
	private CheckBox orderedCheckBox;
	private CheckBox allowDuplicatesCheckBox;

	private Vector<FmmlxAttribute> attributes;
	private List<String> minArray;
	private List<String> maxArray;


	public ChangeMultiplicityDialog(FmmlxObject object) {
		this.object = object;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		layoutContent();
		dialogPane.setContent(flow);
		validateUserInput();

		setResult();

	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new MultiplicityDialogResult(minimumComboBox.getSelectionModel().getSelectedItem(),
						maximumComboBox.getSelectionModel().getSelectedItem(),
						orderedCheckBox.isSelected(),
						allowDuplicatesCheckBox.isSelected());
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		errorLabel.setText("Not implemented yet");
		return false;
	}

	private void layoutContent() {
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeMultiplicity);

		objectLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectedObject);
		selectAttributeLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectAttribute);
		objectTextField = new TextField();
		objectTextField.setText(object.getName());
		objectTextField.setDisable(true);

		String[] min = new String[]{"0", "1"};
		minArray = Arrays.asList(min);
		ObservableList<String> minList = FXCollections.observableArrayList(minArray);

		String[] max = new String[]{"1", "unlimited"};
		maxArray = Arrays.asList(max);
		ObservableList<String> maxList = FXCollections.observableArrayList(maxArray);

		attributes = object.getOwnAttributes();
		attributes.addAll(object.getOtherAttributes());

		ObservableList<FmmlxAttribute> attributeList;
		attributeList = FXCollections.observableList(attributes);

		selectAttributeComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributeList);

		minimumLabel = new Label(StringValueDialog.LabelAndHeaderTitle.minimum);
		minimumComboBox = new ComboBox<>(minList);

		maximumLabel = new Label(StringValueDialog.LabelAndHeaderTitle.maximum);
		maximumComboBox = new ComboBox<>(maxList);

		orderedLabel = new Label(StringValueDialog.LabelAndHeaderTitle.ordered);
		orderedCheckBox = new CheckBox();

		allowDuplicatesLabel = new Label(StringValueDialog.LabelAndHeaderTitle.allowDuplicates);
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
