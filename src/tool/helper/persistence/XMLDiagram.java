package tool.helper.persistence;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

public class XMLDiagram {

	int id;
	Element diagramElement;
	
	
	Vector<Vector<Object>> views;

	public XMLDiagram(int id) {
		this.id = id; 
		Document document = XMLUtil.createDocument("Diagram");
		diagramElement = document.getDocumentElement();
		diagramElement.setAttribute("name", FmmlxDiagramCommunicator.getDiagram(id).diagramName);
		
		appendDiagramPreferences();
		appendObjects();
		
		
		views = FmmlxDiagramCommunicator.getCommunicator().getAllViews(id);
	}

	private void appendObjects() {
		Element objectsElement = XMLUtil.createChildElement(diagramElement, "Objects");
		HashMap<String, HashMap<String, Object>> objects;
		objects = FmmlxDiagramCommunicator.getCommunicator().getAllObjectPositions(id);
		for (Entry entry : objects.entrySet()) {
			XMLObject object = new XMLObject((String)entry.getKey(), (HashMap<String, Object>)entry.getValue());
			appendObject(objectsElement, object);
		}
	}

	private void appendObject(Element objectsElement, XMLObject object) {
		Element objectElement = XMLUtil.createChildElement(objectsElement, "Object");
		objectElement.setAttribute("Path", object.getPath());
		objectElement.setAttribute("visible", object.getVisible().toString());
		objectElement.setAttribute("x", object.getX());
		objectElement.setAttribute("y", object.getY());
	}

	private void appendDiagramPreferences() {
		Element preferencesElement = XMLUtil.createChildElement(diagramElement, "Preferences");
		appendDiagramViewToolBarProperties(preferencesElement);
		
		}

	private void appendDiagramViewToolBarProperties(Element preferencesElement) {
		Element diagramViewToolBarPropertiesElement = XMLUtil.createChildElement(preferencesElement, "DiagramViewToolBarProperties");
		HashMap<String, Boolean> diagramViewToolBarPropertiesMap;
		diagramViewToolBarPropertiesMap = FmmlxDiagramCommunicator.getCommunicator().getDiagramDisplayProperties(id);
		for (Entry entry : diagramViewToolBarPropertiesMap.entrySet()) {
			diagramViewToolBarPropertiesElement.setAttribute(entry.getKey().toString(), entry.getValue().toString());
		}
	}


	public Vector<Vector<Object>> getViews() {
		return views;
	}

	

	public Element getDiagramElement() {
		return diagramElement;
	}



	class XMLObject {
		String path;
		String x;
		String y;
		Boolean visible;

		public XMLObject(String path, HashMap<String, Object> XMFRepresentation) {
			this.path = path;
			x = String.valueOf(XMFRepresentation.get("x"));
			y = String.valueOf(XMFRepresentation.get("y"));
			visible = Boolean.valueOf((XMFRepresentation.get("y")).toString());
		}

		public String getPath() {
			return path;
		}

		public String getX() {
			return x;
		}

		public String getY() {
			return y;
		}

		public Boolean getVisible() {
			return visible;
		}
		
		
	}


}
