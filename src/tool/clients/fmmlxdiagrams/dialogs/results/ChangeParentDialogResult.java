package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeParentDialogResult extends DialogResult{
	
	private FmmlxObject object;
	private ObservableList<FmmlxObject> newParent;
	
	public ChangeParentDialogResult(FmmlxObject object, ObservableList<FmmlxObject> observableList) {
		this.object=object;
		this.newParent=observableList;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public Vector<Integer> getCurrentParentIds() {
		return object.getParents();
	}

	public Vector<Integer> getNewParentIds() {
		Vector<Integer> parentIds = new Vector<>();

		if (newParent.size() > 0) {
			for (FmmlxObject object : newParent) {
				parentIds.add(object.getId());
			}
		}
		return parentIds;
	}

}
