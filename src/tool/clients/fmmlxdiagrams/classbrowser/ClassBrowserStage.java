package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;


public class ClassBrowserStage extends CustomStage {

	protected TextArea codeArea;
	protected ListView<String> modelListView,fmmlxObjectListView, fmmlxAttributeListView, 
						protocolListView, fmmlxOperationListView, fmmlxAssociationListView, slotListView;
	private ComboBox<String> abstractComboBox;
	private TextField objectBrowserTextField, operationInputTextField, operationOutputTexField, 
						associationBrowserTextField, attributeBrowserTextField, projectBrowserTextFied;
	
	protected StackPane root;
	protected VBox container, projectBrowserVBox, objectBrowserVBox, attributeBrowserVBox, abstractVBox,
						operationOutputVBox, operationInputVBox, associationBrowserVBox, consoleContainer;
	protected SplitPane outerSplitPane;
	protected GridPane classBrowserContainer, attributeGridpane;
	
	private FmmlxDiagram diagram;
	private FmmlxObject selectedObject;
	
	public ClassBrowserStage() {
		super();
		
		setTitle("Class Browser");
		
		initAllContainer();

		outerSplitPane = new SplitPane();
		outerSplitPane.setOrientation(Orientation.VERTICAL);
		outerSplitPane.getItems().addAll(classBrowserContainer, consoleContainer);
		
		container = new VBox();
		container.getChildren().addAll(outerSplitPane);
		
		VBox.setVgrow(outerSplitPane,Priority.ALWAYS);
		VBox.setVgrow(codeArea,Priority.ALWAYS);
		
		root = new StackPane(container);
		root.setPadding(new Insets(7));
		scene = new Scene(root);
		setScene(scene);
		
		setOnCloseRequest(e -> onClose());
	}

	private void onClose() {
		clearAll();
		hide();
	}

	private void clearAll() {
		fmmlxAssociationListView.getItems().clear();
		fmmlxAttributeListView.getItems().clear();
		fmmlxOperationListView.getItems().clear();
		fmmlxObjectListView.getItems().clear();
	}

