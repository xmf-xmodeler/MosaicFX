package tool.helper.persistence.modelActionParser;

import java.util.Vector;

import org.w3c.dom.Element;

import tool.helper.persistence.SerializerConstant;

public class AddMetaClassParser extends ModelActionParser {

	public AddMetaClassParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
		int level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		boolean isAbstract = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
		//right now we can not give position information because the position information is saved in an other element
		communicator.addMetaClassAsync(diagramId, name, level, new Vector<>(), isAbstract, 0, 0, false);
	}
}