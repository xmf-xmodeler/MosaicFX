package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.Optional;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.dialogs.results.AttributeGeneratorDialogResult;

public class ListGenerator<T> implements ValueGenerator{
	
	private Vector<T> elements;
	private String type;


	public ListGenerator(String string) {
		super();
		this.type= string;
	}

	public ListGenerator(Vector<T> elements, String string) {
		super();
		this.elements = elements;
		this.type = string;
	}

	@Override
	public String getName() {
		return "List";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void openDialog() {
		AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(InstanceGeneratorGenerateType.LIST, type);
		Optional<AttributeGeneratorDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			AttributeGeneratorDialogResult result = opt.get();
			if (type.equals("Integer")) {
				this.elements = (Vector<T>) result.getIntValues();
			} else if (type.equals("Float")) {
				this.elements = (Vector<T>) result.getFloatValues();
			} else if (type.equals("Boolean")) {
				this.elements = (Vector<T>) result.getBoolValues();		
			} else if (type.equals("String")) {
				this.elements  = (Vector<T>) result.getStringValues();
			}
		}
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
		if("Boolean".equals(type)) return true;
		if("String".equals(type)) return true;
		return false;
	}

	public Vector<T> getElements() {
		return elements;
	}

	public void setElements(Vector<T> elements) {
		this.elements = elements;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getName2() {
		if(elements==null) {
			return getName()+" (incomplete)";
		}
		return getName();
	}

}