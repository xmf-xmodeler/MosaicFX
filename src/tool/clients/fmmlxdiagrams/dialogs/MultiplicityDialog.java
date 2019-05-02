package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class MultiplicityDialog extends CustomDialog<AddAttributeDialog> {

	private Label labelMin;
	private Label labelMax;
	private Label labelOrdered;
	private Label labelDuplicates;

	private ComboBox<String> comboBoxMin;
	private ComboBox<String> comboBoxMax;
	private CheckBox checkBoxOrdered;
	private CheckBox checkBoxDuplicates;
	List<String> minArray;
	List<String> maxArray;

	public MultiplicityDialog() {
		super();

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

	}

	public void addElementToGrid() {
		labelMin = new Label("Minimum");
		labelMax = new Label("Maximum");
		labelOrdered = new Label("Ordered");
		labelDuplicates = new Label("Allow Duplicates");

		String[] min = new String[] { "0", "1" };
		minArray = Arrays.asList(min);
		ObservableList<String> minList = FXCollections.observableArrayList(minArray);

		String[] max = new String[] { "1", "unlimited" };
		maxArray = Arrays.asList(max);
		ObservableList<String> maxList = FXCollections.observableArrayList(maxArray);

		checkBoxOrdered = new CheckBox();
		checkBoxDuplicates = new CheckBox();
		
		comboBoxMin = new ComboBox<>(minList);
		comboBoxMin.setValue("0");
		comboBoxMax = new ComboBox<>(maxList);
		comboBoxMax.setValue("1");
        comboBoxMin.setPrefWidth(COLUMN_WIDTH);
		comboBoxMax.setPrefWidth(COLUMN_WIDTH);

		grid.add(labelMin, 0, 0);
		grid.add(comboBoxMin, 1, 0);
		grid.add(labelMax, 0, 1);
		grid.add(comboBoxMax, 1, 1);
		grid.add(labelOrdered, 0, 2);
		grid.add(checkBoxOrdered, 1, 2);
		grid.add(labelDuplicates, 0, 3);
		grid.add(checkBoxDuplicates, 1, 3);
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
		String min = comboBoxMin.getSelectionModel().getSelectedItem();

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
		String max = comboBoxMax.getSelectionModel().getSelectedItem();

		if (isNullOrEmpty(max)) {
			errorLabel.setText("Enter valid Maximum for this Multiplicity!");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}
	


}
