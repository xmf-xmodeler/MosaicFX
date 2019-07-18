package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;

public class CustomDialog<R> extends Dialog<R> {

	protected int COLUMN_WIDTH = 150;

	protected FlowPane flow;
	protected GridPane grid;
	Label errorLabel;

	public CustomDialog() {
		super();

		initializeGrid();
		flow = new FlowPane();
		flow.setHgap(3);
		flow.setVgap(3);
		flow.setPrefWrapLength(250);

		flow.getChildren().add(grid);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		flow.getChildren().add(errorLabel);
	}

	public void initializeGrid() {
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(3, 3, 3, 3));

		ColumnConstraints cc;
		for (int i = 0; i < 2; i++) {
			cc = new ColumnConstraints();
			cc.setMaxWidth(COLUMN_WIDTH);
			cc.setMinWidth(COLUMN_WIDTH);
			cc.setFillWidth(true);
			cc.setHgrow(Priority.ALWAYS);
			// double size for second column
			COLUMN_WIDTH = COLUMN_WIDTH * 2;
			grid.getColumnConstraints().add(cc);
		}
	}

	public Label getErrorLabel() {
		return errorLabel;
	}

	public boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	protected Integer getComboBoxIntegerValue(ComboBox<Integer> box) {
		Integer result = null;
		try {
			result = Integer.parseInt(box.getEditor().getText());
		} catch (NumberFormatException nfe) {
		}
		return result;
	}

	protected String getComboBoxStringValue(ComboBox<String> box) {
		return box.getEditor().getText();
	}

	public ListView<FmmlxObject> initializeListView(ObservableList<FmmlxObject> list, SelectionMode selectionMode) {

		ListView<FmmlxObject> listView = new ListView<>(list);
		listView.setPrefHeight(75);
		listView.setPrefWidth(COLUMN_WIDTH);

		listView.setCellFactory(param -> new ListCell<FmmlxObject>() {
			@Override
			protected void updateItem(FmmlxObject object, boolean empty) {
				super.updateItem(object, empty);

				if (empty || object == null || object.getName() == null) {
					setText(null);
				} else {
					setText(object.getName());
				}
			}
		});

		listView.getSelectionModel().setSelectionMode(selectionMode);
		return listView;
	}

	public ComboBox<? extends FmmlxProperty> initializeComboBox(ObservableList<? extends FmmlxProperty> list) {
		ComboBox<FmmlxProperty> comboBox = new ComboBox(list);
		comboBox.setCellFactory(param -> new ListCell<FmmlxProperty>() {
			@Override
			protected void updateItem(FmmlxProperty item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || isNullOrEmpty(item.getName())) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		comboBox.setConverter(new StringConverter<FmmlxProperty>() {
			@Override
			public String toString(FmmlxProperty object) {
				if (object == null) {
					return null;
				} else {
					return object.getName();
				}
			}

			@Override
			public FmmlxProperty fromString(String string) {
				return null;
			}
		});
		comboBox.setPrefWidth(COLUMN_WIDTH);
		return comboBox;
	}
}
