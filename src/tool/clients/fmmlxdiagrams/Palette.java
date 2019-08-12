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
		addCheckbox("Operations", e -> actions.toggleShowOperations());
		addCheckbox("Operation Values", e -> actions.toggleShowOperationValues());
		addCheckbox("Slots", e -> actions.toggleShowSlots());
		getItems().add(new Separator());
		addButton("Update Diagram", e -> actions.updateDiagram());
	}

	private void addButton(String string, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		getItems().add(button);
	}

	private void addCheckbox(String string, EventHandler<ActionEvent> eventHandler) {
		Label label = new Label(string);
		CheckBox box = new CheckBox();
		box.setOnAction(eventHandler);
		box.setSelected(true);
		getItems().addAll(label, box);
	}
}
