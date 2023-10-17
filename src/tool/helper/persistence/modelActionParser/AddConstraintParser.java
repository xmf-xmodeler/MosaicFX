package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

public class AddConstraintParser extends ModelActionParser {

	public AddConstraintParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String path = modelElement.getAttribute("class");
		String constName = modelElement.getAttribute("constName");
		Integer instLevel = Integer.parseInt(modelElement.getAttribute("instLevel"));
		String body = modelElement.getAttribute("body");
		String reason = modelElement.getAttribute("reason");
		//TODO in xlst berücksichtigen
		// FOR SAVEFILES BEFORE 5/3/22

		if (body.startsWith("@Operation body(classifier : Class,level : Integer):Boolean")) {
			body = body.substring("@Operation body(classifier : Class,level : Integer):Boolean".length());
			body = body.substring(0, body.length() - 3);
		}

		if (reason.startsWith("@Operation reason(classifier : Class,level : Integer):String")) {
			reason = reason.substring("@Operation reason(classifier : Class,level : Integer):String".length());
			reason = reason.substring(0, reason.length() - 3);
		}

		communicator.addConstraintAsync(diagramId, path, constName, instLevel, body, reason);
	}
}