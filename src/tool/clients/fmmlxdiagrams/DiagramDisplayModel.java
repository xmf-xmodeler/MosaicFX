package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import xos.Value;

public class DiagramDisplayModel {
	
	DiagramViewHeadToolBar diagramViewHeadToolBar;
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
	
	Map<DiagramDisplayProperties, Boolean> showPropertiesMap = new LinkedHashMap<>();
	
	public DiagramDisplayModel(DiagramViewHeadToolBar diagramViewToolBar) {
		
		this.diagramViewHeadToolBar = diagramViewToolBar;
		fmmlxDiagram = diagramViewToolBar.getFmmlxDiagram();
				
		showPropertiesMap.put(DiagramDisplayProperties.SHOWOPERATIONS, showOperations);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWOPERATIONVALUES, showOperationValues);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWSLOTS, showSlots);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWGETTERSANDSETTERS, showGettersAndSetters);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWDERIVEDOPERATIONS, showDerivedOperations);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWDERIVEDATTRIBUTES, showDerivedAttributes);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWCONSTRAINTS, showConstraints);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWCONSTRAINTREPORTS, showConstraintReports);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWMETACLASSNAME, showMetaClassName);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWCONCRETESYNTAX, showConcreteSyntax);
		showPropertiesMap.put(DiagramDisplayProperties.SHOWISSUETABLEVISIBLE, issueTableVisible);
	}

	public boolean getPropertieValue (DiagramDisplayProperties propertie) {
		return showPropertiesMap.get(propertie);
	}
	
	public void setPropertieValue (DiagramDisplayProperties propertie, boolean bool) {
		showPropertiesMap.replace(propertie, bool); 
		diagramViewHeadToolBar.updateCheckBoxValues(getDisplayProperties());
	}
	
	
	public void updateCheckBoxValues(List<Boolean> list) {
		for (Map.Entry<DiagramDisplayProperties, Boolean> entry : showPropertiesMap.entrySet()) {
			entry.setValue(list.remove(0));
			sendDisplayPropertiesToXMF ();		
		}	
	}
	
	public Map<DiagramDisplayProperties, Boolean> getDisplayPropertiesMap(){
		return showPropertiesMap;
	}
	
	public void sendDisplayPropertiesToXMF () {
		Vector<Value> items = new Vector<>();
		for (DiagramDisplayProperties propertie : DiagramDisplayProperties.values()) {
			items.add(new Value(new Value[] {new Value(propertie.name()), 	new Value(getPropertieValue(propertie) )}));
		}
		Value[] itemArray = new Value[items.size()];
		for(int i = 0; i < itemArray.length; i++) {
			itemArray[i] = items.get(i);
		}
		Value[] message = new Value[]{
				FmmlxDiagramCommunicator.getNoReturnExpectedMessageID(diagramViewHeadToolBar.getFmmlxDiagram().getID()),
				new Value(itemArray)
		};
		diagramViewHeadToolBar.getFmmlxDiagram().getComm().sendMessage("sendViewOptions", message);
	}
	
	public void recieveDisplayPropertiesFromXMF () {
		FmmlxDiagramCommunicator communicator = fmmlxDiagram.getComm();
		HashMap<String, Boolean> propertyImport = communicator.getDiagramDisplayProperties(fmmlxDiagram.getID());
		if (propertyImport.isEmpty()) {
			return;
		} else {
			for (Entry<String, Boolean> entry : propertyImport.entrySet()) {
				try {
					setPropertieValue(DiagramDisplayProperties.valueOf(entry.getKey().toUpperCase()),entry.getValue());					
				} catch (Exception e) {
					System.err.println("No Enum Value");
					 continue;
				}			
			}
		}
	}
	
	private List<Boolean> getDisplayProperties(){		 
		 List<Boolean> diagramToolBarPropertiesList = new ArrayList<>();
		 for (Boolean bool  : showPropertiesMap.values()) {
			diagramToolBarPropertiesList.add(bool);
		}
		 return diagramToolBarPropertiesList;
	}
}