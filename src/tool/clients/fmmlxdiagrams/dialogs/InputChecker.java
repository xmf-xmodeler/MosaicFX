package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;

import java.util.Vector;

public class InputChecker {

	private static InputChecker instance;

	private InputChecker() { }

	public static InputChecker getInstance() {
		if (instance == null) {
			instance = new InputChecker();
		}
		return instance;
	}

	public static boolean validateName(String name) {
		if (name.equals("")) {
			return false;
		} else if (checkFirstStringIsDigit(name)) {
			return false;
		} else if (name.contains(" ")) {
			return false;
		}
		return true;
	}

	private static boolean checkFirstStringIsDigit(String name) {
		char[] c = name.toCharArray();

		if (Character.isDigit(c[0])) {
			return true;
		}
		return false;
	}

	public boolean levelIsValid(int choosenLevel, int allowedMaxLevel) {
		if (choosenLevel <= allowedMaxLevel) {
			return true;
		}
		return false;
	}

//	public static boolean attributeNameIsAvailable(String name, FmmlxObject object) {
//		for (FmmlxAttribute attribute : object.getOwnAttributes()) {
//			if (name.equals(attribute.getName())) return false;
//		}
//		for (FmmlxAttribute attribute : object.getOtherAttributes()) {
//			if (name.equals(attribute.getName())) return false;
//		}
//		return true;
//	}

	public boolean classNameIsAvailable(String name, AbstractPackageViewer diagram) {
		for (FmmlxObject object : diagram.getObjects()) {
			if (name.equals(object.getName())) return false;
		}
		return true;
	}

	public boolean operationNameIsAvailable(String name, FmmlxObject object) {
		Vector<FmmlxOperation> operations = object.getOwnOperations();
		operations.addAll(object.getOtherOperations());
		for (FmmlxOperation operation : operations) {
			if (name.equals(operation.getName())) {
				return false;
			}
		}
		return true;
	}

	public boolean associationNameIsAvailable(String name, FmmlxObject object) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isComboBoxItemNull(ComboBox<?> comboBox) {
		return comboBox.getSelectionModel().getSelectedItem() == null;
	}

	public boolean isTextfieldEmpty(TextField textField) {
		return textField.getText() == null
				|| textField.getText().length() == 0;
	}
	
	public static boolean validateInteger(String string) {
		boolean isValidInteger = false;
	      try
	      {
	         Integer.parseInt(string); 
	         isValidInteger = true;
	      }
	      catch (NumberFormatException ex)
	      {
	         //
	      }	 
	      return isValidInteger;
		
	}
	
	public static boolean validateFloat(String string) {
		boolean isValidFloat = false;
	      try
	      {
	         Float.parseFloat(string);
	         isValidFloat = true;
	         
	      }
	      catch (NumberFormatException ex)
	      {
	    	  //
	      }
	 
	      return isValidFloat;
	}
	
	public static boolean validateBoolean(String string) {
		boolean isValidBoolean = false;
	      try
	      {
	         Boolean.parseBoolean(string);
	         isValidBoolean = true;
	         
	      }
	      catch (NumberFormatException ex)
	      {
	    	  //
	      }
	 
	      return isValidBoolean;
	}

}
