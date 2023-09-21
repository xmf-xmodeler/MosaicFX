package tool.clients.fmmlxdiagrams.graphdb;

import java.util.UUID;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.graphdb.Node.label;

public class InstanceNode extends Node {
	

	
	private final double xCoordinate;
    private final double yCoordinate;
    private String ofPath;
    private String instanceOf;
    private Vector<Node> attributes;
    private Vector<SlotNode> slots;
    private InstanceNode instanceOfNode;

	public InstanceNode(FmmlxObject object)
    {
    	super(object.getName(), label.INSTANCE);
        this.xCoordinate 	= object.getCenterX();
        this.yCoordinate 	= object.getCenterY();
        this.ofPath 		= object.getOfPath();
        super.uuid 			= UUID.randomUUID();
        super.create 		= "Create(:"+super.nodeType+"{name:'"+super.name+"',"
        										+ " uuid:'"+super.uuid+"',"
        										+ " xCoordinate:'"+ this.xCoordinate+"',"
        										+ " yCoordinate:'"+this.yCoordinate+"'})";
//        System.err.print(object.getPath() + "\n");
//        System.err.print(object.getOfPath()+"\n \n");
        getOfNode();
    }


	public double getxCoordinate() {
		return xCoordinate;
	}

	public double getyCoordinate() {
		return yCoordinate;
	}
    public String getOfPath() {
		return ofPath;
	}

	public void setOfPath(String ofPath) {
		this.ofPath = ofPath;
	}
	
	private void getOfNode()
	{
		
		String modelPath = this.ofPath.substring(this.ofPath.indexOf(":")+2, this.ofPath.lastIndexOf(":")-1);

		
		if (!this.ofPath.equals("Root::FMMLx::MetaClass"))
		{
			this.instanceOf = this.ofPath.substring(this.ofPath.lastIndexOf(":")+1);

		}
	}
	public String getInstanceOf() {
		return instanceOf;
	}
	public void setAttributes(Vector<Node> attributes) {
		this.attributes = attributes;
	}

	public Vector<Node> getAttributes() {
		return attributes;
	}
	
	public void setSlots(Vector<SlotNode> slots) {
		this.slots = slots;
	}
	
	public Vector<SlotNode> getSlots() {
		return slots;
	}


	public void setInstanceOfNode(InstanceNode instanceOfNode) {
		this.instanceOfNode = instanceOfNode;
	}


	public InstanceNode getInstanceOfNode() {
		return instanceOfNode;
	}
	
	
}
