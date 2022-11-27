package tool.clients.fmmlxdiagrams.graphics.wizard;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class EditLabelDialog extends Dialog<EditLabelDialog.Result> {
	public EditLabelDialog() {
		this("label" + ConcreteSyntaxWizard.getRandomID(), 
				Color.BLACK, Color.TRANSPARENT, Pos.BASELINE_LEFT);
	}
	
	public EditLabelDialog(String id, Color fgColor, Color bgColor, Pos alignment) {
		ColorPicker fgColorPicker = new ColorPicker();
		ColorPicker bgColorPicker = new ColorPicker();
		TextField idField = new TextField();
		ComboBox<Pos> alignmentChooser = new ComboBox<Pos>(FXCollections.observableArrayList(Pos.BASELINE_LEFT, Pos.BASELINE_CENTER, Pos.BASELINE_RIGHT));
		
		idField.setText(id);
		fgColorPicker.setValue(fgColor);
		bgColorPicker.setValue(bgColor);
		alignmentChooser.getSelectionModel().select(alignment);
		
		GridPane pane = new GridPane();
		pane.add(new Label("id"), 0, 0);
		pane.add(new Label("Foreground Color"), 0, 1);
		pane.add(new Label("Background Color"), 0, 2);
		pane.add(new Label("Alignment"), 0, 3);
		
		pane.add(idField, 1, 0);
		pane.add(fgColorPicker, 1, 1);
		pane.add(bgColorPicker, 1, 2);
		pane.add(alignmentChooser, 1, 3);
		
		getDialogPane().setContent(pane);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		
		setResultConverter((dialogButton) -> {
            if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
        	    return new Result(
			    idField.getText(),
			    fgColorPicker.getValue(),
			    bgColorPicker.getValue(),
			    alignmentChooser.getSelectionModel().getSelectedItem());
            } else {
        	    return null;
            }
	    });
	}

    public static class Result{
    	public final String id;
    	public final Color fgColor;
    	public final Color bgColor;
    	public final Pos alignment;
    	
		public Result(String id, Color fgColor, Color bgColor, Pos alignment) {
			super();
			this.id = id;
			this.fgColor = fgColor;
			this.bgColor = bgColor;
			this.alignment = alignment;
		}	    	
    }
}