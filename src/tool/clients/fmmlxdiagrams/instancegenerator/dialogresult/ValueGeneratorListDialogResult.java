package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import java.util.ArrayList;
import java.util.List;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorListDialogResult extends DialogResult {
	
	private List<Integer> intValues = new ArrayList<Integer>();
	private List<Float> floatValues = new ArrayList<Float>();
	private List<Boolean> boolValues = new ArrayList<Boolean>();
	private List<String> stringValues = new ArrayList<String>();
	
	public ValueGeneratorListDialogResult(List<String> values, String type) {

		if(type.equals(StringValue.TraditionalDataType.INTEGER)) {
			for(String str : values) {
				intValues.add(Integer.parseInt(str));
			}
		} else if (type.equals(StringValue.TraditionalDataType.FLOAT)) {
			for(String str : values) {
				floatValues.add(Float.parseFloat(str));
			}
		} else if (type.equals(StringValue.TraditionalDataType.BOOLEAN)) {
			for(String str : values) {
				boolValues.add(Boolean.parseBoolean(str));
			}
		} else if (type.equals(StringValue.TraditionalDataType.STRING)) {
			stringValues = values;
		} 
	}

	public List<Integer> getIntValues() {
		return intValues;
	}

	public List<Float> getFloatValues() {
		return floatValues;
	}

	public List<Boolean> getBoolValues() {
		return boolValues;
	}

	public List<String> getStringValues() {
		return stringValues;
	}

	
}
