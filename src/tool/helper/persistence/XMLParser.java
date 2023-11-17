package tool.helper.persistence;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
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