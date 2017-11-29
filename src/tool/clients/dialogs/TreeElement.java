package tool.clients.dialogs;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeElement.
 */
public class TreeElement {
  
	/** The name. */
	private String name;
	
	/** The children. */
	private Vector children = new Vector();
	
	/** The owner. */
	private TreeElement owner;
	
	/**
	 * Instantiates a new tree element.
	 *
	 * @param owner the owner
	 * @param name the name
	 */
	public TreeElement(TreeElement owner,String name) {
		this.owner = owner;
		this.name = name;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param child the child
	 */
	public void addChild(TreeElement child) {
		children.addElement(child);
	}
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public Object[] getChildren() {
		Object[] o = children.toArray();
		return o;
	}
	
	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public TreeElement getOwner() {
		return owner;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Gets the path.
	 *
	 * @param path the path
	 * @return the path
	 */
	public void getPath(Vector path) {
	  path.addElement(toString());
	  if(owner.owner != null)
	    owner.getPath(path);
	}
}
