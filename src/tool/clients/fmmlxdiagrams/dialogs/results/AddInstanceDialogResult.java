package tool.clients.fmmlxdiagrams.dialogs.results;

import javafx.collections.ObservableList;

public class AddInstanceDialogResult extends DialogResult {

	private String name;
	private int level;
	private ObservableList<String> parents;
	private String of;
	private boolean isAbstract;

	public AddInstanceDialogResult(String name, int level, ObservableList<String> parents, String of,
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

	public ObservableList<String> getParents() {
		return parents;
	}

	public String getOf() {
		return of;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

}
