package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.helper.persistence.SerializerConstant;

public class AddDelegationParser extends ModelActionParser {

	public AddDelegationParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String delegationFromPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_FROM);
		String[] delegationFromPathArray = delegationFromPath.split("::");
		String delegationFromName = delegationFromPathArray[delegationFromPathArray.length - 1];

		String delegationToPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_DELEGATE_TO);
		String[] delegationToPathArray = delegationToPath.split("::");
		String delegationToName = delegationToPathArray[delegationToPathArray.length - 1];
		int delegateToLevel = Integer.parseInt(modelElement.getAttribute("delegateToLevel"));

		communicator.addDelegationAsync(diagramId, delegationFromName, delegationToName, delegateToLevel);
	}
}