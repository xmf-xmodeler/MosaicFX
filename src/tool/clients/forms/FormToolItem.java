package tool.clients.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import tool.xmodeler.XModeler;

public class FormToolItem extends ToolItem implements SelectionListener {

  String id;
  String event;
  String icon;

  public FormToolItem(ToolBar parent, String event, String id, String icon) {
    super(parent, SWT.HORIZONTAL);
    this.id = id;
    this.event = event;
    this.icon = icon;
    Image image = new Image(XModeler.getXModeler().getDisplay(), new ImageData(icon));
    setImage(image);
    addSelectionListener(this);
  }

  public void widgetDefaultSelected(SelectionEvent event) {
  }

  public void widgetSelected(SelectionEvent event) {
    FormsClient.theClient().toolItemEvent(this.event, id);
  }

}
