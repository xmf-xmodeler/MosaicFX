package tool.clients.fmmlxdiagrams;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Palette extends GridPane {

	public Palette(DiagramActions actions) {
		setMinSize(200, 600);
		setPrefSize(200, 600);
		setPadding(new Insets(10, 10, 10, 10));
		setVgap(5);
		setHgap(5);

		addButton("Add MetaClass", 0, e -> actions.addMetaClassDialog());
		addButton("Add Instance", 1, e -> System.out.println("Add Instance"));
		addButton("Remove MetaClass/Instance", 2, e -> System.out.println("Button 2"));
		addButton("Add Attribute", 3, e -> System.out.println("Button 3"));
		addButton("Edit Attribute", 4, e -> System.out.println("Button 4"));
		addButton("Remove Attribute", 5, e -> System.out.println("Button 5"));
		addButton("Change Slot Value", 6, e -> System.out.println("Button 6"));
		addButton("Zoom +", 7, e -> actions.zoomIn());
		addButton("Zoom -", 8, e -> actions.zoomOut());
		addButton("Zoom 100%", 9, e -> actions.zoomOne());

		ColumnConstraints cc = new ColumnConstraints();
		cc.setFillWidth(true);
		cc.setHgrow(Priority.ALWAYS);
		getColumnConstraints().add(cc);

	}

	private void addButton(String string, int y, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		button.setPrefHeight(50);
		button.setOnAction(eventHandler);
		add(button, 0, y);

	}
	

}
