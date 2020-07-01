package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeTargetDialogResult {
	
	private final PropertyType type;
	private FmmlxAssociation association;
	private FmmlxObject object;
	private Integer oldTargetID;
	private Integer newTargetID;
	
	
	public ChangeTargetDialogResult(PropertyType type, FmmlxObject object, FmmlxAssociation association, Integer oldTargetID, Integer newTargetID) {
		this.type=type;
		this.object=object;
		this.oldTargetID=oldTargetID;
		this.newTargetID= newTargetID;
	}

	public PropertyType getType() {
		return type;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public Integer getOldTargetID() {
		return oldTargetID;
	}

	public Integer getNewTargetID() {
		return newTargetID;
	}

	public String getAssociationName() {
		return association.getName();
	}
	
	
	

}
