package tool.clients.fmmlxdiagrams.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Edge;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.graphdb.Node.label;
import tool.clients.fmmlxdiagrams.graphdb.NodeConnection.connection;
import tool.clients.serializer.FmmlxSerializer;
import tool.xmodeler.PropertyManager;

public class GraphDBController 
{
	private String uri;
	private String user;
	private String password;
	private final FmmlxDiagram diagram;
	
//	private final String diagramName;
	
	private Connector connector;
	
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<String> nodeConnectionsList = new ArrayList<>();
	
	public ArrayList<NodeConnection> nodeConnectionList = new ArrayList<>();
	
	private Vector<InstanceNode> instancesList = new Vector<>();
	private Vector<FmmlxObject> objects = new Vector<>();
	protected Vector<Edge<?>>     edges = new Vector<>();
	
	
	// Der Konstruktor der Klasse GraphDBController.
	public GraphDBController (FmmlxDiagram diagram) {
		this.diagram = diagram;
		
		getObjects();
		getClass();
		setConnection();
		
	}
	public void setConnection()
	{
		Preferences userPreferences = Preferences.userRoot(); 
		this.uri = PropertyManager.getProperty("graphDBUri");
		this.user = PropertyManager.getProperty("graphDBUser");
		this.password = PropertyManager.getProperty("graphDBPassword");

	}
	
	public void test ()
	{
	 	System.err.println(uri);
		System.err.println(user);
		System.err.println(password);
	}
// main Method of this class
	public void connectionMain()
	{
		long start = System.currentTimeMillis();
		
		createConnector();
		
		connector.deleteEverything();
		
		Controller c = new Controller(this.diagram);
		this.objects = this.diagram.getObjectsReadOnly();
		
		this.nodeConnectionList = c.testConnects();
		
		Iterator<FmmlxObject> iterator = this.objects.iterator();
		while (iterator.hasNext())
		{	long oneObject = System.currentTimeMillis();
		
			c.create(iterator.next());
			c.nodes(nodeList);
			InstanceNode instance = c.getInstanceNode();
			this.instancesList.add(instance);
			
			long oneObjectEnd = System.currentTimeMillis() - oneObject;
			System.err.println("one Object operation took " + oneObjectEnd + " milliseconds");
			
			createInDB();
		}
		connectInstances();
		connectSlots();
		System.err.print("fertig \n");
		long end = System.currentTimeMillis() - start;
		System.err.println("operation took " + end + " milliseconds");
	}
	
	
	
//	Creates an Connection to the GraphDB
	public void createConnector() 
	{
		Connector c1 = new Connector(this.uri, this.user, this.password);
		this.connector = c1;
	}
	
	public void createFmmlxSerializer()throws TransformerException, ParserConfigurationException
	{
		FmmlxSerializer serializer = new FmmlxSerializer(((FmmlxDiagram)diagram).getFilePath());
	}
	

//	Returns an iterates over all Objects of the Diagram
	private Iterator objectIterator() {
		Iterator<FmmlxObject> objectIterator = this.objects.iterator();
		return objectIterator;
	}
//	Returns an iterates over all Edges of the Diagram
	private Iterator edgesIterator() {
		Iterator<Edge<?>> edgesIterator = this.edges.iterator();
		return edgesIterator;
	}
	
	
	protected void getObjects()
	{
		this.objects = this.diagram.getObjectsReadOnly();
	}
	protected void getEdges()
	{
		this.edges = this.diagram.getEdges();
	}
	
