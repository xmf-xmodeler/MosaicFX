package tool.xmodeler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import tool.clients.menus.MenuClient;

public class PropertyManager {
	static File userPropertiesFile = new File("user.properties");
	static Properties properties = new Properties(new DefaultUserProperties());
	
	public PropertyManager() {
		loadProperties();
	}

	private void loadProperties() {
		try {
			properties.load(new FileInputStream(userPropertiesFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeProperties() {
        setXmfDebugging();
		try {
			properties.store(new FileOutputStream(userPropertiesFile.toString()), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		storeProperties();
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return Integer.parseInt(properties.getProperty(key, defaultValue+""));
	}

	public static boolean getProperty(String key, boolean defaultValue) {
		return Boolean.parseBoolean(properties.getProperty(key, defaultValue+""));
	}
	
	public static void deleteProperty(String key) {
		properties.remove(key);
		storeProperties();
	}
	
	public void showPropertyManagerStage() {
		new PropertyManagerStage().show();
	}
	
	//set xmf debugging values
	private static void setXmfDebugging() {
	MenuClient.setClientCommunicationMonitoring(getProperty("MONITOR_CLIENT_COMMUNICATION", false));
	MenuClient.setDaemonMonitoring(getProperty("MONITOR_DAEMON_FIRING", false));
	}

	public static void setXmfSettings() {
	if (properties != null) setXmfDebugging();
	}
}