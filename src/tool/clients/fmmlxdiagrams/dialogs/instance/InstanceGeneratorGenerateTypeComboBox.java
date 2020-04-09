package tool.clients.fmmlxdiagrams.dialogs.instance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

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
			IncrementGenerator incG = new IncrementGenerator(type);
			StaticGenerator<Integer> sGInt = new StaticGenerator<Integer>("Integer");
			ListGenerator<Integer> listGInt = new ListGenerator<Integer>("Integer" );
			NormalDistributionGenerator<Integer> nDGenerator = new NormalDistributionGenerator<Integer>("Integer");
			RandomGenerator<Integer> rGenerator = new RandomGenerator<Integer>("Integer");
			return FXCollections.observableArrayList(incG, sGInt, listGInt, nDGenerator, rGenerator);
		} else if(type.equals("Float")) {
			IncrementGenerator incF = new IncrementGenerator("Float");
			StaticGenerator<Float> sGFloat = new StaticGenerator<Float>("Float");
			ListGenerator<Float> listGFloat = new ListGenerator<Float>("Float");
			RandomGenerator<Float> rGenerator = new RandomGenerator<Float>("Float");
			NormalDistributionGenerator<Float> nDGenerator = new NormalDistributionGenerator<Float>("Float");
			return FXCollections.observableArrayList(incF, sGFloat, listGFloat, nDGenerator, rGenerator);
		} else if(type.equals("String")) {
			StaticGenerator<String> sString = new StaticGenerator<String>("String");
			ListGenerator<String> listString = new ListGenerator<String>("String");
			return FXCollections.observableArrayList(sString, listString);
		} else if(type.equals("Boolean")) {
			StaticGenerator<String> sGBoolean = new StaticGenerator<String>("Boolean");
			ListGenerator<Boolean> listGBoolean = new ListGenerator<Boolean>("Boolean");
			RandomGenerator<Boolean> rGenerator = new RandomGenerator<Boolean>("Boolean");
			return FXCollections.observableArrayList(sGBoolean, listGBoolean, rGenerator);
		} 
		return  null;
	}
	
}
