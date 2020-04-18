package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.*;

public class InstanceGeneratorGenerateTypeComboBox extends ComboBox<ValueGenerator>{
	
	private FmmlxAttribute attribute;

	public <T> InstanceGeneratorGenerateTypeComboBox(FmmlxAttribute attribute) {
		super(getGenerateTypeList(attribute.getType()));
		this.attribute = attribute;

	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(FmmlxAttribute attribute) {
		this.attribute = attribute;
	}

	@SuppressWarnings("rawtypes")
	public static ObservableList<ValueGenerator> getGenerateTypeList(String type) {
		if(type.equals("Integer")) {
			ValueGeneratorIncrement incG = new ValueGeneratorIncrement(type);
			ValueGeneratorStatic sGInt = new ValueGeneratorStatic("Integer");
			ValueGeneratorList<Integer> listGInt = new ValueGeneratorList<Integer>("Integer" );
			ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution("Integer");
			ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Integer");
			return FXCollections.observableArrayList(incG, sGInt, listGInt, nDGenerator, rGenerator);
		} else if(type.equals("Float")) {
			ValueGeneratorIncrement incF = new ValueGeneratorIncrement("Float");
			ValueGeneratorStatic sGFloat = new ValueGeneratorStatic("Float");
			ValueGeneratorList<Float> listGFloat = new ValueGeneratorList<Float>("Float");
			ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Float");
			ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution("Float");
			return FXCollections.observableArrayList(incF, sGFloat, listGFloat, nDGenerator, rGenerator);
		} else if(type.equals("String")) {
			ValueGeneratorStatic sString = new ValueGeneratorStatic("String");
			ValueGeneratorList<String> listString = new ValueGeneratorList<String>("String");
			return FXCollections.observableArrayList(sString, listString);
		} else if(type.equals("Boolean")) {
			ValueGeneratorStatic sGBoolean = new ValueGeneratorStatic("Boolean");
			ValueGeneratorList<Boolean> listGBoolean = new ValueGeneratorList<Boolean>("Boolean");
			ValueGeneratorRandom rGenerator = new ValueGeneratorRandom("Boolean");
			return FXCollections.observableArrayList(sGBoolean, listGBoolean, rGenerator);
		} 
		return  null;
	}
	
}
