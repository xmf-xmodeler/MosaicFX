package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javafx.scene.control.CheckBox;
import xos.Value;

public class DiagramViewToolBarModell {
	
	DiagramViewToolBar diagramViewToolBar;
	FmmlxDiagram fmmlxDiagram;
	FmmlxDiagramCommunicator fmmlxDiagramCommunicator;
	
	boolean showOperations = true;
	boolean showOperationValues = true;
	boolean showSlots = true;
	boolean showGettersAndSetters = true;
	boolean showDerivedOperations = true;
	boolean showDerivedAttributes = true;
	boolean showConstraints = true;
	boolean showConstraintReports = true;
	boolean showMetaClassName = false;
	boolean showConcreteSyntax = true;
	boolean issueTableVisible = false;
	
	Map<DiagramToolBarProperties, Boolean> showPropertiesMap = new LinkedHashMap<>();
	
	public DiagramViewToolBarModell(DiagramViewToolBar diagramViewToolBar) {
		
		this.diagramViewToolBar = diagramViewToolBar;
		fmmlxDiagram = diagramViewToolBar.getFmmlxDiagram();
		fmmlxDiagramCommunicator = fmmlxDiagram.getComm(); 
		
//		if (!fmmlxDiagramCommunicator.getViewOptions(fmmlxDiagram.getID()).isEmpty()) {
//			boolean showOperations = true;
//			boolean showOperationValues = true;
//			boolean showSlots = true;
//			boolean showGettersAndSetters = true;
//			boolean showDerivedOperations = true;
//			boolean showDerivedAttributes = true;
//			boolean showConstraints = true;
//			boolean showConstraintReports = true;
//			boolean showMetaClassName = false;
//			boolean showConcreteSyntax = true;
//			boolean issueTableVisible = false;
//		}
		
		showPropertiesMap.put(DiagramToolBarProperties.OPERATIONS, showOperations);
		showPropertiesMap.put(DiagramToolBarProperties.OPERATIONVALUES, showOperationValues);
		showPropertiesMap.put(DiagramToolBarProperties.SLOTS, showSlots);
		showPropertiesMap.put(DiagramToolBarProperties.GETTERSANDSETTERS, showGettersAndSetters);
		showPropertiesMap.put(DiagramToolBarProperties.DERIVEDOPERATIONS, showDerivedOperations);
		showPropertiesMap.put(DiagramToolBarProperties.DERIVEDATTRIBUTES, showDerivedAttributes);
		showPropertiesMap.put(DiagramToolBarProperties.CONSTRAINTS, showConstraints);
		showPropertiesMap.put(DiagramToolBarProperties.CONSTRAINTREPORTS, showConstraintReports);
		showPropertiesMap.put(DiagramToolBarProperties.METACLASSNAME, showMetaClassName);
		showPropertiesMap.put(DiagramToolBarProperties.CONCRETESYNTAX, showConcreteSyntax);
		showPropertiesMap.put(DiagramToolBarProperties.ISSUETABLEVISIBLE, issueTableVisible);
	}

	public boolean getPropertieValue (DiagramToolBarProperties propertie) {
		return showPropertiesMap.get(propertie);
	}
	
	public void setPropertieValue (DiagramToolBarProperties propertie, boolean bool) {
		showPropertiesMap.replace(propertie, bool); 
		diagramViewToolBar.updateCheckBoxValues(getDiagramToolBarProperties());
	}
	
	public void updateCheckBoxValues(List<Boolean> list) {
		for (Map.Entry<DiagramToolBarProperties, Boolean> entry : showPropertiesMap.entrySet()) {
			entry.setValue(list.remove(0));
			sendToolBarPropertiesToXMF ();
			
		}	
	}
	
	public Map<DiagramToolBarProperties, Boolean> getShowPropertiesMap(){
		return showPropertiesMap;
	}
	
	public void sendToolBarPropertiesToXMF () {
		Vector<Value> items = new Vector<>();
		items.add(new Value(new Value[] {new Value("DERIVEDATTRIBUTES"), 	new Value(getPropertieValue(DiagramToolBarProperties.DERIVEDATTRIBUTES) )}));
		items.add(new Value(new Value[] {new Value("DERIVEDOPERATIONS"), 	new Value(getPropertieValue(DiagramToolBarProperties.DERIVEDOPERATIONS))}));
		items.add(new Value(new Value[] {new Value("GETTERSANDSETTERS"), 	new Value(getPropertieValue(DiagramToolBarProperties.GETTERSANDSETTERS))}));
		items.add(new Value(new Value[] {new Value("OPERATIONS"), 			new Value(getPropertieValue(DiagramToolBarProperties.OPERATIONS))}));
		items.add(new Value(new Value[] {new Value("OPERATIONVALUES"), 		new Value(getPropertieValue(DiagramToolBarProperties.OPERATIONVALUES))}));
		items.add(new Value(new Value[] {new Value("SLOTS"), 				new Value(getPropertieValue(DiagramToolBarProperties.SLOTS))}));
		items.add(new Value(new Value[] {new Value("METACLASSNAME"),		new Value(getPropertieValue(DiagramToolBarProperties.METACLASSNAME))}));
		items.add(new Value(new Value[] {new Value("CONSTRAINTS"),		    new Value(getPropertieValue(DiagramToolBarProperties.CONSTRAINTS))}));
		items.add(new Value(new Value[] {new Value("CONSTRAINTREPORTS"),    new Value(getPropertieValue(DiagramToolBarProperties.CONSTRAINTREPORTS))}));
		items.add(new Value(new Value[] {new Value("CONCRETESYNTAX"),   	new Value(getPropertieValue(DiagramToolBarProperties.CONCRETESYNTAX))}));
			
		Value[] itemArray = new Value[items.size()];
		for(int i = 0; i < itemArray.length; i++) {
			itemArray[i] = items.get(i);
		}
		Value[] message = new Value[]{
				FmmlxDiagramCommunicator.getNoReturnExpectedMessageID(diagramViewToolBar.getFmmlxDiagram().getID()),
				new Value(itemArray)
		};
		diagramViewToolBar.getFmmlxDiagram().getComm().sendMessage("sendViewOptions", message);
	}
	
	private List<Boolean> getDiagramToolBarProperties(){		 
		 List<Boolean> diagramToolBarPropertiesList = new ArrayList<>();
		 for (Boolean bool  : showPropertiesMap.values()) {
			diagramToolBarPropertiesList.add(bool);
		}
		 return diagramToolBarPropertiesList;
	 }
}
