package tool.helper.persistence;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.serializer.SerializerConstant;
import tool.xmodeler.ControlCenterClient;

public class XMLParser {

	FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
	Document doc;
	Element root;
	String projectPath;
	String projectName;

	public XMLParser(String filePath) {
		doc = XMLUtil.getDocumentFromFile(filePath);
		root = doc.getDocumentElement();
	}

	public void parseXMLDocument() {
		initParsing();
		buildModel();
		Element diagrams = XMLUtil.getChildElement(root, XMLTags.DIAGRAMS.getName());
		NodeList diagramList = diagrams.getChildNodes();

		// TODO refactor extract to method
		for (int i = 0; i < diagramList.getLength() - 1; i++) {
			Node node = diagramList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(XMLTags.DIAGRAM.getName()) ) {
				reconstructDiagram((Element)node);
			}
		}
		
		
		
		
		ControlCenterClient.getClient().getAllProjects();

	}

	private void reconstructDiagram(Element diagram) {
		String diagramName = diagram.getAttribute(XMLAttributes.NAME.getName());
		// TODO is the file used somewhere?
		comm.createFmmlxClassDiagram(projectPath, diagramName, "random", diagramId -> {
			sendDiagramDataToXMF(diagramId, diagram);
		});

	}

	private void sendDiagramDataToXMF(Integer diagramId, Element diagram) {
		Element views = XMLUtil.getChildElement(diagram, XMLTags.VIEWS.getName()); 
		sendDiagramViewStatus(diagramId, views);
		Element diagramsDisplayProperty = XMLUtil.getChildElement(diagram, XMLTags.DIAGRAM_DISPLAY_PROPERTIES.getName());
		sendDiagramDisplayproperties(diagramId, diagramsDisplayProperty);
	}

	//TODO issue table and meta class name haben probleme
	private void sendDiagramDisplayproperties(Integer diagramId, Element diagramDisplayProperties) {
		NodeList properties = diagramDisplayProperties.getChildNodes();
		HashMap<String, Boolean> propertiesMap = new HashMap<>();
		
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			if (property.getNodeType() == Node.ELEMENT_NODE) {
				propertiesMap.put(property.getNodeName(), Boolean.valueOf(property.getTextContent()));
			}
		}
		comm.sendDiagramDisplayOptions(diagramId, propertiesMap);	
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
		comm.sendViewStatus(diagramID, views);
	}

	private void buildModel() {
		// Creates dummy project that hold the model data
		comm.createFmmlxModelBrowser(projectName, "Deserializer", projectPath,
				// if diagram id is returned from XMF the function inside is executed
				diagramId -> {
					sendModelDataToXMF(diagramId);
				});
	}

	private void sendModelDataToXMF(Integer diagramId) {

		Element model = XMLUtil.getChildElement(root, XMLTags.MODEL.getName());
		NodeList logList = model.getChildNodes();
//TODO still necassary?
		comm.setSilent(true);

		for (int i = 0; i < logList.getLength() - 1; i++) {

			if (logList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				// TODO hwo is unsured, that the first operation is done? Callback better here?
				Element modelElement = (Element) logList.item(i);
				parseModelElements(diagramId, modelElement);
			}
			comm.setSilent(false);
		}
	}

