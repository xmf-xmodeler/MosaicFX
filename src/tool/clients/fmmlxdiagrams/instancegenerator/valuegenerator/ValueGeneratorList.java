package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorListDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorListDialogResult;

public class ValueGeneratorList<T> implements ValueGenerator{
	
	private Vector<T> elements;
	private String type;
	private List<String> parameter;
	private List<String> generatedValue;

	public ValueGeneratorList(String string) {
		super();
		this.type= string;
	}

	public ValueGeneratorList(Vector<T> elements, String string) {
		super();
		this.elements = elements;
		this.type = string;
	}

	@Override
	public String getValueGeneratorName() {
		return "LIST";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void openDialog() {
		ValueGeneratorListDialog dlg = new ValueGeneratorListDialog(getValueGeneratorName(), type);
		Optional<ValueGeneratorListDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			ValueGeneratorListDialogResult result = opt.get();
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
	public List<String> generate(int numberOfInstance) {
		return null;
	}

	@Override
	public int possibleGeneratedInstance() {
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
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(List<String> parameter) {
		//TODO
	}

	@Override
    public List<String> getGeneratedValue() {
        return generatedValue;
    }

}
