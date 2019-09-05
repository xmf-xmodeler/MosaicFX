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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.EditAssociationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.ToolTip;

public class EditAssociationDialog extends CustomDialog<EditAssociationDialogResult> {
	

	private DialogPane dialogPane;
	private FmmlxDiagram diagram;
	private FmmlxAssociation selectedAssociation;
	private FmmlxObject source;	
	private FmmlxObject target;
	
	private ComboBox<FmmlxAssociation> selectAssociationComboBox;
	
	private Label typeSource;
	private Label typeTarget;
	private Label currentInstLevelSource;
	private Label currentInstLevelTarget;
	private Label currentDisplayNameSource;
	private Label currentDisplayNameTarget;
	private Label currentIdentifierSource;
	private Label currentIdentifierTarget;
	
	private TextField selectedObject;
	private ComboBox<FmmlxObject> newTypeSource;
	private ComboBox<FmmlxObject> newTypeTarget;
	private ComboBox<Integer> newInstLevelSource;
	private ComboBox<Integer> newInstLevelTarget;
	private TextField newDisplayNameSource;
	private TextField newDisplayNameTarget;
	private TextField newIdentifierSource;
	private TextField newIdentifierTarget;
	
	private Multiplicity multiplicitySource;
	private Multiplicity multiplicityTarget;
	
	private Node multiplicitySourceNode;
	private Node multiplicityTargetNode;

	private Vector<FmmlxAssociation> associations;
	
	private ArrayList<Node> labels;
	private List<Node> sourceNodes;
	private List<Node> targetNodes;

