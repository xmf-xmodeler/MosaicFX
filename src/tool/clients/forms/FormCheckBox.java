package tool.clients.forms;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class FormCheckBox extends CheckBox {

	Label label;

	public FormCheckBox(String labelText) {
		label = new Label(labelText);
	}

	public Label getLabel() {
		return label;
	}
}
