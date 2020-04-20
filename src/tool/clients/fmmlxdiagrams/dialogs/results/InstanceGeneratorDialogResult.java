package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;


import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;

public class InstanceGeneratorDialogResult extends DialogResult{
	
	private final FmmlxObject object;
	private final int numberOfInstance;
	private final HashMap<FmmlxAttribute, ValueGenerator> value;
	private final ObservableList<FmmlxObject> selectedParent;
	private final List<String> instanceName;

    public InstanceGeneratorDialogResult(FmmlxObject object, int numberOfInstance, List<String> instanceName, ObservableList<FmmlxObject> selectedParent, HashMap<FmmlxAttribute, ValueGenerator> value) {
        super();
		this.object = object;
		this.numberOfInstance = numberOfInstance;
		this.instanceName = instanceName;
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

	public List<String> getInstanceName() {
		return instanceName;
	}

	public Vector<Integer> getParentIDs(){

    	Vector<Integer> parentIds = new Vector<>();

    	if (!getSelectedParent().isEmpty()) {
    		for (FmmlxObject o : getSelectedParent()) {
    			parentIds.add(o.getId());
    		}
    	}
    	return parentIds;
	}

}
