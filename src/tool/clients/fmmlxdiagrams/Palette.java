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
		boxO.setOnAction(e -> diagram.setShowOperations(boxO));
		boxOV.setOnAction(e -> diagram.setShowOperationValues(boxOV));
		boxS.setOnAction(e -> diagram.setShowSlots(boxS));
		boxGettersAndSetters.setOnAction(e-> diagram.setShowGettersAndSetters(boxGettersAndSetters));
		boxDerivedOperations.setOnAction(e-> diagram.setShowDerivedOperations(boxDerivedOperations));
		boxDerivedAttributes.setOnAction(e-> diagram.setShowDerivedAttributes(boxDerivedAttributes));
		metaClassName.setOnAction(e-> diagram.setMetaClassNameInPalette(metaClassName));
		
		
		
		}
	
	public Palette(FmmlxDiagram diagram, int secondRow) {
		setPadding(new Insets(5,5,5,5));
		getItems().add(new Label("Zoom"));
		addButton("+", e -> diagram.zoomIn());
		addButton("100%", e -> diagram.zoomOne());
		addButton("-", e -> diagram.zoomOut());
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
