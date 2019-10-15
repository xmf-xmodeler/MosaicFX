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
		CheckBox boxO = addCheckBox("Operations");
		CheckBox boxOV = addCheckBox("Operation Values");
		CheckBox boxS = addCheckBox("Slots");
		
		boxO.setOnAction(e -> actions.setShowOperations(boxO));
		boxOV.setOnAction(e -> actions.setShowOperationValues(boxOV));
		boxS.setOnAction(e -> actions.setShowSlots(boxS));
		
		getItems().add(new Separator());
		addButton("Update Diagram", e -> actions.updateDiagram());
		addButton("Print Protocol", e -> actions.printProtocol());
	}

	private void addButton(String string, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		getItems().add(button);
	}

	private CheckBox addCheckBox(String string) {
		CheckBox box = new CheckBox(string);
		box.setSelected(true);
		getItems().add(box);
		return box;
	}
}
