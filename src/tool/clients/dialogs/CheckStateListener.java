package tool.clients.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving checkState events.
 * The class that is interested in processing a checkState
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addCheckStateListener<code> method. When
 * the checkState event occurs, that object's appropriate
 * method is invoked.
 *
 * @see CheckStateEvent
 */
class CheckStateListener implements ICheckStateListener {
	
	/** The disabled. */
	private Vector disabled;
	
	/**
	 * Instantiates a new check state listener.
	 *
	 * @param disabled the disabled
	 */
	public CheckStateListener(Vector disabled) {
		this.disabled = disabled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
	 */
	public void checkStateChanged(CheckStateChangedEvent event) {
		CheckboxTreeViewer viewer = (CheckboxTreeViewer)event.getSource();
		Object[] objects = viewer.getCheckedElements();
		for(int i=0;i<objects.length;i++) {
			TreeElement element = (TreeElement)objects[i];
			if(disabled.contains(element))
			  viewer.setChecked(element,false);	
		}
	}
	
}