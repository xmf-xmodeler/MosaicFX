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
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;


public class ClassBrowserStage extends CustomStage {

	protected static TextArea codeArea;
	protected static ListView<String> modelListView;
	protected static ListView<FmmlxObject> fmmlxObjectListView;
	protected static ListView<FmmlxAttribute> fmmlxAttributeListView;
	protected static ListView<String> protocolListView;
	protected static ListView<FmmlxOperation> fmmlxOperationListView;
	protected static ListView<FmmlxAssociation> fmmlxAssociationListView;
	protected static ListView<String> slotListView;
	private ComboBox<String> abstractComboBox;
	private TextField objectBrowserTextField;
	private TextField operationInputTextField;
	private TextField operationOutputTexField;
	private TextField associationBrowserTextField;
	private TextField attributeBrowserTextField;
	private TextField projectBrowserTextFied;
	
	//Organizer
	protected StackPane root;
	protected VBox container;
	protected SplitPane outerSplitPane;
	protected VBox projectBrowserVBox;
	protected VBox objectBrowserVBox;
	protected VBox attributeBrowserVBox;
	protected VBox operationOutputVBox;
	protected VBox operationInputVBox;
	protected VBox associationBrowserVBox;
	protected VBox abstractVBox;
	protected GridPane classBrowserContainer;
	protected GridPane attributeGridpane;
	protected VBox consoleContainer;
	
	public ClassBrowserStage() {
		super();
		//this.diagram=diagram;
		
		setTitle("Class Browser");
		
		codeArea = new TextArea();
		
		initClassBrowserContainer();
		
		
		consoleContainer= new VBox();
		consoleContainer.getChildren().add(codeArea);

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
		hide();
	}

	private void initClassBrowserContainer() {
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
				//TODO if model selected
				//First clear all ListView
				fmmlxObjectListView.getItems().clear();
				fmmlxAttributeListView.getItems().clear();
				protocolListView.getItems().clear();
				fmmlxOperationListView.getItems().clear();
				fmmlxAssociationListView.getItems().clear();
			}
		});
		
		projectBrowserTextFied = new TextField();
		projectBrowserVBox= joinNodeInVBox(new Label("Project :"), projectBrowserTextFied);
		
		fmmlxObjectListView = new ListView<FmmlxObject>();
		fmmlxObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				//TODO if object selected
			}
		});
		
		objectBrowserTextField = new TextField();
		abstractComboBox = new ComboBox<String>();
		objectBrowserVBox = joinNodeInVBox(new Label("class :"), objectBrowserTextField);
		abstractVBox = joinNodeInVBox(new Label("abstract :"), abstractComboBox);
		
		fmmlxAttributeListView = new ListView<FmmlxAttribute>();
		fmmlxAttributeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				//TODO if model selected
			}
		});
		
		slotListView = new ListView<String>();
		attributeBrowserTextField = new TextField();
		attributeBrowserVBox = joinNodeInVBox(new Label("class :"), attributeBrowserTextField);
		
		protocolListView = new ListView<String>();
		
		fmmlxOperationListView = new ListView<FmmlxOperation>();
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				//TODO if model selected
			}
		});
		
		operationInputTextField = new TextField();
		operationOutputTexField = new TextField();
		operationOutputVBox = joinNodeInVBox(new Label("Output :"), operationInputTextField);
		operationInputVBox = joinNodeInVBox(new Label("Input :"), operationOutputTexField);
		
		fmmlxAssociationListView = new ListView<FmmlxAssociation>();
		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				//TODO if model selected
			}
		});
		
		associationBrowserTextField = new TextField();
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
//		
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
	
	private VBox joinNodeInVBox(Node node1, Node node2) {
		VBox result = new VBox();
		GridPane grid = new GridPane();
		grid.add(node1, 0, 0);
		grid.add(node2, 1, 0);
		
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(33);
	    ColumnConstraints col2 = new ColumnConstraints();
	    col2.setPercentWidth(68);

	    grid.getColumnConstraints().addAll(col1,col2);
	    
		result.getChildren().add(grid);
		return result;
	}

	public TextArea getCodeArea() {
		return codeArea;
	}

	public ListView<String> getModelListView() {
		return modelListView;
	}

	public ListView<FmmlxObject> getFmmlxObjectListView() {
		return fmmlxObjectListView;
	}

	public ListView<FmmlxAttribute> getFmmlxAttributeListView() {
		return fmmlxAttributeListView;
	}

	public ListView<String> getProtocolListView() {
		return protocolListView;
	}

	public ListView<FmmlxOperation> getFmmlxOperationListView() {
		return fmmlxOperationListView;
	}

	public ListView<FmmlxAssociation> getFmmlxAssociationListView() {
		return fmmlxAssociationListView;
	}

	public ListView<String> getSlotListView() {
		return slotListView;
	}

	public ComboBox<String> getAbstractComboBox() {
		return abstractComboBox;
	}

	public TextField getObjectBrowserTextField() {
		return objectBrowserTextField;
	}

	public TextField getOperationInputTextField() {
		return operationInputTextField;
	}

	public TextField getOperationOutputTexField() {
		return operationOutputTexField;
	}

	public TextField getAssociationBrowserTextField() {
		return associationBrowserTextField;
	}

	public TextField getAttributeBrowserTextField() {
		return attributeBrowserTextField;
	}

	public TextField getProjectBrowserTextFied() {
		return projectBrowserTextFied;
	}
}
