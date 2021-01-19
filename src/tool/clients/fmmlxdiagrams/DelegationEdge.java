package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;

public class DelegationEdge extends Edge {

	public DelegationEdge(String path, String childPath, String parentPath, Integer level, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(childPath), diagram.getObjectByPath(parentPath), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
		this.level = level;
	}
	
	private Integer level;

	protected void checkVisibilityMode() {visible = true;}
	
	@Override
	protected void layoutLabels(FmmlxDiagram diagram) {
		createLabel(""+level, 2, Anchor.TARGET_LEVEL, ()->{}, Color.WHITE, getPrimaryColor(), diagram);

		layoutingFinishedSuccesfully = true;
	} // NONE

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new ContextMenu();
	}

	protected Color getPrimaryColor() {
		return new Color(.1, .2, .7, 1.);
	}

	@Override
	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		super.intermediatePoints = intermediatePoints;
	}

	@Override
	public HeadStyle getTargetDecoration() {
		return HeadStyle.NO_ARROW;
	}

	@Override
	public HeadStyle getSourceDecoration() {
		return HeadStyle.CIRCLE;
	}
	
	@Override
	public String getName() {
		return "doesNotMatter";
	}
}
