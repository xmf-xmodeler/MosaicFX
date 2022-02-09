package tool.clients.fmmlxdiagrams.classbrowser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class CustomGUIController {
	   // These attributes are required to change the diagram/model
	   AbstractPackageViewer diagram;
	   FmmlxObject metaClass;
	   FmmlxObject object;
	   
	   // Access to the surrounding object browser and 
	   // the elements of the custom gui
	   ObjectBrowser parent;
	   FXMLLoader loader;
	   Parent customGUI;
	   private Map<String, Node> objToID = new HashMap<>(); // required to reference all children with id
	   
	   public CustomGUIController(AbstractPackageViewer diagram, FmmlxObject metaClass, FXMLLoader loader, FmmlxObject object , ObjectBrowser parent) {
		   this.diagram = diagram;
		   this.metaClass = metaClass;
		   this.loader = loader;
		   this.object = object;
		   this.parent = parent;
	   }
	   
	   @FXML
	   public void initialize() {
		   // initialize the missing attributes after the loading of the controller is finished
		   // determine the id-node-map for access to I/O
		   this.customGUI = (Parent) loader.getRoot();
		   		   
		   // Handle different kinds of custom GUIs recursively
		   fillChildren(customGUI);
		   
		   // now inject the input values for the custom gui based on the slots of the current instance
		   setObject(this.object);
		   
		   // Output information
		   System.err.println("Load finished!");
	   }
	   
	   private void fillChildren(Parent content) {
		   ObservableList<Node> children;
		   Parent recall;
		   
		   // Handle different kind of contents
		   Class<?> cls = content.getClass();
		   if( Pane.class.isAssignableFrom( cls ) ) {
			   children = ((Pane) content).getChildren();
			   // content.getChildrenUnmodifiable(); -> bei Parent?
		   } else if( TabPane.class.isAssignableFrom( cls ) ) {
			   ObservableList<Tab> tabs = ((TabPane) customGUI).getTabs();
			   for(Tab tab: tabs) {
				   recall = (Parent) tab.getContent();
				   fillChildren(recall);
			   }
			   return; // exit condition on recursive call
		   } else if( TitledPane.class.isAssignableFrom( cls ) ) {
			   recall = (Parent) ((TitledPane) customGUI).getContent();
			   fillChildren(recall);
			   return; // exit condition on recursive call
		   } else {
			   System.err.println("Cannot handle the following GUI-Element during loading of CustomGUI: " + cls.getName());
			   return; // error situation
		   }
		   
		   // Create Map-Entries
		   String id;
		   Node obj;
		   for(Node currEl: children) {
			   
			   // Check if currNode is a Pane on its own (recursive call)
			   Class<?> currCls = currEl.getClass();
			   if( Pane.class.isAssignableFrom( currCls ) ) {
				   fillChildren( (Pane) currEl );
			   } else {	   
				   id = currEl.getId();
				   if( id != null ) {
					   // this has to be done this way as the Parent class does not allow read access to its children
					   obj = customGUI.lookup("#" + id);
					   objToID.put(id, obj);
				   }
			   }
		   }
		   
		   // Ähnliche Funktion im Control Center anlegen?
		   // GUIs je Diagramm anlegen + für ein Level
	   }
	   
	   public void setObject(FmmlxObject object) {
		   this.object = object;
		   
		   // also set the slots in the text of the TextFields
		   int i = 1;
		   String baseID = "tc1r"; 
		   for(FmmlxSlot slot: object.getAllSlots()) {
			   try {	  
				 // read the TextField in the current row from the map attribute
				 TextField currTextField = (TextField) objToID.get(baseID + i);
				 currTextField.setText(slot.getValue());
				 
			   } catch( Exception e ) {
				   e.printStackTrace();
			   }
			   i++;
		   }
	   }
	   	  
	  // this is an action, which is called by the buttons in the gui
	  @FXML private void setSlot(ActionEvent event) { 
		  event.consume();
		  
		  // Get ID of triggering object
		  String id = ((Node) event.getSource()).getId()  ;
		  
		  // Determine column and row from the id
		  Integer col = Integer.parseInt( id.substring( (id.indexOf('c'))+1, id.indexOf('r')) );
		  Integer row = Integer.parseInt( id.substring( id.indexOf('r')+1 ));
		  
		  // subtract 1 from col as 0 = label, 1 = text, 2 = button
		  col -= 1;
		  
		  // subtract 1 from row as the java index starts at zero
		  row -= 1;
		  
		  // build new id assuming the text field independent from the source
		  id = "tc" + col + id.substring(id.indexOf('r'));
		  
		  // get value from the textfield by using the map
		  String value = ((TextField) objToID.get(id)).getText();
		  			
		  // Now we need the slot, which is corresponding to the required TextField (so the row no)
		  FmmlxSlot slotToID = object.getAllSlots().get(row);
		  
		  // Note: The current implementation of the GUI is only supporting expressions as input.
		  
		  // send value to XMF, update the diagram and then update the object browser
		  diagram.getComm().changeSlotValue(diagram.getID(), object.getName(), slotToID.getName(), value);;
		  diagram.updateDiagram();
			
		  // wait a few moments until update is commited
		  try {
			  Thread.sleep(100);
		  } catch( Exception e ) {
			  e.printStackTrace();
		  }
			
	      // also update the object list of the parent as it gets outdated otherwise
		  parent.updateObjectList();
		}
}