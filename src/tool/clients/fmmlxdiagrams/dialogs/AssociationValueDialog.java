package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AssociationValueDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class AssociationValueDialog extends CustomDialog<AssociationValueDialogResult>{
	
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private DialogPane dialogPane;
	
	private Label selectAssociation;
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	
	private Label classALabel;
	private Label associationLabel;
	private Label classBLabel;
	
	private TextField classANameTextField;
	private TextField classBNameTextField;
	
	private ListView<FmmlxObject> classAListView;
	private ListView<String> associationListView;
	private ListView<FmmlxObject> classBListView;	
	
	private ArrayList<Node> classANodes;
	private List<Node> associationNodes;
	private List<Node> classBNodes;

	private ButtonType plusButtonType;
	private ButtonType minusButtonType;
	private ButtonType midlleButtonType;
	
	private Vector<FmmlxAssociation> associations;
	private Vector<FmmlxObject> metaClasses;
	private Vector<FmmlxObject> instancesA;
	private Vector<FmmlxObject> instancesB;
	
	public AssociationValueDialog(FmmlxDiagram diagram, FmmlxObject object) {
		this.diagram=diagram;
		this.object=object;
		
		dialogPane = getDialogPane();
		plusButtonType = new ButtonType("+");
		minusButtonType = new ButtonType("-");
		midlleButtonType = new ButtonType("-> ->");
		dialogPane.getButtonTypes().addAll(plusButtonType, minusButtonType, midlleButtonType, ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResult();
	}


	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				//TODO
			}
			return null;
		});
	}


	private boolean validateUserInput() {
		return false;
	}


	private void layoutContent() {
		associations = diagram.getAssociations();
		ObservableList<FmmlxAssociation> associationList;
		associationList = FXCollections.observableList(associations);
		
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.associationValue);
		
		selectAssociation = new Label(StringValueDialog.LabelAndHeaderTitle.selectAssociation);
		selectAssociationComboBox = (ComboBox<FmmlxAssociation>) initializeComboBox(associationList);
		selectAssociationComboBox.valueProperty().addListener((observable, oldValue,
				newValue) -> { 
					
					classANameTextField.setText(newValue.getSourceNode().getName());
					classBNameTextField.setText(newValue.getTargetNode().getName());
					
					instancesA = newValue.getSourceNode().getInstanceByLevel(newValue.getLevelStartToEnd());
					ObservableList<FmmlxObject> instanceOfClassA = FXCollections.observableList(instancesA); 
					classAListView = initializeListView(instanceOfClassA, SelectionMode.SINGLE);
					updateNodeInsideGrid(classAListView, classAListView, 0, 5);
					
					instancesB = newValue.getTargetNode().getInstanceByLevel(newValue.getLevelEndToStart());
					ObservableList<FmmlxObject> instanceOfClassB = FXCollections.observableList(instancesB); 
					classBListView = initializeListView(instanceOfClassB, SelectionMode.SINGLE);
					updateNodeInsideGrid(classBListView, classBListView, 2, 5);
					
				 
			});

		classALabel = new Label(StringValueDialog.LabelAndHeaderTitle.classALabel);
		associationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.association);
		classBLabel = new Label(StringValueDialog.LabelAndHeaderTitle.classBLabel);
		
		classANameTextField= new TextField();
		classANameTextField.setDisable(true);
		classBNameTextField = new TextField();
		classBNameTextField.setDisable(true);
		
		classAListView = initializeListView(null, SelectionMode.SINGLE);
		associationListView = initializeListViewAssociation(null, SelectionMode.SINGLE);
		classBListView= initializeListView(null, SelectionMode.SINGLE);
		
		classANodes = new ArrayList<>();
		associationNodes = new ArrayList<>();
		classBNodes = new ArrayList<>();
		
		classANodes.add(selectAssociation);
		classANodes.add(selectAssociationComboBox);
		classANodes.add(new Label(" "));
		classANodes.add(classALabel);
		classANodes.add(classANameTextField);
		classANodes.add(classAListView);
		
		associationNodes.add(new Label(" "));
		associationNodes.add(new Label(" "));
		associationNodes.add(new Label(" "));
		associationNodes.add(new Label(" "));
		associationNodes.add(associationLabel);
		associationNodes.add(associationListView);
		
		classBNodes.add(new Label(" "));
		classBNodes.add(new Label(" "));
		classBNodes.add(new Label(" "));
		classBNodes.add(classBLabel);
		classBNodes.add(classBNameTextField);
		classBNodes.add(classBListView);
		
		
		addNodesToGrid(classANodes, 0);
		addNodesToGrid(associationNodes, 1);
		addNodesToGrid(classBNodes, 2);
		
	}

}
