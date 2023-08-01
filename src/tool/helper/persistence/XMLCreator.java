package tool.helper.persistence;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
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
import tool.clients.fmmlxdiagrams.PackageActionsList;
import tool.clients.fmmlxdiagrams.ReturnCall;

public class XMLCreator {
	Vector<DiagramInfo> diagramsWaitingForParsing;
	FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
	Element root;
	private final String exportVersion = "4";
	String packagePath;
	Optional<File> saveFile;

	public void createAndSaveXMLRepresentation(String packagePath) {
		this.packagePath = packagePath;
		Document doc = initXML();
		// calls save operation after representation is build
		getData(packagePath, onDocumentReturned -> {saveToFile(doc);});
	}

	private void saveToFile(Document doc) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose save location");
		chooser.setInitialFileName(packagePath.split("::")[1]);
		chooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		Runnable showFileChooserStage = () -> {
		Stage s = new Stage();
		s.setAlwaysOnTop(true);
		s.initModality(Modality.APPLICATION_MODAL);
		saveFile = Optional.of(chooser.showSaveDialog(s));
		//ErrorLoggin in the case the dialog is escaped
		if (saveFile == null) {
			System.err.println("XML Export was interrupted");
			return; 
		}
		XMLUtil.saveDocumentToFile(doc, saveFile.get());		
		};
		//Everything that is related to UI can not run on external Thread. Because async methods are answered with an external thread here the function must be wrapped with a Plattform.runLater() 
		Platform.runLater(showFileChooserStage);
	}

	private void getData(String packagePath, ReturnCall<Object> onDataReceived) {
		ReturnCall<Vector<DiagramInfo>> onDiagramInfosReceived = diagramInfo -> {
			appendModelToRoot(diagramInfo, packagePath, onDataReceived);
		};
		comm.getAllDiagramInfos(packagePath, onDiagramInfosReceived);
	}

	private void appendModelToRoot(Vector<DiagramInfo> diagramInfos, String packagePath,
			ReturnCall<Object> onDataReceived) {
		ReturnCall<PackageActionsList> onModelDataReceived = packageContent -> {
			// TODO name all as addInstance + add level
			Vector<PackageActionsList> logs = packageContent.getChildren();
			Collections.sort(logs);
			Element model = XMLUtil.createChildElement(root, XMLTags.MODEL.getName());
			// TODO Was bringt der root-zusatz, kann nicht einfach nur der name gespeichert
			// werden?
			model.setAttribute(XMLAttributes.NAME.getName(), packagePath);
			for (PackageActionsList logData : logs) {
				Element log = XMLUtil.createChildElement(model, logData.getName());
				for (String attName : logData.getAttributes()) {
					log.setAttribute(attName, logData.getAttributeValue(attName));
				}
			}
			;
			getDiagramsData(diagramInfos, packagePath, onDataReceived);
		};
		comm.createDiagram(packagePath, "Serializer", "", FmmlxDiagramCommunicator.DiagramType.ModelBrowser, false,
				diagramId -> {
					comm.getModelData(diagramId, onModelDataReceived);
				});
	}

	private void getDiagramsData(Vector<DiagramInfo> diagramInfos, String packagePath,
			ReturnCall<Object> onDataReceived) {
		diagramsWaitingForParsing = new Vector<>(diagramInfos);
		parseNextModel(onDataReceived);
	}

	private void parseNextModel(ReturnCall<Object> onDataReceived) {
		if (diagramsWaitingForParsing.isEmpty()) {
			onDataReceived.run("Resolve ToDoList");
		} else {
			Element diagrams = returnDiagramsTag();
			DiagramInfo diagramInfo = diagramsWaitingForParsing.remove(0);
			Element diagram = createDiagramElement(diagramInfo, diagrams);
			appendEdgesToDiagram(diagramInfo, diagram);
			appendObjectInformationToDiagram(diagramInfo, diagram);
			appendDiagramDisplayPropertiesToDiagrams(diagramInfo, diagram);
			// the next function also makes reursive call to resolveToDoList
			appendViewsToDiagram(onDataReceived, diagramInfo, diagram);
		}
	}

	private Element returnDiagramsTag() {
		NodeList nodes = root.getElementsByTagName(XMLTags.DIAGRAMS.getName());
		// if diagrams do not exist append new tag to document
		if (nodes.getLength() == 0) {
			Element diagrams = XMLUtil.createChildElement(root, XMLTags.DIAGRAMS.getName());
			return diagrams;
		}
		// if diagram exists return existing diagrams tag
		Element diagrams = (Element) nodes.item(0);
		return diagrams;
	}

	private void appendDiagramDisplayPropertiesToDiagrams(DiagramInfo diagramInfo, Element diagram) {
		Element diagramDisplayProperties = XMLUtil.createChildElement(diagram,
				XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		ReturnCall<HashMap<String, Boolean>> onDiagramDisplayPropertiesReturn = diagramDisplayPropertiesData -> {
			for (Entry<String, Boolean> entry : diagramDisplayPropertiesData.entrySet()) {
				String tagname = entry.getKey();
				tagname = tagname.substring(0, 1).toUpperCase() + tagname.substring(1);
				Element element = XMLUtil.createChildElement(diagramDisplayProperties, tagname);
				element.setTextContent(entry.getValue().toString());
			}
		};
		comm.getDiagramDisplayProperties(diagramInfo.getId(), onDiagramDisplayPropertiesReturn);
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

	private void appendEdgeToEdges(Element edges, Entry<String, HashMap<String, Object>> edgeData, DiagramInfo diagramInfo) {
		Element edge = XMLUtil.createChildElement(edges, XMLTags.EDGE.getName());
		String ref = edgeData.getKey();
		String edgeName = packagePath + "::" + ref;
		edge.setAttribute(XMLAttributes.NAME.getName(),edgeName);
		edge.setAttribute(XMLAttributes.REF.getName(), ref);
		
		HashMap<String, Object> attributesMap = edgeData.getValue();
		setEdgePorts(edge, attributesMap);
		appendIntermediatePointsToEdge(attributesMap, edge);
		setEdgeType(edge, edgeData);
		appendLabelsToEdge(edge, diagramInfo);
	}

	private void appendLabelsToEdge(Element edge, DiagramInfo diagramInfo) {
		Element labels = XMLUtil.createChildElement(edge, XMLTags.LABELS.getName());
		appendLabelToLabels(diagramInfo, labels, edge);
		
	}

	private void setEdgeType(Element edge, Entry<String, HashMap<String, Object>> edgeData) {
		String type = edgeData.getKey().split("Mapping")[0];
		if (!XMLEdgeTypes.contains(type)) {
			edge.setAttribute(XMLAttributes.TYPE.getName(), "association");
			// TODO -> why do this? Should there be a value?
			edge.setAttribute(XMLAttributes.ATTRIBUTE_PARENT_ASSOCIATION.getName(), "VOID");
		} else {
			edge.setAttribute(XMLAttributes.TYPE.getName(), type);			
		}
	}

	private void appendIntermediatePointsToEdge(HashMap<String, Object> attributesMap, Element edge) {
		Element intermediatePoints = XMLUtil.createChildElement(edge, XMLTags.INTERMEDIATE_POINTS.getName());
		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> intermediatePointsData = (Vector<Vector<Object>>) attributesMap.get("IntermediatePoints");
		for (Vector<Object> intermediatePointVector : intermediatePointsData) {
			Element intermediatePoint = XMLUtil.createChildElement(intermediatePoints,XMLTags.INTERMEDIATE_POINT.getName());
			intermediatePoint.setAttribute(XMLAttributes.X_COORDINATE.getName(), String.valueOf(intermediatePointVector.get(1)));
			intermediatePoint.setAttribute(XMLAttributes.Y_COORDINATE.getName(), String.valueOf(intermediatePointVector.get(2)));
		}
	}

	private void setEdgePorts(Element edge, HashMap<String, Object> attributesMap) {
		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> ports = (Vector<Vector<Object>>) attributesMap.get("Ports");
		for (Vector<Object> portVector : ports) {
			if (portVector.get(0).equals("startNode")) {
				edge.setAttribute(XMLAttributes.SOURCE_PORT.getName(), (String) portVector.get(1));
			}
			if (portVector.get(0).equals("endNode")) {
				edge.setAttribute(XMLAttributes.TARGET_PORT.getName(), (String) portVector.get(1));
			}
		}
	}

	private void appendLabelToLabels(DiagramInfo diagramInfo, Element labels, Element edge) {
		ReturnCall<HashMap<String, HashMap<String, Object>>> onAllLabelPositionsReceived = labelsData -> {
			for (Entry<String, HashMap<String, Object>> labelData : labelsData.entrySet()) {
				HashMap<String, Object> attributesMap = labelData.getValue();				
				if (attributesMap.get("ownerID").equals(edge.getAttribute(XMLAttributes.NAME.getName()))) {
					Element label = XMLUtil.createChildElement(labels, XMLTags.LABEL.getName());
					setLabelAttributes(attributesMap, label);
				}
			}
		};
		comm.getAllLabelPositions(diagramInfo.getId(), onAllLabelPositionsReceived);
	}

	private void setLabelAttributes(HashMap<String, Object> attributesMap, Element label) {
		label.setAttribute(XMLAttributes.LOCAL_ID.getName(), String.valueOf(attributesMap.get("localID")));
		label.setAttribute(XMLAttributes.OWNER_ID.getName(), String.valueOf(attributesMap.get("ownerID")));
		label.setAttribute(XMLAttributes.X_COORDINATE.getName(), String.valueOf(attributesMap.get("x")));
		label.setAttribute(XMLAttributes.Y_COORDINATE.getName(), String.valueOf(attributesMap.get("y")));
	}

	// TODO store this information until the a fmmlxDiagram object is created. When
	// the object is created, then assign the values to the fmmlObjekt
	// would be nicer if i find the operation how to send object positon to xmf
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
			parseNextModel(onDataReceived);
		};
		comm.getAllViews(diagramInfo.getId(), onAllViewsReceived);
	}

	private void createObjectElement(Element objects,
			java.util.Map.Entry<String, HashMap<String, Object>> objectPosition) {
		Element object = XMLUtil.createChildElement(objects, XMLTags.INSTANCE.getName());
		object.setAttribute(XMLAttributes.REF.getName(), objectPosition.getKey());
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
		root.setAttribute(XMLAttributes.VERSION.getName(), exportVersion);
		// TODO bruacht ein Package einen Path oder nur einen Name?
		root.setAttribute(XMLAttributes.PATH.getName(), packagePath);
		return doc;
	}
}