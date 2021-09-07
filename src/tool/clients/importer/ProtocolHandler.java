package tool.clients.importer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.SerializerConstant;

import java.util.*;

public class ProtocolHandler {
    private final AbstractPackageViewer diagram;
    public List<Conflict> problems;
    private final Vector<FmmlxObject> objects;
    private final Vector<FmmlxAssociation> associations;
    private final Vector<FmmlxLink> links;
    private final Vector<DelegationEdge> delegationEdges;
    private final Vector<RoleFillerEdge> roleFillerEdges;

    public ProtocolHandler(AbstractPackageViewer diagram) {
        this.diagram = diagram;
        this.problems = new ArrayList<>();
        this.objects = diagram.getObjects();
        this.associations = diagram.getAssociations();
        this.links = diagram.getAssociationInstance();
        this.delegationEdges = diagram.getDelegations();
        this.roleFillerEdges = diagram.getRoleFillerEdges();
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
                            Conflict conflict = new Conflict(ImporterStrings.PROBLEM_CLASS_DIFFERENT_LEVEL);
                            conflict.putIn(ImporterStrings.CLASS_NAME, object.getName());
                            problems.add(conflict);
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
                Multiplicity multiplicity = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY));

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
                                    Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_LEVEL);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    problems.add(conflict);
                                }
                                //check type
                                if(!attribute.getType().equals(typeName)){
                                    Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_TYPE);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    problems.add(conflict);
                                }
                                //check multiplicity
                                if(!attribute.getMultiplicity().toString().equals(multiplicity.toString())){
                                    Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ATTRIBUTE_DIFFERENT_MULTIPLICITY);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    problems.add(conflict);
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
                                    Conflict conflict = new Conflict(ImporterStrings.PROBLEM_SLOT_DIFFERENT_VALUE);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.SLOT_NAME, slotName);
                                    problems.add(conflict);
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            case "addAssociation" : {
                String sourceName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE));
                String targetName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET));

                String accessSourceFromTargetName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
                String accessTargetFromSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

                String fwName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
                String reverseName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);

                Multiplicity multiplicityT2S = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY));
                Multiplicity multiplicityS2T = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY));

                int instLevelSource = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
                int instLevelTarget = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));

                boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
                boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));

                boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
                boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));

                for(FmmlxAssociation ass : associations){
                    if(ass.targetEnd.getNode().getName().equals(targetName) && ass.sourceEnd.getNode().getName().equals(sourceName)) {
                        if (ass.getName().equals(fwName)) {
                            if(ass.getReverseName()!=null && reverseName!=null){
                                if (!ass.getReverseName().equals(reverseName)) {
                                    Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_REVERSE_NAME);
                                    conflictAssocIn(conflict, fwName, sourceName, targetName);
                                    problems.add(conflict);
                                }
                            } else if(ass.getReverseName() != null || ass.getReverseName() == null && reverseName != null){
                                if(ass.getReverseName() != null){
                                    if(!ass.getReverseName().equals("-1")){
                                        Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_REVERSE_NAME);
                                        conflictAssocIn(conflict, fwName, sourceName, targetName);
                                        problems.add(conflict);
                                    }
                                } else {
                                    assert reverseName != null;
                                    if(!reverseName.equals("-1")){
                                        Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_REVERSE_NAME);
                                        conflictAssocIn(conflict, fwName, sourceName, targetName);
                                        problems.add(conflict);
                                    }
                                }
                            }
                            if (!ass.getAccessNameStartToEnd().equals(accessTargetFromSourceName)) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_ACCESS_SOURCE_FROM_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (!ass.getAccessNameEndToStart().equals(accessSourceFromTargetName)) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_ACCESS_TARGET_FROM_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.getLevelSource()!=(instLevelSource)) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_INST_LEVEL_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.getLevelTarget()!=(instLevelTarget)) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_INST_LEVEL_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (!ass.getMultiplicityStartToEnd().toString().equals(multiplicityS2T.toString())){
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_MULTIPLICITY_S2E);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (!ass.getMultiplicityEndToStart().toString().equals(multiplicityT2S.toString())){
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_MULTIPLICITY_E2S);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.isSourceVisible()!=sourceVisibleFromTarget) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_SOURCE_VISIBLE_FROM_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.isTargetVisible()!=targetVisibleFromSource) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_TARGET_VISIBLE_FROM_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.isSymmetric()!=isSymmetric) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_SYMMETRIC);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                            if (ass.isTransitive()!=isTransitive) {
                                Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_DIFFERENT_TRANSITIVE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                problems.add(conflict);
                            }
                        }
                    } else if (ass.targetEnd.getNode().getName().equals(sourceName) && ass.sourceEnd.getNode().getName().equals(targetName)){
                        if(ass.getName().equals(fwName)){
                            Conflict conflict = new Conflict(ImporterStrings.PROBLEM_ASSOCIATION_REVERSE_SOURCE_AND_TARGET);
                            conflictAssocIn(conflict, fwName, sourceName, targetName);
                            problems.add(conflict);
                        }
                    }
                }

                break;
            }
            case "addLink" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String className1 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE));
                String className2 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET));

                for(FmmlxLink link:links){
                    if(link.sourceEnd.getNode().getName().equals(className2) && (link.targetEnd.getNode().getName().equals(className1))){
                        if(link.getName().equals(name)){
                            Conflict conflict = new Conflict(ImporterStrings.PROBLEM_LINK_REVERSE_SOURCE_AND_TARGET);
                            conflictAssocIn(conflict, name, className1, className2);
                            problems.add(conflict);
                        }
                    }
                }
                break;
            }
            case "addDelegation" : {
                String delegationFromName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM));
                String delegationToName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO));
                int delegateToLevel = Integer.parseInt(logElement.getAttribute("delegateToLevel"));

                for(DelegationEdge delegationEdge:delegationEdges){
                    if(delegationEdge.sourceEnd.getNode().getName().equals(delegationFromName) && (delegationEdge.targetEnd.getNode().getName().equals(delegationToName))){
                        if(delegationEdge.level!=delegateToLevel){
                            Conflict conflict = new Conflict(ImporterStrings.PROBLEM_LINK_REVERSE_SOURCE_AND_TARGET);
                            conflict.putIn(ImporterStrings.SOURCE_CLASS, delegationFromName);
                            conflict.putIn(ImporterStrings.TARGET_CLASS, delegationToName);
                            problems.add(conflict);
                        }
                    }
                    if(delegationEdge.sourceEnd.getNode().getName().equals(delegationToName) && (delegationEdge.targetEnd.getNode().getName().equals(delegationFromName))){
                        Conflict conflict = new Conflict(ImporterStrings.PROBLEM_LINK_REVERSE_SOURCE_AND_TARGET);
                        conflict.putIn(ImporterStrings.SOURCE_CLASS, delegationFromName);
                        conflict.putIn(ImporterStrings.TARGET_CLASS, delegationToName);
                        problems.add(conflict);
                    }
                }
                break;
            }
            case "setRoleFiller" : {
                String rolePath = logElement.getAttribute("role");
                String[] rolePathArray1 = rolePath.split("::");
                String role = rolePathArray1[rolePathArray1.length-1];

                String roleFillerPath = logElement.getAttribute("roleFiller");
                String[] roleFillerPathArray = roleFillerPath.split("::");
                String roleFiller = roleFillerPathArray[roleFillerPathArray.length-1];

                for(RoleFillerEdge roleFillerEdge:roleFillerEdges){
                    if(roleFillerEdge.sourceEnd.getNode().getName().equals(roleFiller) && (roleFillerEdge.targetEnd.getNode().getName().equals(role))){
                        Conflict conflict = new Conflict(ImporterStrings.PROBLEM_LINK_REVERSE_SOURCE_AND_TARGET);
                        conflict.putIn(ImporterStrings.SOURCE_CLASS, role);
                        conflict.putIn(ImporterStrings.TARGET_CLASS, roleFiller);
                        problems.add(conflict);
                    }
                }

                break;
            }
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

    private Multiplicity getMultiplicityFromXml(String attribute) {
        String multiplicitySubString = attribute.substring(4, attribute.length()-1);
        String[] multiplicityArray =  multiplicitySubString.split(",");
        int min = Integer.parseInt(multiplicityArray[0]);
        int max = Integer.parseInt(multiplicityArray[1]);
        boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
        boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
        return new Multiplicity(min, max, upperLimit, ordered, false);
    }

    private String getNameFromPath(String path) {
        String[] classSourcePathArray = path.split("::");
        return classSourcePathArray[classSourcePathArray.length-1];
    }

    private void conflictAssocIn(Conflict conflict, String fwName, String sourceName, String targetName) {
        conflict.putIn(ImporterStrings.ASSOCIATION_NAME, fwName);
        conflict.putIn(ImporterStrings.SOURCE_CLASS, sourceName);
        conflict.putIn(ImporterStrings.TARGET_CLASS, targetName);
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


    public List<Conflict> getProblems() {
        return problems;
    }

    public void executeMerge(Node logsNode, FmmlxDiagramCommunicator comm) {
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
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                Multiplicity multiplicity = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY));

                String typeName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE));
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
            case "addAssociation" : {
                String projectPath = diagram.getPackagePath();
                String classSourceName = projectPath+"::"+getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE));
                String classTargetName = projectPath+"::"+getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET));

                String accessSourceFromTargetName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
                String accessTargetFromSourceName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

                String fwName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
                String reverseName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);


                Multiplicity multiplicityT2S = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY));
                Multiplicity multiplicityS2T = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY));
                int instLevelSource = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
                int instLevelTarget = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));

                boolean sourceVisibleFromTarget= Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
                boolean targetVisibleFromSource = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));

                boolean isSymmetric = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
                boolean isTransitive = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));

                comm.mergeAssociation(diagramID, classSourceName, classTargetName,
                        accessSourceFromTargetName, accessTargetFromSourceName,
                        fwName, reverseName, multiplicityT2S, multiplicityS2T,
                        instLevelSource, instLevelTarget, sourceVisibleFromTarget,
                        targetVisibleFromSource, isSymmetric, isTransitive);
                break;
            }
            case "addLink" : {
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String className1 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE));
                String className2 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET));

                comm.mergeAssociationInstance(diagramID, className1, className2, name);
                break;
            }
            default:
                break;
        }
    }
}
