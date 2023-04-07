package tool.clients.fmmlxdiagrams.classbrowser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import tool.clients.customui.AssociationInstanceMapping;
import tool.clients.customui.ControllerLanguageInterpreter;
import tool.clients.customui.CustomGUIController;
import tool.clients.customui.FXMLExporter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;


public class ObjectBrowser {
	private Stage stage;
	private Scene scene;
	private GridPane grid;
	
	private final FmmlxObject metaClass; // CREF - Class of Object Browser
	AbstractPackageViewer diagram;

	private Label instancesOfClassLabel;
	private ListView<String> objectListView; // mainListView !!
	private ScrollPane defaultGUIPane;
	
	// Custom GUI elements
	private Button exportCustomGUI;
	private Label graphicSection;
	private boolean instanceIsSelected;
	private FmmlxObject selectedInstance;
	private Integer selectedEl;
	
	private GridPane rechteSeiteGrid;
	
	// Recursive data updates
	private HashMap<AssociationInstanceMapping, List<String>> linksPerAssociationPerInstance = new HashMap<>();
	private List<FmmlxLink> visitedLinks = new ArrayList<>();
	private List<FmmlxAssociation> noListBoxAssocs = new ArrayList<>();
	private List<String> paintedElements = new ArrayList<>();
	HashMap<String, ListView<String>> listViewsForAssociations = new HashMap<>();
	
	public ObjectBrowser(AbstractPackageViewer diagram, FmmlxObject metaClass) {
		this.metaClass = metaClass;
		this.diagram = diagram;

		defaultGUIPane = new ScrollPane();
		
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(3, 3, 3, 3));
		
		defaultGUIPane.setContent(grid);
		
		this.scene = new Scene(defaultGUIPane);
		this.stage = new Stage();
		this.stage.setScene(scene);
		
		stage.setTitle("Object Browser for "+this.metaClass.getName());
		stage.setWidth(800);
		stage.setHeight(400);
		
		// only direct instances are supported for now!!
		// has to be revised at a later point in time
				
