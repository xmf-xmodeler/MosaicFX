package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorRandomDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorRandomDialogResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ValueGeneratorRandom implements ValueGenerator{

	private final String attributeType;
	private String minValueParameter;
	private String maxValueParameter;
	private String selectedScenario;
	private List<String> generatedValue;
	
	public ValueGeneratorRandom(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeType() {
		return attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return "RANDOM";
	}

	@Override
	public void openDialog() {
		if(!attributeType.equals("Boolean")){
			ValueGeneratorRandomDialog dlg = new ValueGeneratorRandomDialog(getValueGeneratorName(),
					getAttributeType(), getSelectedScenario(), getParameter());
			dialogResult(dlg);
		}
	}

	private void dialogResult(ValueGeneratorRandomDialog dlg) {
		Optional<ValueGeneratorRandomDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			ValueGeneratorRandomDialogResult result = opt.get();
			setSelectedScenario(result.getSelectedScenario());
			if(selectedScenario.equals("Range")){
				setParameter(result.getParameter());
			} else {
				List<String> param = new ArrayList<>();
				param.add(null);
				param.add(null);
				setParameter(param);
			}
			System.out.println(getSelectedScenario());
			System.out.println(getParameter().toString());
		}
	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();
		for(int i = 0 ; i<numberOfInstance; i++){
			generatedValue.add(generateRandomValue(getAttributeType(), getSelectedScenario(), getParameter()));
		}
		return generatedValue;
	}

	protected String generateRandomValue(String attributeType, String selectedScenario, List<String> parameter) {
		switch(attributeType){
			case "Integer":
				return generateRandomInteger(selectedScenario, parameter);
			case "Float":
				return generateRandomFloat(selectedScenario, parameter);
			case "Boolean":
				return generateRandomBoolean();
			default:
				return "";
		}
	}

	private String integerConverter(String value) {
		try {
			return Integer.parseInt(value)+"";
		} catch (Exception e){
			return Math.round(Float.parseFloat(value))+"";
		}
	}

	private String floatConverter(String value) {
		try {
			return Float.parseFloat(value)+"";
		} catch (Exception e){
			return (float)Integer.parseInt(value)+"";
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
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		return "String".equals(type);
	}

	@Override
	public String getName2() {
		if(!getAttributeType().equals("Boolean")){
			if(getSelectedScenario().equals("")){
				return getValueGeneratorName()+ " (incomplete)";
			}
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getParameter() {
		List<String> result = new ArrayList<>();
		result.add(minValueParameter);
		result.add(maxValueParameter);
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
				if(attributeType.equals("Integer")){
					this.minValueParameter = integerConverter(parameter.get(0));
					this.maxValueParameter = integerConverter(parameter.get(1));
				} else if (attributeType.equals("Float")){
					this.minValueParameter = floatConverter(parameter.get(0));
					this.maxValueParameter = floatConverter(parameter.get(1));
				}
			}
		}
	}

	@Override
	public List<String> getGeneratedValue() {
		return generatedValue;
	}

	public String getSelectedScenario() {
		return selectedScenario;
	}

	public void setSelectedScenario(String selectedScenario) {
		this.selectedScenario = selectedScenario;
	}

}
