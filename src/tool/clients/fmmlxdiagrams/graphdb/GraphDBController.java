package tool.clients.fmmlxdiagrams.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class GraphDBController 
{
	private String uri;
	private String user;
	private String password;
	private final AbstractPackageViewer diagram;
	private final String diagramName;
	private Connector connector;
	
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<String> nodeConnectionsList = new ArrayList<>();
	
	private Vector<InstanceNode> instancesList = new Vector<>();
	private Vector<FmmlxObject> objects = new Vector<>();
	protected Vector<Edge<?>>     edges = new Vector<>();
	
	
	// Der Konstruktor der Klasse GraphDBController.
	public GraphDBController (AbstractPackageViewer diagram) {
		this.diagram = diagram;
		this.diagramName = clearUpPath(this.diagram.getPackagePath(),"Root::");
		getObjects();
		getClass();
		setConnection();
		
	}
	public void setConnection()
	{
		Preferences userPreferences = Preferences.userRoot(); 
		this.uri = userPreferences.get("uri","Error");
		this.user = userPreferences.get("user","Error");
		this.password = userPreferences.get("password","Error");
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
		
		createConnector();
		
		connector.deleteEverything();
		
		Controller c = new Controller(diagramName);
		this.objects = this.diagram.getObjectsReadOnly();
		
		Iterator<FmmlxObject> iterator = this.objects.iterator();
		while (iterator.hasNext())
		{
			c.create(iterator.next());
			c.nodesAndConnects(nodeList, nodeConnectionsList);
			InstanceNode instance = c.getInstanceNode();
			this.instancesList.add(instance);
			
			createInDB();
		}
		connectInstances();
		System.err.print("fertig ");
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
		String createNodes = "Create";
		String createConnections = "";
		Iterator<Node> nodeIterator = nodeList.iterator();
		Iterator<String> connectionIterator = nodeConnectionsList.iterator();
		
//	creates an String with all Nodes and send it to the DB
		while (nodeIterator.hasNext())
		{
			createNodes += nodeIterator.next().getCreate2() + ",";
		}
		createNodes = createNodes.substring(0, createNodes.length()-1);
		nodeList.clear();		
		connector.sendQuerry(createNodes);

		while (connectionIterator.hasNext()) 
		{	
//			createConnections += connectionIterator.next() + "; ";
			String s  =connectionIterator.next();
//			System.err.print(s +"\n");
			
			connector.sendQuerry(s);
		}
		nodeConnectionsList.clear();
			
//		connector.sendMultipleStatmentQuerry(createConnections);
	}
	
	private void connectInstances()
	{
		System.err.print(instancesList.size());
		
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
						NodeConnection c = new NodeConnection(connection.OF);
						String s = c.connectTwoNodes(instance, secondInstance);
						connector.sendQuerry(s);
					}
				}
			}
			
		}
	}
	private String clearUpPath(String path, String prefix)
	{	
		return path.substring(prefix.length());
	}
}