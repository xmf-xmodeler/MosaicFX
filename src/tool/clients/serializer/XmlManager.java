package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.xmlManipulator.XmlHandler;
import java.util.*;

/*XMLManager extends XMLHandler which is an interface that makes it easy for us to manipulate XML-Document.
* this class has some specifics functions for saving and loading FMMLxDiagram into or from XML file (Serializer / Deserializer).
*/
public class XmlManager extends XmlHandler {
    private static final String TAG = XmlManager.class.getSimpleName();
    public XmlManager(String sourcePath) {
        super(sourcePath);
    }

    public Element createDiagramElement(String label, String path) {
        Element diagram = createXmlElement(SerializerConstant.TAG_NAME_DIAGRAM);
        diagram.setAttribute(SerializerConstant.ATTRIBUTE_LABEL, label);
        diagram.setAttribute(SerializerConstant.ATTRIBUTE_PACKAGE_PATH, path);
        Element categories = createXmlElement(SerializerConstant.TAG_NAME_CATEGORIES);
        Element owners = createXmlElement(SerializerConstant.TAG_NAME_OWNERS);
        Element objects = createXmlElement(SerializerConstant.TAG_NAME_OBJECTS);
        Element edges = createXmlElement(SerializerConstant.TAG_NAME_EDGES);
        Element labels = createXmlElement(SerializerConstant.TAG_NAME_LABELS);
        Element preferences = createXmlElement(SerializerConstant.TAG_NAME_PREFERENCES);
        addXmlElement(diagram, categories);
        addXmlElement(diagram, owners);
        addXmlElement(diagram, objects);
        addXmlElement(diagram, edges);
        addXmlElement(diagram, labels);
        addXmlElement(diagram, preferences);
        return diagram;
    }

    // Check whether Diagram with certain label already exists
    public boolean diagramIsExist(String label) {
        Node diagrams = getDiagramsElement();

        NodeList diagramList = diagrams.getChildNodes();

        for(int i =0; i<diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) diagramList.item(i);
                if(element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getVersionTextContent() {
    	return getChildWithTag(getRoot(), SerializerConstant.TAG_NAME_VERSION).getTextContent();
    }
     
    public Vector<String> getAllDiagramNames() {
        Vector<String> diagrams = new Vector<>();
        Node diagramsNode = getDiagramsElement();
        NodeList diagramNodeList = diagramsNode.getChildNodes();

        for(int i =0; i< diagramNodeList.getLength(); i++){
            Node tmp = diagramNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                String diagramLabel = ((Element) tmp).getAttribute(SerializerConstant.ATTRIBUTE_LABEL);
                diagrams.add(diagramLabel);
            }
        }
        return diagrams;
    }

    public void addDiagramIntoDiagramsElement(Element diagramsElement, Element newDiagramElement) {
        addXmlElement(diagramsElement, newDiagramElement);
    }

    public void removeDiagram(String label)  {
        Element diagrams = getDiagramsElement();
        NodeList diagramsChildNodes = diagrams.getChildNodes();

        for(int i = 0 ; i< diagramsChildNodes.getLength(); i++){
            if(diagramsChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp_element = (Element) diagramsChildNodes.item(i);
                if(tmp_element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)
                        && tmp_element.getAttribute(SerializerConstant.ATTRIBUTE_LABEL).equals(label)){
                    removeChildElement(diagrams, tmp_element);
                }
            }
        }
    }

    public Element createProjectElement(String packagePath) {
        Element projectElement = createXmlElement(SerializerConstant.TAG_NAME_PROJECT);
        projectElement.setAttribute(SerializerConstant.ATTRIBUTE_NAME,packagePath);
        return projectElement;
    }

