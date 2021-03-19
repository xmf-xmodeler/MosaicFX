package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AddConstraintDialog extends Dialog<AddConstraintDialog.AddConstraintDialogResult>{

	private Label label1 = new Label("@Constraint");
	private TextField nameField = new TextField("enterConstraintNameHere");
	private Label label2 = new Label("@");
	private TextField levelField = new TextField("level");
	private TextArea bodyBox = new TextArea("false");
	private Label label3 = new Label("fail");
	private TextArea reasonBox = new TextArea("\"This constraint always fails.\"");
	private Label label4 = new Label("end");
	private Label statusLabel = new Label("");
	
	
	public AddConstraintDialog(AbstractPackageViewer diagram, FmmlxObject object) {
		
		setTitle("Add constraint to " + object.getName());
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		GridPane grid = new GridPane();
		dialogPane.setContent(grid);

		grid.add(label1,     0, 0);
		grid.add(nameField,  1, 0);
		grid.add(label2,     2, 0);
		grid.add(levelField, 3, 0);

		grid.add(bodyBox,     0, 1, 4, 1);
		grid.add(label3,      0, 2, 4, 1);
		grid.add(reasonBox,   0, 3, 4, 1);
		grid.add(label4,      0, 4, 4, 1);
		grid.add(statusLabel, 0, 5, 4, 1);
		
		levelField.setText((object.getLevel()-1)+"");
		statusLabel.setTextFill(Color.RED);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
		
		setResultConverter(button ->{
			if (button != null && button.getButtonData() == ButtonData.OK_DONE) {
				return new AddConstraintDialogResult(
						object, 
						nameField.getText(), 
						Integer.parseInt(levelField.getText()), 
						bodyBox.getText(), reasonBox.getText());
			} else return null;
		});
	}

	private boolean validateUserInput() {
		statusLabel.setText("");
		try{
			Integer.parseInt(levelField.getText());
		} catch (Exception e) {
			statusLabel.setText("Instantiation level is not an Integer");
			return false;
		}
	return true;
}
public class AddConstraintDialogResult {

		public final FmmlxObject object;
		public final String constName;
		public final Integer instLevel;
		public final String body;
		public final String reason;
		
		public AddConstraintDialogResult(FmmlxObject object, String constName, 
				Integer instLevel, String body,	String reason) {
			this.object = object;
			this.constName = constName;
			this.instLevel = instLevel;
			this.body = body;
			this.reason = reason;
		}
	}

}
