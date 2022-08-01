package tool.clients.fmmlxdiagrams;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

public class Palette extends ToolBar {

	CheckBox boxO;
	CheckBox boxOV;
	CheckBox boxS;
	CheckBox boxGettersAndSetters;
	CheckBox boxDerivedOperations;
	CheckBox boxDerivedAttributes;
	CheckBox metaClassName;
	CheckBox boxConstraints;
	CheckBox boxConstraintReports;
	
	public Palette(FmmlxDiagram diagram) {
		setPadding(new Insets(5, 5, 5, 5));
		setOrientation(Orientation.HORIZONTAL);
		isResizable();
		
		getItems().add(new Label("Show: "));
		
		boxO = addCheckBox("Operations");
		boxOV = addCheckBox("Operation Values");
		boxS = addCheckBox("Slots");
		boxGettersAndSetters = addCheckBox("Getters & Setters");
		boxDerivedOperations = addCheckBox("Derived Operations");
		boxDerivedAttributes = addCheckBox("Derived Attributes");
		boxConstraints = addCheckBox("Constraints");
		boxConstraintReports = addCheckBox("Constraint Reports");
		metaClassName = addCheckBox("Metaclass name");
		
		
		metaClassName.setSelected(false);
		getItems().add(new Separator());
		boxO.setOnAction(e -> {diagram.setShowOperations(boxO); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxOV.setOnAction(e -> {diagram.setShowOperationValues(boxOV); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxS.setOnAction(e -> {diagram.setShowSlots(boxS); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxGettersAndSetters.setOnAction(e-> {diagram.setShowGettersAndSetters(boxGettersAndSetters); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxDerivedOperations.setOnAction(e-> {diagram.setShowDerivedOperations(boxDerivedOperations); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxDerivedAttributes.setOnAction(e-> {diagram.setShowDerivedAttributes(boxDerivedAttributes); diagram.comm.sendViewOptions(diagram.diagramID);});
		metaClassName.setOnAction(e-> {diagram.setMetaClassNameInPalette(metaClassName); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxConstraints.setOnAction(e->{diagram.setShowConstraints(boxConstraints); diagram.comm.sendViewOptions(diagram.diagramID);});
		boxConstraintReports.setOnAction(e->{diagram.setShowConstraintReports(boxConstraintReports); diagram.comm.sendViewOptions(diagram.diagramID);});
		}
	
	public void updateToolbar(FmmlxDiagram diagram) {
		metaClassName.setSelected(diagram.isMetaClassNameInPalette()); 
		boxO.setSelected(diagram.isShowOperations());
		boxOV.setSelected(diagram.isShowOperationValues());
		boxS.setSelected(diagram.isShowSlots());
		boxGettersAndSetters.setSelected(diagram.isShowGetterAndSetter());
		boxDerivedOperations.setSelected(diagram.isShowDerivedOperations());
		boxDerivedAttributes.setSelected(diagram.isShowDerivedAttributes());
		boxConstraints.setSelected(diagram.isConstraintsInDiagram());
		boxConstraintReports.setSelected(diagram.isConstraintReportsInDiagram());
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
//		addButton("Table for Issues On/Off", e-> diagram.switchTableOnAndOffForIssues());
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