	private void initAllContainer() {
		codeArea = new TextArea();
		consoleContainer= new VBox();
		consoleContainer.getChildren().add(codeArea);
		
		classBrowserContainer = new GridPane();
		attributeGridpane = new GridPane();
		classBrowserContainer.setHgap(10);
		classBrowserContainer.setVgap(8);
		classBrowserContainer.setPadding(new Insets(3, 3, 3, 3));
		
		ColumnConstraints cc;
		for (int i = 0; i < 6; i++) {
			cc = new ColumnConstraints();
			cc.setFillWidth(true);
			cc.setHgrow(Priority.ALWAYS);
			classBrowserContainer.getColumnConstraints().add(cc);
		}
		
		modelListView = new ListView<String>();
		modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				
				//First clear all ListView
				fmmlxObjectListView.getItems().clear();
				fmmlxAttributeListView.getItems().clear();
				protocolListView.getItems().clear();
				fmmlxOperationListView.getItems().clear();
				fmmlxAssociationListView.getItems().clear();
				
				//TODO if model selected
				//updateDiagram(newValue1);
			}
		});
		
		projectBrowserTextFied = new TextField();
		projectBrowserTextFied.textProperty().addListener((observable, oldValue, newValue) -> {
		    projectBrowserListerner(modelListView, oldValue, newValue);
		});
		projectBrowserVBox= joinNodeInVBox(new Label("Project :"), projectBrowserTextFied);
		
		fmmlxObjectListView = new ListView<String>();
		fmmlxObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				
				String[] parts = newValue1.split(" ");
				
				selectedObject=diagram.getObjectByName(parts[1]);
				
				fmmlxAttributeListView.getItems().clear();
				protocolListView.getItems().clear();
				fmmlxOperationListView.getItems().clear();
				fmmlxAssociationListView.getItems().clear();
				
				fmmlxAttributeListView.getItems().addAll(selectedObject.getAllAttributesString());
				fmmlxOperationListView.getItems().addAll(selectedObject.getAllOperationsString());
				fmmlxAssociationListView.getItems().addAll(selectedObject.getAllRelatedAssociationsString());
			}
		});
		
		objectBrowserTextField = new TextField();
		objectBrowserTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			clearAll();
			objectBrowserListerner(fmmlxObjectListView, diagram, oldValue, newValue);
		});
		abstractComboBox = new ComboBox<String>();
		objectBrowserVBox = joinNodeInVBox(new Label("class :"), objectBrowserTextField);
		abstractVBox = joinNodeInVBox(new Label("abstract :"), abstractComboBox);
		
		fmmlxAttributeListView = new ListView<String>();
		fmmlxAttributeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				//TODO if attribute selected
			}
		});
		
		slotListView = new ListView<String>();
		attributeBrowserTextField = new TextField();
		attributeBrowserVBox = joinNodeInVBox(new Label("class :"), attributeBrowserTextField);
		
		protocolListView = new ListView<String>();
		
		fmmlxOperationListView = new ListView<String>();
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
//			if (newValue1 != null) {
//				StringBuilder stringBuilder = new StringBuilder();
//				int paramLength= newValue1.getParamNames().size();
//				for(int i =0 ; i<paramLength;i++) {
//					stringBuilder.append(newValue1.getParamNames().get(i));
//					if(i!=paramLength-1) {
//						stringBuilder.append(", ");
//					}
//				}
//				operationInputTextField.setText(stringBuilder.toString());
//				operationOutputTexField.setText(newValue1.getType());
//			}
		});
		
		operationInputTextField = new TextField();
		operationOutputTexField = new TextField();
		operationOutputVBox = joinNodeInVBox(new Label("Output :"), operationOutputTexField);
		operationInputVBox = joinNodeInVBox(new Label("Input :"), operationInputTextField);
		
		fmmlxAssociationListView = new ListView<String>();
		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			FmmlxAssociation association = diagram.getAssociationByName(newValue1);
			
			if (association!=null) {
				if(association.getSourceNode().getName().equals(selectedObject.getName())){
					associationBrowserTextField.setText("("+association.getTargetNode().getLevel()+")"+" "+association.getTargetNode().getName());
				} else {
					associationBrowserTextField.setText("("+association.getSourceNode().getLevel()+")"+" "+association.getSourceNode().getName());
				}
			}
		});
		
		associationBrowserTextField = new TextField();
		associationBrowserTextField.setEditable(false);
		associationBrowserTextField.setDisable(true);
		associationBrowserVBox = joinNodeInVBox(new Label("with :"), associationBrowserTextField);
		 
		List<Node> modelNode = new ArrayList<Node>();
		modelNode.add(new Label(""));
		modelNode.add(new Label("Model"));
		modelNode.add(modelListView);
		modelNode.add(projectBrowserVBox);
		modelNode.add(new Label(""));
		modelNode.add(new Label("code"));
		
		List<Node> objectNode = new ArrayList<Node>();
		objectNode.add(new Label(""));
		objectNode.add(new Label("Objects"));
		objectNode.add(fmmlxObjectListView);
		objectNode.add(objectBrowserVBox);
		objectNode.add(abstractVBox);
		
		List<Node> attributeNode = new ArrayList<Node>();
		attributeNode.add(new Label(""));
		attributeNode.add(new Label("Attributes"));
		
		attributeGridpane.add(fmmlxAttributeListView, 0, 1);
		attributeGridpane.add(new Label("Slots"), 0	, 2);
		attributeGridpane.add(slotListView, 0, 3);
		
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(100);
	    attributeGridpane.getColumnConstraints().add(col1);
	    attributeGridpane.setVgap(8);
		
		attributeNode.add(attributeGridpane);
		attributeNode.add(attributeBrowserVBox);	
		
		List<Node> protocolNode = new ArrayList<Node>();
		protocolNode.add(new Label(""));
		protocolNode.add(new Label("Protocols"));
		protocolNode.add(protocolListView);
		
		List<Node> operationNode = new ArrayList<Node>();
		operationNode.add(new Label(""));
		operationNode.add(new Label("Operations"));
		operationNode.add(fmmlxOperationListView);
		operationNode.add(operationOutputVBox);
		operationNode.add(operationInputVBox);
		
		List<Node> associationNode = new ArrayList<Node>();
		associationNode.add(new Label(""));
		associationNode.add(new Label("Associations"));
		associationNode.add(fmmlxAssociationListView);
		associationNode.add(associationBrowserVBox);
		
		addNodesToGrid(classBrowserContainer,modelNode, 0);
		addNodesToGrid(classBrowserContainer,objectNode, 1);
		addNodesToGrid(classBrowserContainer,attributeNode, 2);
		addNodesToGrid(classBrowserContainer,protocolNode, 3);
		addNodesToGrid(classBrowserContainer,operationNode, 4);
		addNodesToGrid(classBrowserContainer,associationNode, 5);
	}

	public void updateDiagram(FmmlxDiagram diagram) {

		clearAll();
		this.diagram=diagram;
		
		for(FmmlxObject obj : diagram.getObjects()) {
			fmmlxObjectListView.getItems().add("("+obj.getLevel()+") "+obj.getName());
		}
		
	}
}
