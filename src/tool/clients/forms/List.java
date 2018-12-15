package tool.clients.forms;

import java.io.PrintStream;
import java.util.Hashtable;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import tool.xmodeler.XModeler;

public class List {

	String id;
	Hashtable<String, String> items = new Hashtable<String, String>();
	ListView<String> listView;

	public List(String id, GridPane parent, int rowIndex, String labelText) {
		if(rowIndex<0) {
			rowIndex = 0;
		}

		this.id = id;
		listView = new ListView<String>();
		listView.setMaxHeight(200);
		
		Label label = new Label(labelText);
		
		listView.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					FormsClient.theClient().doubleClick(getId(listView.getSelectionModel().getSelectedItem()));
				}
			}
		});
		// TODO: add label Collection Slots
		parent.add(label, 0, rowIndex);
		parent.add(listView, 0, rowIndex+1);
	}

	public void add(String id, String value) {
		listView.getItems().add(value);
		items.put(id, value);
	}

	public String getId() {
		return id;
	}

	public void writeXML(PrintStream out) {
		out.print("<List id='" + getId() + "'");
		out.print(" x='" + (int) (listView.getLayoutX()) + "'");
		out.print(" y='" + (int) (listView.getLayoutY()) + "'");
		out.print(" width='" + (int) (listView.getWidth()) + "'");
		out.print(" height='" + (int) (listView.getHeight()) + "'>");
		for (String id : items.keySet())
			out.print("<Item id='" + id + "' value='" + XModeler.encodeXmlAttribute(items.get(id)) + "'/>");
		out.print("</List>");
	}

	public void clear() {
		System.err.println("Clear List " + id + "(" + listView.getItems() + ")");
		listView.getItems().clear();
		System.err.println("Cleared List " + id + "(" + listView.getItems() + ")");
		items.clear();
	}

	private String getId(String string) {
		for (String id : items.keySet())
			if (items.get(id).equals(string))
				return id;
		return null;
	}

}
