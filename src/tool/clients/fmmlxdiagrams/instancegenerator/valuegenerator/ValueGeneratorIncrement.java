package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
		return "INCREMENT";
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
	public List<String> generate(int numberOfInstance) {
		try {
			int counter = 0;
			generatedValue = new ArrayList<>();
			if(type.equals("Integer")){
				int subtotal= Integer.parseInt(startValue);
				while(subtotal<=Integer.parseInt(endValue)){
					generatedValue.add(subtotal+"");
					subtotal+=Integer.parseInt(inc);
					counter+=1;
				}
			}else if(type.equals("Float")){
				float subtotal= Float.parseFloat(startValue);
				while(subtotal<=Float.parseFloat(endValue)){
					generatedValue.add(subtotal+"");
					subtotal+=Float.parseFloat(inc);
					counter+=1;
				}
			}

			List<String> result = new ArrayList<>();
			for(int i = 0 ; i<numberOfInstance; i++){
				result.add(generatedValue.get(i));
			}
			return result;
		} catch (Exception e){
			return null;
		}
	}

    @Override
    public int possibleGeneratedInstance() {
		int counter = 0;

		if(type.equals("Integer")){
			int subtotal= Integer.parseInt(startValue);
			while(subtotal<=Integer.parseInt(endValue)){
				subtotal+=Integer.parseInt(inc);
				counter+=1;
			}
		}else if(type.equals("Float")){
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
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
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

		if(type.equals("Integer")){
			this.startValue = integerConverter(parameter.get(0));
			this.endValue= integerConverter(parameter.get(1));
			this.inc = integerConverter(parameter.get(2));

		} else if (type.equals("Float")){
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
