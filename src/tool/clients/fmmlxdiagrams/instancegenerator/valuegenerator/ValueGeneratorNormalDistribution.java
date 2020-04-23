package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorNormalDistributionDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorNormalDistributionDialogResult;


public class ValueGeneratorNormalDistribution implements ValueGenerator{

	private List<String> parameter;
	private final String attributeType;
	private List<String> generatedValue;
	private FmmlxDiagram diagram;

	
	public ValueGeneratorNormalDistribution(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	@Override
	public void openDialog(FmmlxDiagram diagram) {
		this.diagram = diagram;
		if(getFitsType(getAttributeType())){
			ValueGeneratorNormalDistributionDialog dlg = new ValueGeneratorNormalDistributionDialog(this);
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
		return this.parameter;
	}

	@Override
	public void setParameter(List<String> param) {
		this.parameter = new ArrayList<>();
		if(getAttributeType().equals("Integer")){
			this.parameter.add(integerConverter(param.get(0)));
			this.parameter.add(integerConverter(param.get(1)));
			this.parameter.add(integerConverter(param.get(2)));
			this.parameter.add(integerConverter(param.get(3)));
		} else if (getAttributeType().equals("Float")){
			this.parameter.add(floatConverter(param.get(0)));
			this.parameter.add(floatConverter(param.get(1)));
			this.parameter.add(floatConverter(param.get(2)));
			this.parameter.add(floatConverter(param.get(3)));
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		this.generatedValue = new ArrayList<>();

		for (int i =0 ; i < numberOfInstance ; i++){
			this.generatedValue.add(generateValue(getAttributeType(), getParameter().get(0), getParameter().get(1),
					getParameter().get(2), getParameter().get(3)));
		}
	}

	public String generateValue(String attributeType, String mean, String stdDeviation, String rangeMin, String rangeMax){
		Random random = new Random();
		if(attributeType.equals(StringValue.TraditionalDataType.INTEGER)){
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
		} else if (attributeType.equals(StringValue.TraditionalDataType.FLOAT)){
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
	public boolean getFitsType(String type) {
		if("Integer".equals(type)) return true;
		return "Float".equals(type);
	}

	@Override
	public String getName2() {
		if(this.parameter ==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.NORMALDISTRIBUTION;
	}


	@Override
	public List<String> getGeneratedValue() {
		return this.generatedValue;
	}
}
