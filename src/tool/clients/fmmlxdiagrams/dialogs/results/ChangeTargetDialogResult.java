package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeTargetDialogResult {
	
	private final PropertyType type;
	private FmmlxAssociation association;
	private FmmlxObject object;
	private String oldTargetName;
	private String newTargetName;
	
	
	public ChangeTargetDialogResult(PropertyType type, FmmlxObject object, FmmlxAssociation association, String oldTargetName, String newTargetName) {
		this.type=type;
		this.object=object;
		this.oldTargetName =oldTargetName;
		this.newTargetName = newTargetName;
	}

	public PropertyType getType() {
		return type;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public String getOldTargetName() {
		return oldTargetName;
	}

	public String getNewTargetName() {
		return newTargetName;
	}

	public String getAssociationName() {
		return association.getName();
	}
	
	
	

}
