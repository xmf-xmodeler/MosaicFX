package tool.clients.forms;

import java.io.PrintStream;
import java.util.Hashtable;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import tool.xmodeler.XModeler;

public class List {

  String                       id;
  Hashtable<String, String>    items = new Hashtable<String, String>();
  ListView<String>             list; 

  public List(String id, AnchorPane parent, int x, int y, int width, int height) {

    this.id = id;
    list = new ListView<String>();
    AnchorPane.setLeftAnchor(list, x*1.);
    AnchorPane.setTopAnchor(list, y*1.);
    list.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent click) {
		    if (click.getClickCount() == 2) {
		    	FormsClient.theClient().doubleClick(getId(list.getSelectionModel().getSelectedItem()));}}});
    parent.getChildren().add(list);

  }

  public void add(String id, String value) {
    list.getItems().add(value);
    items.put(id, value);
  }

  public String getId() {
    return id;
  }

  public void writeXML(PrintStream out) {
    out.print("<List id='" + getId() + "'");
    out.print(" x='" + (int)(list.getLayoutX()) + "'");
    out.print(" y='" + (int)(list.getLayoutY()) + "'");
    out.print(" width='" + (int)(list.getWidth()) + "'");
    out.print(" height='" + (int)(list.getHeight()) + "'>");
    for (String id : items.keySet())
      out.print("<Item id='" + id + "' value='" + XModeler.encodeXmlAttribute(items.get(id)) + "'/>");
    out.print("</List>");
  }

  public void clear() {
	System.err.println("Clear List " + id + "(" + list.getItems() + ")");
    list.getItems().clear();
	System.err.println("Cleared List " + id + "(" + list.getItems() + ")");
    items.clear();
  }

  private String getId(String string) {
    for (String id : items.keySet())
      if (items.get(id).equals(string)) return id;
    return null;
  }

}
