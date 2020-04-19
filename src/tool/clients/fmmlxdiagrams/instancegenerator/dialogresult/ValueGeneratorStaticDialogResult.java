package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.ArrayList;
import java.util.List;

public class ValueGeneratorStaticDialogResult extends DialogResult {

	private List<Integer> valueInt;

	private List<Float> valueFloat;

	private List<Boolean> valueBool;

	private List<String> valueString;
	
	public ValueGeneratorStaticDialogResult(List<String> parameter, String type) {

		switch (type) {
			case "Boolean":
				this.valueBool = new ArrayList<>();
				valueBool.add(Boolean.parseBoolean(parameter.get(0)));
				break;
			case "String":
				this.valueString = new ArrayList<>();
				valueString.add(parameter.get(0));
				break;
			case "Integer":
				this.valueInt = new ArrayList<>();
				valueInt.add(Integer.parseInt(parameter.get(0)));
				break;
			case "Float":
				this.valueFloat = new ArrayList<>();
				valueFloat.add(Float.parseFloat(parameter.get(0)));
				break;
		}
	}
	
	public List<String> getValueInt() {
		List<String> result = new ArrayList<>();
		result.add(valueInt.get(0).toString());
		return result;
	}

	public List<String> getValueFloat() {
		List<String> result = new ArrayList<>();
		result.add(valueFloat.get(0).toString());
		return result;
	}

	public List<String> getValueBool() {
		List<String> result = new ArrayList<>();
		result.add(valueBool.get(0).toString());
		return result;
	}

	public List<String> getValueString() {
		return valueString;
	}

}
