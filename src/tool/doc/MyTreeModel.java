package tool.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MyTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;
	private DocFrame frame;
	
	private transient DefaultMutableTreeNode clipboard;
	
	public MyTreeModel(DocFrame parent) {
		super(new MyTreeNode("Root"));
		this.frame = parent;
		System.err.println("loading file");
		load();
		System.err.println("file loaded");
	}

	public void actionRename(MyTreeNode node) {
		String name = JOptionPane.showInputDialog(frame, "New name:", node);
		if(name != null) node.setName(name);
		nodeChanged(node);
	}

	public void actionAdd(DefaultMutableTreeNode parent) {
		String name = JOptionPane.showInputDialog(frame, "New node:", "new node");
		if(name != null) {
			MyTreeNode child = new MyTreeNode(name);
			insertNodeInto(child, parent, parent.getChildCount());
		}
	}

	public void actionAddRequirements(DefaultMutableTreeNode parent) {
		RequirementsNode child = new RequirementsNode("Requirements");
		insertNodeInto(child, parent, parent.getChildCount());
	}

	public void actionAddTests(DefaultMutableTreeNode parent) {
		TestsNode child = new TestsNode("Tests");
		insertNodeInto(child, parent, parent.getChildCount());
	}

	public void actionAddRequirement(DefaultMutableTreeNode node) {
		// TODO Auto-generated method stub
		
	}

	public void actionAddTest(DefaultMutableTreeNode parent) {
		String name = JOptionPane.showInputDialog(frame, "New Test:", "new Test");
		TestNode child = new TestNode(name);
		insertNodeInto(child, parent, parent.getChildCount());
	}

	public void save() {
		try {
			File file = new File("doc/XDoc/mainDoc.xdoc");
			PrintStream out;
			out = new PrintStream(file, "UTF-8");
			out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><XModelerDocumentation>");
			writeTree(out, (MyTreeNode) getRoot());
			out.print("</XModelerDocumentation>");
			out.close();
		} catch (FileNotFoundException e) {e.printStackTrace();
		} catch (UnsupportedEncodingException e) {e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void writeTree(PrintStream out, MyTreeNode node) {
		out.print("<Node");
		out.print(" name = \""+node.toString()+"\"");	
		out.print(" type = \""+node.getType()+"\"");
		node.save(out);
		if(node.getChildCount() > 0) {
			out.print(">");
			for(Object o : Collections.list(node.children())) {
				writeTree(out, (MyTreeNode) o);
			}
			out.print("</Node>");
		} else {
			out.print("/>");
		}
		
	}

	public void load() {

		try {
			File fXmlFile = new File("doc/XDoc/mainDoc.xdoc");
			if (fXmlFile.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				String rootNodeName = doc.getDocumentElement().getNodeName();
				Node node = doc.getDocumentElement();
				if (rootNodeName.equals("XModelerDocumentation")) {
					MyTreeNode root = loadTree(node.getFirstChild());
					setRoot(root);
				}
			}
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (SAXException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace();
		}

	}

	private MyTreeNode loadTree(Node node) {
		String name = node.getAttributes().getNamedItem("name").getNodeValue();
		String type = node.getAttributes().getNamedItem("type").getNodeValue();
		
		MyTreeNode treeNode;
		
		if("test".equals(type)) {
			treeNode = new TestNode(node);
		} else {
			treeNode = new MyTreeNode(name);
		}
		 
		
		NodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			MyTreeNode childTreeNode = loadTree(child);
			treeNode.add(childTreeNode);
		}
		return treeNode;
	}

	public void actionCopy(DefaultMutableTreeNode node) {
		clipboard = node;		
	}

	public void actionCut(DefaultMutableTreeNode node) {
		actionCopy(node);
		actionDelete(node);
	}

	public void actionPaste(DefaultMutableTreeNode parent) {
		MyTreeNode newNode = copyTree((MyTreeNode) clipboard);
		insertNodeInto(newNode, parent, parent.getChildCount());
//		parent.add(newNode);		
	}

	private MyTreeNode copyTree(MyTreeNode original) {
		MyTreeNode newNode = new MyTreeNode(original.getUserObject()+"");
		for(int i = 0; i < original.getChildCount(); i++) {
			MyTreeNode child = (MyTreeNode) original.getChildAt(i);
			newNode.add(copyTree(child));
		}
		return newNode;
	}

	public void actionDelete(DefaultMutableTreeNode node) {
//		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
//		parent.remove(node);
		removeNodeFromParent(node);
	}

	public void actionUp(DefaultMutableTreeNode node) {
		// TODO Auto-generated method stub
		
	}

	public void actionDown(DefaultMutableTreeNode node) {
		// TODO Auto-generated method stub
		
	}

	public void showNodePanel(MyTreeNode node) {
		JPanel p = node.createPanel();
		frame.setEditPanel(p);
	}

	public void storeValues(MyTreeNode node) {
		if(node != null) node.storeValues();
	}

}
