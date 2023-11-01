package tool.clients.fmmlxdiagrams.newpalette;

import java.util.Collections;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

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
		
		PaletteTool metaClassTool = new ToolClass(fmmlxDiagram, "MetaClass", "MetaClass", 1000, false, "");
		PaletteItem metaClassPaletteItem = new PaletteItem(metaClassTool);
		items.add(metaClassPaletteItem);
		
		Vector<FmmlxObject> objects = new Vector<>(fmmlxDiagram.getObjectsReadOnly());
		Collections.sort(objects);
		Collections.reverse(objects);
		
		for (FmmlxObject tmp : objects) {
			if(tmp.getLevel().isClass()){
				items.add(tmp.toPaletteItem(fmmlxDiagram));
			}
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
