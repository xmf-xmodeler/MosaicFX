package tool.clients.fmmlxdiagrams.graphdb;

import java.util.UUID;

class Node {
    protected String name;
    protected String nodeType;
    protected String create;
    protected UUID uuid;
    
    
    enum label
    {   
    	PROJECT,PACKAGE,DIAGRAMM,INSTANCE,
    	
        FIRSTCLASSATTRIBUTE, SECONDCLASSATTRIBUTE,
        
        CONSTRAINT, SLOT,
        
        COMPILEDOPERATION,
        
        DOC,
        
        END,
        
        ASSOCIATION,
    }
 
    public Node (String name, label label)
    {
    	this.name = name;
    	this.nodeType = label.toString().toLowerCase();
        this.uuid = UUID.randomUUID();
        this.create = "Create(:"+this.nodeType+"{name:'"+this.name+"',"
				+ " uuid:'"+this.uuid+"'})";
    }

    public String newAttribute(String attributeName,String attributeValue)
    {
       String s = "MATCH (n:"+nodeType+") SET n."+attributeName+" = '"+attributeValue+"'";
       return s;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        this.name = name;
        String s = "MATCH (n:"+this.nodeType+") where n.uuid = '"+this.uuid+"'  SET n.name = '"+name+"'";
        return s;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getCreate() {
        return create;
    }
    public String getCreate2()
    {
//        System.out.println( create.substring(6));
        return create.substring(6);
    }
}