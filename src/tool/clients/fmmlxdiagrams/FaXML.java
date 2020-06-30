package tool.clients.fmmlxdiagrams;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class FaXML {
	
	private final String name;	
	private final Vector<FaXML> children = new Vector<FaXML>();
	private final HashMap<String, String> attributes = new HashMap<String, String>();
	
	public FaXML(Vector<Object> content) {
		System.err.println("reading " + content);
		name = (String) content.get(0);
		for(int i = 1; i < content.size(); i++) {
			if(! (content.get(i) instanceof Vector<?>)) throw new RuntimeException("FaXML message invalid.");
			@SuppressWarnings("unchecked")
			Vector<Object> child = (Vector<Object>) (content.get(i));
//			if(child.size() >= 1) {
//				if(! (child.get(0) instanceof String)) throw new RuntimeException("FaXML message invalid.");
			if(child.size() == 2 && child.get(0) instanceof String && child.get(1) instanceof String) {
				attributes.put((String)child.get(0),(String)child.get(1));
			} else {
				children.add(new FaXML(child));
			}
//			}
		}
	}
	
	public String getName() { return name; }
	public Vector<FaXML> getChildren() { return new Vector<FaXML>(children); }
	public Set<String> getAttributes() { return attributes.keySet(); }
	public String getAttributeValue(String attributeName) { return attributes.get(attributeName); }
	
	@Override public String toString() { return toString(""); }
	
	public String toString(String prefix) {
		String attString = "";
		for(String key : getAttributes()) attString += " " + key + "=\"" + getAttributeValue(key) + "\"";
		String childrenString = "";
		for(FaXML child : children) childrenString += child.toString(prefix + "  ");
		return prefix + "<" + name + attString + (children.size()>0?(">\n"+childrenString+"</" + name):"/")  + ">\n";
	}
}
