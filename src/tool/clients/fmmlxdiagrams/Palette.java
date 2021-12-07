package tool.clients.fmmlxdiagrams;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

public class Palette extends ToolBar {

	public Palette(FmmlxDiagram diagram) {
		setPadding(new Insets(5, 5, 5, 5));
		setOrientation(Orientation.HORIZONTAL);
		isResizable();
		
		getItems().add(new Label("Show: "));
		CheckBox boxO = addCheckBox("Operations");
		CheckBox boxOV = addCheckBox("Operation Values");
		CheckBox boxS = addCheckBox("Slots");
		CheckBox boxGettersAndSetters = addCheckBox("Getters & Setters");
		CheckBox boxDerivedOperations = addCheckBox("Derived Operations");
		CheckBox boxDerivedAttributes = addCheckBox("Derived Attributes");
		CheckBox metaClassName = addCheckBox("Name of Metaclass in Palette");
		metaClassName.setSelected(false);
		getItems().add(new Separator());
		boxO.setOnAction(e -> {diagram.setShowOperations(boxO); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxOV.setOnAction(e -> {diagram.setShowOperationValues(boxOV); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxS.setOnAction(e -> {diagram.setShowSlots(boxS); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxGettersAndSetters.setOnAction(e-> {diagram.setShowGettersAndSetters(boxGettersAndSetters); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxDerivedOperations.setOnAction(e-> {diagram.setShowDerivedOperations(boxDerivedOperations); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxDerivedAttributes.setOnAction(e-> {diagram.setShowDerivedAttributes(boxDerivedAttributes); diagram.comm.sendViewOptions(diagram.diagramID);});
		metaClassName.setOnAction(e-> diagram.setMetaClassNameInPalette(metaClassName));
		
		
		
		}
	
	public Palette(FmmlxDiagram diagram, int secondRow) {
		autosize();
		setPadding(new Insets(5,5,5,5));
		getItems().add(new Label("Zoom"));
		addButton("+", e -> diagram.getActiveTab().zoomIn());
		addButton("100%", e -> diagram.getActiveTab().zoomOne());
		addButton("-", e -> diagram.getActiveTab().zoomOut());
		getItems().add(new Separator());
		addButton("Update Diagram", e -> diagram.updateDiagram());
		addButton("Print Protocol", e -> diagram.actions.printProtocol());
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
