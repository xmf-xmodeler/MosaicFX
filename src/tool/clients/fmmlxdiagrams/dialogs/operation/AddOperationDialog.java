package tool.clients.fmmlxdiagrams.dialogs.operation;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AddOperationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.List;

public class AddOperationDialog extends CustomDialog<AddOperationDialogResult> {
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxObject object;

	private TextField classTextField; 
	private ComboBox<Integer> levelComboBox;
	private Button monitorButton;

	ObservableList<String> classList;
	private TextArea bodyTextArea;
	
	private ArrayList<Node> labelsNode;
	private List<Node> mainNodes;

	public AddOperationDialog(AbstractPackageViewer diagram, FmmlxObject object) {
		super();
		this.diagram = diagram;
		this.object = object;

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
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
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new AddOperationDialogResult(object, null, levelComboBox.getSelectionModel().getSelectedItem(), null,
						bodyTextArea.getText());

			}
			return null;
		});
	}

	private void layoutContent() {
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.newOperation);

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		levelComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, object.getLevel()));
		levelComboBox.getSelectionModel().selectLast();
		monitorButton = new Button("Monitor Operation Values");
		monitorButton.setOnAction(event -> {
		        resetOperationBody("op0", true);
		    }
		);
		bodyTextArea = new TextArea(StringValue.OperationStringValues.emptyOperation);
		Button checkSyntaxButton = new Button(StringValue.LabelAndHeaderTitle.checkSyntax);
		checkSyntaxButton.setOnAction(event -> AddOperationDialog.this.checkBodySyntax());
		checkSyntaxButton.setPrefWidth(COLUMN_WIDTH * 0.5);
		Button defaultOperationButton = new Button(StringValue.LabelAndHeaderTitle.defaultOperation);
		defaultOperationButton.setOnAction(event -> {
			AddOperationDialog.this.resetOperationBody("op0", false);
		});
		defaultOperationButton.setPrefWidth(COLUMN_WIDTH * 0.5);

		levelComboBox.setPrefWidth(COLUMN_WIDTH);

		labelsNode = new ArrayList<>();
		mainNodes = new ArrayList<>();
		
		labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.aClass));
		labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.level));
		labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.body));
		
		mainNodes.add(classTextField);
		mainNodes.add(levelComboBox);
		
		addNodesToGrid(labelsNode, 0);
		addNodesToGrid(mainNodes, 1);
		
		grid.add(bodyTextArea, 1, 2, 1, 4);
		grid.add(checkSyntaxButton, 0, 2);
		grid.add(defaultOperationButton, 0, 3);
		grid.add(monitorButton, 0, 4);
	}

		
	private void checkBodySyntax() {
		if (!isNullOrEmpty(bodyTextArea.getText()) && !bodyTextArea.getText().contentEquals(StringValue.OperationStringValues.emptyOperation)) {
			diagram.getComm().checkOperationBody(bodyTextArea.getText());
		}
	}

	private void resetOperationBody(String name, boolean monitor) {
		bodyTextArea.setText(
				"@Operation "+name
				+(monitor?"[monitor=true]":"")
				+"()"+":XCore::Element"+"\n" +
				"  null\n" +
				"end");
	}

	private boolean validateUserInput() {

	    if (levelComboBox.getSelectionModel().getSelectedIndex() == -1) {
			errorLabel.setText(StringValue.ErrorMessage.selectLevel);
			return false;
		} else if (bodyTextArea.getText().equals("")) {
			errorLabel.setText(StringValue.ErrorMessage.inputBody);
			return false;
		}
		return true;
	}
}
