package tool.clients.fmmlxdiagrams;

import java.util.Optional;
import java.util.Vector;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

public class DelegationEdge extends Edge<FmmlxObject> {

	public DelegationEdge(String path, String childPath, String parentPath, Integer level, Vector<Point2D> intermediatePoints,
			PortRegion startPortRegion, PortRegion endPortRegion,
			AbstractPackageViewer diagram) {
		super(path, diagram.getObjectByPath(childPath), diagram.getObjectByPath(parentPath), intermediatePoints, startPortRegion, endPortRegion, new Vector<>(),
				diagram);
		this.level = level;
	}
	
	public Integer level;

	protected void checkVisibilityMode() {visible = true;}
	
	@Override
	protected void layoutLabels(FmmlxDiagram diagram) {
		createLabel(""+level, 2, Anchor.TARGET_LEVEL, showChangeLevelDialog, Color.WHITE, getPrimaryColor(), diagram);

		layoutingFinishedSuccesfully = true;
	} // NONE
	
	private final Runnable showChangeLevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(""+level);
		td.setHeaderText("Change Delegation Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.getComm().changeDelegationLevel(diagram.getID(), sourceEnd.getNode().getName(), level);
				diagram.updateDiagram();
			} catch (Exception e) {
				System.err.println("Number not readable. Change Nothing.");
			}
		}
	};

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		MenuItem deleteDelegationItem = new MenuItem("delete");
		ContextMenu menu = new ContextMenu();
		menu.getItems().add(deleteDelegationItem);
		deleteDelegationItem.setOnAction(e -> {
			diagram.getComm().removeDelegation(diagram.getID(), sourceEnd.getNode().getName());
		    diagram.updateDiagram();
		});
		return menu;
	}

	protected Color getPrimaryColor() {
		return new Color(.1, .2, .7, 1.);
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
