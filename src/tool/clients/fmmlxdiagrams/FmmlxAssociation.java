package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;

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
//			FmmlxObject startNode, FmmlxObject endNode, 
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


	}


	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		super.paintOn(g, xOffset, yOffset, fmmlxDiagram);
		if (name != null) {
			Point2D centreAnchor = getCentreAnchor();
			g.fillText(name, centreAnchor.getX(), centreAnchor.getY() - 10);
		}
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


	public String toPair() {
		String firstString = this.getSourceNode().getName();
		String seconString = this.getTargetNode().getName();
		return "( " + firstString + " ; " + seconString + " )";
	}
	
	public Vector<FmmlxAssociationInstance> getInstance(){
		return diagram.getAssociationInstance();
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
