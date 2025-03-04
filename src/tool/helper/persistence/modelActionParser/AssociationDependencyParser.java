package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.AssociationType;
import tool.clients.fmmlxdiagrams.Level;

public class AssociationDependencyParser extends ModelActionParser {

	public AssociationDependencyParser(int diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		
		String classPath0 = modelElement.getAttribute("classAssoc0");
		String classPath1 = modelElement.getAttribute("classAssoc1");
		String idAssoc0 = modelElement.getAttribute("idAssoc0");
		String idAssoc1 = modelElement.getAttribute("idAssoc1");
		
		communicator.addAssociationDependency(diagramId, classPath0, idAssoc0, classPath1, idAssoc1);
		
	}
}