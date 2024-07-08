package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import tool.xmodeler.didactic_ml.learning_unit_managers.LearningUnitManager;

public class LearningUnitChooser extends Dialog<LearningUnitManager> {

	private Button okButton;
	private TableView<LearningUnit> tableView = createTableView();

	public LearningUnitChooser() {
		setTitle("Learning Unit Selection");
		getDialogPane().setPrefWidth(535);
		getDialogPane().setPrefHeight(355);
		getDialogPane().setContent(tableView);
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		adaptOkButton();
		setResultConverter();
		getDialogPane().getStylesheets().add(getClass().getResource("learnUnitChooser.css").toExternalForm());
	}

	private void setResultConverter() {
		setResultConverter(buttonType -> {
			if (buttonType == ButtonType.OK) {
				LearningUnit lu = tableView.getSelectionModel().getSelectedItem();
				return LearningUnitManagerFactory.createLearningUnitManager(lu);
			}
			return null;
		});
	}

	private void adaptOkButton() {
		okButton.setText("Start Unit");
		okButton.disableProperty().bind(createDisableBinding(tableView.getSelectionModel().selectedItemProperty()));
		okButton.setStyle("-fx-background-color: #ffa500;" + "-fx-border-color: #000000;" + "-fx-border-width: 0.75px;"
				+ "-fx-background-radius: 15px; " + "-fx-border-radius: 15px;");
	}

	/**
	 * Helper function. Needed to provide matching binding property
	 * 
	 * @param selectedItemProperty that needs to be checked
	 * @return the boolean if button should be enabled
	 */
	private BooleanBinding createDisableBinding(ReadOnlyObjectProperty<LearningUnit> selectedItemProperty) {
		return selectedItemProperty.isNull();
	}

	private TableView<LearningUnit> createTableView() {
		TableView<LearningUnit> tView = new TableView<>();
		tView.setItems(getItems());
		setColorOfNotImplementedRowsToLightGrey(tView);

		TableColumn<LearningUnit, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		idColumn.setSortable(false);

		TableColumn<LearningUnit, String> nameColumn = new TableColumn<>("Learning Unit Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("prettyName"));
		nameColumn.setSortable(false);

		TableColumn<LearningUnit, Boolean> selectedColumn = new TableColumn<>("Passed");
		selectedColumn.setSortable(false);
		selectedColumn.setCellValueFactory(cellData -> {
			//TODO implement finish logic
			//return new SimpleBooleanProperty(cellData.getValue().getId() == 0);
			return new SimpleBooleanProperty(false);
		});
		hideCheckboxForUnimplementedLearningUnits(selectedColumn);
		tView.getColumns().addAll(idColumn, nameColumn, selectedColumn);
		return tView;
	}

	private void hideCheckboxForUnimplementedLearningUnits(TableColumn<LearningUnit, Boolean> selectedColumn) {
		selectedColumn.setCellFactory(column -> new CheckBoxTableCell<LearningUnit, Boolean>() {
			@Override
			public void updateItem(Boolean item, boolean empty) {
				super.updateItem(item, empty);
				if (!empty) {
					LearningUnit unit = getTableView().getItems().get(getIndex());
					if (unit != null && !unit.isImplemented()) {
						setGraphic(null);
						setText(null);
					}
				}
			}
		});
	}

	/**
	 * This function disables all not implemented learning units.
	 * 
	 * @param tView new table view object that is used to build the table view
	 */
	private void setColorOfNotImplementedRowsToLightGrey(TableView<LearningUnit> tView) {
		tView.setRowFactory(tv -> new TableRow<LearningUnit>() {
			@Override
			protected void updateItem(LearningUnit unit, boolean empty) {
				super.updateItem(unit, empty);
				if (unit == null || empty) {
					setStyle("");
				} else {
					if (!unit.isImplemented()) {
						getStyleClass().add("not-implemented");
						setDisable(true);
					} else {
						setDisable(false);
						setStyle("");
					}
				}
			}
		});
	}

	private ObservableList<LearningUnit> getItems() {
		ObservableList<LearningUnit> items = FXCollections.observableArrayList();
		for (LearningUnit unit : LearningUnit.values()) {
			items.add(unit);
		}
		return items;
	}
}