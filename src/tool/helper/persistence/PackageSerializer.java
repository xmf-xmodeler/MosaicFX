package tool.helper.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.PackageActionsList;
import tool.clients.fmmlxdiagrams.TimeOutException;

public class PackageSerializer {
	//hard coded version number
	static final String version = "4";
	FmmlxDiagramCommunicator communicator;
	Document outputDoc;
	
	//Use here Abstract? Von wo will ich die Funktion callen? Wenn ich eh immer von Diagram frage... Diagram Export, dann kann ich es mir leichter machen und FmmlXDiagram nehmen
	AbstractPackageViewer diagram;
		
	public PackageSerializer(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		communicator = diagram.getComm();
	}
	
	
	//Creates a XML-Document with the needes structure and calls other methos to fill the elements
	public void createPackageXML() {
		
		
		outputDoc = XMLUtil.createDocument("XModelerPackage");
		Element xModelerPackageElm = outputDoc.getDocumentElement();
		xModelerPackageElm.setAttribute("path", diagram.getPackagePath());
		xModelerPackageElm.setAttribute("version", version);
		
		//implemented for future use
		Element categoriesElm = XMLUtil.createChildElement(xModelerPackageElm, "Categories");
		//implemented for future use
		Element referencesElm = XMLUtil.createChildElement(xModelerPackageElm, "References");

		Element packageActionElm = XMLUtil.createChildElement(xModelerPackageElm, "PackageActions");
		addPackageActions(packageActionElm);
		
		Element diagramsElm = XMLUtil.createChildElement(xModelerPackageElm, "Diagrams");
		addDiagrams(diagramsElm);
		
		String test = XMLUtil.getStringFromDocument(outputDoc);
		System.err.println(test);
	}



	private void addPackageActions(Element packageContentElm) {
		PackageActionsList actionsList = null;
		try {
			//DiagramIds are only counted up. Because a diagram can only be build on the model in the package every diagram id will lead to the same result.
			//TODO: Implement getPackage Info -> ask the package for the model, that is related to the package (one package only has one model) 
			actionsList = communicator.getDiagramData(0);
		} catch (TimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vector<PackageActionsList> logs = actionsList.getChildren();
		Collections.sort(logs);
		for (PackageActionsList log : logs) {
			Element logElement = XMLUtil.createChildElement(packageContentElm, log.getName());
			for (String attName : log.getAttributes()) {
				logElement.setAttribute(attName, log.getAttributeValue(attName));
			}
		}
	}

	private void addDiagrams(Element diagramsElement) {
		List<Integer> diagramIds = communicator.getAllDiagramIDs(diagram.getPackagePath()); 
		for (Integer id : diagramIds) {
			XMLDiagram xmlDiagram = new XMLDiagram(id);			
			Element diagramElement = xmlDiagram.getDiagramElement();
			outputDoc.adoptNode(diagramElement);
			diagramsElement.appendChild(diagramElement);
			
			
			
		
			
//			
//			
//			Element viewsElement = XMLUtil.createChildElement(diagramsElement, "Views");
//			
//			
//			
//			
//			XMLDiagram xmlDiagram = new XMLDiagram(id);
//			
//			for(Vector<Object> viewVec : xmlDiagram.getViews()) {    		
//	    		Element viewElement = XMLUtil.createChildElement(diagramElement, "View");
//	    		viewElement.setAttribute("name", ""+viewVec.get(0));
//	    		viewElement.setAttribute("xx", ""+viewVec.get(1));
//	    		viewElement.setAttribute("tx", ""+viewVec.get(2));
//	    		viewElement.setAttribute("ty", ""+viewVec.get(3));
//	    	}
//			
//			for (Integer integer : diagramIds) {
//				
//			}
			
		}
//		
//		
//    	
//            saveComponentsIntoDiagramElement(diagramElement, diagramPath, id);
//            xmlManager.addDiagramIntoDiagramsElement(diagramsElement, diagramElement);
//            diagramElement.appendChild(xmlManager.createXmlElement(SerializerConstant.TAG_NAME_DIAGRAM_DISPLAY_PROPERTIES));
//            serilizeDiagramDisplayProperties(id);
    	}	
	
}