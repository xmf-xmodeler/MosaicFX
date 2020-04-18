package tool.clients.fmmlxdiagrams.instancegenerator.dialog;


public interface ValueGeneratorDialog {
	
	public void setResult();
	public boolean inputIsValid();
	public void layoutContent();
	public boolean validateLogic(String attributeType);
	
}
