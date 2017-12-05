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
	private Vector<TreeElement> children = new Vector<TreeElement>();
	
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
	
	public TreeElement find(String name){
		if(name.equals(this.name)){
			return this;
		}else{
			for(TreeElement te:this.children){
				TreeElement result = te.find(name);
				if (result != null){
					return result;
				}
			}
		}
		return null;
	}
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public TreeElement[] getChildren() {
		TreeElement[] te = new TreeElement[0];
		te = children.toArray(te);
		return te;
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
	public void getPath(Vector<String> path) {
	  path.addElement(toString());
	  if(owner.owner != null)
	    owner.getPath(path);
	}
}
