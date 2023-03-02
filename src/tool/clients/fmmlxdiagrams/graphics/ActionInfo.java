package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.graphics.NodeElement.Action;

public class ActionInfo {
	public final String id;
	public final String localId;
	public final String actionType;
	
	public ActionInfo(String id, String localId, String actionType) {
		this.id = id;
		this.localId = localId;
		this.actionType = actionType;
	}

	public Action getAction(FmmlxObject object, FmmlxDiagram diagram) {
		String[] actionInfo = actionType.split(":");
		if("CHANGE_SLOT_VALUE".equals(actionInfo[0])) {
			final FmmlxSlot slot = object.getSlot(actionInfo[1]);
			if(slot != null) return ()-> {
				diagram.getActions().changeSlotValue(object, slot);
			};
		}
		return null;//()->{};
	}
	
	@Override 
	public String toString() {
		return localId + ": " + actionType + ".";
	}
	
}
