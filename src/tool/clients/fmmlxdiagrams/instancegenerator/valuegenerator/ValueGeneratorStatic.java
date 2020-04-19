package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorStaticDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorStaticDialogResult;

public class ValueGeneratorStatic implements ValueGenerator{

	private List<String> parameter;
	private final String attributeType;
	private List<String> generatedValue;

	public ValueGeneratorStatic(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	public String getAttributeType() {
		return attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return "STATIC";
	}

	@Override
	public void openDialog() {
		if(getFitsType(getAttributeType())){
			ValueGeneratorStaticDialog dlg = new ValueGeneratorStaticDialog(getValueGeneratorName(), getAttributeType(), getParameter());
			dialogResult(dlg);
		}
	}
	
	private void dialogResult(ValueGeneratorStaticDialog dlg) {
		Optional<ValueGeneratorStaticDialogResult> opt = dlg.showAndWait();
		if (opt.isPresent()) {
			ValueGeneratorStaticDialogResult result = opt.get();
			switch (getAttributeType()) {
				case "Integer":
					setParameter(result.getValueInt());
					break;
				case "Float":
					setParameter(result.getValueFloat());
					break;
				case "Boolean":
					setParameter(result.getValueBool());
					break;
				case "String":
					setParameter(result.getValueString());
					break;
			}
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
		if(attributeType.equals("Integer")) {
			this.parameter = listToIntConverter(parameter);
		} else if (getAttributeType().equals("Float")){
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
