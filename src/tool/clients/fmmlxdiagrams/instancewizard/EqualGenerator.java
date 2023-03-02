package tool.clients.fmmlxdiagrams.instancewizard;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public abstract class EqualGenerator extends Generator {

	private VBox pane;
	protected TextField minField;
	protected TextField maxField;
	protected FmmlxAttribute att;
	
	public EqualGenerator(FmmlxAttribute att, String min, String max) {
		this.att = att;
		Label minLabel = new Label("Minimum (inclusive):");
		Label maxLabel = new Label("Maximum (inclusive):");
		minField = new TextField(min);
		maxField = new TextField(max);
		
		GridPane gridPane = new GridPane();
		gridPane.setHgap(5.);
		gridPane.setVgap(5.);

		gridPane.add(minLabel, 0, 0);
		gridPane.add(minField, 1, 0);
		gridPane.add(maxLabel,  0, 1);
		gridPane.add(maxField,  1, 1);
		
		pane = new VBox(
				new Label("Choose equal distribution for attribute " + att.getName() + ":"),
				gridPane);
		pane.setSpacing(5.);
		pane.setPadding(new Insets(5.));	
	}

	@Override
	public Node getEditorPane() {		
		return pane;
	}
}
