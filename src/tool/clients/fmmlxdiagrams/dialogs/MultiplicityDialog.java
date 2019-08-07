package tool.clients.fmmlxdiagrams.dialogs;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;

import static tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;

public class MultiplicityDialog extends CustomDialog<MultiplicityDialogResult> {

	private ComboBox<Integer> minimumComboBox;
	private ComboBox<Integer> maximumComboBox;
	private CheckBox isUpperLimitCheckBox;
	private CheckBox orderedCheckBox;
	private CheckBox duplicatesCheckBox;
	private ObservableList<Integer> valueList = FXCollections.observableArrayList(0, 1, 2, 3, 4);

	private Multiplicity oldMultiplicity;

	public MultiplicityDialog() {
		this(Multiplicity.OPTIONAL);
	}

	MultiplicityDialog(Multiplicity multiplicity) {
		super();


		if (multiplicity != null) {
			this.oldMultiplicity = multiplicity;
		} else {
			this.oldMultiplicity = Multiplicity.MANDATORY;
		}
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Add / Edit Multiplicity");

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();
		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new MultiplicityDialogResult(						
						getComboBoxIntegerValue(minimumComboBox),
						getComboBoxIntegerValue(maximumComboBox),
						isUpperLimitCheckBox.isSelected(),
						orderedCheckBox.isSelected(),
						duplicatesCheckBox.isSelected());
			}
			return null;
		});
	}

	public void addElementToGrid() {
		Label labelMin = new Label(LabelAndHeaderTitle.minimum);
		Label labelMax = new Label(LabelAndHeaderTitle.maximum);
		Label labelOrdered = new Label(LabelAndHeaderTitle.ordered);
		Label labelDuplicates = new Label(LabelAndHeaderTitle.allowDuplicates);
		Label labelUpperLimit = new Label(LabelAndHeaderTitle.upperLimit);

		minimumComboBox = new ComboBox<>(valueList);
		minimumComboBox.setValue(oldMultiplicity.min);
		minimumComboBox.setEditable(true);
		maximumComboBox = new ComboBox<>(valueList);
		maximumComboBox.setValue(oldMultiplicity.max);
		maximumComboBox.setEditable(true);

		orderedCheckBox = new CheckBox();
		orderedCheckBox.setSelected(oldMultiplicity.ordered);
		duplicatesCheckBox = new CheckBox();
		duplicatesCheckBox.setSelected(oldMultiplicity.duplicates);
		isUpperLimitCheckBox = new CheckBox();
		isUpperLimitCheckBox.setSelected(oldMultiplicity.upperLimit);

		minimumComboBox.setPrefWidth(COLUMN_WIDTH);
		maximumComboBox.setPrefWidth(COLUMN_WIDTH);

		isUpperLimitCheckBox.selectedProperty().addListener(this::changedUpperLimit);

		grid.add(labelMin, 0, 0);
		grid.add(minimumComboBox, 1, 0);
		grid.add(labelMax, 0, 1);
		grid.add(maximumComboBox, 1, 1);
		grid.add(labelUpperLimit, 0, 2);
		grid.add(isUpperLimitCheckBox, 1, 2);
		grid.add(labelOrdered, 0, 3);
		grid.add(orderedCheckBox, 1, 3);
		grid.add(labelDuplicates, 0, 4);
		grid.add(duplicatesCheckBox, 1, 4);
	}

	private boolean validateInput() {
		if (minimumComboBox.getSelectionModel().getSelectedItem()==null || maximumComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Minimum and Maximum cannot be blank.");
			return false;
		}
		if (!validateMax()) {
			return false;
		}
		if (!validateMin()) {
			return false;
		}
		return true;
	}

	private boolean validateMin() {
		int min = getComboBoxIntegerValue(minimumComboBox);

		if ((min < 0)) {
			errorLabel.setText("Enter valid Minimum for this Multiplicity!");
			return false;
		}
		return true;
	}

	private boolean validateMax() {
		int min = getComboBoxIntegerValue(minimumComboBox);
		int max = getComboBoxIntegerValue(maximumComboBox);
		if (max < 0) {
			errorLabel.setText("Enter valid maximum for this Multiplicity!");
			return false;
		} else if (min > max) {
			errorLabel.setText("Enter valid maximum for this Multiplicity!");
			return false;
		}
		return true;
	}

	private void changedUpperLimit(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (!newValue) {
			maximumComboBox.setValue(MultiplicityDialog.this.getComboBoxIntegerValue(minimumComboBox) + 1);
			maximumComboBox.setDisable(true);
		} else {
			maximumComboBox.setDisable(false);
		}
	}
}
