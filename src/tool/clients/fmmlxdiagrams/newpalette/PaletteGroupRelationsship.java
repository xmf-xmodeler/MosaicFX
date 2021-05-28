package tool.clients.fmmlxdiagrams.newpalette;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class PaletteGroupRelationsship extends PaletteGroup {
	
	private Vector<PaletteItem> items= new Vector<>();

	public PaletteGroupRelationsship(PaletteTool value) {
		super(value);
		setExpanded(true);
	}

	@Override
	public void populate(FmmlxDiagram fmmlxDiagram) {
		initTools(fmmlxDiagram);
		inflateInGroup();
		
	}

	private void initTools(FmmlxDiagram fmmlxDiagram) {
		PaletteTool associationTool = new ToolRelationsship(fmmlxDiagram, "Association", "association", "resources/gif/Association.gif");
		PaletteTool associationInstanceTool = new ToolRelationsship(fmmlxDiagram, "Link", "associationInstance","resources/gif/Association.gif");
//		PaletteTool spezializationTool = new ToolRelationsship(fmmlxDiagram, "Spezialization", "spezialization", "resources/gif/Inheritance.gif");
//		PaletteTool delegationTool = new ToolRelationsship(fmmlxDiagram, "Delegation", "delegation", "resources/gif/XCore/Delegation.png");
		
		PaletteItem associationPaletteItem = new PaletteItem(associationTool);
		PaletteItem associationInstancePaletteItem = new PaletteItem(associationInstanceTool);
//		PaletteItem spezialization = new PaletteItem(spezializationTool);
//		PaletteItem delegation = new PaletteItem(delegationTool);
		
		items.add(associationPaletteItem);
		items.add(associationInstancePaletteItem);
//		items.add(spezialization);
//		items.add(delegation);
	}

	private void inflateInGroup() {
		for (PaletteItem tmp : items) {
			getChildren().add(tmp);
		}
	}

	@Override
	public void clearTreeItem() {
		getChildren().clear();	
		
	}

	@Override
	public void clearTool() {
		items.clear();
		
	}

}
