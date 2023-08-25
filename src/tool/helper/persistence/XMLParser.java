package tool.helper.persistence;

import java.text.ParseException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.helper.auxilaryFX.JavaFxAlertAuxilary;
import tool.helper.persistence.modelActionParser.ModelActionParser;
import tool.xmodeler.ControlCenterClient;

public class XMLParser {
	FmmlxDiagramCommunicator communicator = FmmlxDiagramCommunicator.getCommunicator();
	Document doc;
	Element root;
	String projectPath;
	String projectName;

	public XMLParser(String filePath) {
		doc = XMLUtil.getDocumentFromFile(filePath);
		root = doc.getDocumentElement();
	}

	public void parseXMLDocument() {
		createProject();
		buildModel();
		Element diagrams = XMLUtil.getChildElement(root, XMLTags.DIAGRAMS.getName());
		NodeList diagramList = diagrams.getChildNodes();

		for (int i = 0; i < diagramList.getLength() - 1; i++) {
			Node node = diagramList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(XMLTags.DIAGRAM.getName()) ) {
				buildDiagram((Element)node);
			}
		}
		ControlCenterClient.getClient().getAllProjects();
	}

	private void buildDiagram(Element diagram) {
		String diagramName = diagram.getAttribute(XMLAttributes.NAME.getName());
		// TODO is the file used somewhere?
		communicator.createFmmlxClassDiagram(projectPath, diagramName, "random", diagramId -> {
			sendDiagramDataToXMF(diagramId, diagram);		
		});
	}
	
	private void sendDiagramDataToXMF(Integer diagramId, Element diagram) {
		Element views = XMLUtil.getChildElement(diagram, XMLTags.VIEWS.getName());
		sendDiagramViewStatus(diagramId, views);
		Element diagramsDisplayProperty = XMLUtil.getChildElement(diagram, XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		sendDiagramDisplayproperties(diagramId, diagramsDisplayProperty);
		Element instances = XMLUtil.getChildElement(diagram, XMLTags.INSTANCES.getName());
		NodeList instancesList = instances.getChildNodes();
		for (int i = 0; i < instancesList.getLength(); i++) {
			Node node = instancesList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sendObjectInformation(diagramId, (Element) node);
			}
		}
	}

	private void sendObjectInformation(Integer diagramId, Element object) {
         int x = Integer.valueOf(object.getAttribute(XMLAttributes.X_COORDINATE.getName()));
         int y = Integer.valueOf(object.getAttribute(XMLAttributes.Y_COORDINATE.getName()));
         boolean hidden = Boolean.valueOf(object.getAttribute(XMLAttributes.HIDDEN.getName()));
         String ref = object.getAttribute(XMLAttributes.PATH.getName());
         XMLInstanceStub stub = new XMLInstanceStub(ref, hidden, x, y);
         communicator.sendObjectInformation(diagramId, stub);
	}

	private void sendDiagramDisplayproperties(Integer diagramId, Element diagramDisplayProperties) {
		NodeList properties = diagramDisplayProperties.getChildNodes();
		HashMap<String, Boolean> propertiesMap = new HashMap<>();

		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			if (property.getNodeType() == Node.ELEMENT_NODE) {
				propertiesMap.put(property.getNodeName(), Boolean.valueOf(property.getTextContent()));
			}
		}
		communicator.sendDiagramDisplayOptions(diagramId, propertiesMap);
	}

	private void sendDiagramViewStatus(Integer diagramID, Element viewsElement) {
		
		SortedMap<String, Affine> views = new TreeMap<String, Affine>();
		NodeList viewElements =  viewsElement.getChildNodes();
		for (int i = 0; i < viewElements.getLength(); i++) {
			if (viewElements.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
				
			Element view = (Element) (viewElements.item(i));
			String viewName = view.getAttribute(XMLAttributes.NAME.getName());
			//Set default values
			double xx = 1.0; 
			double tx = 0.0; 
			double ty = 0.0;		
			try {
				xx = Double.parseDouble(view.getAttribute("xx"));
			} catch (Exception e) {
				System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");
			}
			try {
				tx = Double.parseDouble(view.getAttribute("tx"));
			} catch (Exception e) {
				System.err.println("Cannot read tx: " + e.getMessage() + " Using default instead");
			}
			try {
				ty = Double.parseDouble(view.getAttribute("ty"));
			} catch (Exception e) {
				System.err.println("Cannot read ty: " + e.getMessage() + " Using default instead");
			}
			views.put(viewName, new Affine(xx, 0, tx, 0, xx, ty));
		}
		communicator.sendViewStatus(diagramID, views);
	}

	private void buildModel() {
		// Creates dummy project that hold the model data. Current assumption is, that
		// there is only one model per project
		communicator.createFmmlxModelBrowser(projectName, "Deserializer", projectPath,
				// if diagram id is returned from XMF the function inside is executed
				diagramId -> {
					sendModelDataToXMF(diagramId);
				});
	}
	
	private void sendModelDataToXMF(Integer diagramId) {
		Element model = XMLUtil.getChildElement(root, XMLTags.MODEL.getName());
		NodeList logList = model.getChildNodes();
		for (int i = 0; i < logList.getLength() - 1; i++) {
			if (logList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element modelElement = (Element) logList.item(i);
				// Every request is added to a sorted list on XMF side. Because the request are processed in a ordered way it is ensured, that the first operations are finished before the next one it started. So there should be no error because one Object is waiting for another to be created.
				parseModelElements(diagramId, modelElement);
				//TODO when the objects are created the updated position could be send
			}
		}
	}

	private void parseModelElements(Integer diagramId, Element modelElement) {
		String modelActionName = modelElement.getTagName();
		ModelActionParser modelActionParser = null;
		try {
			modelActionParser = ModelActionParserFactory.create(diagramId, modelActionName);
			modelActionParser.parse(modelElement);
		} catch (ParseException e1) {
			Platform.runLater(()->{
				String headerText = "XML corrupted";
				String contentText = "The XML contains unkown model actions. Please provide valid XML.";
				JavaFxAlertAuxilary unkownModelActionsAlert = new JavaFxAlertAuxilary(AlertType.WARNING, headerText, contentText);
				unkownModelActionsAlert.show();
			});			
		}
	}

	private void createProject() {
		String version = root.getAttribute(XMLAttributes.VERSION.getName());
		projectPath = root.getAttribute(XMLAttributes.PATH.getName());
		projectName = projectPath.split("::")[1];
		communicator.createProject(projectName, projectPath);
	}
}