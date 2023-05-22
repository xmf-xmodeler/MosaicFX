package tool.xmodeler;

public enum UserProperty {
	TOOL_HEIGHT("toolHeight"),
	TOOL_WIDTH("toolWidth"),
	TOOL_X("toolX"),
	TOOL_Y("toolY"),
	LOAD_MODELS_BY_STARTUP("loadModelsByStartup"),
	MODELS_DIR("modelsDir");
	
	private String saveName; 
	
	private UserProperty(String saveName) {
		this.saveName = saveName;
	}

	public String toString() {
		return saveName;
	}
}
