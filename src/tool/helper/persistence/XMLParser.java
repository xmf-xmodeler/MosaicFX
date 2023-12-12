package tool.helper.persistence;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator.DiagramType;
import tool.helper.auxilaryFX.JavaFxAlertAuxilary;
import tool.helper.persistence.modelActionParser.ModelActionParser;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;
import tool.xmodeler.ControlCenterClient;

public class XMLParser {
	private FmmlxDiagramCommunicator communicator = FmmlxDiagramCommunicator.getCommunicator();
	private Element root;
	private String projectPath;
	private static final boolean DEBUG = false;


	public XMLParser() {
		this(getInputFile());
	}
	
	private static File getInputFile() {
		// init chooser
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose input location");
		chooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		if (PropertyManager.getProperty(UserProperty.RECENTLY_LOADED_MODEL_DIR.toString()) != null) {
			String recentlyOpendDirPath = PropertyManager.getProperty(UserProperty.RECENTLY_LOADED_MODEL_DIR.toString());
			File recentlyOpendFile = new File(recentlyOpendDirPath);
			chooser.setInitialDirectory(recentlyOpendFile.getParentFile());
		}
		// choose
		Optional<File> inputFile = Optional.of(chooser.showOpenDialog(new Stage()));
		// TODO TS Add InputOutputLogger
		// ErrorLoggin in the case the dialog is escaped
		if (inputFile == null) {
			System.err.println("XML Export was interrupted");
			throw new NullPointerException("No file was selected by user");
		}
		PropertyManager.setProperty(UserProperty.RECENTLY_LOADED_MODEL_DIR.toString(),inputFile.get().getAbsolutePath());
		return inputFile.get();
	}

	XMLParser(File inputFile) {
		int importVersion = getVersion(inputFile);
		if (importVersion != XMLCreator.getExportversion()) {
			//overrride importFile with transformed Version
			inputFile = new ModelInputTransformer().transform(inputFile, importVersion);
		}
		root = initParser(inputFile);
    	ModelInputTransformer.deleteTempFiles();
	}
	
	private int getVersion(File inputFile) {
		Element root = initParser(inputFile);
		String importVersion = null;
		try {
			importVersion = root.getAttribute(XMLAttributes.VERSION.getName());	
			return Integer.valueOf(importVersion);
		} catch (Exception e) {
			// Version is not 4
		}
		try {
			Element versionElement = XMLUtil.getChildElement(root,"Version");
			importVersion = versionElement.getTextContent();
			return Integer.valueOf(importVersion);
		} catch (Exception e) {
			// Version is not 3 or 2
		}
		//TODO TS add logging
		throw new IllegalArgumentException("InputFile has wrong Version number");
	}

	private Element initParser(File inputFile) {
		Document doc = XMLUtil.getDocumentFromFile(inputFile);

		if (DEBUG == true) {
			XMLUtil.saveDocumentToFile(doc, new File("testXMLImport.xml"));
		}
		return doc.getDocumentElement();
	}

	public void parseXMLDocument() {
		createProject();
		
		//i am unsure where it needs to be, you can move it.
		checkImports();
		
		communicator.createDiagram(projectPath, "projectImporter", "", DiagramType.ModelBrowser, false, 
			localID -> {
				sendModelDataToXMF(localID);
				
				Element diagrams = XMLUtil.getChildElement(root, XMLTags.DIAGRAMS.getName());
				NodeList diagramList = diagrams.getChildNodes();

				for (int i = 0; i < diagramList.getLength() - 1; i++) {
					Node node = diagramList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(XMLTags.DIAGRAM.getName()) ) {
						buildDiagram((Element)node);
					}
				}
				ControlCenterClient.getClient().getAllProjects();
			});

	}
	
	/**
	 * This function proves if the referenced packages of the new imported package are already loaded.
	 * Add Info about what happens when the package is not found.
	 */
	private void checkImports() {
		//Only imports version 4 or higher contains Imports. If Import-Tag is not found an empty element is returned (see getChildElement()) and because this has no children no action is performed.
		Element importElement = XMLUtil.getChildElement(root, XMLTags.IMPORTS.getName());
		NodeList importList  = importElement.getChildNodes();
		for (int i = 0; i < importList.getLength() - 1; i++) {
			Node node = importList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(XMLTags.PACKAGE_IMPORT.getName()) ) {
				//add your needed action for an import here
				System.err.println(node.getTextContent());
			}
		}
	}

	private void createProject() {
		projectPath = root.getAttribute(XMLAttributes.PATH.getName());     
        String projectName = projectPath.split("::")[1];
		communicator.createProject(projectName, projectPath);
	}

