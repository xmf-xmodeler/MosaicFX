package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.TimeOutException;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.instancegenerator.view.ValueGeneratorListDialog;

public class ValueGeneratorList extends ValueGenerator implements IValueGenerator {

	private List<String> parameter;
	private List<String> generatedValue;

	public ValueGeneratorList(String attributeType) {
		super(attributeType);
	}

	@Override
	public String getValueGeneratorName() {
		return StringValue.ValueGeneratorName.LIST;
	}

	@Override
	public void openDialog(AbstractPackageViewer diagram) {
		setDiagram(diagram);
		if(getFitsType(getAttributeType())){
			ValueGeneratorListDialog dlg = new ValueGeneratorListDialog(this);
			dlg.showAndWait();
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

	public List<String> getGeneratedValue() {
		return generatedValue;
	}

	public void fetchList(String listName) {
		try {
			setGeneratedValue(getDiagram().getComm().evalList(getDiagram(), listName));
		} catch (TimeOutException e) {
			e.printStackTrace();
		}
	}
}