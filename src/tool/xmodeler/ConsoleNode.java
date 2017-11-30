package tool.xmodeler;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class ConsoleNode extends ScrollPane {

	public ConsoleNode() {
		TextArea textArea = new TextArea();
		getChildren().add(textArea);
	}
	
}
