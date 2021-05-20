package tool.clients.fmmlxdiagrams;

import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class FaXML {
	
	private final String name;	
	private final Vector<FaXML> children = new Vector<>();
	private final HashMap<String, String> attributes = new HashMap<>();
	
	public FaXML(Vector<Object> content) {
		System.err.println("reading " + content);
		name = content.get(0).toString();
		if (name.contains("addOperation2") || name.contains("changeOperationBody")){
			for(int i = 1; i < content.size(); i++) {
				if(! (content.get(i) instanceof Vector<?>)) throw new RuntimeException("FaXML message invalid.");
				@SuppressWarnings("unchecked")
				Vector<Object> child = (Vector<Object>) (content.get(i));
				if(child.size() == 2 && child.get(0) instanceof String && child.get(1) instanceof String) {
					if(child.get(0).equals("body")){
						String body = transformBase64((String)child.get(1));
						attributes.put((String)child.get(0),body);
					} else {
						attributes.put((String)child.get(0),(String)child.get(1));
					}
				} else {
					children.add(new FaXML(child));
				}
			}
		} else {
			for(int i = 1; i < content.size(); i++) {
				if(! (content.get(i) instanceof Vector<?>)) throw new RuntimeException("FaXML message invalid.");
				@SuppressWarnings("unchecked")
				Vector<Object> child = (Vector<Object>) (content.get(i));
				if(child.size() == 2 && child.get(0) instanceof String && child.get(1) instanceof String) {
					attributes.put((String)child.get(0),(String)child.get(1));
				} else {
					children.add(new FaXML(child));
				}
			}
		}

	}

	private String transformBase64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	public String getName() { return name; }
	public Vector<FaXML> getChildren() { return new Vector<>(children); }
	public Set<String> getAttributes() { return attributes.keySet(); }
	public String getAttributeValue(String attributeName) { return attributes.get(attributeName); }
	
	@Override public String toString() { return toString(""); }
	
	public String toString(String prefix) {
		StringBuilder attString = new StringBuilder();
		for(String key : getAttributes()) attString.append(" ").append(key).append("=\"").append(getAttributeValue(key)).append("\"");

		StringBuilder childrenString = new StringBuilder();
		for(FaXML child : children) childrenString.append(child.toString(prefix + "  "));
		return prefix + "<" + name + attString + (children.size()>0?(">\n"+childrenString+"</" + name):"/")  + ">\n";
	}
}
