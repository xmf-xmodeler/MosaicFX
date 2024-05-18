package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Random;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class FloatGaussianGenerator extends Generator {

	private VBox pane;
	private TextField meanField;
	private TextField devField;
	private TextField minField;
	private TextField maxField;
	public static String name = "Gaussian (Float)";
	private static Random r = new Random();
	private FmmlxAttribute att;
	
	public FloatGaussianGenerator(FmmlxAttribute att) {
		this.att = att;
		Label meanLabel = new Label("Mean:");
		Label devLabel = new Label("Standard Deviation:");
		Label minLabel = new Label("Lower Bound:");
		Label maxLabel = new Label("Upper Bound:");
		meanField = new TextField("0.0");
		devField = new TextField("1.0");
		minField = new TextField("-10.0");
		maxField = new TextField("10.0");
		
		GridPane gridPane = new GridPane();
		gridPane.setHgap(5.);
		gridPane.setVgap(5.);

		gridPane.add(meanLabel, 0, 0);
		gridPane.add(meanField, 1, 0);
		gridPane.add(devLabel,  0, 1);
		gridPane.add(devField,  1, 1);
		gridPane.add(minLabel,  0, 2);
		gridPane.add(minField,  1, 2);
		gridPane.add(maxLabel,  0, 3);
		gridPane.add(maxField,  1, 3);
		
		pane = new VBox(
				new Label("Choose normal distribution for attribute " + att.getName() + ":"),
				gridPane,
				new Label("As a Gaussian distribution may yield "),
				new Label("results from -Infinity to +Infinity,"),
				new Label("lower and upper bound should be provided.")
				);
		pane.setSpacing(5.);
		pane.setPadding(new Insets(5.));	
	}

	@Override
	public Node getEditorPane() {		
		return pane;
	}

	@Override
	public String generate() {
		Double result = null;
		while(result == null) {
			result = r.nextGaussian() * Double.parseDouble(devField.getText()) + Double.parseDouble(meanField.getText());
			if(result < Double.parseDouble(minField.getText()) || result > Double.parseDouble(maxField.getText())) {
				result = null;
			}
		}
		return result+"";
	}

	@Override
	public Vector<String> getProblems() {
		Vector<String> problems = new Vector<>();
		try{
			Double m = Double.parseDouble(meanField.getText());
			if(!Double.isFinite(m)) problems.add("Generator for " + att.getName() + ": Mean is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Mean is not a Double.");
		}
		try{
			Double d = Double.parseDouble(devField.getText());
			if(!Double.isFinite(d)) problems.add("Generator for " + att.getName() + ": Standard Deviation is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Standard Deviation is not a Double.");
		}
		try{
			Double d = Double.parseDouble(minField.getText());
			if(!Double.isFinite(d)) problems.add("Generator for " + att.getName() + ": Lower Bound is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Lower Bound is not a Double.");
		}
		try{
			Double d = Double.parseDouble(maxField.getText());
			if(!Double.isFinite(d)) problems.add("Generator for " + att.getName() + ": Upper Bound is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Upper Bound is not a Double.");
		}
		return problems;
	}

}
