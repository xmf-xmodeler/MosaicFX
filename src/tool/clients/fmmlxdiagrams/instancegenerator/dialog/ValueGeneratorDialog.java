package tool.clients.fmmlxdiagrams.instancegenerator.dialog;


import java.util.List;

public interface ValueGeneratorDialog {

	void setParameter(List<String> staticValue);
	void storeParameter();
	void setResult();
	boolean inputIsValid();
	void layoutContent();
	boolean validateLogic();
	
}
