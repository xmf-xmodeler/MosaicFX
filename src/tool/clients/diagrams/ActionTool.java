package tool.clients.diagrams;

import java.io.File;
import java.io.PrintStream;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

public class ActionTool extends Tool {

  public ActionTool(Diagram diagram, String label, String id, String icon) {
    super(diagram, label, id, icon);
  }

  public TreeItem<String> createButton() {
		ImageView image = new ImageView(new javafx.scene.image.Image(new File(icon).toURI().toString()));
		button = new TreeItem<String>(label, image);
		
//    Image image = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + icon));
//    Button button = new Button(parent, SWT.FLAT);
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
    out.print("<ActionTool label='" + label + "'");
    out.print(" id='" + id + "'");
    out.print(" icon='" + icon + "'/>");
  }

  public void widgetDefaultSelected() {
  }

  @Override
  public void widgetSelected() {
    diagram.action(getId());
  }

  public String getType() {
    return "ACTION";
  }

  public void reset() {
  }

  public void select() {
  }

}
