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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue.LabelAndHeaderTitle;

public class AssociationDialog extends CustomDialog<AssociationDialog.Result> {

	private boolean editMode=false;
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxAssociation association;
	private FmmlxObject source;	
	private FmmlxObject target;
		
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	
	private TextField selectedObject;
	private ComboBox<FmmlxObject> newTypeSource;
	private ComboBox<FmmlxObject> newTypeTarget;
	private ComboBox<Integer> newInstLevelSource;
	private ComboBox<Integer> newInstLevelTarget;
	private TextField newDisplayName;
	private TextField newIdentifierSource;
	private TextField newIdentifierTarget;

	private CheckBox targetVisibleFromSourceBox;
	private CheckBox sourceVisibleFromTargetBox;
	private CheckBox symmetricBox;
	private CheckBox transitiveBox;
	
	private MultiplicityBox multTargetToSourceBox;
	private MultiplicityBox multSourceToTargetBox;

	private Vector<FmmlxAssociation> associations;
	
	private ArrayList<Node> labels;
	private List<Node> sourceNodes;
	private List<Node> targetNodes;
	
	public AssociationDialog(AbstractPackageViewer diagram, FmmlxAssociation association, boolean editMode) {
		
		this.diagram=diagram;
		this.association = association;
		this.editMode=editMode;
		
		this.source=association.getSourceNode();
		this.target=association.getTargetNode();
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		presetClassesInCombobox();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter();
		
		
		
	}

	public AssociationDialog(AbstractPackageViewer diagram, FmmlxObject source, FmmlxObject target, boolean editMode) {
		
		this.editMode=editMode;
		this.diagram = diagram;
		this.source = source;
		this.target = target;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Add Association");

		layoutContent();
		presetClassesInCombobox();
		dialogPane.setContent(flow);
		addValidationListener();
		setResultConverter();
	}
	
	public AssociationDialog(AbstractPackageViewer diagram, FmmlxObject source, boolean editMode) {
		
		this.editMode=editMode;
		this.diagram = diagram;
		this.source = source;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Add Association");

		layoutContent();
		presetClassesInCombobox();
		dialogPane.setContent(flow);
		addValidationListener();
		setResultConverter();
	}
	
	private void addValidationListener() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}

	private void layoutContent() {
		if(editMode) {
			dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.editAssociation);
		} else if (!editMode){
			dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.newAssociation);
		}
		
		associations = new Vector<>(); associations.add(association);//source.getAllRelatedAssociations();
		ObservableList<FmmlxAssociation> associationList;
		associationList = FXCollections.observableList(associations);
		
		if(selectedObject==null) {
			selectedObject = new TextField("");
			selectedObject.setDisable(true);
		} else {
			selectedObject = new TextField(source.getName());
			selectedObject.setDisable(true);	
		}
		
		newTypeSource =  initializeComboBox(diagram.getPossibleAssociationEnds());
		newTypeTarget =  initializeComboBox(diagram.getPossibleAssociationEnds());
		/*newTypeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.source = newValue;
				setLevelList(newInstLevelSource, source);
				setIdentifier(newIdentifierSource, source.getName());
			}
		});
		newTypeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.target = newValue;
				setLevelList(newInstLevelTarget, target);
				setIdentifier(newIdentifierTarget, newValue.getName());
			}
		});*/
		newInstLevelSource = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, 5));
		newInstLevelSource.setEditable(true);
//		newInstLevelSource.getSelectionModel().select(0);
		newInstLevelTarget = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, 5));
		newInstLevelTarget.setEditable(true);
//		newInstLevelTarget.getSelectionModel().select(0);
		newDisplayName = new TextField();
//		newDisplayNameTarget = new TextField();
//		newDisplayNameTarget.setTooltip(new Tooltip(ToolTip.displayNameSource));
		newIdentifierSource = new TextField();
		newIdentifierTarget = new TextField();
		
		newTypeSource.setPrefWidth(COLUMN_WIDTH);
		newTypeTarget.setPrefWidth(COLUMN_WIDTH);
		newInstLevelSource.setPrefWidth(COLUMN_WIDTH);
		newInstLevelTarget.setPrefWidth(COLUMN_WIDTH);
		multTargetToSourceBox = new MultiplicityBox();
		multSourceToTargetBox = new MultiplicityBox();
		
		sourceVisibleFromTargetBox = new CheckBox("sourceVisibleFromTarget");
		targetVisibleFromSourceBox = new CheckBox("targetVisibleFromSource");
		symmetricBox = new CheckBox("symmetric");
		transitiveBox = new CheckBox("transitive");
		
		selectAssociationComboBox = (ComboBox<FmmlxAssociation>) initializeComboBox(associationList);
		selectAssociationComboBox.getSelectionModel().selectFirst();
