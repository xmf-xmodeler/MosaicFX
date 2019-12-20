package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ToolRoot extends FmmlxTool {

	public ToolRoot(FmmlxDiagram diagram, String label, String id, String icon) {
		super(diagram, label, id, icon);
	}

	@Override
	public TreeItem<String> createButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected() {
		// TODO Auto-generated method stub

	}

}
