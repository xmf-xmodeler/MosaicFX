package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import xos.Value;

public class DiagramViewToolBarModell {
	
	DiagramViewToolBar diagramViewToolBar;
	FmmlxDiagram fmmlxDiagram;
		
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
				
		showPropertiesMap.put(DiagramToolBarProperties.SHOWOPERATIONS, showOperations);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWOPERATIONVALUES, showOperationValues);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWSLOTS, showSlots);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWGETTERSANDSETTERS, showGettersAndSetters);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWDERIVEDOPERATIONS, showDerivedOperations);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWDERIVEDATTRIBUTES, showDerivedAttributes);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWCONSTRAINTS, showConstraints);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWCONSTRAINTREPORTS, showConstraintReports);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWMETACLASSNAME, showMetaClassName);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWCONCRETESYNTAX, showConcreteSyntax);
		showPropertiesMap.put(DiagramToolBarProperties.SHOWISSUETABLEVISIBLE, issueTableVisible);
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
		items.add(new Value(new Value[] {new Value("DERIVEDATTRIBUTES"), 	new Value(getPropertieValue(DiagramToolBarProperties.SHOWDERIVEDATTRIBUTES) )}));
		items.add(new Value(new Value[] {new Value("DERIVEDOPERATIONS"), 	new Value(getPropertieValue(DiagramToolBarProperties.SHOWDERIVEDOPERATIONS))}));
		items.add(new Value(new Value[] {new Value("GETTERSANDSETTERS"), 	new Value(getPropertieValue(DiagramToolBarProperties.SHOWGETTERSANDSETTERS))}));
		items.add(new Value(new Value[] {new Value("OPERATIONS"), 			new Value(getPropertieValue(DiagramToolBarProperties.SHOWOPERATIONS))}));
		items.add(new Value(new Value[] {new Value("OPERATIONVALUES"), 		new Value(getPropertieValue(DiagramToolBarProperties.SHOWOPERATIONVALUES))}));
		items.add(new Value(new Value[] {new Value("SLOTS"), 				new Value(getPropertieValue(DiagramToolBarProperties.SHOWSLOTS))}));
		items.add(new Value(new Value[] {new Value("METACLASSNAME"),		new Value(getPropertieValue(DiagramToolBarProperties.SHOWMETACLASSNAME))}));
		items.add(new Value(new Value[] {new Value("CONSTRAINTS"),		    new Value(getPropertieValue(DiagramToolBarProperties.SHOWCONSTRAINTS))}));
		items.add(new Value(new Value[] {new Value("CONSTRAINTREPORTS"),    new Value(getPropertieValue(DiagramToolBarProperties.SHOWCONSTRAINTREPORTS))}));
		items.add(new Value(new Value[] {new Value("CONCRETESYNTAX"),   	new Value(getPropertieValue(DiagramToolBarProperties.SHOWCONCRETESYNTAX))}));
			
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
	
	public void recieveToolBarPropertiesFromXMF () {
		FmmlxDiagramCommunicator communicator = fmmlxDiagram.getComm();
		HashMap<String, Boolean> propertyImport = communicator.getDiagramViewToolBarProperties(fmmlxDiagram.getID());
		if (propertyImport.isEmpty()) {
			return;
		} else {
			for (Entry<String, Boolean> entry : propertyImport.entrySet()) {
				try {
					setPropertieValue(DiagramToolBarProperties.valueOf(entry.getKey().toUpperCase()),entry.getValue());					
				} catch (Exception e) {
					System.err.println("No Enum Value");
					 continue;
				}
				
			}
		}
	}
	
	
	private List<Boolean> getDiagramToolBarProperties(){		 
		 List<Boolean> diagramToolBarPropertiesList = new ArrayList<>();
		 for (Boolean bool  : showPropertiesMap.values()) {
			diagramToolBarPropertiesList.add(bool);
		}
		 return diagramToolBarPropertiesList;
	 }
}
