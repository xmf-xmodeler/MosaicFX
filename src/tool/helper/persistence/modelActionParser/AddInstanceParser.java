package tool.helper.persistence.modelActionParser;

import java.util.Vector;

import org.w3c.dom.Element;

import tool.helper.persistence.SerializerConstant;

public class AddInstanceParser extends ModelActionParser {

	public AddInstanceParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
		String[] ofStringArray = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_OF).split("::");
		String ofName = ofStringArray[2];
		boolean isAbstract = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
		int level = -2; // for now: magic number for /not found & not needed, one less theh class by
						// default
		try {
			level = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		} catch (Exception e) {
		}
		communicator.addNewInstanceAsync(diagramId, ofName, name, level, new Vector<String>(), isAbstract, 0, 0, false);
	}

}