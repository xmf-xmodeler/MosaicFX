package tool.clients.fmmlxdiagrams.instancewizard;

import java.text.NumberFormat;
import java.util.Vector;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class ListGenerator extends Generator {
	
	private VBox pane;
	public static String name = "Pick from List (XMF)";
	private TextArea exprField =   new TextArea();
	private TextArea evalResult0 = new TextArea("N/A");
	private TextArea evalResult1 = new TextArea("N/A");
	
	public ListGenerator(FmmlxAttribute att, final AbstractPackageViewer diagram) {

		pane = new VBox(
			new Label("Supply list for " + att.getName() + " to be chosen from randomly:"),
			new Label("Expression to be evaluated:"),
			new Label("must evaluate to a list, e.g \"[ , , ]\" or a global variable"),
			exprField,
			new Label("Test evaluation: "),
			new Label("This should show the list as \"Seq{...}\""),
			evalResult0,
			new Label("Evaluation Errors:"),
			evalResult1,
			new Label("WARNING:"),
			new Label("This feature fails if the test does "),
			new Label("not show the list as described"));
		
		exprField.textProperty().addListener((obs, oldVal, newVal) -> {
			diagram.getComm().evalString(diagram, newVal, (result) -> resultReceived(result));
		});
		
		exprField.setText("[\"yes\", \"no\", \"maybe\"] + 1.to(20)->select(p|p.isPrime())");
		
	}
	
	private void resultReceived(Vector<Object> result) {
		evalResult0.setText(result.get(0)+"");
		evalResult1.setText(result.get(1)+"");
	}

	@Override
	public String getName() { return name; }

	@Override
	public Node getEditorPane() {
		return pane;
	}

	@Override
	public String generate() {
		String list = "("+exprField.getText()+")";
		return list + ".at(Integer::random(" + list + ".size()-1));";
	}

	@Override
	public Vector<String> getProblems() {		 
		return new Vector<String>();
	}

}
