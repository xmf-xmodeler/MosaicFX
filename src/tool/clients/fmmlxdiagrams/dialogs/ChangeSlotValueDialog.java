package tool.clients.fmmlxdiagrams.dialogs;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ChangeSlotValueDialog extends CustomDialog<ChangeSlotValueDialog.Result> {

	private final AbstractPackageViewer diagram;
	private final FmmlxObject object;
	private final FmmlxSlot initialSlot;
	private String type;

	private TextField slotValueTextField;
	private ComboBox<String> slotValueComboBox;
	private CheckBox isExpressionCheckBox;
	private ComboBox<FmmlxSlot> slotBox;
	private TextField slotTypeTextfield;
	private ToggleGroup toggleGroup = new ToggleGroup();
	private RadioButton trueButton = new RadioButton("true");
	private RadioButton falseButton = new RadioButton("false");
	private String trueOrFalse="false";
	private DatePicker datePicker = new DatePicker();
	
	private ComboBox<String> currency = new ComboBox<>();
	private TextField money = new TextField();
	
	private enum Mode {DEFAULT, STRING, ENUM, INVALID, DATE, BOOLEAN, MONEY}
	private Mode mode;

	public ChangeSlotValueDialog(AbstractPackageViewer diagram, FmmlxObject object, FmmlxSlot initialSlot) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.initialSlot = initialSlot;
		
		DialogPane dialog = getDialogPane();
		dialog.setHeaderText(LabelAndHeaderTitle.changeSlotValue);
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		isExpressionCheckBox = new CheckBox();
		layoutContent();

		dialog.setContent(flow);

		setResultConverter();
		
		//If TextField is in use, then the cursor should jump there!
		if (mode==Mode.STRING || mode==Mode.DEFAULT) Platform.runLater(() -> slotValueTextField.requestFocus());
		
	}

	private void setMode(FmmlxSlot slot) {
		this.type = slot == null ? null : slot.getType(diagram);
		
		this.mode = 
				type==null?Mode.INVALID:
				"String".equals(type)?Mode.STRING:
				diagram.isEnum(type)?Mode.ENUM:
				"Date".equals(type)?Mode.DATE:
				"Boolean".equals(type)?Mode.BOOLEAN:
				"MonetaryValue".equals(type)?Mode.MONEY:	
				Mode.DEFAULT;

		setInputItem();
		
		slotTypeTextfield.setText(this.type);
		isExpressionCheckBox.setSelected(mode == Mode.DEFAULT);
		isExpressionCheckBox.setDisable(mode!=Mode.DEFAULT && mode!=Mode.STRING);		
	}

	private void layoutContent() {
		List<Node> nodes = new ArrayList<>();
		TextField classNameTextField = new TextField(object.getName());
		classNameTextField.setDisable(true);
		Node slotName;
		if(initialSlot == null) {
			slotBox = new ComboBox<FmmlxSlot>();
			slotBox.getItems().addAll(object.getAllSlots());
			slotName = slotBox;
			slotBox.getSelectionModel().selectedItemProperty().addListener((a,b,c)->setMode(c));
		} else {
			TextField slotNameTextField = new TextField(initialSlot.getName());
			slotNameTextField.setDisable(true);
			slotName = slotNameTextField;
		}		
		slotTypeTextfield = new TextField();
		slotTypeTextfield.setDisable(true);
		Node inputItem = null; 


		nodes.add(new Label(LabelAndHeaderTitle.aClass));
		nodes.add(classNameTextField);
		nodes.add(new Label(LabelAndHeaderTitle.name));
		nodes.add(slotName);
		nodes.add(new Label((LabelAndHeaderTitle.type)));
		nodes.add(slotTypeTextfield);
		nodes.add(new Label(LabelAndHeaderTitle.value));
		nodes.add(inputItem);
		nodes.add(new Label(LabelAndHeaderTitle.expression));
		nodes.add(isExpressionCheckBox);

//		addNodesToGrid(nodes);

		grid.add(new Label(LabelAndHeaderTitle.aClass), 	0, 0);
		grid.add(new Label(LabelAndHeaderTitle.name), 		0, 1);
		grid.add(new Label(LabelAndHeaderTitle.type), 		0, 2);
		grid.add(new Label(LabelAndHeaderTitle.value), 		0, 3);
		grid.add(new Label(LabelAndHeaderTitle.expression), 0, 4);
		
		grid.add(classNameTextField, 	1, 0);
		grid.add(slotName, 		        1, 1);
		grid.add(slotTypeTextfield, 	1, 2);
//		grid.add(inputItem, 		    1, 3);
		grid.add(isExpressionCheckBox,  1, 4);
		setMode(initialSlot);
	}

	private void setInputItem() {
		FmmlxSlot currentSlot = initialSlot != null ? initialSlot : slotBox.getSelectionModel().getSelectedItem();
		if(currentSlot==null) mode = Mode.INVALID;
		if(mode != Mode.ENUM && mode!=Mode.DATE && mode!=Mode.BOOLEAN && mode!=Mode.MONEY) {
			slotValueTextField = new TextField(mode == Mode.INVALID?"":currentSlot.getValue());
			grid.add(slotValueTextField, 1, 3);
			slotValueTextField.setDisable(mode == Mode.INVALID);
		} else if (mode==Mode.DATE) {
	        datePicker.setShowWeekNumbers(true);
	        grid.add(datePicker, 1, 3);
		} else if (mode==Mode.BOOLEAN) {
			trueButton.setToggleGroup(toggleGroup);
			falseButton.setToggleGroup(toggleGroup);
			toggleGroup.selectedToggleProperty().addListener(
					   (observable, oldToggle, newToggle) -> {
					       if (newToggle == trueButton) {
					    	   trueOrFalse = "true";
					       } else if (newToggle == falseButton) {
					    	   trueOrFalse = "false";
					       }});
			("true".equals(currentSlot.getValue())?trueButton:falseButton).setSelected(true);
			grid.add(trueButton, 1, 3);
			grid.add(falseButton, 1, 3);
			GridPane.setHalignment(falseButton, HPos.RIGHT);
		} else if (mode==Mode.MONEY) {
			currency.setEditable(true);
			currency.getItems().addAll("USD","EUR","GBP","AUD","NZD");
			HBox moneyHBox = new HBox(money,currency);
			grid.add(moneyHBox, 1, 3);
		} else {
			slotValueComboBox = new ComboBox<>();
			slotValueComboBox.getItems().addAll(diagram.getEnumItems(type));
			slotValueComboBox.getSelectionModel().select(currentSlot.getValue());
			grid.add(slotValueComboBox, 1, 3);
		}
		
	}

	/*private void getSlotType() {
		Vector<FmmlxAttribute> allAttributes = new Vector<>();
		FmmlxObject parent = object;
		while (parent != null) {
			allAttributes.addAll(parent.getOwnAttributes());
			allAttributes.addAll(parent.getOtherAttributes());
			parent = diagram.getObjectById(parent.getOf());
		}

		for (FmmlxAttribute attribute : allAttributes) {
			if (attribute.getName().equals(slot.getName())) {
				this.type = attribute.getType();
			}
		}
	}*/

	private void setResultConverter() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE && mode != Mode.INVALID) {
				FmmlxSlot currentSlot = initialSlot != null ? initialSlot : slotBox.getSelectionModel().getSelectedItem();
				
				if (isExpressionCheckBox.isSelected()) {
					return new Result(object, currentSlot, slotValueTextField.getText());
				} else {
					if(mode != Mode.ENUM && mode!=Mode.BOOLEAN && mode!=Mode.DATE && mode!=Mode.MONEY) {
						String slotValue = "\"" + slotValueTextField.getText() + "\"";
						return new Result(object, currentSlot, slotValue);
					} else if(mode==Mode.BOOLEAN) {
						return new Result(object, currentSlot, trueOrFalse);
					} else if(mode==Mode.DATE) {
						int year = datePicker.getValue().getYear();
						int month = datePicker.getValue().getMonthValue();
						int day = datePicker.getValue().getDayOfMonth();
						String date = "Date::createDate("+year+","+month+","+day+")";
						return new Result(object, currentSlot, date);
					} else if(mode==Mode.MONEY) {
						String currencyString = "Auxiliary::Currency(\"" + currency.getSelectionModel().getSelectedItem() + "\", \"" + currency.getSelectionModel().getSelectedItem() + "\", 1.0)";
						String moneyValue = "Auxiliary::MonetaryValue(" + money.getText() + ", " + currencyString + ")";
						System.err.println(moneyValue);
						return new Result(object, currentSlot, moneyValue);
					} else {
						String enumItem = currentSlot.getType(diagram) + "::" + slotValueComboBox.getSelectionModel().getSelectedItem();
						return new Result(object, currentSlot, enumItem);
					}
				}
			}
			return null;
		});
	}
	
	public class Result {
		public final FmmlxObject object;
		public final FmmlxSlot slot;
		public final String newValue;

		public Result(FmmlxObject object, FmmlxSlot slot, String newValue) {
			this.object = object;
			this.slot = slot;
			this.newValue = newValue;
		}
	}
}
