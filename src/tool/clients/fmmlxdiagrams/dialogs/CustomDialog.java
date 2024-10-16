package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.IValueGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.view.InstanceGeneratorGenerateTypeComboBox;
import tool.clients.importer.Conflict;
import tool.xmodeler.ControlCenter;
import tool.xmodeler.ControlCenterClient;
import tool.xmodeler.XModeler;

import java.util.List;


public class CustomDialog<R> extends Dialog<R> {

	protected int COLUMN_WIDTH = 150;

	protected FlowPane flow;
	protected GridPane grid;
	protected Label errorLabel;
	protected VBoxControl vBoxControl;
	protected int listView_ROW_HEIGHT = 24;

	public CustomDialog() {
		super();
		
		initializeGrid();
		setOnShowing(e ->{
			ControlCenter controlCenter = ControlCenterClient.getClient().getControlCenter();
			if(controlCenter.isIconified()) {
				controlCenter.setIconified(false);
			}
		});
		flow = new FlowPane();
		flow.setHgap(3);
		flow.setVgap(3);
		flow.setPrefWrapLength(250);
		vBoxControl = new VBoxControl();

		initOwner(XModeler.getStage());
		
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
			cc.setMinWidth(50);
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
	

	protected void addNodesToGrid(List<Node> nodes) {
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

//	protected boolean isNullOrEmpty(String string) {
//		return string == null || string.length() == 0;
//	}

	public Integer getComboBoxIntegerValue(ComboBox<Integer> box) {
		int result;
		try {
			result = Integer.parseInt(box.getEditor().getText());
		} catch (NumberFormatException e) {
			return null;
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

				if (empty || object == null || object.equals("") ) {
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

	public ListView<Conflict> initializeConflictListView(ObservableList<Conflict> list, SelectionMode selectionMode, String mode) {

		ListView<Conflict> listView = new ListView<>(list);
		listView.setPrefHeight(75);
		listView.setPrefWidth(COLUMN_WIDTH);

		switch (mode) {
			case "t":
				listView.setCellFactory(param -> new ListCell<Conflict>() {
					@Override
					protected void updateItem(Conflict object, boolean empty) {
						super.updateItem(object, empty);

						if (empty || object == null || object.getType() == null) {
							setText(null);
						} else {
							setText(object.getType());
						}
					}
				});
				break;
			case "d":
				listView.setCellFactory(param -> new ListCell<Conflict>() {
					@Override
					protected void updateItem(Conflict object, boolean empty) {
						super.updateItem(object, empty);

						if (empty || object == null || object.getDescription() == null) {
							setText(null);
						} else {
							setText(object.getDescription());
						}
					}
				});
				break;
			case "w":
				listView.setCellFactory(param -> new ListCell<Conflict>() {
					@Override
					protected void updateItem(Conflict object, boolean empty) {
						super.updateItem(object, empty);

						if (empty || object == null || object.getIn() == null) {
							setText(null);
						} else {
							setText(object.getIn().toString());
						}
					}
				});
				break;
			default:
				listView.setCellFactory(param -> new ListCell<Conflict>() {
					@Override
					protected void updateItem(Conflict object, boolean empty) {
						super.updateItem(object, empty);

						if (empty || object == null) {
							setText(null);
						} else {
							setText(object.toString());
						}
					}
				});
				break;
		}


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
	
	public <Property extends FmmlxProperty> ComboBox<Property> initializeComboBox(ObservableList<Property> list) {
		ComboBox<Property> comboBox = new ComboBox<>(list);
		comboBox.setCellFactory(param -> new ListCell<Property>() {
			@Override
			protected void updateItem(Property item, boolean empty) {
				super.updateItem(item, empty);
				
				if(item == null || empty) {
					setText(null);
				} else {
					setText(item.getName());
				}
//				// Notlösung..
//				if ( item == null ) {
//					empty = true;
//				}
//
//				if (empty || isNullOrEmpty(item.getName())) {
//					setText(null);
//				} else {
//					setText(item.getName());
//				}
			}
		});
		comboBox.setConverter(new StringConverter<Property>() {
			@Override
			public String toString(Property object) {
				if (object == null) {
					return null;
				} else {
					return object.getName();
				}
			}

			@Override
			public Property fromString(String string) {
				return null;
			}
		});
		comboBox.setPrefWidth(COLUMN_WIDTH);
		return comboBox;
	}
		
	protected InstanceGeneratorGenerateTypeComboBox initializeComboBoxGeneratorList(AbstractPackageViewer diagram, FmmlxAttribute attribute) {
		InstanceGeneratorGenerateTypeComboBox comboBox = new InstanceGeneratorGenerateTypeComboBox(attribute);
		comboBox.setCellFactory(param -> new ListCell<IValueGenerator>() {
			@Override
			protected void updateItem(IValueGenerator item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getValueGeneratorName());
				}
			}
		});

		comboBox.setConverter(new StringConverter<IValueGenerator>() {
			@Override
			public String toString(IValueGenerator object) {
				if (object == null) {
					return null;
				} else {
					return object.getName2();
				}
			}

			@Override
			public IValueGenerator fromString(String string) {
				return null;
			}
		});
		
		comboBox.valueProperty().addListener((observable, oldValue, newValue) -> newValue.openDialog(diagram));
		
		comboBox.setPrefWidth(COLUMN_WIDTH);
		return comboBox;
	}
	
	public ComboBox<FmmlxEnum> initializeComboBoxEnum(ObservableList<FmmlxEnum> observableList) {
		ComboBox<FmmlxEnum> comboBox = new ComboBox<>(observableList);
		comboBox.setCellFactory(param -> new ListCell<FmmlxEnum>() {
			@Override
			protected void updateItem(FmmlxEnum item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
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
	
	
	public boolean validateString(String string) {

		InputChecker.getInstance();
		if (!InputChecker.isValidIdentifier(string)) {
			errorLabel.setText("Enter valid String!");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}
	
	
	public boolean validateIncrement(String startValue, String endValue, String increment, String attributeType) {
		boolean valid;
		switch(attributeType){
        case "Integer":   	
        	valid = InputChecker.validateInteger(startValue) && InputChecker.validateInteger(endValue) && InputChecker.validateInteger(increment);
        	if(!valid) {
        		errorLabel.setText("Please input valid Integer-Value");
        	}
        	return valid;
        case "Float":
        	valid =  InputChecker.validateFloat(startValue) && InputChecker.validateFloat(endValue) && InputChecker.validateFloat(increment);
        	if(!valid) {
        		errorLabel.setText("Please input valid Float-Value");
        	}
        	return valid;
        default:
           	return false;
        }
		
	}


	protected CustomDialog.VBoxControl getVBoxControl() {
		return vBoxControl;
	}

	protected static class VBoxControl{

		public VBox joinNodeInVBox(Node node1, Node node2, Node node3) {
			VBox result = new VBox();
			GridPane grid = new GridPane();
			grid.add(node1, 0, 0);
			grid.add(node2, 1, 0);
			grid.add(node3, 2, 0);

			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPercentWidth(45);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPercentWidth(10);
			ColumnConstraints col3 = new ColumnConstraints();
			col3.setPercentWidth(45);

			grid.getColumnConstraints().addAll(col1,col2,col3);

			result.getChildren().add(grid);
			return result;
		}

		public VBox joinNodeInVBox(Node node1, Node node2) {
			VBox result = new VBox();
			GridPane grid = new GridPane();
			grid.add(node1, 0, 0);
			grid.add(node2, 1, 0);


			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPercentWidth(35);
			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPercentWidth(65);


			grid.getColumnConstraints().addAll(col1,col2);

			result.getChildren().add(grid);
			return result;
		}
	}


}
