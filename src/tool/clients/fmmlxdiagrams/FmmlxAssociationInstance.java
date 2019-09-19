package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.menus.AssociationInstanceContextMenu;

import java.util.Vector;

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
}
