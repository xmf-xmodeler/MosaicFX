package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.Optional;


import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;

public class IncrementGenerator<T> implements ValueGenerator{
	
	private T min;
	private T max;
	private T step;
	private String type;
	

	public IncrementGenerator(String type) {
		super();
		this.type = type;
	}

	@Override
	public String getName() {
		return "Increment";
	}

	@Override
	public void openDialog() {
		AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(InstanceGeneratorGenerateType.INCREMENT, type );
		Optional<AttributeGeneratorDialogResult> opt = dlg.showAndWait();
		
//		if (opt.isPresent()) {
//			AttributeGeneratorDialogResult result = opt.get();
//			if (type.equals("Integer")) {
//				this.max = (T) result.getValue1Integer();
//				this.min =  (T)result.getValue2Integer();
//				this.step = (T) result.getIncrementInt();
//			} else if (type.equals("Float")) {
//				this.max =  (T)result.getValue1Float();
//				this.min = (T) result.getValue2Float();
//				this.step = (T) result.getIncrementFloat();
//			} 
//			
//		}
	}

	@Override
	public String generate() {
		//TODO
		return "";
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		return false;
	}

	public T getMin() {
		return min;
	}

	public void setMin(T minInt) {
		this.min = minInt;
	}

	public T getMax() {
		return max;
	}

	public void setMax(T maxInt) {
		this.max = maxInt;
	}

	public T getStep() {
		return step;
	}

	public void setStepInt(T stepInt) {
		this.step = stepInt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getName2() {
		if(min==null || max==null || step== null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}
	

}
