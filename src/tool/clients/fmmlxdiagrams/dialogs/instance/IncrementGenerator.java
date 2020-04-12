package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorIncrementDialogResult;

public class IncrementGenerator<T> implements ValueGenerator{
	
	private T startValue;
	private T endValue;
	private T inc;
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
		
		if (startValue != null && endValue!=null && inc!=null) {
			List<T> values = new ArrayList<T>();
			values.add(startValue);
			values.add(endValue);
			values.add(inc);
			AttributeGeneratorIncrementDialog dlg = new AttributeGeneratorIncrementDialog(InstanceGeneratorGenerateType.INCREMENT, type, values );
			dialogResult(dlg);
			
		} else {
			AttributeGeneratorIncrementDialog dlg = new AttributeGeneratorIncrementDialog(InstanceGeneratorGenerateType.INCREMENT, type);
			dialogResult(dlg);
		}
	}

	@SuppressWarnings("unchecked")
	private void dialogResult(AttributeGeneratorIncrementDialog dlg) {
		Optional<AttributeGeneratorIncrementDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			AttributeGeneratorIncrementDialogResult result = opt.get();				
			this.startValue = (T) result.getValueStart();
			this.endValue = (T) result.getValueEnd();
			this.inc = (T) result.getIncrement();
			
		}
	}

	@Override
	public String generate() {
		String allvalue= "start : "+startValue+", "+"endValue : "+endValue+", "+"increment : "+inc;
		String result = InstanceGeneratorGenerateType.INCREMENT.toString()+" : "+" ( "+ allvalue +" ) ";
		return result;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		return false;
	}

	



	public String getType() {
		return type;
	}

	@Override
	public String getName2() {
		if(startValue==null || endValue==null || inc==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}
	

}
