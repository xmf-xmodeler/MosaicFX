package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tool.helper.IconGenerator;
import tool.xmodeler.ControlCenterClient;

public class LearningUnitChooser extends Dialog<Void> {

	private TableView<LearningUnit> learningUnitTable = createTableView();
	private LearningUnitTabPane learningUnitTabPane = new LearningUnitTabPane(this);

	public LearningUnitChooser() {
		ControlCenterClient.getClient().getControlCenter().close();
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		stage.setOnCloseRequest((e) -> ControlCenterClient.getClient().getControlCenter().show());
		setTitle("Learning Unit Selection");
		getDialogPane().setPrefWidth(1200);
		getDialogPane().setPrefHeight(600);

		learningUnitTable.setMinWidth(518);
		learningUnitTabPane.disableProperty().bind(createDisableBinding(learningUnitTable.getSelectionModel().selectedItemProperty()));
		learningUnitTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			learningUnitTabPane.updateView();
		});

		Separator separator = new Separator();
		separator.setOrientation(javafx.geometry.Orientation.VERTICAL);

		HBox hbox = new HBox(learningUnitTable, separator, learningUnitTabPane);
		getDialogPane().setContent(hbox);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
		getDialogPane().getStylesheets().add(getClass().getResource("learnUnitChooser.css").toExternalForm());
		
		 setResultConverter(dialogButton -> {
	            if (dialogButton == ButtonType.CANCEL) {
	            	ControlCenterClient.getClient().getControlCenter().show();
	            }
	            return null;
	        });
	}

	public LearningUnit getSelectedLearningUnit() {
		return learningUnitTable.getSelectionModel().getSelectedItem();
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
//		idColumn.impl_setReorderable(false);
		idColumn.setResizable(false);

		TableColumn<LearningUnit, String> nameColumn = new TableColumn<>("Learning Unit Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("prettyName"));
		nameColumn.setSortable(false);
//		nameColumn.impl_setReorderable(false);
		nameColumn.setMinWidth(350);
		//nameColumn.setResizable(false);

		TableColumn<LearningUnit, Boolean> selectedColumn = new TableColumn<>("Passed");
		selectedColumn.setSortable(false);
//		selectedColumn.impl_setReorderable(false);
		selectedColumn.setResizable(false);
		selectedColumn.setCellValueFactory(cellData -> {
			LearningUnit unit = cellData.getValue();
			return new SimpleBooleanProperty(unit.isFinished());
		});
		setCellFactory(selectedColumn);
		tView.getColumns().addAll(idColumn, nameColumn, selectedColumn);
		return tView;
	}

	private void setCellFactory(TableColumn<LearningUnit, Boolean> selectedColumn) {
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

	public TableView<LearningUnit> getLearningUnitTable() {
		return learningUnitTable;
	}

	public LearningUnitTabPane getLearningUnitTabPane() {
		return learningUnitTabPane;
	}
}