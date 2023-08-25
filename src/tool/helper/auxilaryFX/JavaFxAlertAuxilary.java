package tool.helper.auxilaryFX;

import javafx.scene.control.Alert;

public class JavaFxAlertAuxilary extends Alert{

	public JavaFxAlertAuxilary(AlertType type, String headerText, String contetnText) {
		super(type);
		setHeaderText(headerText);
		setContentText(contetnText);
	}

}
