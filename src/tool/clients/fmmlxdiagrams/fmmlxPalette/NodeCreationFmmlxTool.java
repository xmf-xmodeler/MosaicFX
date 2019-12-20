package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class NodeCreationFmmlxTool extends FmmlxTool implements ITool {
	
	private int level;

	public NodeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, int level, String icon) {
		super(diagram, label, toolId, icon);
		this.level=level;
		button = createButton();
	}

	public NodeCreationFmmlxTool(FmmlxDiagram diagram, String label, String toolId, String icon) {
		super(diagram, label, toolId, icon);
		button = createButton();
	}

//	@Override
	private TreeItem<String> createButton() {
		if(icon != null) {
			ImageView imageView = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		    button = new TreeItem<String>(label, imageView);
		    return button;
		} else {
			WritableImage image = new WritableImage(16, 16);
			PixelWriter pw = image.getPixelWriter();
			for(int x = 0; x < 16; x++) {
				for(int y = 0; y < 16; y++) {
					int color = Math.pow((Math.pow(x-8, 2) + Math.pow(y-8, 2)), 1/2.) < 6 ? (
							level == 5 ? 0xFFBB1133 : 
							level == 4 ? 0xFF4466EE : 
							level == 3 ? 0xFF000000 : 
							level == 2 ? 0xFFDDDDDD : 
							level == 1 ? 0xFF8C8C8C : 0xFFFF8800
					) : 0;
					pw.setArgb(x, y, color);
				}	
			}
			
			ImageView imageView = new ImageView(image);
		    button = new TreeItem<String>(label, imageView);
		    return button;
		}

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

	}

	@Override
	public void widgetSelected() {
		diagram.deselectPalette();
	    select();
	    diagram.setNodeCreationType(getId());	
	}
}
