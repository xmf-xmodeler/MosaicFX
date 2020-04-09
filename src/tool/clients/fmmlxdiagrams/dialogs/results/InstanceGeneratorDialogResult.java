package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.HashMap;


import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.instance.ValueGenerator;

public class InstanceGeneratorDialogResult extends DialogResult{
	
	private FmmlxObject object;
	private int numberOfInstance;
	private HashMap<FmmlxAttribute, ValueGenerator> value;

	public InstanceGeneratorDialogResult(FmmlxObject object, int numberOfInstance, HashMap<FmmlxAttribute, ValueGenerator> value) {
		super();
		this.object = object;
		this.value = value;
		this.numberOfInstance = numberOfInstance;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public HashMap<FmmlxAttribute, ValueGenerator> getValue() {
		return value;
	}

	public int getNumberOfInstance() {
		return numberOfInstance;
	}

}
