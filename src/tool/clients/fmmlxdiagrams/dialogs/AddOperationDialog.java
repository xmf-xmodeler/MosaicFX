package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class AddOperationDialog extends Dialog<AddOperationDialog.Result> {
	private DialogPane dialogPane;
	private AbstractPackageViewer diagram;
	private FmmlxObject object;

	private TextField classTextField; 
	private ComboBox<Integer> levelComboBox;

	ObservableList<String> classList;
	private CodeBoxPair codeBoxPair;	
	
	private final String oldOpName;

	private Label statusLabel = new Label();

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
		setResizable(true);

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
		
		codeBoxPair.getBodyScrollPane().setMinHeight(200);
		codeBoxPair.getBodyScrollPane().setPrefHeight(200);
		codeBoxPair.getBodyScrollPane().setMaxHeight(750);
		codeBoxPair.getBodyScrollPane().setPrefHeight(100);
		
		codeBoxPair.getErrorTextArea().setMinHeight(40);
		codeBoxPair.getErrorTextArea().setMaxHeight(80);
		
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

		classTextField.setPrefWidth(150);
		levelComboBox.setPrefWidth(150);
		defaultOperationButton.setPrefWidth(150);

		GridPane theGrid = new GridPane();
		theGrid.add(new Label(StringValue.LabelAndHeaderTitle.aClass), 0, 0);
		theGrid.add(new Label(StringValue.LabelAndHeaderTitle.level), 0, 1);
		theGrid.add(new Label("Operation body"), 0, 2);
		theGrid.add(classTextField, 1, 0);
		theGrid.add(levelComboBox, 1, 1);
		theGrid.add(defaultOperationButton, 1, 2);
		theGrid.setHgap(5);
		theGrid.setVgap(5);
		
		VBox mainBox = new VBox(5, 
			theGrid, 
			codeBoxPair.getBodyScrollPane(),
			new Label("Parse result"),
			codeBoxPair.getErrorTextArea(),
			statusLabel
			);
		VBox.setVgrow(codeBoxPair.getBodyScrollPane(), Priority.ALWAYS);
		
		dialogPane.setContent(mainBox);
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
			statusLabel.setText(StringValue.ErrorMessage.selectLevel);
			return false;
		} else if (codeBoxPair.getBodyText().equals("")) {
			statusLabel.setText(StringValue.ErrorMessage.inputBody);
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
