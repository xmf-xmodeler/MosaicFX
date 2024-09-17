package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

public class SetRoleFillerParser extends ModelActionParser {

	public SetRoleFillerParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String rolePath = modelElement.getAttribute("role");
		String roleFillerPath = modelElement.getAttribute("roleFiller");
		communicator.setRoleFiller(diagramId, rolePath, roleFillerPath);
	}
}