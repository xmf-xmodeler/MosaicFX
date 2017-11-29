package tool.clients.undo.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import tool.clients.EventHandler;
import xos.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class RedoAction.
 */
public class RedoAction extends Action {

	/** The handler. */
	EventHandler handler;

	/**
	 * Instantiates a new redo action.
	 */
	public RedoAction() {
		super("&Redo");
		setEnabled(false);
		setImages();
		setId("com.ceteva.xmf.redoAction");
	}

	/**
	 * Sets the images.
	 */
	public void setImages() {
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
		this.setAccelerator(SWT.CTRL | 'Y');
	}

	/**
	 * Register event handler.
	 *
	 * @param handler the handler
	 */
	public void registerEventHandler(EventHandler handler) {
		this.handler = handler;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if (handler != null) {
			Message m = handler.newMessage("redo", 0);
			handler.raiseEvent(m);
		}
	}
}
