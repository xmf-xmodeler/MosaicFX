package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorStaticDialog;

public class ValueGeneratorStatic implements ValueGenerator{

	private List<String> parameter;
	private final String attributeType;
	private List<String> generatedValue;
	private FmmlxDiagram diagram;

	public ValueGeneratorStatic(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.STATIC;
	}

	@Override
	public void openDialog(FmmlxDiagram diagram) {
		this.diagram = diagram;
		if(getFitsType(getAttributeType())){
			ValueGeneratorStaticDialog dlg = new ValueGeneratorStaticDialog(this);
			dlg.showAndWait();
		}
	}


	private String floatConverter(String value) {
		try {
			return Float.parseFloat(value)+"";
		} catch (Exception e){
			return (float)Integer.parseInt(value)+"";
		}
	}

	private String integerConverter(String value) {
		try {
			return Integer.parseInt(value)+"";
		} catch (Exception e){
			return Math.round(Float.parseFloat(value))+"";
		}
	}

	private List<String> listToIntConverter(List<String> value){
		List<String> result = new ArrayList<>();
		for (String str : value){
			result.add(integerConverter(str));
		}
		return result;
	}

	private List<String> listToFloatConverter(List<String> value){
		List<String> result = new ArrayList<>();
		for (String str : value){
			result.add(floatConverter(str));
		}
		return result;
	}

	@Override
	public void setParameter(List<String> parameter) {
		if(getAttributeType().equals(StringValue.TraditionalDataType.INTEGER)) {
			this.parameter = listToIntConverter(parameter);
		} else if (attributeType.equals(StringValue.TraditionalDataType.FLOAT)){
			this.parameter = listToFloatConverter(parameter);
		} else{
			this.parameter = parameter;
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
		return this.parameter;
	}

	@Override
    public List<String> getGeneratedValue() {
        return this.generatedValue;
    }
}
