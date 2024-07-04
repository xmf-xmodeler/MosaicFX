package tool.xmodeler.didactic_ml;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class LearningUnitChooser extends Dialog<LearningUnit> {

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
	}

	private void setResultConverter() {
		setResultConverter(buttonType -> {
			if (buttonType == ButtonType.OK) {
				return tableView.getSelectionModel().getSelectedItem();
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

	private BooleanBinding createDisableBinding(ReadOnlyObjectProperty<LearningUnit> selectedItemProperty) {
		return selectedItemProperty.isNull();
	}

	private TableView<LearningUnit> createTableView() {
		TableView<LearningUnit> tView = new TableView<>();
		tView.setItems(getItems());

		TableColumn<LearningUnit, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		idColumn.setSortable(false); 

		TableColumn<LearningUnit, String> nameColumn = new TableColumn<>("Learning Unit Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("prettyName"));
		nameColumn.setSortable(false); 
		
		TableColumn<LearningUnit, Boolean> selectedColumn = new TableColumn<>("Passed");
		selectedColumn.setSortable(false); 
		selectedColumn.setCellValueFactory(cellData -> {
			return new SimpleBooleanProperty(cellData.getValue().getId() == 0);
		});
		selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
		tView.getColumns().addAll(idColumn, nameColumn, selectedColumn);
		return tView;
	}

	private ObservableList<LearningUnit> getItems() {
		ObservableList<LearningUnit> items = FXCollections.observableArrayList();
		for (LearningUnit unit : LearningUnit.values()) {
			items.add(unit);
		}
		return items;
	}
}