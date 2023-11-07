package tool.helper.persistence;

public class SerializerConstant {
    public static final int SERIALIZER_VERSION = 3;

    public static final String TAG_NAME_ROOT = "XModeler";
    public static final String TAG_NAME_VERSION = "Version";
    public static final String TAG_NAME_CATEGORIES = "Categories";
    public static final String TAG_NAME_PROJECTS = "Projects";
    public static final String TAG_NAME_PROJECT = "Project";
    public static final String TAG_NAME_DIAGRAMS = "Diagrams";
    public static final String TAG_NAME_DIAGRAM = "Diagram";
    public static final String TAG_NAME_DIAGRAM_DISPLAY_PROPERTIES = "DiagramDisplayProperties";
    public static final String TAG_NAME_OBJECTS = "Objects";
    public static final String TAG_NAME_OBJECT = "Object";
    public static final String TAG_NAME_LOGS = "Logs";
    public static final String TAG_NAME_OWNERS = "Owners";
    public static final String TAG_NAME_PREFERENCES = "Preferences";
    public static final String TAG_NAME_EDGE = "Edge";
    public static final String TAG_NAME_EDGES = "Edges";
    public static final String TAG_NAME_INTERMEDIATE_POINTS = "IntermediatePoints";
    public static final String TAG_NAME_INTERMEDIATE_POINT = "IntermediatePoint";
    public static final String TAG_NAME_OPERATION = "Operation";
    public static final String TAG_NAME_OPERATIONS = "Operations";
    public static final String TAG_NAME_ATTRIBUTES = "Attributes";
    public static final String TAG_PARAM_NAME = "Param";
    public static final String TAG_NAME_BODY = "Body";
    public static final String TAG_NAME_ATTRIBUTE = "Attribute";
	public static final String TAG_NAME_LABELS = "Labels";
	public static final String TAG_NAME_LABEL = "Label";
	public static final String TAG_NAME_VIEWS = "Views";
	public static final String TAG_NAME_VIEW = "View";

    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_PACKAGE_PATH = "package_path";
    public static final String ATTRIBUTE_REFERENCE = "ref";
    public static final String ATTRIBUTE_LABEL = "label";
    public static final String ATTRIBUTE_COORDINATE_X = "x";
    public static final String ATTRIBUTE_COORDINATE_Y = "y";
    public static final String ATTRIBUTE_LEVEL = "level";
    public static final String ATTRIBUTE_MAX_LEVEL = "maxLevel";
    public static final String ATTRIBUTE_PARENTS = "parents";
    public static final String ATTRIBUTE_TYPE = "type";
    public static final String ATTRIBUTE_LEVEL_TARGET = "level_target";
    public static final String ATTRIBUTE_LEVEL_SOURCE = "level_source";
    public static final String ATTRIBUTE_SOURCE_NODE = "source_node";
    public static final String ATTRIBUTE_TARGET_NODE = "target_node";
    public static final String ATTRIBUTE_OF = "of";
    public static final String ATTRIBUTE_SOURCE_PORT = "source_port";
    public static final String ATTRIBUTE_TARGET_PORT = "target_port";
    public static final String ATTRIBUTE_PARENT_ASSOCIATION = "parent_association";
    public static final String ATTRIBUTE_MULTIPLICITY = "multiplicity";
    public static final String ATTRIBUTE_IS_ABSTRACT = "abstract";
    public static final String ATTRIBUTE_IS_SINGLETON = "singleton";
    public static final String ATTRIBUTE_BODY = "body";
    public static final String ATTRIBUTE_CLASS = "class";
    public static final String ATTRIBUTE_OLD_NAME = "oldName";
    public static final String ATTRIBUTE_NEW_NAME = "newName";
    public static final String ATTRIBUTE_OLD_LEVEL = "oldLevel";
    public static final String ATTRIBUTE_NEW_LEVEL = "newLevel";
    public static final String ATTRIBUTE_NEW_TYPE = "newType";
    public static final String ATTRIBUTE_OLD_TYPE = "oldType";
    public static final String ATTRIBUTE_OLD_MULTIPLICITY = "oldMul";
    public static final String ATTRIBUTE_NEW_MULTIPLICITY = "newMul";
    public static final String ATTRIBUTE_OLD_CLASS = "class_old";
    public static final String ATTRIBUTE_NEW_CLASS = "class_new";
    public static final String ATTRIBUTE_SLOT_NAME = "slotName";
    public static final String ATTRIBUTE_VALUE_TOBE_PARSED = "valueToBeParsed";
    public static final String ATTRIBUTE_FW_NAME = "fwName";
    public static final String ATTRIBUTE_REVERSE_NAME = "reverseName";
    public static final String ATTRIBUTE_T2S_MULTIPLICITY = "multTargetToSource";
    public static final String ATTRIBUTE_S2T_MULTIPLICITY = "multSourceToTarget";
    public static final String ATTRIBUTE_INST_LEVEL_SOURCE = "instLevelSource";
    public static final String ATTRIBUTE_INST_LEVEL_TARGET = "instLevelTarget";
    public static final String ATTRIBUTE_SOURCE_VISIBLE = "sourceVisibleFromTarget";
    public static final String ATTRIBUTE_TARGET_VISIBLE = "targetVisibleFromSource";
    public static final String ATTRIBUTE_IS_SYMMETRIC = "isSymmetric";
    public static final String ATTRIBUTE_IS_TRANSITIVE = "isTransitive";
    public static final String ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET = "accessSourceFromTargetName";
    public static final String ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE = "accessTargetFromSourceName";
    public static final String ATTRIBUTE_DELEGATE_FROM = "delegateFrom";
    public static final String ATTRIBUTE_DELEGATE_TO = "delegateTo";
    public static final String ATTRIBUTE_CLASS_SOURCE = "classSource";
    public static final String ATTRIBUTE_CLASS_TARGET = "classTarget";
	public static final String ATTRIBUTE_TEXT = "text";
    public static final String ATTRIBUTE_ANCHORS = "anchors";
    public static final String ATTRIBUTE_HIDDEN = "hidden";
    public static final String FILE_XML = "xml";
    public static final String ATTRIBUTE_MONITORED = "monitored";
    public static final String ATTRIBUTE_PACKAGE = "package";
    public static final String ATTRIBUTE_PARAM_NAMES = "paramNames";
    public static final String ATTRIBUTE_PARAM_TYPES = "paramTypes";


    public static class EdgeType {
        public static final String ASSOCIATION = "association";
        public static final String DELEGATION = "delegation";
        public static final String INHERITANCE = "inheritance";
        public static final String LINK = "link";

        public static final String ROLEFILLEREDGE = "rolefilleredge";
    }
}
