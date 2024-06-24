package tool.clients.fmmlxdiagrams.newpalette;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public abstract class PaletteTool {
	
		private FmmlxDiagram diagram;	
		private String name;
		private String  path;
		private String  icon;
		private final int level;
		private final boolean isAbstract;

		public PaletteTool(FmmlxDiagram diagram, String name, String id, int level, boolean isAbstract, String icon) {
		    super();
		    this.diagram = diagram;
		    this.name = name;
		    this.path = id;
		    this.level= level;
		    this.isAbstract=isAbstract;
		    this.icon = icon;
		}
	  
		public FmmlxDiagram getDiagram() {
			return diagram;
		}
		
		public String getIcon() {
			return icon;
		}

		public String getName() {
		    return name;
		}
		
		public int getLevel() {
			return level;
		}
		
		  
		public void setID(String text) {
			this.path = text;
		}

		public String getId() {
			return path;
		}

		public boolean isAbstract() {
			return isAbstract;
		}

		public abstract void widgetSelected();

}
