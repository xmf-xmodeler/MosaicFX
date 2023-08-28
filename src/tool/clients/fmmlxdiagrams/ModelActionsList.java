package tool.clients.fmmlxdiagrams;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class ModelActionsList implements Comparable<ModelActionsList>{
	
	private final String name;	
	private final Vector<ModelActionsList> children = new Vector<>();
	private final HashMap<String, String> attributes = new HashMap<>();
	
	public ModelActionsList(Vector<Object> content) {
//		System.err.println("reading " + content);
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
					children.add(new ModelActionsList(child));
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
					if(child.size() == 2) {
						System.err.println("wrong type: " + child.get(0) + "/" +child.get(1).getClass().getCanonicalName());
					}
					children.add(new ModelActionsList(child));
				}
			}
		}
	}

	private String transformBase64(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	public String getName() { return name; }
	public Vector<ModelActionsList> getChildren() { return new Vector<>(children); }
	public Set<String> getAttributes() { return attributes.keySet(); }
	public String getAttributeValue(String attributeName) { return attributes.get(attributeName); }
	
	@Override public String toString() { return toString(""); }
	
	public String toString(String prefix) {
		StringBuilder attString = new StringBuilder();
		for(String key : getAttributes()) attString.append(" ").append(key).append("=\"").append(getAttributeValue(key)).append("\"");

		StringBuilder childrenString = new StringBuilder();
		for(ModelActionsList child : children) childrenString.append(child.toString(prefix + "  "));
		return prefix + "<" + name + attString + (children.size()>0?(">\n"+childrenString+"</" + name):"/")  + ">\n";
	}
	
	@Override
	public int compareTo(ModelActionsList that) {
		Integer prio = this.priority().compareTo(that.priority());
		if(prio != 0) return prio;
		
		if("addMetaClass".equals(name) || "addInstance".equals(name)) {
			return 0; // Don't mess up with the original order !!!
		}
		
		Vector<String> keys = new Vector<>(attributes.keySet());
		Collections.sort(keys);
		for(String key : keys) {
			if(that.attributes.get(key) == null) return -1;
			Integer c = this.attributes.get(key).compareTo(that.attributes.get(key));
			if(c != 0) return c;
		}
		return 0;
	}
	
	private Integer priority() {
		if("addMetaClass".equals(name))         return 0000;
		if("addInstance".equals(name))          return 0010;
		if("changeParent".equals(name))         return 0020;
		if("addEnumeration".equals(name))       return 0030;
		if("addEnumerationValue".equals(name))  return 0040;
		if("addAttribute".equals(name))         return 0050;
		if("addOperation".equals(name))         return 0060;
		if("addConstraint".equals(name))        return 0070;
		if("changeSlotValue".equals(name))      return 0100;
		if("addAssociation".equals(name))       return 0110;
		if("addLink".equals(name))              return 0120;
		if("addDelegation".equals(name))        return 0130;
		if("setRoleFiller".equals(name))        return 0140;
		return Integer.MAX_VALUE;
	}
}