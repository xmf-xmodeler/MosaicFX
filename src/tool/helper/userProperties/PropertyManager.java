package tool.helper.userProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tool.clients.menus.MenuClient;

public class PropertyManager {
	
	static final String PROPERTIES_FILE_NAME = "user.properties";
	static File userPropertiesFile;
	static Properties properties = new Properties(new DefaultUserProperties());
	private static final Logger logger = LogManager.getLogger(PropertyManager.class);
	
	//init prod file depending on XModeler stage (dev, prod)
	static {
	        String envVariableValue = System.getenv("XMODELER_STAGE");

	        if (envVariableValue != null && envVariableValue.equals("prod")) {
	        	initProdProperties(); 
	        } else {
	        	logger.debug("Programm started in dev stage");        	
	        	userPropertiesFile = new File(PROPERTIES_FILE_NAME);
	        }
	        logger.debug(String.format("user.properties file path: %s", userPropertiesFile.getAbsoluteFile()));
	}

	private static void initProdProperties() {
		logger.debug("Programm started in prod stage");
		String localAppData = System.getenv("LOCALAPPDATA");
		String prodUserPropertiesPath = localAppData + File.separator + "XModeler" + File.separator + PROPERTIES_FILE_NAME;
		userPropertiesFile = new File(prodUserPropertiesPath);	    
		if (!userPropertiesFile.exists()) {
			try {
				userPropertiesFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
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