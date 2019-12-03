package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.AssociationInstanceContextMenu;

import java.util.Vector;

public class FmmlxLink extends Edge {

//	FmmlxAssociation ofAssociation;
	int ofId;
	private FmmlxDiagram diagram;

	public FmmlxLink(int id, int startId, int endId, int ofId, Vector<Point2D> points, 
				PortRegion sourcePort, PortRegion targetPort, 
				Vector<Object> labelPositions, FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, sourcePort, targetPort, labelPositions, diagram);
//		this.ofAssociation = (FmmlxAssociation) diagram.getAssociationById(ofId);
		this.ofId = ofId;
		this.diagram = diagram;
//		layout();
	}
	
	private enum Anchor {SOURCE,CENTRE,TARGET};

	@Override protected void layoutLabels() {
		try{
			createLabel("of " + getOfAssociation().getName(), 0, Anchor.CENTRE, ()->{System.err.println("Huhu!");}, 0);
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
		return (FmmlxAssociation) diagram.getAssociationById(ofId);
	}

	private void createLabel(String value, int localId, Anchor anchor, Runnable action, int yDiff) {
//		double x = (getSourceNode().getX() + getSourceNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getX() + getTargetNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
//		double y = (getSourceNode().getY() + getSourceNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getY() + getTargetNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
		double w = Math.max(20, diagram.calculateTextWidth(value));
		double h = diagram.calculateTextHeight();
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add(startNode);
		if(anchor!=Anchor.SOURCE) anchors.add(endNode);
		diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 50, -100+yDiff, w, h, Color.BLACK, Color.YELLOW));
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
	public ContextMenu getContextMenu(DiagramActions actions) {
		return new AssociationInstanceContextMenu(this, actions);
	}
	
	public String toPair() {
		String firstString = this.startNode.getName();
		String seconString = this.endNode.getName();
		return "( "+firstString+" ; "+seconString+" )";
	}

	public void edit(FmmlxObject selectedItem, FmmlxObject selectedItem2) {
		this.startNode=diagram.getObjectById(selectedItem.getId());
		this.endNode= diagram.getObjectById(selectedItem2.getId());
	}
	
	@Override
	public void unHighlight() {	}
}
