package tool.clients.fmmlxdiagrams.dialogs.results;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.Vector;

public class AddInstanceDialogResult extends DialogResult {

	private String name;
	private int level;
	private ObservableList<FmmlxObject> parents;
	private int of;
	private boolean isAbstract;

	public AddInstanceDialogResult(String name, int level, ObservableList<FmmlxObject> parents, int of,
								   boolean isAbstract) {
		this.name = name;
		this.level = level;
		this.parents = parents;
		this.of = of;
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

	public int getOf() {
		return of;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

}
