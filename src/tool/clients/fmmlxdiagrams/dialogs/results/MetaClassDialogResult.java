package tool.clients.fmmlxdiagrams.dialogs.results;

public class MetaClassDialogResult extends DialogResult {

	private String name;
	private String level;
	private boolean isAbstract;

	// TODO: change TYPE!!!!
	String parent;

	public MetaClassDialogResult(String name, String level, boolean isAbstract, String parent) {
		this.name = name;
		this.level = level;
		this.isAbstract = isAbstract;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public String getLevel() {
		return level;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public String getParent() {
		return parent;
	}

}
