package tool.clients.fmmlxdiagrams.menus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class DefaultContextMenu extends ContextMenu {

	public DefaultContextMenu() {
		setAutoHide(true);
		MenuItem item1 = new MenuItem("Test unselected");
		item1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.println("UNSELECTEDCONTEXTMENU ITEM1");
				
			}
		});
		MenuItem item2 = new MenuItem("Test2");
		
		getItems().addAll(item1, item2);
	}
}
