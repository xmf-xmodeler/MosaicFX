package tool.helper.auxilaryFX;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class JavaFxMenuAuxiliary {
	
	public static MenuItem addMenuItem(Menu menu, String string, EventHandler<ActionEvent> eventHandler) {
		MenuItem item = new MenuItem(string);
		item.setOnAction(eventHandler);
		menu.getItems().add(item);
		return item;
	}
}
