package tool.clients.importer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.serializer.SerializerConstant;

import java.util.*;

public class ProtocolHandler {
    private final AbstractPackageViewer diagram;
    public List<Conflict> conflicts;
    private final Vector<FmmlxObject> objects;
    private final Vector<FmmlxAssociation> associations;
    private final Vector<FmmlxLink> links;
    private final Vector<DelegationEdge> delegationEdges;
    private final Vector<RoleFillerEdge> roleFillerEdges;
    private final String projectPath;

    public ProtocolHandler(AbstractPackageViewer diagram) {
        this.diagram = diagram;
        this.conflicts = new ArrayList<>();
        this.objects = diagram.getObjects();
        this.associations = diagram.getAssociations();
        this.links = diagram.getAssociationInstance();
        this.delegationEdges = diagram.getDelegations();
        this.roleFillerEdges = diagram.getRoleFillerEdges();
        this.projectPath = diagram.getPackagePath();
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
                String conflictType = ImporterStrings.ConflictType.CLASS;
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                // check name
                for(FmmlxObject object : objects){
                    if(object.getName().equals(name)){
                        //Check level
                        if(object.getLevel() != level){
                            Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_LEVEL);
                            conflict.putIn(ImporterStrings.CLASS_NAME, object.getName());
                            conflicts.add(conflict);
                        }
                        break;
                    }
                }
                break;
            }
            // maybe there will be no problem
            case "changeParent" : {
                break;
            }
            case "addAttribute" : {
                String conflictType = ImporterStrings.ConflictType.ATTRIBUTE;
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                String typeName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE));
                Multiplicity multiplicity = getMultiplicityFromXml(logElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY));

                for(FmmlxObject object : objects){
                    if(object.getName().equals(className)){
                        //check att
                        Vector<FmmlxAttribute> attributes = object.getAllAttributes();
                        //check name
                        for(FmmlxAttribute attribute : attributes){
                            if(attribute.getName().equals(name)){
                                //check level
                                if(attribute.getLevel()!=level){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_LEVEL);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    conflicts.add(conflict);
                                }
                                //check type
                                if(!attribute.getType().equals(typeName)){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_TYPE);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    conflicts.add(conflict);
                                }
                                //check multiplicity
                                if(!attribute.getMultiplicity().toString().equals(multiplicity.toString())){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_MULTIPLICITY);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.ATTRIBUTE_NAME, name);
                                    conflicts.add(conflict);
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case "changeSlotValue" : {
                String conflictType = ImporterStrings.ConflictType.SLOT;
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                String slotName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
                String valueToBeParsed = checkPathExists(logElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED), projectPath);

                for(FmmlxObject object : objects){
                    if(object.getName().equals(className)){
                        Vector<FmmlxSlot> slots = object.getAllSlots();
                        for(FmmlxSlot slot : slots){
                            if(slot.getName().equals(slotName)){
                                if(!slot.getValue().equals(valueToBeParsed)){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_VALUE);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.SLOT_NAME, slotName);
                                    conflicts.add(conflict);
                                    break;
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
                String conflictType = ImporterStrings.ConflictType.OPERATION;
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String type = logElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE);
                boolean monitored = Boolean.parseBoolean(logElement.getAttribute(SerializerConstant.ATTRIBUTE_MONITORED));
                String body = replacePath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY));
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                for(FmmlxObject object: objects){

                    if(object.getName().equals(className)){
                        Vector<FmmlxOperation> operations = object.getAllOperations();
                        for(FmmlxOperation operation: operations){
                            if(operation.getName().equals(name)){
                                if(operation.getLevel()!=level){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_LEVEL);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                if(!operation.getType().equals(checkPathExists(type, projectPath))){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_TYPE);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                if(operation.isMonitored()!=monitored){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_MONITORED);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                String paramNames = logElement.getAttribute(SerializerConstant.ATTRIBUTE_PARAM_NAMES);
                                String[] paramNameList = paramNames.split(",");
                                if(!compareNames(paramNameList, operation.getParamNames())){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_PARAM_NAMES);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                String paramTypes = logElement.getAttribute(SerializerConstant.ATTRIBUTE_PARAM_TYPES);
                                String[] paramTypeList = paramTypes.split(",");
                                if(!compareTypes(paramTypeList, operation.getParamTypes())){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_PARAM_TYPES);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                if(!operation.getBody().equals(replacePath(body))){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_PARAM_TYPES);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflict.putIn(ImporterStrings.OPERATION_NAME, name);
                                    conflicts.add(conflict);
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            }
            // TODO Bug
            case "addAssociation" : {
                String conflictType = ImporterStrings.ConflictType.ASSOCIATION;
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
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_REVERSE_NAME);
                                    conflictAssocIn(conflict, fwName, sourceName, targetName);
                                    conflicts.add(conflict);
                                }
                            } else if(ass.getReverseName() != null || ass.getReverseName() == null && reverseName != null){
                                if(ass.getReverseName() != null){
                                    if(!ass.getReverseName().equals("-1")){
                                        Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_REVERSE_NAME);
                                        conflictAssocIn(conflict, fwName, sourceName, targetName);
                                        conflicts.add(conflict);
                                    }
                                } else {
                                    assert reverseName != null;
                                    if(!reverseName.equals("-1")){
                                        Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_REVERSE_NAME);
                                        conflictAssocIn(conflict, fwName, sourceName, targetName);
                                        conflicts.add(conflict);
                                    }
                                }
                            }
                            if (!ass.getAccessNameStartToEnd().equals(accessTargetFromSourceName)) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_ACCESS_SOURCE_FROM_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (!ass.getAccessNameEndToStart().equals(accessSourceFromTargetName)) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_ACCESS_TARGET_FROM_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.getLevelSource()!=(instLevelSource)) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_INST_LEVEL_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.getLevelTarget()!=(instLevelTarget)) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_INST_LEVEL_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (!ass.getMultiplicityStartToEnd().toString().equals(multiplicityS2T.toString())){
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_MULTIPLICITY_S2E);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (!ass.getMultiplicityEndToStart().toString().equals(multiplicityT2S.toString())){
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_MULTIPLICITY_E2S);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.isSourceVisible()!=sourceVisibleFromTarget) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_SOURCE_VISIBLE_FROM_TARGET);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.isTargetVisible()!=targetVisibleFromSource) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_TARGET_VISIBLE_FROM_SOURCE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.isSymmetric()!=isSymmetric) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_SYMMETRIC);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                            if (ass.isTransitive()!=isTransitive) {
                                Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_TRANSITIVE);
                                conflictAssocIn(conflict, fwName, sourceName, targetName);
                                conflicts.add(conflict);
                            }
                        }
                    } else if (ass.targetEnd.getNode().getName().equals(sourceName) && ass.sourceEnd.getNode().getName().equals(targetName)){
                        if(ass.getName().equals(fwName)){
                            Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_REVERSE_SOURCE_AND_TARGET);
                            conflictAssocIn(conflict, fwName, sourceName, targetName);
                            conflicts.add(conflict);
                        }
                    }
                }

                break;
            }
            case "addLink" : {
                String conflictType = ImporterStrings.ConflictType.LINK;
                String name = logElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
                String className1 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE));
                String className2 = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET));

                for(FmmlxLink link:links){
                    if(link.sourceEnd.getNode().getName().equals(className2) && (link.targetEnd.getNode().getName().equals(className1))){
                        if(link.getName().equals(name)){
                            Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_REVERSE_SOURCE_AND_TARGET);
                            conflictAssocIn(conflict, name, className1, className2);
                            conflicts.add(conflict);
                            break;
                        }
                    }
                }
                break;
            }
            case "addDelegation" : {
                String conflictType = ImporterStrings.ConflictType.DELEGATION;
                String delegationFromName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM));
                String delegationToName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO));
                int delegateToLevel = Integer.parseInt(logElement.getAttribute("delegateToLevel"));

                for(DelegationEdge delegationEdge:delegationEdges){
                    if(delegationEdge.sourceEnd.getNode().getName().equals(delegationFromName) && (delegationEdge.targetEnd.getNode().getName().equals(delegationToName))){
                        if(delegationEdge.level!=delegateToLevel){
                            Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_DELEGATION_LEVEL);
                            conflict.putIn(ImporterStrings.SOURCE_CLASS, delegationFromName);
                            conflict.putIn(ImporterStrings.TARGET_CLASS, delegationToName);
                            conflicts.add(conflict);
                        }
                    }
                    if(delegationEdge.sourceEnd.getNode().getName().equals(delegationToName) && (delegationEdge.targetEnd.getNode().getName().equals(delegationFromName))){
                        Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_REVERSE_SOURCE_AND_TARGET);
                        conflict.putIn(ImporterStrings.SOURCE_CLASS, delegationFromName);
                        conflict.putIn(ImporterStrings.TARGET_CLASS, delegationToName);
                        conflicts.add(conflict);
                    }
                }
                break;
            }
            case "setRoleFiller" : {
                String conflictType = ImporterStrings.ConflictType.ROLE_FILLER;
                String role =getNameFromPath(logElement.getAttribute("role"));
                String roleFiller = getNameFromPath(logElement.getAttribute("roleFiller"));

                for(RoleFillerEdge roleFillerEdge:roleFillerEdges){
                    if(roleFillerEdge.sourceEnd.getNode().getName().equals(roleFiller) && (roleFillerEdge.targetEnd.getNode().getName().equals(role))){
                        Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_REVERSE_SOURCE_AND_TARGET);
                        conflict.putIn(ImporterStrings.SOURCE_CLASS, role);
                        conflict.putIn(ImporterStrings.TARGET_CLASS, roleFiller);
                        conflicts.add(conflict);
                        break;
                    }
                }
                break;
            }

            case "addConstraint" : {
                String conflictType = ImporterStrings.ConflictType.CONSTRAINT;
                String path = logElement.getAttribute("class");
                String className = getNameFromPath(path);
                String constName = logElement.getAttribute("constName");
                int instLevel = Integer.parseInt(logElement.getAttribute("instLevel"));
                String body = logElement.getAttribute("body");
                String reason = logElement.getAttribute("reason");

                for(FmmlxObject object:objects){
                    if(object.getName().equals(className)){
                        Vector<Constraint> constraints = object.getConstraints();
                        for(Constraint constraint: constraints){
                            if(constraint.getName().equals(constName)){
                                if(constraint.getLevel()!=instLevel){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_INST_LEVEL);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflicts.add(conflict);
                                }
                                if(!constraint.getBodyFull().equals(replacePath(body))){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_BODY);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflicts.add(conflict);
                                }
                                if(!constraint.getReasonFull().equals(replacePath(reason))){
                                    Conflict conflict = new Conflict(conflictType, ImporterStrings.PROBLEM_DIFFERENT_REASON);
                                    conflict.putIn(ImporterStrings.CLASS_NAME, className);
                                    conflicts.add(conflict);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private String replacePath(String string) {
        String result = string;
        if(string.contains(projectPath)){
            String currentPackagePath = diagram.getPackagePath();
            result = result.replace(projectPath, currentPackagePath);
        }
        return result;
    }

    private boolean compareNames(String[] paramNameList, Vector<String> paramNames) {
        for(String s : paramNameList){
            if(!paramNames.contains(s)){
                return false;
            }
        }
        return true;
    }

    private boolean compareTypes(String[] paramTypeList, Vector<String> paramTypes) {
        if(paramTypeList.length!=paramTypes.size()){
            return false;
        }
        for(String p : paramTypeList){
            if(p.contains(projectPath)){
                String r = p.replace(projectPath, diagram.getPackagePath());
                if(!paramTypes.contains(r)){
                    return false;
                }
            }
        }
    return true;
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

    public void clearProblems(){
        conflicts.clear();
    }


    public List<Conflict> getConflicts() {
        return conflicts;
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
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));

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
                        parents2.add(getNameFromPath(s));
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
                String ofName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_OF));
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
            case "addEnumerationValue" : {
                String enumName = logElement.getAttribute("enum_name");
                String itemName = checkPathExists(logElement.getAttribute("enum_value_name"), projectPath);
                try {
                    comm.mergeEnumerationItem(diagramID, enumName, itemName);
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "addAssociation" : {

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
            case "addConstraint" : {
                String classPath = projectPath+"::"+getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                String constName = logElement.getAttribute("constName");
                Integer instLevel = Integer.parseInt(logElement.getAttribute("instLevel"));
                String body = replacePath(logElement.getAttribute("body"));
                String reason = replacePath(logElement.getAttribute("reason"));
                comm.mergeConstraint(diagramID, classPath, constName, instLevel, body, reason);
                break;
            }
            case "addDelegation" : {
                String delegationFromName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM));
                String delegationToName = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO));
                int delegateToLevel = Integer.parseInt(logElement.getAttribute("delegateToLevel"));

                comm.mergeDelegation(diagramID, delegationFromName, delegationToName, delegateToLevel);
                break;
            }
            case "setRoleFiller" : {
                String role = getNameFromPath(logElement.getAttribute("role"));
                String roleFiller = getNameFromPath(logElement.getAttribute("roleFiller"));

                comm.mergeRoleFiller(diagramID, role, roleFiller);
                break;
            }
            case "changeSlotValue" : {
                String className = getNameFromPath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS));
                String packagePath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_PACKAGE);
                String slotName = logElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
                String value = logElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
                String valueToBeParsed = checkPathExists(value, packagePath);
                comm.mergeSlotValue(diagramID, className, slotName, valueToBeParsed);
                break;
            }
            case "addOperation": {
                String classPath = logElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
                String[] classPathArray = classPath.split("::");
                String className = classPathArray[classPathArray.length-1];
                String body = replacePath(logElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY));
                int level = Integer.parseInt(logElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
                comm.mergeOperation2(diagramID, className, level, body);
                break;
            }
            default:
                break;
        }
    }

    private String checkPathExists(String toBeParsed1, String packagePath) {
        String result = toBeParsed1;
        String currentPackagePath = "";
        String[] path = toBeParsed1.split("::");
        if (path.length>1){
            if(toBeParsed1.contains(packagePath)){
                currentPackagePath = diagram.getPackagePath();
                result = result.replace(packagePath, currentPackagePath);
            }
        }
        return result;
    }
}
