package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.*;

public class InstanceGeneratorGenerateTypeComboBox extends ComboBox<ValueGenerator>{
	
	private FmmlxAttribute attribute;

	public InstanceGeneratorGenerateTypeComboBox(FmmlxAttribute attribute) {
		super(getGenerateTypeList(attribute.getType()));
		this.attribute = attribute;

	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(FmmlxAttribute attribute) {
		this.attribute = attribute;
	}

	public static ObservableList<ValueGenerator> getGenerateTypeList(String type) {
		switch (type) {
			case "Integer": {
				ValueGeneratorIncrement incG = new ValueGeneratorIncrement(type);
				ValueGeneratorStatic sGInt = new ValueGeneratorStatic("Integer");
				ValueGeneratorList listGInt = new ValueGeneratorList("Integer");
				ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution("Integer");
				ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Integer");
				return FXCollections.observableArrayList(incG, sGInt, listGInt, nDGenerator, rGenerator);
			}
			case "Float": {
				ValueGeneratorIncrement incF = new ValueGeneratorIncrement("Float");
				ValueGeneratorStatic sGFloat = new ValueGeneratorStatic("Float");
				ValueGeneratorList listGFloat = new ValueGeneratorList("Float");
				ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Float");
				ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution("Float");
				return FXCollections.observableArrayList(incF, sGFloat, listGFloat, nDGenerator, rGenerator);
			}
			case "String":
				ValueGeneratorStatic sString = new ValueGeneratorStatic("String");
				ValueGeneratorList listString = new ValueGeneratorList("String");
				return FXCollections.observableArrayList(sString, listString);
			case "Boolean": {
				ValueGeneratorStatic sGBoolean = new ValueGeneratorStatic("Boolean");
				ValueGeneratorList listGBoolean = new ValueGeneratorList("Boolean");
				ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Boolean");
				return FXCollections.observableArrayList(sGBoolean, listGBoolean, rGenerator);
			}
		}
		return  null;
	}
	
}
