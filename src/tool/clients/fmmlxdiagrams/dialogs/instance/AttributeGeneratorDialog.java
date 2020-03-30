package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.ValueList;

public class AttributeGeneratorDialog extends CustomDialog<AttributeGeneratorDialogResult>{
	
	private FmmlxAttribute attribute;
	private InstanceGeneratorGenerateType type;
	
	private DialogPane dialogPane;
	
	private List<Node> labelNode, inputNode;
	
	//For Static -------------------------------
	private Label staticValueLabel;
	private TextField staticValueTextField;
	private ComboBox<String> staticValueComboBox;
	//==========================================
	
	//For List ---------------------------------
	private Label valueListLabel;
	private ListView<String> listValue;
	private Button addItemButton;
	private Button removeItemButton;
	//==========================================
	
	//For Increment ----------------------------
	private Label startValueLabel;
	private Label endValueLabel;
	private Label incrementValueLabel;
	
	private TextField startValueTextField;
	private TextField endValueTextField;
	private TextField incrementValueTextField;
	//==========================================
	

	public AttributeGeneratorDialog(FmmlxAttribute tmp, InstanceGeneratorGenerateType selectedType) {
		this.attribute=tmp;
		this.type=selectedType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(attribute.getName()+ " : "+ type);
		layoutContent();
		dialogPane.setContent(flow);
		setValidation();
		setResult();
	}

	private void setResult() {
		// TODO Auto-generated method stub
		
	}

	private void setValidation() {
		switch(type){
        case INCREMENT:
            validateIncrement();
            break;
        case STATIC:
        	validateStatic();
            break;
        case NULL:
        	//TODO
            break;
        case LIST:
        	validateList();
            break;
        default:
            System.out.println("undifined Type");
        }
		
	}

	private void validateList() {
		switch(attribute.getType()){
        case "Integer":
            //TODO
            break;
        case "Float":
        	//TODO
            break;
        case "String":
        	//TODO
            break;
        case "Boolean":
        	//TODO
            break;
        default:
            System.out.println("undifined Type");
        }
		
	}

	private void validateStatic() {
		switch(attribute.getType()){
        case "Integer":
            validateStaticForInteger();
            break;
        case "Float":
        	validateStaticForFloat();
            break;
        case "String":
        	validateStaticForString();
            break;
        case "Boolean":
        	validateStaticForBoolean();
            break;
        default:
            System.out.println("undifined Type");
        }		
	}

	private void validateStaticForBoolean() {
		// TODO Auto-generated method stub
		
	}

	private void validateStaticForString() {
		// TODO Auto-generated method stub
		
	}

	private void validateStaticForFloat() {
		// TODO Auto-generated method stub
		
	}

	private void validateStaticForInteger() {
		// TODO Auto-generated method stub
		
	}

	private void validateIncrement() {
		switch(attribute.getType()){
        case "Integer":
            //TODO
            break;
        case "Float":
        	//TODO
            break;
        case "String":
        	//TODO
            break;
        case "Boolean":
        	//TODO
            break;
        default:
            System.out.println("undifined Type");
        }
	}

	private void layoutContent() {
		switch(type){
        case INCREMENT:
            layoutIncrement(attribute.getType());
            break;
        case STATIC:
        	layoutStatic(attribute.getType());
            break;
        case NULL:
        	//TODO
            break;
        case LIST:
        	layoutList(attribute.getType());
            break;
        default:
            System.out.println("undifined Type");
        }
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);	
	}

	private void layoutList(String type2) {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		
		valueListLabel = new Label(StringValue.LabelAndHeaderTitle.valueList);
		listValue = new ListView<String>();
		
		addItemButton = new Button("Add");
		removeItemButton = new Button("Remove");
		
		labelNode.add(valueListLabel);
		
		inputNode.add(listValue);
		inputNode.add(joinNodeElementInHBox(addItemButton, removeItemButton));
			
				
	}

	private void layoutIncrement(String type2) {	
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
		
		startValueLabel = new Label(StringValue.LabelAndHeaderTitle.startValue);
		endValueLabel = new Label(StringValue.LabelAndHeaderTitle.endValue);
		incrementValueLabel = new Label(StringValue.LabelAndHeaderTitle.incrementValue);
		
		startValueTextField = new TextField();
		endValueTextField = new TextField();
		incrementValueTextField = new TextField();
		
		labelNode.add(startValueLabel);
		labelNode.add(endValueLabel);
		labelNode.add(incrementValueLabel);
		
		inputNode.add(startValueTextField);
		inputNode.add(endValueTextField);
		inputNode.add(incrementValueTextField);
	}

	private void layoutStatic(String type2) {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();
			
		staticValueLabel = new Label(StringValue.LabelAndHeaderTitle.value);
		
		if(attribute.getType().equals("Boolean")) {
			staticValueComboBox = new ComboBox<String>(AllValueList.booleanList);
			inputNode.add(staticValueComboBox);
		} else {
			staticValueTextField = new TextField();
			inputNode.add(staticValueTextField);
		}
		
		labelNode.add(staticValueLabel);
		
	}

}
