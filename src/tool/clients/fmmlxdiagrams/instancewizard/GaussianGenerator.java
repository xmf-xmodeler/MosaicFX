package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Random;
import java.util.Vector;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class GaussianGenerator extends Generator {	

	private VBox pane;
	private TextField meanField;
	private TextField devField;
	public static String name = "Gaussian";
	private static Random r = new Random();
	private FmmlxAttribute att;
	
	public GaussianGenerator(FmmlxAttribute att) {
		this.att = att;
		Label meanLabel = new Label("Mean:");
		Label devLabel = new Label("Standard Deviation:");
		meanField = new TextField("0.0");
		devField = new TextField("1.0");
		
		GridPane gridPane = new GridPane();

		gridPane.add(meanLabel, 0, 0);
		gridPane.add(meanField, 1, 0);
		gridPane.add(devLabel,  0, 1);
		gridPane.add(devField,  1, 1);
		
		pane = new VBox(
				new Label("Choose normal distribution for attribute " + att.getName() + ":"),
				gridPane);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Node getEditorPane() {		

		return pane;
	}

	@Override
	public String generate() {
		Double result = r.nextGaussian() * Double.parseDouble(devField.getText()) + Double.parseDouble(meanField.getText());
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
		return problems;
	}

}
