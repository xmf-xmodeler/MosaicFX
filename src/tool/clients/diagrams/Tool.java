package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class Tool implements SelectionListener {

  // A tool is a button in a palette-group. Tools have labels that are displayed
  // and have identifiers that are used in events sent to XMF. Some tools perform
  // actions directly and some will set a mode in the diagram.

  Diagram diagram;
  String  label;
  String  id;
  String  icon;
  Button  button;

  public Tool(Composite container, Diagram diagram, String label, String id, String icon) {
    super();
    this.diagram = diagram;
    this.label = label;
    this.id = id;
    this.icon = icon;
    button = createButton(container);
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
	  getButton().setText(text);
  }

  public String getId() {
    return id;
  }

  public Button getButton() {
    return button;
  }

  public abstract Button createButton(Composite container);

  public abstract void writeXML(PrintStream out);

  public abstract String getType();

  public abstract void reset();

  public abstract void select();

  public void delete() {
    button.dispose();
  }
}
