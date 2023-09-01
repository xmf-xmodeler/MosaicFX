package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import tool.clients.fmmlxdiagrams.Level;

public class LevelBox extends HBox {
	public final ComboBox<String> levelBox = new ComboBox<>(
			FXCollections.observableArrayList("1", "2", "3", "4", "5"));
	public final Button levelButton = new Button("...");

	public LevelBox() {this(null);}
	
	public LevelBox(Level oldlevel) {
		super(3);
		getChildren().add(levelBox);
		getChildren().add(levelButton);
		levelBox.setEditable(true);
		levelButton.setOnAction(e->showExtendedDialog());
		if(oldlevel != null) levelBox.setValue(oldlevel.toString());
		HBox.setHgrow(levelBox, Priority.ALWAYS);
		levelBox.setMaxWidth(Double.POSITIVE_INFINITY);
	}

	public Level getLevel() {
		try{
			return Level.parseLevel(levelBox.getSelectionModel().getSelectedItem());
		} catch (Level.UnparseableException upe) {
			upe.printStackTrace();
			return null;
		}
	}

	private void showExtendedDialog() {
		new Alert(AlertType.INFORMATION, 
			"This dialog for configuring contingent level classes has not been implmented yet. \n"
			+ "However until then, the level can be entered manually into the level box: \n"
			+ "n for any fixed level class\n"
			+ "n-m for a contingent level class on levels n to m\n"
			+ "n-? for a contingent level class on levels n or above", 
		ButtonType.OK).showAndWait();
	}
}