//	private void buildModel() {
//			//Creates dummy project that hold the model data.
//			//Current assumption is, that there is only one model per project
////			int localID = 	communicator.createDiagramAsync(projectPath, projectName, DiagramType.ModelBrowser);		
////			sendModelDataToXMF(localID);
//		}

	private void sendModelDataToXMF(Integer diagramId) {
		Element model = XMLUtil.getChildElement(root, XMLTags.MODEL.getName());
		NodeList logList = model.getChildNodes();
		for (int i = 0; i < logList.getLength() - 1; i++) {
			if (logList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element modelElement = (Element) logList.item(i);
				// Every request is added to a sorted list on XMF side. Because the request are processed in a ordered way it is ensured, that the first operations are finished before the next one it started. So there should be no error because one Object is waiting for another to be created.
				parseModelElements(diagramId, modelElement);
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
			//TODO TS if this error occurs every action needs to be reverted, otherwise partly working models are loaded -> remove package operation needed
			//TODO TS logging
			Platform.runLater(()->{
				String headerText = "XML corrupted";
				String contentText = "The XML contains unkown model actions. Please provide valid XML.";
				JavaFxAlertAuxilary unkownModelActionsAlert = new JavaFxAlertAuxilary(AlertType.WARNING, headerText, contentText);
				unkownModelActionsAlert.show();
			});			
		}
	}

	private void buildDiagram(Element diagram) {
		String diagramName = diagram.getAttribute(XMLAttributes.NAME.getName());
		// TODO is the file used somewhere?
//		int newDiagramId = communicator.createDiagramAsync(projectPath, diagramName, DiagramType.ClassDiagram );
//		sendDiagramDataToXMF(newDiagramId, diagram);	
		communicator.createDiagram(projectPath, diagramName, "", DiagramType.ClassDiagram, false, 
				newDiagramId -> sendDiagramDataToXMF(newDiagramId, diagram));
	}
	
	private void sendDiagramDataToXMF(Integer diagramId, Element diagram) {
		Element views = XMLUtil.getChildElement(diagram, XMLTags.VIEWS.getName());
		sendDiagramViewStatus(diagramId, views);
		Element diagramsDisplayProperty = XMLUtil.getChildElement(diagram, XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		sendDiagramDisplayproperties(diagramId, diagramsDisplayProperty);
		Element instances = XMLUtil.getChildElement(diagram, XMLTags.INSTANCES.getName());
		NodeList instancesList = instances.getChildNodes();
		//TODO TS Why is this line important?
//		System.err.println("xxxxxx   after wait " + 	communicator.getAllObjectPositions(diagramId));
		for (int i = 0; i < instancesList.getLength(); i++) {
			Node node = instancesList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sendObjectInformation(diagramId, (Element) node);
			}
		}
		Element edges = XMLUtil.getChildElement(diagram, XMLTags.EDGES.getName());
		_ALIGN_EDGES(edges, diagramId);
//		_ALIGN_LABELS(edges, diagramId);
	}
	/*
    public void _ALIGN_LABELS(Element edgesNode, Integer diagramId) {
    	FmmlxDiagram diagram = FmmlxDiagramCommunicator.getDiagram(diagramId);
        Vector<DiagramEdgeLabel<?>>labels = diagram.getLabels();
        for(DiagramEdgeLabel<?> label : labels){
            Point2D initCoordinate = new Point2D(label.getRelativeX(), label.getRelativeY());
            Point2D coordinate = getLabelCoordinate(edgesNode, label, initCoordinate);
            if(validateLabelName(label.getText())){

                label.setRelativePosition(coordinate.getX(), coordinate.getY());
                label.getOwner().updatePosition(label);
                fmmlxDiagram.getComm().storeLabelInfo(fmmlxDiagram, label);
            }

            label.getOwner().updatePosition(label);
            fmmlxDiagram.getComm().storeLabelInfo(fmmlxDiagram, label);
        }
        fmmlxDiagram.objectsMoved = true;
    }
    
    private Point2D getLabelCoordinate(Element edgesNode, DiagramEdgeLabel<?> label, Point2D initCoordinate) {
//        Element labelsElement = getLabelsElement(diagramElement);
    	Vector<Element> label
        NodeList labelList_XML = labelsElement.getChildNodes();

        for (int i = 0 ; i < labelList_XML.getLength() ; i++){
            if (labelList_XML.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element label_xml_element = (Element) labelList_XML.item(i);
                if(label_xml_element.getAttribute("ownerID").equals(label.getOwner().path) &&
                   label_xml_element.getAttribute("localID").equals(label.localID+"")) {
                    double x = Double.parseDouble(label_xml_element.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                    double y = Double.parseDouble(label_xml_element.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                    return new Point2D(x, y);
                }
            }
        }
        return initCoordinate;
    }*/
    
    private void _ALIGN_EDGES(Element edgesNode, int diagramID) {

        NodeList edgeList = edgesNode.getChildNodes();

        for(int i = 0 ; i < edgeList.getLength(); i++){
            if(edgeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element edgeElement = (Element) edgeList.item(i);
                String edgePath = edgeElement.getAttribute("path");
                String sourcePort = edgeElement.getAttribute("sourcePort");
                String targetPort = edgeElement.getAttribute("targetPort");
                
                /////////QUICKFIX/////////
                if(sourcePort == null || "".equals(sourcePort)) sourcePort = edgeElement.getAttribute("source_port");
                if(targetPort == null || "".equals(targetPort)) targetPort = edgeElement.getAttribute("target_port");                
                //////////////////////////
                
                Node intermediatePointsNode = XMLUtil.getChildElement(edgeElement, "IntermediatePoints");
                NodeList intermediatePointList = intermediatePointsNode.getChildNodes();

                Vector<Point2D> intermediatePoints = new Vector<>();
                for(int k = 0 ; k<intermediatePointList.getLength(); k++){
                    if(intermediatePointList.item(k).getNodeType()==Node.ELEMENT_NODE){
                        Element intermediatePointElement = (Element) intermediatePointList.item(k);
                        double x = 0;
                        double y = 0;
                        try{
                        	x = Double.parseDouble(intermediatePointElement.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                        } catch (Exception e) {
                        	x = Double.parseDouble(intermediatePointElement.getAttribute("xCoordinate"));
                        }
                        try{
                        	y = Double.parseDouble(intermediatePointElement.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                        } catch (Exception e) {
                        	y = Double.parseDouble(intermediatePointElement.getAttribute("yCoordinate"));
                        }
                        Point2D point2D = new Point2D(x, y);
                        intermediatePoints.add(point2D);
                    }
                }
                if(intermediatePointList.getLength()>0 || !sourcePort.equals("null") || !targetPort.equals("null")) {
//                    System.err.println(edgePath + intermediatePoints);
                	communicator.sendEdgePositionsFromXml(diagramID, edgePath, intermediatePoints, sourcePort, targetPort);
                }
                NodeList labelsNodeList = XMLUtil.getChildElement(edgeElement, "Labels").getChildNodes();
                for(int j = 0; j < labelsNodeList.getLength(); j++) if(labelsNodeList.item(j).getNodeType()==Node.ELEMENT_NODE){
                	try{
	                	Element labelE = (Element) labelsNodeList.item(j);
	                	int id = Integer.parseInt(labelE.getAttribute("localID"));
	                    float x = Float.parseFloat(labelE.getAttribute("xCoordinate"));
	                    float y = Float.parseFloat(labelE.getAttribute("yCoordinate"));
	
	                	communicator.storeLabelInfo(diagramID, edgePath, id, x, y);
                	} catch (Exception e) {
                		System.err.println("Label cannot be read: " + e.getMessage());
                	}
                }                
            }
        }
    }

	private void sendDiagramViewStatus(Integer diagramID, Element viewsElement) {
		SortedMap<String, Affine> views = new TreeMap<>();
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

	private void sendObjectInformation(Integer diagramId, Element object) {
        int x = Integer.parseInt(object.getAttribute(XMLAttributes.X_COORDINATE.getName()));
        int y = Integer.valueOf(object.getAttribute(XMLAttributes.Y_COORDINATE.getName()));
        boolean hidden = Boolean.parseBoolean(object.getAttribute(XMLAttributes.HIDDEN.getName()));
        String ref = object.getAttribute(XMLAttributes.PATH.getName());
        XMLInstanceStub stub = new XMLInstanceStub(ref, hidden, x, y);

        communicator.sendObjectInformation(diagramId, stub);
	}
}