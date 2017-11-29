package tool.doc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class MyTree extends JTree implements MouseListener, TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	
	@Override
	public MyTreeModel getModel() {
		return (MyTreeModel) super.getModel();
	}
	
	public MyTree() {
		addMouseListener(this);
		addTreeSelectionListener(this);
		setCellRenderer(new MyTreeCellRenderer());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		TreePath path = getSelectionPath();
		if(path == null) return;
		Object selected = path.getLastPathComponent();
		final MyTreeNode node = (MyTreeNode) selected;
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			if(node != null) {
				if(node instanceof MyTreeNode) {
					JMenuItem addMenu = new JMenuItem("Add Node");
					addMenu.addActionListener(new ActionListener() {			
						@Override public void actionPerformed(ActionEvent e) {getModel().actionAdd(node);}
					});
					menu.add(addMenu);
//					JMenuItem addRRMenu = new JMenuItem("Add Requirements");
//					addRRMenu.addActionListener(new ActionListener() {			
//						@Override public void actionPerformed(ActionEvent e) {getModel().actionAddRequirements(node);}
//					});
//					menu.add(addRRMenu);
//					JMenuItem addTTMenu = new JMenuItem("Add Tests");
//					addTTMenu.addActionListener(new ActionListener() {			
//						@Override public void actionPerformed(ActionEvent e) {getModel().actionAddTests(node);}
//					});
//					menu.add(addTTMenu);
//				}
//				if(node instanceof RequirementsNode) {
					JMenuItem addRMenu = new JMenuItem("(Add Requirement)");
					addRMenu.addActionListener(new ActionListener() {			
						@Override public void actionPerformed(ActionEvent e) {getModel().actionAddRequirement(node);}
					});
					menu.add(addRMenu);
//				}
//				if(node instanceof TestsNode) {
					JMenuItem addTMenu = new JMenuItem("Add Test");
					addTMenu.addActionListener(new ActionListener() {			
						@Override public void actionPerformed(ActionEvent e) {getModel().actionAddTest(node);}
					});
					menu.add(addTMenu);
				}
				
				menu.add(new JSeparator());
				
				JMenuItem renameMenu = new JMenuItem("Rename");
				renameMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionRename(node);}
				});
				
				JMenuItem copyMenu = new JMenuItem("Copy");
				copyMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionCopy(node);}
				});
				JMenuItem cutMenu = new JMenuItem("Cut");
				cutMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionCut(node);}
				});
				JMenuItem pasteMenu = new JMenuItem("Paste");
				pasteMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionPaste(node);}
				});
				JMenuItem deleteMenu = new JMenuItem("Delete");
				deleteMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionDelete(node);}
				});
				JMenuItem upMenu = new JMenuItem("(Up)");
				upMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionUp(node);}
				});
				JMenuItem downMenu = new JMenuItem("(Down)");
				downMenu.addActionListener(new ActionListener() {			
					@Override public void actionPerformed(ActionEvent e) {getModel().actionDown(node);}
				});

				menu.add(renameMenu);
				menu.add(copyMenu);
				menu.add(cutMenu);
				menu.add(pasteMenu);
				menu.add(deleteMenu);
				menu.add(new JSeparator());
				menu.add(upMenu);
				menu.add(downMenu);
				
				menu.show(this, e.getX(), e.getY());
			}
		}
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}

	transient MyTreeNode lastSelected = null;
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(lastSelected != null) {
			getModel().storeValues(lastSelected);
		}
		
		TreePath path = getSelectionPath();
		if(path == null) return;
		Object selected = path.getLastPathComponent();
		final MyTreeNode node = (MyTreeNode) selected;
		lastSelected = node;
		
		getModel().showNodePanel(node);
		

	}

	public void save() {
		getModel().storeValues(lastSelected);
		getModel().save();
	}

	public void load() {
		getModel().load();
	}

}
