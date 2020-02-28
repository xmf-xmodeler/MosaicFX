package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

@SuppressWarnings("hiding")
public class InstanceGeneratorGenerateTypeComboBox<GenerateType> extends ComboBox<GenerateType> {
	
	private FmmlxAttribute attribute;

	public InstanceGeneratorGenerateTypeComboBox(ObservableList<GenerateType> observableList, FmmlxAttribute attribute) {
		super(observableList);
		this.attribute = attribute;
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}
	
}
