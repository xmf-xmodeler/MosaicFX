package tool.clients.fmmlxdiagrams;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import tool.clients.fmmlxdiagrams.menus.DiagramViewHeadToolBar;
import xos.Value;

public class DiagramDisplayModel {
	
	private final int relatedDiagramID;
	private Map<DiagramDisplayProperty, Boolean> showPropertiesMap = new HashMap<>();

	public DiagramDisplayModel(DiagramViewHeadToolBar diagramViewToolBar) {
		relatedDiagramID = diagramViewToolBar.getFmmlxDiagram().getID(); 
		showPropertiesMap = FmmlxDiagramCommunicator.getCommunicator().getDiagramDisplayPropertiesSynchronous(relatedDiagramID);
		//if not all properties contained in the map add the missing with default values
		for (DiagramDisplayProperty property : DiagramDisplayProperty.values()) {
			if (!showPropertiesMap.containsKey(property)) {
				showPropertiesMap.put(property, property.getDefaultValue());
			}
		}
		//if not all values were contained in the map received from XMF, the XMF data now gets updated
		sendDisplayPropertiesToXMF();
	}

	public boolean getPropertieValue(DiagramDisplayProperty propertie) {
		return showPropertiesMap.get(propertie);
	}

	public void setPropertyValue(DiagramDisplayProperty propertie, boolean bool) {
		showPropertiesMap.replace(propertie, bool);
		sendDisplayPropertiesToXMF();
	}

	public boolean toggleDisplayProperty(DiagramDisplayProperty property) {
		setPropertyValue(property, !getPropertieValue(property));
		return getPropertieValue(property);
	}

	public Map<DiagramDisplayProperty, Boolean> getDisplayPropertiesMap() {
		return showPropertiesMap;
	}

	public void sendDisplayPropertiesToXMF() {
		Vector<Value> items = new Vector<>();
		for (DiagramDisplayProperty propertie : DiagramDisplayProperty.values()) {
			items.add(new Value(new Value[] { new Value(propertie.name()), new Value(getPropertieValue(propertie)) }));
		}
		Value[] itemArray = new Value[items.size()];
		for (int i = 0; i < itemArray.length; i++) {
			itemArray[i] = items.get(i);
		}
		Value[] message = new Value[] {
				FmmlxDiagramCommunicator.getNoReturnExpectedMessageID(relatedDiagramID),
				new Value(itemArray) };
		FmmlxDiagramCommunicator.getCommunicator().sendMessage("sendViewOptions", message);
	}
}