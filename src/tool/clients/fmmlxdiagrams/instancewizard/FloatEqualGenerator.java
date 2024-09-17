package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Vector;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class FloatEqualGenerator extends EqualGenerator {
	
	public static String name = "Equal (Float)";
	
	public FloatEqualGenerator(FmmlxAttribute att) {
		super(att, "0.0", "1.0");
	}

	@Override
	public String generate() {
		return "Float::random() * (" + maxField.getText() + "-" + minField.getText() + ") + (" + minField.getText() + ")";
	}
	
	@Override
	public Vector<String> getProblems() {
		Vector<String> problems = new Vector<>();
		try{
			Double m = Double.parseDouble(minField.getText());
			if(!Double.isFinite(m)) problems.add("Generator for " + att.getName() + ": Minimum is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Minimum is not a Double.");
		}
		try{
			Double d = Double.parseDouble(maxField.getText());
			if(!Double.isFinite(d)) problems.add("Generator for " + att.getName() + ": Maximum is not finite.");
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Maximum is not a Double.");
		}
		return problems;
	}
}
