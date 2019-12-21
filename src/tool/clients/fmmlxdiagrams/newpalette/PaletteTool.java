package tool.clients.fmmlxdiagrams.newpalette;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class PaletteTool {
	
		public FmmlxDiagram diagram;	
		public String  label;
		public String  id;
		public String  icon;
		public final int level;

		public PaletteTool(FmmlxDiagram diagram, String label, String id, int level, String icon) {
		    super();
		    this.diagram = diagram;
		    this.label = label;
		    this.id = id;
		    this.level= level;
		    this.icon = icon;
		}
	  
		public FmmlxDiagram getDiagram() {
			return diagram;
		}
		
		public String getIcon() {
			return icon;
		}

		public String getLabel() {
		    return label;
		}
		
		public int getLevel() {
			return level;
		}
		
		  
		public void setID(String text) {
			this.id = text;
		}

		public String getId() {
			return id;
		}

		public abstract void widgetSelected();

}
