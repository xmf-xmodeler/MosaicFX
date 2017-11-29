package tool.clients.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiSelectionTreeDialog.
 */
class MultiSelectionTreeDialog extends CheckedTreeSelectionDialog {
	
	/**
	 * Instantiates a new multi selection tree dialog.
	 *
	 * @param shell the shell
	 * @param labelProvider the label provider
	 * @param content the content
	 */
	public MultiSelectionTreeDialog(Shell shell,ILabelProvider labelProvider,ITreeContentProvider content) {
		super(shell,labelProvider,content);
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.CheckedTreeSelectionDialog#updateOKStatus()
     */
    protected void updateOKStatus() {
    	super.updateOKStatus();
    	CheckboxTreeViewer viewer = getTreeViewer();
		Object[] selected = viewer.getCheckedElements();
		for(int i=0;i<selected.length;i++) {
	      Object o = selected[i];
		  if(viewer.getGrayed(o))
			viewer.setChecked(o,false);
		}
    }
	
	/**
	 * Disable nodes.
	 *
	 * @param nodes the nodes
	 */
	public void disableNodes(Vector nodes) {
		for(int i=0;i<nodes.size();i++) {
			TreeElement te = (TreeElement)nodes.elementAt(i);
			getTreeViewer().setGrayed(te,true);
		}
		CheckStateListener csl = new CheckStateListener(nodes);
		getTreeViewer().addCheckStateListener(csl);
	}
	
	/**
	 * Expand tree.
	 *
	 * @param nodes the nodes
	 */
	public void expandTree(Vector nodes) {
		for(int i=0;i<nodes.size();i++) {
		   TreeElement te = (TreeElement)nodes.elementAt(i);
		   getTreeViewer().setExpandedState(te,true);
		}
	}
	
	/**
	 * Select nodes.
	 *
	 * @param nodes the nodes
	 */
	public void selectNodes(Vector nodes) {
		for(int i=0;i<nodes.size();i++) {
		  TreeElement te = (TreeElement)nodes.elementAt(i);
		  getTreeViewer().setChecked(te,true);
		}
	}
}

