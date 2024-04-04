package tool.helper.user_properties;

public enum UserProperty {
	TOOL_HEIGHT("toolHeight"),
	TOOL_WIDTH("toolWidth"),
	TOOL_X("toolX"),
	TOOL_Y("toolY"),
	LOAD_MODELS_BY_STARTUP("loadModelsByStartup"),
	MODELS_DIR("modelsDir"),
	RECENTLY_LOADED_MODEL_DIR("recentlyLoadedModelDir"),
	APPLICATION_CLOSING_WARNING("applicationClosingWarning"),
	RECENTLY_SAVED_MODEL_DIR("recentlySavedModelDir");
	
	private String saveName; 
	
	private UserProperty(String saveName) {
		this.saveName = saveName;
	}

	public String toString() {
		return saveName;
	}
}
