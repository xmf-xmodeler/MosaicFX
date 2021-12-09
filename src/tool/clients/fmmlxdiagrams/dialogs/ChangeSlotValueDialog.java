package tool.clients.fmmlxdiagrams.dialogs;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;

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
	
	private enum Mode {DEFAULT, STRING, ENUM, INVALID}
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

		if (mode != Mode.ENUM) Platform.runLater(() -> slotValueTextField.requestFocus());
	}

	private void setMode(FmmlxSlot slot) {
		this.type = slot == null ? null : slot.getType(diagram);
		this.mode = 
				type==null?Mode.INVALID:
				"String".equals(type)?Mode.STRING:
				diagram.isEnum(type)?Mode.ENUM:
				Mode.DEFAULT;

		setInputItem();
		
		slotTypeTextfield.setText(this.type);
				
		isExpressionCheckBox.setSelected(mode == Mode.DEFAULT);
		isExpressionCheckBox.setDisable(mode == Mode.ENUM);
		
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
		if(mode != Mode.ENUM) {
			slotValueTextField = new TextField(mode == Mode.INVALID?"":currentSlot.getValue());
			grid.add(slotValueTextField, 1, 3);
			slotValueTextField.setDisable(mode == Mode.INVALID);
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
					if(mode != Mode.ENUM) {
						String slotValue = "\"" + slotValueTextField.getText() + "\"";
						return new Result(object, currentSlot, slotValue);
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
