package tool.clients.dialogs;

import java.util.Vector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeDialog.
 */
class TreeDialog extends ElementTreeSelectionDialog {
	
	/**
	 * Instantiates a new tree dialog.
	 *
	 * @param shell the shell
	 * @param labelProvider the label provider
	 * @param content the content
	 */
	public TreeDialog(Shell shell,ILabelProvider labelProvider,ITreeContentProvider content) {
		super(shell,labelProvider,content);
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
}

