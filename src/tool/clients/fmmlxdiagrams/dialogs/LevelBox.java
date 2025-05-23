package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import tool.clients.fmmlxdiagrams.Level;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;

public class LevelBox extends HBox {
	final TextField levelTextField = new TextField();
	final private Button levelButton =JavaFxButtonAuxilary.createButtonWithPicture("", this::showExtendedDialog, "resources/gif/img/about.gif");
	private LevelChangedListener levelChangedListener;
	
	public LevelBox() {this(null);}
	
	public LevelBox(Level oldlevel) {
		super(3);
		getChildren().add(levelTextField);
		getChildren().add(levelButton);
		levelTextField.setEditable(true);
		//levelButton.setOnAction(e->showExtendedDialog());
		if(oldlevel != null) levelTextField.setText(oldlevel.toString());
		HBox.setHgrow(levelTextField, Priority.ALWAYS);
		levelTextField.setMaxWidth(Double.POSITIVE_INFINITY);
		levelTextField.setOnKeyReleased((e) -> {
			if (levelChangedListener != null) {
				levelChangedListener.run(getLevel());
			}
		});
	}

	public void setLevel(Level l) {
		levelTextField.setText(l.toString());
	}
	
	public Level getLevel() {
		try{
			return Level.parseLevel(levelTextField.getText());
		} catch (Level.UnparseableException upe) {
//			upe.printStackTrace();
			return null;
		}
	}

	private void showExtendedDialog(ActionEvent event) {
		Alert alert = 
		new Alert(AlertType.INFORMATION, 
			"This dialog for configuring contingent-level classes has not been implmented yet. "
			+ " The (contingent) level of a class can be entered using the following conventions:\n"
			+ "n for any fixed level class\n"
			+ "n-m for a contingent level class on levels n to m\n"
			+ "n-? for a contingent level class on levels n or above", 
		ButtonType.OK);
		alert.setResizable(true);
		alert.showAndWait();
	}
	
	public void setLevelListener(LevelChangedListener e){
		this.levelChangedListener = e;
	}
	
	public interface LevelChangedListener
	{
		public void run(Level level); 
	}
}