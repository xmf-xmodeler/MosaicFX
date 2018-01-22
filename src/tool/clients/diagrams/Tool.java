package tool.clients.diagrams;

import java.io.PrintStream;

import javafx.scene.control.TreeItem;

public abstract class Tool {

  // A tool is a button in a palette-group. Tools have labels that are displayed
  // and have identifiers that are used in events sent to XMF. Some tools perform
  // actions directly and some will set a mode in the diagram.

  Diagram diagram;
  String  label;
  String  id;
  String  icon;
  TreeItem<String>  button;

  public Tool(Diagram diagram, String label, String id, String icon) {
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

  public abstract void writeXML(PrintStream out);

  public abstract String getType();

  public /*abstract*/ void reset() {/*throw new RuntimeException("Not implemented yet.");*/}

  public /*abstract*/ void select() {/*throw new RuntimeException("Not implemented yet.");*/}
  
  public /*abstract*/ void widgetSelected() {System.err.println("widgetSelected");}

  public void delete() {
	  new RuntimeException("Button cannot be deleted yet.");
//    button.dispose();
  }
}
