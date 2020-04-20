package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.List;

public interface ValueGenerator {

	public String getValueGeneratorName();

	public void openDialog();

	public void generate(int numberOfInstance);

	public int possibleGeneratedInstance();

	public boolean fitsType(String type);

	public String getName2();

	public List<String> getParameter();

	public void setParameter(List<String> parameter);

	public List<String> getGeneratedValue();

}
