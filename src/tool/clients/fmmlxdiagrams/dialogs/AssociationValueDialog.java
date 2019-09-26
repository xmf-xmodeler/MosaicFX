package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAssociationInstance;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AssociationValueDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class AssociationValueDialog extends CustomDialog<AssociationValueDialogResult>{
	
	private FmmlxDiagram diagram;
	private DialogPane dialogPane;
	
	private Label selectAssociation;
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	
	private Label classALabel;
	private Label associationLabel;
	private Label classBLabel;
	
	private TextField classANameTextField;
	private TextField classBNameTextField;
	
	private ListView<FmmlxObject> classAListView;
	private ListView<FmmlxAssociationInstance> associationListView;
	private ListView<FmmlxObject> classBListView;	
	
	private ArrayList<Node> classANodes;
	private List<Node> associationNodes;
	private List<Node> classBNodes;

	private ButtonType plusButtonType;
	private ButtonType minusButtonType;
	private ButtonType midlleButtonType;
	
	private Vector<FmmlxAssociation> associations;
	private Vector<FmmlxAssociationInstance> associationInstances;
	private Vector<FmmlxObject> instancesA;
	private Vector<FmmlxObject> instancesB;
	
	
	public AssociationValueDialog(FmmlxDiagram diagram) {
		this.diagram=diagram;
		
		dialogPane = getDialogPane();
		plusButtonType = new ButtonType("+");
		minusButtonType = new ButtonType("-");
		midlleButtonType = new ButtonType("Confirm Edit");
		dialogPane.getButtonTypes().addAll(plusButtonType, minusButtonType, midlleButtonType, ButtonType.CLOSE);
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button plusButton = (Button) getDialogPane().lookupButton(plusButtonType);
		plusButton.addEventFilter(ActionEvent.ACTION, e -> {
			if(validateAdd()) {
				addAssociationInstance(classAListView.getSelectionModel().getSelectedItem(), classBListView.getSelectionModel().getSelectedItem());
			}
			e.consume();
		});
		final Button minusButton = (Button) getDialogPane().lookupButton(minusButtonType);
		minusButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (validateRemove()) {
				removeAssociationInstance(associationListView.getSelectionModel().getSelectedItem());
			}
			e.consume();
		});
		final Button middleButton = (Button) getDialogPane().lookupButton(midlleButtonType);
		middleButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (validateChange()) {
				editAssociationInstance(associationListView.getSelectionModel().getSelectedItem(), 
						classAListView.getSelectionModel().getSelectedItem(),
						classBListView.getSelectionModel().getSelectedItem());
			}
			e.consume();
		});
	}

	private boolean validateAdd() {
		if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if(classAListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Select instance from class "+ classANameTextField.getText());
			return false;
		} else if(classBListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Select instance from class "+ classBNameTextField.getText());
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	
	private boolean validateRemove() {
		if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if(associationListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociationInstance);
			return false;
		}
		errorLabel.setText("");
		return true;
	}


	private boolean validateChange() {
		if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if(associationListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociationInstance);
			return false;
		} else if(classAListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Select instance from class "+ classANameTextField.getText());
			return false;
		} else if(classBListView.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Select instance from class "+ classBNameTextField.getText());
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private void addAssociationInstance(FmmlxObject startNode, FmmlxObject endNode){		
		diagram.getComm().addAssociationInstance(startNode.getId(), endNode.getId(), selectAssociationComboBox.getSelectionModel().getSelectedItem().id);
		diagram.updateDiagram();
		updateAssociationListView(selectAssociationComboBox.getSelectionModel().getSelectedItem());
	}
	
	private void removeAssociationInstance(FmmlxAssociationInstance selectedItem) {
		diagram.getComm().removeAssociationInstance(selectedItem.id);
		associationListView.getItems().remove(selectedItem);
		diagram.updateDiagram();
	}

	private void editAssociationInstance(FmmlxAssociationInstance selectedAssociationInstance, FmmlxObject newStartObject,
			FmmlxObject newEndObject) {
		diagram.getComm().updateAssociationInstance(selectedAssociationInstance.id, newStartObject.getId(), newEndObject.getId());
		selectedAssociationInstance.edit(newStartObject, newEndObject);
		updateAssociationListView(selectAssociationComboBox.getSelectionModel().getSelectedItem());
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	private void layoutContent() {
		associations = diagram.getAssociations();
		ObservableList<FmmlxAssociation> associationList;
		associationList = FXCollections.observableList(associations);
		
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.associationValue);
		
		selectAssociation = new Label(StringValueDialog.LabelAndHeaderTitle.selectAssociation);
		selectAssociationComboBox = (ComboBox<FmmlxAssociation>) initializeComboBox(associationList);
		selectAssociationComboBox.valueProperty().addListener((observable, oldValue,
				newValue) -> { 		
					classALabel.setText("Level : "+selectAssociationComboBox.getSelectionModel().getSelectedItem().getLevelStartToEnd());
					classBLabel.setText("Level : "+selectAssociationComboBox.getSelectionModel().getSelectedItem().getLevelEndToStart());
					refreshAllDialogElement(newValue); 
			});

		classALabel = new Label(" ");
		associationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.association);
		classBLabel = new Label(" ");
		
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
		classANodes.add(classANameTextField);
		classANodes.add(classALabel);
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
		classBNodes.add(classBNameTextField);
		classBNodes.add(classBLabel);
		classBNodes.add(classBListView);
		
		addNodesToGrid(classANodes, 0);
		addNodesToGrid(associationNodes, 1);
		addNodesToGrid(classBNodes, 2);
	}
	
	private void refreshAllDialogElement(FmmlxAssociation newValue) {
		classANameTextField.setText(newValue.getSourceNode().getName());
		classBNameTextField.setText(newValue.getTargetNode().getName());		
		updateMetaClassListView(newValue);
		updateAssociationListView(newValue);
	}
	
	private void updateAssociationListView(FmmlxAssociation newValue) {
		FmmlxAssociation association = (FmmlxAssociation) diagram.getAssociationById(newValue.getId());
		associationInstances = association.getInstance();
		ObservableList<FmmlxAssociationInstance> instanceOfAssociation = FXCollections.observableList(associationInstances);
		associationListView = initializeListViewAssociation(instanceOfAssociation, SelectionMode.SINGLE);
		updateNodeInsideGrid(associationListView, associationListView, 1, 5);
	}


	private void updateMetaClassListView(FmmlxAssociation newValue) {
		instancesA = newValue.getSourceNode().getInstancesByLevel(newValue.getLevelStartToEnd());
		ObservableList<FmmlxObject> instanceOfClassA = FXCollections.observableList(instancesA); 
		classAListView = initializeListView(instanceOfClassA, SelectionMode.SINGLE);
		updateNodeInsideGrid(classAListView, classAListView, 0, 5);
		
		instancesB = newValue.getTargetNode().getInstancesByLevel(newValue.getLevelEndToStart());
		ObservableList<FmmlxObject> instanceOfClassB = FXCollections.observableList(instancesB); 
		classBListView = initializeListView(instanceOfClassB, SelectionMode.SINGLE);
		updateNodeInsideGrid(classBListView, classBListView, 2, 5);
	}
	
}
