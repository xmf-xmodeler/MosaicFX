package tool.clients.fmmlxdiagrams.instancegenerator;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;

import java.util.HashMap;
import java.util.Vector;

public interface IInstanceGenerator {

    void openDialog(AbstractPackageViewer diagram);

    void generateName();

    void generateInstance(int instanceNumber, String name, int positionX, int positionY);

    HashMap<FmmlxAttribute, String> getSlotValuesMap(int instanceNumber);

    Vector<String> getParentNames();
}
