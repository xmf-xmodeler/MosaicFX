package tool.clients.consoleInterface;

// TODO: Auto-generated Javadoc
//import org.osgi.framework.BundleContext;

/**
 * The Class ConsoleInterfacePlugin.
 */
public class ConsoleInterfacePlugin
// extends Plugin
{

	/** The plugin. */
	private static ConsoleInterfacePlugin plugin;

	/**
	 * Instantiates a new console interface plugin.
	 */
	public ConsoleInterfacePlugin() {
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
	public static ConsoleInterfacePlugin getDefault() {
		return plugin;
	}
}
