package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorNormalDistributionDialogResult;


public class NormalDistributionGenerator<T> implements ValueGenerator{

	private List<T> value;
	private String type;
	
	
	public NormalDistributionGenerator(String type) {
		super();
		this.type = type;
	}
	
	public NormalDistributionGenerator(List<T> value, String type) {
		super();
		this.value = value;
		this.type = type;
	}
	

	public List<T> getValue() {
		return value;
	}


	public void setValue(List<T> value) {
		this.value = value;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String getName() {
		return "Normal-Distribution";
	}

	@Override
	public void openDialog() {
		AttributeGeneratorNormalDistributionDialog dlg = new AttributeGeneratorNormalDistributionDialog(InstanceGeneratorGenerateType.NORMALDISTRIBUTION, type);
		Optional<AttributeGeneratorNormalDistributionDialogResult> opt = dlg.showAndWait();
	}

	@Override
	public String generate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		return false;
	}

	@Override
	public String getName2() {
		if(value==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

}