//TODO build flightweight factory
	private void parseModelElements(Integer diagramId, Element modelElement) {
		String tagName = modelElement.getTagName();
		switch (tagName) {
		case "addMetaClass": {
			String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
			int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
			boolean isAbstract = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
			comm.addMetaClass(diagramId, name, level, new Vector<>(), isAbstract, 0, 0, false);
			break;
		}
		case "addInstance": {
			String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
			String[] ofStringArray = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_OF).split("::");
			String ofName = ofStringArray[2];
			boolean isAbstract = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
			int level = -2; // for now: magic number for /not found & not needed, one less theh class by
							// default
			try {
				level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
			} catch (Exception e) {
			}
			comm.addNewInstance(diagramId, ofName, name, level, new Vector<String>(), isAbstract, 0, 0, false);
			break;
		}
		case "changeParent": {
			String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
			String[] classPathArray = classpath.split("::");
			String className = classPathArray[classPathArray.length - 1];

			String oldParentPathsString = modelElement.getAttribute("old");
			Vector<String> oldParents = new Vector<>();
			if (!oldParentPathsString.equals("")) {
				String[] oldParentPathsArray = oldParentPathsString.split(",");

				for (String s : oldParentPathsArray) {
					String[] parentPathArray = s.split("::");
					oldParents.add(parentPathArray[parentPathArray.length - 1]);
				}
			}

			String newParentPathsString = modelElement.getAttribute("new");
			Vector<String> newParents = new Vector<>();
			if (!newParentPathsString.equals("")) {
				String[] newParentPathsArray = newParentPathsString.split(",");

				for (String s : newParentPathsArray) {
					String[] newParentPathArray = s.split("::");
					newParents.add(newParentPathArray[newParentPathArray.length - 1]);
				}
			}

			comm.changeParent(diagramId, className, oldParents, newParents);
			break;
		}
		case "addAttribute": {
			String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
			String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
			String[] classPathArray = classpath.split("::");
			String className = classPathArray[classPathArray.length - 1];
			int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
			String typePath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE);
			String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY);
			String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
			String[] multiplicityArray = multiplicitySubString.split(",");
			int upper = Integer.parseInt(multiplicityArray[0]);
			int under = Integer.parseInt(multiplicityArray[1]);
			boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
			boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
			Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

			String[] typePathArray = typePath.split("::");
			String typeName = typePathArray[typePathArray.length - 1];
			comm.addAttribute(diagramId, className, name, level, typeName, multiplicity);
			break;
		}
		case "addOperation": {
			String classPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
			String[] classPathArray = classPath.split("::");
			String className = classPathArray[classPathArray.length - 1];
			String body = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY);
			int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
			comm.addOperation2(diagramId, className, level, body);
			break;
		}
		case "changeSlotValue": {
			String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
			String[] classPathArray = classpath.split("::");
			String className = classPathArray[classPathArray.length - 1];
			String slotName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
			String valueToBeParsed = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
			comm.changeSlotValue(diagramId, className, slotName, valueToBeParsed);
			break;
		}
		case "addAssociation": {
			String classSourceName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);// classPathArray1[classPathArray1.length-1];
			String classpath2 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
			String accessSourceFromTargetName = modelElement
					.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
			String accessTargetFromSourceName = modelElement
					.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

			String fwName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
			String reverseName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);

			Multiplicity multiplicityT2S;
			{
				String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY);
				String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
				String[] multiplicityArray = multiplicitySubString.split(",");
				int min = Integer.parseInt(multiplicityArray[0]);
				int max = Integer.parseInt(multiplicityArray[1]);
				boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
				boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
				multiplicityT2S = new Multiplicity(min, max, upperLimit, ordered, false);
			}

			Multiplicity multiplicityS2T;
			{
				String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY);
				String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
				String[] multiplicityArray = multiplicitySubString.split(",");
				int min = Integer.parseInt(multiplicityArray[0]);
				int max = Integer.parseInt(multiplicityArray[1]);
				boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
				boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
				multiplicityS2T = new Multiplicity(min, max, upperLimit, ordered, false);
			}

			int instLevelSource = Integer
					.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
			int instLevelTarget = Integer
					.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));

			boolean sourceVisibleFromTarget = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
			boolean targetVisibleFromSource = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));

			boolean isSymmetric = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
			boolean isTransitive = Boolean
					.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));

			comm.addAssociation(diagramId, classSourceName, classpath2, accessSourceFromTargetName,
					accessTargetFromSourceName, fwName, reverseName, multiplicityT2S, multiplicityS2T, instLevelSource,
					instLevelTarget, sourceVisibleFromTarget, targetVisibleFromSource, isSymmetric, isTransitive);
			break;
		}
		case "addLink": {
			String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

			String classpath1 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);
			String[] classPathArray1 = classpath1.split("::");
			String className1 = classPathArray1[classPathArray1.length - 1];

			String classpath2 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
			String[] classPathArray2 = classpath2.split("::");
			String className2 = classPathArray2[classPathArray2.length - 1];

			comm.addAssociationInstance(diagramId, className1, className2, name);
			break;
		}
		case "addDelegation": {
			String delegationFromPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM);
			String[] delegationFromPathArray = delegationFromPath.split("::");
			String delegationFromName = delegationFromPathArray[delegationFromPathArray.length - 1];

			String delegationToPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO);
			String[] delegationToPathArray = delegationToPath.split("::");
			String delegationToName = delegationToPathArray[delegationToPathArray.length - 1];
			int delegateToLevel = Integer.parseInt(modelElement.getAttribute("delegateToLevel"));

			comm.addDelegation(diagramId, delegationFromName, delegationToName, delegateToLevel);
			break;
		}
		case "setRoleFiller": {
			String rolePath = modelElement.getAttribute("role");
			String[] rolePathArray1 = rolePath.split("::");
			String role = rolePathArray1[rolePathArray1.length - 1];

			String roleFillerPath = modelElement.getAttribute("roleFiller");
			String[] roleFillerPathArray = roleFillerPath.split("::");
			String roleFiller = roleFillerPathArray[roleFillerPathArray.length - 1];

			comm.setRoleFiller(diagramId, role, roleFiller);
			break;
		}
		case "addEnumeration": {
			String enumName = modelElement.getAttribute("name");
			comm.addEnumeration(diagramId, enumName);
			break;
		}
		case "addEnumerationValue": {
			String enumName = modelElement.getAttribute("enum_name");
			String itemName = modelElement.getAttribute("enum_value_name");
			comm.addEnumerationItem(diagramId, enumName, itemName);
			break;
		}
		case "addConstraint": {
			String path = modelElement.getAttribute("class");
			String constName = modelElement.getAttribute("constName");
			Integer instLevel = Integer.parseInt(modelElement.getAttribute("instLevel"));
			String body = modelElement.getAttribute("body");
			String reason = modelElement.getAttribute("reason");

			// FOR SAVEFILES BEFORE 5/3/22

			if (body.startsWith("@Operation body(classifier : Class,level : Integer):Boolean")) {
				body = body.substring("@Operation body(classifier : Class,level : Integer):Boolean".length());
				body = body.substring(0, body.length() - 3);
			}

			if (reason.startsWith("@Operation reason(classifier : Class,level : Integer):String")) {
				reason = reason.substring("@Operation reason(classifier : Class,level : Integer):String".length());
				reason = reason.substring(0, reason.length() - 3);
			}

			comm.addConstraint(diagramId, path, constName, instLevel, body, reason);
			break;
		}
		default:
			System.out.println(tagName + " not implemented yet.");
			break;
		}
	}

