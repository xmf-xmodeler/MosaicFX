package tool.clients.forms;

import java.io.PrintStream;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import tool.xmodeler.XModeler;

public class FormToolDef {

  String event;
  String id;
  String icon;
  String disabledIcon;
  boolean enabled;

  ToolItem item = null;
  
  public FormToolDef(String event, String id, String icon) {
    super();
    this.event = event;
    this.id = id;
    this.icon = icon;
    this.disabledIcon = icon;
    this.enabled = false;
  }
  
  public FormToolDef(String event, String id, String icon, String disabledIcon, boolean enabled) {
	    super();
	    this.event = event;
	    this.id = id;
	    this.icon = icon;
	    this.disabledIcon = disabledIcon;
	    this.enabled = enabled;
	  }

  public String getEvent() {
    return event;
  }

  public String getId() {
    return id;
  }

  public String getIcon() {
    return icon;
  }

  public void populateToolBar(ToolBar toolBar) {
//	item = new ToolItem(toolBar, SWT.PUSH);
//    ImageData imageData = new ImageData(icon);
//    Image image = new Image(XModeler.getXModeler().getDisplay(), imageData);
//    item.setImage(image);
//    ImageData disabledImageData = new ImageData(disabledIcon);
//    Image disabledImage = new Image(XModeler.getXModeler().getDisplay(), disabledImageData);
//    item.setDisabledImage(disabledImage);
//    setEnabled(enabled);
//    item.addSelectionListener(this);
  }
  
  public void setEnabled(boolean enabled){
//	  if(item != null && !item.isDisposed()){
//    	  this.enabled = enabled;
////		  final boolean enabled_local = enabled;
////		  FormsClient.theClient().runOnDisplay(new Runnable() {
////		      public void run() {
//		  		if (event.equals("lockForm")) {
////		  			System.out.println(enabled);
//		  			if(enabled ){
//		  			    ImageData imageData = new ImageData(disabledIcon);
//		  			    Image image = new Image(XModeler.getXModeler().getDisplay(), imageData);
//		  				item.setImage(image);
//		  			}else{
//		  				ImageData imageData = new ImageData(icon);
//		  			    Image image = new Image(XModeler.getXModeler().getDisplay(), imageData);
//		  				item.setImage(image);
//		  			}
//		  		}else{
//		    	  item.setEnabled(enabled);
//		  		}  
////		      }
////		    });
//	  }
  }
  
//  public void widgetDefaultSelected(SelectionEvent event) {
//  }
//
//  public void widgetSelected(SelectionEvent event) {
//	 FormsClient.theClient().toolItemEvent(this.event, id,enabled);
//  }

  public void writeXML(PrintStream out) {
    out.print("<FormToolDef id='" + getId() + "' event='" + getEvent() + "' icon = '" + getIcon() + "'/>");
  }
}
