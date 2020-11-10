package tool.clients.fmmlxdiagrams.newpalette;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class PaletteTool {
	
		private FmmlxDiagram diagram;	
		private String name;
		private String  id;
		private String  icon;
		private final int level;
		private final boolean isAbstract;

		public PaletteTool(FmmlxDiagram diagram, String name, String id, int level, boolean isAbstract, String icon) {
		    super();
		    this.diagram = diagram;
		    this.name = name;
		    this.id = id;
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
			this.id = text;
		}

		public String getId() {
			return id;
		}

		public boolean isAbstract() {
			return isAbstract;
		}

		public abstract void widgetSelected();

}
