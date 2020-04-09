package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;


public class AttributeGeneratorDialog extends CustomDialog<AttributeGeneratorDialogResult>{
	
	private InstanceGeneratorGenerateType type;
	private String attributeType;
	
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
	
	//For Random -------------------------------
	
	//StaticValue
	private String staticValue;
	
	//IncValues
	private String startValue;
	private String endValue;
	private String incValue;
	
	//ListValues
	
	//RandomValue
	

	public AttributeGeneratorDialog(InstanceGeneratorGenerateType type, String attributeType) {
		this.type=type;
		this.attributeType = attributeType;
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString() + " : "+attributeType);
		layoutContent();
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {		
				e.consume();
			}
		});
		setResult();
	}


	public <T> AttributeGeneratorDialog(InstanceGeneratorGenerateType type, String attributeType, List<T> value) {
		this.type=type;
		this.attributeType = attributeType;
		System.out.println();
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(type.toString());
		layoutContent();
		dialogPane.setContent(flow);
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!inputIsValid()) {
				e.consume();
			}
		});
		setResult();
		
		switch(type){
        case INCREMENT:
        	this.startValue = value.get(0).toString();
        	this.startValueTextField.setText(startValue);
        	this.endValue = value.get(1).toString();
        	this.endValueTextField.setText(endValue);
        	this.incValue = value.get(2).toString();
        	this.incrementValueTextField.setText(incValue);
        	break;
        case STATIC:
        	if (attributeType.equals("Boolean")) {
        		this.staticValue =  value.get(0).toString();
        		staticValueComboBox.setValue(value.toString());
        	} else {
        		this.staticValue =  value.get(0).toString();
            	staticValueTextField.setText(staticValue);
        	}

        	break;
        case LIST:     	
        	break;
        case NORMALDISTRIBUTION:
        	break;
        case RANDOM:
        	break;
        default:
            System.out.println("undifined Type");
        }
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {		
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				switch(type){
		        case INCREMENT:
		        	return new AttributeGeneratorDialogResult(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
		        case STATIC:
		        	if (attributeType.equals("Boolean")) {
		        		return new AttributeGeneratorDialogResult(staticValueComboBox.getSelectionModel().getSelectedItem(), attributeType, type);
		        	} else {
		        		return new AttributeGeneratorDialogResult(staticValueTextField.getText(), attributeType, type);
		        	}
		        case LIST:
		        	//return new AttributeGeneratorDialogResult(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
		        	break;
		        case NORMALDISTRIBUTION:
		        	//return new AttributeGeneratorDialogResult(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
		        	break;
		        case RANDOM:
		        	//return new AttributeGeneratorDialogResult(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText(), attributeType, type);
		        	break;
		        default:
		            System.out.println("undifined Type");
		        }
			}
			return null;
		});	
	}

	private boolean inputIsValid() {
		switch(type){
        case INCREMENT:
            return validateIncrement(startValueTextField.getText(), endValueTextField.getText(), incrementValueTextField.getText());    
        case STATIC:
        	if (attributeType.equals("Boolean")) {
        		if (staticValueComboBox.getSelectionModel().getSelectedItem()!=null) {
        			return true;
        		}
        	}
        	return validateStatic(staticValueTextField.getText());
        case RANDOM:
        	return validateRandom();    
        case LIST:
        	return validateList();      
        default:
            return false;
        }
		
	}

	private boolean validateRandom() {
		switch(attributeType){
        case "Integer":
            //TODO
            return false;
        case "Float":
        	//TODO
        	 return false;
        case "String":
        	//TODO
        	 return false;
        case "Boolean":
        	//TODO
        	 return false;
        default:
        	 return false;
		}
		
	}


	private boolean validateList() {
		switch(attributeType){
        case "Integer":
            //TODO
        	 return false;
        case "Float":
        	//TODO
        	 return false;
        case "String":
        	//TODO
        	 return false;
        case "Boolean":
        	//TODO
        	 return false;
        default:
        	 return false;
        }
		
	}

	private boolean validateStatic(String string) {
		switch(attributeType){
        case "Integer":
            return validateInteger(string);
        case "Float":
        	return validateFloat(string);
        case "String":
        	return validateString(string);
        case "Boolean":
        	return validateBoolean(string);
        default:
            return false;
        }		
	}

	private boolean validateBoolean(String string) {
		boolean isValidBoolean = false;
	      try
	      {
	         Boolean.parseBoolean(string);
	         isValidBoolean = true;
	         
	      }
	      catch (NumberFormatException ex)
	      {
	    	  errorLabel.setText("Please select boolean value");
	      }
	 
	      return isValidBoolean;
	}

	private boolean validateString(String string) {

		if (!InputChecker.getInstance().validateName(string)) {
			errorLabel.setText("Enter valid name!");
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateFloat(String string) {
		boolean isValidFloat = false;
	      try
	      {
	         Float.parseFloat(string);
	         isValidFloat = true;
	         
	      }
	      catch (NumberFormatException ex)
	      {
	    	  errorLabel.setText("Please input valid Float-Value");
	      }
	 
	      return isValidFloat;
	}

	private boolean validateInteger(String string) {
		boolean isValidInteger = false;
	      try
	      {
	         Integer.parseInt(string); 
	         isValidInteger = true;
	      }
	      catch (NumberFormatException ex)
	      {
	         errorLabel.setText("Please input valid Integer-Value");
	      }	 
	      return isValidInteger;
		
	}

	private boolean validateIncrement(String startValue, String endValue, String increment) {
		Boolean valid = false;
		switch(attributeType){
        case "Integer":   	
        	valid = validateInteger(startValue) && validateInteger(endValue) && validateInteger(increment);
        	if(!valid) {
        		errorLabel.setText("Please input valid Integer-Value");
        	}
        	return valid;
        case "Float":
        	valid =  validateFloat(startValue) && validateFloat(endValue) && validateFloat(increment);
        	if(!valid) {
        		errorLabel.setText("Please input valid Float-Value");
        	}
        	return valid;
        default:
           	return false;
        }
		
	}

	private void layoutContent() {
		switch(type){
        case INCREMENT:
            layoutIncrement(attributeType);
            break;
        case STATIC:
        	layoutStatic(attributeType);
            break;
        case RANDOM:
        	layoutRandom(attributeType);
            break;
        case LIST:
        	layoutList(attributeType);
            break;
        case NORMALDISTRIBUTION:
        	layoutNormalDistribution(attributeType);
        	break;
        default:
            System.out.println("undifined Type");
        }
		
		addNodesToGrid(labelNode, 0);
		addNodesToGrid(inputNode, 1);	
	}

	private void layoutNormalDistribution(String attributeType2) {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();		
	}

	private void layoutRandom(String attributeType2) {
		labelNode = new ArrayList<Node>();
		inputNode = new ArrayList<Node>();	
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
		
		if(attributeType.equals("Boolean")) {
			staticValueComboBox = new ComboBox<String>(AllValueList.booleanList);
			inputNode.add(staticValueComboBox);
		} else {
			staticValueTextField = new TextField();
			inputNode.add(staticValueTextField);
		}		
		labelNode.add(staticValueLabel);	
	}
}
