package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorIncrementDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorIncrementDialogResult;

public class ValueGeneratorIncrement implements ValueGenerator{
	
	private String startValue;
	private String endValue;
	private String inc;
	private final String type;
	private List<String> generatedValue;

	public ValueGeneratorIncrement(String type) {
		super();
		this.type = type;
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.INCREMENT;
	}

	@Override
	public void openDialog() {
		
		if (startValue != null && endValue!=null && inc!=null) {
			List<String> parameter = new ArrayList<>();
			parameter.add(startValue);
			parameter.add(endValue);
			parameter.add(inc);
			ValueGeneratorIncrementDialog dlg = new ValueGeneratorIncrementDialog(getValueGeneratorName(), type, parameter );
			dialogResult(dlg);
			
		} else {
			ValueGeneratorIncrementDialog dlg = new ValueGeneratorIncrementDialog(getValueGeneratorName(), type);
			dialogResult(dlg);
		}
	}

	private void dialogResult(ValueGeneratorIncrementDialog dlg) {
		Optional<ValueGeneratorIncrementDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			ValueGeneratorIncrementDialogResult result = opt.get();
			setParameter(result.getParameter());
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		try {
			this.generatedValue = new ArrayList<>();
			if(type.equals(StringValue.TraditionalDataType.INTEGER)){
				int subtotal= Integer.parseInt(startValue);
				while(subtotal<=Integer.parseInt(endValue)){
					this.generatedValue.add(subtotal+"");
					subtotal+=Integer.parseInt(inc);
				}
			}else if(type.equals(StringValue.TraditionalDataType.FLOAT)){
				float subtotal= Float.parseFloat(startValue);
				while(subtotal<=Float.parseFloat(endValue)){
					this.generatedValue.add(subtotal+"");
					subtotal+=Float.parseFloat(inc);
				}
			}
		} catch (Exception e){

		}
	}

    @Override
    public int possibleGeneratedInstance() {
		int counter = 0;

		if(type.equals(StringValue.TraditionalDataType.INTEGER)){
			int subtotal= Integer.parseInt(startValue);
			while(subtotal<=Integer.parseInt(endValue)){
				subtotal+=Integer.parseInt(inc);
				counter+=1;
			}
		}else if(type.equals(StringValue.TraditionalDataType.FLOAT)){
			float subtotal= Float.parseFloat(startValue);
			while(subtotal<=Float.parseFloat(endValue)){
				subtotal+=Float.parseFloat(inc);
				counter+=1;
			}
		}
		return counter;
    }

    @Override
	public boolean fitsType(String type) {
		if(StringValue.TraditionalDataType.INTEGER.equals(type)) return true;
		return StringValue.TraditionalDataType.FLOAT.equals(type);
	}

	public String getType() {
		return type;
	}

	@Override
	public String getName2() {
		if(startValue==null || endValue==null || inc==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getGeneratedValue() {
		return generatedValue;
	}

	public void setParameter(List<String> parameter){

		if(type.equals(StringValue.TraditionalDataType.INTEGER)){
			this.startValue = integerConverter(parameter.get(0));
			this.endValue= integerConverter(parameter.get(1));
			this.inc = integerConverter(parameter.get(2));

		} else if (type.equals(StringValue.TraditionalDataType.FLOAT)){
			this.startValue = floatConverter(parameter.get(0));
			this.endValue = floatConverter(parameter.get(1));
			this.inc = floatConverter(parameter.get(2));
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

	public List<String> getParameter(){
		List<String> parameter = new ArrayList<>();
		parameter.add(startValue);
		parameter.add(endValue);
		parameter.add(inc);

		return parameter;
	}

}
