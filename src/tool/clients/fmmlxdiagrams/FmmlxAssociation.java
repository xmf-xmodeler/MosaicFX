package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;

public class FmmlxAssociation extends Edge{

	public FmmlxAssociation(
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

	private String name;
	private String reverseName;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
	private Multiplicity multiplicityStartToEnd;
	private Multiplicity multiplicityEndToStart;


}
