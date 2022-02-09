package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.AssociationInstanceContextMenu;

import java.util.Vector;

public class FmmlxLink extends Edge<FmmlxObject> implements FmmlxProperty{

	String ofPath;

	public FmmlxLink(String path, String startPath, String endPath, String ofPath, Vector<Point2D> points,
				PortRegion sourcePort, PortRegion targetPort, 
				Vector<Object> labelPositions, AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(startPath), diagram.getObjectByPath(endPath), points, sourcePort, targetPort, labelPositions, diagram);
		this.ofPath = ofPath;
	}

	public String getOfPath() {
		return ofPath;
	}

	public String getOfName() {
		return diagram.getAssociationByPath(ofPath).getName();
	}

	private enum Anchor {SOURCE,CENTRE,TARGET}

	@Override protected void layoutLabels(FmmlxDiagram diagram) {
		try{
			createLabel(getAssociation().getName(), 0, Anchor.CENTRE, ()->{}, 0, diagram);
			layoutingFinishedSuccesfully = true;
		} catch(Exception e) {}
	}

	public FmmlxAssociation getAssociation() {
		return (FmmlxAssociation) diagram.getAssociationByPath(ofPath);
	}

	private void createLabel(String value, int localId, Anchor anchor, Runnable action, int yDiff, FmmlxDiagram diagram) {
		double w = Math.max(20, FmmlxDiagram.calculateTextWidth(value));
		double h = FmmlxDiagram.calculateTextHeight();
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add((FmmlxObject) sourceNode);
		if(anchor!=Anchor.SOURCE) anchors.add((FmmlxObject) targetNode);
		
		if(anchor==Anchor.CENTRE) {
			Point2D storedPostion = getLabelPosition(localId);	
			anchors.add(getSourceNode());
			anchors.add(getTargetNode());
			if(storedPostion != null) {
				diagram.addLabel(new DiagramEdgeLabel<FmmlxObject>(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, Color.BLACK, null));
			} else {
				diagram.addLabel(new DiagramEdgeLabel<FmmlxObject>(this, localId, action, null, anchors, value, 0, -h*1.5, w, h, Color.BLACK, null));
			}	
		}
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
	protected String getSvgDashes() {
		return "10";
	}

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new AssociationInstanceContextMenu(this, actions);
	}
	
	public String toPair() {
		String firstString = ((FmmlxObject) (this.sourceNode)).getName();
		String secondString = ((FmmlxObject) (this.targetNode)).getName();
		return "( "+firstString+" ; "+secondString+" )";
	}

	@Override
	public void unHighlight() {	}

	@Override
	public String getName() {
		return getOfName()+"#"+((FmmlxObject) (this.sourceNode)).getName()+"#"+((FmmlxObject) (this.targetNode)).getName();
	}

	@Override
	public PropertyType getPropertyType() {
		return PropertyType.AssociationInstance;
	}

	@Override
	public tool.clients.fmmlxdiagrams.Edge.HeadStyle getTargetDecoration() { //TODO: Breakpoint here to check why TargetDecoration is causing problems
		return getAssociation().getTargetDecoration();
	}

	@Override
	public tool.clients.fmmlxdiagrams.Edge.HeadStyle getSourceDecoration() {
		return getAssociation().getSourceDecoration();
	}
}
