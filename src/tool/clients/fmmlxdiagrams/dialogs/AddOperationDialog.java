package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.AllValueList;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

public class AddOperationDialog extends Dialog<AddOperationDialog.Result> {
	private DialogPane dialogPane;
	private TabPane tabPane = new TabPane();	//ToDo Parameters?
	
	private AbstractPackageViewer diagram;
	private FmmlxObject object;

	private TextField classTextField; 
	private TextField umlFunctionSignature;	//only for umlMode
	private ComboBox<Integer> levelComboBox;
	private VBox mainBox;

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
			levelComboBox = new ComboBox<>(AllValueList.generateLevelListToThreshold(0, object.getLevel().getMinLevel()));
			levelComboBox.getSelectionModel().selectLast();
			codeBoxPair.setBodyText(StringValue.OperationStringValues.emptyOperation);
			defaultOperationButton.setOnAction(event -> {
				AddOperationDialog.this.codeBoxPair.bodyCodeBox.setText(oldOpName);});
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
		if(!diagram.isUMLMode()) {
		theGrid.add(new Label(StringValue.LabelAndHeaderTitle.aClass), 0, 0);
		theGrid.add(classTextField, 1, 0);
		theGrid.add(levelComboBox, 1, 1);
		theGrid.add(new Label(StringValue.LabelAndHeaderTitle.level), 0, 1);
		}
		theGrid.setHgap(5);
		theGrid.setVgap(5);
		theGrid.add(new Label("Operation body"), 0, 2);
		//theGrid.add(defaultOperationButton, 1, 2);
		

		if(!diagram.isUMLMode()) {	//Implementation of gui is different enough that it makes sense to keep the different methods for the layout
			layoutStandard(defaultOperationButton,theGrid);
		}
		else {
			layoutUML(defaultOperationButton,theGrid,oldOp);
		}
		
		VBox.setVgrow(codeBoxPair.getBodyScrollPane(), Priority.ALWAYS);

		dialogPane.setContent(mainBox);		//Code feld hinzugefuegt
	}
	
	private void layoutStandard(Button defaultOperationButton, GridPane theGrid) {
		mainBox = new VBox(5, 
				theGrid, 
				codeBoxPair.getBodyScrollPane(),
				new Label("Parse result"),
				codeBoxPair.getErrorTextArea(),
				defaultOperationButton,
				statusLabel
				);
	}
	
	private void layoutUML(Button defaultOperationButton, GridPane theGrid, FmmlxOperation oldOp) {
		umlFunctionSignature = new TextField();
		umlFunctionSignature.setPrefWidth(200);
		AddOperationDialog.this.codeBoxPair.setBodyText(
				"@Operation " + "methodName[monitor=true,delToClassAllowed=false]():XCore::Element" + "\n" +
				"null" + "\n" + "end");
		
		GridPane theGrid2 = new GridPane();
		theGrid2.add(umlFunctionSignature, 0, 0);
		
		VBox expertBox = new VBox(5,  
				codeBoxPair.getBodyScrollPane(),
				new Label("Parse result"),
				codeBoxPair.getErrorTextArea(),
				defaultOperationButton
				);
		
		Tab expertTab = new Tab("Expert Mode",expertBox);
		Tab normalModeTab = new Tab("Normal Mode",theGrid2);
		
		codeBoxPair.getBodyScrollPane().setOnKeyReleased(e -> {
			String[] codeBody;
			codeBody = AddOperationDialog.this.codeBoxPair.getBodyText().split("\n");	//split on line breaks should result in: [@Operation methodsiganture, body, body, body, etc., end]
			codeBody = codeBody[0].split(" ");
			 String signature = "";
			for(int i = 1;i<codeBody.length;i++) {			//recreates signature. Yes this important. No you cannot just do signature = codeBody[0]. the i = 1 skips @Operation
				signature = signature + codeBody[i];
			}
		
			umlFunctionSignature.setText(signature);
		});
		
		umlFunctionSignature.setOnKeyTyped(event -> {		//synchronise expert mode code with function signature
			String[] codeBody;
			codeBody = AddOperationDialog.this.codeBoxPair.getBodyText().split("\n");	//split on line breaks should result in: [@Operation methodsiganture, body, body, body, etc., end]
			String finalCode = "";
			String nextLine = "";
			for(int i = 0; i < codeBody.length;i++){
				if(i==0) {
					nextLine = "@Operation " + umlFunctionSignature.getText() +"\n";
				}
				else if (!codeBody[i].equals("end")) {
					nextLine = nextLine + codeBody[i] + "\n";
				}
				else {
					nextLine = nextLine + codeBody[i];
				}
			}
			finalCode = nextLine;
			codeBoxPair.setBodyText(finalCode);
		});
		

		if(oldOp!=null) {							//editing an existing operation
			AddOperationDialog.this.codeBoxPair.setBodyText(oldOp.getBody());
			String[] codeBody=oldOp.getBody().split("/n");
			codeBody = AddOperationDialog.this.codeBoxPair.getBodyText().split("\n");	//split on line breaks should result in: [@Operation methodsiganture, body, body, body, etc., end]
			codeBody = codeBody[0].split(" ");
			 String signature = "";
			for(int i = 1;i<codeBody.length;i++) {			//recreates signature. Yes this important. No you cannot just do signature = codeBody[0]. the i = 1 skips @Operation
				signature = signature + codeBody[i];
			}
			umlFunctionSignature.setText(signature);	//cannot just use old name because rest of signature would be missing then
		}
		else {
			umlFunctionSignature.setText("methodName" + "(parameter:String):Integer");		//default values for creating a new operation
		}
		
		
		tabPane.getTabs().addAll(normalModeTab,expertTab);
	mainBox = new VBox(5,  
			theGrid,
			tabPane,
			statusLabel
				);
	tabPane.setMinHeight(400);
	tabPane.setMinWidth(450);
	VBox.setVgrow(tabPane, Priority.ALWAYS);
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

	public void initAttributeSetter(FmmlxAttribute attribute) {
		this.levelComboBox.getSelectionModel().select(attribute.getLevel());
		String name = "get" + attribute.getName().substring(0,1).toUpperCase() + attribute.getName().substring(1);
		
		codeBoxPair.setBodyText(
				"@Operation "+name+"[monitor=false, getterKey=\""+attribute.getName()+"\"]()"+":"+attribute.getType()+"\n" +
				"  self."+attribute.getName()+"\n" +
				"end");
	}

	public void initAssociationSetter(
			String endName, 
			Integer endInstLevel,
			String typeName,
			Multiplicity endMult) {

		this.levelComboBox.getSelectionModel().select(endInstLevel);
		String name = "get" + endName.substring(0,1).toUpperCase() + endName.substring(1);
		if(!(endMult.upperLimit && endMult.max <=1)) name = name + "s";
		String type = (endMult.upperLimit && endMult.max <=1)?
				(typeName):
				("Set("+typeName+")");

		codeBoxPair.setBodyText(
				"@Operation "+name+"[monitor=false]()"+":"+type+"\n" +
				"  self."+endName+"\n" +
				"end");
	}
}
