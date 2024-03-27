package tool.clients.fmmlxdiagrams.newpalette;
import tool.clients.fmmlxdiagrams.FmmlxDiagramView;

public abstract class PaletteTool {
	
		private FmmlxDiagramView diagram;	
		private String name;
		private String  path;
		private String  icon;
		private final int level;
		private final boolean isAbstract;

		public PaletteTool(FmmlxDiagramView diagram, String name, String id, int level, boolean isAbstract, String icon) {
		    super();
		    this.diagram = diagram;
		    this.name = name;
		    this.path = id;
		    this.level= level;
		    this.isAbstract=isAbstract;
		    this.icon = icon;
		}
	  
		public FmmlxDiagramView getDiagram() {
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
