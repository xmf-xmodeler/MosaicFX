package tool.clients.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;

public class SelectionDialog extends Dialog<String[]> {

	private ListView<String> listView = null;
	
	public SelectionDialog(String title, String message,boolean multi,  String[] contents, String[] selected ) {
		super();
		
		 final DialogPane dialogPane = getDialogPane();
	        
	     setTitle(title);
	     
	     ObservableList<String> observableContents = FXCollections.observableArrayList(contents); 
	    		 
		 listView = new ListView<String>();
		 listView.setItems(observableContents);
		 if(multi){
			 listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			 for(String s: selected){
				 listView.getSelectionModel().select(s);
			 }
		 }else{
			 if(selected.length > 0) listView.getSelectionModel().select(selected[0]);
		 }
		 
		 Label l = new Label(message);
		 
		 VBox box = new VBox();
		 box.getChildren().addAll(l,listView);
		 
		 dialogPane.setContent(box);
	     dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
	     
	     setResultConverter((dialogButton) -> {
	    	 if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
	        	   //String result = listView.getSelectionModel().getSelectedItems();
	        	   String[] result = new String[0];
	        	   result = listView.getSelectionModel().getSelectedItems().toArray(result);
//	        	   Vector<String> resultVector = new Vector<String>();
//	        	   for(String s: listView.getSelectionModel().getSelectedItems()){
//	        		   resultVector.add(s);
//	        	   }
	        	   return result;
	           }else{
	        	   return null;
	           }
	        });
	}
	
}
