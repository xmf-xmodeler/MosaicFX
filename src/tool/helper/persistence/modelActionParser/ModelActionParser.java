package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

public abstract class ModelActionParser {
	
	FmmlxDiagramCommunicator communicator = FmmlxDiagramCommunicator.getCommunicator();
	int diagramId;
	
	public ModelActionParser(int diagramId) {
		this.diagramId = diagramId;
	}

	public abstract void parse(Element modelElement);
	
}
