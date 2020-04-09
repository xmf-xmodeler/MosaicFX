package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;

public class StaticGenerator<T> implements ValueGenerator{

	private String value;
	private String type;

	public StaticGenerator(String type) {
		super();
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
		return "Static";
	}

	@Override
	public void openDialog() {
		if (value!=null) {
			List<String> values = new ArrayList<String>();
			values.add(value);
			AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(InstanceGeneratorGenerateType.STATIC, type, values);
			dialogResult(dlg);
			
		} else {
			AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(InstanceGeneratorGenerateType.STATIC, type);
			dialogResult(dlg);
		}	
	}

	
	private void dialogResult(AttributeGeneratorDialog dlg) {
		Optional<AttributeGeneratorDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {

			AttributeGeneratorDialogResult result = opt.get();
			if (type.equals("Integer")) {
				this.value =  result.getValueInt().toString();
			} else if (type.equals("Float")) {
				this.value =  result.getValueFloat().toString();
			} else if (type.equals("Boolean")) {
				this.value =  result.getValueBool().toString();	
			} else if (type.equals("String")) {
				this.value = result.getValueString();
			}
		}
	}

	@Override
	public String generate() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		if("String".equals(type)) return true;
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
