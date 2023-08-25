package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.serializer.SerializerConstant;

public class AddLinkParser extends ModelActionParser {

	public AddLinkParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);

		String classpath1 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);
		String[] classPathArray1 = classpath1.split("::");
		String className1 = classPathArray1[classPathArray1.length - 1];

		String classpath2 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
		String[] classPathArray2 = classpath2.split("::");
		String className2 = classPathArray2[classPathArray2.length - 1];

		communicator.addAssociationInstanceAsync(diagramId, className1, className2, name);
	}
}