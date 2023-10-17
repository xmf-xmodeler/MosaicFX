package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

public class AddEnumerationValueParser extends ModelActionParser {

	public AddEnumerationValueParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String enumName = modelElement.getAttribute("enum_name");
		String itemName = modelElement.getAttribute("enum_value_name");
		communicator.addEnumerationValueAsync(diagramId, enumName, itemName);
	}
}