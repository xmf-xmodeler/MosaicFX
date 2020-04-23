package tool.clients.fmmlxdiagrams.instancegenerator.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.*;

public class TypeList {

    public static ObservableList<ValueGenerator> getGenerateTypeList(String type) {
        switch (type) {
            case "Integer": {
                ValueGeneratorIncrement incG = new ValueGeneratorIncrement(type);
                ValueGeneratorStatic sGInt = new ValueGeneratorStatic(type);
                ValueGeneratorList listGInt = new ValueGeneratorList(type);
                ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution(type);
                ValueGeneratorRandom rGenerator = new ValueGeneratorRandom(type);
                return FXCollections.observableArrayList(incG, sGInt, listGInt, nDGenerator, rGenerator);
            }
            case "Float": {
                ValueGeneratorIncrement incF = new ValueGeneratorIncrement(type);
                ValueGeneratorStatic sGFloat = new ValueGeneratorStatic(type);
                ValueGeneratorList listGFloat = new ValueGeneratorList(type);
                ValueGeneratorRandom rGenerator = new ValueGeneratorRandom(type);
                ValueGeneratorNormalDistribution nDGenerator = new ValueGeneratorNormalDistribution(type);
                return FXCollections.observableArrayList(incF, sGFloat, listGFloat, nDGenerator, rGenerator);
            }
            case "String":
                ValueGeneratorStatic sString = new ValueGeneratorStatic(type);
                ValueGeneratorList listString = new ValueGeneratorList(type);
                return FXCollections.observableArrayList(sString, listString);
            case "Boolean": {
                ValueGeneratorStatic sGBoolean = new ValueGeneratorStatic(type);
                ValueGeneratorList listGBoolean = new ValueGeneratorList(type);
                ValueGeneratorRandom rGenerator = new ValueGeneratorRandom(type);
                return FXCollections.observableArrayList(sGBoolean, listGBoolean, rGenerator);
            }
        }
        return  null;
    }
}
