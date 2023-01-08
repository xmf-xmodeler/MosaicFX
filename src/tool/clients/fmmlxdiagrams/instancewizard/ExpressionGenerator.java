package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Vector;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class ExpressionGenerator extends Generator {
	
	private VBox pane;
	public static String name = "Evaluate Expression";
	private TextArea exprField =   new TextArea();
	private TextArea evalResult0 = new TextArea("N/A");
	private TextArea evalResult1 = new TextArea("N/A");
	
	public ExpressionGenerator(FmmlxAttribute att, final AbstractPackageViewer diagram) {

		pane = new VBox(
			new Label("Supply expression for " + att.getName() + " to be evaluated for each instance:"),
			new Label("Expression to be evaluated:"),
			new Label("must evaluate in a global context,"),
			new Label("should avoid side effects"),
			exprField,
			new Label("Test evaluation: "),
			new Label("This should show the evaluated expression."),
			new Label("Be aware that the value chosen may be "),
			new Label("different if using randomizers."),
			evalResult0,
			new Label("Evaluation Errors:"),
			evalResult1,
			new Label("WARNING:"),
			new Label("This feature fails if there are any evaluation errors."),
			new Label("Be aware that there may be other "),
			new Label("errors if using randomizers."));
		
		exprField.textProperty().addListener((obs, oldVal, newVal) -> {
			diagram.getComm().evalString(diagram, newVal, (result) -> resultReceived(result));
		});
		
		exprField.setText("Integer::random(20)");
		
	}
	
	private void resultReceived(Vector<Object> result) {
		evalResult0.setText(result.get(0)+"");
		evalResult1.setText(result.get(1)+"");
	}

	@Override
	public Node getEditorPane() {
		return pane;
	}

	@Override
	public String generate() {
		return exprField.getText();
	}

	@Override
	public Vector<String> getProblems() {		 
		return new Vector<String>();
	}

}
