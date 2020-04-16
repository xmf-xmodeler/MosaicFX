package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorStaticDialogResult;

public class StaticGenerator implements ValueGenerator{

	private String value;
	private String type;

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
			List<String> values = new ArrayList<String>();
			values.add(value);
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type, values);
			dialogResult((AttributeGeneratorStaticDialog) dlg);
			
		} else {
			AttributeGeneratorStaticDialog dlg = new AttributeGeneratorStaticDialog(getName(), type);
			dialogResult((AttributeGeneratorStaticDialog) dlg);
		}	
	}

	
	private void dialogResult(AttributeGeneratorStaticDialog dlg) {
		Optional<AttributeGeneratorStaticDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {

			AttributeGeneratorStaticDialogResult result = opt.get();
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
