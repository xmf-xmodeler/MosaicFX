package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import tool.clients.fmmlxdiagrams.Level;

class LevelBox extends HBox {
	final TextField levelTextField = new TextField();
	private final Button levelButton = new Button("...");
	private LevelChangedListener levelChangedListener;
	
	public LevelBox() {this(null);}
	
	LevelBox(Level oldlevel) {
		super(3);
		getChildren().add(levelTextField);
		getChildren().add(levelButton);
		levelTextField.setEditable(true);
		levelButton.setOnAction(e->showExtendedDialog());
		if(oldlevel != null) levelTextField.setText(oldlevel.toString());
		HBox.setHgrow(levelTextField, Priority.ALWAYS);
		levelTextField.setMaxWidth(Double.POSITIVE_INFINITY);
		levelTextField.setOnKeyTyped((e) -> {
			if (levelChangedListener != null) {
				levelChangedListener.run(getLevel());
			}
		});
	}

	public Level getLevel() {
		try{
			return Level.parseLevel(levelTextField.getText());
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
	
	public void setLevelListener(LevelChangedListener e){
		this.levelChangedListener = e;
	}
	
	public interface LevelChangedListener
	{
		public void run(Level level); 
	}
}