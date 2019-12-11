package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.File;
import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class FmmlxActionTool extends FmmlxTool {

	public FmmlxActionTool(FmmlxDiagram diagram, String label, String id, String icon) {
		super(diagram, label, id, icon);
	}

	@Override
	public TreeItem<String> createButton() {
		ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		button = new TreeItem<String>(label, image);
		
		return button;
	}

	@Override
	public void writeXML(PrintStream out) {
		out.print("<ActionTool label='" + label + "'");
	    out.print(" id='" + id + "'");
	    out.print(" icon='" + icon + "'/>");
	}

	@Override
	public String getType() {
		return "FMMLXACTION";
	}
	
	

}
