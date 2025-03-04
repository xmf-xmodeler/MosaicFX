package tool.clients.fmmlxdiagrams.dialogs;

// this class is used for the display of the different data types in the addAttributeDialog
// the difference is between the displayName and the actualName that needs to be transmitted to XMF

public class AddAttributeDialogDataType {

	// name that goes to XMF
	private String name;

	// name that is displayed
	private String displayName;

	public String getDisplayName() {
		return this.displayName;
	}

	public String getName() {
		return this.name;
	}

	public AddAttributeDialogDataType(String name, AddAttributeDialogMetaDataType displayName) {
		this.name = name;
		if (displayName == AddAttributeDialogMetaDataType.Primitive
				|| displayName == AddAttributeDialogMetaDataType.NonPrimitive) {
			this.displayName = name;
		} else {
			if (displayName == AddAttributeDialogMetaDataType.Domainspecific) {
				this.displayName = this.name + " [Domain-Specific]";
			} else {
				this.displayName = this.name + " [" + displayName.toString() + "]";
			}
		}
	}

	public enum AddAttributeDialogMetaDataType {
		Domainspecific, Primitive, Enum, NonPrimitive
	}

}
