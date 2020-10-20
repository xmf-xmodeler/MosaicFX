package tool.clients.serializer;

import javafx.geometry.Point2D;
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
                    Point2D coordinate = new Point2D(0.0,0.0);
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
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    boolean abstractValue = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_ABSTRACT));
                    diagram.getComm().setClassAbstract(diagram, className, abstractValue);
                    break;

                }
                case "changeParent" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
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

                    diagram.getComm().changeParent(diagram, className, oldParents, newParents);
                    break;
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
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    diagram.getComm().removeAttribute(diagram, className, name, 0);
                    break;
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
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];

                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int upper = Integer.parseInt(multiplicityArray[0]);
                    int under = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                    String multiplicityString1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_MULTIPLICITY);
                    String multiplicitySubString1 = multiplicityString1.substring(4, multiplicityString1.length()-1);
                    String[] multiplicityArray1 =  multiplicitySubString1.split(",");
                    int upper1 = Integer.parseInt(multiplicityArray1[0]);
                    int under1 = Integer.parseInt(multiplicityArray1[1]);
                    boolean upperLimit1 = Boolean.parseBoolean(multiplicityArray1[2]);
                    boolean ordered1 = Boolean.parseBoolean(multiplicityArray1[3]);
                    Multiplicity multiplicity1 = new Multiplicity(upper1, under1, upperLimit1, ordered1, false);

                    diagram.getComm().changeAttributeMultiplicity(diagram, className, name, multiplicity, multiplicity1);
                    break;
                }
                case "addOperation2": {
                    String classPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classPath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String body = parseBase64(logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY));
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
                    Point2D coordinate = new Point2D(0.0,0.0);
                    int x = (int) Math.round(coordinate.getX());
                    int y = (int) Math.round(coordinate.getY());
                    diagram.getComm().addNewInstance(diagram, ofName, name, level, parents, isAbstract, x, y);
                    break;
                }
                case "changeOperationBody" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String className = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String body = parseBase64(logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY));
                    diagram.getComm().changeOperationBody(diagram, className, name, body);
                    break;
                }
                case "changeOperationLevel" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    int oldLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_LEVEL));
                    int newLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_LEVEL));
                    diagram.getComm().changeOperationLevel(diagram, className, name, oldLevel, newLevel);
                    break;
                }
                case "changeOperationOwner" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String oldClasspath = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_CLASS);
                    String[] oldClassPathArray = oldClasspath.split("::");
                    String oldClassName = oldClassPathArray[oldClassPathArray.length-1];

                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];

                    diagram.getComm().changeOperationOwner(diagram, name, oldClassName, className);
                    break;
                }
                case "removeOperation" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    diagram.getComm().removeOperation(diagram, className, name, 0);
                    break;
                }
                case "changeSlotValue" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String slotName = logElement.getAttribute(XmlConstant.ATTRIBUTE_SLOT_NAME);
                    String valueToBeParsed = logElement.getAttribute(XmlConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
                    diagram.getComm().changeSlotValue(diagram, className, slotName, valueToBeParsed);
                    break;
                }
                case "addAssociation" : {
                    String classpath1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_1);
                    String[] classPathArray1 = classpath1.split("::");
                    String className1 = classPathArray1[classPathArray1.length-1];

                    String classpath2 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_2);
                    String[] classPathArray2 = classpath2.split("::");
                    String className2 = classPathArray2[classPathArray2.length-1];

                    String accessSourceFromTargetName = logElement.getAttribute(XmlConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
                    String accessTargetFromSourceName = logElement.getAttribute(XmlConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

                    String fwName = logElement.getAttribute(XmlConstant.ATTRIBUTE_FW_NAME);
                    String reverseName = logElement.getAttribute(XmlConstant.ATTRIBUTE_REVERSE_NAME);

                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_1_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int upper = Integer.parseInt(multiplicityArray[0]);
                    int under = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                    String multiplicityString1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_2_MULTIPLICITY);
                    String multiplicitySubString1 = multiplicityString1.substring(4, multiplicityString1.length()-1);
                    String[] multiplicityArray1 =  multiplicitySubString1.split(",");
                    int upper1 = Integer.parseInt(multiplicityArray1[0]);
                    int under1 = Integer.parseInt(multiplicityArray1[1]);
                    boolean upperLimit1 = Boolean.parseBoolean(multiplicityArray1[2]);
                    boolean ordered1 = Boolean.parseBoolean(multiplicityArray1[3]);
                    Multiplicity multiplicity1 = new Multiplicity(upper1, under1, upperLimit1, ordered1, false);

                    int instLevel1 = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_INST_LEVEL_1));
                    int instLevel2 = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_INST_LEVEL_2));

                    boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_VISIBLE));
                    boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_VISIBLE));

                    boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_SIMETRIC));
                    boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_TRANSITIVE));

                    diagram.getComm().addAssociation(diagram, className1, className2,
                            accessSourceFromTargetName, accessTargetFromSourceName,
                            fwName, reverseName, multiplicity, multiplicity1,
                            instLevel1, instLevel2, sourceVisibleFromTarget,
                            targetVisibleFromSource, isSymmetric, isTransitive);
                    break;
                }
                case "removeAssociation" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    diagram.getComm().removeAssociation(diagram, name, 0);
                    break;
                }
                case "changeAssociationForwardName" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String newFwName = logElement.getAttribute("newFwName");
                    diagram.getComm().changeAssociationForwardName(diagram, name, newFwName);
                    break;
                }
                case "changeAssociationEnd2StartMultiplicity" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int upper = Integer.parseInt(multiplicityArray[0]);
                    int under = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                    diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram, name, multiplicity);
                    break;
                }
                case "changeAssociationStart2EndMultiplicity" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_MULTIPLICITY);
                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
                    String[] multiplicityArray =  multiplicitySubString.split(",");
                    int upper = Integer.parseInt(multiplicityArray[0]);
                    int under = Integer.parseInt(multiplicityArray[1]);
                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
                    Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

                    diagram.getComm().changeAssociationStart2EndMultiplicity(diagram, name, multiplicity);
                    break;
                }
                case "addLink" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String classpath1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_1);
                    String[] classPathArray1 = classpath1.split("::");
                    String className1 = classPathArray1[classPathArray1.length-1];

                    String classpath2 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_2);
                    String[] classPathArray2 = classpath2.split("::");
                    String className2 = classPathArray2[classPathArray2.length-1];

                    diagram.getComm().addAssociationInstance(diagram, className1, className2, name);
                    break;
                }
                case "removeLink" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    //TODO still using id :: diagram.getComm().removeAssociationInstance(diagram, name);
                    break;
                }
                case "addDelegation" : {
                    String delegationFromPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_DELEGATE_FROM);
                    String[] delegationFromPathArray = delegationFromPath.split("::");
                    String delegationFromName = delegationFromPathArray[delegationFromPathArray.length-1];

                    String delegationToPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_DELEGATE_TO);
                    String[] delegationToPathArray = delegationToPath.split("::");
                    String delegationToName = delegationToPathArray[delegationToPathArray.length-1];

                    diagram.getComm().addDelegation(diagram, delegationFromName, delegationToName);
                    break;
                }
                case "setRoleFiller" : {
                    String rolePath = logElement.getAttribute("role");
                    String[] rolePathArray1 = rolePath.split("::");
                    String role = rolePathArray1[rolePathArray1.length-1];

                    String roleFillerPath = logElement.getAttribute("roleFiller");
                    String[] roleFillerPathArray = roleFillerPath.split("::");
                    String roleFiller = roleFillerPathArray[roleFillerPathArray.length-1];

                    diagram.getComm().setRoleFiller(diagram, role, roleFiller);
                    break;

                }
                default:
                    System.out.println(tagName + " not implemented yet");
                    break;
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
}
