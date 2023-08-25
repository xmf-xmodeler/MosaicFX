package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.serializer.SerializerConstant;

public class AddAttributeParser extends ModelActionParser {

	public AddAttributeParser(Integer diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
		String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
		String[] classPathArray = classpath.split("::");
		String className = classPathArray[classPathArray.length - 1];
		int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		String typePath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_TYPE);
		String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MULTIPLICITY);
		String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
		String[] multiplicityArray = multiplicitySubString.split(",");
		int upper = Integer.parseInt(multiplicityArray[0]);
		int under = Integer.parseInt(multiplicityArray[1]);
		boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
		boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
		Multiplicity multiplicity = new Multiplicity(upper, under, upperLimit, ordered, false);

		String[] typePathArray = typePath.split("::");
		String typeName = typePathArray[typePathArray.length - 1];
		communicator.addAttributeAsync(diagramId, className, name, level, typeName, multiplicity);
	}
}