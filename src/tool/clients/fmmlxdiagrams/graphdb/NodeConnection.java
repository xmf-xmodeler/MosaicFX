package tool.clients.fmmlxdiagrams.graphdb;

class NodeConnection
{

    private String  connectionType;
    private String connectionName;
    
    enum connection
    {
        PACKAGE_OF, DIAGRAMM_OF, CONTAINS_IN,VISUALISED_IN,
        
        OF,
    }

    public NodeConnection(connection connection) {
        this.connectionType = connection.toString().toLowerCase();
        this.connectionName = this.connectionType;
    }

    public String connectTwoNodes(Node start, Node end)
    {
        String s =  "MATCH (a:"+ start.getNodeType()+"),"
        		+ "(b:"+end.getNodeType()+") " +
                    "WHERE a.uuid = '"+start.getUuid()+"' AND b.uuid = '"+end.getUuid()+"'" +
                    " CREATE (a)-[:"+this.connectionType+"{name:'"+connectionName+"'}] -> (b)";
        return s; 
    }
    public String uuidOfTwoNodes(Node start, Node end)
    {
    	String s = "(a_uuid= '"+start.getUuid()+"' AND b_uuid= '"+end.getUuid()+"')";
    	
    	return s;
    }
    
    

}