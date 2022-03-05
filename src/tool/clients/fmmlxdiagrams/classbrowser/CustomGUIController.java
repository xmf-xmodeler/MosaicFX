package tool.clients.fmmlxdiagrams.classbrowser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
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
	   Map<String, String> opToEvent; // required to reference operations for combination of ID and event
	   
	   public CustomGUIController(AbstractPackageViewer diagram, FmmlxObject metaClass, FXMLLoader loader, FmmlxObject object , Map<String, String> opToEvent, ObjectBrowser parent) {
		   this.diagram = diagram;
		   this.metaClass = metaClass;
		   this.loader = loader;
		   this.object = object;
		   this.opToEvent = opToEvent;
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
		   
		   // determine possible actions
		   //fillActions(this.loader);
		   
		   // Output information
		   System.err.println("Load finished!");
	   }
	   
//	   private void fillActions(FXMLLoader loader) {
//		   URL xmlFile = (URL) loader.getNamespace().get("location");
//		   String fileName = xmlFile.getFile();
//		   SAXReader reader = new SAXReader();
//		   try {
//			   // Read FXML file
//			   Document document = reader.read(fileName);
//			   
//			   // Get all Nodes with an Id
//			   List<org.dom4j.Node> elements = document.selectNodes("//*[@fx:id]");
//			   for(org.dom4j.Node node : elements ) {
//				   Element element = (Element) node;
//				   
//				   // Get all attributes of nodes with id
//				   Iterator<Attribute> currAttributes = element.attributeIterator();
//				   String id = "";
//				   while( currAttributes.hasNext() ) {
//					   Attribute currAtt = currAttributes.next();
//					   String att = currAtt.getName();
//					   
//					   if(att.equals("id")) {
//						   id = currAtt.getValue();
//					   } else if(att.substring(0, 2).equals("on")) {
//						   // Only event attributes are starting with the prefix on
//						   // Build entry for every event in map
//						   if (!id.equals("")) {
//							   opToEvent.put(id+"/"+currAtt.getName().toUpperCase().substring(2), currAtt.getValue().substring(1));
//						   }
//					   }
//					   
//				   }
//			   }
//
//		   } catch(Exception e) { 
//			   System.err.println("Failure on creating events of custom GUI!");
//		   }
//		   
//	   }
	   
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
					   
					   // Fetch all methods of the current GUI element and place default event handlers on it
					   Method[] methods = currCls.getMethods();
					   for( Method method : methods ) {
						   if( Modifier.isPublic(method.getModifiers()) && method.getName().length() > 5 && method.getName().substring(0, 5).equals("setOn")){
							   EventHandler<?> defaultAction = (e) -> { execCustomAction(e); };
							   try {
								   method.invoke(obj, defaultAction );
							   } catch(Exception e) {
								   e.printStackTrace();
							   }
						   }
					   }
				   }
			   }
		   }
		   
		   // Ähnliche Funktion im Control Center anlegen?
		   // GUIs je Diagramm anlegen + für ein Level
	   }
	   
	   public void injectGUI(FmmlxObject object) {		   
		   // inject values into fields with respective instruction
		   for( String currID : objToID.keySet() ) {
			   if( currID.contains("INJ") ) {
				   String injectValue;

				   // Differenciate INJ, ACTINJ and REF ... INJ				   
				   if( currID.contains("ACTINJ") ) {
					   // inject result of action
					   injectValue = object.getOperationValue(currID.replace("ACTINJ", "")).getValue();
					   
				   } else if( currID.contains("REF") ) {
					   // inject slot of of reference
					   String ref = currID.split("INJ")[0].replace("REF", "");
					   String slot = currID.split("INJ")[1];
					   
					   // grab relevant associated object
					   FmmlxObject refObj = diagram.getObjectByPath(diagram.getPackagePath() + "::" + ref);
					   
					   // grab slot value
					   injectValue = refObj.getSlot(slot).getValue();
					   
				   } else {
					   // grab slot value
					   injectValue = object.getSlot(currID.replace("INJ", "")).getValue();
					   
				   }
				   
				   // setText-Method with grabbed valued
				   Class<?> currCls = (objToID.get(currID)).getClass(); // find a better way for this
				   // tbd: especially allow multiple elements on the same slot!! number?
				   
				   // simply inject value
				   Method[] methods = currCls.getMethods();
				   for( Method method : methods ) {
					   if( Modifier.isPublic(method.getModifiers()) && method.getName().equals("setText")){
						   try {
							   method.invoke(objToID.get(currID), injectValue );
						   } catch(Exception e) {
							   e.printStackTrace();
						   }
					   }
				   }

			   }
		   }
	   }
	   
	   public void setObject(FmmlxObject object) {
		   this.object = object;
		   injectGUI(object);
	   }
	   
	   // This method allows the execution of operations which are attached to the gui
	   private void execCustomAction(Event event) {
		   // Determine the associated name of the operation for current id
		   String id = ((Node) event.getSource()).getId();
		   if( !id.equals("") ){
			   id = id + "/" + event.getEventType().getName(); // get id of current action
			   String op = opToEvent.get(id); // determine name of operation
			   if( op != null ){
				   // Get actions helper from diagram and trigger operation for current object
				   diagram.getActions().runOperation(object.getPath(), op );
				   //tbd: How to handle operations with parameters??
				   
			   }
			   
			   // Use setter if intended
			   // tbd: optimize this with parameter handling!
			   if(id.length() > 2 && id.substring(0, 3).equals("ACT") && event.getEventType().getName().equals("ACTION")) {
				   setSlot((ActionEvent) event);
			   }
			   
			   // also update the object list of the parent as it gets outdated otherwise
			   parent.updateObjectList();
		   }
		   
	   }
	   	  
	  // this is an action, which is called by the buttons in the gui
	  // TBD: Might be removed later as it is only a setter. Therefore the
	  // the implementation of parameters have to be handled properly!
	  // I think there is no need to handle return parameters as well?
	  @FXML private void setSlot(ActionEvent event) { 
		  event.consume();
		  
		  // Get ID of triggering object
		  String id = ((Node) event.getSource()).getId()  ;
		  id = "INJ" + id.substring(3); // id of textfield
		  
		  // get value from the textfield by using the map
		  String value = ((TextField) objToID.get(id)).getText();
		  			
		  // Now we need the slot, which is corresponding to the required TextField (so the row no)
		  id = id.replaceFirst("INJ", ""); 
		  for( FmmlxSlot currSlot : object.getAllSlots() ) {
			   String slotName = currSlot.getName();
			   
			   if(slotName.equals(id)) {
					  // send value to XMF, update the diagram and then update the object browser
					  diagram.getComm().changeSlotValue(diagram.getID(), object.getName(), slotName, value);;
					  diagram.updateDiagram();   
			   }
		  }
		  
		  // Note: The current implementation of the GUI is only supporting expressions as input.
		}
	  		 
}