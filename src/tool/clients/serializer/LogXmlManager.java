package tool.clients.serializer;

import javafx.geometry.Point2D;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.FaXML;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.TimeOutException;

import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class LogXmlManager {
    private static final String TAG = LogXmlManager.class.getSimpleName();
    private final XmlHandler xmlHandler;

    public LogXmlManager(XmlHandler xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    public void add(Element parent, Element newElement) {
        xmlHandler.addXmlElement(parent, newElement);
    }

    public void remove(Element element) {
        //TODO
    }

    public List<Node> getAll() {
        //TODO
        return null;
    }

    public void back(int diagramId) {
        //TODO
    }

    public void forward(int diagramId) {
        //TODO
    }

    public Element getLogs() {
        Element Root = xmlHandler.getRoot();
        return xmlHandler.getChildWithTag(Root, XmlConstant.TAG_NAME_LOGS);
    }

    public Element createNewLogFromFaXML(FaXML faXML){
        Element element = xmlHandler.createXmlElement(faXML.getName());
        for(String attName : faXML.getAttributes()){
            element.setAttribute(attName, faXML.getAttributeValue(attName));
        }
        return element;
    }

    public void clearLog() {
        Element rootElement = xmlHandler.getRoot();
        Element logs = xmlHandler.getChildWithTag(rootElement, XmlConstant.TAG_NAME_LOGS);
        xmlHandler.removeAllChildren(logs);
    }

    public void backToLatestSave(int diagramId, String diagramLabel) {
        //TODO
    }

    @Override
    public String toString() {
        return "Log{" +
                "xmlLogHandler=" + xmlHandler.toString() +
                '}';
    }

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

    private void reproduceDiagramElement(FmmlxDiagramCommunicator comm, Integer diagramID, Element logElement) {
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
                    comm.addMetaClass(diagramID, name, level, parents, isAbstract, x, y);
                    break;
                }
                case "removeClass" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    comm.removeClass(diagramID, className, 0);
                    break;
                }
                case "changeClassName" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String newName = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_NAME);
                    comm.changeClassName(diagramID, name, newName);
                    break;
                }
                case "setClassAbstract" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    boolean abstractValue = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_ABSTRACT));
                    comm.setClassAbstract(diagramID, className, abstractValue);
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

                    comm.changeParent(diagramID, className, oldParents, newParents);
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
                    comm.addAttribute(diagramID, className, name, level, typeName, multiplicity);
                    break;
                }
                case "removeAttribute" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    comm.removeAttribute(diagramID, className, name, 0);
                    break;
                }
                case "changeAttributeName" : {
                    String oldName = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_NAME);
                    String newName = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    comm.changeAttributeName(diagramID, className, oldName, newName);
                    break;
                }
                case "changeAttributeLevel" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    int oldLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_LEVEL));
                    int newLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_LEVEL));
                    comm.changeAttributeLevel(diagramID, className, name, oldLevel, newLevel);
                    break;

                }
                case "changeAttributeType" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String oldType = logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_TYPE);
                    String newType = logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_TYPE);
                    comm.changeAttributeType(diagramID, className, name, oldType, newType);
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

                    comm.changeAttributeMultiplicity(diagramID, className, name, multiplicity, multiplicity1);
                    break;
                }
                case "addOperation": {
                    String classPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classPath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String body = logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY);
                    int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
                    comm.addOperation2(diagramID, className, level, body);
                    break;
                }
                case "addOperation2": {
                    String classPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classPath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String body = parseBase64(logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY));
                    int level = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_LEVEL));
                    comm.addOperation2(diagramID, className, level, body);
                    break;
                }
                case "addInstance": {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String ofName = parseOf(logElement.getAttribute(XmlConstant.ATTRIBUTE_OF));
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
                    comm.addNewInstance(diagramID, ofName, name, parents, isAbstract, x, y);
                    break;
                }
                case "changeOperationBody" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String className = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String body = parseBase64(logElement.getAttribute(XmlConstant.ATTRIBUTE_BODY));
                    comm.changeOperationBody(diagramID, className, name, body);
                    break;
                }
                case "changeOperationLevel" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    int oldLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_OLD_LEVEL));
                    int newLevel = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_NEW_LEVEL));
                    comm.changeOperationLevel(diagramID, className, name, oldLevel, newLevel);
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

                    comm.changeOperationOwner(diagramID, oldClassName, name, className);
                    break;
                }
                case "removeOperation" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    comm.removeOperation(diagramID, className, name, 0);
                    break;
                }
                case "changeSlotValue" : {
                    String classpath = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS);
                    String[] classPathArray = classpath.split("::");
                    String className = classPathArray[classPathArray.length-1];
                    String slotName = logElement.getAttribute(XmlConstant.ATTRIBUTE_SLOT_NAME);
                    String valueToBeParsed = logElement.getAttribute(XmlConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
                    comm.changeSlotValue(diagramID, className, slotName, valueToBeParsed);
                    break;
                }
                case "addAssociation" : {
                    String classSourceName = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_SOURCE);//classPathArray1[classPathArray1.length-1];
                    String classpath2 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_TARGET);
                    String accessSourceFromTargetName = logElement.getAttribute(XmlConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
                    String accessTargetFromSourceName = logElement.getAttribute(XmlConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

                    String fwName = logElement.getAttribute(XmlConstant.ATTRIBUTE_FW_NAME);
                    String reverseName = logElement.getAttribute(XmlConstant.ATTRIBUTE_REVERSE_NAME);

                    
                    Multiplicity multiplicityT2S; {
	                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_T2S_MULTIPLICITY);
	                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
	                    String[] multiplicityArray =  multiplicitySubString.split(",");
	                    int min = Integer.parseInt(multiplicityArray[0]);
	                    int max = Integer.parseInt(multiplicityArray[1]);
	                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
	                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
	                    multiplicityT2S = new Multiplicity(min, max, upperLimit, ordered, false);
                    }

                    Multiplicity multiplicityS2T; {
	                    String multiplicityString = logElement.getAttribute(XmlConstant.ATTRIBUTE_S2T_MULTIPLICITY);
	                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
	                    String[] multiplicityArray =  multiplicitySubString.split(",");
	                    int min = Integer.parseInt(multiplicityArray[0]);
	                    int max = Integer.parseInt(multiplicityArray[1]);
	                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
	                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
	                    multiplicityS2T = new Multiplicity(min, max, upperLimit, ordered, false);
                    }

                    int instLevelSource = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
                    int instLevelTarget = Integer.parseInt(logElement.getAttribute(XmlConstant.ATTRIBUTE_INST_LEVEL_TARGET));

                    boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_SOURCE_VISIBLE));
                    boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_TARGET_VISIBLE));

                    boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_SYMMETRIC));
                    boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(XmlConstant.ATTRIBUTE_IS_TRANSITIVE));

                    comm.addAssociation(diagramID, classSourceName, classpath2,
                            accessSourceFromTargetName, accessTargetFromSourceName,
                            fwName, reverseName, multiplicityT2S, multiplicityS2T,
                            instLevelSource, instLevelTarget, sourceVisibleFromTarget,
                            targetVisibleFromSource, isSymmetric, isTransitive);
                    break;
                }
                case "removeAssociation" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    comm.removeAssociation(diagramID, name, 0);
                    break;
                }
                case "changeAssociationForwardName" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);
                    String newFwName = logElement.getAttribute("newFwName");
                    comm.changeAssociationForwardName(diagramID, name, newFwName);
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

                    comm.changeAssociationEnd2StartMultiplicity(diagramID, name, multiplicity);
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

                    comm.changeAssociationStart2EndMultiplicity(diagramID, name, multiplicity);
                    break;
                }
                case "addLink" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String classpath1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_SOURCE);
                    String[] classPathArray1 = classpath1.split("::");
                    String className1 = classPathArray1[classPathArray1.length-1];

                    String classpath2 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_TARGET);
                    String[] classPathArray2 = classpath2.split("::");
                    String className2 = classPathArray2[classPathArray2.length-1];

                    comm.addAssociationInstance(diagramID, className1, className2, name);
                    break;
                }
                case "removeLink" : {
                    String name = logElement.getAttribute(XmlConstant.ATTRIBUTE_NAME);

                    String classpath1 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_SOURCE);
                    String[] classPathArray1 = classpath1.split("::");
                    String className1 = classPathArray1[classPathArray1.length-1];

                    String classpath2 = logElement.getAttribute(XmlConstant.ATTRIBUTE_CLASS_TARGET);
                    String[] classPathArray2 = classpath2.split("::");
                    String className2 = classPathArray2[classPathArray2.length-1];

                    comm.removeAssociationInstance(diagramID, name, className1, className2);
                    break;
                }
                case "addDelegation" : {
                    String delegationFromPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_DELEGATE_FROM);
                    String[] delegationFromPathArray = delegationFromPath.split("::");
                    String delegationFromName = delegationFromPathArray[delegationFromPathArray.length-1];

                    String delegationToPath = logElement.getAttribute(XmlConstant.ATTRIBUTE_DELEGATE_TO);
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
                    try {
                    	comm.addEnumerationItem(diagramID, enumName, itemName);
                    } catch (TimeOutException e) {
                        e.printStackTrace();
                    }
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
