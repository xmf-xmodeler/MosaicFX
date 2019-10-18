package tool.clients.fmmlxdiagrams.dialogs.results;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.Vector;

public class MetaClassDialogResult extends DialogResult {

	private String name;
	private int level;
	private boolean isAbstract;
	private ObservableList<FmmlxObject> parent;

	public MetaClassDialogResult(String name, int level, boolean isAbstract, ObservableList<FmmlxObject> parent) {
		this.name = name;
		this.level = level;
		this.isAbstract = isAbstract;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public Vector<Integer> getParentIds() {
		Vector<Integer> parentIds = new Vector<>();

		if (parent.size() > 0) {
			for (FmmlxObject object : parent) {
				parentIds.add(object.getId());
			}
		}
		return parentIds;
	}
}
