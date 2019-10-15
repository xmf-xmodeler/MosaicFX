package tool.clients.fmmlxdiagrams.dialogs.results;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;

public class AddOperationDialogResult extends DialogResult {

	// General fields used for all types
	private final PropertyType type;
	private final FmmlxObject object;

	//Used for operation
	private String operationName;
	private int level;
	private String operationType;
	private String body;

	public AddOperationDialogResult(FmmlxObject object, PropertyType type) {
		this.type = type;
		this.object = object;
	}

	//Constructor for type = operation
	public AddOperationDialogResult(FmmlxObject object, String name, int level, String operationType, String body) {
		this.object = object;
		this.type = PropertyType.Operation;
		this.operationName = name;
		this.level = level;
		this.operationType = operationType;
		this.body = body;
	}

	public PropertyType getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public FmmlxObject getObject() {
		return object;
	}

	public int getObjectId() {
		return object.getId();
	}


	public String getOperationName() {
		return operationName;
	}

	public int getLevel() {
		return level;
	}

	public String getOperationType() {
		return operationType;
	}

	public String getBody() {
		return body;
	}
}
