package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorNormalDistributionDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorNormalDistributionDialogResult;


public class ValueGeneratorNormalDistribution implements ValueGenerator{

	private List<String> parameters;
	private final String attributeType;
	private List<String> generatedValue;

	
	public ValueGeneratorNormalDistribution(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	@Override
	public void openDialog() {
		if (parameters != null){
			ValueGeneratorNormalDistributionDialog dlg = new ValueGeneratorNormalDistributionDialog(getValueGeneratorName(), attributeType, parameters);
			dialogResult(dlg);
		} else {
			ValueGeneratorNormalDistributionDialog dlg = new ValueGeneratorNormalDistributionDialog(getValueGeneratorName(), attributeType);
			dialogResult(dlg);
		}
	}

	private void dialogResult(ValueGeneratorNormalDistributionDialog dlg) {
		Optional<ValueGeneratorNormalDistributionDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()){
			ValueGeneratorNormalDistributionDialogResult result = opt.get();
			setParameter(result.getParameter());
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

	public List<String> getParameter() {
		return parameters;
	}

	@Override
	public void setParameter(List<String> param) {
		this.parameters = new ArrayList<>();
		if(attributeType.equals("Integer")){
			parameters.add(integerConverter(param.get(0)));
			parameters.add(integerConverter(param.get(1)));
			parameters.add(integerConverter(param.get(2)));
			parameters.add(integerConverter(param.get(3)));
		} else if (attributeType.equals("Float")){
			parameters.add(floatConverter(param.get(0)));
			parameters.add(floatConverter(param.get(1)));
			parameters.add(floatConverter(param.get(2)));
			parameters.add(floatConverter(param.get(3)));
		}
	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			generatedValue.add(generateValue(attributeType, parameters.get(0), parameters.get(1),
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
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	public String getAttributeType() {
		return attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return "NORMAL DISTRIBUTION";
	}


	@Override
	public List<String> getGeneratedValue() {
		return generatedValue;
	}
}
