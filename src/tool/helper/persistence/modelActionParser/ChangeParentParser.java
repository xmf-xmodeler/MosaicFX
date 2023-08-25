package tool.helper.persistence.modelActionParser;

import java.util.Vector;

import org.w3c.dom.Element;

import tool.clients.serializer.SerializerConstant;

public class ChangeParentParser extends ModelActionParser {

	public ChangeParentParser(Integer diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		String classpath = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS);
		String[] classPathArray = classpath.split("::");
		String className = classPathArray[classPathArray.length - 1];

		String oldParentPathsString = modelElement.getAttribute("old");
		Vector<String> oldParents = new Vector<>();
		if (!oldParentPathsString.equals("")) {
			String[] oldParentPathsArray = oldParentPathsString.split(",");

			for (String s : oldParentPathsArray) {
				String[] parentPathArray = s.split("::");
				oldParents.add(parentPathArray[parentPathArray.length - 1]);
			}
		}

		String newParentPathsString = modelElement.getAttribute("new");
		Vector<String> newParents = new Vector<>();
		if (!newParentPathsString.equals("")) {
			String[] newParentPathsArray = newParentPathsString.split(",");

			for (String s : newParentPathsArray) {
				String[] newParentPathArray = s.split("::");
				newParents.add(newParentPathArray[newParentPathArray.length - 1]);
			}
		}

		communicator.changeParentAsync(diagramId, className, oldParents, newParents);

	}

}
