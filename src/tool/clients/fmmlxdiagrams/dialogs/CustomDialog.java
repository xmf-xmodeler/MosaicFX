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
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
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
			//cc.setMaxWidth(COLUMN_WIDTH);
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
	
	public ListView<FmmlxEnum> initializeEnumListView(ObservableList<FmmlxEnum> list, SelectionMode selectionMode) {

		ListView<FmmlxEnum> listView = new ListView<>(list);
		listView.setPrefHeight(75);
		listView.setPrefWidth(COLUMN_WIDTH);

		listView.setCellFactory(param -> new ListCell<FmmlxEnum>() {
			@Override
			protected void updateItem(FmmlxEnum object, boolean empty) {
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
	
	public ListView<FmmlxLink> initializeListViewAssociation(ObservableList<FmmlxLink> instanceOfAssociation, SelectionMode selectionMode){
		ListView<FmmlxLink> listView = new ListView<>(instanceOfAssociation);
		listView.setPrefHeight(75);
		listView.setPrefWidth(COLUMN_WIDTH);

		listView.setCellFactory(param -> new ListCell<FmmlxLink>() {
			@Override
			protected void updateItem(FmmlxLink object, boolean empty) {
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
	
	protected InstanceGeneratorGenerateTypeComboBox<InstanceGeneratorGenerateType> initializeComboBoxGeneratorList(ObservableList<InstanceGeneratorGenerateType> observableList, FmmlxAttribute attribute) {
		ComboBox<InstanceGeneratorGenerateType> comboBox = new InstanceGeneratorGenerateTypeComboBox<InstanceGeneratorGenerateType>(observableList, attribute);
		comboBox.setCellFactory(param -> new ListCell<InstanceGeneratorGenerateType>() {
			@Override
			protected void updateItem(InstanceGeneratorGenerateType item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || isNullOrEmpty(item.toString())) {
					setText(null);
				} else {
					setText(item.toString());
				}
			}
		});
		comboBox.setConverter(new StringConverter<InstanceGeneratorGenerateType>() {
			@Override
			public String toString(InstanceGeneratorGenerateType object) {
				if (object == null) {
					return null;
				} else {
					return object.toString();
				}
			}

			@Override
			public InstanceGeneratorGenerateType fromString(String string) {
				return null;
			}
		});
		comboBox.setPrefWidth(COLUMN_WIDTH);
		return (InstanceGeneratorGenerateTypeComboBox<InstanceGeneratorGenerateType>) comboBox;
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
	
	public Node joinNodeElementInHBox(Button button1, Button button2) {
		HBox hBox = new HBox();
		hBox.setPrefWidth(COLUMN_WIDTH);
	
		button1.setPrefWidth(COLUMN_WIDTH * 0.5);	
		button2.setPrefWidth(COLUMN_WIDTH * 0.5);

		hBox.getChildren().addAll(button1, button2);

		return hBox;
	}
	
	public Node joinNodeElementInHBox(ComboBox<FmmlxEnum> textField, Button button) {
		HBox hBox = new HBox();
		hBox.setPrefWidth(COLUMN_WIDTH);
	
		textField.setPrefWidth(COLUMN_WIDTH * 0.6);	
		button.setPrefWidth(COLUMN_WIDTH * 0.4);

		hBox.getChildren().addAll(textField, button);

		return hBox;
	}
	
	protected ObservableList<InstanceGeneratorGenerateType> getGenerateTypeList(String type) {
		if(type.equals("Integer") || type.equals("Float")) {
			return FXCollections.observableArrayList(InstanceGeneratorGenerateType.INCREMENT, InstanceGeneratorGenerateType.STATIC, InstanceGeneratorGenerateType.LIST);
		} else if (type.equals("String")) {
			return FXCollections.observableArrayList(InstanceGeneratorGenerateType.STATIC, InstanceGeneratorGenerateType.LIST);
		} else if (type.equals("Boolean")) {
			return FXCollections.observableArrayList(InstanceGeneratorGenerateType.STATIC, InstanceGeneratorGenerateType.LIST);
		}
		return  FXCollections.observableArrayList(InstanceGeneratorGenerateType.NULL);
	}


}
