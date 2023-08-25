package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.serializer.SerializerConstant;

public class ChangeSlotValueParser extends ModelActionParser {

	public ChangeSlotValueParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
		String[] classPathArray = classpath.split("::");
		String className = classPathArray[classPathArray.length - 1];
		String slotName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_SLOT_NAME);
		String valueToBeParsed = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_VALUE_TOBE_PARSED);
		communicator.changeSlotValueAsync(diagramId, className, slotName, valueToBeParsed);
	}
}