package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import java.util.List;

public interface ValueGenerator {

	String getValueGeneratorName();

	void openDialog();

	void generate(int numberOfInstance);

	int possibleGeneratedInstance();

	boolean getFitsType(String type);

	String getName2();

	List<String> getParameter();

	void setParameter(List<String> parameter);

	List<String> getGeneratedValue();

}