//		selectAssociationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
//			if (newValue != null) {				
//				selectedAssociation = newValue;
		if(association!=null) {
			FmmlxObject startNode = association.getSourceNode();
			FmmlxObject targetNode = association.getTargetNode();
			
			newTypeSource.getSelectionModel().select(startNode);				
			newTypeTarget.getSelectionModel().select(targetNode);
			newInstLevelSource.getSelectionModel().select(association.getLevelSource());
			newInstLevelTarget.getSelectionModel().select(association.getLevelTarget());
					
			newDisplayName.setText(association.getName());
			
			newIdentifierSource.setText(association.getAccessNameEndToStart());
			newIdentifierTarget.setText(association.getAccessNameStartToEnd());
			multTargetToSourceBox.setMultiplicity(association.getMultiplicityEndToStart());
			multSourceToTargetBox.setMultiplicity(association.getMultiplicityStartToEnd());
			sourceVisibleFromTargetBox.setSelected(association.isSourceVisible());	
			targetVisibleFromSourceBox.setSelected(association.isTargetVisible());		
			symmetricBox.setSelected(association.isSymmetric());		
			transitiveBox.setSelected(association.isTransitive());	
		} else {
			selectAssociationComboBox.setDisable(true);
			newTypeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					this.source = newValue;
					setLevelList(newInstLevelSource, source);
					setIdentifier(newIdentifierSource, source.getName());
				}
			});
			
			
			newTypeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					this.target = newValue;
					setLevelList(newInstLevelTarget, target);
					setIdentifier(newIdentifierTarget, newValue.getName());
				}
			});
			
			multTargetToSourceBox.setMultiplicity(Multiplicity.OPTIONAL);
			multSourceToTargetBox.setMultiplicity(Multiplicity.OPTIONAL);
		}
		
				
		
				
		
				
