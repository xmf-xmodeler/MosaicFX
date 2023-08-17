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


class Controller {
	private Connector nodeConnector;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<String> connectionList = new ArrayList<>();
	private Node InstanceNode;
	
	
	
	public void setConnector(Connector nodConnector)
	{
		this.nodeConnector = nodConnector;
	}
	
	public void createInstanceNode(FmmlxObject object)
	{
		String 	name 	= object.getName();
		int		level	= object.getLevel();
		double 	xCoordinate = object.getCenterX();
		double 	yCoordinate = object.getCenterY();
		
		Node n = new Node(name, Node.label.INSTANCE, xCoordinate, yCoordinate);
		this.nodeList.add(n);
		this.InstanceNode = n;
		Node instanceLevel 				= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, n);
		Node slotOfInstanceLevel		= createConnectAndInsert(String.valueOf(level), label.SECONDCLASSATTRIBUTE, instanceLevel);
		
		createAttributes(object);
		createOperations(object);
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
			
			
			Node attributeNode 					= createAndInsertInList(name,Node.label.SECONDCLASSATTRIBUTE);
			connectAndInsertInList(NodeConnection.connection.OF,attributeNode,this.InstanceNode);
			
	        Node levelOfAttributeNode          	= createAndInsertInList("level", Node.label.FIRSTCLASSATTRIBUTE);
	        Node slotOflevelOfAttributeNode     = createAndInsertInList(String.valueOf(level),Node.label.SLOT);
	        Node levelOfAttributeTypeNode     	= createAndInsertInList("Root::XCore::Integer",Node.label.SECONDCLASSATTRIBUTE);
	        
	        connectAndInsertInList(NodeConnection.connection.OF,levelOfAttributeNode,attributeNode);
	        connectAndInsertInList(NodeConnection.connection.OF,slotOflevelOfAttributeNode,levelOfAttributeNode);
	        connectAndInsertInList(NodeConnection.connection.OF,levelOfAttributeTypeNode,levelOfAttributeNode);

	        
	        Node typeOfAttributeNode           	= createAndInsertInList("type", Node.label.FIRSTCLASSATTRIBUTE);
	        Node slotOfTypeOfAttributeNode      = createAndInsertInList(type,Node.label.SLOT);
	        connectAndInsertInList(NodeConnection.connection.OF,typeOfAttributeNode,attributeNode);
	        connectAndInsertInList(NodeConnection.connection.OF,slotOfTypeOfAttributeNode,typeOfAttributeNode);
	        
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
		
		Node multiplicityNode 		= createAndInsertInList("multiplicity",Node.label.FIRSTCLASSATTRIBUTE);
		
		Node minNode				= createAndInsertInList("min",Node.label.FIRSTCLASSATTRIBUTE);
		Node minValueNode 			= createAndInsertInList(String.valueOf(min),Node.label.SLOT);
		connectAndInsertInList(NodeConnection.connection.OF,minNode,multiplicityNode);
		connectAndInsertInList(NodeConnection.connection.OF,minValueNode,minNode);
		
		Node minValueTypeNode 		= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, minValueNode);
		
		Node maxNode 				= createAndInsertInList("max",Node.label.FIRSTCLASSATTRIBUTE);
		Node maxValueNode			= createAndInsertInList(String.valueOf(max),Node.label.SLOT);
		connectAndInsertInList(NodeConnection.connection.OF,maxNode,multiplicityNode);
		connectAndInsertInList(NodeConnection.connection.OF,maxValueNode,maxNode);
		
		Node maxValueTypeNode		= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, maxValueNode);
		
		Node upperlimitNode			= createAndInsertInList("upperlimit",Node.label.FIRSTCLASSATTRIBUTE);
		Node upperlimitValueNode 	= createAndInsertInList(String.valueOf(upperLimit),Node.label.SLOT);
		connectAndInsertInList(NodeConnection.connection.OF,upperlimitNode,multiplicityNode);
		connectAndInsertInList(NodeConnection.connection.OF,upperlimitValueNode,upperlimitNode);
		
		Node upperlimitValueTypeNode	= createConnectAndInsert("Root::XCore::Boolean", label.FIRSTCLASSATTRIBUTE, upperlimitValueNode);
		
		Node orderedNode			= createAndInsertInList("ordered",Node.label.FIRSTCLASSATTRIBUTE);
		Node orderedValueNode		= createAndInsertInList(String.valueOf(ordered),Node.label.SLOT);
		connectAndInsertInList(NodeConnection.connection.OF,orderedNode,multiplicityNode);
		connectAndInsertInList(NodeConnection.connection.OF,orderedValueNode,orderedNode);
		
		Node orderedValueTypeNode	= createConnectAndInsert("Root::XCore::Boolean", label.FIRSTCLASSATTRIBUTE, orderedValueNode);
		
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
			
			
			
			Node operationNode = createAndInsertInList(name, label.SECONDCLASSATTRIBUTE);
			connectAndInsertInList(connection.OF, operationNode, this.InstanceNode);
			
	        Node levelOfOperationNode          	= createAndInsertInList("level", Node.label.FIRSTCLASSATTRIBUTE);
	        Node levelOfOperationValueNode     	= createAndInsertInList(String.valueOf(level),Node.label.SLOT);
	        Node levelOfOperationTypeNode     	= createAndInsertInList("Root::XCore::Integer",Node.label.SECONDCLASSATTRIBUTE);
			
	        connectAndInsertInList(NodeConnection.connection.OF,levelOfOperationNode,operationNode);
	        connectAndInsertInList(NodeConnection.connection.OF,levelOfOperationValueNode,levelOfOperationNode);
	        connectAndInsertInList(NodeConnection.connection.OF,levelOfOperationTypeNode,levelOfOperationValueNode);

	        Node typeOfOperationNode           	= createAndInsertInList("type", Node.label.FIRSTCLASSATTRIBUTE);
	        Node slotOfTypeOfOperationNode      = createAndInsertInList(type,Node.label.SLOT);
	        connectAndInsertInList(NodeConnection.connection.OF,typeOfOperationNode,operationNode);
	        connectAndInsertInList(NodeConnection.connection.OF,slotOfTypeOfOperationNode,typeOfOperationNode);
	        
	        Node isMonitoredNode 				= createConnectAndInsert("is Monitored", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfisMonitoredNode			= createConnectAndInsert(String.valueOf(isMonitored), label.SECONDCLASSATTRIBUTE, isMonitoredNode);
	        
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
}