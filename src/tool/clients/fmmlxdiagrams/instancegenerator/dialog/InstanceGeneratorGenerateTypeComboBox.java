package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.*;

import static tool.clients.fmmlxdiagrams.instancegenerator.dialog.TypeList.*;

public class InstanceGeneratorGenerateTypeComboBox extends ComboBox<ValueGenerator>{
	
	private FmmlxAttribute attribute;

	public InstanceGeneratorGenerateTypeComboBox(FmmlxAttribute attribute) {
		super(getGenerateTypeList(attribute.getType()));
		this.attribute = attribute;
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(FmmlxAttribute attribute) {
		this.attribute = attribute;
	}


	
}
