package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.view.ValueGeneratorStaticDialog;

public class ValueGeneratorStatic extends ValueGenerator implements IValueGenerator {

	private String staticValue;
	private List<String> generatedValue;

	public ValueGeneratorStatic(String attributeType) {
		super(attributeType);
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.STATIC;
	}

	@Override
	public void openDialog(AbstractPackageViewer diagram) {
		setDiagram(diagram);
		if(getFitsType(getAttributeType())){
			ValueGeneratorStaticDialog dlg = new ValueGeneratorStaticDialog(this);
			dlg.showAndWait();
		}
	}

	@Override
	public void setParameter(List<String> parameter) {
		if(getAttributeType().equals(StringValue.TraditionalDataType.INTEGER)) {
			this.staticValue = integerConverter(parameter.get(0));
		} else if (getAttributeType().equals(StringValue.TraditionalDataType.FLOAT)){
			this.staticValue = floatConverter(parameter.get(0));
		} else if (getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)){
			this.staticValue = booleanConverter(parameter.get(0));
		} else {
			this.staticValue = parameter.get(0);
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		this.generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			this.generatedValue.add(getParameter().get(0));
		}
	}

	@Override
	public int possibleGeneratedInstance() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean getFitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		return "String".equals(type);
	}

	@Override
	public String getName2() {
		if(getParameter() ==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getParameter() {
		List<String> parameter = new ArrayList<>();
		parameter.add(this.staticValue);

		return parameter;
	}

	@Override
    public List<String> getGeneratedValue() {
        return this.generatedValue;
    }
}
