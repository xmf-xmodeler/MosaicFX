package tool.helper.persistence;

import java.text.ParseException;

import tool.helper.persistence.modelActionParser.AddAssociationParser;
import tool.helper.persistence.modelActionParser.AddAttributeParser;
import tool.helper.persistence.modelActionParser.AddConstraintParser;
import tool.helper.persistence.modelActionParser.AddDelegationParser;
import tool.helper.persistence.modelActionParser.AddEnumerationParser;
import tool.helper.persistence.modelActionParser.AddEnumerationValueParser;
import tool.helper.persistence.modelActionParser.AddInstanceParser;
import tool.helper.persistence.modelActionParser.AddLinkParser;
import tool.helper.persistence.modelActionParser.AddMetaClassParser;
import tool.helper.persistence.modelActionParser.AddOperationParser;
import tool.helper.persistence.modelActionParser.ChangeParentParser;
import tool.helper.persistence.modelActionParser.ChangeSlotValueParser;
import tool.helper.persistence.modelActionParser.ModelActionParser;
import tool.helper.persistence.modelActionParser.SetRoleFillerParser;

public class ModelActionParserFactory {
	int diagramId; 
	
	public ModelActionParserFactory(int diagramId) {
		this.diagramId = diagramId;
	}

	public static ModelActionParser create(Integer diagramId, String modelActionName) throws ParseException {
		switch (modelActionName) {
		case "addMetaClass":
			return new AddMetaClassParser(diagramId);	
		case "addInstance":
			return new AddInstanceParser(diagramId);
		case "changeParent":
			return new ChangeParentParser(diagramId);
		case "addAttribute":
			return new AddAttributeParser(diagramId);
		case "addOperation":
			return new AddOperationParser(diagramId);
		case "changeSlotValue":
			return new ChangeSlotValueParser(diagramId);
		case "addAssociation":
			return new AddAssociationParser(diagramId);
		case "addLink":
			return new AddLinkParser(diagramId);
		case "addDelegation":
			return new AddDelegationParser(diagramId);
		case "setRoleFiller":
			return new SetRoleFillerParser(diagramId);
		case "addEnumeration":
			return new AddEnumerationParser(diagramId);	
		case "addEnumerationValue":
			return new AddEnumerationValueParser(diagramId);
		case "addConstraint":
			return new AddConstraintParser(diagramId);
		default:
			throw new ParseException("XML Element name does not match Model Actions", 0);
		}
		
	}

}
