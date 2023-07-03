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
	
	Map<DiagramDisplayProperty, Boolean> showPropertiesMap = new LinkedHashMap<>();
	
	public DiagramDisplayModel(DiagramViewHeadToolBar diagramViewToolBar) {
		this.diagramViewHeadToolBar = diagramViewToolBar;
		fmmlxDiagram = diagramViewToolBar.getFmmlxDiagram();
				
		showPropertiesMap.put(DiagramDisplayProperty.OPERATIONS, showOperations);
		showPropertiesMap.put(DiagramDisplayProperty.OPERATIONVALUES, showOperationValues);
		showPropertiesMap.put(DiagramDisplayProperty.SLOTS, showSlots);
		showPropertiesMap.put(DiagramDisplayProperty.GETTERSANDSETTERS, showGettersAndSetters);
		showPropertiesMap.put(DiagramDisplayProperty.DERIVEDOPERATIONS, showDerivedOperations);
		showPropertiesMap.put(DiagramDisplayProperty.DERIVEDATTRIBUTES, showDerivedAttributes);
		showPropertiesMap.put(DiagramDisplayProperty.CONSTRAINTS, showConstraints);
		showPropertiesMap.put(DiagramDisplayProperty.CONSTRAINTREPORTS, showConstraintReports);
		showPropertiesMap.put(DiagramDisplayProperty.METACLASSNAME, showMetaClassName);
		showPropertiesMap.put(DiagramDisplayProperty.CONCRETESYNTAX, showConcreteSyntax);
		showPropertiesMap.put(DiagramDisplayProperty.ISSUETABLE, issueTableVisible);
		sendDisplayPropertiesToXMF();
	}

	public boolean getPropertieValue (DiagramDisplayProperty propertie) {
		return showPropertiesMap.get(propertie);
	}
	
	public void setPropertyValue (DiagramDisplayProperty propertie, boolean bool) {
		showPropertiesMap.replace(propertie, bool); 
		sendDisplayPropertiesToXMF ();
	}
	
	public boolean toggleDisplayProperty(DiagramDisplayProperty property){
		setPropertyValue(property, !getPropertieValue(property));
		return getPropertieValue(property);
	}
	
	public Map<DiagramDisplayProperty, Boolean> getDisplayPropertiesMap(){
		return showPropertiesMap;
	}
	
	public void sendDisplayPropertiesToXMF () {
		Vector<Value> items = new Vector<>();
		for (DiagramDisplayProperty propertie : DiagramDisplayProperty.values()) {
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
						setPropertyValue(DiagramDisplayProperty.valueOf(entry.getKey().toUpperCase()),entry.getValue());					
					} catch (Exception e) {
						System.err.println("No Enum Value");
					}			
				}
			}
		};
		
		communicator.getDiagramDisplayProperties(fmmlxDiagram.getID(), onViewOptionsReturn);
	}
		
}