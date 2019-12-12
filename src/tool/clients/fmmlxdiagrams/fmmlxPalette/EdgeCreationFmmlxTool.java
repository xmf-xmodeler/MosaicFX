package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.File;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class EdgeCreationFmmlxTool extends FmmlxTool {

	public EdgeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, String icon) {
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
		return "EDGE";
	}

}
