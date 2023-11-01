package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Optional;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OptionalTextField extends HBox{
	
	private final CheckBox checkbox;
	private final TextField textField;
	
	public OptionalTextField(String initText, boolean selected) {
		super();
		
		checkbox = new CheckBox("");
		textField = new TextField();
		
		checkbox.setSelected(selected);
		textField.setDisable(!selected);
		
		this.getChildren().add(checkbox);
		this.getChildren().add(textField);
		
		checkbox.selectedProperty().addListener((x0,x1,sel)->{
			textField.setDisable(!sel);
		});
	}
	
	public Optional<String> getText() {
		if(checkbox.isSelected())
			return Optional.of(textField.getText());
		else
			return Optional.empty();
	}
	
	public void setEditable(boolean editable) {
		if(editable) {
			checkbox.setDisable(false);
		} else {
			checkbox.setSelected(false);
			textField.setDisable(true);
			checkbox.setDisable(true);
		}
	}
	
}
