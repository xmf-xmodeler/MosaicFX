package tool.clients.importer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.SerializerConstant;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class ProtocolHandler {
    private final AbstractPackageViewer diagram;
    public List<String> problems;
    private final Vector<FmmlxObject> objects;
    private final Vector<FmmlxAssociation> associations;

    public ProtocolHandler(AbstractPackageViewer diagram) {
        this.diagram = diagram;
        this.problems = new ArrayList<>();
        this.objects = diagram.getObjects();
        this.associations = diagram.getAssociations();
    }

    public void readLogs(Node logsNode) {
        NodeList logList = logsNode.getChildNodes();
        for(int i = 0; i< logList.getLength(); i++){
            if(logList.item(i).getNodeType()== Node.ELEMENT_NODE){
                Element logNode = (Element) logList.item(i);
                checkProblem(logNode);
            }
        }
    }

    public void checkProblem(Element logElement) {
        String tagName = logElement.getTagName();
        switch (tagName) {
            case "addMetaClass": {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                // check name
                for(FmmlxObject object : objects){
                    if(object.getName().equals(name)){
                        //Check level
                        if(object.getLevel() != level){
                            problems.add(ImporterStrings.PROBLEM_CLASS_DIFFERENT_LEVEL +object.getName());
                        }
                    }
                }
                break;
            }
            // maybe there will be no problem
            case "changeParent" : {
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

                for(FmmlxObject object : objects){
                    if(object.getName().equals(className)){
                        //check att
                        Vector<FmmlxAttribute> attributes = object.getAllAttributes();
                        //check name
                        for(FmmlxAttribute attribute : attributes){
                            if(attribute.getName().equals(name)){
                                //check level
                                if(attribute.getLevel()!=level){
                                    problems.add(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_LEVEL +"class "+className+", "+"attribute "+ name);
                                }
                                //check type
                                if(!attribute.getType().equals(typeName)){
                                    problems.add(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_TYPE +"class "+className+", "+"attribute "+ name);
                                }
                                //check multiplicity
                                if(!attribute.getMultiplicity().toString().equals(multiplicity.toString())){
                                    problems.add(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_MULTIPLICITY +"class "+className+", "+"attribute "+ name);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case "addOperation": {
                String classPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classPath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String body = parseBase64(logElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY));
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));

                break;
            }
            case "changeSlotValue" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String slotName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
                String valueToBeParsed = logElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED);

                for(FmmlxObject object : objects){
                    if(object.getName().equals(className)){
                        Vector<FmmlxSlot> slots = object.getAllSlots();
                        for(FmmlxSlot slot : slots){
                            if(slot.getName().equals(slotName)){
                                if(!slot.getValue().equals(valueToBeParsed)){
                                    problems.add(ImporterStrings.PROBLEM_SLOT_DIFFERENT_VALUE +"class "+className+", "+"slot "+ slotName);
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
                break;
//            }
//            case "addAssociation" : {
//                String classSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);//classPathArray1[classPathArray1.length-1];
//                String classpath2 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
//                String accessSourceFromTargetName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
//                String accessTargetFromSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);
//
//                String fwName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
//                String reverseName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);
//
//
//                Multiplicity multiplicityT2S; {
//                    String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY);
//                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
//                    String[] multiplicityArray =  multiplicitySubString.split(",");
//                    int min = Integer.parseInt(multiplicityArray[0]);
//                    int max = Integer.parseInt(multiplicityArray[1]);
//                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
//                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
//                    multiplicityT2S = new Multiplicity(min, max, upperLimit, ordered, false);
//                }
//
//                Multiplicity multiplicityS2T; {
//                    String multiplicityString = logElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY);
//                    String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length()-1);
//                    String[] multiplicityArray =  multiplicitySubString.split(",");
//                    int min = Integer.parseInt(multiplicityArray[0]);
//                    int max = Integer.parseInt(multiplicityArray[1]);
//                    boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
//                    boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
//                    multiplicityS2T = new Multiplicity(min, max, upperLimit, ordered, false);
//                }
//
//                int instLevelSource = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
//                int instLevelTarget = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));
//
//                boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
//                boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));
//
//                boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
//                boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));
//
//                break;
//            }
//            case "addLink" : {
//                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
//
//                String classpath1 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);
//                String[] classPathArray1 = classpath1.split("::");
//                String className1 = classPathArray1[classPathArray1.length-1];
//
//                String classpath2 = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
//                String[] classPathArray2 = classpath2.split("::");
//                String className2 = classPathArray2[classPathArray2.length-1];
//
//                break;
//            }
//            case "addDelegation" : {
//                String delegationFromPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM);
//                String[] delegationFromPathArray = delegationFromPath.split("::");
//                String delegationFromName = delegationFromPathArray[delegationFromPathArray.length-1];
//
//                String delegationToPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO);
//                String[] delegationToPathArray = delegationToPath.split("::");
//                String delegationToName = delegationToPathArray[delegationToPathArray.length-1];
//                int delegateToLevel = Integer.parseInt(logElement.getAttribute("delegateToLevel"));
//
//                break;
//            }
//            case "setRoleFiller" : {
//                String rolePath = logElement.getAttribute("role");
//                String[] rolePathArray1 = rolePath.split("::");
//                String role = rolePathArray1[rolePathArray1.length-1];
//
//                String roleFillerPath = logElement.getAttribute("roleFiller");
//                String[] roleFillerPathArray = roleFillerPath.split("::");
//                String roleFiller = roleFillerPathArray[roleFillerPathArray.length-1];
//
//                break;
//            }
//            case "addConstraint" : {
//                String path = logElement.getAttribute("class");
//                String constName = logElement.getAttribute("constName");
//                Integer instLevel = Integer.parseInt(logElement.getAttribute("instLevel"));
//                String body = logElement.getAttribute("body");
//                String reason = logElement.getAttribute("reason");
//                break;
//            }
            default:
                break;
        }
    }

    private String parseBase64(String body) {
        byte[] decodedBytes = Base64.getDecoder().decode(body);
        return new String(decodedBytes);
    }

    private String parseOf(String ofString) {
        String[] ofStringArray = ofString.split("::");
        return ofStringArray[2];
    }

    public void clearProblems(){
        problems.clear();
    }


    public List<String> getProblems() {
        return problems;
    }

    public void executeMerge(Node logsNode, FmmlxDiagramCommunicator comm) {
        System.out.println("execute merge");
        NodeList logList = logsNode.getChildNodes();
        for(int i = 0; i< logList.getLength(); i++){
            if(logList.item(i).getNodeType()== Node.ELEMENT_NODE){
                Element logNode = (Element) logList.item(i);
                mergeLog(logNode, comm, diagram.getID());
            }
        }
    }

    private void mergeLog(Element logElement, FmmlxDiagramCommunicator comm, int diagramID) {
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
                comm.mergeMetaClass(diagramID, name, level, parents, isAbstract, 0, 0, false);
                break;
            }
            case "changeParent" : {
                String classpath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classpath.split("::");
                String className = classPathArray[classPathArray.length-1];

                Vector<String> oldParents = new Vector<>();
                Vector<String> parents1 = new Vector<>();
                for(FmmlxObject object : objects){
                    if(object.getName().equals(className)){
                        parents1.addAll(object.getParentsPaths());
                    }
                }
                String newParentPathsString = logElement.getAttribute("new");
                Vector<String> parents2 = new Vector<>();
                if(!newParentPathsString.equals("")){
                    String[] newParentPathsArray = newParentPathsString.split(",");

                    for (String s : newParentPathsArray) {
                        String[] newParentPathArray = s.split("::");
                        parents2.add(newParentPathArray[newParentPathArray.length - 1]);
                    }
                }
                Vector<String> mergedParents = new Vector<>(parents1);
                for(String parent : parents2){
                    if(!mergedParents.contains(parent)){
                        mergedParents.add(parent);
                    }
                }
                comm.mergeParent(diagramID, className, oldParents, mergedParents);
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
                comm.mergeAttribute(diagramID, className, name, level, typeName, multiplicity);
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
                comm.mergeNewInstance(diagramID, ofName, name, parents, isAbstract, 0, 0, false);
                break;
            }
            case "addEnumeration" : {
                String enumName = logElement.getAttribute("name");
                comm.mergeEnumeration(diagramID, enumName);
                break;
            }
//            case "addEnumerationValue" : {
//                String enumName = logElement.getAttribute("enum_name");
//                String itemName = logElement.getAttribute("enum_value_name");
//                try {
//                    comm.mergeEnumerationItem(diagramID, enumName, itemName);
//                } catch (TimeOutException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
            default:
                break;
        }
    }
}
