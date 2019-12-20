package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class PaletteTool {
	
		public FmmlxDiagram diagram;	
		public String  label;
		public String  id;
		public String  icon;
		public TreeItem<String>  button;

		public FmmlxDiagram getDiagram() {
			return diagram;
		}

		public PaletteTool(FmmlxDiagram diagram, String label, String id, String icon) {
		    super();
		    this.diagram = diagram;
		    this.label = label;
		    this.id = id;
		    this.icon = icon;
		}
	  
		public String getIcon() {
			return icon;
		}

		public String getLabel() {
		    return label;
		}
		
		  
		public void setID(String text) {
			this.id = text;
		}

		public String getId() {
			return id;
		}

		public abstract void widgetSelected();

}
