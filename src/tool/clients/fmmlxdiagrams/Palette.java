package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Palette extends GridPane{
	
	private final FmmlxDiagram diagram;

	public Palette(FmmlxDiagram diagram) {
		this.diagram = diagram;
		setMinSize(200, 600);
		setPrefSize(200, 600);
		setPadding(new Insets(10, 10, 10, 10)); 
	    setVgap(5); 
	    setHgap(5);    

	    addButton("Add MetaClass", 0, e -> test());
	    addButton("Add Instance", 1, e -> test2());
	    addButton("Remove MetaClass/Instance", 2, e -> System.out.println("Button 2"));
	    addButton("Add Attribute", 3, e -> System.out.println("Button 3"));
	    addButton("Edit Attribute", 4, e -> test());
	    addButton("Remove Attribute", 5, e -> System.out.println("Button 5"));
	    addButton("Change Slot Value", 6, e -> System.out.println("Button 6"));
	    
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

	private FmmlxDiagramCommunicator getComm() {
		return diagram.comm;
	}
	
	private void test() {
		diagram.addMetaClass("TestClass", 5, new Vector<Integer>(), false, 1,1);
		diagram.updateDiagram();
	}

	private void test2() {
		diagram.addInstance(diagram.getTestClassId(), "TestInstance", new Vector<Integer>(), false, 1,1);
		diagram.updateDiagram();
	}
}
