package tool.clients.fmmlxdiagrams.graphdb;

import java.time.Duration;

import org.neo4j.driver.*;


class Connector  implements AutoCloseable{
	
	private final Driver driver;

    public String createNodeQuerry = "Create";
    private String createConnectionQuerry = "";

    public Connector(String uri, String user, String password) {
    	Config config = Config.builder()
                .withDriverMetrics()
                .build();
    	
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        
    }
    


    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void sendQuerry(String querry)
    {
//        try(var session = driver.session())
//        {
//            var querry1 = new Query(querry);
//            session.run(querry1);
//        }
        this.driver.session().run(querry);
    }
    
    public void sendMultipleStatmentQuerry(String querry)
    {
    	
    	try(Session session = driver.session(SessionConfig.forDatabase("neo4j")))
    	{
    		session.executeWriteWithoutResult(tx -> tx.run(querry));
    	}
    }

    public void createNode (Node node)
    {
        final String CYPHER_CREATE_TEMPLATE = node.getCreate();

        this.createNodeQuerry += node.getCreate2() + ",";

        try(Session session = driver.session())
        {
            Query querry = new Query(CYPHER_CREATE_TEMPLATE);
            session.run(querry);
        }
    }

    public void createNodes ()
    {
        final String CYPHER_CREATE_TEMPLATE = this.createNodeQuerry.substring(0,createNodeQuerry.length()-1) +" ";
        try(Session session = driver.session())
        {
            Query querry = new Query(CYPHER_CREATE_TEMPLATE);
            session.run(querry);
        }
    }

    public void connectTwoNodes (NodeConnection connection, Node start, Node end)
    {

        final String CYPHER_CONNENCT_TEMPLATE = connection.connectTwoNodes(start,end);

//	        this.createConnectionQuerry += connection.connectTwoNodes(start,end);

        try(Session session = driver.session())
        {
            Query querry = new Query(CYPHER_CONNENCT_TEMPLATE);
            session.run(querry);
        }
    }
    public void changeName(Node node, String newName)
    {
        final String CYPHER_CHANGE_TEMPLATE = node.setName(newName);
        try(Session session = driver.session())
        {
            Query querry = new Query(CYPHER_CHANGE_TEMPLATE);
            session.run(querry);
        }
    }

    public void deleteEverything()
    {
        final String CYPHER_DELETE = "Match (n) Detach Delete (n)";
        try(Session session = driver.session())
        {
            Query querry = new Query(CYPHER_DELETE);
            session.run(querry);
        }
    }
}