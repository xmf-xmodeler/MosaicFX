package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javafx.collections.ObservableList;
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
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.SortedValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.ValueList;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.XModeler;


public class ModelBrowser extends CustomStage {

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
	FmmlxDiagramCommunicator communicator;
	private AbstractPackageViewer activePackage;
	
	private HashMap<String,AbstractPackageViewer> models = new HashMap<>();
	
	public ModelBrowser(String project, String selectedModel, ObservableList<String> models) {
		super(StringValue.LabelAndHeaderTitle.modelBrowser+" " + project, XModeler.getStage(), 1100, 800);
		communicator = FmmlxDiagramCommunicator.getCommunicator();
		initAllElements();
		addAllElementToPane();			
		getContainer().getChildren().addAll(outerSplitPane);
		setOnCloseRequest(e -> onClose());
		modelListView.getItems().clear();
		modelListView.getItems().addAll(models);
		modelListView.getSelectionModel().getSelectedItems().clear();
		if (selectedModel!=null) {
		modelListView.getSelectionModel().select(selectedModel);
		}
	}

	public void onClose() {
		clearAll(ClearSelectionMode.MODEL);
		for (String key:models.keySet()) {
			communicator.closeDiagram(models.get(key).getID());
		}
		hide();
	}

	private void clearAll(ClearSelectionMode mode) {	
		if (mode == ClearSelectionMode.MODEL) {
			fmmlxObjectListView.getItems().clear();
			classBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
		}
		
		if (mode == ClearSelectionMode.OBJECT || mode == ClearSelectionMode.MODEL) {
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
	protected void initAllElements() {
		mainGridPane = new GridPane();
		attributeGridpane = new GridPane();
		mainGridPane.setHgap(10);
		mainGridPane.setVgap(8);
		mainGridPane.setPadding(new Insets(3, 3, 3, 3));
		setColumnConstrain(mainGridPane);

		modelListView = new ListView<>();
		fmmlxObjectListView = new ListView<>();
		fmmlxAttributeListView = new ListView<>();
		slotListView = new ListView<>();
		fmmlxAssociationListView = new ListView<>();
		fmmlxOperationListView = new ListView<>();
		
		modellBrowserTextFied = new TextField();
		classBrowserTextField = new TextField();
		attributeBrowserTextField = new TextField();
		operationInputTextField = new TextField();
		operationInputTextField.setEditable(false);
		operationOutputTexField = new TextField();
		operationOutputTexField.setEditable(false);
		associationBrowserTextField = new TextField();
		associationBrowserTextField.setEditable(false);
		
		abstractComboBox = new ComboBox<>(ValueList.booleanList);
		
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
		List<Node> modelNode = new ArrayList<>();
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.model));
		modelNode.add(modelListView);
		modelNode.add(modellBrowserVBox);
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.code));
		
		List<Node> objectNode = new ArrayList<>();
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.objects));
		objectNode.add(fmmlxObjectListView);
		objectNode.add(classBrowserVBox);
		objectNode.add(abstractVBox);
		
		List<Node> attributeNode = new ArrayList<>();
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

		List<Node> operationNode = new ArrayList<>();
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.operations));
		operationNode.add(fmmlxOperationListView);
		operationNode.add(operationOutputVBox);
		operationNode.add(operationInputVBox);
		
		List<Node> associationNode = new ArrayList<>();
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

	}
	
	private void modellBrowserListerner(ListView<String> modelListView2, String oldValue, String newValue) {
		
	}

	private void classBrowserTextFieldListener(String oldValue, String newValue) {
		
	}

	private void onObjectListViewNewValue(String oldValue, String newValue) {
		
	}

	private void onAssociationListViewNewValue(String oldValue, String newValue) {
		
	}

	private void onAbstractNewValue(Boolean oldValue, Boolean newValue) {
		
	}

	private void setColumnConstrain(GridPane gridPane) {
		
	}

	public void updateDiagram(FmmlxDiagram diagram) {
		
	}
	
	private void onModelListViewNewValue(String oldValue, String selectedPath) {
		if(!models.containsKey(selectedPath)) {
			Integer newDiagramID=communicator.createDiagram(selectedPath, "Test", "");
			ClassBrowserPackageViewer tempViewer = new ClassBrowserPackageViewer(communicator, newDiagramID, selectedPath);
			models.put(selectedPath, tempViewer);
		}
		activePackage = models.get(selectedPath);
		activePackage.updateDiagram();
		Vector<FmmlxObject> objects = activePackage.getObjects();
		Vector<String> names = new Vector<>();
		for (FmmlxObject o:objects) {
			names.add(o.getName());
		}
		fmmlxObjectListView.getItems().clear();
		fmmlxObjectListView.getItems().addAll(names);
	}	
	
	private void onSlotListViewNewValue(ListView<String> modelListView2, String oldValue, String newValue) {
		
	}
	
	private void onAttributeListViewNewValue(String oldValue, String newValue) {
		
	}

}
