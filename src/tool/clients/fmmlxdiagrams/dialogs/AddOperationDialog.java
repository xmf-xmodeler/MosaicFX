package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AddOperationDialog extends CustomDialog<AddOperationDialog.Result> {
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxObject object;

	private TextField classTextField; 
	private ComboBox<Integer> levelComboBox;
	private Label bodyLabel;
	private Label parseResLabel;
	//private Button monitorButton;

	ObservableList<String> classList;
	//private TextArea bodyTextArea;
	//private TextArea errorTextArea;
	private CodeBox bodyCodeBox;
	
	
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
		bodyCodeBox.checkBodySyntax();
		setResult();

	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new Result(object, 
						levelComboBox.getSelectionModel().getSelectedItem(),
						bodyCodeBox.bodyTextArea.getText());
			}
			return null;
		});
	}

	private void layoutContent() {
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.newOperation);

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		bodyCodeBox = new CodeBox(diagram,e->{
			getDialogPane().lookupButton(ButtonType.OK).setDisable(!bodyCodeBox.getCheckPassed());
		});
		bodyCodeBox.errorTextArea.setMinWidth(COLUMN_WIDTH*2);
		
		
		levelComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, object.getLevel()));
		levelComboBox.getSelectionModel().selectLast();
//		monitorButton = new Button("Monitor Operation Values");
//		monitorButton.setOnAction(event -> {
//		        resetOperationBody("op0", true);
//		    }
//		);
		bodyCodeBox.bodyTextArea.setText(StringValue.OperationStringValues.emptyOperation);
		//bodyTextArea = new TextArea(StringValue.OperationStringValues.emptyOperation);
		//bodyTextArea.textProperty().addListener((a,b,c) -> {getDialogPane().lookupButton(ButtonType.OK).setDisable(true);checkBodySyntax();});
		bodyCodeBox.bodyTextArea.setMinWidth(COLUMN_WIDTH*2);
		//Button checkSyntaxButton = new Button(StringValue.LabelAndHeaderTitle.checkSyntax);
		//checkSyntaxButton.setOnAction(event -> AddOperationDialog.this.checkBodySyntax());
		//checkSyntaxButton.setPrefWidth(COLUMN_WIDTH * 0.5);
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
		
		grid.setMinWidth(COLUMN_WIDTH*3);
		bodyLabel=new Label("Operation body");
		grid.add(bodyLabel, 0, 2);
		grid.add(bodyCodeBox.bodyTextArea, 0, 3);
		//grid.add(checkSyntaxButton, 0, 2);
		grid.add(defaultOperationButton, 1, 7);
		//grid.add(monitorButton, 0, 4);
		parseResLabel = new Label("Parse result");
		grid.add(bodyCodeBox.errorTextArea, 0, 5);
		
		//getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
	}

		
//	private void checkBodySyntax() {
//		ReturnCall<OperationException> returnCall = opException -> {
//			if(opException == null) {
//				getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
//				errorTextArea.setText("This operation compiles without parse/syntax error!");
//				errorTextArea.setStyle("-fx-text-fill: darkgreen;" +"-fx-font-weight: bold;");
//			} else {
//				errorTextArea.setText(opException.message);
//				errorTextArea.setStyle("-fx-text-fill: darkred;" +"-fx-font-weight: bold;");
//				errorTextArea.setWrapText(true);
//				//Alert alert = new Alert(AlertType.ERROR, opException.message, ButtonType.CLOSE);
//				//alert.showAndWait(); NOPE
//				//alert.show();
//			}
//		};
//		
//		diagram.getComm().checkSyntax(diagram, bodyTextArea.getText(), returnCall);
//	}
	
	private void resetOperationBody(String name, boolean monitor) {
		bodyCodeBox.bodyTextArea.setText(
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
		} else if (bodyCodeBox.bodyTextArea.getText().equals("")) {
			errorLabel.setText(StringValue.ErrorMessage.inputBody);
			return false;
		}
		return true;
	}
	
	public class Result {

		public final FmmlxObject object;
		public final int level;
		public final String body;

		public Result(FmmlxObject object, int level, String body) {
			this.object = object;
			this.level = level;
			this.body = body;
		}
	}
}
