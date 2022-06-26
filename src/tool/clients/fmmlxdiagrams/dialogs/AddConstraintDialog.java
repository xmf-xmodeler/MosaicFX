package tool.clients.fmmlxdiagrams.dialogs;

import java.awt.event.ActionListener;

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
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AddConstraintDialog extends Dialog<AddConstraintDialog.Result> {

	private Label label1 = new Label();
	private TextField nameField = new TextField();
	private Label label2 = new Label();
	private TextField levelField = new TextField();
	//private TextArea bodyBox = new TextArea();
	private CodeBoxPair bodyBox;
	private Label label3 = new Label();
	//private TextArea reasonBox = new TextArea();
	private CodeBoxPair reasonBox;
	private Label label4 = new Label();
	private Label statusLabel = new Label();

	private DialogPane dialogPane;
	private GridPane grid;
	
	public AddConstraintDialog(AbstractPackageViewer diagram, FmmlxObject object) {
		layoutContent(object,diagram);
		setTitle("Add constraint to " + object.getName());
		nameField.setText("enterConstraintNameHere");
		levelField.setText((object.getLevel() - 1) + "");
		bodyBox.setBodyText("false");
		reasonBox.setBodyText("\"This constraint always fails.\"");
		setResultConverter(button -> {
			if (button != null && button.getButtonData() == ButtonData.OK_DONE) {
				return new Result(object, nameField.getText(),
						Integer.parseInt(levelField.getText()),
						bodyBox.getBodyText(),
						reasonBox.getBodyText());
			} else {
				return null;
			}
		});
	}

	public AddConstraintDialog(AbstractPackageViewer diagram, FmmlxObject object, Constraint constraint) {
		layoutContent(object,diagram);
		setTitle("Edit Constraint " + constraint.getName() + " from " + object.getName());
		nameField.setText(constraint.getName());
		levelField.setText(constraint.getLevel()+"");
		bodyBox.setBodyText(constraint.getBodyRaw());
		reasonBox.setBodyText(constraint.getReasonRaw());
		validator();
		setResultConverter(button -> {
			if (button != null && button.getButtonData() == ButtonData.OK_DONE) {
				return new Result(object, nameField.getText(),
						Integer.parseInt(levelField.getText()),
						bodyBox.getBodyText(),
						reasonBox.getBodyText()
						);
			} else {
				return null;
			}
		});
	}
	

	private boolean validateUserInput() {
		statusLabel.setText("");
		try {
			Integer.parseInt(levelField.getText());
		} catch (Exception e) {
			statusLabel.setText("Instantiation level is not an Integer");
			return false;
		}
		return true;
	}
	
	private void layoutContent(FmmlxObject object, AbstractPackageViewer diagram) {
		ActionListener checkActionForSyntax = e -> {
			getDialogPane().lookupButton(ButtonType.OK).setDisable(!bodyBox.getCheckPassed()||!reasonBox.getCheckPassed());
		};
		
		bodyBox = new CodeBoxPair(diagram,checkActionForSyntax, true);
		reasonBox = new CodeBoxPair(diagram,checkActionForSyntax, true);
		
		bodyBox.getBodyScrollPane().setMinHeight(250);
		bodyBox.getBodyScrollPane().setMaxHeight(250);
		reasonBox.getBodyScrollPane().setMinHeight(250);
		reasonBox.getBodyScrollPane().setMaxHeight(250);
		
		bodyBox.getErrorTextArea().setMinHeight(100);
		bodyBox.getErrorTextArea().setMaxHeight(100);
		reasonBox.getErrorTextArea().setMinHeight(100);
		reasonBox.getErrorTextArea().setMaxHeight(100);
		
		
		label1.setText("@Constraint");
		label2.setText("@");
		label3.setText("fail");
		label4.setText("end");
		statusLabel.setText("");
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		grid = new GridPane();
		dialogPane.setContent(grid);
		statusLabel.setTextFill(Color.RED);
		
		
		
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(label1, 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(label2, 2, 0);
		grid.add(levelField, 3, 0);

		grid.add(bodyBox.getBodyScrollPane(), 0, 1, 4, 1);
		grid.add(bodyBox.getErrorTextArea(), 0,2,4,1);
		grid.add(label3, 0, 3, 4, 1);
		grid.add(reasonBox.getBodyScrollPane(), 0, 4, 4, 1);
		grid.add(reasonBox.getErrorTextArea(), 0, 5, 4, 1);
		grid.add(label4, 0, 6, 4, 1);
		grid.add(statusLabel, 0, 7, 4, 1);
		
		validator();
	}

	public class Result {

		public final FmmlxObject object;
		public final String constName;
		public final Integer instLevel;
		public final String body;
		public final String reason;
		public Constraint constraint;

		public Result(FmmlxObject object, String constName, Integer instLevel, String body,
				String reason) {
			this.object = object;
			this.constName = constName;
			this.instLevel = instLevel;
			this.body = body;
			this.reason = reason;
		}
		
//		public Result(FmmlxObject object, String constName, Integer instLevel, String body,
//				String reason, Constraint constraint) {
//			this.object = object;
//			this.constName = constName;
//			this.instLevel = instLevel;
//			this.body = body;
//			this.reason = reason;
//			this.constraint = constraint;
//		}
	}
	
	private void validator() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}
}