	private void createInDB()
	{
		long start = System.currentTimeMillis();
		
		StringBuilder createNodes = new StringBuilder();
		String createConnections = "";
		Iterator<Node> nodeIterator = nodeList.iterator();
		createNodes.append("Create");
//	creates an String with all Nodes and send it to the DB
		while (nodeIterator.hasNext())
		{
			createNodes.append(nodeIterator.next().getCreate2()+ ",");
		}
		createNodes.delete(createNodes.length()-1, createNodes.length());
		nodeList.clear();		
		connector.sendQuerry(createNodes.toString());
		
		long nodeSending = System.currentTimeMillis()-start;
		System.err.print("Creating Nodes took "+ nodeSending + "milliseconds \n");

//		while (connectionIterator.hasNext()) 
//		{	
//			String s  =connectionIterator.next();
//			connector.sendQuerry(s);
//		}
//		connector.test(nodeConnectionsList);
		batchQuerry();
		
		long connectionSending = System.currentTimeMillis()-nodeSending;
		System.err.print("Creating Connections took "+ connectionSending + "milliseconds \n");
		
		nodeConnectionList.clear();
		long end = System.currentTimeMillis()-start;
		System.err.print("one createInDB took "+ end + "milliseconds \n");
	}
	
	
	private void batchQuerry()
	{
		StringBuilder batchQuery = new StringBuilder();
		batchQuery.append("UNWIND $data as row ");
		batchQuery.append("Match (start " + "{uuid: row.startUuid}) ");
		batchQuery.append("Match (end " + "{uuid: row.endUuid}) ");
		batchQuery.append("MERGE (start)-[r:"+String.valueOf(NodeConnection.connection.OF)+" " + "{name: row.connectionName}]->(end) ");
		
		List<Map<String, Object>> querryData = new ArrayList<>();
		Iterator<NodeConnection> it = nodeConnectionList.iterator();
		while(it.hasNext())
		{
			NodeConnection nc = it.next();
			Map<String, Object> rowData = new HashMap<>();
			rowData.put("startUuid", String.valueOf(nc.getStart().getUuid()));
			rowData.put("endUuid", String.valueOf(nc.getEnd().getUuid()));
			rowData.put("connectionName", nc.getConnectionName());
			querryData.add(rowData);
		}
		connector.sendBatchQuerry(batchQuery.toString(), querryData);
	}
	
	
	
	private void connectInstances()
	{
//		System.err.print(instancesList.size());
		
		Iterator<InstanceNode> instanceIterator = instancesList.iterator();
		String connectInstancesStatment;
		while (instanceIterator.hasNext())
		{
			InstanceNode instance = instanceIterator.next();
			if (instance.getInstanceOf() != null)
			{
				Iterator<InstanceNode> instanceIterator2 = instancesList.iterator();
				String instanceOf = instance.getInstanceOf();
				while (instanceIterator2.hasNext())
				{
					InstanceNode secondInstance = instanceIterator2.next();
					
					if (instanceOf.equals(secondInstance.getName()))
					{
						instance.setInstanceOfNode(secondInstance);
						NodeConnection c = new NodeConnection(connection.OF);
						String s = c.connectTwoNodes(instance, secondInstance);
						connector.sendQuerry(s);
					}
				}
			}
			
		}
	}
	
	private void connectSlots()
	{
		Iterator<InstanceNode> instanceIterator = instancesList.iterator();
		
		while(instanceIterator.hasNext())
		{
			
			InstanceNode instance1 	= instanceIterator.next();
			
			if (instance1.getInstanceOfNode() == null)
			{
				continue;
			}
			InstanceNode instance2	= instance1.getInstanceOfNode();
			
			Vector<SlotNode> slots 	= instance1.getSlots();
			Vector<Node> attributes = instance2.getAttributes();
			
			Iterator<SlotNode> slotIterator = slots.iterator();
			
			
			while (slotIterator.hasNext())
			{
				SlotNode slot = slotIterator.next();
				
				Iterator<Node> attributesIterator = attributes.iterator();
				while(attributesIterator.hasNext())
				{
					Node attribute = attributesIterator.next();
					if (slot.getSlotName().equals(attribute.getName()))
					{
						NodeConnection c = new NodeConnection (connection.OF);
						String s = c.connectTwoNodes(slot,attribute);
						connector.sendQuerry(s);
						System.err.print("Slots connected \n");
						break;
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
}