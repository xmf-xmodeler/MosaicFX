package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AssociationValueDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.EditAssociationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class AssociationValueDialog extends CustomDialog<AssociationValueDialogResult>{
	
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private DialogPane dialogPane;
	
	private Label classALabel;
	private Label associationLabel;
	private Label classBLabel;
	private Label selectClassALabel;
	private Label selectClassBLabel;
	
	private ComboBox<FmmlxObject> selectClassAComboBox;
	private ComboBox<FmmlxObject> selectClassBComboBox;
	
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
		if (selectClassAComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectMetaClassA);
			return false;
		} else if (selectClassBComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectMetaClassB);
			return false;
		} 
		return true;
	}


	private void layoutContent() {
		associations = diagram.getAssociations();
		ObservableList<FmmlxAssociation> associationList;
		associationList = FXCollections.observableList(associations);
		
		metaClasses = (Vector<FmmlxObject>) diagram.getAllMetaClass();
		ObservableList<FmmlxObject> metaClassList;
		metaClassList =FXCollections.observableList(metaClasses);
		
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.associationValue);
		
		selectClassALabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectMetaClassA);
		selectClassBLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectMetaClassB);
		
		selectClassAComboBox= (ComboBox<FmmlxObject>) initializeComboBox(metaClassList);
		selectClassAComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				instancesA = newValue.getInstance();
				
				ObservableList<FmmlxObject> instancesAList;
				instancesAList =FXCollections.observableList(instancesA);
				classAListView = initializeListView(instancesAList, SelectionMode.SINGLE);
				
				updateNodeInsideGrid(classAListView, classAListView, 0, 4);
				
				if (selectClassBComboBox.getSelectionModel().getSelectedItem()!=null) {
					associationListView = initializeListViewAssociation(diagram.getAssociationListToPair(
							classAListView.getSelectionModel().getSelectedItem(), classBListView.getSelectionModel().getSelectedItem()), 
							SelectionMode.SINGLE);
				}
			}
		});
		
		selectClassBComboBox= (ComboBox<FmmlxObject>) initializeComboBox(metaClassList);
		selectClassBComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				instancesB = newValue.getInstance();
				
				ObservableList<FmmlxObject> instancesBList;
				instancesBList =FXCollections.observableList(instancesB);
				classBListView = initializeListView(instancesBList, SelectionMode.SINGLE);
				
				updateNodeInsideGrid(classBListView, classBListView, 2, 4);
				
				
			}
		});
		
		classALabel = new Label(StringValueDialog.LabelAndHeaderTitle.classALabel);
		associationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.association);
		classBLabel = new Label(StringValueDialog.LabelAndHeaderTitle.classBLabel);
		
		classAListView = initializeListView(null, SelectionMode.SINGLE);
		associationListView = initializeListViewAssociation(null, SelectionMode.MULTIPLE);
		classBListView= initializeListView(null, SelectionMode.MULTIPLE);
		
		classANodes = new ArrayList<>();
		associationNodes = new ArrayList<>();
		classBNodes = new ArrayList<>();
		
		classANodes.add(selectClassALabel);
		classANodes.add(selectClassAComboBox);
		classANodes.add(new Label(" "));
		classANodes.add(classALabel);
		classANodes.add(classAListView);
		
		associationNodes.add(new Label(" "));
		associationNodes.add(new Label(" "));
		associationNodes.add(new Label(" "));
		associationNodes.add(associationLabel);
		associationNodes.add(associationListView);
		
		classBNodes.add(selectClassBLabel);
		classBNodes.add(selectClassBComboBox);
		classBNodes.add(new Label(" "));
		classBNodes.add(classBLabel);
		classBNodes.add(classBListView);
		
		
		addNodesToGrid(classANodes, 0);
		addNodesToGrid(associationNodes, 1);
		addNodesToGrid(classBNodes, 2);
		
	}

}
