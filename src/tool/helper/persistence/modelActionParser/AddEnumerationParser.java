package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

public class AddEnumerationParser extends ModelActionParser {

	public AddEnumerationParser(int diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		String enumName = modelElement.getAttribute("name");
		communicator.addEnumerationAsync(diagramId, enumName);
	}
}