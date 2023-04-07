package tool.clients.customui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ListView;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.classbrowser.ObjectBrowser;

public class CustomGUIController {
	// Context of custom UI
	Map<String, String> eventToID; // required to reference operations for combination of ID and event
	FXMLLoader loader;
	CustomUI customUI; // Access to UI object, diagram, and customGUI nodes

    private Map<String, Node> objToID = new HashMap<>(); // required to reference all nodes with id in customGUI
	boolean refreshInProgress = false;
	   
	public CustomGUIController(FXMLLoader loader, Map<String, String> eventToID, CustomUI customUI) {
		this.loader = loader;
		this.eventToID = eventToID;
		this.customUI = customUI;
	}  
	   
	@FXML
	public void initialize() {
		// initialize the missing attributes after the loading of the controller is finished
		// determine the id-node-map for access to I/O
		Parent customGUI = this.customUI.getCustomUI();
		   
		// Handle different kinds of custom GUIs recursively
		fillChildren(customGUI);
				   
		// Link listviews to associations and add action listeners to all list views
		for( Node currEl : objToID.values() ) {
			if( currEl instanceof ListView ) {
				// Why not refreshGUI? 
				// When to update? e.g. only after execution of actions / selection in listview?
			   ((ListView) currEl).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> injectGUI());
			    // Update Reference with selected ListView
			    // Wenn ListView neu angelegt wird / aktualisiert wird, prüfe, ob Selektion gesetzt ist ansonsten setze immer eine!
			    //if( mainListView.getSelectionModel().isEmpty() ) {
				//   mainListView.getSelectionModel().select(0);
			    //} 
			}
		}
		   
