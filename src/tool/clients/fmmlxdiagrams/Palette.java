package tool.clients.fmmlxdiagrams;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

public class Palette extends ToolBar {

	public Palette(DiagramActions actions) {
		setPadding(new Insets(10, 10, 10, 10));
		setOrientation(Orientation.HORIZONTAL);

		getItems().add(new Label("Zoom"));
		addButton("+", e -> actions.zoomIn());
		addButton("100%", e -> actions.zoomOne());
		addButton("-", e -> actions.zoomOut());
		getItems().add(new Separator());
		getItems().add(new Label("Show: "));
		addCheckBox("Operations", e -> actions.toggleShowOperations());
		addCheckBox("Operation Values", e -> actions.toggleShowOperationValues());
		addCheckBox("Slots", e -> actions.toggleShowSlots());
		getItems().add(new Separator());
		addButton("Update Diagram", e -> actions.updateDiagram());
	}

	private void addButton(String string, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		getItems().add(button);
	}

	private void addCheckBox(String string, EventHandler<ActionEvent> eventHandler) {
		CheckBox box = new CheckBox(string);
		box.setOnAction(eventHandler);
		box.setSelected(true);
		getItems().add(box);
	}
}
