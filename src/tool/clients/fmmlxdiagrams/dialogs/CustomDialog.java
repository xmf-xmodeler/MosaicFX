package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import tool.clients.fmmlxdiagrams.FmmlxAssociationInstance;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog<R> extends Dialog<R> {

	protected int COLUMN_WIDTH = 150;

	protected FlowPane flow;
	protected GridPane grid;
	protected Label errorLabel;

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

	private void initializeGrid() {
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
			if (COLUMN_WIDTH == 150) COLUMN_WIDTH = 300;
			grid.getColumnConstraints().add(cc);
		}
	}

	protected void addNodesToGrid(List<Node> nodes, int columnIndex) {
		int counter = 0;
		for (Node node : nodes) {
			grid.add(node, columnIndex, counter);
			counter++;
		}
	}

	void addNodesToGrid(List<Node> nodes) {
		int row = 0;
		int i = 0;
		while (i < nodes.size()) {
			grid.add(nodes.get(i), 0, row);
			i++;
			grid.add(nodes.get(i), 1, row);
			row++;
			i++;
		}
	}

	public Label getErrorLabel() {
		return errorLabel;
	}

	boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public Integer getComboBoxIntegerValue(ComboBox<Integer> box) {
		Integer result = null;
		try {
			result = Integer.parseInt(box.getEditor().getText());
		} catch (NumberFormatException nfe) {
		}
		return result;
	}
	
	protected void updateNodeInsideGrid(Node oldNode, Node newNode, int column, int row) {
		grid.getChildren().remove(oldNode);
		grid.add(newNode, column, row);
	}

	protected String getComboBoxStringValue(ComboBox<String> box) {
		return box.getEditor().getText();
	}
	
	public ListView<String> initializeListView(int rowNumber) {
		
		ObservableList<String> initListString = FXCollections.observableArrayList();

		for (int i=1; i<=rowNumber; i++) {
			initListString.add("Element "+ i);
		}
		
		ListView<String> listView = new ListView<>(initListString);
		listView.setPrefWidth(COLUMN_WIDTH);

		listView.setCellFactory(param -> new ListCell<String>() {
			@Override
			protected void updateItem(String object, boolean empty) {
				super.updateItem(object, empty);

				if (empty || object == null || object == "") {
					setText("");
				} else {
					setText(object);
				}
			}
		});

		return listView;
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
	
	public ListView<FmmlxAssociationInstance> initializeListViewAssociation(ObservableList<FmmlxAssociationInstance> instanceOfAssociation, SelectionMode selectionMode){
		ListView<FmmlxAssociationInstance> listView = new ListView<>(instanceOfAssociation);
		listView.setPrefHeight(75);
		listView.setPrefWidth(COLUMN_WIDTH);

		listView.setCellFactory(param -> new ListCell<FmmlxAssociationInstance>() {
			@Override
			protected void updateItem(FmmlxAssociationInstance object, boolean empty) {
				super.updateItem(object, empty);

				if (empty || object == null) {
					setText(null);
				} else {
					setText(object.toPair());
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
	
	public ComboBox<FmmlxEnum> initializeComboBoxEnum(ObservableList<FmmlxEnum> observableList) {
		ComboBox<FmmlxEnum> comboBox = new ComboBox<FmmlxEnum>(observableList);
		comboBox.setCellFactory(param -> new ListCell<FmmlxEnum>() {
			@Override
			protected void updateItem(FmmlxEnum item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || isNullOrEmpty(item.getName())) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		comboBox.setConverter(new StringConverter<FmmlxEnum>() {
			@Override
			public String toString(FmmlxEnum object) {
				if (object == null) {
					return null;
				} else {
					return object.getName();
				}
			}

			@Override
			public FmmlxEnum fromString(String string) {
				return null;
			}
		});
		comboBox.setPrefWidth(COLUMN_WIDTH);
		return comboBox;
	}
	
	public Node createAddAndRemoveButton(Button button1, Button button2) {
		HBox hBox = new HBox();
		hBox.setPrefWidth(COLUMN_WIDTH);
	
		button1.setPrefWidth(COLUMN_WIDTH * 0.5);	
		button2.setPrefWidth(COLUMN_WIDTH * 0.5);

		hBox.getChildren().addAll(button1, button2);

		return hBox;
	}


}