		// Inject values of the mapping model into the custom UI to complete initialization
		injectGUI();
		   		   
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
		   } else if( TabPane.class.isAssignableFrom( cls ) ) {
			   ObservableList<Tab> tabs = ((TabPane) content).getTabs();
			   for(Tab tab: tabs) {
				   recall = (Parent) tab.getContent();
				   fillChildren(recall);
			   }
			   return; // exit condition on recursive call
		   } else if( TitledPane.class.isAssignableFrom( cls ) ) {
			   recall = (Parent) ((TitledPane) content).getContent();
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
				   if( id != null ) { // id set is required for mapping in UI
					   // this has to be done this way as the Parent class does not allow read access to its children
					   obj = content.lookup("#" + id);
					   // alternative approach also not advised
					   if( obj == null) {
						   Map<String, Object> namespace = loader.getNamespace();
						   obj = (Node) namespace.get(id);
					   }
					   
					   objToID.put(id, obj);
					   
					   // Fetch all methods of the current GUI element and place default event handlers on it
					   // TBD: You might register only those events, which are defined in the attribute "eventToID"
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
	   }
	   
	   public void injectGUI() {	   
		   // Fetch controller mapping objects on level 0
		   String packageOfDiagram = this.customUI.getDiagram().getPackagePath();
		   String rootPath = packageOfDiagram + "::" + "UIElement";
		   FmmlxObject rootObjectMapping = this.customUI.getDiagram().getObjectByPath(rootPath);
		   
		   HashSet<FmmlxObject> subClass = rootObjectMapping.getAllSubclasses();
		   Vector<FmmlxObject> allObjectsL1 = new Vector<FmmlxObject>();
		   for( FmmlxObject inst : subClass ) {
			   allObjectsL1.addAll( inst.getInstances() );
		   }
		   
		   Vector<FmmlxObject> allObjectsL0 = new Vector<FmmlxObject>();
		   for( FmmlxObject inst : allObjectsL1 ) {
			   allObjectsL0.addAll( inst.getInstances() );
		   }
		   
		   // inject values into nodes
		   for( String currID : objToID.keySet() ) {
			   // Distinguish single and multiple values
			   boolean isListView = false;
			   String value = "";
			   
			   // Reflection for UI handling
				   Class<?> currCls = (objToID.get(currID)).getClass();
				   Method[] methods = currCls.getMethods();
				   Node currElem = objToID.get(currID);
				   
				   // Find the mapping objects which comprises the current UI ID
				   for( FmmlxObject mappingObj : allObjectsL0 ) {
					   for( FmmlxSlot currSlot : mappingObj.getAllSlots() ) {
						   if( currSlot.getName().equals("idOfUIElement") ) {
							   if( currSlot.getValue().equals(currID) ) {
								   // Get Value from operation to inject
								   FmmlxSlot isListViewSlot = mappingObj.getSlot("isListView");
								   if( isListViewSlot != null ) {
									   isListView = isListViewSlot.getValue().equals("true") ? true : false;
								   }
								   
								   // Fill proper result value
								   for( FmmlxOperationValue opVal : mappingObj.getAllOperationValues()) {
									   if( opVal.getName().equals("getInjection")){
										   value = opVal.getValue();
									   }
								   }
							   }
						   }
					   }
				   }
				   				  
				   // Inject value via Reflection		 
				   try {								
					   for( Method method : methods ) {
						   if( Modifier.isPublic( method.getModifiers() )
								   && ( ( method.getName().equals("setText") && !isListView  ) )
						   			|| ( method.getName().equals("setItems") && isListView ) ){
							   // Handle list view result
							   if( isListView ) {
								   value = value.replace("Seq{", "");
								   value = value.replace("}", "");
								   ObservableList<String> objList = FXCollections.observableArrayList(Arrays.asList( value.split(",")));
								   method.invoke(currElem, objList );
							   } else {
								   method.invoke(currElem, value );
							   }
									
						   }   
					   }
					} catch( Exception e ) {
						e.printStackTrace();
					}
			   }
	   }
	   	   
	   // This method allows the execution of operations which are attached to the gui
	   private void execCustomAction(Event event) {
		   // Determine the associated name of the operation for current id
		   String id = ((Node) event.getSource()).getId();
		   if( !id.equals("") ){
			   String lookupId = id + "/" + event.getEventType().getName(); // get id of current action
			   String eventID = eventToID.get(lookupId); // determine name of operation
			   if( eventID != null ){	
				   try {
				   // First step TBD: Synchronize parameters to Model Mapping
				   // Then they will be retrieved for operation execution!
				   // Parameters are all objects from the mapping, which have a UI id, but do not have a model element name
				   // Selected instance within listview as parameter?
				   // Distinction between text fields and list views?
				   
				   
				   // obtain user input from the gui for parametrized ids
//				   for( String currId : params) {
//					   Node el = customGUI.lookup("#" + currId);
//					   if( el instanceof ListView ) {
//						   String currParam = ((ListView) el).getSelectionModel().getSelectedItem().toString();
//						   obtParams.add( currParam );
//					   }
//					   
//					   if( el instanceof TextField ) {
//						   String currParam = ((TextField) el).getText();
//						   obtParams.add( currParam );
//					   }
//				   }
					   
					// manipulate value for dates into corresponding XMF expression
					//if( id.toLowerCase().contains("date") ) {
					//	  if( value.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
					//		  value = "Auxiliary::Date::createDate(" + value.split("\\.")[2] + "," + value.split("\\.")[1] + "," + value.split("\\.")[0] + ")";
					//	  }
					//}
				   
				   // Second step TBD: Trigger execution of action
				   // Determine correct instance of "Action" and trigger operation "runAction"
				   // Correct requires a correspondence of UI ID, eventID
				   
				   // Update first diagram (due to possible changes) and afterwards the GUI
				   // diagram.updateDiagram( e -> { refreshGUI("OLD","NEW"); } );
					   
				   // Note: The current implementation of the GUI is only supporting expressions as input.
				   // (Except for date..)
					   
				   } catch( Exception e ) {
					   e.printStackTrace();
				   }
			   }
		   }
	   }	 
}