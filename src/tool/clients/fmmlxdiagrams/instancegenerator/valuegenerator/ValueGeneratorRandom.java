package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.view.ValueGeneratorRandomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ValueGeneratorRandom extends ValueGenerator implements IValueGenerator {

	private String minValueParameter;
	private String maxValueParameter;
	private String selectedScenario;
	private List<String> generatedValue;
	
	public ValueGeneratorRandom(String attributeType) {
		super(attributeType);
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.RANDOM;
	}

	@Override
	public void openDialog(FmmlxDiagram diagram) {
		if(getFitsType(getAttributeType())){
			setDiagram(diagram);
			if(getFitsType(getAttributeType())){
				ValueGeneratorRandomDialog dlg = new ValueGeneratorRandomDialog(this);
				dlg.showAndWait();
			}
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		this.generatedValue = new ArrayList<>();
		for(int i = 0 ; i<numberOfInstance; i++){
			this.generatedValue.add(generateRandomValue(getAttributeType(), getSelectedScenario(), getParameter()));
		}
	}

	protected String generateRandomValue(String attributeType, String selectedScenario, List<String> parameter) {
		switch(attributeType){
			case StringValue.TraditionalDataType.INTEGER:
				return generateRandomInteger(selectedScenario, parameter);
			case StringValue.TraditionalDataType.FLOAT:
				return generateRandomFloat(selectedScenario, parameter);
			case StringValue.TraditionalDataType.BOOLEAN:
				return generateRandomBoolean();
			default:
				return "";
		}
	}

	private String generateRandomInteger(String selectedScenario, List<String> parameter){
		Random rd = new Random();
		if(selectedScenario.equals("Free")){
			return rd.nextInt()+"";
		} else{
			if(parameter.size()==2){
				int min = Integer.parseInt(integerConverter(parameter.get(0)));
				int max = Integer.parseInt(integerConverter(parameter.get(1)));
				return ThreadLocalRandom.current().nextInt(min, max+1)+"";
			}
		}
		return "";
	}

	private String generateRandomFloat(String selectedScenario, List<String> parameter){
		if(selectedScenario.equals("Free")){
			Random rd = new Random();
			return rd.nextFloat()+"";
		} else if (parameter.size() == 2) {
			Random rd = new Random();
			float min = Float.parseFloat(floatConverter(parameter.get(0)));
			float max = Float.parseFloat(floatConverter(parameter.get(1)));
			return (min + (rd.nextFloat() * (max - min))) + "";
		}
		return "";
	}

	private String generateRandomBoolean(){
		Random rd2 = new Random();
		boolean bool = rd2.nextBoolean();
		return Boolean.toString(bool);
	}

	@Override
	public int possibleGeneratedInstance() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean getFitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		return "String".equals(type);
	}

	@Override
	public String getName2() {
		if(!getAttributeType().equals(StringValue.TraditionalDataType.BOOLEAN)){
			if(getSelectedScenario().equals("")){
				return getValueGeneratorName()+ " (incomplete)";
			}
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(this.minValueParameter);
		result.add(this.maxValueParameter);
		return result;
	}

	@Override
	public void setParameter(List<String> parameter) {
		if (parameter!=null){
			if(getSelectedScenario().equals("Free")){
				this.minValueParameter=null;
				this.maxValueParameter=null;
			}
			if(parameter.get(0)!=null && parameter.get(1)!=null){
				if(getAttributeType().equals(StringValue.TraditionalDataType.INTEGER)){
					this.minValueParameter = integerConverter(parameter.get(0));
					this.maxValueParameter = integerConverter(parameter.get(1));
				} else if (getAttributeType().equals(StringValue.TraditionalDataType.FLOAT)){
					this.minValueParameter = floatConverter(parameter.get(0));
					this.maxValueParameter = floatConverter(parameter.get(1));
				}
			}
		}
		else {
			this.minValueParameter = null;
			this.maxValueParameter = null;
		}
	}

	@Override
	public List<String> getGeneratedValue() {
		return this.generatedValue;
	}

	public String getSelectedScenario() {
		return this.selectedScenario;
	}

	public void setSelectedScenario(String selectedScenario) {
		this.selectedScenario = selectedScenario;
	}

}
