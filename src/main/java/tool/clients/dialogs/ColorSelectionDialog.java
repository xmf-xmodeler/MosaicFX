package tool.clients.dialogs;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;

public class ColorSelectionDialog extends Dialog<Color> {

	private ColorPicker colorPicker = null;
	
	public ColorSelectionDialog(String title,  int red, int green, int blue ) {
		super();
		
		 final DialogPane dialogPane = getDialogPane();
	        
	     setTitle(title);
	     Color c = Color.rgb(red, green, blue);
		 colorPicker = new ColorPicker();
		 colorPicker.setValue(c);
		 
		 dialogPane.setContent(colorPicker);
	     dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
	     
	     setResultConverter((dialogButton) -> {
	           if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
	        	   return colorPicker.getValue();
	           }else{
	        	   return null;
	           }
	        });
	}
	
}
