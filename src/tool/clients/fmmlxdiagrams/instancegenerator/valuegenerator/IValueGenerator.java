package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

import java.util.List;

public interface IValueGenerator {

	String getValueGeneratorName();

	void openDialog(FmmlxDiagram fmmlxDiagram);

	void generate(int numberOfInstance);

	int possibleGeneratedInstance();

	boolean getFitsType(String type);

	String getName2();

	List<String> getParameter();

	void setParameter(List<String> parameter);

	List<String> getGeneratedValue();

}
