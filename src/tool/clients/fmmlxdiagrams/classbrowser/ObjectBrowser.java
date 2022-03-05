package tool.clients.fmmlxdiagrams.classbrowser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;



public class ObjectBrowser {
	private final Stage stage;
	private final Scene scene;
	//private final VBox container;
	//private GridControl gridControl;
	//private VBoxControl vBoxControl;
	private final FmmlxObject metaClass;
	private final GridPane grid;
	
	private Label instancesOfClassLabel;
	private CheckBox directInstancesOnlyBox;
	private ComboBox<Integer> minLevelBox;
	private ComboBox<Integer> maxLevelBox;
	private ListView<FmmlxObject> objectListView;
	private Label totalNoLabel;
	private Label minLevelLabel;
	private Label maxLevelLabel;
	private ScrollPane rechteSeite;
	
	// LM, 22.12.2021
	// Custom GUI elements
	private Button importCustomGUI;
	private Button exportCustomGUI;
	private Button createInstance;
	private Label graphicSection;
	private boolean instanceIsSelected;
	private boolean customGUIinUse; // Might be set to false to allow the default generated GUI after import
	private FmmlxObject selectedInstance;
	private GridPane rechteSeiteGrid;
	private CustomGUIController controller;
	private String selectedInstanceName;
	

	AbstractPackageViewer diagram;
	Object object2;
	
	public ObjectBrowser(AbstractPackageViewer diagram, FmmlxObject metaClass, Object object2) {
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(3, 3, 3, 3));
			this.scene = new Scene(grid);
			this.stage = new Stage();
			this.stage.setScene(scene);
			this.metaClass = metaClass;
			this.diagram = diagram;
			stage.setTitle("Object Browser for "+this.metaClass.getName());
			stage.setWidth(600);
			stage.setHeight(400);
			instancesOfClassLabel = new Label("Instances of "+this.metaClass.getName());
			Font font = instancesOfClassLabel.getFont();
			instancesOfClassLabel.setFont(Font.font(font.getName(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize()));
			directInstancesOnlyBox = new CheckBox("direct instances only");			
			minLevelLabel = new Label("min level");
			maxLevelLabel = new Label("max level");
			Vector<Integer> minVector = new Vector<>();
			for (int i = 0; i<this.metaClass.getLevel(); i++) {
				minVector.add(i);
			}
			ObservableList<Integer> minList = FXCollections.observableArrayList(minVector);
			minLevelBox = new ComboBox<>(minList);
			minLevelBox.getSelectionModel().selectLast();
			ObservableList<Integer> maxList = FXCollections.observableArrayList(minVector);
			maxLevelBox = new ComboBox<>(maxList);
			maxLevelBox.getSelectionModel().selectLast();
			objectListView = new ListView<>();		
			totalNoLabel = new Label();
				
			// LM, 22.12.2021, Ergänze GUI-Elemente für Custom GUI
			// Upload & Download
			graphicSection = new Label("Custom GUI Support");
			graphicSection.setFont(Font.font(font.getName(), FontWeight.BOLD, FontPosture.REGULAR, font.getSize()));
			importCustomGUI = new Button("Import");
			exportCustomGUI = new Button("Export");
			createInstance = new Button("New direct Instance");
			
			// When the button Export is pressed, call the designated export function
			exportCustomGUI.setOnAction((e)-> {
				exportGeneratedGUI(rechteSeiteGrid);
			});
			// When the button import is pressed, call the designated import function
			importCustomGUI.setOnAction((e)-> {
				replaceGeneratedGUI();
			});
			// When the button new instance is pressed, call the designated instanciate function
			createInstance.setOnAction((e) -> {
				newdirectInstance();
			});
			
			rechteSeite = new ScrollPane();
			objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> newInstanceSelected(newValue));
		
