package tool.clients.fmmlxdiagrams.graphdb;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class SlotNode extends Node {
	private FmmlxObject owner;
	private String slotName;

	public SlotNode(String name, label label) {
		super(name, label);
		// TODO Auto-generated constructor stub
	}
	public FmmlxObject getOfPath() {
		return this.owner;
	}

	public void setOfPath(FmmlxObject fmmlxObject) {
		this.owner = fmmlxObject;
	}
	public String getSlotName() {
		return slotName;
	}
	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}
	
	
	
}
