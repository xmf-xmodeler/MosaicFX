package tool.clients.fmmlxdiagrams.graphdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javafx.geometry.NodeOrientation;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.graphdb.Node.label;
import tool.clients.fmmlxdiagrams.graphdb.NodeConnection.connection;


public class Controller 
{
	private Connector nodeConnector;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<String> connectionList = new ArrayList<>();
	private InstanceNode InstanceNode;
	private Node diagramNode;
	private String ofPath;
	
		
	public Controller(String diagramName) 
	{
		this.diagramNode = createAndInsertInList(diagramName, label.DIAGRAMM);
		
	}

	

	public void create(FmmlxObject object)
	{
		createInstance(object);
		createAttributes(object);
		createOperations(object);
		createConstraints(object);
	}
	
	private void createInstance(FmmlxObject object)
	{
		String 	name 	= object.getName();
		int		level	= object.getLevel();
		double 	xCoordinate = object.getCenterX();
		double 	yCoordinate = object.getCenterY();
		
		InstanceNode n = new InstanceNode(object);
		this.nodeList.add(n);
		this.InstanceNode = n;
		
		connectAndInsertInList(connection.OF, n, diagramNode);
		
		Node instanceLevel 				= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, n);
		Node slotOfInstanceLevel		= createConnectAndInsert(String.valueOf(level), label.SECONDCLASSATTRIBUTE, instanceLevel);
		
	}
	
	private void createAttributes(FmmlxObject object)
	{
		Vector<FmmlxAttribute> attributes = object.getAllAttributes();
		Iterator<FmmlxAttribute> attributesIterator = attributes.iterator();
		
		while (attributesIterator.hasNext())
		{
			FmmlxAttribute attribute = attributesIterator.next();
			String name = attribute.getName();
			String type = attribute.getType();
			int level = attribute.getLevel();
			Multiplicity multiplicity = attribute.getMultiplicity();
			
			
			Node attributeNode 					= createConnectAndInsert(name,label.SECONDCLASSATTRIBUTE,this.InstanceNode);
			
	        Node levelOfAttributeNode          	= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE,attributeNode);
	        Node slotOflevelOfAttributeNode     = createConnectAndInsert(String.valueOf(level),label.SLOT,levelOfAttributeNode);
	        Node levelOfAttributeTypeNode     	= createConnectAndInsert("Root::XCore::Integer",label.SECONDCLASSATTRIBUTE,levelOfAttributeNode);
	        
	        Node typeOfAttributeNode           	= createConnectAndInsert("type", label.FIRSTCLASSATTRIBUTE,attributeNode);
	        Node slotOfTypeOfAttributeNode      = createConnectAndInsert(type,label.SLOT,typeOfAttributeNode);
	        
	        Node multiplicityNode 				= createMultipicityNode(multiplicity);
	        connectAndInsertInList(NodeConnection.connection.OF,multiplicityNode,attributeNode);
	        
		}
	}
	private Node createMultipicityNode(Multiplicity multiplicity)
	{
		int min = multiplicity.min;
		int max = multiplicity.max;
		boolean upperLimit = multiplicity.upperLimit;
		boolean ordered = multiplicity.ordered;
		
		Node multiplicityNode 			= createAndInsertInList("multiplicity",label.FIRSTCLASSATTRIBUTE);
		
		Node minNode					= createConnectAndInsert("min",label.FIRSTCLASSATTRIBUTE,multiplicityNode);
		Node minValueNode 				= createConnectAndInsert(String.valueOf(min),label.SLOT,minNode);
		
		Node minValueTypeNode 			= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, minValueNode);
		
		Node maxNode 					= createConnectAndInsert("max",label.FIRSTCLASSATTRIBUTE,multiplicityNode);
		Node maxValueNode				= createConnectAndInsert(String.valueOf(max),label.SLOT,maxNode);
		
		Node maxValueTypeNode			= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, maxValueNode);
		
		Node upperlimitNode				= createConnectAndInsert("upperlimit",label.FIRSTCLASSATTRIBUTE,multiplicityNode);
		Node upperlimitValueNode 		= createConnectAndInsert(String.valueOf(upperLimit),label.SLOT,upperlimitNode);
		
		Node upperlimitValueTypeNode	= createConnectAndInsert("Root::XCore::Boolean", label.FIRSTCLASSATTRIBUTE, upperlimitValueNode);
		
		Node orderedNode				= createConnectAndInsert("ordered",label.FIRSTCLASSATTRIBUTE,multiplicityNode);
		Node orderedValueNode			= createConnectAndInsert(String.valueOf(ordered),label.SLOT,orderedNode);
		
		Node orderedValueTypeNode		= createConnectAndInsert("Root::XCore::Boolean", label.FIRSTCLASSATTRIBUTE, orderedValueNode);
		
		return multiplicityNode;
	}
	
	// Method to create the Nodes for the Operations of one Instance
	private void createOperations(FmmlxObject object)
	{
		Vector<FmmlxOperation> operations = object.getAllOperations();
		Iterator<FmmlxOperation> operationsIterator = operations.iterator();
		
		while (operationsIterator.hasNext())
		{
			FmmlxOperation operation = operationsIterator.next();
			String name = operation.getName();
			String type = operation.getType();
			String body = operation.getBody();
			int level = operation.getLevel();
			Boolean isMonitored = operation.isMonitored();
			Boolean delegateToClassAllowed = operation.isDelegateToClassAllowed();
			
			
			Node operationNode 						= createConnectAndInsert(name, label.SECONDCLASSATTRIBUTE, this.InstanceNode);
			
	        Node levelOfOperationNode          		= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node levelOfOperationValueNode     		= createConnectAndInsert(String.valueOf(level),label.SLOT, levelOfOperationNode);
	        Node levelOfOperationTypeNode     		= createConnectAndInsert("Root::XCore::Integer",label.SECONDCLASSATTRIBUTE,levelOfOperationValueNode);
			

	        Node typeOfOperationNode           		= createConnectAndInsert("type", label.FIRSTCLASSATTRIBUTE,operationNode);
	        Node slotOfTypeOfOperationNode      	= createConnectAndInsert(type,label.SLOT,typeOfOperationNode);
	        
	        Node isMonitoredNode 					= createConnectAndInsert("is Monitored", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfisMonitoredNode				= createConnectAndInsert(String.valueOf(isMonitored), label.SECONDCLASSATTRIBUTE, isMonitoredNode);
	        
	        Node delegateToClassAllowedNode			= createConnectAndInsert("delegateToClassAllowed", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfdelegateToClassAllowedNode	= createConnectAndInsert(String.valueOf(delegateToClassAllowed), 
	        																	label.SECONDCLASSATTRIBUTE, delegateToClassAllowedNode);
	        
			
		}
	}
	
	private void createConstraints(FmmlxObject object)
	{
		Vector<Constraint> constraints 				= object.getConstraints();
		Iterator<Constraint> constraintsIterator	= constraints.iterator();
		
		while (constraintsIterator.hasNext())
		{
			Constraint constraint = constraintsIterator.next();
			
			String 	name 	= constraint.getName();
			int 	level 	= constraint.getLevel();
			String 	body 	= constraint.getBodyRaw();
			String	reason	= constraint.getReasonRaw();
			
			Node constraintNode						= createConnectAndInsert(name, label.FIRSTCLASSATTRIBUTE, this.InstanceNode);
			
			Node levelOfConstraintNode				= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node levelOfConstraintValueNode			= createConnectAndInsert(String.valueOf(level), label.SECONDCLASSATTRIBUTE, levelOfConstraintNode);
			Node levelOfConstraintTypeNode			= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, levelOfConstraintValueNode);
			
			Node bodyOfConstraintNode				= createConnectAndInsert("body", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node slotOfBodyOfConstraintNode			= createConnectAndInsert(body, label.SECONDCLASSATTRIBUTE, bodyOfConstraintNode);
			Node slotOfBodyOfConstraintTypeNode 	= createConnectAndInsert("Root::XCore::String", label.FIRSTCLASSATTRIBUTE, slotOfBodyOfConstraintNode);
			
			Node reasonOfConnstraintNode			= createConnectAndInsert("reason", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node slotOfReasonOfConnstraintNode		= createConnectAndInsert(reason, label.SECONDCLASSATTRIBUTE, reasonOfConnstraintNode);
			Node slotOfReasonOfConnstraintTypeNode	= createConnectAndInsert("Root::XCore::String", label.FIRSTCLASSATTRIBUTE, reasonOfConnstraintNode);
			
		}
		
	}
	
	private Node createAndInsertInList(String name, Node.label label)
	{
		Node n = new Node(name,label);
		nodeList.add(n);
		return n;
	}
	private void connectAndInsertInList(NodeConnection.connection connection,Node start,Node end)
	{
		NodeConnection nc = new NodeConnection(connection);
		String connectionStatment = nc.connectTwoNodes(start, end);
		connectionList.add(connectionStatment);
		
	}
	
	private Node createConnectAndInsert(String name, Node.label label, Node end)
	{
		Node n = createAndInsertInList(name, label);
		connectAndInsertInList(connection.OF, n, end);
		
		return n;
		
	}
	
	
	public void nodesAndConnects(ArrayList<Node> nodeList, ArrayList<String> connectionsList)
	{
		nodeList.addAll(this.nodeList);
		this.nodeList.clear();
		connectionsList.addAll(this.connectionList);
		this.connectionList.clear();
	}
	
	
	public void setConnector(Connector nodConnector){this.nodeConnector = nodConnector;}
	public InstanceNode getInstanceNode() {return InstanceNode;}
	public String getOfPath() {return ofPath;}
}