package tool.clients.fmmlxdiagrams.instancegenerator.helper;

import javafx.scene.control.Button;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class InstanceGeneratorEditButton extends Button {

	private FmmlxAttribute attribute;
	
	public InstanceGeneratorEditButton(String string, FmmlxAttribute att) {
		super(string);
		this.attribute = att;
	}
	public FmmlxAttribute getAttribute() {
		return attribute;
	}

}
