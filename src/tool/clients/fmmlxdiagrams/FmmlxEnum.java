package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;

public class FmmlxEnum {

	private final String name;
	private final Vector<String> elements;
	
//	public FmmlxEnum(String name) {
//		super();
//		this.name = name;
//		this.elements = new Vector<String>();
//	}

	public FmmlxEnum(String name, Vector<String> elements) {
		super();
		this.name = name;
		this.elements = elements;
		Collections.sort(this.elements);
	}

//	private void addElement(EnumElement element) {
//		if(elements!=null) {
//			elements.add(element);
//		}
//	}
//	
//	private void removeElement(String elementName) {
//		if(elements!= null) {
//			for(EnumElement tmp : elements) {
//				if(tmp.getName().equals(elementName)) {
//					elements.remove(tmp);
//				}
//			}
//		}
//	}
//	
//	private EnumElement getElement(String name) {
//		if(elements!= null) {
//			for(EnumElement tmp : elements) {
//				if(tmp.getName().equals(name)) {
//					return tmp;
//				}
//			}
//		}
//		return null;
//	}
//	
//	private void editElement(String elementName, String newElementName) {
//		if(elements!= null) {
//			EnumElement tmp = getElement(elementName);
//			if(tmp!=null) {
//				tmp.setName(newElementName);
//			}
//		}
//	}

	public String getName() {
		return name;
	}


//	public void setName(String name) {
//		this.name = name;
//	}


	public Vector<String> getElements() {
		return elements;
	}
//
//	public void setElements(Vector<EnumElement> elements) {
//		this.elements = elements;
//	}
	
	
	
}
