package tool.clients.fmmlxdiagrams.graphdb;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public class SlotNode extends Node {
	private FmmlxObject owner;

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
	
}
