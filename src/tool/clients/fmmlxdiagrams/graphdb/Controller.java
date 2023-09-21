package tool.clients.fmmlxdiagrams.graphdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javafx.geometry.NodeOrientation;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.graphdb.Node.label;
import tool.clients.fmmlxdiagrams.graphdb.NodeConnection.connection;


public class Controller 
{
	private Connector nodeConnector;
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<NodeConnection> connectionList	= new ArrayList<>();
	private InstanceNode InstanceNode;
	private Node diagramNode;
	private Node packageNode;
	private String ofPath;
	private String diagramName;
	
		
	public Controller(FmmlxDiagram diagram) 
	{
//		Das nachfolgende ist in wirklichkeit das Project
//		TODO möglichkeit finden an den Diagram namen zu kommen.
		String diagramPackage 	= clearUpPath(diagram.getPackagePath(),"Root::");
		this.packageNode		= createAndInsertInList(diagramPackage, label.PACKAGE);
		
		this.diagramName = diagram.getDiagramLabel();
		this.diagramNode = createConnectAndInsert(diagramName, label.DIAGRAMM,this.packageNode);
		
		
		
//		System.err.print(diagramPackage + "\n");
		
		
	}

	

	public void create(FmmlxObject object)
	{
		createInstance(object);
		this.InstanceNode.setAttributes(createAttributes(object));
		this.InstanceNode.setSlots(createSlots(object));
		createOperations(object);
		createConstraints(object);
	}
	
