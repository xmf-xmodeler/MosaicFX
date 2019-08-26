package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class FmmlxAssociationInstance extends Edge {
	
	FmmlxAssociation ofAssociation;

	public FmmlxAssociationInstance(int id, int startId, int endId, int ofId, Vector<Point2D> points,
			FmmlxDiagram diagram) {
		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, diagram);
		this.ofAssociation = (FmmlxAssociation) diagram.getAssociationById(ofId);
	}
	
	@Override
	protected Color getPrimaryColor() {
		return Color.GRAY;
	}

}
