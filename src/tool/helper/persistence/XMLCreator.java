package tool.helper.persistence;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator.DiagramInfo;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.helper.user_properties.PropertyManager;
import tool.helper.user_properties.UserProperty;
import tool.clients.fmmlxdiagrams.ModelActionsList;
import tool.clients.fmmlxdiagrams.Note;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.xmodeler.XModeler;
import tool.clients.fmmlxdiagrams.graphics.GraphicalMappingInfo;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;

/**
 * This class is used to create an XML representation of a package.
 */
public class XMLCreator {
	private Vector<DiagramInfo> diagramsWaitingForParsing;
	private FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
	private Element root;
	private static final int EXPORT_VERSION = 4;
	private String packagePath;
	private FmmlxDiagram currentDiagram;

	
	public void createAndSaveXMLRepresentation(String packagePath) {
		this.packagePath = packagePath;
		Document doc = initXML();
		// calls save operation after representation is build
		getData(packagePath, onDocumentReturned -> {saveToFile(doc);});
	}
	
	/**
	 * Return an XML representation of an model. The representation can only be used inside the ReturnCall, that is defined in the signature
	 * 
	 * @param packagePath of the diagram that should be represented as XML
	 * @param onDocumentCreated return call, that will give back the document. Any action can be performed on the doc
	 * @param diagram used to check if a diagram is in umlMode
	 */
	public void getXmlRepresentation(String packagePath, ReturnCall<Document> onDocumentCreated, AbstractPackageViewer diagram) {
		this.packagePath = packagePath;
		Document doc = initXML();
		// calls save operation after representation is build
		getData(packagePath, onDocumentReturned -> {onDocumentCreated.run(doc);});
	}

