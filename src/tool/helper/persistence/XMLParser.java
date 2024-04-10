package tool.helper.persistence;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Objects;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator.DiagramType;
import tool.clients.fmmlxdiagrams.Note;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.auxilaryFX.JavaFxAlertAuxilary;
import tool.helper.persistence.modelActionParser.ModelActionParser;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;
import tool.xmodeler.ControlCenterClient;

/**
 * This class is used to send data that is contained in a XML-represenation of an model to the backend. So later the model and its diagrams could be displayed in the java-frontend
 */
public class XMLParser {
	private FmmlxDiagramCommunicator communicator = FmmlxDiagramCommunicator.getCommunicator();
	private Element root;
	private Document doc;
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
		root = getRoot(inputFile);
		if (DEBUG == true) {
			XMLUtil.saveDocumentToFile(doc, new File("testXMLTransformation.xml"));
		}
		//used to clean disc from temporary used files for transformation
		ModelInputTransformer.deleteTempFiles();
		projectPath = root.getAttribute(XMLAttributes.PATH.getName());     
	}
	
	private int getVersion(File inputFile) {
		String importVersion = null;
		Element rootTemp = getRoot(inputFile);		
		try {
			importVersion = rootTemp.getAttribute(XMLAttributes.EXPORT_VERSION.getName());	
			return Integer.valueOf(importVersion);
		} catch (Exception e) {
			// Version is not 4
		}
		try {
			Element versionElement = XMLUtil.getChildElement(rootTemp,"Version");
			importVersion = versionElement.getTextContent();
			return Integer.valueOf(importVersion);
		} catch (Exception e) {
			// Version is not 3 or 2
		}
		//TODO TS add logging
		throw new IllegalArgumentException("InputFile has wrong Version number");
	}

	private Element getRoot(File inputFile) {
		doc = XMLUtil.getDocumentFromFile(inputFile);
		if (DEBUG == true) {
			XMLUtil.saveDocumentToFile(doc, new File("testXMLImport.xml"));
		}
		return doc.getDocumentElement();
	}

	public void parseXMLDocument() {
		//2. run ReturnCall when there are no name conflicts
		ReturnCall<Object> onConflicitingNamesChecked = noConflict -> {
		
		communicator.createProject(projectPath.split("::")[1], projectPath);		
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
		};
		//Check if XModeler already contains project with same name
		checkForNameConflict(onConflicitingNamesChecked);
	}
	
	/**
	 * If the backend tries to create a project with the same name it crashes. This
	 * function checks whether there is already a project whose name matches the
	 * name of the import project. If so, an alert is presented and the import is aborted.
	 */
	private void checkForNameConflict(ReturnCall<Object> onNameChecked) {

		ReturnCall<Vector<Object>> onProjectNamesReturned = projectNamesVec -> {

			Vector<String> projectNames = (Vector) projectNamesVec.get(0);
			for (String projectName : projectNames) {
				if ((projectPath.split("::")[1]).equals(projectName)) {
					showNameConflictAlert(projectPath.split("::")[1]);
					return;
				}
			}
			onNameChecked.run(null);
		};
		// please replace 0 with getHandle() and the second 0 with get DiagramId -> there was an update in another branch that contain these functions
		communicator.xmfRequestAsync(0, 0, "getAllProjectNames", onProjectNamesReturned);
	}

	/**
	 * In case of conflicting project names by import this function shows an alert.
	 */
	private void showNameConflictAlert(String conflictingProject) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Conflicting names");
			alert.setHeaderText("The current session already contains a project with the name \"" + conflictingProject + "\"!");
			alert.setContentText("Please change name of the project you would like to import or restart XModeler");
			alert.getButtonTypes().setAll(ButtonType.OK);
			alert.showAndWait();}
		);
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
		boolean umlMode = Boolean.parseBoolean(diagram.getAttribute(XMLAttributes.UML_Mode.getName()));
		communicator.createDiagram(projectPath, diagramName, "", DiagramType.ClassDiagram, umlMode, 
				newDiagramId -> sendDiagramDataToXMF(newDiagramId, diagram));
	}
	
	private void sendDiagramDataToXMF(Integer diagramId, Element diagramElement) {
		Element views = XMLUtil.getChildElement(diagramElement, XMLTags.VIEWS.getName());
		sendDiagramViewStatus(diagramId, views);
		Element diagramsDisplayProperty = XMLUtil.getChildElement(diagramElement, XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		sendDiagramDisplayproperties(diagramId, diagramsDisplayProperty);
		Element instances = XMLUtil.getChildElement(diagramElement, XMLTags.INSTANCES.getName());
		NodeList instancesList = instances.getChildNodes();
		for (int i = 0; i < instancesList.getLength(); i++) {
			Node node = instancesList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sendObjectInformation(diagramId, (Element) node);
			}
		}
		sendNotesDataToXMF(diagramId, diagramElement);
		Element edges = XMLUtil.getChildElement(diagramElement, XMLTags.EDGES.getName());
		_ALIGN_EDGES(edges, diagramId);
//		_ALIGN_LABELS(edges, diagramId);
	}

	/**
	 * Prepares loop. This loop will call for every Note-Element in the XML a function that sends note corresponding data to XMF
	 * @param diagramId defines the diagram the note is added to
	 * @param diagramElement XML-Element, that contains as children further XML-Elements with data
	 */
	private void sendNotesDataToXMF(Integer diagramId, Element diagramElement) {
		Element notesElement = XMLUtil.getChildElement(diagramElement, XMLTags.NOTES.getName());
		NodeList notesList = notesElement.getChildNodes();
		//loop over every note-element
		for (int i = 0; i < notesList.getLength(); i++) {
			Node node = notesList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				sendNoteDataToXMF(diagramId, (Element) node);
			}
		}
	}
	
	/**
	 * For every note element containing data is send to XMF
	 * @param diagramId defines the diagram the note is added to
	 * @param noteElement contains the note data from the XML-representation
	 */
	private void sendNoteDataToXMF(Integer diagramId, Element noteElement) {
        //Create note instance
		Note note = new Note();
        note.setId(Integer.parseInt(XMLUtil.getChildElement(noteElement, XMLTags.NOTEID.getName()).getTextContent()));
        note.setNoteColor(Color.valueOf((XMLUtil.getChildElement(noteElement, XMLTags.NOTECOLOR.getName()).getTextContent())));
        note.setContent((XMLUtil.getChildElement(noteElement, XMLTags.NOTECONTENT.getName()).getTextContent()));
		double[] position = parseNotePosition(noteElement);
		note.setPosition(position[0], position[1]);
        
		//send data to XMF
		note.addNoteToDiagram(diagramId);
        note.sendCurrentNoteMappingToXMF(diagramId, r -> {});
	}

	/**
	 * Parses x and y coordinate of note from XML
	 * @param noteElement has child element, that contains data
	 * @return double array. First position = x, second position = y
	 */
	private double[] parseNotePosition(Element noteElement) {
		double[] positions = new double[2];
		Element notePosition = XMLUtil.getChildElement(noteElement, XMLTags.NOTEPOSITION);
		NodeList positionsList = notePosition.getChildNodes();
		for (int i = 0; i < positionsList.getLength(); i++) {
			Node node = positionsList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (Objects.equals(node.getNodeName(), XMLTags.XPOSITION.getName())) {
					positions[0] = Double.parseDouble(node.getTextContent());
				} else if (Objects.equals(node.getNodeName(), XMLTags.YPOSITION.getName())) {
					positions[1] = Double.parseDouble(node.getTextContent());
				}
			}
		}
		return positions;
	}
  
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