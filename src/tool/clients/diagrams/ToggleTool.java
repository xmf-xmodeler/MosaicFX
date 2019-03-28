package tool.clients.diagrams;

import java.io.File;
import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class ToggleTool extends Tool {

  boolean state;
  String iconTrue;
  String iconFalse;
  transient Image imageTrue;
  transient Image imageFalse;

  public ToggleTool(Diagram diagram, String label, String id, boolean state, String iconTrue, String iconFalse) {
    super(diagram, label, id, state?iconTrue:iconFalse);
    this.state = state;
    this.iconTrue = iconTrue;
    this.iconFalse = iconFalse;
//    if (state)
//      select();
//    else unselect();
  }

  public TreeItem<String> createButton() { //TODO: all of this is pretty bad and redundant. I assume all extend Tool classes look like this
	    ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		button = new TreeItem<String>(label, image);	  
	  
//	  imageTrue = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + iconTrue));
//	  imageFalse = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + iconFalse));
//    Image image = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + icon));
//    Button button = new Button(parent, SWT.CHECK);
//    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//    button.setText(label);
//    button.setImage(image);
//    button.setLayoutData(data);
//    button.setFont(new Font(XModeler.getXModeler().getDisplay(), Group.defaultFont));
//    button.addSelectionListener(this);
//    button.pack();
    return button;
  }

  public void writeXML(PrintStream out) {
    out.print("<ToggleTool label='" + label + "'");
    out.print(" id='" + id + "'");
    out.print(" state='" + state + "'");
    out.print(" icon='" + iconTrue + "'");
    out.print(" icon2='" + iconFalse + "'/>");
  }

//  public void widgetDefaultSelected(SelectionEvent event) {
//  }
//
  public void widgetSelected() {
    toggle();
//    event.doit = false;
    // TODO //diagram.toggle(getId(), button.getSelection());
  }

  private void toggle() {
	  throw new RuntimeException("Button cannot be toggled yet.");
//    state = !state;
//    button.setSelection(state);
//    System.err.println("toggle: " + state);
//    String file = "icons/" + (state?iconTrue:iconFalse);
//    System.err.println(file);
//    button.setImage(new Image(XModeler.getXModeler().getDisplay(), new ImageData(file)));
//    System.err.println(button.getImage());
  }

  public String getType() {
    return "TOGGLE";
  }

  public void reset() {
    // Called by the palette to globally affect the state of the palette.
    // Ignore such requests since the toggle should reflect the state
    // of a boolean flag...
  }

//  private void unselect() {
//    button.setSelection(false);
//    button.setGrayed(false);
//    button.setImage(new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + iconFalse)));
//  }
//
//  public void select() {
//    button.setSelection(true);
//    button.setGrayed(true);
//    button.setImage(new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + iconTrue)));
//  }

}
