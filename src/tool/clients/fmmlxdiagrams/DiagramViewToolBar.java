package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DiagramViewToolBar extends VBox {
	
	private DiagramViewToolBarModell modell;
	private FmmlxDiagram fmmlxDiagram; 
	
	private CheckBox boxOperations = new CheckBox("Operations");
	private CheckBox boxOperationValues = new CheckBox("Operation Values");
	private CheckBox boxSlots= new CheckBox("Slots");
	private CheckBox boxGettersAndSetters= new CheckBox("Getters & Setters");
	private CheckBox boxDerivedOperations= new CheckBox("Derived Operations");
	private CheckBox boxDerivedAttributes= new CheckBox("Derived Attributes");
	private CheckBox boxConstraints= new CheckBox("Constraints");
	private CheckBox boxConstraintReports= new CheckBox("Constraint Reports");
	private CheckBox boxMetaClassName= new CheckBox("Metaclass name");
	private CheckBox boxConcreteSyntax= new CheckBox("Concrete Syntax");
	private CheckBox boxIssueTable= new CheckBox("Issue Table");
	
	private Map<DiagramToolBarProperties, CheckBox> checkBoxMap = new LinkedHashMap<>();
			
	public DiagramViewToolBar(FmmlxDiagram diagram) {
		
		fmmlxDiagram = diagram;
		modell = new DiagramViewToolBarModell(this);
		ToolBar line1 = new ToolBar();
		line1.setPadding(new Insets(5, 5, 5, 5));
		line1.setOrientation(Orientation.HORIZONTAL);
		line1.isResizable();		
		line1.getItems().add(new Label("Show: "));
	
		checkBoxMap.put(DiagramToolBarProperties.OPERATIONS, boxOperations);
		checkBoxMap.put(DiagramToolBarProperties.OPERATIONVALUES, boxOperationValues);
		checkBoxMap.put(DiagramToolBarProperties.SLOTS, boxSlots);
		checkBoxMap.put(DiagramToolBarProperties.GETTERSANDSETTERS, boxGettersAndSetters);
		checkBoxMap.put(DiagramToolBarProperties.DERIVEDOPERATIONS, boxDerivedOperations);
		checkBoxMap.put(DiagramToolBarProperties.DERIVEDATTRIBUTES, boxDerivedAttributes);
		checkBoxMap.put(DiagramToolBarProperties.CONSTRAINTS, boxConstraints);
		checkBoxMap.put(DiagramToolBarProperties.CONSTRAINTREPORTS, boxConstraintReports);
		checkBoxMap.put(DiagramToolBarProperties.METACLASSNAME, boxMetaClassName);
		checkBoxMap.put(DiagramToolBarProperties.CONCRETESYNTAX, boxConcreteSyntax);
		checkBoxMap.put(DiagramToolBarProperties.ISSUETABLEVISIBLE, boxIssueTable);
		
		for (Map.Entry<DiagramToolBarProperties, CheckBox> entry : checkBoxMap.entrySet()) {
				setCheckBoxSelected(entry.getKey());
				line1.getItems().add(checkBoxMap.get(entry.getKey()));				
				entry.getValue().setOnAction(e->{changeModell();
				diagram.updateDiagram();
				}
				);
			}
		
		
		boxIssueTable.setOnAction(e-> {changeModell();diagram.switchTableOnAndOffForIssues();				
		;});
		
			//Add FMLX- Communication
		
			
		ToolBar line2 = new ToolBar();
		line2.autosize();
		line2.setPadding(new Insets(5,5,5,5));
		line2.getItems().add(new Label("Zoom"));
		addButton("+", e -> diagram.getActiveTab().zoomIn(),line2);
		addButton("100%", e -> diagram.getActiveTab().zoomOne(),line2);
		addButton("-", e -> diagram.getActiveTab().zoomOut(),line2);
		line2.getItems().add(new Separator());
		addButton("Update Diagram", e -> diagram.updateDiagram(),line2);
		addButton("Print Protocol", e -> {diagram.actions.printProtocol()
		;	
		diagram.getComm().getViewOptions(diagram.getID());
		},line2);
		
		
		this.getChildren().addAll(line1, line2);
			
		
	}
	
	public FmmlxDiagram getFmmlxDiagram() {
		return fmmlxDiagram;
	}
	

//	public void updateToolbar(FmmlxDiagram diagram) {
//		boxMetaClassName.setSelected(diagram.isMetaClassNameInPalette()); 
//		boxOperations.setSelected(diagram.isShowOperations());
//		boxOperationValues.setSelected(diagram.isShowOperationValues());
//		boxSlots.setSelected(diagram.isShowSlots());
//		boxGettersAndSetters.setSelected(diagram.isShowGetterAndSetter());
//		boxDerivedOperations.setSelected(diagram.isShowDerivedOperations());
//		boxDerivedAttributes.setSelected(diagram.isShowDerivedAttributes());
//		boxConstraints.setSelected(diagram.isConstraintsInDiagram());
//		boxConstraintReports.setSelected(diagram.isConstraintReportsInDiagram());
//		
//	}
		
	private void addButton(String string, EventHandler<ActionEvent> eventHandler, ToolBar toolbar) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		toolbar.getItems().add(button);
	}

	private void setCheckBoxSelected(DiagramToolBarProperties propertie) {
		checkBoxMap.get(propertie).setSelected(modell.getPropertieValue(propertie));
	}
	
	 private void changeModell() {
	        modell.updateCheckBoxValues(getDiagramToolBarProperties());
	        }
	 
	 private void forceFxmmlDiagramRedraw() {
		 fmmlxDiagram.redraw();
	 }
	 
	 private List<Boolean> getDiagramToolBarProperties(){		 
		 List<Boolean> diagramToolBarPropertiesList = new ArrayList<>();
		 for (CheckBox box  : checkBoxMap.values()) {
			diagramToolBarPropertiesList.add(box.isSelected());
		}
		 return diagramToolBarPropertiesList;
	 }

	 public DiagramViewToolBarModell getModell() {
		 return modell;
	 }

	public void updateCheckBoxValues(List<Boolean> list) {
			for (Map.Entry<DiagramToolBarProperties, CheckBox> entry : checkBoxMap.entrySet()) {
				entry.getValue().setSelected(list.remove(0));		
			}	
	}
}
