package tool.clients.fmmlxdiagrams;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.menus.DiagramViewHeadToolBar;
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
	
	Map<DiagramsDisplayProperty, Boolean> showPropertiesMap = new LinkedHashMap<>();
	
	public DiagramDisplayModel(DiagramViewHeadToolBar diagramViewToolBar) {
		this.diagramViewHeadToolBar = diagramViewToolBar;
		fmmlxDiagram = diagramViewToolBar.getFmmlxDiagram();
				
		showPropertiesMap.put(DiagramsDisplayProperty.OPERATIONS, showOperations);
		showPropertiesMap.put(DiagramsDisplayProperty.OPERATIONVALUES, showOperationValues);
		showPropertiesMap.put(DiagramsDisplayProperty.SLOTS, showSlots);
		showPropertiesMap.put(DiagramsDisplayProperty.GETTERSANDSETTERS, showGettersAndSetters);
		showPropertiesMap.put(DiagramsDisplayProperty.DERIVEDOPERATIONS, showDerivedOperations);
		showPropertiesMap.put(DiagramsDisplayProperty.DERIVEDATTRIBUTES, showDerivedAttributes);
		showPropertiesMap.put(DiagramsDisplayProperty.CONSTRAINTS, showConstraints);
		showPropertiesMap.put(DiagramsDisplayProperty.CONSTRAINTREPORTS, showConstraintReports);
		showPropertiesMap.put(DiagramsDisplayProperty.METACLASSNAME, showMetaClassName);
		showPropertiesMap.put(DiagramsDisplayProperty.CONCRETESYNTAX, showConcreteSyntax);
		showPropertiesMap.put(DiagramsDisplayProperty.ISSUETABLE, issueTableVisible);
		sendDisplayPropertiesToXMF();
	}

	public boolean getPropertieValue (DiagramsDisplayProperty propertie) {
		return showPropertiesMap.get(propertie);
	}
	
	public void setPropertyValue (DiagramsDisplayProperty propertie, boolean bool) {
		showPropertiesMap.replace(propertie, bool); 
		sendDisplayPropertiesToXMF ();
	}
	
	public boolean toggleDisplayProperty(DiagramsDisplayProperty property){
		setPropertyValue(property, !getPropertieValue(property));
		return getPropertieValue(property);
	}
	
	public Map<DiagramsDisplayProperty, Boolean> getDisplayPropertiesMap(){
		return showPropertiesMap;
	}
	
	public void sendDisplayPropertiesToXMF () {
		Vector<Value> items = new Vector<>();
		for (DiagramsDisplayProperty propertie : DiagramsDisplayProperty.values()) {
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
	
	public void receiveDisplayPropertiesFromXMF () {
		FmmlxDiagramCommunicator communicator = fmmlxDiagram.getComm();
		ReturnCall<HashMap<String, Boolean>> onViewOptionsReturn = propertyImport -> {
			if (propertyImport.isEmpty()) {
				return;
			} else {
				for (Entry<String, Boolean> entry : propertyImport.entrySet()) {
					try {
						setPropertyValue(DiagramsDisplayProperty.valueOf(entry.getKey().toUpperCase()),entry.getValue());					
					} catch (Exception e) {
						System.err.println("No Enum Value");
					}			
				}
			}
		};
		
		communicator.getDiagramDisplayProperties(fmmlxDiagram.getID(), onViewOptionsReturn);
	}
		
}