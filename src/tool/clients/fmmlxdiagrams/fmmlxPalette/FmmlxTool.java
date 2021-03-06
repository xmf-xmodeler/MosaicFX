package tool.clients.fmmlxdiagrams.fmmlxPalette;

import javafx.scene.control.TreeItem;
import tool.clients.diagrams.Tool;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public abstract class FmmlxTool {
	
		FmmlxDiagram diagram;	
		String  label;
		String  id;
		String  icon;
		TreeItem<String>  button;

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
			System.out.println("ID : "+id);
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

		protected abstract void widgetSelected();

}
