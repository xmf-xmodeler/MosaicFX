package tool.clients.fmmlxdiagrams.dialogs.instance;

public interface ValueGenerator {
	public String getName();
	public void openDialog();
	public String generate();
	
	public boolean fitsType(String type);
}
