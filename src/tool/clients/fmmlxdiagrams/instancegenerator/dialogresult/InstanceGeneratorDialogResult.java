package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;


import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;

public class InstanceGeneratorDialogResult extends DialogResult {
	
	private final FmmlxObject object;
	private final int numberOfInstance;
	private final HashMap<FmmlxAttribute, ValueGenerator> value;
	private final ObservableList<FmmlxObject> selectedParent;

    public InstanceGeneratorDialogResult(FmmlxObject object, int numberOfInstance, ObservableList<FmmlxObject> selectedParent, HashMap<FmmlxAttribute, ValueGenerator> value) {
        super();
		this.object = object;
		this.numberOfInstance = numberOfInstance;
		this.selectedParent = selectedParent;
		this.value = value;
    }

    public FmmlxObject getObject() {
		return object;
	}

	public ObservableList<FmmlxObject> getSelectedParent() {
		return selectedParent;
	}

	public HashMap<FmmlxAttribute, ValueGenerator> getValue() {
		return value;
	}

	public int getNumberOfInstance() {
		return numberOfInstance;
	}


}
