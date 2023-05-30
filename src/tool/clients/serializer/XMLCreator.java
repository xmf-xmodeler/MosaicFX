package tool.clients.serializer;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.PackageActionsList;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator.DiagramInfo;

public class XMLCreator  {
	
	FmmlxDiagramCommunicator comm;
	Document doc;
	Element root; 
	Vector<DiagramInfo> diagramToDoList;
	
	
	public XMLCreator() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		comm = FmmlxDiagramCommunicator.getCommunicator();
		root = doc.createElement(SerializerConstant.TAG_NAME_ROOT);
	    doc.appendChild(root);
		
	}
	
	
	public void createXMLRepresentation(String packagePath, ReturnCall<Document> onDocumentCreated){
		
		getData(packagePath, o-> System.err.println("fertig:" + o));
		
		
		
		
	}

	private void getData(String packagePath, ReturnCall<Object> onDataReceived) {
		
		ReturnCall<Vector<DiagramInfo>> onDiagramInfosReceived = diagramInfo -> {
			getModelData(diagramInfo, packagePath, onDataReceived);	
		};
		comm.getAllDiagramInfos(packagePath, onDiagramInfosReceived );
		
		
	}

	private void getModelData(Vector<DiagramInfo> diagramInfos, String packagePath, ReturnCall<Object> onDataReceived) {

		ReturnCall<PackageActionsList> onModelDataReceived = packageContent -> {
			Vector<PackageActionsList> logs = packageContent.getChildren();
			Collections.sort(logs);
			Element logsElement = doc.createElement(SerializerConstant.TAG_NAME_LOGS);
			root.appendChild(logsElement);
			for (PackageActionsList log : logs) {
				Element newLogElement = doc.createElement(log.getName());
				for (String attName : log.getAttributes()) {
					newLogElement.setAttribute(attName, log.getAttributeValue(attName));
				}
				logsElement.appendChild(newLogElement);
			};
			
			getDiagramsData(diagramInfos, packagePath, onDataReceived);
			printDoc();
		};
		
		comm.createDiagram(packagePath, "Serializer", "", 
	   			FmmlxDiagramCommunicator.DiagramType.ModelBrowser, false, 
	   			diagramId -> {
	   				
	   				comm.getModelData(diagramId, onModelDataReceived);
	   			});
		
		
		
		//		
//		ReturnCall<PackageActionsList> onDiagramDataReceived
//		
//		getModelData(Integer diagramID, ReturnCall<PackageActionsList> onDiagramDataReceived )
	}


	private void getDiagramsData(Vector<DiagramInfo> diagramInfos, String packagePath,
			ReturnCall<Object> onDataReceived) {
		
		diagramToDoList = new Vector<>(diagramInfos);
		resolveToDoList(onDataReceived);
		
		
		
	}


	private void resolveToDoList(ReturnCall<Object> onDataReceived) {
		
		
		if (diagramToDoList.isEmpty()) {
			onDataReceived.run("Resolve ToDoList");
		} else {
			DiagramInfo diagramInfo = diagramToDoList.remove(0);
			
		//	ReturnCall<EdgeReturnType>
		//	ReturnCall<Label>	
		//	ReturnCall<View>
			
			//Object calls view, view calls label, label calls edge, edge calls himself (resolv to to list
			
			ReturnCall<HashMap<String, HashMap<String, Object>>> onAllObjectPositionsReceived = objectPositions -> {
				//TODO Positions to XML
				resolveToDoList(onDataReceived);
			};
			comm.getAllObjectPositions(diagramInfo.id, onAllObjectPositionsReceived );
		}
		
	}


	private void printDoc() throws TransformerFactoryConfigurationError {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter sw = new StringWriter();
		try {
			trans.transform(new DOMSource(doc), new StreamResult(sw));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		System.err.println(sw.toString());
	}
	
	

}
