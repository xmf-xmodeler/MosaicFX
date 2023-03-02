package tool.clients.fmmlxdiagrams.dialogs;

import java.awt.event.ActionListener;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class AddConstraintDialog extends Dialog<AddConstraintDialog.Result> {

	private Label label1 = new Label();
	private TextField nameField = new TextField();
	private Label label2 = new Label();
	private TextField levelField = new TextField();
	private CodeBoxPair bodyBox;
	private Label label3 = new Label();
	private CodeBoxPair reasonBox;
	private Label label4 = new Label();
	private Label statusLabel = new Label();

	private DialogPane dialogPane;
	
	public AddConstraintDialog(AbstractPackageViewer diagram, FmmlxObject object) {
		setTitle("Add constraint to " + object.getName());
		init(object,diagram);
		nameField.setText("enterConstraintNameHere");
		levelField.setText((object.getLevel() - 1) + "");
		bodyBox.setBodyText("false");
		reasonBox.setBodyText("\"This constraint always fails.\"");
	}

	public AddConstraintDialog(AbstractPackageViewer diagram, FmmlxObject object, Constraint constraint) {
		setTitle("Edit Constraint " + constraint.getName() + " from " + object.getName());
		init(object,diagram);
		nameField.setText(constraint.getName());
		levelField.setText(constraint.getLevel()+"");
		bodyBox.setBodyText(constraint.getBodyRaw());
		reasonBox.setBodyText(constraint.getReasonRaw());
	}
	
	private void init(FmmlxObject object, AbstractPackageViewer diagram) {
		layoutContent(object,diagram);
		addValidator();
		setResizable(true);
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
		
		bodyBox.getBodyScrollPane().setMinHeight(200);
		bodyBox.getBodyScrollPane().setPrefHeight(200);
		bodyBox.getBodyScrollPane().setMaxHeight(750);
		reasonBox.getBodyScrollPane().setMinHeight(100);
		bodyBox.getBodyScrollPane().setPrefHeight(100);
		reasonBox.getBodyScrollPane().setMaxHeight(500);
		
		bodyBox.getErrorTextArea().setMinHeight(40);
		bodyBox.getErrorTextArea().setMaxHeight(80);
		reasonBox.getErrorTextArea().setMinHeight(40);
		reasonBox.getErrorTextArea().setMaxHeight(80);
		
		label1.setText("@Constraint");
		label2.setText("@");
		label3.setText("fail");
		label4.setText("end");
		statusLabel.setText("");
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		HBox hBox1 = new HBox(5,
				label1, nameField, label2, levelField);
		VBox mainBox = new VBox(5, 
				hBox1, 
				bodyBox.getBodyScrollPane(),
				bodyBox.getErrorTextArea(),
				label3,
				reasonBox.getBodyScrollPane(),
				reasonBox.getErrorTextArea(),
				label4,
				statusLabel
				);
		VBox.setVgrow(bodyBox.getBodyScrollPane(), Priority.ALWAYS);
		VBox.setVgrow(reasonBox.getBodyScrollPane(), Priority.ALWAYS);
		dialogPane.setContent(mainBox);
		statusLabel.setTextFill(Color.RED);
				
		addValidator();
	}

	public class Result {

		public final FmmlxObject object;
		public final String constName;
		public final Integer instLevel;
		public final String body;
		public final String reason;

		public Result(FmmlxObject object, String constName, Integer instLevel, String body,
				String reason) {
			this.object = object;
			this.constName = constName;
			this.instLevel = instLevel;
			this.body = body;
			this.reason = reason;
		}
	}
	
	private void addValidator() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}
}
