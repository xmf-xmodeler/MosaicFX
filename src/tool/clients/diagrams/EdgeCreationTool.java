package tool.clients.diagrams;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tool.xmodeler.XModeler;

public class EdgeCreationTool extends Tool {

  public EdgeCreationTool(Composite parent, Diagram diagram, String label, String id, String icon) {
    super(parent, diagram, label, id, icon);
  }

  public Button createButton(Composite parent) {
    Image image = new Image(XModeler.getXModeler().getDisplay(), new ImageData("icons/" + icon));
    Button button = new Button(parent, SWT.CHECK);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    button.setText(label);
    button.setImage(image);
    button.setLayoutData(data);
    button.setFont(new Font(XModeler.getXModeler().getDisplay(), Group.defaultFont));
    button.addSelectionListener(this);
    button.pack();
    return button;
  }

  public void writeXML(PrintStream out) {
    out.print("<EdgeCreationTool label='" + label + "'");
    out.print(" id='" + id + "'");
    out.print(" icon='" + icon + "'/>");
  }

  public void widgetDefaultSelected(SelectionEvent event) {
  }

  public void widgetSelected(SelectionEvent event) {
    diagram.deselectPalette();
    select();
    diagram.setEdgeCreationType(getId());
  }

  public String getType() {
    return "EDGE";
  }

  public void reset() {
    button.setSelection(false);
    button.setGrayed(false);
  }

  public void select() {
    button.setSelection(true);
    button.setGrayed(true);
  }

}
