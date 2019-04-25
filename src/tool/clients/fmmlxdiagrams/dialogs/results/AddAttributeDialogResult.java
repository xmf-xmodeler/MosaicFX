package tool.clients.fmmlxdiagrams.dialogs.results;

public class AddAttributeDialogResult extends DialogResult {
	
	private String name;
	private String type;
	private int level;
	
	public AddAttributeDialogResult(String name, int level, String type) {
		this.name = name;
		this.level = level;
		this.type =type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
