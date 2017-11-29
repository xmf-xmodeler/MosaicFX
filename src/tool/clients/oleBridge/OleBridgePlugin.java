package tool.clients.oleBridge;


// TODO: Auto-generated Javadoc
/**
 * The Class OleBridgePlugin.
 */
public class OleBridgePlugin {

	/** The plugin. */
	private static OleBridgePlugin plugin;

	/**
	 * Instantiates a new ole bridge plugin.
	 */
	public OleBridgePlugin() {
		plugin = this;
	}

	// public void start(BundleContext context) throws Exception {
	// super.start(context);
	// }
	//
	// public void stop(BundleContext context) throws Exception {
	// super.stop(context);
	// plugin = null;
	// }

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	public static OleBridgePlugin getDefault() {
		return plugin;
	}
	/*
	 * public void earlyStartup() { OleBridgeClient client = new
	 * OleBridgeClient();
	 * XmfPlugin.xos.newMessageClient("com.ceteva.oleBridge",client); }
	 */

}
