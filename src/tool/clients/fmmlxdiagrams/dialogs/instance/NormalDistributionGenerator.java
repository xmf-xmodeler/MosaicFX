package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorNormalDistributionDialogResult;


public class NormalDistributionGenerator implements ValueGenerator{

	private List<String> parameters;
	private final String type;
	private List<String> generatedValue;

	
	public NormalDistributionGenerator(String type) {
		super();
		this.type = type;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return "NORMAL DISTRIBUTION";
	}

	@Override
	public void openDialog() {
		if (parameters != null){
			AttributeGeneratorNormalDistributionDialog dlg = new AttributeGeneratorNormalDistributionDialog(getName(), type, parameters);
			dialogResult(dlg);
		} else {
			AttributeGeneratorNormalDistributionDialog dlg = new AttributeGeneratorNormalDistributionDialog(getName(), type);
			dialogResult(dlg);
		}
	}

	private void dialogResult(AttributeGeneratorNormalDistributionDialog dlg) {
		Optional<AttributeGeneratorNormalDistributionDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()){
			AttributeGeneratorNormalDistributionDialogResult result = opt.get();
			setParameters(result.getMeanValue(), result.getStdDevValue(), result.getRangeMinValue(), result.getRangeMaxValue());
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

	public void setParameters(String mean, String std, String min, String max) {
		this.parameters = new ArrayList<>();
		if(type.equals("Integer")){
			parameters.add(integerConverter(mean));
			parameters.add(integerConverter(std));
			parameters.add(integerConverter(min));
			parameters.add(integerConverter(max));
		} else if (type.equals("Float")){
			parameters.add(floatConverter(mean));
			parameters.add(floatConverter(std));
			parameters.add(floatConverter(min));
			parameters.add(floatConverter(max));
		}
	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			generatedValue.add(generateValue(type, parameters.get(0), parameters.get(1),
					parameters.get(2), parameters.get(3)));
		}
		return generatedValue;
	}

	public String generateValue(String attributeType, String mean, String stdDeviation, String rangeMin, String rangeMax){
		Random random = new Random();
		if(attributeType.equals("Integer")){
			int meanInt = Integer.parseInt(integerConverter(mean));
			int stdDevInt = Integer.parseInt(integerConverter(stdDeviation));
			int rangeMinInt = Integer.parseInt(integerConverter(rangeMin));
			int rangeMaxInt = Integer.parseInt(integerConverter(rangeMax));
			while(true){
				long nextGauss = Math.round((random.nextGaussian()*stdDevInt)+meanInt);
				if(nextGauss<=rangeMaxInt && nextGauss>=rangeMinInt){
					return nextGauss+"";
				}
			}
		} else if (attributeType.equals("Float")){
			float meanFloat = Float.parseFloat(floatConverter(mean));
			float stdDevFloat = Float.parseFloat(floatConverter(stdDeviation));
			float rangeMinFloat = Float.parseFloat(integerConverter(rangeMin));
			float rangeMaxFloat = Float.parseFloat(integerConverter(rangeMax));
			while(true){
				double nextGauss = (random.nextGaussian()*stdDevFloat)+meanFloat;
				if(nextGauss<=rangeMaxFloat && nextGauss>=rangeMinFloat){
					return nextGauss+"";
				}
			}
		}
		return "";
	}

	@Override
	public int possibleGeneratedInstance() {
		return 0;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
	}

	@Override
	public String getName2() {
		if(parameters ==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

	@Override
	public List<String> getValues() {
		return generatedValue;
	}

}
