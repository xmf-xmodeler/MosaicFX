package tool.helper.FXAuxilary;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class JavaFxButtonAuxilary {

	public static Button createButton(String text, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(text);
		button.setOnAction(eventHandler);
		return button;
	}

	public static Button createButtonWithGraphic(String text, EventHandler<ActionEvent> eventHandler, Node node) {
		Button button = createButton(text, eventHandler);
		button.setGraphic(node);
		return button;
	}
}
