package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ChangeParentDialogResult {
	
	public final FmmlxObject object;
	public final Vector<FmmlxObject> oldParents;
	public final Vector<FmmlxObject> newParents;
	
	public ChangeParentDialogResult(FmmlxObject object, Vector<FmmlxObject> newList, Vector<FmmlxObject> oldList) {
		this.object=object;
		this.oldParents=oldList;
		this.newParents=newList;
	}

//	public FmmlxObject getObject() {
//		return object;
//	}

//	public Vector<String> getCurrentParentNames() {
//		return object.getParentsPaths();
//	}

//	public Vector<String> getCurrentParentPaths() {
//		Vector<String> parentPaths = new Vector<>();
//		for (FmmlxObject object : oldParents) {
//			parentPaths.add(object.getPath());
//		}
//		return parentPaths;
//	}
//	
//	public Vector<String> getNewParentPaths() {
//		Vector<String> parentPaths = new Vector<>();
//		for (FmmlxObject object : newParents) {
//			parentPaths.add(object.getPath());
//		}
//		return parentPaths;
//	}

	public Vector<String> getCurrentParentNames() {
		Vector<String> parentNames = new Vector<>();
		for (FmmlxObject p : oldParents) {
			parentNames.add(p.getName());
		}
		return parentNames;
	}
	
	public Vector<String> getNewParentNames() {
		Vector<String> parentNames = new Vector<>();
		for (FmmlxObject p : newParents) {
			parentNames.add(p.getName());
		}
		return parentNames;
	}

}
