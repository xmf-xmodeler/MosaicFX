package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorStaticDialogResult;

public class StaticGenerator implements ValueGenerator{

	private String value;
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
		if (value!=null) {
			List<String> values = new ArrayList<>();
			values.add(value);
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type, values);
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
					setValue(result.getValueInt().toString());
					break;
				case "Float":
					setValue(result.getValueFloat().toString());
					break;
				case "Boolean":
					setValue(result.getValueBool().toString());
					break;
				case "String":
					setValue(result.getValueString());
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

	public void setValue(String value) {
		if(type.equals("Integer")) {
			this.value = integerConverter(value);
		} else if (type.equals("Float")){
			this.value = floatConverter(value);
		} else{
			this.value = value;
		}
	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			generatedValue.add(value);
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
		if(value==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

    @Override
    public List<String> getValues() {
        return generatedValue;
    }
}
