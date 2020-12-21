package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.AssociationInstanceContextMenu;

import java.util.Vector;

public class FmmlxLink extends Edge {

//	FmmlxAssociation ofAssociation;
	String ofPath;
	private FmmlxDiagram diagram;

	public FmmlxLink(String path, String startPath, String endPath, String ofPath, Vector<Point2D> points,
				PortRegion sourcePort, PortRegion targetPort, 
				Vector<Object> labelPositions, FmmlxDiagram diagram) {
		super(path, diagram.getObjectByName(startPath), diagram.getObjectByName(endPath), points, sourcePort, targetPort, labelPositions, diagram);
//		this.ofAssociation = (FmmlxAssociation) diagram.getAssociationById(ofId);
		this.ofPath = ofPath;
		this.diagram = diagram;
//		layout();
	}

	public String getOfPath() {
		return ofPath;
	}

	public String getOfName() {
		return diagram.getAssociationByName(ofPath).getName();
	}

	private enum Anchor {SOURCE,CENTRE,TARGET}

	@Override protected void layoutLabels() {
		try{
			createLabel(getOfAssociation().getName(), 0, Anchor.CENTRE, ()->{}, 0);
			layoutingFinishedSuccesfully = true;
		} catch(Exception e) {layoutingFinishedSuccesfully = false;}
//		if(reverseName != null) 
//	    createLabel(reverseName, Anchor.CENTRE, ()->{System.err.println("Huhu!");}, 20);
//		createLabel(accessNameStartToEnd, Anchor.TARGET, ()->{System.err.println("Huhu!");}, 0);
//		createLabel(accessNameEndToStart, Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 0);
//		createLabel(""+levelStartToEnd, Anchor.TARGET, ()->{System.err.println("Huhu!");}, 20);
//		createLabel(""+levelEndToStart, Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 20);
//		createLabel(multiplicityStartToEnd.toString(), Anchor.TARGET, ()->{System.err.println("Huhu!");}, 40);
//		createLabel(multiplicityEndToStart.toString(), Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 40);
	}

	private FmmlxAssociation getOfAssociation() {
		return (FmmlxAssociation) diagram.getAssociationByName(ofPath);
	}

	private void createLabel(String value, int localId, Anchor anchor, Runnable action, int yDiff) {
//		double x = (getSourceNode().getX() + getSourceNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getX() + getTargetNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
//		double y = (getSourceNode().getY() + getSourceNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getY() + getTargetNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
		double w = Math.max(20, FmmlxDiagram.calculateTextWidth(value));
		double h = FmmlxDiagram.calculateTextHeight();
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add(sourceNode);
		if(anchor!=Anchor.SOURCE) anchors.add(targetNode);
		
		if(anchor==Anchor.CENTRE) {
		Point2D storedPostion = getLabelPosition(localId);	
		anchors.add(getSourceNode());
		anchors.add(getTargetNode());
		if(storedPostion != null) {
			diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, Color.BLACK, new Color(0,0,0,0)));
		} else {
			diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 0, -h*1.5, w, h, Color.BLACK, new Color(0,0,0,0)));
		}	
		}
		//diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 0, -20+yDiff, w, h, Color.BLACK, new Color(0, 0, 0, 0)));
	}
	
	@Override
	protected Color getPrimaryColor() {
		return Color.GRAY;
	}

	@Override
	protected Double getLineDashes() {
		return 10d;
	}

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new AssociationInstanceContextMenu(this, actions);
	}
	
	public String toPair() {
		String firstString = this.sourceNode.getName();
		String secondString = this.targetNode.getName();
		return "( "+firstString+" ; "+secondString+" )";
	}

	public void edit(FmmlxObject selectedItem, FmmlxObject selectedItem2) {
		this.sourceNode=diagram.getObjectByName(selectedItem.getName());
		this.targetNode= diagram.getObjectByName(selectedItem2.getName());
	}
	
	@Override
	public void unHighlight() {	}

	@Override
	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		super.intermediatePoints = intermediatePoints;
	}

	@Override
	public String getName() {
		return getOfName()+"#"+this.sourceNode.getName()+"#"+this.targetNode.getName();
	}
}
