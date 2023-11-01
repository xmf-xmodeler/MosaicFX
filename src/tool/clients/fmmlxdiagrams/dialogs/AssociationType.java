package tool.clients.fmmlxdiagrams.dialogs;

public class AssociationType {
	public final String displayName;
	public final String path;
	
	public AssociationType(String displayName, String path) {
		super();
		this.displayName = displayName;
		this.path = path;
	}

	public String toString() {
		return displayName;
	}
	
	
}
