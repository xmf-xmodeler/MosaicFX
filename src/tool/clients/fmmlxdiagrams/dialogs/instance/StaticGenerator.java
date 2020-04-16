package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorStaticDialogResult;

public class StaticGenerator implements ValueGenerator{

	private String value;
	private final String type;

	public StaticGenerator(String type) {
		super();
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return "STATIC";
	}

	@Override
	public void openDialog() {
		if (value!=null) {
			List<String> values = new ArrayList<>();
			values.add(value);
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type, values);
			dialogResult(dlg);
			
		} else {
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type);
			dialogResult(dlg);
		}	
	}
	
	private void dialogResult(AttributeGeneratorStaticDialog dlg) {
		Optional<AttributeGeneratorStaticDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {

			AttributeGeneratorStaticDialogResult result = opt.get();
			switch (type) {
				case "Integer":
					this.value = result.getValueInt().toString();
					break;
				case "Float":
					this.value = result.getValueFloat().toString();
					break;
				case "Boolean":
					this.value = result.getValueBool().toString();
					break;
				case "String":
					this.value = result.getValueString();
					break;
			}
		}
	}

	@Override
	public String generate() {		
		return "STATIC : "+" ( "+value+" ) ";
	}

	@Override
	public int possibleGeneratedValue() {
		return 0;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		return "String".equals(type);
	}

	@Override
	public String getName2() {
		if(value==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}
}
