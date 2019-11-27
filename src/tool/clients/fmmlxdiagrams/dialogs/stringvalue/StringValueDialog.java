package tool.clients.fmmlxdiagrams.dialogs.stringvalue;

public class StringValueDialog {

	public static class ErrorMessage {
		//Select
		public static final String selectNewOwner = "Select New Owner";
		public static final String selectAttribute = "Select Attribute!";
		public static final String selectOperation = "Select Operation!";
		public static final String selectAssociation = "Select Association!";
		public static final String selectLevel = "Select Level!";
		public static final String selectNewLevel = "Select new Level!";
		public static final String selectType = "Select Type!";
		public static final String selectNewType = "Select New Type!";
		public static final String selectAnotherType = "Select Another Type!";
		public static final String selectNewParent = "Select New Parent!";
		public static final String selectAnotherClass = "Select Another Class";

		public static final String enterValidName = "Type Valid Name!";
		public static final String nameAlreadyUsed = "Name Already Used.";

		public static final String pleaseSelectAnotherLevel = "Please select another level!";
		public static final String inputBody = "Input Body!";
		public static final String selectTarget = "Select Target";

		public static final String setDisplayName = "Set display name for source";
		public static final String inputNewMultiplicity = "Input new Multiplicity";
		public static final String selectNewTarget = "Select New Target!";
		public static final String selectNewTypeSource = "Select New Start Type";
		public static final String selectNewTypeTarget = "Select New End Type";
		public static final String selectAllowedLevelSource = "Select Allowed Start Level";
		public static final String selectAllowedLevelTarget = "Select Allowed End Level";
		public static final String enterValidNameIdentifierTarget = "Type Valid Name For End Identifier!";
		public static final String enterValidNameIdentifierSource = "Type Valid Name For Start Identifier!";
		public static final String enterValidNameDisplaySource = "Type Valid Name For Start Display-Name!";
		public static final String enterValidNameDisplayTarget = "Type Valid Name For End Display-Name!";
		public static final String selectMetaClassA = "Select MetaClass-A!";
		public static final String selectMetaClassB = "Select MetaClass-B!";
		public static final String selectInstanceA = "Select Instance of MetaClass-A!";
		public static final String selectInstanceB = "Select Instance of MetaClass-B!";
		public static final String selectAssociationInstance = "Select Association Instance!";
		public static final String inputNumberOfElement= "Input Number of Element";
		public static final String pleaseInputValidNameForEnumElement= "Please Input Valid Name for Enum-Element";
		public static final String selectEnumeration= "Select Enumeration";
		public static final String thereAreDuplicates= "There are Duplicates";
		public static final String elementAlreadyExist = "Element with the same name already exist";
		public static final String enumAlreadyExist= "Enumeration with the same name already exist";
	}

	public static class LabelAndHeaderTitle {

		public static final String selectedObject = "Selected Object";
		public static final String aClass = "Class";
		public static final String name = "Name";
		public static final String level = "Level";
		public static final String owner = "Owner";
		public static final String of = "Of";
		public static final String parent = "Parent";
		public static final String type = "Type";
		public static final String Multiplicity = "Multiplicity";
		public static final String body = "Body";
		public static final String minimum = "Minimum";
		public static final String maximum = "Maximum";
		public static final String upperLimit = "Upper limit";
		public static final String instLevel = "InstLevel";
		public static final String displayName = "Display Name";
		public static final String identifier = "Identifier";
		public static final String multiplicity = "Multiplicity";
		public static final String value = "Value";
		public static final String expression = "Is expression";


		//Current
		public static final String currentOwner = "Current Owner";
		public static final String currentLevel = "Current Level";
		public static final String currentOf = "Current Of";
		public static final String currentParent = "Current Parent";

		//New
		public static final String newOwner = "New Owner";
		public static final String newLevel = "New Level";
		public static final String newOperation = "New Operation";

		//Change
		public static final String change = "Change";
		public static final String changeClassLevel = "Change Class Level";
		public static final String changeOf = "Change Of";
		public static final String changeAttributeOwner = "Change Attribute Owner";
		public static final String changeAttributeLevel = "Change Attribute Level";
		public static final String changeAttributeType = "Change Attribute Type";
		public static final String changeOperationOwner = "Change Operation Owner";
		public static final String changeOperationLevel = "Change Operation Level";
		public static final String changeOperationType = "Change Operation Type";
		public static final String changeAssociationLevel = "Change Association Level";
		public static final String changeAssociationType = "Change Association Type";
		public static final String changeParent = "Change Parent";
		public static final String changeMultiplicity = "Change Multiplicity";
		public static final String changeOperationsBody = "Change Body";
		public static final String changeAssociationTarget = "Change Association Target";
		public static final String changeSlotValue = "Change Slot Value";

		//Select
		public static final String select = "Select ";
		public static final String selectAttribute = "Select Attribute";
		public static final String selectOperation = "Select Operation";
		public static final String selectAssociation = "Select Association";
		public static final String selectNewLevel = "Select New Level";
		public static final String selectNewOf = "Select New Of";
		public static final String selectType = "Select Type";
		public static final String selectLevel = "Select Level";
		public static final String selectNewParent = "Select New Parent";
		public static final String ordered = "Ordered";
		public static final String allowDuplicates = "Allow Duplicates";

		public static final String checkSyntax = "Check Syntax";
		public static final String defaultOperation = "Reset Body";

		public static final String start = "Start";
		public static final String end = "End";
		public static final String editAssociation = "Edit Association";
		public static final String newType = "New Type";
		public static final String newInstLevel = "New instLevel";
		public static final String newDisplayName = "New Display Name";
		public static final String newIdentifier = "New Identifier";
		public static final String associationValue = "Association Value";
		public static final String classALabel = "Class A";
		public static final String classBLabel = "Class B";
		public static final String association = "Association";
		public static final String selectMetaClassA = "Select Meta-Class A";
		public static final String selectMetaClassB = "Select Meta-Class B";
		
	}

	public static class OperationStringValues {
		public static final String emptyOperation =
				"@Operation op0():XCore::Element\n" +
						"  null\n" +
						"end";
	}

	public static class ToolTip {

		public static final String displayNameSource = "The second display name is optional.\n Leave the field empty if necessary.";


	}
}
