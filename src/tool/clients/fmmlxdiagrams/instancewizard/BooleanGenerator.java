package tool.clients.fmmlxdiagrams.instancewizard;

import java.text.NumberFormat;
import java.util.Vector;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class BooleanGenerator extends Generator {	
	
	private VBox pane;
	private Slider pSlider;
	private Label pDisplay = new Label("50% false, 50% true");
	public static String name = "Probablistic (Boolean)";
	
	public BooleanGenerator(FmmlxAttribute att) {
		pSlider = new Slider(0, 1, .5);
		pSlider.setMajorTickUnit(.25);
		pSlider.setShowTickMarks(true);
		pane = new VBox(
			new Label("Choose probability for attribute " + att.getName() + " to be true:"),
			pSlider,
			pDisplay);
		pSlider.valueProperty().addListener((obs, oldVal, newVal)->{
			pDisplay.setText(newVal.doubleValue()==0?"always false":newVal.doubleValue()==1?"always true":(
				NumberFormat.getPercentInstance().format(1-newVal.doubleValue()) + " false, " + 
				NumberFormat.getPercentInstance().format(newVal.doubleValue()) + " true"));
		});
		pSlider.setValue(.5);
	}
	
	@Override
	public Node getEditorPane() {
		return pane;
	}

	@Override
	public String generate() {
		return (Math.random() < pSlider.getValue()) + "";
	}

	@Override
	public Vector<String> getProblems() { return new Vector<>(); }


}
