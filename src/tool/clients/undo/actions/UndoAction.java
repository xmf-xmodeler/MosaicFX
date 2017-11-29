package tool.clients.undo.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import tool.clients.EventHandler;
import xos.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class UndoAction.
 */
public class UndoAction extends Action {

	/** The handler. */
	EventHandler handler;

	/**
	 * Instantiates a new undo action.
	 */
	public UndoAction() {
		super("&Undo");
		setEnabled(false);
		setImages();
		setId("com.ceteva.xmf.undoAction");
	}

	/**
	 * Sets the images.
	 */
	public void setImages() {
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
		this.setAccelerator(SWT.CTRL | 'Z');
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
			Message m = handler.newMessage("undo", 0);
			handler.raiseEvent(m);
		}
	}
}
