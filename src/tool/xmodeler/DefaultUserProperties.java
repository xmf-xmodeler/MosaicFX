package tool.xmodeler;

import java.util.Properties;

public class DefaultUserProperties extends Properties {

	public DefaultUserProperties() {
		put(UserProperty.TOOL_HEIGHT.toString(), "300");
		put(UserProperty.TOOL_WIDTH.toString(), "1066");
		put(UserProperty.TOOL_X.toString(), "300");
		put(UserProperty.TOOL_Y.toString(), "300");
		put(UserProperty.LOAD_MODELS_BY_STARTUP.toString(),"false");
		put(UserProperty.APPLICATION_CLOSING_WARNING.toString(), "true");
	}
}