//	private void extracted() {
//		populateDiagram(diagramId);

//		NodeList diagramNodes = getDiagramsElement().getChildNodes();
//		for(int i = 0; i < diagramNodes.getLength(); i++) {
//		    Node diagramNode = diagramNodes.item(i);
//		    if(diagramNode.getNodeType()==Node.ELEMENT_NODE){
//		    	String diagramName = ((Element) diagramNode).getAttribute(SerializerConstant.ATTRIBUTE_LABEL);
//		    	fmmlxDiagramCommunicator.createDiagram(projectName, diagramName, 
//					this.xmlManager.getSourcePath(), 
//					FmmlxDiagramCommunicator.DiagramType.ClassDiagram, false, 
//					localDiagramId -> {
//						sendDiagramViewStatus(localDiagramId, diagramNode, fmmlxDiagramCommunicator);
//						sendDiagramDisplayProperties(localDiagramId, fmmlxDiagramCommunicator);
//						fmmlxDiagramCommunicator.preparePositionInfo(localDiagramId, diagramNode);  			            
//				});
//		    }    
//		}
//	}

	private void initParsing() {
		String version = root.getAttribute(XMLAttributes.VERSION.getName());
		projectPath = root.getAttribute(XMLAttributes.PATH.getName());
		projectName = projectPath.split("::")[1];
		comm.createProject(projectName, projectPath);

	}

}
