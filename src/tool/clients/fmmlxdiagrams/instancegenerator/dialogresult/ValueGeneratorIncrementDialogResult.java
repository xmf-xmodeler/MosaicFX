package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.ArrayList;
import java.util.List;

public class ValueGeneratorIncrementDialogResult extends DialogResult {
	
	private String valueStart;
	private String valueEnd;
	private String increment;
	
	public ValueGeneratorIncrementDialogResult(String value1, String value2, String increment, String type) {
		this.valueStart = value1;
		this.valueEnd = value2;
		this.increment = increment;
	}

	public List<String> getParameter(){
		List<String> result = new ArrayList<>();
		result.add(valueStart);
		result.add(valueEnd);
		result.add(increment);
		return result;
	}

}
