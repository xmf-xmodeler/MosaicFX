package tool.clients.fmmlxdiagrams.dialogs;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class InputChecker {
	
	private static InputChecker instance;
	
	public InputChecker() {
		// Blank Constructor needed
	}

	public static InputChecker getInstance() {
		if (instance==null) {
			instance = new InputChecker();
		}
		return instance;
	}
	
	public boolean validateName(String name) {
		if (name.equals("")) {
			return false;
		} else if(checkFirstStringIsDigit(name)) {
			return false;
		}
		return true;
	}
		
	private boolean checkFirstStringIsDigit(String name) {
		char[] c = name.toCharArray();
		
		if (Character.isDigit(c[0])){
			return true;
		}
		return false;
	}

	public boolean levelIsValid(int choosenLevel, int allowedMaxLevel) {
		if (choosenLevel<=allowedMaxLevel) {
			return true;
		}
		return false;
	}

	public boolean attributeNameIsAvailable(String name, FmmlxObject object) {
		for (FmmlxAttribute attribute : object.getOwnAttributes()) {
			if (name.equals(attribute.getName())) return false;
		}
		for (FmmlxAttribute attribute : object.getOtherAttributes()) {
			if (name.equals(attribute.getName())) return false;
		}	
		return true;
	}

	public boolean classNameIsAvailable(String name, FmmlxDiagram diagram) {
		for (FmmlxObject object : diagram.getObjects()) {
			if(name.equals(object.getName())) return false;
		}
		return true;
	}

	public boolean associationNameIsAvailable(String name, FmmlxObject object) {
		// TODO Auto-generated method stub
		return false;
	}

}
