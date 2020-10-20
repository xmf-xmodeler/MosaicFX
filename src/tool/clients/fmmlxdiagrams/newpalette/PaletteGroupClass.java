package tool.clients.fmmlxdiagrams.newpalette;

import java.util.List;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.SortedValue;

public class PaletteGroupClass extends PaletteGroup {
	
	private Vector<PaletteItem> items= new Vector<>();

	public PaletteGroupClass(PaletteTool value) {
		super(value);
		setExpanded(true);
	}

	@Override
	public void populate(FmmlxDiagram fmmlxDiagram) {
		initTools(fmmlxDiagram);
		inflateInGroup();
		
	}

	private void initTools(FmmlxDiagram fmmlxDiagram) {
		
		PaletteTool metaClassTool = new ToolClass(fmmlxDiagram, "MetaClass", "metaClass", 1000, false, "");
		PaletteItem metaClassPaletteItem = new PaletteItem(metaClassTool);
		items.add(metaClassPaletteItem);
		
		List<FmmlxObject> objects = fmmlxDiagram.getSortedObject(SortedValue.REVERSE);
		for (FmmlxObject tmp : objects) {
			items.add(tmp.toPaletteItem(fmmlxDiagram));			
		}		
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
