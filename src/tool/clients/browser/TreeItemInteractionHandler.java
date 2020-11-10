package tool.clients.browser;


import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
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
	
	private Thread showEditThread = null;
	private boolean interrupted = false;
	private ContextMenu	currentContextMenu = null;
	
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
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
        		this.setOnMouseClicked((MouseEvent me)->{
        			if(me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 1 && justSelected && me.isControlDown()){ //enough for command of MAC?
        				//First Click
        				if (currentContextMenu != null) {
        					currentContextMenu.hide();	
        				}
        				System.err.println("TreeItemInteractionhandler: " +  this);
        				System.err.println("TreeItem: " +  this.getTreeItem());
        				System.err.println("X: " +  me.getX());
        				System.err.println("Y: " +  me.getY());
    					currentContextMenu = MenuClient.popup(
    						ModelBrowserClient.theClient().itemId(this.getTreeItem()), 
    						this, 
    						(new Double(me.getX()).intValue()), 
    						(new Double(me.getY()).intValue()));
        			}else
        			if(me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 1 && !justSelected){
        				//Second Click
        				interrupted = false;
        				showEditThread = new Thread(() -> {try {
							Thread.sleep(250); //time waiting for double click
							
							if (!interrupted)
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									showEditView();
								}
							}) ;
									
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}});
        				showEditThread.start();
        				
        				
        				
        			}else 
        			if (me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 2){
        				//Double Click
        				if (showEditThread != null) {
        					interrupted = true;
        					showEditThread = null;
        				}
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
        			if (currentContextMenu != null) {
    					currentContextMenu.hide();
        			}
        			
    					currentContextMenu = MenuClient.popup(
    							ModelBrowserClient.theClient().itemId(this.getTreeItem()), 
    							this.getTreeItem().getGraphic().getParent(), 
        						(new Double(e.getX()).intValue()), 
        						(new Double(e.getY()).intValue()));
    				   				
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
