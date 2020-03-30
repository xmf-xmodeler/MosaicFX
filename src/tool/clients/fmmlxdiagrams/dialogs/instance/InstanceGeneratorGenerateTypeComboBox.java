package tool.clients.fmmlxdiagrams.dialogs.instance;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class InstanceGeneratorGenerateTypeComboBox<T> extends ComboBox<T> {
	
	private FmmlxAttribute attribute;

	public InstanceGeneratorGenerateTypeComboBox(ObservableList<T> observableList, FmmlxAttribute attribute) {
		super(observableList);
		this.attribute = attribute;
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}
	
}
