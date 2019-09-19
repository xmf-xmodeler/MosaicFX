package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;

import java.awt.event.ActionListener;
import java.util.Vector;

public class FmmlxAssociation extends Edge implements FmmlxProperty {

	private final PropertyType propertyType = PropertyType.Association;
	private String name;
	private String reverseName;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
	private Integer levelStartToEnd;
	private Integer levelEndToStart;
	private Multiplicity multiplicityStartToEnd;
	private Multiplicity multiplicityEndToStart;

	FmmlxAssociation(
			Integer id,
			Integer startId,
			Integer endId,
			Integer parentAssociationId,
			Vector<Point2D> points,
			String name,
			String reverseName,
			String accessNameStartToEnd,
			String accessNameEndToStart,
			int levelStartToEnd,
			int levelEndToStart,
			Multiplicity multiplicityStartToEnd,
			Multiplicity multiplicityEndToStart,
			FmmlxDiagram diagram) {

		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, diagram);

		this.name = name;
		this.reverseName = reverseName;
		this.accessNameStartToEnd = accessNameStartToEnd;
		this.accessNameEndToStart = accessNameEndToStart;
		this.levelStartToEnd = levelStartToEnd;
		this.levelEndToStart = levelEndToStart;
		this.multiplicityStartToEnd = multiplicityStartToEnd;
		this.multiplicityEndToStart = multiplicityEndToStart;
		
		layout();
	}
	
	private enum Anchor {SOURCE,CENTRE,TARGET};
//	private enum LabelType {FW_NAME,RV_NAME, ACCESS_S_T, ACCESS_T_S, LEVEL_S_T, LEVEL_T_S, MULT_S_T, MULT_T_S};

	private void layout() {
		createLabel(name, Anchor.CENTRE, ()->{System.err.println("Huhu!");}, 0);
		if(reverseName != null) 
	    createLabel(reverseName, Anchor.CENTRE, ()->{System.err.println("Huhu!");}, 20);
		createLabel(accessNameStartToEnd, Anchor.TARGET, ()->{System.err.println("Huhu!");}, 0);
		createLabel(accessNameEndToStart, Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 0);
		createLabel(""+levelStartToEnd, Anchor.TARGET, ()->{System.err.println("Huhu!");}, 20);
		createLabel(""+levelEndToStart, Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 20);
		createLabel(multiplicityStartToEnd.toString(), Anchor.TARGET, ()->{System.err.println("Huhu!");}, 40);
		createLabel(multiplicityEndToStart.toString(), Anchor.SOURCE, ()->{System.err.println("Huhu!");}, 40);
	}

	private void createLabel(String value, Anchor anchor, Runnable action, int yDiff) {
//		double x = (getSourceNode().getX() + getSourceNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getX() + getTargetNode().getWidth()  / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
//		double y = (getSourceNode().getY() + getSourceNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.8:anchor==Anchor.TARGET?0.2:0.5)
//				 + (getTargetNode().getY() + getTargetNode().getHeight() / 2) * (anchor==Anchor.SOURCE?0.2:anchor==Anchor.TARGET?0.8:0.5);
		double w = Math.max(20, diagram.calculateTextWidth(value));
		double h = diagram.calculateTextHeight();
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add(getSourceNode());
		if(anchor!=Anchor.SOURCE) anchors.add(getTargetNode());
		diagram.addLabel(new DiagramLabel(this, action, null, anchors, value, 50, -100+yDiff, w, h));
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		super.paintOn(g, xOffset, yOffset, fmmlxDiagram);
//		if (name != null) {
//			Point2D centreAnchor = getCentreAnchor();
//			g.fillText(name, centreAnchor.getX(), centreAnchor.getY() - 10);
//		}
	}

	@Override
	public String getName() {
		return name;
	}

	public String getReverseName() {
		return reverseName;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public int getId() {
		return id;
	}

	public FmmlxObject getSourceNode() {
		return startNode;
	}

	public FmmlxObject getTargetNode() {
		return endNode;
	}

	public Integer getLevelStartToEnd() {
		return levelStartToEnd;
	}

	public Integer getLevelEndToStart() {
		return levelEndToStart;
	}

	public String getAccessNameStartToEnd() {
		return accessNameStartToEnd;
	}

	public String getAccessNameEndToStart() {
		return accessNameEndToStart;
	}

	public Multiplicity getMultiplicityStartToEnd() {
		return multiplicityStartToEnd;
	}

	public Multiplicity getMultiplicityEndToStart() {
		return multiplicityEndToStart;
	}

	public String associationToPair() {
		String firstString = this.getSourceNode().getName();
		String seconString = this.getTargetNode().getName();
		return "( " + firstString + " ; " + seconString + " )";
	}

	public boolean doObjectsFit(FmmlxObject source, FmmlxObject target) {
		if (source.isInstanceOf(getSourceNode(), levelEndToStart) && target.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		if (target.isInstanceOf(getSourceNode(), levelEndToStart) && source.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		return false;
	}

	@Override
	public ContextMenu getContextMenu(DiagramActions actions) {
		return new AssociationContextMenu(this, actions); //temporary
	}

	@Override
	protected Double getLineDashes() {
		return new Double(0);
	}
}
