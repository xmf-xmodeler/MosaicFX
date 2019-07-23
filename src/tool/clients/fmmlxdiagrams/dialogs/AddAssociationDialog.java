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

import static tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;


public class AddAssociationDialog extends CustomDialog<AddAssociationDialogResult> {

	private FmmlxDiagram diagram;
	private FmmlxObject source;
	private FmmlxObject target;

	private FmmlxObject selectedTypeSource;
	private FmmlxObject selectedTypeTarget;

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

		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
	}

	private boolean validateUserInput() {
		return true;
	}

	private void layoutContent() {
		ArrayList<Node> labels = new ArrayList<>();
		labels.add(new Label(LabelAndHeaderTitle.type));
		labels.add(new Label(LabelAndHeaderTitle.instLevel));
		labels.add(new Label(LabelAndHeaderTitle.displayName));
		labels.add(new Label(LabelAndHeaderTitle.identifier));
		labels.add(new Label(LabelAndHeaderTitle.multiplicity));

		List<Node> sourceNodes = new ArrayList<>();

		typeSource = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());

		typeSource.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedTypeSource = newValue;
				setLevelList(instLevelSource, selectedTypeSource);
			}
		});

		instLevelSource = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		instLevelSource.setPrefWidth(COLUMN_WIDTH);
		instLevelSource.setEditable(true);
		displayNameSource = new TextField();
		identifierSource = new TextField();

		sourceNodes.add(typeSource);
		sourceNodes.add(instLevelSource);
		sourceNodes.add(displayNameSource);
		sourceNodes.add(identifierSource);
		sourceNodes.add(createMultiplicityBox(multiplicitySource));

		List<Node> targetNodes = new ArrayList<>();

		typeTarget = (ComboBox<FmmlxObject>) initializeComboBox(diagram.getAllPossibleParentList());
		typeTarget.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedTypeTarget = newValue;
				setLevelList(instLevelTarget, selectedTypeTarget);
			}
		});
		instLevelTarget = new ComboBox<>(LevelList.generateLevelListToThreshold(0, 5));
		instLevelTarget.setPrefWidth(COLUMN_WIDTH);
		instLevelTarget.setEditable(true);
		displayNameTarget = new TextField();
		identifierTarget = new TextField();
		targetNodes.add(typeTarget);
		targetNodes.add(instLevelTarget);
		targetNodes.add(displayNameTarget);
		targetNodes.add(identifierTarget);
		targetNodes.add(createMultiplicityBox(multiplicityTarget));

		addNodesToGrid(labels, 0);
		addNodesToGrid(sourceNodes, 1);
		addNodesToGrid(targetNodes, 2);

	}

	private Node createMultiplicityBox(Multiplicity multiplicity) {
		HBox multiplicityBox = new HBox();
		multiplicityBox.setPrefWidth(COLUMN_WIDTH);
		TextField textField = new TextField(multiplicity.toString());

		Button sourceMultiplicityButton = new Button(LabelAndHeaderTitle.change);
		sourceMultiplicityButton.setOnAction(e -> showMultiplicityDialog(multiplicity, textField));

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

	private void addNodesToGrid(List<Node> nodes, int columnIndex) {
		int counter = 0;
		for (Node node : nodes) {
			grid.add(node, columnIndex, counter);
			counter++;
		}
	}
}
