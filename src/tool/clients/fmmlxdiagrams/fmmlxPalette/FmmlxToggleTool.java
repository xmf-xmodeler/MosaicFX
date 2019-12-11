package tool.clients.fmmlxdiagrams.fmmlxPalette;

import java.io.File;
import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import tool.clients.diagrams.Image;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class FmmlxToggleTool extends FmmlxTool {
	
	boolean state;
	String iconTrue;
	String iconFalse;
	transient Image imageTrue;
	transient Image imageFalse;

	public FmmlxToggleTool(FmmlxDiagram fmmlxDiagram, String label, String toolId, boolean state, String iconTrue,
			String iconFalse) {
		super(fmmlxDiagram, label, toolId, state?iconTrue:iconFalse);
	    this.state = state;
	    this.iconTrue = iconTrue;
	    this.iconFalse = iconFalse;
	}

	@Override
	public TreeItem<String> createButton() {
		ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		button = new TreeItem<String>(label, image);	  
		return button;
	}

	@Override
	public void writeXML(PrintStream out) {
		out.print("<ToggleTool label='" + label + "'");
		out.print(" id='" + id + "'");
		out.print(" state='" + state + "'");
		out.print(" icon='" + iconTrue + "'");
		out.print(" icon2='" + iconFalse + "'/>");
	}

	public void widgetSelected() {
		toggle();
	}
	
	private void toggle() {
		throw new RuntimeException("Button cannot be toggled yet.");
	}
	  
	@Override
	public String getType() {
		return "FMMLXTOGGLE";
	}

}
