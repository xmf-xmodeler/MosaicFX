package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.ValueList;
import tool.xmodeler.XModeler;


public class ModellBrowserStage extends CustomStage {

	private TextArea codeArea;
	private ListView<String> modelListView,fmmlxObjectListView, fmmlxAttributeListView, 
						protocolListView, fmmlxOperationListView, fmmlxAssociationListView, slotListView;
	private ComboBox<Boolean> abstractComboBox;
	private TextField modellBrowserTextFied, classBrowserTextField, operationInputTextField, operationOutputTexField, 
						associationBrowserTextField, attributeBrowserTextField;
	private VBox modellBrowserVBox, classBrowserVBox, attributeBrowserVBox, abstractVBox,
						operationOutputVBox, operationInputVBox, associationBrowserVBox, consoleContainerVBox;
	private SplitPane outerSplitPane;
	private GridPane mainGridPane, attributeGridpane;	
	private FmmlxDiagram diagram;
	private FmmlxObject selectedObject;
	
	public ModellBrowserStage() {
		super("Modell Browser", XModeler.getStage(), 1100, 800);		
		
		initAllElement();
		addAllElementToPane();			
		getContainer().getChildren().addAll(outerSplitPane);
		
		setOnCloseRequest(e -> onClose());
	}

	private void onClose() {
		clearAll();
		hide();
	}

	private void clearAll() {
		fmmlxObjectListView.getItems().clear();
		fmmlxAttributeListView.getItems().clear();
		slotListView.getItems().clear();
		protocolListView.getItems().clear();
		fmmlxOperationListView.getItems().clear();
		fmmlxAssociationListView.getItems().clear();
		
		classBrowserTextField.setText("");
		attributeBrowserTextField.setText("");
		operationInputTextField.setText("");
		operationOutputTexField.setText("");
		associationBrowserTextField.setText("");
	}
	