    //Check whether project already exists
    public boolean projectIsExist(String packagePath) {
        Node projects = getProjectsElement();
        NodeList projectList = projects.getChildNodes();

        for(int i =0; i< projectList.getLength(); i++){
            Node tmp = projectList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                Element projectElement = (Element) tmp;
                if(projectElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME).equals(packagePath)){
                    return true;
                }
            }
        }
        return false;
    }

    public Element getProjectsElement() {
        Element Root = getRoot();
        return getChildWithTag(Root, SerializerConstant.TAG_NAME_PROJECTS);
    }
    
    public String getProjectName(String projectPath) {
        String[] projectPathSplit= projectPath.split("::");
        return projectPathSplit[1];
    }

    public String getProjectPath() {
        String projectPath = "";
        List<Node> projectList = getAllProjects();
        if(projectList.size()==1){
            Element tmp = (Element) projectList.get(0);
            projectPath = tmp.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
        }
        return projectPath;
    }

    //Return list of all projects form documents
    public List<Node> getAllProjects(){
        List<Node> projects = new ArrayList<>();
        Node projectsNode = getProjectsElement();
        NodeList projectNodeList = projectsNode.getChildNodes();

        for(int i =0; i< projectNodeList.getLength(); i++){
            Node tmp = projectNodeList.item(i);
            if(tmp.getNodeType()==Node.ELEMENT_NODE){
                projects.add(tmp);
            }
        }
        return projects;
    }
    
    public Element getDiagramDisplayPropertiesElement() {
    	Element diagrams = getDiagramsElement();
    	Element diagram = getChildWithTag(diagrams, SerializerConstant.TAG_NAME_DIAGRAM);
    	Element diagramDisplayProperties = getChildWithTag(diagram,SerializerConstant.TAG_NAME_DIAGRAM_DISPLAY_PROPERTIES);
    	return diagramDisplayProperties;
    }

    public void addProject(Element element) {
        Element projects = getProjectsElement();
        addXmlElement(projects, element);
    }

    public Element createObjectElement(String objectPath, Integer x, Integer y, Boolean hidden) {
        Element object = createXmlElement(SerializerConstant.TAG_NAME_OBJECT);
        object.setAttribute(SerializerConstant.ATTRIBUTE_REFERENCE, objectPath);
        object.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X, x+"");
        object.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        object.setAttribute(SerializerConstant.ATTRIBUTE_HIDDEN, hidden+"");
        return object;
    }

    public void addObject(Element diagramElement, Element element) {
        Element objects = getObjectsElement(diagramElement);
        addXmlElement(objects, element);
    }

    public Element getObjectsElement(Element diagramsElement){
        return getChildWithTag(diagramsElement, SerializerConstant.TAG_NAME_OBJECTS);
    }

    public Element getDiagramsElement(){
        Element Root = getRoot();
        return getChildWithTag(Root, SerializerConstant.TAG_NAME_DIAGRAMS);
    }
    
    public void alignObjects(Element diagramElement, int diagramID, FmmlxDiagramCommunicator communicator) {
        Node objectsNode = getChildWithTag(diagramElement, SerializerConstant.TAG_NAME_OBJECTS);
        NodeList objectList = objectsNode.getChildNodes();
        for(int i = 0 ; i < objectList.getLength(); i++){
            if(objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) objectList.item(i);
                double x = Double.parseDouble(tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                double y = Double.parseDouble(tmp.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                boolean hidden = "true".equals(tmp.getAttribute(SerializerConstant.ATTRIBUTE_HIDDEN));
                String objectPath = tmp.getAttribute(SerializerConstant.ATTRIBUTE_REFERENCE);
                communicator.sendCurrentPosition(diagramID, objectPath, (int)Math.round(x), (int)Math.round(y), hidden);
            }
        }
    }

    public Element createLabelElement(String key, String ownerID, int localID, float x, float y) {
        Element label = createXmlElement(SerializerConstant.TAG_NAME_LABEL);
        label.setAttribute("ownerID", ownerID);
        label.setAttribute("localID", localID+"");
        label.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X, x+"");
        label.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y, y+"");
        return label;
    }

    public void addLabel(Element diagramElement, Element newElement) {
        Element labels = getLabelsElement(diagramElement);
        addXmlElement(labels, newElement);
    }

    private Element getLabelsElement(Element diagramNode) {
        return getChildWithTag(diagramNode, SerializerConstant.TAG_NAME_LABELS);
    }

    public void alignLabels(Element diagramElement_XML, FmmlxDiagram fmmlxDiagram) {
        Vector<DiagramEdgeLabel<?>>labels = fmmlxDiagram.getLabels();
        for(DiagramEdgeLabel<?> label : labels){
            Point2D initCoordinate = new Point2D(label.getRelativeX(), label.getRelativeY());
            Point2D coordinate = getLabelCoordinate(diagramElement_XML, label, initCoordinate);
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

    public boolean validateLabelName(String name) {
        if (name.equals("")) {
            return false;
        } else if (checkFirstStringIsDigit(name)) {
            return false;
        } else return !name.contains(" ");
    }

    private boolean checkFirstStringIsDigit(String name) {
        char[] c = name.toCharArray();
        return Character.isDigit(c[0]);
    }

    private Point2D getLabelCoordinate(Element diagramElement, DiagramEdgeLabel<?> label, Point2D initCoordinate) {
        Element labelsElement = getLabelsElement(diagramElement);
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
    }

    public Element createEdgeXmlElement(Vector<Object> intermediatePoints, String ref, Vector<Object>ports){
        PortRegion sourcePort = getSourcePort(ports);
        PortRegion targetPort = getTargetPort(ports);

        Element edgeElement = createXmlElement(SerializerConstant.TAG_NAME_EDGE);
        edgeElement.setAttribute(SerializerConstant.ATTRIBUTE_SOURCE_PORT, sourcePort+"");
        edgeElement.setAttribute(SerializerConstant.ATTRIBUTE_TARGET_PORT, targetPort+"");
        edgeElement.setAttribute(SerializerConstant.ATTRIBUTE_REFERENCE, ref);

        Element intermediatePointsNode = createXmlElement(SerializerConstant.TAG_NAME_INTERMEDIATE_POINTS);
        for(Object intermediatePointObject : intermediatePoints){
            Vector<Object> points = (Vector<Object>) intermediatePointObject;
            Element intermediatePoint = createXmlElement(SerializerConstant.TAG_NAME_INTERMEDIATE_POINT);
            intermediatePoint.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X, points.get(1)+"");
            intermediatePoint.setAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y, points.get(2)+"");
            intermediatePointsNode.appendChild(intermediatePoint);
        }
        addXmlElement(edgeElement, intermediatePointsNode);
        return edgeElement;
    }

    public Element createAssociationXmlElement(String name, String diagramPath, HashMap<String, Object> edgeInfo) {
        String ref = diagramPath+"::"+name;
        String type = SerializerConstant.EdgeType.ASSOCIATION;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(SerializerConstant.ATTRIBUTE_TYPE, type);
        edge.setAttribute(SerializerConstant.ATTRIBUTE_NAME, name);
        edge.setAttribute(SerializerConstant.ATTRIBUTE_PARENT_ASSOCIATION, "VOID");
        return edge ;
    }

    public Element createLinkXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = SerializerConstant.EdgeType.LINK;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(SerializerConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createInheritanceXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = SerializerConstant.EdgeType.INHERITANCE;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(SerializerConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createRoleFillerEdgeXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = SerializerConstant.EdgeType.ROLEFILLEREDGE;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(SerializerConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public Element createDelegationXmlElement(String ref, HashMap<String, Object> edgeInfo) {
        String type = SerializerConstant.EdgeType.DELEGATION;

        Element edge = createEdgeXmlElement((Vector<Object>) edgeInfo.get("IntermediatePoints"), ref, (Vector<Object>) edgeInfo.get("Ports"));
        edge.setAttribute(SerializerConstant.ATTRIBUTE_TYPE, type);
        return edge;
    }

    public void addEdge(Element diagramElement, Element newElement) {
        if(newElement!=null){
            Element edges = getEdgesElement(diagramElement);
            addXmlElement(edges, newElement);
        }
    }

    private Element getEdgesElement(Element diagramNode){
        return getChildWithTag(diagramNode, SerializerConstant.TAG_NAME_EDGES);
    }

    private PortRegion getTargetPort(Vector<Object> ports) {
        if(ports!=null && ports.size()>0) {
            for (Object port : ports) {
                Vector<Object> portInfo = (Vector<Object>) port;
                if (((String) portInfo.get(0)).trim().equals("endNode")) {
                    return PortRegion.valueOf(((String) portInfo.get(1)).trim());
                }
            }
        }
        return null;
    }

    private PortRegion getSourcePort(Vector<Object> ports) {
        if(ports!=null && ports.size()>0){
            for (Object port : ports){
                Vector<Object> portInfo = (Vector<Object>) port;
                if(((String)portInfo.get(0)).trim().equals("startNode")){
                    return PortRegion.valueOf(((String) portInfo.get(1)).trim());
                }
            }
        }
        return null;
    }

    public void alignEdges(Element diagramElement, int diagramID, FmmlxDiagramCommunicator communicator) {

        Element edgesNode = getEdgesElement(diagramElement);
        NodeList edgeList = edgesNode.getChildNodes();

        for(int i = 0 ; i < edgeList.getLength(); i++){
            if(edgeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element edgeElement = (Element) edgeList.item(i);
                String edgePath = edgeElement.getAttribute(SerializerConstant.ATTRIBUTE_REFERENCE);
                String sourcePort = edgeElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_PORT);
                String targetPort = edgeElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_PORT);
                Node intermediatePointsNode = getChildWithTag(edgeElement, SerializerConstant.TAG_NAME_INTERMEDIATE_POINTS);
                NodeList intermediatePointList = intermediatePointsNode.getChildNodes();

                Vector<Point2D> intermediatePoints = new Vector<>();
                for(int k = 0 ; k<intermediatePointList.getLength(); k++){
                    if(intermediatePointList.item(k).getNodeType()==Node.ELEMENT_NODE){
                        Element intermediatePointElement = (Element) intermediatePointList.item(k);
                        double x = Double.parseDouble(intermediatePointElement.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_X));
                        double y = Double.parseDouble(intermediatePointElement.getAttribute(SerializerConstant.ATTRIBUTE_COORDINATE_Y));
                        Point2D point2D = new Point2D(x, y);
                        intermediatePoints.add(point2D);
                    }
                }
                if(intermediatePointList.getLength()>0 || !sourcePort.equals("null") || !targetPort.equals("null")) {
                    communicator.sendEdgePositionsFromXml(diagramID, edgePath, intermediatePoints, sourcePort, targetPort);
                }
            }
        }
    }

    public Element createNewLogFromFaXML(FaXML faXML){
        Element element = createXmlElement(faXML.getName());
        for(String attName : faXML.getAttributes()){
            element.setAttribute(attName, faXML.getAttributeValue(attName));
        }
        return element;
    }

    public void addLog(Element parent, Element newElement) {
        addXmlElement(parent, newElement);
    }

    public void clearLog() {
        Element rootElement = getRoot();
        Element logs = getChildWithTag(rootElement, SerializerConstant.TAG_NAME_LOGS);
        removeAllChildren(logs);
    }

    public Element getLogs() {
        Element Root = getRoot();
        return getChildWithTag(Root, SerializerConstant.TAG_NAME_LOGS);
    }

    //this recreate all diagram-components based on the order in the diagram's log
    public void reproduceFromLog(Integer newDiagramID) {
        Node logs = getLogs();
        NodeList logList = logs.getChildNodes();
        FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
        try {
            comm.setSilent(true);
            for(int i = 0 ; i<logList.getLength(); i++){
                if(logList.item(i).getNodeType()==Node.ELEMENT_NODE){
                    Element logElement = (Element) logList.item(i);
                    reproduceDiagramElement(comm, newDiagramID, logElement);
                }
            }
        } finally {
            comm.setSilent(false);
        }
    }

    //recreate diagram component based on the tag of the xml-element that stored in diagram's log
    private void reproduceDiagramElement(FmmlxDiagramCommunicator comm, Integer diagramID, Element logElement) {
        String tagName = logElement.getTagName();
        switch (tagName) {
            case "addMetaClass": {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                String parentPathsString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_PARENTS);
                Vector<String> parents = new Vector<>();
                if(!parentPathsString.equals("")){
                    String[] parentPathsArray = parentPathsString.split(",");

                    for (String s : parentPathsArray) {
                        String[] parentPathArray = s.split("::");
                        parents.add(parentPathArray[parentPathArray.length - 1]);
                    }
                }
                boolean isAbstract = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
                comm.addMetaClass(diagramID, name, level, parents, isAbstract, 0, 0, false);
                break;
            }
            case "removeClass" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                comm.removeClass(diagramID, className, 0);
                break;
            }
            case "changeClassName" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String newName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_NAME);
                comm.changeClassName(diagramID, name, newName);
                break;
            }
            case "setClassAbstract" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                boolean abstractValue = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
                comm.setClassAbstract(diagramID, className, abstractValue);
                break;

            }
            case "changeParent" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];

                String oldParentPathsString = logElement.getAttribute("old");
                Vector<String> oldParents = new Vector<>();
                if(!oldParentPathsString.equals("")){
                    String[] oldParentPathsArray = oldParentPathsString.split(",");

                    for (String s : oldParentPathsArray) {
                        String[] parentPathArray = s.split("::");
                        oldParents.add(parentPathArray[parentPathArray.length - 1]);
                    }
                }

                String newParentPathsString = logElement.getAttribute("new");
                Vector<String> newParents = new Vector<>();
                if(!newParentPathsString.equals("")){
                    String[] newParentPathsArray = newParentPathsString.split(",");

                    for (String s : newParentPathsArray) {
                        String[] newParentPathArray = s.split("::");
                        newParents.add(newParentPathArray[newParentPathArray.length - 1]);
                    }
                }

                comm.changeParent(diagramID, className, oldParents, newParents);
                break;
            }
            case "addAttribute" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                String typePath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE);
                String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY);
                String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                String[] multiplicityArray =  multiplicitySubString.split(",");
                int upper = Integer.parseInt(multiplicityArray[0]);
                int under = Integer.parseInt(multiplicityArray[1]);
                boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                String[] typePathArray = typePath.split("::");
                String typeName = typePathArray[typePathArray.length-1];
                comm.addAttribute(diagramID, className, name, level, typeName, multiplicity);
                break;
            }
            case "removeAttribute" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                comm.removeAttribute(diagramID, className, name, 0);
                break;
            }
            case "changeAttributeName" : {
                String oldName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_NAME);
                String newName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                comm.changeAttributeName(diagramID, className, oldName, newName);
                break;
            }
            case "changeAttributeLevel" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                int oldLevel = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_LEVEL));
                int newLevel = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_LEVEL));
                comm.changeAttributeLevel(diagramID, className, name, oldLevel, newLevel);
                break;

            }
            case "changeAttributeType" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String oldType = logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_TYPE);
                String newType = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_TYPE);
                comm.changeAttributeType(diagramID, className, name, oldType, newType);
                break;
            }
            case "changeAttributeMultiplicity" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];

                String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_MULTIPLICITY);
                String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                String[] multiplicityArray =  multiplicitySubString.split(",");
                int upper = Integer.parseInt(multiplicityArray[0]);
                int under = Integer.parseInt(multiplicityArray[1]);
                boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                String multiplicityString1 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_MULTIPLICITY);
                String multiplicitySubString1 = multiplicityString1.substring(4, multiplicityString1.length()-1);
                String[] multiplicityArray1 =  multiplicitySubString1.split(",");
                int upper1 = Integer.parseInt(multiplicityArray1[0]);
                int under1 = Integer.parseInt(multiplicityArray1[1]);
                boolean upperLimit1 = Boolean.parseBoolean(multiplicityArray1[2]);
                boolean ordered1 = Boolean.parseBoolean(multiplicityArray1[3]);
                Multiplicity multiplicity1 = new Multiplicity(upper1, under1, upperLimit1, ordered1, false);

                comm.changeAttributeMultiplicity(diagramID, className, name, multiplicity, multiplicity1);
                break;
            }
            case "addOperation": {
                String classPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classPath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String body = logElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY);
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                comm.addOperation2(diagramID, className, level, body);
                break;
            }
            case "addInstance": {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String ofName = parseOf(logElement.getAttribute(SerializerConstant.ATTRIBUTE_OF));
                String parentPathsString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_PARENTS);
                Vector<String> parents = new Vector<>();

                if(!parentPathsString.equals("")){
                    String[] parentPathsArray = parentPathsString.split(",");

                    for (String s : parentPathsArray) {
                        String[] parentPathArray = s.split("::");
                        parents.add(parentPathArray[parentPathArray.length - 1]);
                    }
                }
                boolean isAbstract = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
                comm.addNewInstance(diagramID, ofName, name, -2, parents, isAbstract, 0, 0, false);
                break;
            }
            case "changeOperationBody" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String className = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String body = parseBase64(logElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY));
                comm.changeOperationBody(diagramID, className, name, body);
                break;
            }
            case "changeOperationLevel" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                int oldLevel = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_LEVEL));
                int newLevel = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_LEVEL));
                comm.changeOperationLevel(diagramID, className, name, oldLevel, newLevel);
                break;
            }
            case "changeOperationOwner" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

                String oldClasspath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_OLD_CLASS);
                String[] oldClassPathArray = oldClasspath.split("::");
                String oldClassName = oldClassPathArray[oldClassPathArray.length-1];

                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NEW_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];

                comm.changeOperationOwner(diagramID, oldClassName, name, className);
                break;
            }
            case "removeOperation" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                comm.removeOperation(diagramID, className, name, 0);
                break;
            }
            case "changeSlotValue" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String slotName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
                String valueToBeParsed = logElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
                comm.changeSlotValue(diagramID, className, slotName, valueToBeParsed);
                break;
            }
            case "addAssociation" : {
                String classSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);//classPathArray1[classPathArray1.length-1];
                String classpath2 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
                String accessSourceFromTargetName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
                String accessTargetFromSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

                String fwName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
                String reverseName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);

                Multiplicity multiplicityT2S; {
                    String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int min = Integer.parseInt(multiplicityArray[0]);
                    int max = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    multiplicityT2S = new Multiplicity(min, max, upperLimit, ordered, false);
                }

                Multiplicity multiplicityS2T; {
                    String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int min = Integer.parseInt(multiplicityArray[0]);
                    int max = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    multiplicityS2T = new Multiplicity(min, max, upperLimit, ordered, false);
                }

                int instLevelSource = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
                int instLevelTarget = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));

                boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
                boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));

                boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
                boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));

                comm.addAssociation(diagramID, classSourceName, classpath2,
                        accessSourceFromTargetName, accessTargetFromSourceName,
                        fwName, reverseName, multiplicityT2S, multiplicityS2T,
                        instLevelSource, instLevelTarget, sourceVisibleFromTarget,
                        targetVisibleFromSource, isSymmetric, isTransitive);
                break;
            }
            case "removeAssociation" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                comm.removeAssociation(diagramID, name, 0);
                break;
            }
            case "changeAssociationForwardName" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String newFwName = logElement.getAttribute("newFwName");
                comm.changeAssociationForwardName(diagramID, name, newFwName);
                break;
            }
            case "changeAssociationEnd2StartMultiplicity" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

                String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY);
                String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                String[] multiplicityArray =  multiplicitySubString.split(",");
                int upper = Integer.parseInt(multiplicityArray[0]);
                int under = Integer.parseInt(multiplicityArray[1]);
                boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                comm.changeAssociationEnd2StartMultiplicity(diagramID, name, multiplicity);
                break;
            }
            case "changeAssociationStart2EndMultiplicity" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

                String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY);
                String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                String[] multiplicityArray =  multiplicitySubString.split(",");
                int upper = Integer.parseInt(multiplicityArray[0]);
                int under = Integer.parseInt(multiplicityArray[1]);
                boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                comm.changeAssociationStart2EndMultiplicity(diagramID, name, multiplicity);
                break;
            }
            case "addLink" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

                String classpath1 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);
                String[] classPathArray1 = classpath1.split("::");
                String className1 = classPathArray1[classPathArray1.length-1];

                String classpath2 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
                String[] classPathArray2 = classpath2.split("::");
                String className2 = classPathArray2[classPathArray2.length-1];

                comm.addAssociationInstance(diagramID, className1, className2, name);
                break;
            }
            case "removeLink" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

                String classpath1 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);
                String[] classPathArray1 = classpath1.split("::");
                String className1 = classPathArray1[classPathArray1.length-1];

                String classpath2 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
                String[] classPathArray2 = classpath2.split("::");
                String className2 = classPathArray2[classPathArray2.length-1];

                comm.removeAssociationInstance(diagramID, name, className1, className2);
                break;
            }
            case "addDelegation" : {
                String delegationFromPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM);
                String[] delegationFromPathArray = delegationFromPath.split("::");
                String delegationFromName = delegationFromPathArray[delegationFromPathArray.length-1];

                String delegationToPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO);
                String[] delegationToPathArray = delegationToPath.split("::");
                String delegationToName = delegationToPathArray[delegationToPathArray.length-1];
                int delegateToLevel = Integer.parseInt(logElement.getAttribute("delegateToLevel"));

                comm.addDelegation(diagramID, delegationFromName, delegationToName, delegateToLevel);
                break;
            }
            case "setRoleFiller" : {
                String rolePath = logElement.getAttribute("role");
                String[] rolePathArray1 = rolePath.split("::");
                String role = rolePathArray1[rolePathArray1.length-1];

                String roleFillerPath = logElement.getAttribute("roleFiller");
                String[] roleFillerPathArray = roleFillerPath.split("::");
                String roleFiller = roleFillerPathArray[roleFillerPathArray.length-1];

                comm.setRoleFiller(diagramID, role, roleFiller);
                break;
            }
            case "addEnumeration" : {
                String enumName = logElement.getAttribute("name");
                comm.addEnumeration(diagramID, enumName);
                break;
            }
            case "addEnumerationValue" : {
                String enumName = logElement.getAttribute("enum_name");
                String itemName = logElement.getAttribute("enum_value_name");
                comm.addEnumerationItem(diagramID, enumName, itemName);
                break;
            }
            case "levelRaiseAll" : {
                String amountStr = logElement.getAttribute("amount");
                int raiseValue = Integer.parseInt(amountStr);

                if(raiseValue>0){
                    comm.levelRaiseAll(diagramID);
                } else {
                    comm.levelLowerAll(diagramID);
                }
                break;
            }
            case "addConstraint" : {
                String path = logElement.getAttribute("class");
                String constName = logElement.getAttribute("constName");
                Integer instLevel = Integer.parseInt(logElement.getAttribute("instLevel"));
                String body = logElement.getAttribute("body");
                String reason = logElement.getAttribute("reason");
                
                // FOR SAVEFILES BEFORE 5/3/22
                					
                if(body.startsWith("@Operation body(classifier : Class,level : Integer):Boolean")) {
                	body = body.substring("@Operation body(classifier : Class,level : Integer):Boolean".length());
                	body = body.substring(0, body.length()-3);
                }
                
                if(reason.startsWith("@Operation reason(classifier : Class,level : Integer):String")) {
                	reason = reason.substring("@Operation reason(classifier : Class,level : Integer):String".length());
                	reason = reason.substring(0, reason.length()-3);
                }
                
                comm.addConstraint(diagramID, path, constName, instLevel, body, reason);
                break;
            }
            case "removeConstraint" : {
                String path = logElement.getAttribute("class");
                String name = logElement.getAttribute("name");
                comm.removeConstraint(diagramID, path, name);
                break;
            }
            default:
                System.out.println(tagName + " not implemented yet. Check "+TAG);
                break;
        }
    }

    private String parseOf(String ofString) {
        String[] ofStringArray = ofString.split("::");
        return ofStringArray[2];
    }

    private String parseBase64(String body) {
        byte[] decodedBytes = Base64.getDecoder().decode(body);
        return new String(decodedBytes);
    }
}