		// LM, 22.12.2021, Erg�nze GUI-Elemente f�r Custom GUI
		// Upload & Download
		graphicSection = new Label("Custom GUI Support");
		graphicSection.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize()));
		exportCustomGUI = new Button("Export");
			
		// When the button Export is pressed, call the designated export function
		exportCustomGUI.setOnAction((e)-> {
			exportGeneratedGUI(rechteSeiteGrid);
		});
			
		// Add Custom GUI elements to the layout
		grid.add(graphicSection, 0, 0);
		grid.add(exportCustomGUI, 0, 1);
		exportCustomGUI.setMinWidth(70);
		Insets importButton = new Insets(0, 0, 0, 80);
		Insets createInstanceButton = new Insets(0, 0, 0, 160);
			
		// trigger generation of default GUI with first instance!
		newInstanceSelected();
	}
	
	// 17.12.2021, LM
	// Try to export the created Grid with a custom built JavaFX Exporter into FXML.
	// This can only be done, if an instance is currently selected and there has not
	// been a custom GUI imported.
	private void exportGeneratedGUI(Node rechteSeite ){
		if( instanceIsSelected ) {
			// File picker as save dialogue
			FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select location for saving the GUI extraction");
            fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("JavaFX as XML", "*.fxml"),
			        new ExtensionFilter("All Files", "*.*"));
            
            File file = fileChooser.showSaveDialog(stage);
		
			if (file != null) {
				FXMLExporter exporter = new FXMLExporter(file.getPath());
				exporter.export(rechteSeiteGrid);
			}
		}
	}
	
	
	private transient int rowCount;
	
	private void newInstanceSelected() {
		rowCount = 0;
		// only execute, if the last selected instance is different
		// from the currently selected one.
		Integer selEl;
		if( objectListView != null ) {
			selEl = objectListView.getSelectionModel().getSelectedIndex();
		} else {
			selEl = 0;
		}
		
		if( selEl != selectedEl) {
			
			// initial fill of class instances
			Vector<String> objectsVector = new Vector<>();
			for( FmmlxObject el : metaClass.getInstances() ) {
				objectsVector.add(el.getName());
			}
			objectsVector.sort(null);
			ObservableList<String> objectList = FXCollections.observableArrayList(objectsVector);
			
			rechteSeiteGrid = new GridPane();
			rechteSeiteGrid.setHgap(3);
			rechteSeiteGrid.setVgap(3);
			rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));
					
			if(!objectsVector.isEmpty()) {
				FmmlxObject currObj = diagram.getObjectByPath(diagram.getPackagePath() + "::" + objectList.get(selEl));
				
		
				// Current implementation of the GUI generator

					
				// allow repaint of layout
				paintedElements.clear();
					
				// Header label
				instancesOfClassLabel = new Label("Instances of "+this.metaClass.getName());
				instancesOfClassLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize()));
				rechteSeiteGrid.add(instancesOfClassLabel, 0, rowCount++);
					
				// Main objects to be browsed
				objectListView = new ListView<>();
				rechteSeiteGrid.add(objectListView, 0, rowCount++);
					
				objectListView.setItems(objectList);
				objectListView.getSelectionModel().select(selEl);
									
				// CREF is used to inject all instances of a class without any dependence
				String maxMetaClass = maxParent.max(diagram, metaClass);
				objectListView.setId("CREF" + maxMetaClass );
					
				// Handle updates
				objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> newInstanceSelected());
				
				// Output additional class components for main class to be browsed
				if(currObj.getAllSlots().size()>0) {
					Label initLabel = new Label("Slots:");
					rechteSeiteGrid.add(initLabel, 0, rowCount++);
				}
				for(FmmlxSlot slot: currObj.getAllSlots()) {
					TextField valueTextField = new TextField(slot.getValue());
					Button wertAendern = new Button("Submit");
						
					// ID of a slot will be defined as the slotname
					// Also there will be instructions defined for the respective slot
					// INJ = inject value of slot
					// ACT = setter of slot as action
					// labels will not get an id
						
					valueTextField.setId("REF" +  maxMetaClass + "INJ" + slot.getName() );
					wertAendern.setId( "REF" +  maxMetaClass + "ACT" + slot.getName() );
						
					wertAendern.setOnAction((e)-> {
						diagram.getComm().changeSlotValue(diagram.getID(), currObj.getName(), slot.getName(), valueTextField.getText());;
					diagram.updateDiagram();
					});
					Label slotName = new Label(slot.getName());
	
					// remember painted slot
					if( ! paintedElements.contains( currObj.getMetaClassName() + slot.getName() ) ) {
							
						paintedElements.add(currObj.getMetaClassName() + slot.getName());
							
						rechteSeiteGrid.add(slotName, 0, rowCount);
						rechteSeiteGrid.add(valueTextField, 1, rowCount);
						rechteSeiteGrid.add(wertAendern, 2, rowCount);
						rowCount++;
						
					}
						
						
	
				}
					
				// Operations and links are only displayed
				// So there is no need for an update function in a custom gui
				// Nevertheless they should also be accounted for display
				if(currObj.getAllOperationValues().size()>0) {
					rechteSeiteGrid.add(new Label("Operations:"), 0, rowCount++);
				}
				for(FmmlxOperationValue operationValue: currObj.getAllOperationValues()) {
						
					TextField valueTextField = new TextField(operationValue.getValue());
						
					// ID of an operation will be defined as its name
					// Also there will be instructions defined for the respective field
					// ACTINJ = inject result of operation
					// labels will not get an id
					valueTextField.setId( "REF" +  maxMetaClass + "ACTINJ" + operationValue.getName() );
						
					// remember painted operations
					if( ! paintedElements.contains( currObj.getMetaClassName() + operationValue.getName() ) ) {
							
						paintedElements.add(currObj.getMetaClassName() + operationValue.getName());
							
						rechteSeiteGrid.add(new Label(operationValue.getName()), 0, rowCount);
						rechteSeiteGrid.add(valueTextField, 1, rowCount);
						rowCount++;
						
					}		
	
				}
					
				// Evtl. Logik von ID trennen in anderer Information?`
				// z. B. fx:reference??
				// https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html
					
				// How to trigger actions / set actions?
				// Hot to allow parameters? / set parameters?
					
				// clean up previous calls
				linksPerAssociationPerInstance.clear();
				visitedLinks.clear();
				noListBoxAssocs.clear();
				listViewsForAssociations.clear();
					
				// recursive handling of references
				recursiveAssocs(currObj, 0);
					
				// end of current implementation of the GUI generator
				
				// LM, 22.12.2021
				// Remember that an instance has been selected
				instanceIsSelected = true;
				selectedInstance = currObj;
				selectedEl = selEl;
			}
			
			// Repaint layout
			int childrenNo = grid.getChildren().size();
			if( childrenNo == 5) {
				grid.getChildren().remove(childrenNo-1);
			}
			grid.add(rechteSeiteGrid, 0, 2);
			updateInputFields();
		}
	}
	
	public void recursiveAssocs(FmmlxObject currObject, int depth) {
		// Cancel this call if there are no links, which have not been referenced yet
		boolean complete = true;
		for(FmmlxLink link: diagram.getRelatedLinksByObject(currObject)) {
			if( ! visitedLinks.contains(link) ) {
				complete = false;
				break;
			}
		}
		
		if( complete ) {
			return;
		} else {
			depth++; // count depth
		}
		
		// Count no. of links per each link of an instance with respondance to a single association
		// and remember elements instances in a list
		for(FmmlxLink link: diagram.getRelatedLinksByObject(currObject)) {
			FmmlxAssociation currAssoc = link.getAssociation();
			
			// do not rehandle an already handled link
			if( visitedLinks.contains(link) ) {
				continue;
			} else {
				visitedLinks.add(link);
			}
			
			FmmlxObject otherObject = link.getSourceNode() == currObject? link.getTargetNode(): link.getSourceNode();
			
			// if the object is already present within the references, than do not consider it again
			// this is the case e. g. if there are two links on the same object from different sources
			boolean elemExist = false;
			Iterator<List<String>> iter = linksPerAssociationPerInstance.values().iterator();
			while( iter.hasNext() ) {
				if( iter.next().contains(otherObject.getName()) ) {
					elemExist = true;
				}
			}
			
			if( elemExist ) {
				continue;
			}
			
			// build memory of related elements
			// combination of association name and originating instance
			// the amount of association instances, which relate to this one element can be considered the length
			// of the stored list within the current key
			AssociationInstanceMapping currKey = new AssociationInstanceMapping(currAssoc, currObject);
			if( !linksPerAssociationPerInstance.containsKey(currKey) ) {
				List<String> list = new ArrayList<String>();
				linksPerAssociationPerInstance.put(currKey, list);
			}
				
			List<String> list = linksPerAssociationPerInstance.get(currKey);
			list.add(otherObject.getName());	
		}
		
		for(FmmlxLink link: diagram.getRelatedLinksByObject(currObject)) {
			// get Key for Values
			FmmlxAssociation currAssoc = link.getAssociation();
			AssociationInstanceMapping currKey = new AssociationInstanceMapping(currAssoc, currObject);
			
			// get other end of the link
			FmmlxObject otherobject = link.getSourceNode() == currObject? link.getTargetNode(): link.getSourceNode();
			
			// if there is more than one association instance ask the user if a listbox shall be created
			// only if there is not already a list box or the question has been asked already
			int j = 0;
			List<String> currList = linksPerAssociationPerInstance.get(currKey);
			if( currList != null ) {
				j = currList.size();
			}
			
			if( j > 1 ) {
				if( ! listViewsForAssociations.containsKey(link.getAssociation().getName() ) ) {						
					// Create list box & add respective elements
					ListView<String> listView = new ListView<>();
					ObservableList<String> items = FXCollections.observableArrayList(linksPerAssociationPerInstance.get(currKey));
					listView.setItems(items);
	
					// reference the metaclass which is contained by the list view with the id (= REF)
					String maxMetaClass = maxParent.max(diagram, otherobject);
					
					listView.setId("REF" + maxMetaClass );
					listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateInputFields());
					listView.getSelectionModel().selectFirst();
					
					listViewsForAssociations.put(link.getAssociation().getName(), listView);
	
					// remember painted assocs
					if( ! paintedElements.contains( otherobject.getMetaClassName() ) ) {
						
						paintedElements.add( otherobject.getMetaClassName() );
						
						rechteSeiteGrid.add(new Label("Links to instances of class "+ otherobject.getMetaClassName()), 0, rowCount++);
						rechteSeiteGrid.add(listView, 0, rowCount++);
						
						appendToDefaultGUI(otherobject);
					
					}	
					
				} else {
					// add items of other source node?
					// is this even possible? or senseful? e. g. different invoice items to their products
				}
			} else if ( j == 1 ) {
				// create input fields for every link if no list view has been created
					if( ! noListBoxAssocs.contains(link.getAssociation())) {
						
						noListBoxAssocs.add(link.getAssociation());
						
						// remember painted assocs
						if( ! paintedElements.contains( otherobject.getMetaClassName() ) ) {
							
							paintedElements.add( otherobject.getMetaClassName() );
							
							rechteSeiteGrid.add(new Label("Link to instance of class "+ otherobject.getMetaClassName()), 0, rowCount++); //
							
							appendToDefaultGUI(otherobject);
						
						}
						
					}
			} else {
				// j == 0
				// occurs if the link is not considered as relevant due to the fact that
				// an instance is already represented within the default transformation
			}
			
			// recall operation for the currently selected object
			recursiveAssocs(otherobject, depth);
			
		}
	}
	
	public void appendToDefaultGUI(FmmlxObject object) {
		// create the slots for the selected link once
		if(object.getAllSlots().size()>0) {
			rechteSeiteGrid.add(new Label("Slots:"), 0, rowCount++);
		}
		
		for(FmmlxSlot slot: object.getAllSlots()) {
			
			TextField valueTextField = new TextField(slot.getValue());
				
			// ID of an associated slot will be defined as its slotname in combination with objectname
			// the object will be separated from the slotname by a "REF"
			// Also there will be instructions defined for the respective field
			// INJ = inject slotvalue
			// labels will not get an id
			String maxMetaClass = maxParent.max(diagram, object);
			valueTextField.setId( "REF" + maxMetaClass + "INJ" + slot.getName() );
				
			// remember painted slots
			if( ! paintedElements.contains( object.getMetaClassName() + slot.getName() ) ) {
				
				paintedElements.add( object.getMetaClassName() + slot.getName() );
				
				rechteSeiteGrid.add(new Label(slot.getName()), 0, rowCount);
				rechteSeiteGrid.add( valueTextField, 1, rowCount);
				rowCount++;
			}

		}
		
		// Operations and links are only displayed
		// So there is no need for an update function in a custom gui
		// Nevertheless they should also be accounted for display
		if(object.getAllOperationValues().size()>0) {
			rechteSeiteGrid.add(new Label("Operations:"), 0, rowCount++);
		}
		
		for(FmmlxOperationValue operationValue: object.getAllOperationValues()) {
			
			TextField valueTextField = new TextField(operationValue.getValue());
			
			// ID of an operation will be defined as its name
			// Also there will be instructions defined for the respective field
			// ACTINJ = inject result of operation
			// labels will not get an id
			String maxMetaClass = maxParent.max(diagram, object);
			valueTextField.setId( "REF" + maxMetaClass + "ACTINJ" + operationValue.getName() );
						
			// remember painted operations
			if( ! paintedElements.contains( object.getMetaClassName() + operationValue.getName() ) ) {
			
				paintedElements.add( object.getMetaClassName() + operationValue.getName() );
				
				
				rechteSeiteGrid.add(new Label(operationValue.getName()), 0, rowCount);
				rechteSeiteGrid.add(valueTextField, 1, rowCount);
				rowCount++;
			
			}
			
		}
	}
	
	public void updateInputFields() {		
		// get through all elements and update them corresponding to their ID
		for( Node currElem : rechteSeiteGrid.getChildren() ) {
			String id = currElem.getId();
			if( id != null && id.length() > 0 ){
				ControllerLanguageInterpreter exec = new ControllerLanguageInterpreter(id, diagram, objectListView, linksPerAssociationPerInstance, listViewsForAssociations);
				try {
					if( ! exec.isResultList() ) {
						if( currElem instanceof Button ) {
							continue;
						}
						
						((TextField) currElem ).setText( (String) exec.fetchResult());
					} else {
						((ListView<String>) currElem).setItems( (ObservableList<String>) exec.fetchResult());
					}
				} catch( Exception e ) {
					if( currElem instanceof TextField) {
						((TextField) currElem ).setText( "null");
					}
					e.printStackTrace();
				}
			}
		}
	}
	
	public void show() {	
		stage.show();
	}

}

class maxParent{
	public static String max(AbstractPackageViewer diagram, FmmlxObject start){
		
		String par = start.getMetaClassName();
		
		if( par.equals("MetaClass") ) {
			return start.getName();
		} else {
			return max( diagram, diagram.getObjectByPath(diagram.getPackagePath() + "::" + par));
		}
	}
}