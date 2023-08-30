package tool.helper.auxilaryFX;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
	
	public static Button createButtonWithPicture(String text, EventHandler<ActionEvent> eventHandler, String file) {
		Button button = createButton(text, eventHandler);
		button.setGraphic(new ImageView(new Image(new File(file).toURI().toString())));
		return button;
	}
}
