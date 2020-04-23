package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.TimeOutException;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.dialog.ValueGeneratorListDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.dialogresult.ValueGeneratorListDialogResult;

public class ValueGeneratorList implements ValueGenerator{

	private FmmlxDiagram diagram;
	private final String attributeType;
	private List<String> parameter;
	private List<String> fetchedList;
	private List<String> generatedValue;


	public ValueGeneratorList(String attributeType) {
		super();
		this.attributeType = attributeType;
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.LIST;
	}

	@Override
	public void openDialog(FmmlxDiagram diagram) {
		this.diagram = diagram;
		if(getFitsType(getAttributeType())){
			ValueGeneratorListDialog dlg = new ValueGeneratorListDialog(this);
			Optional<ValueGeneratorListDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				ValueGeneratorListDialogResult result = opt.get();
				setParameter(result.getParameter());
				setGeneratedValue(result.getElements());
			}
		}
	}

	@Override
	public void generate(int numberOfInstance) {
		//TODO
	}

	@Override
	public int possibleGeneratedInstance() {
		return generatedValue.size();
	}

	@Override
	public boolean getFitsType(String type) {
		if(StringValue.TraditionalDataType.INTEGER.equals(type)) return true;
		if(StringValue.TraditionalDataType.FLOAT.equals(type)) return true;
		if(StringValue.TraditionalDataType.BOOLEAN.equals(type)) return true;
		return StringValue.TraditionalDataType.STRING.equals(type);
	}

	private String integerConverter(String value) {
		try {
			return Integer.parseInt(value)+"";
		} catch (Exception e){
			return Math.round(Float.parseFloat(value))+"";
		}
	}

	private String floatConverter(String value) {
		try {
			return Float.parseFloat(value)+"";
		} catch (Exception e){
			return (float)Integer.parseInt(value)+"";
		}
	}

	private String booleanConverter(String value) {
		try {
			return Boolean.parseBoolean(value)+"";
		} catch (Exception e){
			return "";
		}
	}

	public void setGeneratedValue(List<String> elements) {
		this.generatedValue = new ArrayList<>();
		switch (getAttributeType()) {
			case StringValue.TraditionalDataType.INTEGER:
				for (String value : elements) {
					this.generatedValue.add(integerConverter(value));
				}
				break;
			case StringValue.TraditionalDataType.FLOAT:
				for (String value : elements) {
					this.generatedValue.add(floatConverter(value));
				}
				break;
			case StringValue.TraditionalDataType.BOOLEAN:
				for (String value : elements) {
					this.generatedValue.add(booleanConverter(value));
				}
				break;
			case StringValue.TraditionalDataType.STRING:
				this.generatedValue.addAll(elements);
				break;
		}
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	@Override
	public String getName2() {
		if(this.parameter==null) {
			return getValueGeneratorName()+" (incomplete)";
		}
		return getValueGeneratorName();
	}

	@Override
	public List<String> getParameter() {
		return this.parameter;
	}

	@Override
	public void setParameter(List<String> listName) {
		this.parameter = listName;
	}

	@Override
    public List<String> getGeneratedValue() {
        return this.generatedValue;
    }

	public List<String> getFetchedList() {
		return fetchedList;
	}

	public void fetchList(String listName) {
		try {
			fetchedList = new ArrayList<>();
			fetchedList.addAll(diagram.getComm().evalList(diagram, listName));
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
	}
}