	public EditAssociationDialog(FmmlxDiagram diagram, FmmlxObject object) {
		
		this.diagram=diagram;
		this.source=object;
		multiplicitySource = Multiplicity.OPTIONAL;
		multiplicityTarget = Multiplicity.OPTIONAL;

		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		setClasses();
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
				return new EditAssociationDialogResult(selectedAssociation,
						source,
						target,
						getComboBoxIntegerValue(newInstLevelSource),
						getComboBoxIntegerValue(newInstLevelTarget),
						newDisplayNameSource.getText(),
						newDisplayNameTarget.getText(),
						newIdentifierSource.getText(),
						newIdentifierTarget.getText(),
						multiplicitySource,
						multiplicityTarget
				);
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if(selectAssociationComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAssociation);
			return false;
		} else if (newTypeSource.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewTypeSource);
			return false;
		} else if (newTypeTarget.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectNewTypeTarget);
			return false;
		} else if (!LevelList.generateLevelListToThreshold(0, newTypeSource.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelSource))) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAllowedLevelSource);
			return false;
		} else if (!LevelList.generateLevelListToThreshold(0, newTypeTarget.getSelectionModel().getSelectedItem().getLevel()).contains(getComboBoxIntegerValue(newInstLevelTarget))) {
			errorLabel.setText(StringValueDialog.ErrorMessage.selectAllowedLevelTarget);
			return false;
		} else if (!validateNewDisplayNameSource()) {
			return false;
		} else if (!validateNewIdentifierSource()) {
			return false;
		} else if (!validateNewIdentifierTarget()) {
			return false;
		}
		return true;
	}

	private boolean validateNewIdentifierTarget() {
		String name = newIdentifierTarget.getText();
		
		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidNameIdentifierTarget);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateNewIdentifierSource() {
		String name = newIdentifierSource.getText();
		
		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidNameIdentifierSource);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private boolean validateNewDisplayNameTarget() {
		String name = newDisplayNameTarget.getText();
		
		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidNameDisplayTarget);
			return false;
		} else if (name.trim() =="") {
			return true;
		}
		return false;
	}

	private boolean validateNewDisplayNameSource() {
		String name = newDisplayNameSource.getText();

		if (!InputChecker.getInstance().validateName(name)) {
			errorLabel.setText(StringValueDialog.ErrorMessage.enterValidNameDisplaySource);
			return false;
		} else {
			errorLabel.setText("");
			return true;
		}
	}

	private void layoutContent() {

		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.editAssociation);
		
		associations = source.getAllRelatedAssociations();
		ObservableList<FmmlxAssociation> associationList;
		associationList = FXCollections.observableList(associations);
		
		selectedObject = new TextField(source.getName());
		selectedObject.setDisable(true);
		typeSource = new Label(source.getName());
		typeSource.setDisable(true);
		typeTarget = new Label();
		typeTarget.setDisable(true);
		
		
		newTypeSource = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());
		newTypeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.source = newValue;
				setLevelList(newInstLevelSource, source);
				setIdentifier(newIdentifierSource, source.getName());
			}
		});
		newTypeTarget = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());
		newTypeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.target = newValue;
				setLevelList(newInstLevelTarget, target);
				setIdentifier(newIdentifierTarget, newValue.getName());
			}
		});
		newInstLevelSource = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		newInstLevelSource.setEditable(true);
		newInstLevelSource.getSelectionModel().select(0);
		newInstLevelTarget = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		newInstLevelTarget.setEditable(true);
		newInstLevelTarget.getSelectionModel().select(0);
		newInstLevelTarget.setEditable(true);
		newDisplayNameSource = new TextField();
		newDisplayNameTarget = new TextField();
		newDisplayNameTarget.setTooltip(new Tooltip(ToolTip.displayNameSource));
		newIdentifierSource = new TextField();
		newIdentifierTarget = new TextField();
		
		newTypeSource.setPrefWidth(COLUMN_WIDTH);
		newTypeTarget.setPrefWidth(COLUMN_WIDTH);
		newInstLevelSource.setPrefWidth(COLUMN_WIDTH);
		newInstLevelTarget.setPrefWidth(COLUMN_WIDTH);
		multiplicitySourceNode = createMultiplicityBox(multiplicitySource);
		multiplicityTargetNode = createMultiplicityBox(multiplicityTarget);
		
		selectAssociationComboBox = (ComboBox<FmmlxAssociation>) initializeComboBox(associationList);
		selectAssociationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {				
				selectedAssociation = newValue;
				FmmlxObject startNode = newValue.getSourceNode();
				FmmlxObject targetNode = newValue.getTargetNode();
				
				newTypeSource.getSelectionModel().select(startNode);				
				newTypeTarget.getSelectionModel().select(targetNode);
				
				newInstLevelSource.getSelectionModel().select(newValue.getLevelStartToEnd());
				newDisplayNameSource.setText(newValue.getName());
				newIdentifierSource.setText(newValue.getAccessNameStartToEnd());
				Multiplicity newMultiplicitySource = newValue.getMultiplicityStartToEnd();
				updateNodeInsideGrid(multiplicitySourceNode, createMultiplicityBox(newMultiplicitySource), 1, 8);
				
				newInstLevelTarget.getSelectionModel().select(newValue.getLevelEndToStart());
				newDisplayNameTarget.setText(newValue.getReverseName());
				newIdentifierTarget.setText(newValue.getAccessNameEndToStart());
				Multiplicity newMultiplicityTarget = newValue.getMultiplicityEndToStart();
				updateNodeInsideGrid(multiplicityTargetNode, createMultiplicityBox(newMultiplicityTarget), 2, 8);
				
			}
		});
		
		labels = new ArrayList<>();
		sourceNodes = new ArrayList<>();
		targetNodes = new ArrayList<>();
		
		labels.add(new Label(LabelAndHeaderTitle.selectedObject));
		labels.add(new Label(LabelAndHeaderTitle.selectAssociation));
		labels.add(new Label(" "));
		labels.add(new Label(" "));
		labels.add(new Label(LabelAndHeaderTitle.type));
		labels.add(new Label(LabelAndHeaderTitle.instLevel));
		labels.add(new Label(LabelAndHeaderTitle.displayName));
		labels.add(new Label(LabelAndHeaderTitle.identifier));
		labels.add(new Label(LabelAndHeaderTitle.multiplicity));
		
		sourceNodes.add(selectedObject);
		sourceNodes.add(selectAssociationComboBox);
		sourceNodes.add(new Label(" "));
		sourceNodes.add(new Label(LabelAndHeaderTitle.start));
		sourceNodes.add(newTypeSource);
		sourceNodes.add(newInstLevelSource);
		sourceNodes.add(newDisplayNameSource);
		sourceNodes.add(newIdentifierSource);
		sourceNodes.add(multiplicitySourceNode);
		
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label(" "));
		targetNodes.add(new Label (LabelAndHeaderTitle.end));
		targetNodes.add(newTypeTarget);
		targetNodes.add(newInstLevelTarget);
		targetNodes.add(newDisplayNameTarget);
		targetNodes.add(newIdentifierTarget);
		targetNodes.add(multiplicityTargetNode);

		
		addNodesToGrid(labels, 0);
		addNodesToGrid(sourceNodes, 1);
		addNodesToGrid(targetNodes, 2);
	}
	
	protected void updateNodeInsideGrid(Node oldNode, Node newNode, int column, int row) {
		grid.getChildren().remove(oldNode);
		grid.add(newNode, column, row);
	}

	
	private void setLevelList(ComboBox<Integer> comboBox, FmmlxObject refObject) {
		if (refObject != null) {
			comboBox.setItems(LevelList.generateLevelListToThreshold(0, refObject.getLevel()));
		}
	}
	
	private void setIdentifier(TextField textField, String name) {
		textField.setText(name.toLowerCase());
	}
	
	private void setClasses() {
		if (source != null) {
			newTypeSource.getSelectionModel().select(source);
		}
		if (target != null) {
			newTypeTarget.getSelectionModel().select(target);
		}
	}
	
	private Node createMultiplicityBox(Multiplicity multiplicity) {
		HBox multiplicityBox = new HBox();
		multiplicityBox.setPrefWidth(COLUMN_WIDTH);
		TextField textField = new TextField(multiplicity.toString());
		textField.setDisable(true);
		textField.setPrefWidth(COLUMN_WIDTH * 0.7);
		Button sourceMultiplicityButton = new Button(LabelAndHeaderTitle.change);
		sourceMultiplicityButton.setOnAction(e -> showMultiplicityDialog(multiplicity, textField));
		sourceMultiplicityButton.setPrefWidth(COLUMN_WIDTH * 0.3);

		multiplicityBox.getChildren().addAll(textField, sourceMultiplicityButton);

		return multiplicityBox;
	}
	
	private void showMultiplicityDialog(Multiplicity multiplicity, TextField textField) {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<MultiplicityDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			MultiplicityDialogResult result = opt.get();

			multiplicity = result.convertToMultiplicity();
			textField.setText(multiplicity.toString());

		}
	}

}
