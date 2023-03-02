package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Vector;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

public class IntegerEqualGenerator extends EqualGenerator {

	public static String name = "Equal (Integer)";

	public IntegerEqualGenerator(FmmlxAttribute att) {
		super(att, "0", "100");
	}
	
	@Override
	public String generate() {
		return "Integer::random(" + maxField.getText() + "-" + minField.getText() + ") + (" + minField.getText() + ")";
	}
	
	@Override
	public Vector<String> getProblems() {
		Vector<String> problems = new Vector<>();
		try{
			Integer.parseInt(minField.getText());
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Minimum is not a Double.");
		}
		try{
			Integer.parseInt(maxField.getText());
		} catch (NumberFormatException e) {
			problems.add("Generator for " + att.getName() + ": Maximum is not a Double.");
		}
		return problems;
	}
}
