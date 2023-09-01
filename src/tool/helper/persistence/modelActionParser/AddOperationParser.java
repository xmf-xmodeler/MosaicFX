package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.helper.persistence.SerializerConstant;

public class AddOperationParser extends ModelActionParser {

	public AddOperationParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String classPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
		String[] classPathArray = classPath.split("::");
		String className = classPathArray[classPathArray.length - 1];
		String body = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_BODY);
		int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		communicator.addOperationAsync(diagramId, className, level, body);
	}
}