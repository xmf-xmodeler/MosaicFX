package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeParentDialogResult {
	
	private FmmlxObject object;
	private ObservableList<FmmlxObject> newParent;
	
	public ChangeParentDialogResult(FmmlxObject object, ObservableList<FmmlxObject> observableList) {
		this.object=object;
		this.newParent=observableList;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public Vector<String> getCurrentParentNames() {
		return object.getParentsPaths();
	}

	public Vector<String> getNewParentPaths() {
		Vector<String> parentPaths = new Vector<>();

		if (newParent.size() > 0) {
			for (FmmlxObject object : newParent) {
				parentPaths.add(object.getOwnPath());
			}
		}
		return parentPaths;
	}

	public Vector<String> getNewParentNames() {
		Vector<String> parentNames = new Vector<>();

		if (newParent.size() > 0) {
			for (FmmlxObject object : newParent) {
				parentNames.add(object.getName());
			}
		}
		return parentNames;
	}

}
