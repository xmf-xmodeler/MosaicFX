package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class NoteCreationDialog extends CustomDialog<NoteCreationDialog.Result> {
	//TODO TS handle cancel of dialog
	
	 
	private ColorPicker colorPicker; 
	private Result result;

	
	public NoteCreationDialog() {
		super();
		layoutDialog();
		layoutContent();
		result = new Result();
	}

	private void layoutDialog() {
		DialogPane dialog = getDialogPane();
		setTitle("Add Note");
		dialog.setHeaderText("New Note");
		dialog.setContent(flow);
		dialog.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.setOnAction(e -> {
            setResult(result);
            close();
        });
	}

	private void layoutContent() {
		TextArea contentTextField;
		Label contentLabel = new Label("Content:");
		contentTextField = new TextArea();
		contentTextField.setWrapText(true);
		contentTextField.setPrefSize(200, 100);
		contentTextField.setPromptText("Enter note content here.");
		contentTextField.textProperty().addListener((observable, oldValue, newValue) -> result.setContent(newValue));
		
		Label colorLabel = new Label("Choose note color:");
		colorPicker = new ColorPicker();
		colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> result.setColor(newValue));
		
		grid.add(contentLabel, 0, 0);
		GridPane.setValignment(contentLabel, VPos.TOP);
		grid.add(contentTextField, 1, 0);
		grid.add(colorLabel, 0, 1);
		grid.add(colorPicker, 1, 1);
		grid.setHgap(7);
		}
		
	public final class Result {
		private String content;
		private Color color;
		
		public Result() {
			color = colorPicker.getValue();
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public Color getColor() {
			return color;
		}
		public void setColor(Color color) {
			this.color = color;
		} 
	}
}