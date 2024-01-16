package tool.helper.persistence.modelActionParser;

import java.util.Vector;
import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.Level;
import tool.helper.persistence.SerializerConstant;

public class AddInstanceParser extends ModelActionParser {

	public AddInstanceParser(int diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
		String ofPath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_OF);
		boolean isAbstract = Boolean.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
		boolean isSingleton = Boolean.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SINGLETON));
		Level level = defineLevel(modelElement);
		communicator.addNewInstance(diagramId, ofPath, name, level, new Vector<>(), isAbstract, isSingleton, 0, 0, false);
	}
	
	private Level defineLevel(Element modelElement) {
		if (!modelElement.hasAttribute(SerializerConstant.ATTRIBUTE_LEVEL)) {
			return new Level(-2); // for now: magic number for /not found & not needed, one less then class by default
		}
		
		int minLevel = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		if (!modelElement.hasAttribute(SerializerConstant.ATTRIBUTE_MAX_LEVEL)) {
			return new Level(minLevel);
		}
		if ("none".equals(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MAX_LEVEL))) {
			return new Level(minLevel, null);
		}
		int maxLevel = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MAX_LEVEL));
		return new Level(minLevel, maxLevel);
	}
}