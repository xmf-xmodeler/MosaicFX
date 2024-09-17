package tool.communication.java_to_python;

import java.awt.Desktop;
import java.net.URL;

import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MissingPythonAlert extends Alert {

	public MissingPythonAlert() {
		super(Alert.AlertType.WARNING);

		setTitle("Python is missing");
		setHeaderText("No Python installation found");
		VBox vbox = new VBox();
		Label label = new Label(
				"The function you have called uses Python. Please install Python on your local machine to run this function.\n"
				+ "Restart XModeler after you have installed Python.\n"
				+ "Please make sure to use the user defined installation variant and set the environment variable for Python\n"
				+ "You have to restart your machine after the installation");
		vbox.getChildren().add(label);

		Hyperlink hyperlink = new Hyperlink("Python download page");
		hyperlink.setOnAction(event -> openPythonDownloadPage());
		vbox.getChildren().add(hyperlink);

		getDialogPane().setContent(vbox);
	}

	private static void openPythonDownloadPage() {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URL("https://www.python.org/downloads/").toURI());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}