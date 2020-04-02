package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;

public class StaticGenerator<T> implements ValueGenerator{

	private T value;
	private String type;

	public StaticGenerator(String type) {
		super();
		this.type = type;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
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

	@SuppressWarnings("unchecked")
	@Override
	public void openDialog() {
		AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(InstanceGeneratorGenerateType.STATIC, type);
		Optional<AttributeGeneratorDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			AttributeGeneratorDialogResult result = opt.get();
			if (type.equals("Integer")) {
				this.value = (T) result.getValueInt();
			} else if (type.equals("Float")) {
				this.value = (T) result.getValueFloat();
			} else if (type.equals("Boolean")) {
				this.value = (T) result.getValueBool();		
			} else if (type.equals("String")) {
				this.value = (T) result.getValueString();
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
