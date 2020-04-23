package tool.clients.fmmlxdiagrams.instancegenerator;

import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

import java.util.HashMap;
import java.util.Vector;

public interface IInstanceGenerator {

    void openDialog(FmmlxDiagram diagram);

    void generateName();

    void generateInstance(int instanceNumber, String name, int positionX, int positionY);

    HashMap<FmmlxAttribute, String> getSlotValuesMap(int instanceNumber);

    Vector<Integer> getParentIDs();
}
