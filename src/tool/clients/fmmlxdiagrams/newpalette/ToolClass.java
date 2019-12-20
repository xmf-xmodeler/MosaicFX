package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ToolClass extends FmmlxTool implements ITool {

	public ToolClass(FmmlxDiagram fmmlxDiagram, String label, String id, String icon) {
		super(fmmlxDiagram, label, id, icon);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TreeItem<String> createButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void select() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected() {
		diagram.deselectPalette();
		select();
		diagram.setNodeCreationType(getId());		
	}



}
