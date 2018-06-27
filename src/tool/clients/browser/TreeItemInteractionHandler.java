package tool.clients.browser;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tool.clients.menus.MenuClient;

public class TreeItemInteractionHandler extends TreeCell<String> {

	private TextField textField;

	private boolean justSelected = false;
	
	public TreeItemInteractionHandler() {
//		MenuItem mi1 = new MenuItem("Message");
//		mi1.setOnAction((ActionEvent t) -> {
//			System.out.println(getItem());
//		});
//		MenuItem mi2 = new MenuItem("Error");
//		mi2.setOnAction((ActionEvent t) -> {
//			System.err.println(getItem());
//		});
//
//		cm = new ContextMenu();
//		cm.getItems().addAll(mi1,mi2);
		if(ModelBrowserClient.font != null) this.setFont(ModelBrowserClient.font);
//		this.getTreeView().requestFocus();
	}

    public void showEditView() {

        if (textField == null) {
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
        textField.requestFocus();
    }

    public void discardChanges() {
        setText(getItem());
        setGraphic(getTreeItem().getGraphic());
    }

    public void commitChanges(String text) {
    	
        if (!getItem().equals(text)) {
        	this.getTreeItem().setValue(text);
            ModelBrowserClient.theClient().sendMessageUpdateText(this.getTreeItem(), text);
        }
        setText(getItem());
        setGraphic(getTreeItem().getGraphic());
    }
    
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            this.setOnMouseClicked(null);
        } else {
//            if (isEditing()) {
//                if (textField != null) {
//                    textField.setText(getString());
//                }
//                setText(null);
//                setGraphic(textField);
//            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
        		this.setOnMouseClicked((MouseEvent me)->{
        			if(me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 1 && justSelected && me.isControlDown()){ //enough for command of MAC?
        				//First Click
        				MenuClient.popup(ModelBrowserClient.theClient().itemId(this.getTreeItem()), this, (new Double(me.getScreenX()).intValue()), (new Double(me.getScreenY()).intValue()));
        			}else
        			if(me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 1 && !justSelected){
        				//Second Click
        				showEditView();
        			}else 
        			if (me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 2){
        				//Double Click
        				ModelBrowserClient.theClient().sendMessageMouseDoubleClick(getTreeItem());
        			}
        			
        			if(justSelected){
        				justSelected = false;
        			}
        		});
        		
        		this.getTreeItem().expandedProperty().addListener((observable, oldValue, newValue)->{
        			if( newValue){
        				ModelBrowserClient.theClient().sendMessageExpanded(getTreeItem());
        			}
        	    });
        		
        		this.selectedProperty().addListener((observable, oldValue,newValue)->{
        			justSelected = true;
        		});
        		this.setOnContextMenuRequested((e)->{
        			MenuClient.popup(ModelBrowserClient.theClient().itemId(this.getTreeItem()), this, (new Double(e.getSceneX()).intValue()), (new Double(e.getSceneY()).intValue()));
        		});
//            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                	this.commitChanges(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    this.cancelEdit();
                }
        });
        textField.focusedProperty().addListener((observable, oldValue,newValue)->{
            	if(oldValue && ! newValue){
            		this.commitChanges(textField.getText());
            	}
        });
    }

    private String getString() {
        if(getItem() == null ){
        	return "";
        }else{
        	return getItem().toString();
        }
    }
	
}
