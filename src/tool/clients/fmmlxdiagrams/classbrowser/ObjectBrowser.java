package tool.clients.fmmlxdiagrams.classbrowser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.ReturnCall;


public class ObjectBrowser {
	
	private Stage stage;
	private Scene scene;
	private GridPane grid;
	
	private final FmmlxObject metaClass; // CREF - Class of Object Browser
	AbstractPackageViewer diagram;
	
	private ScrollPane defaultGUIPane;
	
	private List<String> visitedObjects = new ArrayList<>();
	private List<String> visitedClasses = new ArrayList<>();
	
	private int rowCount = 0;
	private Integer minLevel = 0;
	int cellHeight = 24;

	
	public ObjectBrowser(AbstractPackageViewer diagram, FmmlxObject metaClass)
	{
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
		
		GridPane minLevelGrid = createMinLevelGrid();
		
		grid.addRow(rowCount,  minLevelGrid);
		rowCount++;
		
		anyClassSelected(metaClass);	
	}
	
	public void anyClassSelected(FmmlxObject selectedClass)
	{
		if(selectedClass.getLevel() > 1)
		{
			metaClassSelected(selectedClass);
		}
		else
		{
			classSelected(selectedClass);
		}
	}

	/**
	 * Called when a meta-class is selected in the ObjectBrowser
	 * @param selectedClass The selected FmmlxObject representing the meta-class
	 */
	public void metaClassSelected(FmmlxObject selectedClass) {
	    // Add the selected meta-class to the visitedClasses list
	    visitedClasses.add(selectedClass.getName());

	    List<String> instanceNamesOfClass = new ArrayList<>();

	    // Retrieve the names of instances belonging to the selected meta-class
	    for (FmmlxObject instance : selectedClass.getInstances()) {
	        instanceNamesOfClass.add(instance.getName());
	    }

	    // Sort the instance names alphabetically
	    instanceNamesOfClass.sort(null);

	    // Create and populate the objectListView
	    ListView<String> objectListView = convertToListView(instanceNamesOfClass);

	    // Calculate the number of items in the objectListView
	    int itemCount = objectListView.getItems().size();

	    // Set the preferred height of the objectListView based on the number of items
	    objectListView.setPrefHeight(cellHeight * itemCount);

	    GridPane instancesOfClassGrid = new GridPane();

	    VBox rowContainerInstances = new VBox();
	    rowContainerInstances.getChildren().add(new Label("Instances of " + selectedClass.getName()));
	    instancesOfClassGrid.addRow(0, rowContainerInstances);
	    rowContainerInstances.setPrefHeight(40);
	    rowContainerInstances.setAlignment(Pos.CENTER_LEFT);

	    instancesOfClassGrid.addRow(1, objectListView);

	    grid.addRow(rowCount, instancesOfClassGrid);
	    rowCount++;

	    // Set up a listener to respond to selection changes in the objectListView
	    objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue != null) {
	            // Get the level of the selected instance
	            int someClassLevel = getFmmlxObjectByName(newValue, diagram).getLevel();
	            // If the instance meets the selection criteria, call anyClassSelected() to display its information
	            if (!visitedClasses.contains(newValue) && (minLevel == 0 || someClassLevel >= minLevel)) 
	            {
	            	List<String> classesToRemove = new ArrayList<String>();
	            	for(String metaClass : visitedClasses)
	            	{
	            		if(getFmmlxObjectByName(metaClass, diagram).getLevel() < selectedClass.getLevel())
	            		{
	            			classesToRemove.add(metaClass);
	            		}
	            	}
	            	visitedClasses.removeAll(classesToRemove);
	            	visitedObjects.clear();
	            	
	            	updateObjectBrowser(visitedClasses, visitedObjects);
	            	anyClassSelected(getFmmlxObjectByName(newValue, diagram));
	            }
	        }
	    });
	}

	/**
	 * Called when a class is selected in the ObjectBrowser
	 * @param selectedClass The selected FmmlxObject representing the class
	 */
	public void classSelected(FmmlxObject selectedClass) {
	    // Add the selected class to the visitedClasses list
	    visitedClasses.add(selectedClass.getName());

	    List<String> instanceNamesOfClass = new ArrayList<>();

	    // Retrieve the names of instances belonging to the selected class
	    for (FmmlxObject instance : selectedClass.getInstances()) {
	        instanceNamesOfClass.add(instance.getName());
	    }

	    // Sort the instance names alphabetically
	    instanceNamesOfClass.sort(null);

	    // Create and populate the objectListView
	    ListView<String> objectListView = convertToListView(instanceNamesOfClass);

	    // Calculate the number of items in the objectListView
	    int itemCount = objectListView.getItems().size();

	    // Set the preferred height of the objectListView based on the number of items
	    objectListView.setPrefHeight(cellHeight * itemCount);

	    GridPane instancesOfClassGrid = new GridPane();

	    VBox rowContainerInstances = new VBox();
	    rowContainerInstances.getChildren().add(new Label("Instances of " + selectedClass.getName()));
	    instancesOfClassGrid.addRow(0, rowContainerInstances);
	    rowContainerInstances.setPrefHeight(40);
	    rowContainerInstances.setAlignment(Pos.CENTER_LEFT);

	    instancesOfClassGrid.addRow(1, objectListView);

	    grid.addRow(rowCount, instancesOfClassGrid);
	    rowCount++;

	    // Set up a mouse-click event handler for the objectListView to handle instance selection
	    objectListView.setOnMouseClicked(event -> {
	        if (!visitedObjects.contains(objectListView.getSelectionModel().getSelectedItem()) && (minLevel <= 0)) {
	            // If a valid instance is selected, call newInstanceSelected() to display its information
	            newInstanceSelected(getFmmlxObjectByName(objectListView.getSelectionModel().getSelectedItem(), diagram));
	        }
	    });
	}

	/**
	 * Called when a new instance of a FmmlxObject class is selected in the ObjectBrowser
	 * @param instance The selected FmmlxObject representing the new instance.
	 */
	public void newInstanceSelected(FmmlxObject instance) {
	    // Check if minLevel is 0 (i.e., no minimum level restriction)
	    if (minLevel == 0) {
	        visitedObjects.add(instance.getName());

	        // Create a GridPane to display the attributes and methods of the selected instance
	        GridPane instanceGrid = new GridPane();
	        instanceGrid.add(getAttributesAndMethods(instance), 0, 0);
	        instanceGrid.add(getInstanceLinks(instance), 1, 0);

	        // Create a VBox to hold the GridPane and add a top border to the VBox
	        VBox vbox = new VBox(instanceGrid);
	        vbox.getStyleClass().add("top-border-vbox");
	        String css = ".top-border-vbox { -fx-border-width: 1 0 0 0; -fx-border-color: black; }";
	        vbox.setStyle(css);

	        // Add the VBox to the main grid to display the details of the selected instance
	        grid.add(vbox, 1, rowCount - 1);
	        rowCount++;
	    }
	}
	
	/**
	 * Creates a GridPane that displays the attributes and methods of a given FmmlxObject instance.
	 * The GridPane includes rows for attribute names and corresponding input fields (DatePickers or TextFields)
	 * and rows for method names and their corresponding values (if available).
	 *
	 * @param instance The FmmlxObject instance whose attributes and methods are to be displayed.
	 * @return A GridPane displaying the attributes and methods of the given FmmlxObject instance.
	 */
	public GridPane getAttributesAndMethods(FmmlxObject instance) {
	    GridPane attributesAndMethodsGrid = new GridPane();
	    int attrAndMethodesRowCount = 0;

	    // Create and add a VBox for attribute names
	    VBox rowContainerAttributes = new VBox();
	    rowContainerAttributes.getChildren().add(new Label("Attributes of " + instance.getName()));
	    attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, rowContainerAttributes);
	    rowContainerAttributes.setPrefHeight(40);
	    rowContainerAttributes.setAlignment(Pos.CENTER_LEFT);

	    attrAndMethodesRowCount++;

	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

	    // Loop through all slots of the instance and display them in the GridPane
	    for (FmmlxSlot slot : instance.getAllSlots()) {
	        Label slotName = new Label(slot.getName());

	        if (isDate(slot.getValue(), "dd MMM yyyy")) {
	            // If the slot value is a date, display it using a DatePicker
	            DatePicker datePicker = new DatePicker();

	            // Set the initial value of the DatePicker to the current date
	            LocalDate currentDate = LocalDate.parse(slot.getValue(), dateFormatter);
	            datePicker.setValue(currentDate);

	            datePicker.setOnAction((event) -> {
	                // When a new date is selected in the DatePicker, update the slot value in the diagram
	                LocalDate selectedDate = datePicker.getValue();
	                String newDate = "Date::createDate(" + selectedDate.getYear() + "," +
	                                selectedDate.getMonthValue() + "," + selectedDate.getDayOfMonth() + ")";
	                diagram.getComm().changeSlotValue(diagram.getID(), instance.getName(), slot.getName(), newDate);

	                // Update the diagram and the ObjectBrowser
	                ReturnCall<Object> onUpdate = update -> {
	                    updateObjectBrowser(visitedClasses, visitedObjects);
	                };
	                diagram.updateDiagram(onUpdate);
	            });

	            // Add the DatePicker to the GridPane
	            attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, slotName, datePicker);
	            attrAndMethodesRowCount++;
	        } else if (slot.getType(diagram).equals("String")) {
	            // If the slot type is "String", display it using a TextField
	            TextField valueTextField = new TextField(slot.getValue());

	            // Action performed when the "Submit" button is clicked
	            Button changeValue = new Button("Submit");
	            changeValue.setOnAction((e) -> {
	                // When the "Submit" button is clicked, update the slot value in the diagram
	                String newValueString = "\"" + valueTextField.getText() + "\"";
	                diagram.getComm().changeSlotValue(diagram.getID(), instance.getName(), slot.getName(), newValueString);

	                // Update the diagram and the ObjectBrowser
	                ReturnCall<Object> onUpdate = update -> {
	                    updateObjectBrowser(visitedClasses, visitedObjects);
	                };
	                diagram.updateDiagram(onUpdate);
	            });

	            // Add the TextField and the "Submit" button to the GridPane
	            attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, slotName, valueTextField, changeValue);
	            attrAndMethodesRowCount++;
	        } else {
	            // For slots with other types, display the values using a TextField
	            TextField valueTextField = new TextField(slot.getValue());

	            // Action performed when the "Submit" button is clicked
	            Button changeValue = new Button("Submit");
	            changeValue.setOnAction((e) -> {
	                // When the "Submit" button is clicked, update the slot value in the diagram
	                String newValueString = valueTextField.getText();
	                diagram.getComm().changeSlotValue(diagram.getID(), instance.getName(), slot.getName(), newValueString);

	                // Update the diagram and the ObjectBrowser
	                ReturnCall<Object> onUpdate = update -> {
	                    updateObjectBrowser(visitedClasses, visitedObjects);
	                };
	                diagram.updateDiagram(onUpdate);
	            });

	            // Add the TextField and the "Submit" button to the GridPane
	            attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, slotName, valueTextField, changeValue);
	            attrAndMethodesRowCount++;
	        }
	    }

	    // Create and add a VBox for method names
	    VBox rowContainerMethods = new VBox();
	    rowContainerMethods.getChildren().add(new Label("Methods of " + instance.getName()));
	    attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, rowContainerMethods);
	    rowContainerMethods.setPrefHeight(40);
	    rowContainerMethods.setAlignment(Pos.CENTER_LEFT);

	    attrAndMethodesRowCount++;

	    // Create labels for operation values
	    ArrayList<Label> operationValueLabels = new ArrayList<>();
	    // Create labels for operation names
	    ArrayList<Label> operationNameLabels = new ArrayList<>();

	    // Adding labels for each operation value
	    for (FmmlxOperationValue operationValue : instance.getAllOperationValues()) {
	        Label valueLabel = new Label(operationValue.getValue());
	        operationValueLabels.add(valueLabel);
	    }

	    // Adding labels for each operation
	    for (FmmlxOperation operation : getFmmlxObjectByName(instance.getMetaClassName(), diagram).getAllOperations()) {
	        Label operationName = new Label(operation.getName());
	        operationNameLabels.add(operationName);
	    }

	    // Adding rows for each operation and its corresponding value (if available)
	    for (int i = 0; i < operationNameLabels.size(); i++) {
	        if (i < operationValueLabels.size()) {
	            attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, operationNameLabels.get(i), operationValueLabels.get(i));
	            attrAndMethodesRowCount++;
	        } else {
	            attributesAndMethodsGrid.addRow(attrAndMethodesRowCount, operationNameLabels.get(i));
	            attrAndMethodesRowCount++;
	        }
	    }

	    return attributesAndMethodsGrid;
	}
	
	/**
	 * Creates a GridPane to display the links of the selected instance
	 * @param instance The selected FmmlxObject representing the instance
	 * @return The GridPane displaying the links of the selected instance
	 */
	public GridPane getInstanceLinks(FmmlxObject instance) {
	    // Create a list to hold the names of target or source nodes for each link related to the instance
	    List<String> objectLinks = new ArrayList<>();
	    List<FmmlxLink> allInstanceLinks = diagram.getRelatedLinksByObject(instance);

	    // Adding target or source node names of each link to the objectLinks list
	    for (FmmlxLink link : allInstanceLinks) {
	        if (!link.getTargetNode().equals(instance)) {
	            objectLinks.add(link.getTargetNode().getName());
	        } else {
	            objectLinks.add(link.getSourceNode().getName());
	        }
	    }

	    // Removing duplicates from the objectLinks list
	    removeDuplicates(objectLinks);

	    // Convert the objectLinks list to a ListView
	    ListView<String> objectLinksListView = convertToListView(objectLinks);
	    int itemCount = objectLinksListView.getItems().size();
	    objectLinksListView.setPrefHeight(cellHeight * itemCount);

	    // Create a grid for the links
	    GridPane linkGrid = new GridPane();

	    // Adding a label row for "Links of [instance name]"
	    VBox rowContainerLinks = new VBox();
	    rowContainerLinks.getChildren().add(new Label("Links of " + instance.getName()));
	    linkGrid.addRow(0, rowContainerLinks);
	    rowContainerLinks.setPrefHeight(40);
	    rowContainerLinks.setAlignment(Pos.CENTER_LEFT);

	    // Adding the ListView to the linkGrid
	    linkGrid.addRow(1, objectLinksListView);

	    // Event handler for when an item in the ListView is clicked to show the details of the selected link's target node
	    objectLinksListView.setOnMouseClicked(event -> {
	        if (!visitedObjects.contains(objectLinksListView.getSelectionModel().getSelectedItem())) {
	            newInstanceSelected(getFmmlxObjectByName(objectLinksListView.getSelectionModel().getSelectedItem(), diagram));
	        }
	    });

	    return linkGrid;
	}
	
	/**
	 * Creates a GridPane to set the value for the minimum level (minLevel)
	 * @return The GridPane to set the value for minLevel.
	 */
	private GridPane createMinLevelGrid() {
	    GridPane minLevelGrid = new GridPane();
	    Label minLevelLabel = new Label("Min Level:");
	    TextField minLevelTextField = new TextField();

	    // Listener for the TextField to ensure that only numeric values are entered
	    minLevelTextField.textProperty().addListener((observable, oldValue, newValue) -> {
	        if (!newValue.matches("\\d*")) {
	            minLevelTextField.setText(newValue.replaceAll("[^\\d]", ""));
	        }
	    });

	    Button submitButton = new Button("Submit");
	    submitButton.setOnAction((event) -> {
	        String minLevelText = minLevelTextField.getText();
	        if (!minLevelText.isEmpty()) {
	            int level = Integer.parseInt(minLevelText);
	            if (level >= 0) {
	                minLevel = level; // Update minLevel if a valid numeric value is entered
	            }
	        } else {
	            minLevel = 0; // Reset minLevel to 0 if no value is entered
	        }
	    });

	    minLevelGrid.addRow(0, minLevelLabel, minLevelTextField, submitButton);
	    return minLevelGrid;
	}
	
	public ListView<String> convertToListView(List<String> inputList)
	{
	    ObservableList<String> observableList = FXCollections.observableArrayList(inputList);
	    ListView<String> listView = new ListView<>(observableList);
	    return listView;
	}
	
	public FmmlxObject getFmmlxObjectByName(String instanceName, AbstractPackageViewer diagram) 
	{
		Vector<FmmlxObject> objekte = diagram.getObjectsReadOnly();
	    for (FmmlxObject instance : objekte)
	    {
	        boolean sameName = instance.getName().equals(instanceName);
	        if (sameName) 
	        {
	            return instance;
	        }
	    }
	    return null; // If no match is found
	}

	/**
	 * Updates the view of the Object Browser.
	 */
	public void updateObjectBrowser(List<String> visitedClasses, List<String> visitedObjects) {
	    // Clears all existing contents of the GridPane (grid) and sets rowCount to 0
	    grid.getChildren().clear();
	    rowCount = 0;

	    // Creates a new GridPane for the minimum level input and adds it to the GridPane
	    GridPane minLevelGrid = createMinLevelGrid();
	    grid.addRow(rowCount, minLevelGrid);
	    rowCount++;

	    // Copies the list of visited classes (visitedClasses) and clears the original list
	    List<String> copyVisitedClasses = new ArrayList<>(visitedClasses);
	    visitedClasses.clear();

	    // Copies the list of visited instances (visitedObjects) and clears the original list
	    List<String> copyVisitedObjects = new ArrayList<>(visitedObjects);
	    visitedObjects.clear();

	    // Iterates over the list of visited classes (copyVisitedClasses) and displays their instances
	    for (String metaClass : copyVisitedClasses) {
	        anyClassSelected(getFmmlxObjectByName(metaClass, diagram));
	    }

	    // Iterates over the list of visited instances (copyVisitedObjects) and displays their attributes, methods, and links
	    for (String instance : copyVisitedObjects) {
	        newInstanceSelected(getFmmlxObjectByName(instance, diagram));
	    }
	}

	/**
	 * Removes duplicates from a list of strings and returns a new list that contains only unique elements.
	 *
	 * @param objectLinks The list of strings from which duplicates should be removed.
	 * @param <T> The type of the list (not used in this method as it only works with strings).
	 * @return A new list that contains only unique elements from the original list.
	 */
	public static <T> List<String> removeDuplicates(List<String> objectLinks) 
	{
	    HashSet<String> set = new HashSet<>();
	    ArrayList<String> uniqueList = new ArrayList<>();
	
	    for (String element : objectLinks) {
	        if (set.add((String) element)) {
	            uniqueList.add((String) element);
	        }
	    }
	    return uniqueList;
	}
	
	public static boolean isDate(String potentialDate, String format)
	{
	    DateFormat dateFormat = new SimpleDateFormat(format);
	    dateFormat.setLenient(false);
	
	    try {
	        dateFormat.parse(potentialDate);
	        return true;
	    } catch (ParseException exception) {
	        return false;
	    }
	}

	
	public void show()
	{	
		stage.show();
	}
	
}


