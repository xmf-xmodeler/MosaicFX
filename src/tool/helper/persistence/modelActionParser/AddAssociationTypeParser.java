package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.AssociationType;
import tool.clients.fmmlxdiagrams.Level;

public class AddAssociationTypeParser extends ModelActionParser {

	public AddAssociationTypeParser(int diagramId) {
		super(diagramId);
	}

	@Override
	public void parse(Element modelElement) {
		
		Level sourceLevel = new Level(Integer.parseInt(modelElement.getAttribute("sourceLevelMin")), 
				"none".equals(modelElement.getAttribute("sourceLevelMax"))?null:Integer.parseInt(modelElement.getAttribute("sourceLevelMax")));
		Level targetLevel = new Level(Integer.parseInt(modelElement.getAttribute("targetLevelMin")), 
				"none".equals(modelElement.getAttribute("targetLevelMax"))?null:Integer.parseInt(modelElement.getAttribute("targetLevelMax")));
		
		AssociationType aType = new AssociationType(
				modelElement.getAttribute("name"),
				"void",
				modelElement.getAttribute("color"),
				Integer.parseInt(modelElement.getAttribute("strokeWidth")),
				modelElement.getAttribute("dashArray"),
				modelElement.getAttribute("startDeco"),
				modelElement.getAttribute("endDeco"),
				modelElement.getAttribute("colorLink"),
				Integer.parseInt(modelElement.getAttribute("strokeWidthLink")),
				modelElement.getAttribute("dashArrayLink"),
				modelElement.getAttribute("startDecoLink"),
				modelElement.getAttribute("endDecoLink"),
				modelElement.getAttribute("sourceType"),
				modelElement.getAttribute("targetType"),
				sourceLevel,
				targetLevel,
				modelElement.getAttribute("sourceMult"),
				modelElement.getAttribute("targetMult"));
		communicator.addAssociationType(diagramId, aType, (failedAType) -> {});
		
	}
}