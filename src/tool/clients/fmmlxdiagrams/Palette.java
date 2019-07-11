package tool.clients.fmmlxdiagrams;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class Palette extends ToolBar {

	public Palette(DiagramActions actions) {
		setPadding(new Insets(10, 10, 10, 10));
		setOrientation(Orientation.HORIZONTAL);

		getItems().add(new Label("Zoom"));
		addButton("+", e -> actions.zoomIn());
		addButton("100%", e -> actions.zoomOne());
		addButton("-", e -> actions.zoomOut());
		getItems().add(new Separator());
		addButton("Show Operations", e -> actions.toogleShowOperations());
		addButton("Show Operation Values", e -> actions.toogleShowOperationValues());
		addButton("Show Slots", e -> actions.toogleShowSlots());
		getItems().add(new Separator());
		addButton("Update Diagram", e -> actions.updateDiagram());
	}

	private void addButton(String string, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		getItems().add(button);
	}
}
