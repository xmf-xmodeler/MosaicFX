package tool.clients.undo;

import tool.clients.Client;
import tool.clients.EventHandler;
import xos.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class UndoClient.
 */
public class UndoClient extends Client {

	// public static UndoAction undo = new UndoAction();
	// public static RedoAction redo = new RedoAction();
	/** The handler. */
	public EventHandler handler = null;

	/* (non-Javadoc)
	 * @see uk.ac.mdx.xmf.swt.client.Client#setEventHandler(uk.ac.mdx.xmf.swt.client.EventHandler)
	 */
	public void setEventHandler(EventHandler eventsOut) {
		handler = eventsOut;
	}

	/**
	 * Instantiates a new undo client.
	 */
	public UndoClient() {
		super("com.ceteva.undo");
	}

	// public void setEventHandler(EventHandler handler) {
	// this.handler = handler;
	// // undo.registerEventHandler(handler);
	// // redo.registerEventHandler(handler);
	// }

	/* (non-Javadoc)
	 * @see uk.ac.mdx.xmf.swt.client.Client#processMessage(xos.Message)
	 */
	public boolean processMessage(Message message) {
		// if (message.hasName("enableUndo")) {
		// undo.setEnabled(true);
		// return true;
		// }
		// if (message.hasName("disableUndo")) {
		// undo.setEnabled(false);
		// return true;
		// }
		// if (message.hasName("enableRedo")) {
		// redo.setEnabled(true);
		// return true;
		// }
		// if (message.hasName("disableRedo")) {
		// redo.setEnabled(false);
		// return true;
		// }
		return false;
	}
}