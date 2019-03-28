package tool.clients.diagrams;

import java.io.File;
import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class EdgeCreationTool extends Tool { //TODO: why is this an abstract class when you end up with redundant code?

  public EdgeCreationTool(Diagram diagram, String label, String id, String icon) {
    super(diagram, label, id, icon);
  }

  public TreeItem<String> createButton() {
    ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
	button = new TreeItem<String>(label, image);
		
//    Image image = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + icon));
//    Button button = new Button(parent, SWT.CHECK);
//    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//    button.setText(label);
//    button.setImage(image);
//    button.setLayoutData(data);
////    button.setFont(new Font(XModeler.getXModeler().getDisplay(), Group.defaultFont));
//    button.addSelectionListener(this);
//    button.pack();
    return button;
  }

  public void writeXML(PrintStream out) {
    out.print("<EdgeCreationTool label='" + label + "'");
    out.print(" id='" + id + "'");
    out.print(" icon='" + icon + "'/>");
  }

  public void widgetDefaultSelected() {
  }

  public void widgetSelected() {
    diagram.deselectPalette();
    select();
    diagram.setEdgeCreationType(getId());
  }

  public String getType() {
    return "EDGE";
  }

//  public void reset() {
//    button.setSelection(false);
//    button.setGrayed(false);
//  }
//
//  public void select() {
//    button.setSelection(true);
//    button.setGrayed(true);
//  }

}
