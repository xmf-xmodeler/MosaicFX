package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class NodeCreationFmmlxTool extends FmmlxTool implements ITool {
	
	private int level;

	public NodeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, int level, String icon) {
		super(diagram, label, toolId, icon);
		this.level=level;
	}

	public NodeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, String icon) {
		super(diagram, label, toolId, icon);
	}

	@Override
	public TreeItem<String> createButton() {
		ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		button = new TreeItem<String>(label, image);
		
	    return button;
	}


	@Override
	public String getType() {
		return "NODE";
	}

	@Override
	public void reset() {
		
		
	}

	@Override
	public void select() {
		diagram.deselectPalette();
	    select();
	    diagram.setNodeCreationType(getId());
	}

	@Override
	public void widgetSelected() {
		// TODO Auto-generated method stub
		
	}
}
