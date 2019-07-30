package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

import java.util.Vector;

public class FmmlxAssociation extends Edge implements FmmlxProperty {

	private final PropertyType propertyType = PropertyType.Association;
	private String name;
	private String reverseName;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
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

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}
}
