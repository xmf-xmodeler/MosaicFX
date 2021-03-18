package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.Vector;

import org.eclipse.swt.widgets.Link;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Edge;
import tool.clients.fmmlxdiagrams.Edge.Anchor;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.classbrowser.CustomStage.GridControl;
import tool.clients.fmmlxdiagrams.classbrowser.CustomStage.VBoxControl;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.OperationStringValues;



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
	private Button submitButton;

	private FmmlxLink link;
	AbstractPackageViewer diagram;
	Object object2;
	
	
	public ObjectBrowser(AbstractPackageViewer diagram, FmmlxObject metaClass, Object object2) {
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(3, 3, 3, 3));
			//this.container = new VBox();
			this.scene = new Scene(grid);
	//		this.gridControl = new GridControl();
		//	this.vBoxControl = new VBoxControl();
			this.stage = new Stage();
			this.stage.setScene(scene);
//			this.stage.setOnShowing(e-> onShow());
			this.metaClass = metaClass;
			this.diagram = diagram;
			stage.setTitle("Object Browser for "+this.metaClass.getName());
			stage.setWidth(500);
			stage.setHeight(300);
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
				
			rechteSeite = new ScrollPane();
			
			objectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> newInstanceSelected(newValue));
		
			updateObjectList();
			minLevelBox.valueProperty().addListener((e, oldText, newText) -> {updateObjectList();});
			maxLevelBox.valueProperty().addListener((e, oldText, newText) -> {updateObjectList();});
			directInstancesOnlyBox.setOnAction((e) -> {updateObjectList();});
	
			layout();
			
	}
	
	
	private void newInstanceSelected(FmmlxObject object) {
		
		GridPane rechteSeiteGrid = new GridPane();
		rechteSeiteGrid.setHgap(3);
		rechteSeiteGrid.setVgap(3);
		rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));
		rechteSeite.setContent(rechteSeiteGrid);
		int i=0;
		if(object.getAllSlots().size()>0) {
			rechteSeiteGrid.add(new Label("Slots:"), 0, i);
			i++;
		}
		for(FmmlxSlot slot: object.getAllSlots()) {
			TextField valueTextField = new TextField(slot.getValue());
			Button wertAendern = new Button("Submit");
			wertAendern.setOnAction((e)-> {
				diagram.getComm().changeSlotValue(diagram.getID(), object.getName(), slot.getName(), valueTextField.getText());;
			diagram.updateDiagram();
			});
			rechteSeiteGrid.add(new Label(slot.getName()), 0, i);
			rechteSeiteGrid.add(valueTextField, 1, i);
			rechteSeiteGrid.add(wertAendern, 2, i);
			i++;
		}
		if(object.getAllOperationValues().size()>0) {
			rechteSeiteGrid.add(new Label("Operations:"), 0, i);
			i++;
		}
		for(FmmlxOperationValue operationValue: object.getAllOperationValues()) {
			rechteSeiteGrid.add(new Label(operationValue.getName()), 0, i);
			rechteSeiteGrid.add(new TextField(operationValue.getValue()), 1, i);
			i++;
		}
		
	
		for(FmmlxLink link: diagram.getRelatedLinksByObject(object)) {
			FmmlxObject otherobject = link.getSourceNode() == object? link.getTargetNode(): link.getSourceNode();
			rechteSeiteGrid.add(new Label("Link: "+ otherobject.getName()), 0, i); //
			i++;
			for(FmmlxSlot slot: otherobject.getAllSlots()) {
				rechteSeiteGrid.add(new Label(slot.getName()), 0, i);
				rechteSeiteGrid.add( new TextField(slot.getValue()), 1, i);
				
				i++;
			}
		}

		
		/*für alle Links im Diagram, wenn der Source = object ist, dann mach irgendwas mit target;
		 *  wenn der target=newValue ist, mach etwas mit source
		stopf alle in einen Vektor rein=irgendwas;
		dann die gefundenden(im Vektor gespeicheren) Namen anzeigen
		später für alle gefundenen die Eigenschaften anzeigen
			 */	
	}


	private void updateObjectList() {

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
		objectListView.setItems(objectList);
		totalNoLabel.setText("total no.: "+ objectsVector.size());
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
		grid.add(rechteSeite, 2, 0,1,6);
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints());
		grid.getColumnConstraints().add(new ColumnConstraints(200, 300, Integer.MAX_VALUE, Priority.ALWAYS, HPos.LEFT, true));
	}
	
	
	public void show() {
		
stage.show();
	}

}
