package tool.helper.persistence.modelActionParser;

import java.util.Vector;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.Level;
import tool.helper.persistence.SerializerConstant;

public class AddMetaClassParser extends ModelActionParser {

	public AddMetaClassParser(int diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		Level level = defineLevel(modelElement); 
		String name = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_NAME);
		boolean isAbstract = Boolean.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_ABSTRACT));
		boolean isSingleton = Boolean.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SINGLETON)); 
		
		//right now we can not give position information because the position information is saved in an other element
		communicator.addMetaClass(diagramId, name, level, new Vector<>(), isAbstract, isSingleton, 0, 0, false);
	}

	private Level defineLevel(Element modelElement) {
		int minLevel = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_LEVEL));
		if (!modelElement.hasAttribute("maxLevel")) {
			return new Level(minLevel);
		}
		if ("none".equals(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MAX_LEVEL))) {
			return new Level(minLevel, null);
		}
		int maxLevel = Integer.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_MAX_LEVEL));
		return new Level(minLevel, maxLevel);
	}
}