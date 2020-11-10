package tool.clients.fmmlxdiagrams.dialogs.results;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.Vector;

public class AddInstanceDialogResult {

	private String name;
	private int level;
	private ObservableList<FmmlxObject> parents;
	private String ofName;
	private boolean isAbstract;

	public AddInstanceDialogResult(String name, int level, ObservableList<FmmlxObject> parents, String ofName,
								   boolean isAbstract) {
		this.name = name;
		this.level = level;
		this.parents = parents;
		this.ofName = ofName;
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public ObservableList<FmmlxObject> getParents() {
		return parents;
	}

	public Vector<Integer> getParentId() {
		Vector<Integer> parentIds = new Vector<>();

		if (!parents.isEmpty()) {
			for (FmmlxObject o : parents) {
				parentIds.add(o.getId());
			}
		}
		return parentIds;
	}

	public String getOfName() {
		return ofName;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public Vector<String> getParentNames() {
		Vector<String> parentnames = new Vector<>();

		if (!parents.isEmpty()) {
			for (FmmlxObject o : parents) {
				parentnames.add(o.getName());
			}
		}
		return parentnames;
	}
}
