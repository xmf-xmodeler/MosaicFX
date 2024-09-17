package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
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

	@Override
	public void layoutLabels(FmmlxDiagram diagram) {
		try{
			createLabel(getAssociation().getName(), 0, Anchor.CENTRE, ()->{}, 0, diagram);
			layoutingFinishedSuccesfully = true;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
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
		try{
			AssociationType type = getAssociation().getAssociationType();
			String s = type.colorLink;
			return Color.web(s);
		} catch (Exception e) {
//		  System.err.println("getPrimaryColor FAIL: " + e.getMessage());
		}
		return Color.GRAY;
	}

	@Override
	protected double getStrokeWidth() {
		try{
			AssociationType type = getAssociation().getAssociationType();
			return 1. * type.strokeWidthLink;
		} catch (Exception e) {
//		  System.err.println("getStrokeWidth FAIL: " + e.getMessage());
		}
		return 1.;
	}

	@Override
	protected double[] getLineDashes() {
		try{
			AssociationType type = getAssociation().getAssociationType();
			if("".equals(type.dashArrayLink)) return new double[]{};
			String[] dashesS = type.dashArrayLink.split(",");
			double[] dashes = new double[dashesS.length];
			for(int i = 0; i < dashesS.length; i++) {
				dashes[i] = Double.parseDouble(dashesS[i]);
			}
			return dashes;
		} catch (Exception e) {
//			System.err.println("getLineDashes FAIL: " + e.getMessage());
			return new double[] {10.,10.};
		}
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
		FmmlxAssociation assoc = getAssociation();
		return assoc!=null?assoc.getTargetDecoration():tool.clients.fmmlxdiagrams.Edge.HeadStyle.NO_ARROW;
	}

	@Override
	public tool.clients.fmmlxdiagrams.Edge.HeadStyle getSourceDecoration() {
		FmmlxAssociation assoc = getAssociation();
		return assoc!=null?assoc.getSourceDecoration():tool.clients.fmmlxdiagrams.Edge.HeadStyle.NO_ARROW;
	}
	
	public static FmmlxLink getFmmlxLink(FmmlxDiagram diagram, String source, String target, String name) {
		String nameString = name + "#" + source + "#" + target;
		Vector<FmmlxLink> associations = diagram.getFmmlxLinks();

		for (FmmlxLink link : associations) {
			if (link.sourceNode.name.equals(source)
					&& (link.getTargetNode().name.equals(target)) &&
					link.getName().equals(nameString)) {
				return link;
			}
		}
		return null;
	}	
}