package tool.clients.editors;

import javafx.scene.Node;
import javafx.scene.control.TabPane;

public class EditorClientView {

	private final TabPane tabPane;
	
	public EditorClientView() {
		tabPane = new TabPane();
		
	}

	public Node getView() {
		return tabPane;
	}
}
