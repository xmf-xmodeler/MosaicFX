package tool.helper.persistence.modelActionParser;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.helper.persistence.SerializerConstant;

public class AddAssociationParser extends ModelActionParser {

	public AddAssociationParser(int diagramId) {
		super(diagramId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parse(Element modelElement) {
		String classSourceName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_SOURCE);// classPathArray1[classPathArray1.length-1];
		String classpath2 = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_CLASS_TARGET);
		String accessSourceFromTargetName = modelElement
				.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_SOURCE_FROM_TARGET);
		String accessTargetFromSourceName = modelElement
				.getAttribute(SerializerConstant.ATTRIBUTE_ACCESS_TARGET_FROM_SOURCE);

		String fwName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_FW_NAME);
		String reverseName = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_REVERSE_NAME);

		Multiplicity multiplicityT2S;
		{
			String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_T2S_MULTIPLICITY);
			String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
			String[] multiplicityArray = multiplicitySubString.split(",");
			int min = Integer.parseInt(multiplicityArray[0]);
			int max = Integer.parseInt(multiplicityArray[1]);
			boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
			boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
			multiplicityT2S = new Multiplicity(min, max, upperLimit, ordered, false);
		}

		Multiplicity multiplicityS2T;
		{
			String multiplicityString = modelElement.getAttribute(SerializerConstant.ATTRIBUTE_S2T_MULTIPLICITY);
			String multiplicitySubString = multiplicityString.substring(4, multiplicityString.length() - 1);
			String[] multiplicityArray = multiplicitySubString.split(",");
			int min = Integer.parseInt(multiplicityArray[0]);
			int max = Integer.parseInt(multiplicityArray[1]);
			boolean upperLimit = Boolean.parseBoolean(multiplicityArray[2]);
			boolean ordered = Boolean.parseBoolean(multiplicityArray[3]);
			multiplicityS2T = new Multiplicity(min, max, upperLimit, ordered, false);
		}

		int instLevelSource = Integer
				.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_SOURCE));
		int instLevelTarget = Integer
				.parseInt(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_INST_LEVEL_TARGET));

		boolean sourceVisibleFromTarget = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_SOURCE_VISIBLE));
		boolean targetVisibleFromSource = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_TARGET_VISIBLE));

		boolean isSymmetric = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_SYMMETRIC));
		boolean isTransitive = Boolean
				.parseBoolean(modelElement.getAttribute(SerializerConstant.ATTRIBUTE_IS_TRANSITIVE));

		communicator.addAssociationAsync(diagramId, classSourceName, classpath2, accessSourceFromTargetName,
				accessTargetFromSourceName, fwName, reverseName, multiplicityT2S, multiplicityS2T, instLevelSource,
				instLevelTarget, sourceVisibleFromTarget, targetVisibleFromSource, isSymmetric, isTransitive);
	}
}