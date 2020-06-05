package tool.xmodeler;

import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ControlCenter extends Stage {

	public ControlCenter() {
		setTitle("XModeler ML Control Center");
		
		MenuBar menuBar = new MenuBar();
		BorderPane border = new BorderPane();
		HBox hBox = new HBox(menuBar);
		HBox.setHgrow(menuBar, Priority.ALWAYS);
//		GridPane grid = new GridPane();
		border.setTop(hBox);

		Scene scene = new Scene(border, 800, 300);

		setScene(scene);
	}
	
	public Stage getStageForConsole() {
		return new Stage();
	}
	
}
