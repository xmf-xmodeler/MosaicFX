package tool.clients.fmmlxdiagrams.dialogs.instance;

public interface ValueGenerator {
	public String getName();
	public void openDialog();
	public String generate();
	public int possibleGeneratedValue();
	public boolean fitsType(String type);
	public String getName2();
}
