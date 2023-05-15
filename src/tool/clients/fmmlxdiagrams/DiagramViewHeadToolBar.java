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

public class DiagramViewHeadToolBar extends VBox {
	
	private DiagramDisplayModel model;
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
	
	private Map<DiagramDisplayProperties, CheckBox> checkBoxMap = new LinkedHashMap<>();
			
	public DiagramViewHeadToolBar(FmmlxDiagram diagram) {
		
		fmmlxDiagram = diagram;
		model = new DiagramDisplayModel(this);
		ToolBar line1 = new ToolBar();
		line1.setPadding(new Insets(5, 5, 5, 5));
		line1.setOrientation(Orientation.HORIZONTAL);
		line1.isResizable();		
		line1.getItems().add(new Label("Show: "));
	
		checkBoxMap.put(DiagramDisplayProperties.SHOWOPERATIONS, boxOperations);
		checkBoxMap.put(DiagramDisplayProperties.SHOWOPERATIONVALUES, boxOperationValues);
		checkBoxMap.put(DiagramDisplayProperties.SHOWSLOTS, boxSlots);
		checkBoxMap.put(DiagramDisplayProperties.SHOWGETTERSANDSETTERS, boxGettersAndSetters);
		checkBoxMap.put(DiagramDisplayProperties.SHOWDERIVEDOPERATIONS, boxDerivedOperations);
		checkBoxMap.put(DiagramDisplayProperties.SHOWDERIVEDATTRIBUTES, boxDerivedAttributes);
		checkBoxMap.put(DiagramDisplayProperties.SHOWCONSTRAINTS, boxConstraints);
		checkBoxMap.put(DiagramDisplayProperties.SHOWCONSTRAINTREPORTS, boxConstraintReports);
		checkBoxMap.put(DiagramDisplayProperties.SHOWMETACLASSNAME, boxMetaClassName);
		checkBoxMap.put(DiagramDisplayProperties.SHOWCONCRETESYNTAX, boxConcreteSyntax);
		checkBoxMap.put(DiagramDisplayProperties.SHOWISSUETABLEVISIBLE, boxIssueTable);
		
		for (Map.Entry<DiagramDisplayProperties, CheckBox> entry : checkBoxMap.entrySet()) {
				setCheckBoxSelected(entry.getKey());
				line1.getItems().add(checkBoxMap.get(entry.getKey()));				
				entry.getValue().setOnAction(e->{changeModell();
				diagram.triggerOverallReLayout();
				diagram.redraw();
				}
				);
			}
		boxIssueTable.setOnAction(e-> {changeModell();diagram.switchTableOnAndOffForIssues();				
		;});	
		ToolBar line2 = new ToolBar();
		line2.autosize();
		line2.setPadding(new Insets(5,5,5,5));
		line2.getItems().add(new Label("Zoom"));
		addButton("+", e -> diagram.getActiveTab().zoomIn(),line2);
		addButton("100%", e -> diagram.getActiveTab().zoomOne(),line2);
		addButton("-", e -> diagram.getActiveTab().zoomOut(),line2);
		line2.getItems().add(new Separator());
		addButton("Update Diagram", e -> diagram.updateDiagram(),line2);
		addButton("Print Protocol", e -> diagram.actions.printProtocol(),line2);
		
		this.getChildren().addAll(line1, line2);
	}
	
	public FmmlxDiagram getFmmlxDiagram() {
		return fmmlxDiagram;
	}
			
	private void addButton(String string, EventHandler<ActionEvent> eventHandler, ToolBar toolbar) {
		Button button = new Button(string);
		button.setOnAction(eventHandler);
		toolbar.getItems().add(button);
	}

	private void setCheckBoxSelected(DiagramDisplayProperties propertie) {
		checkBoxMap.get(propertie).setSelected(model.getPropertieValue(propertie));
	}
	
	 private void changeModell() {
	        model.updateCheckBoxValues(getDiagramDisplayProperties());
	        }
	 
	 private List<Boolean> getDiagramDisplayProperties(){		 
		 List<Boolean> diagramToolBarPropertiesList = new ArrayList<>();
		 for (CheckBox box  : checkBoxMap.values()) {
			diagramToolBarPropertiesList.add(box.isSelected());
		}
		 return diagramToolBarPropertiesList;
	 }

	 public DiagramDisplayModel getModell() {
		 return model;
	 }

	public void updateCheckBoxValues(List<Boolean> list) {
		for (Map.Entry<DiagramDisplayProperties, CheckBox> entry : checkBoxMap.entrySet()) {
		entry.getValue().setSelected(list.remove(0));		
		}	
	}
}