package tool.clients.fmmlxdiagrams.dialogs.results;

public class MetaClassDialogResult extends DialogResult {

	private String name;
	private int level;
	private boolean isAbstract;

	// TODO: change TYPE!!!!
	int parent;

	public MetaClassDialogResult(String name, int level, boolean isAbstract, int parent) {
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

	public int getParent() {
		return parent;
	}

}
