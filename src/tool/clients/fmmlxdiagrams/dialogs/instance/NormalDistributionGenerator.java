package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorNormalDistributionDialogResult;


public class NormalDistributionGenerator implements ValueGenerator{

	private List<String> value;
	private final String type;
	
	public NormalDistributionGenerator(String type) {
		super();
		this.type = type;
	}

	public List<String> getValue() {
		return value;
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
		if (value != null){
			AttributeGeneratorNormalDistributionDialog dlg = new AttributeGeneratorNormalDistributionDialog(getName(), type, value);
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
			setValue(result.getAttributeType(), result.getMeanValue(), result.getStdDevValue(), result.getRangeMinValue(), result.getRangeMaxValue());
		}
	}

	public void setValue(String attributeType, String mean, String std, String min, String max) {
		this.value = new ArrayList<>();
		value.add(mean);
		value.add(std);
		value.add(min);
		value.add(max);
	}

	@Override
	public String generate() {
		return generateValue(type, value.get(0), value.get(1), Long.parseLong(value.get(2)), Long.parseLong(value.get(3)));
	}

	public String generateValue(String attributeType, String mean, String stdDeviation, long rangeMin, long rangeMax){
		Random random = new Random();
		if(attributeType.equals("Integer")){
			int meanInt = Integer.parseInt(mean);
			int stdDevInt = Integer.parseInt(stdDeviation);
			while(true){
				long nextGauss = Math.round((random.nextGaussian()*stdDevInt)+meanInt);
				if(nextGauss<=rangeMax && nextGauss>=rangeMin){
					return nextGauss+"";
				}
			}
		} else if (attributeType.equals("Float")){
			float meanFloat = Float.parseFloat(mean);
			float stdDevFloat = Float.parseFloat(stdDeviation);
			while(true){
				double nextGauss = (random.nextGaussian()*stdDevFloat)+meanFloat;
				if(nextGauss<=rangeMax && nextGauss>=rangeMin){
					return nextGauss+"";
				}
			}
		}
		return "";
	}

	@Override
	public int possibleGeneratedValue() {
		return 0;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
	}

	@Override
	public String getName2() {
		if(value==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

}
