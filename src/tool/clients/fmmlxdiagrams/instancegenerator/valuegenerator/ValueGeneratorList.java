package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorListDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorListDialogResult;

public class ValueGeneratorList<T> implements ValueGenerator{
	
	private List<String> elements;
	private String attributeType;
	private List<String> parameter;
	private List<String> generatedValue;

	public ValueGeneratorList(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	public ValueGeneratorList(List<String> elements, String attributeType) {
		super();
		this.elements = elements;
		this.attributeType = attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void openDialog() {
		ValueGeneratorListDialog dlg = new ValueGeneratorListDialog(getValueGeneratorName(), attributeType);
		Optional<ValueGeneratorListDialogResult> opt = dlg.showAndWait();
		
		if (opt.isPresent()) {
			ValueGeneratorListDialogResult result = opt.get();
			//TODO
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		//TODO
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

	public List<String> getElements() {
		return elements;
	}

	public void setElements(List<String> elements) {
		this.elements = elements;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
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
