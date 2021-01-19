package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.view.ValueGeneratorNormalDistributionDialog;


public class ValueGeneratorNormalDistribution extends ValueGenerator implements IValueGenerator {

	private String mean;
	private String stdDeviation;
	private String rangeMin;
	private String rangeMax;
	private List<String> generatedValue;


	public ValueGeneratorNormalDistribution(String attributeType) {
		super(attributeType);
	}

	@Override
	public void openDialog(AbstractPackageViewer diagram) {
		setDiagram(diagram);
		if(getFitsType(getAttributeType())){
			ValueGeneratorNormalDistributionDialog dlg = new ValueGeneratorNormalDistributionDialog(this);
			dlg.showAndWait();
		}
	}

	public List<String> getParameter() {
		List<String> parameter = new ArrayList<>();
		parameter.add(this.mean);
		parameter.add(this.stdDeviation);
		parameter.add(this.rangeMin);
		parameter.add(this.rangeMax);

		return parameter;
	}

	@Override
	public void setParameter(List<String> param) {
		if(getAttributeType().equals("Integer")){
			this.mean = integerConverter(param.get(0)) ;
			this.stdDeviation = integerConverter(param.get(1));
			this.rangeMin = integerConverter(param.get(2));
			this.rangeMax = integerConverter(param.get(3));
		} else if (getAttributeType().equals("Float")){
			this.mean = floatConverter(param.get(0)) ;
			this.stdDeviation = floatConverter(param.get(1));
			this.rangeMin = floatConverter(param.get(2));
			this.rangeMax = floatConverter(param.get(3));
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
		for(String param: getParameter()){
			if(param==null){
				return getValueGeneratorName()+" (incomplete)";
			}
		}
		return getValueGeneratorName();
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
