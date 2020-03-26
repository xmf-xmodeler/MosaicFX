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
import tool.clients.fmmlxdiagrams.SortedValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.ValueList;
import tool.xmodeler.XModeler;


public class ModellBrowserStage extends CustomStage {

	private TextArea codeArea;
	private ListView<String> modelListView,fmmlxObjectListView, fmmlxAttributeListView, 
						fmmlxOperationListView, fmmlxAssociationListView, slotListView;
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
		super(StringValue.LabelAndHeaderTitle.modellBrowser, XModeler.getStage(), 1100, 800);		
		
		initAllElement();
		addAllElementToPane();			
		getContainer().getChildren().addAll(outerSplitPane);
		
		setOnCloseRequest(e -> onClose());
	}

	public void onClose() {
		clearAll(ClearSelectionMode.MODELL);
		hide();
	}

	private void clearAll(ClearSelectionMode mode) {	
		if (mode == ClearSelectionMode.MODELL) {
			fmmlxObjectListView.getItems().clear();
			classBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
		}
		
		if (mode == ClearSelectionMode.OBJECT || mode == ClearSelectionMode.MODELL) {
			fmmlxAttributeListView.getItems().clear();
			slotListView.getItems().clear();
			fmmlxOperationListView.getItems().clear();
			fmmlxAssociationListView.getItems().clear();
			
			attributeBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationInputTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationOutputTexField.setText(StringValue.LabelAndHeaderTitle.empty);
			associationBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			
			codeArea.clear();
		}
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
		
		String doubleDots = " :";
		abstractVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.abstractSmall+doubleDots), abstractComboBox);
		modellBrowserVBox= getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.project+doubleDots), modellBrowserTextFied);
		operationOutputVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.output+doubleDots), operationOutputTexField);
		operationInputVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.input+doubleDots), operationInputTextField);
		classBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.aClassSmall+doubleDots), classBrowserTextField);
		associationBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.withSmall+doubleDots), associationBrowserTextField);
		attributeBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.aClassSmall+doubleDots), attributeBrowserTextField);
		
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
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onOperationListViewNewValue(oldValue, newValue));
		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAssociationListViewNewValue(oldValue,newValue)); 
	}

	@Override
	protected void addAllElementToPane() {
		List<Node> modelNode = new ArrayList<Node>();
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.modell));
		modelNode.add(modelListView);
		modelNode.add(modellBrowserVBox);
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.code));
		
		List<Node> objectNode = new ArrayList<Node>();
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.objects));
		objectNode.add(fmmlxObjectListView);
		objectNode.add(classBrowserVBox);
		objectNode.add(abstractVBox);
		
		List<Node> attributeNode = new ArrayList<Node>();
		attributeNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		attributeNode.add(new Label(StringValue.LabelAndHeaderTitle.attributes));
		
		attributeGridpane.add(fmmlxAttributeListView, 0, 1);
		attributeGridpane.add(new Label(StringValue.LabelAndHeaderTitle.slots), 0	, 2);
		attributeGridpane.add(slotListView, 0, 3);
		
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(100);
	    attributeGridpane.getColumnConstraints().add(col1);
	    attributeGridpane.setVgap(8);
		
		attributeNode.add(attributeGridpane);
		attributeNode.add(attributeBrowserVBox);	

		List<Node> operationNode = new ArrayList<Node>();
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.operations));
		operationNode.add(fmmlxOperationListView);
		operationNode.add(operationOutputVBox);
		operationNode.add(operationInputVBox);
		
		List<Node> associationNode = new ArrayList<Node>();
		associationNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		associationNode.add(new Label(StringValue.LabelAndHeaderTitle.associations));
		associationNode.add(fmmlxAssociationListView);
		associationNode.add(associationBrowserVBox);
		
		getGridControl().addNodesToGrid(mainGridPane,modelNode, 0);
		getGridControl().addNodesToGrid(mainGridPane,objectNode, 1);
		getGridControl().addNodesToGrid(mainGridPane,attributeNode, 2);
		getGridControl().addNodesToGrid(mainGridPane,operationNode, 3);
		getGridControl().addNodesToGrid(mainGridPane,associationNode, 4);
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
			codeArea.setText(op.getBody());
		}
	}
	
	private void modellBrowserListerner(ListView<String> modelListView2, String oldValue, String newValue) {
		clearAll(ClearSelectionMode.MODELL);
		//TODO
	}

	private void classBrowserTextFieldListener(String oldValue, String newValue) {
		clearAll(ClearSelectionMode.OBJECT);
		fmmlxObjectListView.getItems().clear();
		
		for(FmmlxObject obj : diagram.getSortedObject(SortedValue.REVERSE)) {
			if(obj.getName().toLowerCase().contains(newValue.toLowerCase())) {
				fmmlxObjectListView.getItems().add("("+obj.getLevel()+") "+obj.getName());
			}
		}
	}

	private void onObjectListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			
			selectedObject=diagram.getObjectByName(newValue.split(" ")[1]);		
			clearAll(ClearSelectionMode.OBJECT);
			
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
				//TODO
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
		clearAll(ClearSelectionMode.MODELL);
		this.diagram=diagram;
		
		List<FmmlxObject> objects = diagram.getSortedObject(SortedValue.REVERSE);
		
		for(FmmlxObject obj : objects) {
			fmmlxObjectListView.getItems().add("("+obj.getLevel()+") "+obj.getName());
		}
	}
	
	private void onModelListViewNewValue(String oldValue, String newValue) {
		if (newValue != null) {
			
			clearAll(ClearSelectionMode.MODELL);
						
			//TODO if model selected			
			//updateDiagram(newValue1);
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
