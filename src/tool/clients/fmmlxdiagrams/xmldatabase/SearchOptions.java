package tool.clients.fmmlxdiagrams.xmldatabase;

public enum SearchOptions {
    MODEL_NAME("Model[contains(@name"),
    
    META_CLASS_NAME("Model//addMetaClass[contains(@name"),
    
    INSTANCE_NAME("Model//addInstance[contains(@name"),
    
    ENUMERATION_NAME("Model//addEnumeration[contains(@name"),
    
    ATTRIBUTE_NAME("Model//addAttribute[contains(@name"),
    
    OPERATION_NAME("Model//addOperation[contains(@name"),
    
    OPERATION_BODY("Model//addOperation[contains(@body"),
    
    ASSOCIATION_FW_NAME("Model//addAssociation[contains(@fwName"),
    
    LINK_NAME("Model//addLink[contains(@name"),
    
    DIAGRAM_NAME("Diagrams//Diagram[contains(@name");

    private final String xpath;

    SearchOptions(String xpath) {
        this.xpath = xpath;
    }

    public String getXpath() {
        return xpath;
    }
}

