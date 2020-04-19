package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorIncrementDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorIncrementDialogResult;

public class ValueGeneratorIncrement implements ValueGenerator{

	private final String attributeType;

	private String startValue;
	private String endValue;
	private String inc;

	private List<String> generatedValue;

	public ValueGeneratorIncrement(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return "INCREMENT";
	}

	@Override
	public void openDialog() {
		if (getFitsType(getAttributeType())){
			ValueGeneratorIncrementDialog dlg = new ValueGeneratorIncrementDialog(getValueGeneratorName(), getAttributeType(), getParameter() );
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
			if(getAttributeType().equals("Integer")){
				int subtotal= Integer.parseInt(getParameter().get(0));
				while(subtotal<=Integer.parseInt(getParameter().get(1))){
					this.generatedValue.add(subtotal+"");
					subtotal+=Integer.parseInt(getParameter().get(2));
				}
			}else if(getAttributeType().equals("Float")){
				float subtotal= Float.parseFloat(getParameter().get(0));
				while(subtotal<=Float.parseFloat(getParameter().get(1))){
					this.generatedValue.add(subtotal+"");
					subtotal+=Float.parseFloat(getParameter().get(2));
				}
			}

		} catch (Exception ignored){

		}
	}

    @Override
    public int possibleGeneratedInstance() {
		int counter = 0;

		if(getAttributeType().equals("Integer")){
			int subtotal= Integer.parseInt(getParameter().get(0));
			while(subtotal<=Integer.parseInt(getParameter().get(1))){
				subtotal+=Integer.parseInt(getParameter().get(2));
				counter+=1;
			}
		}else if(getAttributeType().equals("Float")){
			float subtotal= Float.parseFloat(getParameter().get(0));
			while(subtotal<=Float.parseFloat(getParameter().get(1))){
				subtotal+=Float.parseFloat(getParameter().get(2));
				counter+=1;
			}
		}
		return counter;
    }

    @Override
	public boolean getFitsType(String type) {
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public String getName2() {
		if(this.startValue==null || this.endValue==null || this.inc==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getGeneratedValue() {
		return this.generatedValue;
	}

	public void setParameter(List<String> parameter){
		if(getAttributeType().equals("Integer")){
			this.startValue = integerConverter(parameter.get(0));
			this.endValue= integerConverter(parameter.get(1));
			this.inc = integerConverter(parameter.get(2));

		} else if (getAttributeType().equals("Float")){
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
		parameter.add(this.startValue);
		parameter.add(this.endValue);
		parameter.add(this.inc);

		return parameter;
	}

}
