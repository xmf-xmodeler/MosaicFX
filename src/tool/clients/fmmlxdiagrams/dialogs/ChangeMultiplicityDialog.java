package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog.LabelAndHeaderTitle;

public class ChangeMultiplicityDialog extends CustomDialog<MultiplicityDialogResult> {

	private final PropertyType type;
	private final FmmlxObject object;
	private Multiplicity multiplicity;

	private ComboBox<FmmlxAttribute> propertyComboBox;
	private ObservableList<FmmlxAttribute> attributes;

	private TextField multiplicityTextField;
	private List<Node> labelList;
	private List<Node> inputsList;


	public ChangeMultiplicityDialog(FmmlxObject object, PropertyType type) {
		this.object = object;
		this.type = type;

		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText(LabelAndHeaderTitle.changeMultiplicity + " of " + type.toString());

		layoutContent();
		dialogPane.setContent(flow);
		setValidation();
		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				switch (type) {

					case Attribute:
						return new MultiplicityDialogResult(object, propertyComboBox.getSelectionModel().getSelectedItem(),
								type, this.multiplicity);
					case Association:
						//TODO
					default:
						System.err.println("ChangeNameDialog: No matching content type!");
				}
			}
			return null;
		});
	}

	private boolean validateInput() {
		if (!validateSelectedAttribute()) {
			return false;
		}
		if (!validateNewMultiplicity()) {
			return false;
		}
		return true;
	}

	private boolean validateNewMultiplicity() {
		if (this.multiplicity != null) {
			return true;
		}
		errorLabel.setText(StringValueDialog.ErrorMessage.inputNewMultiplicity);
		return false;
	}

	private boolean validateSelectedAttribute() {
		if (propertyComboBox.getSelectionModel().getSelectedItem() != null) {
			return true;
		}
		errorLabel.setText(StringValueDialog.ErrorMessage.selectAttribute);
		return false;
	}

	private void layoutContent() {
		labelList = new ArrayList<>();
		inputsList = new ArrayList<>();

		Label classLabel = new Label(LabelAndHeaderTitle.aClass);
		labelList.add(classLabel);

		TextField className = new TextField(object.getName());
		className.setDisable(true);
		inputsList.add(className);

		Label propertyLabel = new Label(LabelAndHeaderTitle.select + type.toString());
		labelList.add(propertyLabel);

		switch (type) {
			case Association:
				layoutAssociation();
				break;
			case Attribute:
				layoutAttribute();
				break;
			default:
				errorLabel.setText("Invalid property type");
				break;
		}

		addNodesToGrid(labelList, 0);
		addNodesToGrid(inputsList, 1);
	}

	private void setValidation() {
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateInput()) {
				e.consume();
			}
		});
	}

	private void layoutAssociation() {
		errorLabel.setText("Association not implemented yet.");

		// TODO: implement layout
	}

	private void layoutAttribute() {
		attributes = object.getAllAttributesAsList();
		propertyComboBox = (ComboBox<FmmlxAttribute>) initializeComboBox(attributes);
		propertyComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				setMultiplicityTextField(newValue);
			}
		});
		inputsList.add(propertyComboBox);

		Label multiplicityLabel = new Label(LabelAndHeaderTitle.multiplicity);
		labelList.add(multiplicityLabel);

		Button changeButton = new Button(LabelAndHeaderTitle.change);
		changeButton.setOnAction(e -> showMultiplicityDialog());
		changeButton.setPrefWidth(COLUMN_WIDTH * 0.3);

		multiplicityTextField = new TextField();
		multiplicityTextField.setDisable(true);
		multiplicityTextField.setPrefWidth(COLUMN_WIDTH * 0.7);

		HBox multiplicityHBox = new HBox();
		multiplicityHBox.setMaxWidth(COLUMN_WIDTH);
		multiplicityHBox.getChildren().addAll(multiplicityTextField, changeButton);
		inputsList.add(multiplicityHBox);

	}

	private void showMultiplicityDialog() {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<MultiplicityDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			MultiplicityDialogResult result = opt.get();

			this.multiplicity = result.convertToMultiplicity();
			multiplicityTextField.setText(multiplicity.toString());
		}
	}

	private void setMultiplicityTextField(FmmlxAttribute attribute) {
		if (attribute.getMultiplicity() != null) {
			this.multiplicity = attribute.getMultiplicity();

			multiplicityTextField.setText(this.multiplicity.toString());
		}
	}

	public void setSelected(FmmlxProperty selectedProperty) {
		if (type == PropertyType.Attribute) {
			propertyComboBox.getSelectionModel().select((FmmlxAttribute) selectedProperty);
		}
	}
}

