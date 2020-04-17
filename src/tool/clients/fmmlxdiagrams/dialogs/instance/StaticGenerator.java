package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorStaticDialogResult;

public class StaticGenerator implements ValueGenerator{

	private List<String> parameter;
	private final String type;
	private List<String> generatedValue;

	public StaticGenerator(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return "STATIC";
	}

	@Override
	public void openDialog() {
		if (parameter !=null) {
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type, parameter);
			dialogResult(dlg);
			
		} else {
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type);
			dialogResult(dlg);
		}	
	}
	
	private void dialogResult(AttributeGeneratorStaticDialog dlg) {
		Optional<AttributeGeneratorStaticDialogResult> opt = dlg.showAndWait();
		if (opt.isPresent()) {
			AttributeGeneratorStaticDialogResult result = opt.get();
			switch (type) {
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
		if(type.equals("Integer")) {
			this.parameter = listToIntConverter(parameter);
		} else if (type.equals("Float")){
			this.parameter = listToFloatConverter(parameter);
		} else{
			this.parameter = parameter;
		}
	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			generatedValue.add(parameter.get(0));
		}
		return generatedValue;
	}

	@Override
	public int possibleGeneratedInstance() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		return "String".equals(type);
	}

	@Override
	public String getName2() {
		if(parameter ==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

	@Override
	public List<String> getParameter() {
		return parameter;
	}

	@Override
    public List<String> getValues() {
        return generatedValue;
    }
}