	private void clearOnObjectSelection() {
		fmmlxAttributeListView.getItems().clear();
		slotListView.getItems().clear();
		protocolListView.getItems().clear();
		fmmlxOperationListView.getItems().clear();
		fmmlxAssociationListView.getItems().clear();
		
		attributeBrowserTextField.setText("");
		operationInputTextField.setText("");
		operationOutputTexField.setText("");
		associationBrowserTextField.setText("");
	}
	@Override
	protected void initAllElement() {
		mainGridPane = new GridPane();
		attributeGridpane = new GridPane();
		mainGridPane.setHgap(10);
		mainGridPane.setVgap(8);
		mainGridPane.setPadding(new Insets(3, 3, 3, 3));
		setColumnConstrain(mainGridPane);

		modelListView = new ListView<String>();
		fmmlxObjectListView = new ListView<String>();
		fmmlxAttributeListView = new ListView<String>();
		slotListView = new ListView<String>();
		protocolListView = new ListView<String>();
		fmmlxAssociationListView = new ListView<String>();
		fmmlxOperationListView = new ListView<String>();
		
		modellBrowserTextFied = new TextField();
		classBrowserTextField = new TextField();
		attributeBrowserTextField = new TextField();
		operationInputTextField = new TextField();
		operationInputTextField.setEditable(false);
		operationOutputTexField = new TextField();
		operationOutputTexField.setEditable(false);
		associationBrowserTextField = new TextField();
		associationBrowserTextField.setEditable(false);
		
		abstractComboBox = new ComboBox<Boolean>(ValueList.booleanList);
		
		codeArea = new TextArea();
		consoleContainerVBox= new VBox();
		consoleContainerVBox.getChildren().add(codeArea);
		
		outerSplitPane = new SplitPane();
		outerSplitPane.setOrientation(Orientation.VERTICAL);
		outerSplitPane.getItems().addAll(mainGridPane, consoleContainerVBox);
		
		VBox.setVgrow(outerSplitPane,Priority.ALWAYS);
		VBox.setVgrow(codeArea,Priority.ALWAYS);
		
		abstractVBox = getVBoxControl().joinNodeInVBox(new Label("abstract :"), abstractComboBox);
		modellBrowserVBox= getVBoxControl().joinNodeInVBox(new Label("Project :"), modellBrowserTextFied);
		operationOutputVBox = getVBoxControl().joinNodeInVBox(new Label("Output :"), operationOutputTexField);
		operationInputVBox = getVBoxControl().joinNodeInVBox(new Label("Input :"), operationInputTextField);
		classBrowserVBox = getVBoxControl().joinNodeInVBox(new Label("class :"), classBrowserTextField);
		associationBrowserVBox = getVBoxControl().joinNodeInVBox(new Label("with :"), associationBrowserTextField);
		attributeBrowserVBox = getVBoxControl().joinNodeInVBox(new Label("class :"), attributeBrowserTextField);
		
		modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onModelListViewNewValue(oldValue, newValue));
		modellBrowserTextFied.textProperty().addListener((observable, oldValue, newValue) 
				-> modellBrowserListerner(modelListView, oldValue, newValue));
		fmmlxObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onObjectListViewNewValue(oldValue, newValue));	
		classBrowserTextField.textProperty().addListener((observable, oldValue, newValue) 
				-> classBrowserTextFieldListener(oldValue, newValue));
		abstractComboBox.valueProperty().addListener((observable, oldValue, newValue)
				-> onAbstractNewValue(oldValue, newValue));
		fmmlxAttributeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAttributeListViewNewValue(oldValue, newValue));
		slotListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onSlotListViewNewValue(modelListView, oldValue, newValue));
		protocolListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onProtocolListViewNewValue(oldValue, newValue));
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onOperationListViewNewValue(oldValue, newValue));
		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAssociationListViewNewValue(oldValue,newValue)); 
	}

	@Override
	protected void addAllElementToPane() {
		List<Node> modelNode = new ArrayList<Node>();
		modelNode.add(new Label(""));
		modelNode.add(new Label("Model"));
		modelNode.add(modelListView);
		modelNode.add(modellBrowserVBox);
		modelNode.add(new Label(""));
		modelNode.add(new Label("code"));
		
		List<Node> objectNode = new ArrayList<Node>();
		objectNode.add(new Label(""));
		objectNode.add(new Label("Objects"));
		objectNode.add(fmmlxObjectListView);
		objectNode.add(classBrowserVBox);
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
		
		getGridControl().addNodesToGrid(mainGridPane,modelNode, 0);
		getGridControl().addNodesToGrid(mainGridPane,objectNode, 1);
		getGridControl().addNodesToGrid(mainGridPane,attributeNode, 2);
		getGridControl().addNodesToGrid(mainGridPane,protocolNode, 3);
		getGridControl().addNodesToGrid(mainGridPane,operationNode, 4);
		getGridControl().addNodesToGrid(mainGridPane,associationNode, 5);
	}

	private void onOperationListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {		
			FmmlxOperation op = selectedObject.getOperationByName(newValue.split(" ")[0]);
			StringBuilder stringBuilder = new StringBuilder();
			if(op!=null) {
				if(op.getParamTypes()!=null) {
					int paramLength= op.getParamTypes().size();
					
					for(int i =0 ; i<paramLength;i++) {			
						stringBuilder.append(op.getParamTypes().get(i).split("::")[2]);
						if(i!=paramLength-1) {
							stringBuilder.append(", ");
						}
					}
				}
			}					
			operationInputTextField.setText(stringBuilder.toString());
			operationOutputTexField.setText(op.getType().split("::")[2]);
		}
	}
	
	private void modellBrowserListerner(ListView<String> modelListView2, String oldValue, String newValue) {
		clearAll();
		//TODO
		
	}

	private void classBrowserTextFieldListener(String oldValue, String newValue) {
		clearOnObjectSelection();
		fmmlxObjectListView.getItems().clear();
		for(FmmlxObject tmp : diagram.getObjects()) {
			if(tmp.getName().contains(newValue)) {
				fmmlxObjectListView.getItems().add("("+tmp.getLevel()+") "+tmp.getName());
			}
		}
	}

	private void onObjectListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			
			selectedObject=diagram.getObjectByName(newValue.split(" ")[1]);		
			clearOnObjectSelection();
			
			fmmlxAttributeListView.getItems().addAll(selectedObject.getAllAttributesString());
			slotListView.getItems().addAll(selectedObject.getAllSlotString());
			fmmlxOperationListView.getItems().addAll(selectedObject.getAllOperationsString());
			fmmlxAssociationListView.getItems().addAll(selectedObject.getAllRelatedAssociationsString());
			abstractComboBox.setValue(selectedObject.isAbstract());
		}
	}

	private void onAssociationListViewNewValue(String oldValue, String newValue) {
		FmmlxAssociation association = diagram.getAssociationByName(newValue);	
		if (association!=null) {
			if(association.getSourceNode().getName().equals(selectedObject.getName())){
				associationBrowserTextField.setText("("+association.getTargetNode().getLevel()+")"+" "+association.getTargetNode().getName());
			} else {
				associationBrowserTextField.setText("("+association.getSourceNode().getLevel()+")"+" "+association.getSourceNode().getName());
			}
		}
	}

	private void onAbstractNewValue(Boolean oldValue, Boolean newValue) {
		if(newValue != null) {
			if(newValue!=oldValue) {
				diagram.getComm().setClassAbstract(diagram, selectedObject.getId(), !selectedObject.isAbstract());
				diagram.updateDiagram();
			}
		}
	}

	private void setColumnConstrain(GridPane gridPane) {
		ColumnConstraints cc;
		for (int i = 0; i < 6; i++) {
			cc = new ColumnConstraints();
			cc.setFillWidth(true);
			cc.setHgrow(Priority.ALWAYS);
			gridPane.getColumnConstraints().add(cc);
		}
	}

	public void updateDiagram(FmmlxDiagram diagram) {
		clearAll();
		this.diagram=diagram;
		
		for(FmmlxObject obj : diagram.getObjects()) {
			fmmlxObjectListView.getItems().add("("+obj.getLevel()+") "+obj.getName());
		}
	}
	
	private void onModelListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			
			clearAll();
						
			//TODO if model selected			
			//updateDiagram(newValue1);
		}
	}	

	private void onProtocolListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			// TODO if protocol selected
		}
	}
	
	private void onSlotListViewNewValue(ListView<String> modelListView2, String oldValue, String newValue) {
		if (newValue != null) {
			// TODO if slotValue selected
		}
	}
	
	private void onAttributeListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			//TODO if attribute selected
		}
	}

}
