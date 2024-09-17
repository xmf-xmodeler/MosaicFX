package tool.clients.customui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class CustomGUIController {
	// Context of custom UI
	private Map<String, String> eventToID; // required to reference operations for combination of ID and event
	private FXMLLoader loader;
	private CustomUI customUI; // Access to UI object, diagram, and customGUI nodes

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
				   
		// Add events on list views for updates of selections
		for( Node currEl : objToID.values() ) {
			if( currEl instanceof ListView ) {
				((ListView) currEl).setOnMouseClicked(this::selectNewInstance); // event also covers update of UI / Model
			}
		}
		   
		// Inject values of the mapping model into the custom UI to complete initialization
		injectGUI();
		   		   
		// Output information
		System.err.println("Load of View from file finished!");
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
			   System.err.println("Cannot handle the following UI-Element during loading of CustomUI: " + cls.getName());
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
	
	   public void selectNewInstance(MouseEvent event) {
		   // Called when a new item is selected in a listView
		   // Get source of event
		   ListView source = (ListView) event.getSource();
		   
		   // Get UI-ID + selected Item
		   String currItem = source.getSelectionModel().selectedItemProperty().getValue().toString();	   
		   String id = source.getId();

		   // Call XMF-Operation to handle the update
		   String comm = this.customUI.getDiagram().getPackagePath() + "::" + "ListInjection"; // relevant object
		   comm = comm + "." + "selectNewInstance"; // operation in ControllerMapping, which allows updating references
		   comm = comm + "(" + "\"" + id + "\"" + "," + "\"" + currItem + "\"" + ")"; // pass UI-ID and selected object from listView with string handling
		   // The operation is also triggering recursive updating of the reference list
		   try {
			  this.customUI.getDiagram().getComm().runOperation(this.customUI.getDiagram().getID(), comm);
		   } catch(Exception e ) {
			   e.printStackTrace();
		   }
			   
		   // Yet, the diagram has to be updated first and then the UI
		   this.customUI.getDiagram().updateDiagram( e -> { this.injectGUI(); });
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
								   if( ! value.equals("") ) {
									   method.invoke(currElem, value );
								   }
							   }
									
						   }   
					   }
					} catch( Exception e ) {
						e.printStackTrace();
					}
			   }
		   
		   System.err.println("Injection of Model into View finished!");
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
				   // First step: Synchronize parameters to parameter instances
				   // Once stored in the corresponding slots, they will be considered for operation execution
				   // all parameters are passed as string, in case conversions are required, they will be handled in the XMF
				   // in case of listViews the currently selected element will be used as value
				   
				   // Loop through all nodes and call update of parameter (if existing) on ID based on current value
				   for( String currID : objToID.keySet() ) {
					   Node currEl = objToID.get(currID);
					   String value = "";
					   
					   if( currEl instanceof Button ) {
						   continue;
					   }
					   
					   Boolean isListView = currEl instanceof ListView;
					   try {			
						   if( isListView ) { // Handle listView result
							   if( ((ListView) currEl).getSelectionModel().getSelectedItem() == null ) {
								   ((ListView) currEl).getSelectionModel().selectFirst();
							   }
							   value = (String) ((ListView) currEl).getSelectionModel().getSelectedItem();
						   } else { // Otherwise simply obtain the text by using reflection
							   Class<?> currCls = currEl.getClass();
							   Method[] methods = currCls.getMethods();
							   
							   for( Method method : methods ) {
								   if( Modifier.isPublic( method.getModifiers() )
										   && ( method.getName().equals("getText") 
												   && ( method.getParameterCount() == 0 ) ) ){
										   value = (String) method.invoke(currEl);
										   }
							   }
						   }
						   
						   // Call update of parameter in controller mapping based on value and uiid
						   String comm = this.customUI.getDiagram().getPackagePath() + "::" + "Action"; // relevant object
						   comm = comm + "." + "setParameterValue"; // operation in ControllerMapping, which allows running an operation
						   comm = comm + "(" + "\"" + currID + "\"" + "," + "\"" + value + "\"" + ")"; // pass UI-ID and relevant event type with string handling
						   this.customUI.getDiagram().getComm().runOperation(this.customUI.getDiagram().getID(), comm);

						} catch( Exception e ) {
							e.printStackTrace();
						}    
				   }
				   
				   // outdated: this should be transferred into the model as converter class! bidirectional
				   // also need for representation classes (e.g. prettyStrings)
				   // manipulate value for dates into corresponding XMF expression
				   //if( id.toLowerCase().contains("date") ) {
				   //	  if( value.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
				   //		  value = "Auxiliary::Date::createDate(" + value.split("\\.")[2] + "," + value.split("\\.")[1] + "," + value.split("\\.")[0] + ")";
				   //	  }
				   //}
				   
				   // Second step: Trigger execution of action
				   // Determine correct instance of "Action" and trigger operation "runAction"
				   // Correct requires a correspondence of UI ID, eventID
				   // Call XMF-Operation to handle the operation execution of the callee
				   String opID = event.getEventType().getName();
				   String comm = this.customUI.getDiagram().getPackagePath() + "::" + "Action"; // relevant object
				   comm = comm + "." + "sendMessage"; // operation in ControllerMapping, which allows running an operation
				   comm = comm + "(" + "\"" + id + "\"" + "," + "\"" + opID + "\"" + ")"; // pass UI-ID and relevant event type with string handling
				   try {
					   this.customUI.getDiagram().getComm().runOperation(this.customUI.getDiagram().getID(), comm);
				   } catch(Exception e ) {
					   e.printStackTrace();
				   }
					   
				   // Update first diagram (due to possible changes) and afterwards the GUI
				   this.customUI.getDiagram().updateDiagram( e -> { injectGUI(); } );
					    
				   } catch( Exception e ) {
					   e.printStackTrace();
				   }
			   }
		   }
	   }	 
}