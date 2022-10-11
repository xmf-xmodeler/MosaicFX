package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.List;

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
	private CodeBoxPair codeBoxPair;	
	
	private ArrayList<Node> labelsNode;
	private List<Node> mainNodes;
	private final String oldOpName;

	public AddOperationDialog(AbstractPackageViewer diagram, FmmlxObject object) {
		this(diagram, object, null);
	}
	
	public AddOperationDialog(AbstractPackageViewer diagram, FmmlxObject object, FmmlxOperation oldOp) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.oldOpName = oldOp == null?null:oldOp.getName();

		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent(oldOp);
		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
		codeBoxPair.checkBodySyntax();
		setResult();		
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new Result(object, 
						levelComboBox.getSelectionModel().getSelectedItem(),
						codeBoxPair.getBodyText(),
						oldOpName);
						//bodyCodeBox.getBodyTextArea().getText());
			}
			return null;
		});
	}

	private void layoutContent(FmmlxOperation oldOp) {
		Button defaultOperationButton = new Button(StringValue.LabelAndHeaderTitle.defaultOperation);
		dialogPane.setHeaderText(oldOp == null?
				StringValue.LabelAndHeaderTitle.newOperation:
				StringValue.LabelAndHeaderTitle.changeOperationsBody);

		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		
		codeBoxPair = new CodeBoxPair(diagram,
				e->{getDialogPane().lookupButton(ButtonType.OK).setDisable(!codeBoxPair.getCheckPassed());},
				false);
		codeBoxPair.getErrorTextArea().setMinWidth(COLUMN_WIDTH*2);
		codeBoxPair.getErrorTextArea().setMaxHeight(100);
		codeBoxPair.getBodyScrollPane().setMaxHeight(300);
		codeBoxPair.getBodyScrollPane().setMinHeight(300);
		codeBoxPair.getErrorTextArea().setMinHeight(100);
		codeBoxPair.getBodyScrollPane().setMinWidth(COLUMN_WIDTH*2);
		
		if(oldOp == null) {
			levelComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, object.getLevel()));
			levelComboBox.getSelectionModel().selectLast();
			codeBoxPair.setBodyText(StringValue.OperationStringValues.emptyOperation);
			defaultOperationButton.setOnAction(event -> {
				AddOperationDialog.this.resetOperationBody("op0", false);});
		} else {
			levelComboBox = new ComboBox<>(FXCollections.observableArrayList(oldOp.getLevel()));
			levelComboBox.getSelectionModel().selectLast();
			codeBoxPair.setBodyText(oldOp.getBody());
			defaultOperationButton.setOnAction(event -> {
				codeBoxPair.setBodyText(oldOp.getBody());});
		}
//		monitorButton = new Button("Monitor Operation Values");
//		monitorButton.setOnAction(event -> {
//		        resetOperationBody("op0", true);
//		    }
//		);


		defaultOperationButton.setPrefWidth(COLUMN_WIDTH * 0.5);

		levelComboBox.setPrefWidth(COLUMN_WIDTH);

		labelsNode = new ArrayList<>();
		mainNodes = new ArrayList<>();
		
		labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.aClass));
		labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.level));
		//labelsNode.add(new Label(StringValue.LabelAndHeaderTitle.body));
		
		mainNodes.add(classTextField);
		mainNodes.add(levelComboBox);
		
		addNodesToGrid(labelsNode, 0);
		addNodesToGrid(mainNodes, 1);
		
		grid.setMinWidth(COLUMN_WIDTH*2);
		bodyLabel=new Label("Operation body");
		grid.add(bodyLabel, 0, 2);
		grid.add(codeBoxPair.getBodyScrollPane(), 0, 3);
		grid.add(defaultOperationButton, 1, 2);
		parseResLabel = new Label("Parse result");
		grid.add(parseResLabel, 0, 4);
		grid.add(codeBoxPair.getErrorTextArea(), 0, 5);
	}

	private void resetOperationBody(String name, boolean monitor) {
		codeBoxPair.setBodyText(
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
		} else if (codeBoxPair.getBodyText().equals("")) {
			errorLabel.setText(StringValue.ErrorMessage.inputBody);
			return false;
		}
		return true;
	}
	
	public class Result {

		public final FmmlxObject object;
		public final int level;
		public final String body;
		public final String name;

		public Result(FmmlxObject object, int level, String body, String name) {
			this.object = object;
			this.level = level;
			this.body = body;
			this.name = name;
		}
	}
}