	private void createInstance(FmmlxObject object)
	{
		String 	name 	= object.getName();
		int		level	= object.getLevel();
		boolean hidden 	= object.isHidden();
		
		
		InstanceNode instance = new InstanceNode(object);
		this.nodeList.add(instance);
		this.InstanceNode = instance;
		
		connectAndInsertInList(connection.OF, instance, diagramNode);
		
		Node instanceLevel 				= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, instance);
		Node slotOfInstanceLevel		= createConnectAndInsert(String.valueOf(level), label.SLOT, instanceLevel);
		Node slotOfInstanceLevelType	= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, instanceLevel);
		
		Node instanceHidden				= createConnectAndInsert("hidden",label.FIRSTCLASSATTRIBUTE, instance);
		Node instanceHiddenValue		= createConnectAndInsert(String.valueOf(hidden), label.SLOT, instanceHidden);
		
		Node isAbstract					= createConnectAndInsert("isAbstract", label.FIRSTCLASSATTRIBUTE,instance);
		Node isAbstractSlot				= createConnectAndInsert(String.valueOf(object.isAbstract()), label.SLOT, isAbstract);
	}
	
	private Vector<Node> createAttributes(FmmlxObject object)
	{
		Vector<FmmlxAttribute> attributes 			= object.getAllAttributes();
		Iterator<FmmlxAttribute> attributesIterator = attributes.iterator();
		
		Vector<Node> attributeNodes = new Vector<Node>();
		
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
	        Node levelOfAttributeTypeNode     	= createConnectAndInsert("Root::XCore::Integer",label.FIRSTCLASSATTRIBUTE,levelOfAttributeNode);
	        
	        Node typeOfAttributeNode           	= createConnectAndInsert("type", label.FIRSTCLASSATTRIBUTE,attributeNode);
	        Node slotOfTypeOfAttributeNode      = createConnectAndInsert(type,label.SLOT,typeOfAttributeNode);
	        
	        Node multiplicityNode 				= createMultipicityNode(multiplicity);
	        connectAndInsertInList(NodeConnection.connection.OF,multiplicityNode,attributeNode);
	        attributeNodes.add(attributeNode);
		}
		
		return attributeNodes;
	}
	
	private Vector<SlotNode> createSlots(FmmlxObject object)
	{
		Vector<FmmlxSlot> slots						= object.getAllSlots();
		Vector<SlotNode> slotNodes 					= new Vector<SlotNode>();					
		Iterator<FmmlxSlot> slotsIterator			= slots.iterator();

		while (slotsIterator.hasNext())
		{
			FmmlxSlot 	slot = slotsIterator.next();
			SlotNode 	slotNode 	=	createConnectAndInsertSlot(String.valueOf(slot.getValue()), label.SLOT, InstanceNode);
			Node 		slotLabel	=	createConnectAndInsert("Slot", label.FIRSTCLASSATTRIBUTE, slotNode);
			slotNodes.add(slotNode);
			slotNode.setOfPath(slot.getOwner());
			
			slotNode.setSlotName(slot.getName());
//			System.err.print(slot.getName() + " " + slot.getOwner().getOfPath() + "\n");
		}
		
		return slotNodes;
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
			
			
			Node operationNode 						= createConnectAndInsert(name, label.COMPILEDOPERATION, this.InstanceNode);
			
	        Node levelOfOperationNode          		= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node levelOfOperationValueNode     		= createConnectAndInsert(String.valueOf(level),label.SLOT, levelOfOperationNode);
	        Node levelOfOperationTypeNode     		= createConnectAndInsert("Root::XCore::Integer",label.FIRSTCLASSATTRIBUTE,levelOfOperationValueNode);
			

	        Node typeOfOperationNode           		= createConnectAndInsert("type", label.FIRSTCLASSATTRIBUTE,operationNode);
	        Node slotOfTypeOfOperationNode      	= createConnectAndInsert(type,label.SLOT,typeOfOperationNode);
	        
	        Node isMonitoredNode 					= createConnectAndInsert("is Monitored", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfisMonitoredNode				= createConnectAndInsert(String.valueOf(isMonitored), label.SLOT, isMonitoredNode);
	        
	        Node delegateToClassAllowedNode			= createConnectAndInsert("delegateToClassAllowed", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfdelegateToClassAllowedNode	= createConnectAndInsert(String.valueOf(delegateToClassAllowed), 
	        																	label.SLOT, delegateToClassAllowedNode);
	        
	        Node bodyOfOperation					= createConnectAndInsert("body", label.FIRSTCLASSATTRIBUTE, operationNode);
	        Node slotOfbodyOfOperation				= createConnectAndInsert(body, label.SLOT, bodyOfOperation);
			
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
			
			Node constraintNode						= createConnectAndInsert(name, label.CONSTRAINT, this.InstanceNode);
			
			Node levelOfConstraintNode				= createConnectAndInsert("level", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node levelOfConstraintValueNode			= createConnectAndInsert(String.valueOf(level), label.SLOT, levelOfConstraintNode);
			Node levelOfConstraintTypeNode			= createConnectAndInsert("Root::XCore::Integer", label.FIRSTCLASSATTRIBUTE, levelOfConstraintValueNode);
			
			Node bodyOfConstraintNode				= createConnectAndInsert("body", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node slotOfBodyOfConstraintNode			= createConnectAndInsert(body, label.SLOT, bodyOfConstraintNode);
			Node slotOfBodyOfConstraintTypeNode 	= createConnectAndInsert("Root::XCore::String", label.FIRSTCLASSATTRIBUTE, slotOfBodyOfConstraintNode);
			
			Node reasonOfConnstraintNode			= createConnectAndInsert("reason", label.FIRSTCLASSATTRIBUTE, constraintNode);
			Node slotOfReasonOfConnstraintNode		= createConnectAndInsert(reason, label.SLOT, reasonOfConnstraintNode);
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
		NodeConnection nc1	= new NodeConnection(start, end);
		connectionList.add(nc1);
		
	}
	
	private Node createConnectAndInsert(String name, Node.label label, Node end)
	{
		Node n = createAndInsertInList(name, label);
		connectAndInsertInList(connection.OF, n, end);
		
		return n;
		
	}
	
	private SlotNode createConnectAndInsertSlot(String name, Node.label label, Node end)
	{
		SlotNode n = new SlotNode(name,label);
		nodeList.add(n);		
		connectAndInsertInList(connection.OF, n, end);
		
		return n;
		
	}
	
	
	public void nodes(ArrayList<Node> nodeList)
	{
		nodeList.addAll(this.nodeList);
		this.nodeList.clear();
		
		
	}
	public ArrayList<NodeConnection> testConnects()
	{
		return this.connectionList;
	}
	
	public void setConnector(Connector nodConnector){this.nodeConnector = nodConnector;}
	public InstanceNode getInstanceNode() {return InstanceNode;}
	public String getOfPath() {return ofPath;}
	
	private String clearUpPath(String path, String prefix)
	{	
		return path.substring(prefix.length());
	}
}