	private void saveToFile(Document doc) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose save location");
		chooser.setInitialFileName(packagePath.split("::")[1]);
		chooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		if (PropertyManager.getProperty(UserProperty.RECENTLY_SAVED_MODEL_DIR.toString()) != null) {
			String recentlySavedDirPath = PropertyManager.getProperty(UserProperty.RECENTLY_SAVED_MODEL_DIR.toString());
			File recentlySavedFile = new File(recentlySavedDirPath);
			chooser.setInitialDirectory(recentlySavedFile.getParentFile());
		}
		Runnable showFileChooserStage = () -> {
		Stage s = new Stage();
		s.setAlwaysOnTop(true);
		s.initModality(Modality.APPLICATION_MODAL);
		Optional<File> saveFile = Optional.of(chooser.showSaveDialog(s));
		//ErrorLoggin in the case the dialog is escaped
		if (saveFile == null) {
			System.err.println("XML Export was interrupted");
			return; 
		}
		PropertyManager.setProperty(UserProperty.RECENTLY_SAVED_MODEL_DIR.toString(),saveFile.get().getAbsolutePath());
		XMLUtil.saveDocumentToFile(doc, saveFile.get());		
		};
		//Everything that is related to UI can not run on external Thread. Because async methods are answered with an external thread here the function must be wrapped with a Plattform.runLater() 
		Platform.runLater(showFileChooserStage);
	}

	private void getData(String packagePath, ReturnCall<Object> onDataReceived) {
		ReturnCall<Vector<DiagramInfo>> onDiagramInfosReceived = diagramInfo -> appendModelToRoot(diagramInfo, packagePath, onDataReceived);
		comm.getAllDiagramInfos(packagePath, onDiagramInfosReceived);
	}

	private void appendModelToRoot(Vector<DiagramInfo> diagramInfos, String packagePath, ReturnCall<Object> onDataReceived) {
		ReturnCall<ModelActionsList> onModelDataReceived = packageContent -> {
			Vector<ModelActionsList> logs = packageContent.getChildren();
			Collections.sort(logs);
			Element model = XMLUtil.createChildElement(root, XMLTags.MODEL.getName());
			model.setAttribute(XMLAttributes.NAME.getName(), packagePath);
			for (ModelActionsList logData : logs) {
				buildModelActionTag(model, logData);
			}
			getDiagramsData(diagramInfos, packagePath, onDataReceived);
		};
		
		ReturnCall<Integer> onDiagramCreated = diagramId -> {
			ReturnCall<Vector<String>> importedPackagesReturn = importedPackages -> {
				exportPackageImports(importedPackages);
				comm.getModelData(diagramId, onModelDataReceived);
			};			
			comm.getImportedPackages(diagramId, importedPackagesReturn);
		};				
		comm.createDiagram(packagePath, "Serializer", "", FmmlxDiagramCommunicator.DiagramType.ModelBrowser, false, onDiagramCreated);
	}

	/**
	 * This function exports all packages that are imported by the package that the user exports.
	 * @param imports a string list that holds the imported package names
	 */
	private void exportPackageImports(List<String> imports) {
		Element importsElement = XMLUtil.createChildElement(root, XMLTags.IMPORTS.getName());
		for (String importName : imports) {
			Element importElement = XMLUtil.createChildElement(importsElement, XMLTags.PACKAGE_IMPORT.getName());
			importElement.setTextContent(importName);
		}		
	}

	private void buildModelActionTag(Element model, ModelActionsList logData) {
		Element log = XMLUtil.createChildElement(model, logData.getName());
		for (String attName : logData.getAttributes()) {
			log.setAttribute(attName, logData.getAttributeValue(attName));
		}
	}

	private void getDiagramsData(Vector<DiagramInfo> diagramInfos, String packagePath, ReturnCall<Object> onDataReceived) {
		diagramsWaitingForParsing = new Vector<>(diagramInfos);
		buildNextDiagram(onDataReceived);
	}

	private void buildNextDiagram(ReturnCall<Object> onDataReceived) {
		if (diagramsWaitingForParsing.isEmpty()) {
			onDataReceived.run("Resolve ToDoList");
		} else {
			Element diagrams = returnDiagramsTag();
			DiagramInfo diagramInfo = diagramsWaitingForParsing.remove(0);
			Element diagram = createDiagramElement(diagramInfo, diagrams);
			appendEdgesToDiagram(diagramInfo, diagram);
			appendObjectInformationToDiagram(diagramInfo, diagram);
			appendDiagramDisplayPropertiesToDiagrams(diagramInfo, diagram);
			appendNotesToDiagram(diagramInfo, diagram);
			// the next function also makes recursive call to buildNextDiagram
			appendViewsToDiagram(onDataReceived, diagramInfo, diagram);
		}
	}

	/**
	 * This function exports all notes of a diagram
	 * @param diagramInfo holds info about the diagram
	 * @param diagram represents the XML-Element that holds the diagram-export-data
	 */
	private void appendNotesToDiagram(DiagramInfo diagramInfo, Element diagram) {
		Element notesElement = XMLUtil.createChildElement(diagram, XMLTags.NOTES.getName());
		ReturnCall<Vector<Note>> onNotesReturned = notes -> {
			ReturnCall<Vector<GraphicalMappingInfo>> noteMappingRetturned = noteMapping -> {
				//3. loop over every note
				for (Note note : notes) {
					boolean matched = false;
					//4. for every note loop over the list of returned mappingInfos
					for (GraphicalMappingInfo mappingInfo : noteMapping) {
						//5. if the mappingInfoKey matches the note id use all infos to append note to XML 
						if (mappingInfo.getNoteIdFromMappingKey() == note.getId()) {
							appendNoteToNotes(notesElement, note, mappingInfo);
							matched = true;
						}
					}
					if (!matched) {
						//6. if there is no match raise exception
						throw new NoSuchElementException("Try to find a noteMapping for Note with id '" +  note.getId() + "'. Backend does not contain information about that");
					}
				}
			};
			//2. after all notes are returned (local var "notes") all noteMappings are requested
			Note.getNotesMappings(diagramInfo.getId(), noteMappingRetturned);
		};
		//1. request all notes from xmf
		Note.getAllNotes(diagramInfo.getId(), onNotesReturned);
	}

	/**
	 * For each note on the diagram an note tag is added to the XML-File. See all contained subtags in the method
	 * @param notesElement is the element each note tag is added to
	 * @param note contains the note information
	 * @param mappingInfo contains the mappingInformation
	 */
	private void appendNoteToNotes(Element notesElement, Note note, GraphicalMappingInfo mappingInfo) {	
		Element noteElement = XMLUtil.createChildElement(notesElement, XMLTags.NOTE.getName());
		XMLUtil.createChildElement(noteElement, XMLTags.NOTEID.getName(), String.valueOf(note.getId()));
		XMLUtil.createChildElement(noteElement, XMLTags.NOTECOLOR.getName(), note.getNoteColor().toString());
		XMLUtil.createChildElement(noteElement, XMLTags.NOTECONTENT.getName(), note.getContent());
		Element notePositionElement = XMLUtil.createChildElement(noteElement, XMLTags.NOTEPOSITION.getName());
		XMLUtil.createChildElement(notePositionElement, XMLTags.XPOSITION.getName(), String.valueOf(mappingInfo.getxPosition()));
		XMLUtil.createChildElement(notePositionElement, XMLTags.YPOSITION.getName(), String.valueOf(mappingInfo.getyPosition()));
	}

	private Element returnDiagramsTag() {
		NodeList nodes = root.getElementsByTagName(XMLTags.DIAGRAMS.getName());
		// if diagrams do not exist append new tag to document
		if (nodes.getLength() == 0) {
			return XMLUtil.createChildElement(root, XMLTags.DIAGRAMS.getName());
		}
		// if diagram exists return existing diagrams tag
		return (Element) nodes.item(0);
	}

	private void appendDiagramDisplayPropertiesToDiagrams(DiagramInfo diagramInfo, Element diagram) {
		Element diagramDisplayProperties = XMLUtil.createChildElement(diagram, XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		ReturnCall<HashMap<String, Boolean>> onDiagramDisplayPropertiesReturn = diagramDisplayPropertiesData -> {
			for (Entry<String, Boolean> entry : diagramDisplayPropertiesData.entrySet()) {
				appendPropertyToProperties(diagramDisplayProperties, entry);
			}
		};
		comm.getDiagramDisplayProperties(diagramInfo.getId(), onDiagramDisplayPropertiesReturn);
	}

	private void appendPropertyToProperties(Element diagramDisplayProperties, Entry<String, Boolean> entry) {
		String tagname = entry.getKey();
		tagname = tagname.substring(0, 1).toUpperCase() + tagname.substring(1);
		Element element = XMLUtil.createChildElement(diagramDisplayProperties, tagname);
		element.setTextContent(entry.getValue().toString());
	}

	private void appendEdgesToDiagram(DiagramInfo diagramInfo, Element diagram) {
		Element edges = XMLUtil.createChildElement(diagram, XMLTags.EDGES.getName());
		ReturnCall<HashMap<String, HashMap<String, Object>>> onAllEdgePositionsReceived = edgesData -> {
			for (Entry<String, HashMap<String, Object>> edgeData : edgesData.entrySet()) {
				appendEdgeToEdges(edges, edgeData, diagramInfo);
			}
		};
		comm.getAllEdgePositions(diagramInfo.getId(), onAllEdgePositionsReceived);
	}

	@SuppressWarnings("unchecked")
	private void appendEdgeToEdges(Element edges, Entry<String, HashMap<String, Object>> edgeData, DiagramInfo diagramInfo) {
		Element edge = createEdgeElement(edges, edgeData);
		HashMap<String, Object> attributesMap = edgeData.getValue();
		setEdgePorts(edge, (Vector<Vector<Object>>) attributesMap.get("Ports"));
		appendIntermediatePointsToEdge((Vector<Vector<Object>>) attributesMap.get("IntermediatePoints"), edge);
		appendLabelsToEdge(edge, diagramInfo);
	}

	private Element createEdgeElement(Element edges, Entry<String, HashMap<String, Object>> edgeData) {
		Element edge = XMLUtil.createChildElement(edges, XMLTags.EDGE.getName());
		setEdgeType(edge, edgeData);
		return edge;
	}

	private void appendLabelsToEdge(Element edge, DiagramInfo diagramInfo) {
		Element labels = XMLUtil.createChildElement(edge, XMLTags.LABELS.getName());
		requestLabelsData(diagramInfo, labels, edge);
		
	}

	private void setEdgeType(Element edge, Entry<String, HashMap<String, Object>> edgeData) {
		String type = edgeData.getKey().split("Mapping")[0];
//		if (!XMLEdgeTypes.contains(type)) {
//			//TODO for association no string value for the type is send back from XMF... On the long run this should not be handled on the Java side but fixed on XMF
//			String edgePath = packagePath + "::" + edgeData.getKey();
//			edge.setAttribute(XMLAttributes.PATH.getName(), edgePath);
//			edge.setAttribute(XMLAttributes.TYPE.getName(), XMLEdgeTypes.ASSOCIATION.getName());
//			edge.setAttribute(XMLAttributes.DISPLAYNAME.getName(), edgeData.getKey());
//		} else {
			edge.setAttribute(XMLAttributes.TYPE.getName(), type);
			edge.setAttribute(XMLAttributes.PATH.getName(), edgeData.getKey());
//		}
	}

	private void appendIntermediatePointsToEdge(Vector<Vector<Object>> intermediatePointsData, Element edge) {
		Element intermediatePoints = XMLUtil.createChildElement(edge, XMLTags.INTERMEDIATE_POINTS.getName());
		for (Vector<Object> intermediatePointVector : intermediatePointsData) {
			Element intermediatePoint = XMLUtil.createChildElement(intermediatePoints,XMLTags.INTERMEDIATE_POINT.getName());
			intermediatePoint.setAttribute(XMLAttributes.X_COORDINATE.getName(), String.valueOf(intermediatePointVector.get(1)));
			intermediatePoint.setAttribute(XMLAttributes.Y_COORDINATE.getName(), String.valueOf(intermediatePointVector.get(2)));
		}
	}

	private void setEdgePorts(Element edge, Vector<Vector<Object>> ports) {
		for (Vector<Object> portVector : ports) {
			if (portVector.get(0).equals("startNode")) {
				edge.setAttribute(XMLAttributes.SOURCE_PORT.getName(), (String) portVector.get(1));
			}
			if (portVector.get(0).equals("endNode")) {
				edge.setAttribute(XMLAttributes.TARGET_PORT.getName(), (String) portVector.get(1));
			}
		}
	}

	private void requestLabelsData(DiagramInfo diagramInfo, Element labels, Element edge) {
		ReturnCall<HashMap<String, HashMap<String, Object>>> onAllLabelPositionsReceived = labelsData -> {
			for (Entry<String, HashMap<String, Object>> labelData : labelsData.entrySet()) {
				HashMap<String, Object> attributesMap = labelData.getValue();
				appendMatchingLabels(attributesMap, labels, edge);
				}
		};
		comm.getAllLabelPositions(diagramInfo.getId(), onAllLabelPositionsReceived);
	}		

	private void appendMatchingLabels(HashMap<String, Object> attributesMap, Element labels, Element edge) {
		if (attributesMap.get("ownerID").equals(edge.getAttribute(XMLAttributes.PATH.getName()))) {
			Element label = XMLUtil.createChildElement(labels, XMLTags.LABEL.getName());
			setLabelAttributes(attributesMap, label);
		}
	}

	private void setLabelAttributes(HashMap<String, Object> attributesMap, Element label) {
		label.setAttribute(XMLAttributes.LOCAL_ID.getName(), String.valueOf(attributesMap.get("localID")));
		label.setAttribute(XMLAttributes.OWNER_ID.getName(), String.valueOf(attributesMap.get("ownerID")));
		label.setAttribute(XMLAttributes.X_COORDINATE.getName(), String.valueOf(attributesMap.get("x")));
		label.setAttribute(XMLAttributes.Y_COORDINATE.getName(), String.valueOf(attributesMap.get("y")));
	}

	private void appendObjectInformationToDiagram(DiagramInfo diagramInfo, Element diagram) {
		ReturnCall<HashMap<String, HashMap<String, Object>>> onAllObjectPositionsReceived = objectPositions -> {
			Element objects = XMLUtil.getChildElement(diagram, XMLTags.INSTANCES.getName());
			for (java.util.Map.Entry<String, HashMap<String, Object>> objectPosition : objectPositions.entrySet()) {
				createObjectElement(objects, objectPosition);
			}
		};
		comm.getObjectsInformation(diagramInfo.getId(), onAllObjectPositionsReceived);
	}

	private void appendViewsToDiagram(ReturnCall<Object> onDataReceived, DiagramInfo diagramInfo, Element diagram) {
		Element views = XMLUtil.createChildElement(diagram, XMLTags.VIEWS.getName());
		ReturnCall<Vector<Vector<Object>>> onAllViewsReceived = viewsData -> {
			for (Vector<Object> viewData : viewsData) {
				Element view = XMLUtil.createChildElement(views, XMLTags.VIEW.getName());
				view.setAttribute(XMLAttributes.NAME.getName(), "" + viewData.get(0));
				view.setAttribute(XMLAttributes.XX.getName(), "" + viewData.get(1));
				view.setAttribute(XMLAttributes.TX.getName(), "" + viewData.get(2));
				view.setAttribute(XMLAttributes.TY.getName(), "" + viewData.get(3));
			}
			buildNextDiagram(onDataReceived);
		};
		comm.getAllViews(diagramInfo.getId(), onAllViewsReceived);
	}

	private void createObjectElement(Element objects,
			java.util.Map.Entry<String, HashMap<String, Object>> objectPosition) {
		Element object = XMLUtil.createChildElement(objects, XMLTags.INSTANCE.getName());
		object.setAttribute(XMLAttributes.PATH.getName(), objectPosition.getKey());
		HashMap<String, Object> attributesMap = objectPosition.getValue();
		object.setAttribute(XMLAttributes.X_COORDINATE.getName(), String.valueOf(attributesMap.get("x")));
		object.setAttribute(XMLAttributes.Y_COORDINATE.getName(), String.valueOf(attributesMap.get("y")));
		object.setAttribute(XMLAttributes.HIDDEN.getName(), String.valueOf(attributesMap.get("hidden")));
	}

	private Element createDiagramElement(DiagramInfo diagramInfo, Element diagrams) {
		Element diagram = XMLUtil.createChildElement(diagrams, XMLTags.DIAGRAM.getName());
		diagram.setAttribute(XMLAttributes.NAME.getName(), diagramInfo.getDiagramName());
		XMLUtil.createChildElement(diagram, XMLTags.INSTANCES.getName());
		return diagram;
	}

	private Document initXML() {
		Document doc = XMLUtil.createDocument(XMLTags.ROOT.getName());
		root = doc.getDocumentElement();
		root.setAttribute(XMLAttributes.EXPORT_VERSION.getName(), String.valueOf(EXPORT_VERSION));
		root.setAttribute(XMLAttributes.PATH.getName(), packagePath);
		root.setAttribute(XMLAttributes.XMODELER_VERSION.getName(), XModeler.getVersion());
		return doc;
	}
	
	/**
	 * @return current used version of XML-Exports
	 */
	public static int getExportversion() {
		return EXPORT_VERSION;
	}
}