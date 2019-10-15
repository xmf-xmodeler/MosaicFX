package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class ChangeTypeDialogResult extends DialogResult{
	
	private final PropertyType type;
	private FmmlxObject object;
	private FmmlxAssociation association;
	private FmmlxAttribute attribute;
	private FmmlxOperation operation;
	private String oldType;
	private String newType;
	

	public ChangeTypeDialogResult(PropertyType type, FmmlxObject object, FmmlxOperation selectedItem, String currentType, String newType) {
		this.type =type;
		this.object =object;
		this.operation=selectedItem;
		this.oldType=currentType;
		this.newType=newType;
	}

	public ChangeTypeDialogResult(PropertyType type, FmmlxObject object, FmmlxAttribute selectedItem, String currentType, String newType) {
		this.type =type;
		this.object =object;
		this.attribute=selectedItem;
		this.oldType=currentType;
		this.newType=newType;
	}
	
	public ChangeTypeDialogResult(PropertyType type, FmmlxObject object, FmmlxAssociation selectedItem, String currentType, String newType) {
		this.type =type;
		this.object =object;
		this.association=selectedItem;
		this.oldType=currentType;
		this.newType=newType;
	}
	
	public FmmlxObject getObject() {
		return object;
	}


	public String getOldType() {
		return oldType;
	}


	public String getNewType() {
		return newType;
	}

	public FmmlxAssociation getAssociation() {
		return association;
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public FmmlxOperation getOperation() {
		return operation;
	}

	public PropertyType getType() {
		return type;
	}
	
	
	

}
