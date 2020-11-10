package tool.clients.fmmlxdiagrams.newpalette;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class PaletteGroupModels extends PaletteGroup {
	
	private Vector<PaletteItem> items= new Vector<>();

	public PaletteGroupModels(PaletteTool value) {
		super(value);
		setExpanded(true);
	}

	@Override
	public void populate(FmmlxDiagram fmmlxDiagram) {
		initTools(fmmlxDiagram);
		inflateInGroup();
		
	}

	private void inflateInGroup() {
		for (PaletteItem tmp : items) {
			getChildren().add(tmp);
		}		
	}

	private void initTools(FmmlxDiagram fmmlxDiagram) {
		PaletteTool auxillaryTools = new ToolModels(fmmlxDiagram, "Auxillary Classes", "auxillary", "");
		PaletteTool ordersTool = new ToolModels(fmmlxDiagram, "Orders", "orders", "");

		PaletteItem associationPaletteItem = new PaletteItem(auxillaryTools);
		PaletteItem associationInstancePaletteItem = new PaletteItem(ordersTool);

		items.add(associationPaletteItem);
		items.add(associationInstancePaletteItem);
		
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
