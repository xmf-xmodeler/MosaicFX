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
			StaticGenerator sGInt = new StaticGenerator("Integer");
			ListGenerator<Integer> listGInt = new ListGenerator<Integer>("Integer" );
			NormalDistributionGenerator<Integer> nDGenerator = new NormalDistributionGenerator<Integer>("Integer");
			RandomGenerator rGenerator = new RandomGenerator("Integer");
			return FXCollections.observableArrayList(incG, sGInt, listGInt, nDGenerator, rGenerator);
		} else if(type.equals("Float")) {
			IncrementGenerator incF = new IncrementGenerator("Float");
			StaticGenerator sGFloat = new StaticGenerator("Float");
			ListGenerator<Float> listGFloat = new ListGenerator<Float>("Float");
			RandomGenerator rGenerator = new RandomGenerator("Float");
			NormalDistributionGenerator<Float> nDGenerator = new NormalDistributionGenerator<Float>("Float");
			return FXCollections.observableArrayList(incF, sGFloat, listGFloat, nDGenerator, rGenerator);
		} else if(type.equals("String")) {
			StaticGenerator sString = new StaticGenerator("String");
			ListGenerator<String> listString = new ListGenerator<String>("String");
			return FXCollections.observableArrayList(sString, listString);
		} else if(type.equals("Boolean")) {
			StaticGenerator sGBoolean = new StaticGenerator("Boolean");
			ListGenerator<Boolean> listGBoolean = new ListGenerator<Boolean>("Boolean");
			RandomGenerator rGenerator = new RandomGenerator("Boolean");
			return FXCollections.observableArrayList(sGBoolean, listGBoolean, rGenerator);
		} 
		return  null;
	}
	
}