package tool.clients.forms;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FormTextField extends TextField {
	
	Label label;
	
	public FormTextField(String labelText) {
		label = new Label(labelText);
	}
	
	public Label getLabel() {
		return label;
	}
}
