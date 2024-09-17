package tool.clients.fmmlxdiagrams.dialogs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;
import tool.helper.XDate;

public class ChangeSlotValueDialog extends CustomDialog<ChangeSlotValueDialog.Result> {

	private final AbstractPackageViewer diagram;
	private final FmmlxObject object;
	private final FmmlxSlot initialSlot;
	private String type;
	private boolean dateFormatValidation = true;

	private TextField slotValueTextField;
	private ComboBox<String> slotValueComboBox;
	private CheckBox isExpressionCheckBox = new CheckBox();
	private ComboBox<FmmlxSlot> slotBox;
	private TextField slotTypeTextfield;
	private ToggleGroup toggleGroup = new ToggleGroup();
	private RadioButton trueButton = new RadioButton("true");
	private RadioButton falseButton = new RadioButton("false");
	private String trueOrFalse="false";
	private DatePicker datePicker = buildDatePicker();
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
		setOnCloseRequest(event -> {
			if (!dateFormatValidation) {
				dateFormatValidation = true;
				event.consume();
			}
		});		
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

		grid.add(new Label(LabelAndHeaderTitle.aClass), 	0, 0);
		grid.add(new Label(LabelAndHeaderTitle.name), 		0, 1);
		grid.add(new Label(LabelAndHeaderTitle.type), 		0, 2);
		grid.add(new Label(LabelAndHeaderTitle.value), 		0, 3);
		grid.add(new Label(LabelAndHeaderTitle.expression), 0, 4);
		
		grid.add(classNameTextField, 	1, 0);
		grid.add(slotName, 		        1, 1);
		grid.add(slotTypeTextfield, 	1, 2);
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
			grid.add(datePicker, 1, 3);
			if (!initialSlot.getValue().equals("null")) {
				try{
					LocalDate currentDateValue = XDate.parseStringToLocalDate(initialSlot.getValue(),"dd MMM yyyy");
					datePicker.setValue(currentDateValue);
				} catch (Exception e) {
					System.err.println("Date unparseable!");
				}				
			}
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
			currency.getItems().addAll("USD","EUR","GBP","AUD","NZD","SEK");
			HBox moneyHBox = new HBox(money,currency);
			grid.add(moneyHBox, 1, 3);
		} else {
			slotValueComboBox = new ComboBox<>();
			slotValueComboBox.getItems().addAll(diagram.getEnumItems(type));
			slotValueComboBox.getSelectionModel().select(currentSlot.getValue());
			grid.add(slotValueComboBox, 1, 3);
		}
		
	}

	private DatePicker buildDatePicker() {
		datePicker = new DatePicker();
		datePicker.setConverter(new StringConverter<LocalDate>() {
			 String pattern = "dd MMM yyyy";
			 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			 {
			     datePicker.setPromptText(pattern.toLowerCase());
			 }

			 @Override public String toString(LocalDate date) {
			     if (date != null) {
			         return dateFormatter.format(date);
			     } else {
			         return "";
			     }
			 }

			 @Override public LocalDate fromString(String string) {
			     if (string != null && !string.isEmpty()) {
			         return LocalDate.parse(string, dateFormatter);
			     } else {
			         return null;
			     }
			 }
			});
		datePicker.setShowWeekNumbers(true);
		return datePicker;
	}

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
						String newDateValue = datePicker.getEditor().getText(); 
						LocalDate parsedDate = null;
						try {
 							parsedDate = XDate.parseStringToLocalDate(newDateValue, "dd MMM yyyy");														
						} catch (DateTimeParseException e) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setHeaderText("Input of wrong format");
							alert.setContentText("You inserted the date in a wrong format. Choose date via Datepicker or enter date in the Format 'dd MMM yyyy'.");
							alert.show();
							dateFormatValidation = false;
						}
						int year = parsedDate.getYear();
						int month = parsedDate.getMonthValue();
						int day = parsedDate.getDayOfMonth();
						String date = String.format("Date::createDate(%s,%s,%s)", year, month, day);
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