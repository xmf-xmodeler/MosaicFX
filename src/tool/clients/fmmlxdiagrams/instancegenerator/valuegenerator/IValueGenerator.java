package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;

import java.util.List;

public interface IValueGenerator {

	String getValueGeneratorName();

	void openDialog(AbstractPackageViewer fmmlxDiagram);

	void generate(int numberOfInstance);

	int possibleGeneratedInstance();

	boolean getFitsType(String type);

	String getName2();

	List<String> getParameter();

	void setParameter(List<String> parameter);

	List<String> getGeneratedValue();

}
