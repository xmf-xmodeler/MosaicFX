package tool.clients.fmmlxdiagrams.graphdb;

class NodeConnection
{

    private String  connectionType;
    private String 	connectionName;
    private Node 	start;
    private Node 	end;
    
    enum connection
    {
        PACKAGE_OF, DIAGRAMM_OF, CONTAINS_IN,VISUALISED_IN,
        
        OF,TO,
    }

    public NodeConnection(connection connection) {
        this.connectionType = connection.toString().toLowerCase();
        this.connectionName = this.connectionType;
    }
    
    public NodeConnection(Node start, Node end)
    {
    	this.start 	= start;
    	this.end 	= end;
    	this.connectionName = String.valueOf(connection.OF);
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
	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	public String getConnectionName() {
		return connectionName;
	}
    

}









