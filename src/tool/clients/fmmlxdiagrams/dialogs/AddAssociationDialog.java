package tool.clients.fmmlxdiagrams.dialogs;


import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAssociationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.*;


public class AddAssociationDialog extends CustomDialog<AddAssociationDialogResult> {

	private FmmlxDiagram diagram;
	private FmmlxObject source;
	private FmmlxObject target;

	private Multiplicity multiplicitySource;
	private Multiplicity multiplicityTarget;

	private ComboBox<FmmlxObject> typeSource;
	private ComboBox<Integer> instLevelSource;
	private TextField displayNameSource;
	private TextField identifierSource;
	private ComboBox<FmmlxObject> typeTarget;
	private ComboBox<Integer> instLevelTarget;
	private TextField displayNameTarget;
	private TextField identifierTarget;
	private CheckBox sourceVisible;
	private CheckBox targetVisible;
	private CheckBox isSymmetric;
	private CheckBox isTransitive;

	public AddAssociationDialog(FmmlxDiagram diagram, FmmlxObject source, FmmlxObject target) {
		this.diagram = diagram;
		this.source = source;
		this.target = target;
		multiplicitySource = Multiplicity.OPTIONAL;
		multiplicityTarget = Multiplicity.OPTIONAL;


		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Add Association");

		layoutContent();
		setClasses();
		dialogPane.setContent(flow);
		setValidation();
		setResult();
	}

	private void layoutContent() {
		ArrayList<Node> labels = new ArrayList<>();
		labels.add(new Label());
		labels.add(new Label(LabelAndHeaderTitle.type));
		labels.add(new Label(LabelAndHeaderTitle.instLevel));
		labels.add(new Label(LabelAndHeaderTitle.displayName));
		labels.add(new Label(LabelAndHeaderTitle.identifier));
		labels.add(new Label(LabelAndHeaderTitle.multiplicity));
		labels.add(new Label(LabelAndHeaderTitle.visible));
		labels.add(new Label(LabelAndHeaderTitle.symmetric));
		labels.add(new Label(LabelAndHeaderTitle.transitive));


		List<Node> sourceNodes = new ArrayList<>();

		typeSource = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());

		typeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.source = newValue;
				setLevelList(instLevelSource, source);
				setIdentifier(identifierSource, source.getName());
			}
		});

		Label startLabel = new Label(LabelAndHeaderTitle.start);
		instLevelSource = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		instLevelSource.setPrefWidth(COLUMN_WIDTH);
		instLevelSource.setEditable(true);
		instLevelSource.getSelectionModel().select(0);
		displayNameSource = new TextField();
		identifierSource = new TextField();
		sourceVisible = new CheckBox();
		sourceVisible.setSelected(true);
		isSymmetric = new CheckBox();
		isTransitive = new CheckBox();
		sourceNodes.add(startLabel);
		sourceNodes.add(typeSource);
		sourceNodes.add(instLevelSource);
		sourceNodes.add(displayNameSource);
		sourceNodes.add(identifierSource);
		sourceNodes.add(createMultiplicityBox(multiplicitySource));
		sourceNodes.add(sourceVisible);
		sourceNodes.add(isSymmetric);
		sourceNodes.add(isTransitive);
		List<Node> targetNodes = new ArrayList<>();

		typeTarget = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());
		typeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.target = newValue;
				setLevelList(instLevelTarget, target);
				setIdentifier(identifierTarget, newValue.getName());
			}
		});
		instLevelTarget = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		instLevelTarget.setPrefWidth(COLUMN_WIDTH);
		instLevelTarget.setEditable(true);
		instLevelTarget.getSelectionModel().select(0);
		displayNameTarget = new TextField();
		displayNameTarget.setTooltip(new Tooltip(ToolTip.displayNameSource));
		identifierTarget = new TextField();
		targetVisible = new CheckBox();		
		targetVisible.setSelected(true);
		targetNodes.add(new Label(LabelAndHeaderTitle.end));
		targetNodes.add(typeTarget);
		targetNodes.add(instLevelTarget);
		targetNodes.add(displayNameTarget);
		targetNodes.add(identifierTarget);
		targetNodes.add(createMultiplicityBox(multiplicityTarget));
		targetNodes.add(targetVisible);
		addNodesToGrid(labels, 0);
		addNodesToGrid(sourceNodes, 1);
		addNodesToGrid(targetNodes, 2);
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
			typeSource.getSelectionModel().select(source);
		}
		if (target != null) {
			typeTarget.getSelectionModel().select(target);
		}
	}

	private void setValidation() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}

	private boolean validateUserInput() {
		InputChecker inputChecker = InputChecker.getInstance();
		if (inputChecker.isComboBoxItemNull(typeSource) || inputChecker.isComboBoxItemNull(typeTarget)) {
			errorLabel.setText(ErrorMessage.selectType);
			return false;
		}
		if (inputChecker.isComboBoxItemNull(instLevelSource) || inputChecker.isComboBoxItemNull(instLevelTarget)) {
			errorLabel.setText(ErrorMessage.selectLevel);
			return false;
		}
		if (inputChecker.isTextfieldEmpty(displayNameSource)) {
			errorLabel.setText(ErrorMessage.setDisplayName);
			return false;
		}
		

		return true;
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new AddAssociationDialogResult(
						source,
						target,
						getComboBoxIntegerValue(instLevelSource),
						getComboBoxIntegerValue(instLevelTarget),
						displayNameSource.getText(),
						displayNameTarget.getText(),
						identifierSource.getText(),
						identifierTarget.getText(),
						multiplicitySource,
						multiplicityTarget,
						sourceVisible.isSelected(),
						targetVisible.isSelected(),
						isSymmetric.isSelected(),
						isTransitive.isSelected()						
				);
			}
			return null;
		});
	}
}
