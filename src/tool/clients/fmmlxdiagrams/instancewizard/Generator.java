package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Vector;

import javafx.scene.Node;

public abstract class Generator {
	
	public abstract String getName();
	public abstract Node getEditorPane();
	public abstract String generate();
	public abstract Vector<String> getProblems();
	public final boolean isReady() {
		return getProblems().isEmpty();
	}
}
