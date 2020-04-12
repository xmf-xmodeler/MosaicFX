package tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

public class AttributeGeneratorIncrementDialogResult extends DialogResult {
	
	private String valueStart;
	private String valueEnd;
	private String increment;
	
	public AttributeGeneratorIncrementDialogResult(String value1, String value2, String increment, String type) {		
		this.valueStart = value1;
		this.valueEnd = value2;
		this.increment = increment;
	}

	public String getValueStart() {
		return valueStart;
	}

	public String getValueEnd() {
		return valueEnd;
	}

	public String getIncrement() {
		return increment;
	}

}
