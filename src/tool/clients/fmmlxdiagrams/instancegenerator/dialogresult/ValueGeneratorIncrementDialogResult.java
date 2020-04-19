package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.List;

public class ValueGeneratorIncrementDialogResult extends DialogResult {

	private List<String> parameter;

	public ValueGeneratorIncrementDialogResult(List<String> parameter, String attributeType) {
		this.parameter=parameter;
	}

    public List<String> getParameter(){
		return parameter;
	}

}
