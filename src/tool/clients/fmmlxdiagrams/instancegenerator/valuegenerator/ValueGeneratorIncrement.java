package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.view.ValueGeneratorIncrementDialog;

public class ValueGeneratorIncrement extends ValueGenerator implements IValueGenerator {

	private String startValue;
	private String endValue;
	private String inc;
	private List<String> generatedValue;

	public ValueGeneratorIncrement(String attributeType) {
		super(attributeType);
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.INCREMENT;
	}

	@Override
	public String getName2() {
		if(this.startValue==null || this.endValue==null || this.inc==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public boolean getFitsType(String type) {
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
	}

	@Override
	public void openDialog(AbstractPackageViewer diagram) {
		setDiagram(diagram);
		if (getFitsType(getAttributeType())){
			ValueGeneratorIncrementDialog dlg = new ValueGeneratorIncrementDialog(this);
			dlg.showAndWait();
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

	public List<String> getParameter(){
		List<String> parameter = new ArrayList<>();
		parameter.add(this.startValue);
		parameter.add(this.endValue);
		parameter.add(this.inc);

		return parameter;
	}

	@Override
	public List<String> getGeneratedValue() {
		return this.generatedValue;
	}

}
