package tool.clients.undo;

// TODO: Auto-generated Javadoc
/**
 * The Class UndoPlugin.
 */
public class UndoPlugin {

	/** The plugin. */
	private static UndoPlugin plugin;

	/**
	 * Instantiates a new undo plugin.
	 */
	public UndoPlugin() {
		plugin = this;
	}

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	public static UndoPlugin getDefault() {
		return plugin;
	}

	/**
	 * Early startup.
	 */
	public void earlyStartup() {
//		UndoClient undoClient = new UndoClient();
		// XmfPlugin.xos.newMessageClient("com.ceteva.undo",undoClient);
	}
}
