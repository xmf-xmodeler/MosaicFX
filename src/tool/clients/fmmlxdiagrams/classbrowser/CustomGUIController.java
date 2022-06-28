package tool.clients.fmmlxdiagrams.classbrowser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class CustomGUIController {
	   // These attributes are required to change the diagram/model
	   AbstractPackageViewer diagram;
	   FmmlxObject metaClass;
	   String metaClassName;
	   
	   // Access to the surrounding object browser and 
	   // the elements of the custom gui
	   ObjectBrowser parent;
	   FXMLLoader loader;
	   Parent customGUI;
	   private Map<String, Node> objToID = new HashMap<>(); // required to reference all children with id
	   Map<String, String> opToEvent; // required to reference operations for combination of ID and event
	   
	   HashMap<AssociationInstanceMapping, List<String>> linksPerAssociationPerInstance; // required to handle data refreshing recursively
	   HashMap<String, ListView<String>> listViewsForAssociations = new HashMap<>();
	   
	   ListView mainListView;
	   
	   boolean refreshInProgress = false;
	   
	   public CustomGUIController(AbstractPackageViewer diagram, FmmlxObject metaClass, FXMLLoader loader, Map<String, String> opToEvent, HashMap<AssociationInstanceMapping, List<String>> linksPerAssociationPerInstance, ObjectBrowser parent) {
		   this.diagram = diagram;
		   this.metaClass = metaClass; // depending on CREF ?
		   this.loader = loader;
		   this.opToEvent = opToEvent;
		   this.linksPerAssociationPerInstance = linksPerAssociationPerInstance;
		   this.parent = parent;
		   this.metaClassName = metaClass.getName();
	   }
	   
	   @FXML
	   public void initialize() {
		   // initialize the missing attributes after the loading of the controller is finished
		   // determine the id-node-map for access to I/O
		   this.customGUI = (Parent) loader.getRoot();
		   		   
		   // Handle different kinds of custom GUIs recursively
		   fillChildren(customGUI);
		   
		   // TBD: order objToID; therefore -> own class with interface comparable..
		   // CREF has to be first and REFs have to be afterwards in chronological order
		   // The order of the ref depends on their distance to the CREF!
		   
		   // set Main ListView to determine items
		   mainListView = (ListView) objToID.get("CREF" + metaClass);
		   mainListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> refreshGUI(oldValue, newValue));
		   
		   // Link listviews to associations and add action listeners to all list views
		   for( Node currEl : objToID.values() ) {
			   if( currEl instanceof ListView ) {
				   // ignore main reference
				   if( currEl.getId().startsWith("CREF") ){
					   continue;
				   }
				   
				   ((ListView) currEl).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> injectGUI(oldValue, newValue));
				   
				   // Figure out the proper Association
				   // TBD: This will not work if a class has more than one association to the main class
				   // This analysis has to occur recursively as well!!
				   String refName = currEl.getId().split("REF")[1];
				   Vector<FmmlxAssociation> assocs = diagram.getAssociations();
				   for( FmmlxAssociation assoc : assocs ) {
					   if( assoc.getSourceNode().getName().equals(metaClass.getName() ) 
							   && assoc.getTargetNode().getName().equals(refName) ) {
						   // Persist association and listview combination
						   listViewsForAssociations.put(assoc.getName(), (ListView) currEl);
					   }
				   }
			   }
		   }
		   
		   // now inject the input values for the custom gui based on the slots of the current instance
		   refreshGUI("OLD", "NEW");
		   //setObject(this.object);
		   		   
		   // determine possible actions
		   //fillActions(this.loader);
		   
		   // Output information
		   System.err.println("Load finished!");
	   }
	   
	   public void refreshGUI(Object oldValue, Object newValue){
		   // prevent unintended execution of the method
		   if(  oldValue == null || newValue == null || oldValue.equals(newValue) ) {
			   return;
		   }
		   
		   // determine new metaclass reference as it may have been updated
		   metaClass = diagram.getObjectByPath(diagram.getPackagePath() + "::" + metaClassName);
		   
		   // Start by CREF ListView
		   Vector<String> objectsVector = new Vector<>();
		   for( FmmlxObject el : metaClass.getInstances() ) {
			   objectsVector.add(el.getName());
		   }
		   objectsVector.sort(null);
		   ObservableList<String> objectList = FXCollections.observableArrayList(objectsVector);
		   mainListView.setItems( objectList );
		   
		   if( mainListView.getSelectionModel().isEmpty() ) {
			   mainListView.getSelectionModel().select(0);
			   //oldValue = mainListView.getItems().get(0);
		   } 
			   
		   // Inject GUI based on currObj
		   injectGUI("OLD", "NEW");
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
					   // alternative approach also not advised
					   if( obj == null) {
						   Map<String, Object> namespace = loader.getNamespace();
						   obj = (Node) namespace.get(id);
					   }
					   
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
	   }
	   
	   public void injectGUI(Object oldValue, Object newValue) {
		   // prevent unintended execution of the method
		   if(  oldValue == null || newValue == null || oldValue.equals(newValue) ) {
			   return;
		   }
		   
		   // inject values into fields with respective instruction
		   for( String currID : objToID.keySet() ) {
			   // 1.) only REF / CREF
			   if( ! ( currID.contains("ACT") || currID.contains("INJ") ) ) {
			   
				   // TBD: Get rid of ListViews to Associations as parameter.. data should be fetched from the diagram!
				   ControllerLanguageInterpreter2 exec = new ControllerLanguageInterpreter2(currID, diagram, mainListView, linksPerAssociationPerInstance, listViewsForAssociations);
				   
				   Class<?> currCls = (objToID.get(currID)).getClass();
				   Method[] methods = currCls.getMethods();
				   Node currElem = objToID.get(currID);
				   
				   Object res;
				   try {
					   res = exec.fetchResult();
				   } catch( Exception e ) {
					   e.printStackTrace();
					   res = "null";
				   }
				   
				   try {								
					   for( Method method : methods ) {
						   if( Modifier.isPublic( method.getModifiers() )
								   && ( ( method.getName().equals("setText") && ! exec.isResultList() ) )
						   			|| ( method.getName().equals("setItems") && exec.isResultList() ) ){
									method.invoke(currElem, res );
						   }   
					   }
					} catch( Exception e ) {
						e.printStackTrace();
					}
			   }
		   }
		   
		   // 2. everything else
		   for( String currID : objToID.keySet() ) {
			   if( currID.contains("ACTINJ") || currID.contains("INJ") ) {
			   
				   // TBD: Get rid of ListViews to Associations as parameter.. data should be fetched from the diagram!
				   ControllerLanguageInterpreter2 exec = new ControllerLanguageInterpreter2(currID, diagram, mainListView, linksPerAssociationPerInstance, listViewsForAssociations);
				   
				   Class<?> currCls = (objToID.get(currID)).getClass();
				   Method[] methods = currCls.getMethods();
				   Node currElem = objToID.get(currID);
				   
				   Object res;
				   try {
					   res = exec.fetchResult();
				   } catch( Exception e ) {
					   e.printStackTrace();
					   res = "null";
				   }
				   
				   try {								
					   for( Method method : methods ) {
						   if( Modifier.isPublic( method.getModifiers() )
								   && ( ( method.getName().equals("setText") && ! exec.isResultList() ) )
						   			|| ( method.getName().equals("setItems") && exec.isResultList() ) ){
									method.invoke(currElem, res );
						   }   
					   }
					} catch( Exception e ) {
						e.printStackTrace();
					}
			   }
		   }
		   
	   }
	   
	   public void updateClassReferences(Object newValue) {
		   	// get selected instance
			FmmlxObject currObj = diagram.getObjectByPath(diagram.getPackagePath() + "::" + ((FmmlxObject) newValue).getName());
			
			// updated all class references with the slot value of the selected instance
			for( FmmlxSlot slot: currObj.getAllSlots()) {
				String id = "CREF" + currObj.getMetaClassName();
				id += "INJ" + slot.getName();
				
				Node text = customGUI.lookup("#" + id);					
				((TextField) text).setText(slot.getValue());
			}
	   }
	   
	   // This method allows the execution of operations which are attached to the gui
	   private void execCustomAction(Event event) {
		   // Determine the associated name of the operation for current id
		   String id = ((Node) event.getSource()).getId();
		   if( !id.equals("") ){
			   String lookupId = id + "/" + event.getEventType().getName(); // get id of current action
			   String op = opToEvent.get(lookupId); // determine name of operation
			   if( op != null ){				   
				   ControllerLanguageInterpreter2 exec = new ControllerLanguageInterpreter2(id, diagram, mainListView, linksPerAssociationPerInstance, listViewsForAssociations);
				   
				   
				   
				   try {
					   // Call with parameters
					   // "USE" as Part of the Operation which references the GUI-elements
					   // which contents shall be used as parameters in the method.
					   // Each parameter is separated by another USE.   
					   Vector<String> params = new Vector<>( Arrays.asList(op.split("USE") ));
					   params.remove(0);
					   
					   Vector<String> obtParams = new Vector<>();
					   // obtain user input from the gui for parametrized ids
					   for( String currId : params) {
						   Node el = customGUI.lookup("#" + currId);
						   if( el instanceof ListView ) {
							   String currParam = ((ListView) el).getSelectionModel().getSelectedItem().toString();
							   obtParams.add( currParam );
						   }
						   
						   if( el instanceof TextField ) {
							   String currParam = ((TextField) el).getText();
							   obtParams.add( currParam );
						   }
					   }
					   
					  
					   // maybe a setParameters operation?? and another instance attribute?
					   // no conflicts with other calls!
					   
					   String[] obtParameters = new String[obtParams.size()];
					   System.arraycopy(obtParams.toArray(), 0, obtParameters, 0, obtParams.size());
					   Object res = exec.fetchResult(obtParameters); 
					   
					   // Update first diagram (due to possible changes) and afterwards the GUI
					   diagram.updateDiagram( e -> { refreshGUI("OLD","NEW"); } );
					   
				   } catch( Exception e ) {
					   e.printStackTrace();
				   }
			   }
			   
			   // Use setter if intended
			   // tbd: optimize this with parameter handling!
			   if(id.length() > 2 && id.contains("ACT") && event.getEventType().getName().equals("ACTION")) {
				   if( op == null || op.contains("setSlot") ) {
					   setSlot((ActionEvent) event);
					   
					   // Update first diagram (due to possible changes) and afterwards the GUI
					   diagram.updateDiagram( e -> { refreshGUI("OLD","NEW"); } );
					   
					   // wait some time until the diagram has updated
					   while( diagram.isUpdating() ) {		   
					   }
					   
					   refreshGUI("OLD","NEW");
				   }
			   }
			   

		   }
		   
	   }
	   	  
	  // this is an action, which is called by the buttons in the gui
	  // TBD: Might be removed later as it is only a setter. Therefore the
	  // the implementation of parameters have to be handled properly!
	  // I think there is no need to handle return parameters as well?
	  @FXML private void setSlot(ActionEvent event) { 
		  event.consume();
		  
		  // Get ID of triggering object
		  String id = ((Node) event.getSource()).getId();
		  
		  id = id.replace("ACTset", "INJ"); // injection value for setter (requires corresponding textField on GUI!)
		  
		  // get value from the textfield by using the map
		  String value;
		  if( objToID.containsKey(id) ) {
			  value = ((TextField) objToID.get(id)).getText();
		  } else {
			  // manipulateID due to setter manipulation
			  int toUpper = id.indexOf("INJ")+3; // generated setters use capital letters!
			  String newChar = "" + id.charAt(toUpper);
			  newChar = newChar.toLowerCase();
			  id = id.substring(0, toUpper) + newChar + id.substring(toUpper+1);
			  value = ((TextField) objToID.get(id)).getText();
		  }
		  
		  // manipulate value for dates into corresponding XMF expression
		  if( id.toLowerCase().contains("date") ) {
			  if( value.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
				  value = "Auxiliary::Date::createDate(" + value.split("\\.")[2] + "," + value.split("\\.")[1] + "," + value.split("\\.")[0] + ")";
			  }
		  }
		  
		  // Determine main object
		  // TBD: More flexible depending on REF of ACT
		  // TBD: Call actual setter as soon parameters are supported!
		  // Will make this operation obsolete!
		  // Still it shall be kept to show the injection through annotations within the FXML controller
		  String name = mainListView.getSelectionModel().getSelectedItem().toString();
		  FmmlxObject object = diagram.getObjectByPath(diagram.getPackagePath() + "::" + name);
		  
		  for( FmmlxSlot currSlot : object.getAllSlots() ) {
			   String slotName = currSlot.getName();
			   
			   if(slotName.equals(id.split("INJ")[1])) {
					  // send value to XMF, update the diagram and then update the object browser
					  diagram.getComm().changeSlotValue(diagram.getID(), object.getName(), slotName, value);;
			   }
		  }
		  
		  // Note: The current implementation of the GUI is only supporting expressions as input.
		  // (Except for date..)
		}
	  		 
}