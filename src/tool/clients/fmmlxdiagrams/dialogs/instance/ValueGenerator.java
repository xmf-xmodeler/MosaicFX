package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.List;

public interface ValueGenerator {
	public String getName();
	public void openDialog();
	public List<String> generate(int numberOfInstance);
	public int possibleGeneratedInstance();
	public boolean fitsType(String type);
	public String getName2();
	public List<String> getParameter();
	public void setParameter(List<String> parameter);
	public List<String> getValues();
}
