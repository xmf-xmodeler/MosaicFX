package tool.clients.serializer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FaXML;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.serializer.interfaces.ILog;
import tool.clients.serializer.interfaces.IXmlManager;

import javax.xml.transform.TransformerException;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class LogXmlManager implements ILog, IXmlManager {
    private final XmlHandler xmlHandler;
    FmmlxDiagram diagram;

    public LogXmlManager(FmmlxDiagram fmmlxDiagram) {
        this.xmlHandler = new XmlHandler();
        this.diagram = fmmlxDiagram;
    }

    @Override
    public void add(Node node) {
        Node logs = xmlHandler.getLogsNode();
        try {
            xmlHandler.addLogElement(logs, node);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Node node) {
        //TODO
    }

    @Override
    public List<Node> getAll() {
        //TODO
        return null;
    }

    @Override
    public void back(int diagramId) {
        //TODO
    }

    @Override
    public void forward(int diagramId) {
        //TODO
    }

    public Node createNewLogFromFaXML(FaXML faXML){
        Element node = (Element) xmlHandler.createXmlElement(faXML.getName());
        for(String attName : faXML.getAttributes()){
            node.setAttribute(attName, faXML.getAttributeValue(attName));
        }
        return node;
    }

    public void clearLog() throws TransformerException {
        xmlHandler.clearLogs();
    }

    @Override
    public void backToLatestSave(int diagramId, String diagramLabel) {
        //TODO
    }

    @Override
    public String toString() {
        return "Log{" +
                "xmlLogHandler=" + xmlHandler.toString() +
                '}';
    }

    public void reproduceFromLog(String diagramLabel) {
        Node logs = xmlHandler.getLogsNode();
        Node diagrams = xmlHandler.getDiagramsNode();
        NodeList diagramList = diagrams.getChildNodes();

        Node diagramNode = null;

        for (int i = 0 ; i< diagramList.getLength(); i++){
            if(diagramList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element tmp = (Element) diagramList.item(i);
                if (tmp.getAttribute(XmlConstant.ATTRIBUTE_LABEL).equals(diagramLabel)){
                    diagramNode = tmp;
                }
            }
        }

        NodeList logList = logs.getChildNodes();

        for(int i = 0 ; i<logList.getLength(); i++){
            if(logList.item(i).getNodeType()==Node.ELEMENT_NODE){
                Element logElement = (Element) logList.item(i);
                reproduceDiagramElement(diagramNode, logElement);
            }
        }
    }

    private void reproduceDiagramElement(Node diagramNode, Element logElement) {
        if(diagramNode!= null){
            String tagName = logElement.getTagName();
            switch (tagName) {
                case "addMetaClass": {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
                    String parentPathsString = logElement.getAttribute(XmlConstant.ATTRIBUTE_PARENTS);
                    Vector<String> parents = new Vector<>();
                    if(!parentPathsString.equals("")){
                        String[] parentPathsArray = parentPathsString.split(",");

                        for (String s : parentPathsArray) {
                            String[] parentPathArray = s.split("::");
                            parents.add(parentPathArray[parentPathArray.length - 1]);
                        }
                    }
                    boolean isAbstract = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_ABSTRACT));
                    Coordinate coordinate = getCoordinate(diagramNode, name);
                    int x = (int) Math.round(coordinate.getX());
                    int y = (int) Math.round(coordinate.getY());
                    diagram.getComm().addMetaClass(diagram, name, level, parents, isAbstract, x, y);
                    break;
                }
                case "removeClass" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    diagram.getComm().removeClass(diagram, className, 0);
                    break;
                }
                case "changeClassName" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String newName = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_NAME);
                    diagram.getComm().changeClassName(diagram, name, newName);
                    break;
                }
                case "setClassAbstract" : {

                }
                case "changeParent" : {

                }
                case "addAttribute" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
                    String typePath = logElement.getAttribute(XmlConstant.ATTRIBUTE_TYPE);
                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int upper = Integer.parseInt(multiplicityArray[0]);
                    int under = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                    String[] typePathArray = typePath.split("::");
                    String typeName = typePathArray[typePathArray.length-1];
                    diagram.getComm().addAttribute(diagram, className, name, level, typeName, multiplicity);
                    break;
                }
                case "removeAttribute" : {
                    //TODO
                }
                case "changeAttributeName" : {
                    String oldName = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_NAME);
                    String newName = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    diagram.getComm().changeAttributeName(diagram, className, oldName, newName);
                    break;
                }
                case "changeAttributeLevel" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    int oldLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_LEVEL));
                    int newLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_LEVEL));
                    diagram.getComm().changeAttributeLevel(diagram, className, name, oldLevel, newLevel);
                    break;

                }
                case "changeAttributeType" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String oldType = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_TYPE);
                    String newType = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_TYPE);
                    diagram.getComm().changeAttributeType(diagram, className, name, oldType, newType);
                    break;
                }
                case "changeAttributeMultiplicity" : {
                    //TODO
                }
                case "addOperation2": {
                    String classPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classPath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String body = parseBase64(logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY));
                    String name = "";
                    int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
                    diagram.getComm().addOperation2(diagram, className, level, body);
                    break;
                }
                case "addInstance": {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String ofName = parseOf(logElement.getAttribute(XmlConstant.ATTRIBUTE_OF));
                    int level = getInstanceLevel(diagramNode, name);
                    String parentPathsString = logElement.getAttribute(XmlConstant.ATTRIBUTE_PARENTS);
                    Vector<String> parents = new Vector<>();

                    if(!parentPathsString.equals("")){
                        String[] parentPathsArray = parentPathsString.split(",");

                        for (String s : parentPathsArray) {
                            String[] parentPathArray = s.split("::");
                            parents.add(parentPathArray[parentPathArray.length - 1]);
                        }
                    }
                    boolean isAbstract = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_ABSTRACT));
                    Coordinate coordinate = getCoordinate(diagramNode, name);
                    int x = (int) Math.round(coordinate.getX());
                    int y = (int) Math.round(coordinate.getY());
                    diagram.getComm().addNewInstance(diagram, ofName, name, level, parents, isAbstract, x, y);


                    break;
                }
                case "changeOperationBody" : {
                    //TODO

                }
                case "changeOperationLevel" : {

                }
                case "changeOperationOwner" : {

                }
                case "removeOperation" : {

                }
                case "changeSlotValue" : {

                }
                case "addAssociation" : {

                }
                case "removeAssociation" : {

                }
                case "changeAssociationForwardName" : {

                }
                case "changeAssociationEnd2StartMultiplicity" : {

                }
                case "changeAssociationStart2EndMultiplicity" : {

                }
                case "addLink" : {

                }
                case "removeLink" : {

                }
                case "addDelegation" : {

                }
                default:
                    System.out.println(tagName + " not implemented yet");
            }
        }
    }

    private String parseOf(String ofString) {
        String[] ofStringArray = ofString.split("::");
        return ofStringArray[2];
    }

    private int getInstanceLevel(Node diagramNone, String name) {
        int level = 0;
        NodeList objectList = diagramNone.getChildNodes();

        for (int i = 0 ; i< objectList.getLength() ; i++){
            if (objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element object_tmp = (Element) objectList.item(i);
                if(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name)){
                    level = Integer.parseInt(XmlConstant.ATTRIBUTE_LEVEL);
                }
            }
        }
        return level;
    }

    private String parseBase64(String body) {
        byte[] decodedBytes = Base64.getDecoder().decode(body);
        return new String(decodedBytes);
    }

    private Coordinate getCoordinate(Node diagramNone, String name) {
        Node objectsNode = xmlHandler.getChildWithName(diagramNone, "Objects");
        NodeList objectList = objectsNode.getChildNodes();
        Coordinate coordinate = new Coordinate(0.0, 0.0);

        for (int i = 0 ; i< objectList.getLength() ; i++){
            if (objectList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element object_tmp = (Element) objectList.item(i);
                if(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_NAME).equals(name)){
                    double x = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_X));
                    double y = Double.parseDouble(object_tmp.getAttribute(XmlConstant.ATTRIBUTE_COORDINATE_Y));
                    coordinate.setX(x);
                    coordinate.setY(y);
                }
            }
        }
        return coordinate;
    }

    private class Coordinate {
        double x;
        double y;

        public Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Coordinat{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
