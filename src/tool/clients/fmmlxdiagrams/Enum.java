package tool.clients.fmmlxdiagrams;

import java.util.Vector;

public class Enum {

	private String name;
	private Vector<EnumElement> elements;
	
	public Enum(String name) {
		super();
		this.name = name;
		this.elements = new Vector<EnumElement>();
	}

	public Enum(String name, Vector<EnumElement> elements) {
		super();
		this.name = name;
		this.elements = elements;
	}

	private void addElement(EnumElement element) {
		if(elements!=null) {
			elements.add(element);
		}
	}
	
	private void removeElement(String elementName) {
		if(elements!= null) {
			for(EnumElement tmp : elements) {
				if(tmp.getName().equals(elementName)) {
					elements.remove(tmp);
				}
			}
		}
	}
	
	private EnumElement getElement(String name) {
		if(elements!= null) {
			for(EnumElement tmp : elements) {
				if(tmp.getName().equals(name)) {
					return tmp;
				}
			}
		}
		return null;
	}
	
	private void editElement(String elementName, String newElementName) {
		if(elements!= null) {
			EnumElement tmp = getElement(elementName);
			if(tmp!=null) {
				tmp.setName(newElementName);
			}
		}
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Vector<EnumElement> getElements() {
		return elements;
	}


	public void setElements(Vector<EnumElement> elements) {
		this.elements = elements;
	}
	
	
	
}
