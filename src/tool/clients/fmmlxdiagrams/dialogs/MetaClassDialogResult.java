package tool.clients.fmmlxdiagrams.dialogs;

public class MetaClassDialogResult {

	String name;
	String level;
	boolean isAbstract;

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

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
