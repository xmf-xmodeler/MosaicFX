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
		String[] rolePathArray1 = rolePath.split("::");
		String role = rolePathArray1[rolePathArray1.length - 1];

		String roleFillerPath = modelElement.getAttribute("roleFiller");
		String[] roleFillerPathArray = roleFillerPath.split("::");
		String roleFiller = roleFillerPathArray[roleFillerPathArray.length - 1];

		communicator.setRoleFiller(diagramId, role, roleFiller);
	}
}