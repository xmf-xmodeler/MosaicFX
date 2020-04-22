package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import java.util.ArrayList;
import java.util.List;


import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class ValueGeneratorListDialogResult extends DialogResult {

	private final String attributeType;
	private final List<String> parameter;
	private final List<String> elements;
	
	public ValueGeneratorListDialogResult(String attributeType, List<String> parameter, List<String> elements) {
		this.attributeType = attributeType;
		this.parameter = parameter;
		this.elements= elements;
	}

	public List<String> getParameter() {
		return parameter;
	}

	public List<String> getElements() {
		return elements;
	}
}
