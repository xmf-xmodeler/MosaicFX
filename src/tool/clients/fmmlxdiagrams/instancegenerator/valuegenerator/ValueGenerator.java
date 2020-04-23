package tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

import java.util.ArrayList;
import java.util.List;

public class ValueGenerator {

    private final String attributeType;
    private FmmlxDiagram diagram;

    public ValueGenerator(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public FmmlxDiagram getDiagram() {
        return diagram;
    }

    public void setDiagram(FmmlxDiagram diagram) {
        this.diagram = diagram;
    }

    protected String floatConverter(String value) {
        try {
            return Float.parseFloat(value)+"";
        } catch (Exception e){
            return (float)Integer.parseInt(value)+"";
        }
    }

    protected String integerConverter(String value) {
        try {
            return Integer.parseInt(value)+"";
        } catch (Exception e){
            return Math.round(Float.parseFloat(value))+"";
        }
    }

    protected String booleanConverter(String value) {
        try {
            return Boolean.parseBoolean(value)+"";
        } catch (Exception e){
            return "";
        }
    }

    protected List<String> listToIntConverter(List<String> value){
        List<String> result = new ArrayList<>();
        for (String str : value){
            result.add(integerConverter(str));
        }
        return result;
    }

    protected List<String> listToFloatConverter(List<String> value){
        List<String> result = new ArrayList<>();
        for (String str : value){
            result.add(floatConverter(str));
        }
        return result;
    }
}