//				Multiplicity newMultiplicitySource = association.getMultiplicityStartToEnd();
//				updateNodeInsideGrid(multiplicitySourceNode, createMultiplicityBox(newMultiplicitySource), 1, 8);
//				
//				newDisplayNameTarget.setText(association.getReverseName());
//				Multiplicity newMultiplicityTarget = association.getMultiplicityEndToStart();
//				updateNodeInsideGrid(multiplicityTargetNode, createMultiplicityBox(newMultiplicityTarget), 2, 8);

					
//			}
//		});

		
		labels = new ArrayList<>();
		sourceNodes = new ArrayList<>();
		targetNodes = new ArrayList<>();
		
		labels.add(new Label(LabelAndHeaderTitle.displayName));
		labels.add(new Label(LabelAndHeaderTitle.selectedObject));
		labels.add(new Label(LabelAndHeaderTitle.selectAssociation));
		labels.add(new Label(" "));
		labels.add(new Label(" "));
		labels.add(new Label(LabelAndHeaderTitle.type));
		labels.add(new Label(LabelAndHeaderTitle.instLevel));
		labels.add(new Label(LabelAndHeaderTitle.identifier));
		labels.add(new Label(LabelAndHeaderTitle.multiplicity));
		labels.add(new Label("Visibility"));
		
		sourceNodes.add(newDisplayName);
		sourceNodes.add(selectedObject);
		sourceNodes.add(selectAssociationComboBox);
		sourceNodes.add(new Label(" "));
		sourceNodes.add(new Label(LabelAndHeaderTitle.start));
		sourceNodes.add(newTypeSource);
		sourceNodes.add(newInstLevelSource);
		sourceNodes.add(newIdentifierSource);
		sourceNodes.add(multTargetToSourceBox);
		sourceNodes.add(sourceVisibleFromTargetBox);
		sourceNodes.add(symmetricBox);
		sourceNodes.add(transitiveBox);
		
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label (LabelAndHeaderTitle.end));
		targetNodes.add(newTypeTarget);
		targetNodes.add(newInstLevelTarget);
		targetNodes.add(newIdentifierTarget);
		targetNodes.add(multSourceToTargetBox);
		targetNodes.add(targetVisibleFromSourceBox);

		
		addNodesToGrid(labels, 0);
		addNodesToGrid(sourceNodes, 1);
		addNodesToGrid(targetNodes, 2);
		
	}
	
	

	

	private boolean validateUserInput() {
		 if (newTypeSource.getSelectionModel().getSelectedItem()==null) {
				errorLabel.setText(StringValue.ErrorMessage.selectNewTypeSource);
				return false;
				} else if (newTypeTarget.getSelectionModel().getSelectedItem()==null) {
				errorLabel.setText(StringValue.ErrorMessage.selectNewTypeTarget);
				return false;
				}
		if (!validateNewDisplayName()) {
			return false;
			} else if (!validateNewIdentifierSource()) {
			return false;
			} else if (!validateNewIdentifierTarget()) {
			return false;
			}
		if(association!=null) {
			if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValue.ErrorMessage.selectAssociation);
			return false;
			}  else if (!AllValueList.generateLevelListToThreshold(0, newTypeSource.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelSource))) {
			errorLabel.setText(StringValue.ErrorMessage.selectAllowedLevelSource  + " Highest allowed level is: " + (association.getSourceNode().getLevel()-1));
			return false;
			} else if (!AllValueList.generateLevelListToThreshold(0, newTypeTarget.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelTarget))) {
			errorLabel.setText(StringValue.ErrorMessage.selectAllowedLevelTarget + " Highest allowed level is: " + (association.getTargetNode().getLevel()-1));
			return false;
			} 
		}else {
			  if (!AllValueList.generateLevelListToThreshold(0, newTypeSource.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelSource))) {
					errorLabel.setText(StringValue.ErrorMessage.selectAllowedLevelSource  + " Highest allowed level is: " + (source.getLevel()-1));
					return false;
					} else if (!AllValueList.generateLevelListToThreshold(0, newTypeTarget.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelTarget))) {
					errorLabel.setText(StringValue.ErrorMessage.selectAllowedLevelTarget + " Highest allowed level is: " + (target.getLevel()-1));
					return false;
					} 
			}
		return true;
		}
	
	private boolean validateNewIdentifierTarget() {
		String name = newIdentifierTarget.getText();
		
		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidNameIdentifierTarget);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateNewIdentifierSource() {
		String name = newIdentifierSource.getText();
		
		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidNameIdentifierSource);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateNewDisplayName() {
		String name = newDisplayName.getText();

		if (!InputChecker.validateName(name)) {
			errorLabel.setText(StringValue.ErrorMessage.enterValidNameDisplaySource);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}
	
	private void setLevelList(ComboBox<Integer> comboBox, FmmlxObject refObject) {
		if (refObject != null) {
			comboBox.setItems(AllValueList.generateLevelListToThreshold(0, refObject.getLevel()));
		}
	}
	
	private void setIdentifier(TextField textField, String name) {
		textField.setText(name.length() > 1 ? (name.substring(0, 1).toLowerCase() + name.substring(1)) : name.toLowerCase());
	}
	
	private void presetClassesInCombobox() {
		if (source != null) {
			newTypeSource.getSelectionModel().select(source);
		}
		if (target != null) {
			newTypeTarget.getSelectionModel().select(target);
		}
	}
	
	private void setResultConverter() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new Result(association,
						source,
						target,
						getComboBoxIntegerValue(newInstLevelSource),
						getComboBoxIntegerValue(newInstLevelTarget),
						newDisplayName.getText(),
						newIdentifierSource.getText(),
						newIdentifierTarget.getText(),
						multTargetToSourceBox.getMultiplicity(),
						multSourceToTargetBox.getMultiplicity(),
						sourceVisibleFromTargetBox.isSelected(),
						targetVisibleFromSourceBox.isSelected(),
						symmetricBox.isSelected(),
						transitiveBox.isSelected()
				);
			}
			return null;
		});		
	}		
	
	public class Result {
	
		public final FmmlxAssociation selectedAssociation;
		public final FmmlxObject source;
		public final FmmlxObject target;
		public final int newInstLevelSource;
		public final int newInstLevelTarget;
		public final String newDisplayName;
		public final String newIdentifierSource;
		public final String newIdentifierTarget;
		public final Multiplicity multTargetToSource;
		public final Multiplicity multSourceToTarget;
		public final boolean sourceVisibleFromTarget;
		public final boolean targetVisibleFromSource;
		public final boolean symmetric;
		public final boolean transitive;	
		
	public Result(FmmlxAssociation selectedAssociation, FmmlxObject source, FmmlxObject target, 
			Integer newInstLevelSource, Integer  newInstLevelTarget, 
			String  newDisplayName,  			
			String  newIdentifierSource, String  newIdentifierTarget,		
			Multiplicity multTargetToSource, Multiplicity multSourceToTarget,		
			boolean sourceVisibleFromTarget,
			boolean targetVisibleFromSource,
			boolean symmetric,
			boolean transitive) {
		
			this.selectedAssociation = selectedAssociation;
			this.source = source;
			this.target = target;
			this.newInstLevelSource = newInstLevelSource;
			this.newInstLevelTarget = newInstLevelTarget;
			this.newDisplayName = newDisplayName;
			this.newIdentifierSource = newIdentifierSource;
			this.newIdentifierTarget = newIdentifierTarget;
			this.multTargetToSource = multTargetToSource;
			this.multSourceToTarget = multSourceToTarget;
			this.sourceVisibleFromTarget = sourceVisibleFromTarget;
			this.targetVisibleFromSource = targetVisibleFromSource;
			this.symmetric = symmetric;
			this.transitive = transitive;
		}
	}
}	
