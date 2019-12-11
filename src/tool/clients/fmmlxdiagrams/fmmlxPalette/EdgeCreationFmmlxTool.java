package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class EdgeCreationFmmlxTool extends FmmlxTool {

	public EdgeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, String icon) {
		super(diagram, label, toolId, icon);
	}

	@Override
	public TreeItem<String> createButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeXML(PrintStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
