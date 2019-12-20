package tool.clients.fmmlxdiagrams.newpalette;

import javafx.scene.control.TreeItem;
import tool.clients.diagrams.Tool;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class FmmlxTool {
	
		public FmmlxDiagram diagram;	
		public String  label;
		public String  id;
		public String  icon;
		public TreeItem<String>  button;

		public FmmlxDiagram getDiagram() {
			return diagram;
		}

		public FmmlxTool(FmmlxDiagram diagram, String label, String id, String icon) {
		    super();
		    this.diagram = diagram;
		    this.label = label;
		    this.id = id;
		    this.icon = icon;
		    button = createButton();
		}
	  
		public String getIcon() {
			return icon;
		}

		public String getLabel() {
		    return label;
		}
		
		  
		public void setID(String text) {
			this.label = text;
			this.id = text;
			getButton().setValue(text);
		}

		public String getId() {
			return id;
		}

		public TreeItem<String> getButton() {
			return button;
		}


		public abstract TreeItem<String> createButton();


		public void delete() {
			new RuntimeException("Button cannot be deleted yet.");
//	    button.dispose();
		}

		protected abstract String getType();

		protected abstract void reset();

		public abstract void widgetSelected();

}
