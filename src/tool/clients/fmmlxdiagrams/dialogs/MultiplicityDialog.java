package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;

public class MultiplicityDialog extends CustomDialog<MultiplicityDialogResult> {

	private Label labelMin;
	private Label labelMax;
	private Label labelOrdered;
	private Label labelDuplicates;

	private ComboBox<String> minimumComboBox;
	private ComboBox<String> maximumComboBox;
	private CheckBox orderedCheckBox;
	private CheckBox duplicatesCheckBox;
	private ObservableList<String> minList = FXCollections.observableArrayList("0", "1");
	private ObservableList<String> maxList = FXCollections.observableArrayList("0", "*");

	Multiplicity oldMultiplicity;

	public MultiplicityDialog() {
		this(Multiplicity.OPTIONAL);
	}

	public MultiplicityDialog(Multiplicity multiplicity) {
		super();

		this.oldMultiplicity = multiplicity;
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
				return new MultiplicityDialogResult(minimumComboBox.getSelectionModel().getSelectedItem(),
						maximumComboBox.getSelectionModel().getSelectedItem(),
						orderedCheckBox.isSelected(),
						duplicatesCheckBox.isSelected());
			}
			return null;
		});
	}

	public void addElementToGrid() {
		labelMin = new Label("Minimum");
		labelMax = new Label("Maximum");
		labelOrdered = new Label("Ordered");
		labelDuplicates = new Label("Allow Duplicates");

		orderedCheckBox = new CheckBox();
		orderedCheckBox.setSelected(oldMultiplicity.ordered);
		duplicatesCheckBox = new CheckBox();
		duplicatesCheckBox.setSelected(oldMultiplicity.duplicates);

		minimumComboBox = new ComboBox<>(minList);
		minimumComboBox.setValue(String.valueOf(oldMultiplicity.min));
		maximumComboBox = new ComboBox<>(maxList);

		setMaximumComboBox();
		minimumComboBox.setPrefWidth(COLUMN_WIDTH);
		maximumComboBox.setPrefWidth(COLUMN_WIDTH);

		grid.add(labelMin, 0, 0);
		grid.add(minimumComboBox, 1, 0);
		grid.add(labelMax, 0, 1);
		grid.add(maximumComboBox, 1, 1);
		grid.add(labelOrdered, 0, 2);
		grid.add(orderedCheckBox, 1, 2);
		grid.add(labelDuplicates, 0, 3);
		grid.add(duplicatesCheckBox, 1, 3);
	}

	private void setMaximumComboBox() {
		if (oldMultiplicity.max == 2) {
			maximumComboBox.setValue("*");
		} else {
			maximumComboBox.setValue("1");
		}
	}

	public boolean validateInput() {
		if (!validateMax()) {
			return false;
		}
		if (!validateMin()) {
			return false;
		}
		return true;
	}

	public boolean validateMin() {
		Label errorLabel = getErrorLabel();
		String min = minimumComboBox.getSelectionModel().getSelectedItem();

		if (isNullOrEmpty(min)) {
			errorLabel.setText("Enter valid Minimum for this Multiplicity!");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	public boolean validateMax() {
		Label errorLabel = getErrorLabel();
		String max = maximumComboBox.getSelectionModel().getSelectedItem();

		if (isNullOrEmpty(max)) {
			errorLabel.setText("Enter valid Maximum for this Multiplicity!");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}


}
