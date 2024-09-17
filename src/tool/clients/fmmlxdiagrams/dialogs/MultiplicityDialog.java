package tool.clients.fmmlxdiagrams.dialogs;

import static tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.converter.IntegerStringConverter;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.ValueList;

public class MultiplicityDialog extends CustomDialog<Multiplicity> {

	private ComboBox<Integer> minimumComboBox;
	private ComboBox<Integer> maximumComboBox;
	private CheckBox isUpperLimitCheckBox;
	private CheckBox orderedCheckBox;
	private CheckBox duplicatesCheckBox;
	private Multiplicity oldMultiplicity;

	public MultiplicityDialog() {
		this(Multiplicity.OPTIONAL);
	}

	public MultiplicityDialog(Multiplicity multiplicity) {
		super();

		if (multiplicity != null) {
			this.oldMultiplicity = multiplicity;
		} else {
			this.oldMultiplicity = Multiplicity.OPTIONAL;
		}
		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Edit Multiplicity");

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementsToGrid();
		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new Multiplicity(
						getComboBoxIntegerValue(minimumComboBox),
						isUpperLimitCheckBox.isSelected()?getComboBoxIntegerValue(maximumComboBox):Integer.MAX_VALUE,
						isUpperLimitCheckBox.isSelected(),
						orderedCheckBox.isSelected(),
						duplicatesCheckBox.isSelected());
			}
			return null;
		});
	}

	public void addElementsToGrid() {
		Label labelMin = new Label(LabelAndHeaderTitle.minimum);
		Label labelMax = new Label(LabelAndHeaderTitle.maximum);
		Label labelOrdered = new Label(LabelAndHeaderTitle.ordered);
		Label labelDuplicates = new Label(LabelAndHeaderTitle.allowDuplicates);
		Label labelUpperLimit = new Label(LabelAndHeaderTitle.upperLimit);

		minimumComboBox = new ComboBox<>();
		minimumComboBox.setValue(oldMultiplicity.min);

		maximumComboBox = new ComboBox<>();
		maximumComboBox.setEditable(true);
		maximumComboBox.setConverter(new IntegerStringConverter());
		if(oldMultiplicity.upperLimit) {
			maximumComboBox.setValue(oldMultiplicity.max);
		} else {
			maximumComboBox.setValue(0);
			maximumComboBox.setDisable(true);
		}
		minimumComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				maximumComboBox.setItems(ValueList.getValueInterval(newValue));
			}
		});
		minimumComboBox.setEditable(true);
		minimumComboBox.setConverter(new IntegerStringConverter());

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
		/*			//commented out instead of using an if statement because we cant access a diagram/AbstractPackageViewer object from here
		grid.add(labelOrdered, 0, 3);
		grid.add(orderedCheckBox, 1, 3);
		grid.add(labelDuplicates, 0, 4);
		grid.add(duplicatesCheckBox, 1, 4);
		*/
	}

	private boolean validateInput() {
		if (minimumComboBox.getSelectionModel().getSelectedItem() == null || maximumComboBox.getSelectionModel().getSelectedItem() == null) {
			errorLabel.setText("Minimum or Maximum cannot be blank.");
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
		if(!isUpperLimitCheckBox.isSelected()) return true;
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