			updateObjectList();
			minLevelBox.valueProperty().addListener((e, oldText, newText) -> {updateObjectList();});
			maxLevelBox.valueProperty().addListener((e, oldText, newText) -> {updateObjectList();});
			directInstancesOnlyBox.setOnAction((e) -> {updateObjectList();});
			layout();
			
	}
	
	// 17.12.2021, LM
	// Try to export the created Grid with a custom built JavaFX Exporter into FXML.
	// This can only be done, if an instance is currently selected and there has not
	// been a custom GUI imported.
	private void exportGeneratedGUI(Node rechteSeite ){
		if( instanceIsSelected && !customGUIinUse ) {
			// File picker as save dialogue
			FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select location for saving the GUI extraction");
            File file = fileChooser.showSaveDialog(stage);
		
			if (file != null) {
				FXMLExporter exporter = new FXMLExporter(file.getPath());
				exporter.export(rechteSeiteGrid);
			}
		}
	}
	
	// 22.12.2021, LM
	// Try to import a custom GUI if an instance is already selected
	// The respective generated GUI should be omitted.
	// The system should remember that there is a custom GUI in use,
	// as custom GUIs shall not be exported.
	private void replaceGeneratedGUI() {
		// Only if an instance is selected
		if (instanceIsSelected) {
			// Pick the file, which shall be read
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open FXML file als custom GUI for the selected instance");
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("JavaFX as XML", "*.fxml"),
			        new ExtensionFilter("All Files", "*.*"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				try {
					// First of sample information about the GUI, which shall be replaced
					String fxml = "";
					SAXReader reader = new SAXReader();
					
					// reference children with id
					Map<String, String> opToEvent = new HashMap<>();
					
					try {
						// Read FXML file
						Document document = reader.read(selectedFile);
												
						// Get all Nodes with an Id
						List<org.dom4j.Node> elements = document.selectNodes("//*[@fx:id]");
						for(org.dom4j.Node node : elements ) {
						   Element element = (Element) node;
							   
						   // Get all attributes of nodes with id
						   Iterator<Attribute> currAttributes = element.attributeIterator();
						   String id = "";
						   while( currAttributes.hasNext() ) {
							   Attribute currAtt = currAttributes.next();
							   String att = currAtt.getName();
							   
							   if(att.equals("id")) {
								   id = currAtt.getValue();
							   } else if(att.substring(0, 2).equals("on")) {
								   // Only event attributes are starting with the prefix on
								   // Build entry for every event in map
								   if (!id.equals("")) {
									   String name = currAtt.getValue();
									   if( !name.equals("#setSlot")) { // only default action at the moment
										   opToEvent.put(id+"/"+currAtt.getName().toUpperCase().substring(2), currAtt.getValue().substring(1));
										   currAttributes.remove(); // remote element from iterator
									   }
								   }
							   }
								   
						   }

						}
						
						// Create a string from the document
						StringWriter stringWriter = new StringWriter();
						XMLWriter writer = new XMLWriter(stringWriter);
						
				        writer.write( document );
				        
				        writer.close();
				        writer.flush();
				        
				        fxml = stringWriter.toString();
						
					} catch( Exception e ) {
							e.printStackTrace();
						}
					
					// Define loading of the custom GUI
					FXMLLoader loader = new FXMLLoader();
					
					// Controller factory in case we wanna use the controller from the fxml file
					loader.setControllerFactory(controller -> {
						// Fmmlx: the controller needs access to the diagramm, the current instance and its type 
						// Also the surrounding object browser and the loader of the GUI has to be referencable
						return new CustomGUIController(diagram, metaClass, loader, selectedInstance, opToEvent, this);
					});
					
					// Load custom GUI from URL
					//loader.setLocation(selectedFile.toURI().toURL()); // Datei als URL in den FXMLLoader übergeben
					//Parent customGUI = loader.load();
					
					// Read manipulated xml file instead of original file
					Parent customGUI = loader.load( new ByteArrayInputStream(fxml.getBytes()) );
					
					// Set default controller if necessary
					if( loader.getController() == null ) {
						controller = new CustomGUIController(diagram, metaClass, loader, selectedInstance, opToEvent, this);
						loader.setController(controller);
						controller.initialize(); // has to be called manually if not called during load process
					} else {
						controller = loader.getController();
					}
					
					// Replace existing UI by loaded custom GUI
					rechteSeite.setContent(customGUI);
											
					// Remember the customGUI is set
					customGUIinUse = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Additionally it has to be catered for a controller class, which might be used
		// by a custom gui. Therefore for a class, which has an attribute for every ID
		// of type String has to be created. This class also has to maintain a method,
		// which allows the update of the FMMLX diagram.
	}
	
	private void newInstanceSelected(FmmlxObject object) {
		// LM, 22.12.2021
		// only execute, if the last selected instance is different
		// from the currently selected one.
		if( object != null && object != selectedInstance) {
			
			// So far the custom GUI supports only slots/attributes and not operations / links
			// Also there is no support for non direct instances as the GUI has to be rebuilt for non direct instances
			if(customGUIinUse) {
				// Unfortunately the default object browser is only updating the diagram
				// but not the instances already read
				// This is fixed by the CustomGUI controller
				controller.setObject(object);
			} else {
				// Current implementation of the GUI generator			
				rechteSeiteGrid = new GridPane();
				rechteSeiteGrid.setHgap(3);
				rechteSeiteGrid.setVgap(3);
				rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));
				rechteSeite.setContent(rechteSeiteGrid);
				int i=0;
				if(object.getAllSlots().size()>0) {
					Label initLabel = new Label("Slots:");
					rechteSeiteGrid.add(initLabel, 0, i);
					i++;
				}
				for(FmmlxSlot slot: object.getAllSlots()) {
					TextField valueTextField = new TextField(slot.getValue());
					Button wertAendern = new Button("Submit");
					
					// ID of a slot will be defined as the slotname
					// Also there will be instructions defined for the respective slot
					// INJ = inject value of slot
					// ACT = setter of slot as action
					// labels will not get an id
					valueTextField.setId( "INJ" + slot.getName() );
					wertAendern.setId( "ACT" + slot.getName() );
					
					wertAendern.setOnAction((e)-> {
						diagram.getComm().changeSlotValue(diagram.getID(), object.getName(), slot.getName(), valueTextField.getText());;
					diagram.updateDiagram();
					});
					Label slotName = new Label(slot.getName());

					rechteSeiteGrid.add(slotName, 0, i);
					rechteSeiteGrid.add(valueTextField, 1, i);
					rechteSeiteGrid.add(wertAendern, 2, i);
					i++;
				}
				
				// Operations and links are only displayed
				// So there is no need for an update function in a custom gui
				// Nevertheless they should also be accounted for display
				if(object.getAllOperationValues().size()>0) {
					rechteSeiteGrid.add(new Label("Operations:"), 0, i);
					i++;
				}
				for(FmmlxOperationValue operationValue: object.getAllOperationValues()) {
					
					TextField valueTextField = new TextField(operationValue.getValue());
					
					// ID of an operation will be defined as its name
					// Also there will be instructions defined for the respective field
					// ACTINJ = inject result of operation
					// labels will not get an id
					valueTextField.setId( "ACTINJ" + operationValue.getName() );
					
					rechteSeiteGrid.add(new Label(operationValue.getName()), 0, i);
					rechteSeiteGrid.add(valueTextField, 1, i);
					i++;
				}
				for(FmmlxLink link: diagram.getRelatedLinksByObject(object)) {
					FmmlxObject otherobject = link.getSourceNode() == object? link.getTargetNode(): link.getSourceNode();
					rechteSeiteGrid.add(new Label("Link: "+ otherobject.getName()), 0, i); //
					i++;
					for(FmmlxSlot slot: otherobject.getAllSlots()) {
						
						TextField valueTextField = new TextField(slot.getValue());
						
						// ID of an associated slot will be defined as its slotname in combination with objectname
						// the object will be separated from the slotname by a "REF"
						// Also there will be instructions defined for the respective field
						// INJ = inject slotvalue
						// labels will not get an id
						
						valueTextField.setId( "REF" + otherobject.getName() + "INJ" + slot.getName() );
						
						rechteSeiteGrid.add(new Label(slot.getName()), 0, i);
						rechteSeiteGrid.add( valueTextField, 1, i);
						
						i++;
					}
				}
		
				
				/*fï¿½r alle Links im Diagram, wenn der Source = object ist, dann mach irgendwas mit target;
				 *  wenn der target=newValue ist, mach etwas mit source
				stopf alle in einen Vektor rein=irgendwas;
				dann die gefundenden(im Vektor gespeicheren) Namen anzeigen
				spï¿½ter fï¿½r alle gefundenen die Eigenschaften anzeigen
					 */	
				
				// end of current implementation of the GUI generator
			}
			
			// LM, 22.12.2021
			// Remember that an instance has been selected
			instanceIsSelected = true;
			selectedInstance = object;
			selectedInstanceName = object.getName();
		}
	}


	public void updateObjectList() {

		Vector<FmmlxObject> objectsVector = new Vector<>();
		if (directInstancesOnlyBox.isSelected()) {
			objectsVector.addAll(metaClass.getInstances());
		} else {
			if (minLevelBox.getSelectionModel().getSelectedItem() <= maxLevelBox.getSelectionModel()
					.getSelectedItem()) {
				for (int i = minLevelBox.getSelectionModel().getSelectedItem(); i <= maxLevelBox.getSelectionModel()
						.getSelectedItem(); i++) {
					objectsVector.addAll(metaClass.getInstancesByLevel(i));
				}
			}
		}
		
		ObservableList<FmmlxObject> objectList = FXCollections.observableArrayList(objectsVector);
		int currEl = objectListView.getSelectionModel().getSelectedIndex();
		objectListView.setItems(objectList);
		objectListView.getSelectionModel().select(currEl);
		totalNoLabel.setText("total no.: "+ objectsVector.size());
	}
	
	private void newdirectInstance() {
		if( customGUIinUse ) {
	        // create a text input dialog
	        TextInputDialog td = new TextInputDialog("");
	        td.setHeaderText("Enter name of the new instance");
	        td.showAndWait();
			
	        // Create class without attributes
	        String newInstance = td.getEditor().getText();
			diagram.getComm().addNewInstance(diagram.getID(), metaClass.getName(), newInstance, metaClass.getLevel()-1, selectedInstance.getParentsPaths(), false, 0, 0, false);
			
			// also associations have to be satisfied
			for( FmmlxAssociation currAssoc : metaClass.getAllRelatedAssociations() ) {
		        td.setHeaderText("Enter name of linked object for association from " + currAssoc.getSourceNode().getName() + " to " + currAssoc.getTargetNode().getName());
		        td.showAndWait();
		        String assoc = td.getEditor().getText();
			
		        diagram.getComm().addAssociationInstance(diagram.getID(), newInstance, assoc, currAssoc.getName());
			
			}
		        
			diagram.updateDiagram();
			try {
				Thread.sleep(200);
			} catch( Exception e ) {
				
			}
			updateObjectList();
		}
	}


	private void layout() {
	
		grid.add(instancesOfClassLabel, 0, 0,2,1);
		grid.add(directInstancesOnlyBox, 0, 1,2,1);
		grid.add(minLevelLabel, 0, 2);
		grid.add(minLevelBox, 1, 2);
		grid.add(maxLevelLabel, 0, 3);
		grid.add(maxLevelBox, 1, 3);
		grid.add(objectListView, 0, 4,2,1);
		grid.add(totalNoLabel, 0, 5,2,1);
		
		// LM, 22.12.2021
		// Add Custom GUI elements to the layout
		grid.add(graphicSection, 0, 6);
		grid.add(exportCustomGUI, 0, 7);
		grid.add(importCustomGUI, 0, 7);
		grid.add(createInstance, 0, 7);
		importCustomGUI.setMinWidth(70);
		exportCustomGUI.setMinWidth(70);
		createInstance.setMinWidth(140);
		Insets importButton = new Insets(0, 0, 0, 80);
		Insets createInstanceButton = new Insets(0, 0, 0, 160);
		GridPane.setMargin(importCustomGUI, importButton);
		GridPane.setMargin(createInstance, createInstanceButton);
		
		grid.add(rechteSeite, 2, 0,1,7);
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints(200, 400, Integer.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true));
	}
	
	public void show() {	
		stage.show();
	}

}
