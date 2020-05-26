package tool.clients.fmmlxdiagrams.instancegenerator;

import org.junit.jupiter.api.Test;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorRandom;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorRandomTest {

    @Test
    void getType() {

        ValueGeneratorRandom valueGeneratorRandom = new ValueGeneratorRandom("Integer");
        assertEquals("Integer", valueGeneratorRandom.getAttributeType());

        ValueGeneratorRandom valueGeneratorRandom1 = new ValueGeneratorRandom("Float");
        assertEquals("Float", valueGeneratorRandom1.getAttributeType());

        ValueGeneratorRandom valueGeneratorRandom2 = new ValueGeneratorRandom("Boolean");
        assertEquals("Boolean", valueGeneratorRandom2.getAttributeType());
    }

    @Test
    void generateInteger() {

        ValueGeneratorRandom valueGeneratorRandom = new ValueGeneratorRandom("Integer");
        valueGeneratorRandom.setSelectedScenario("Free");
        valueGeneratorRandom.generate(10);
        System.out.println(valueGeneratorRandom.getGeneratedValue().toString());

        ValueGeneratorRandom valueGeneratorRandom1 = new ValueGeneratorRandom("Integer");
        valueGeneratorRandom1.setSelectedScenario("Range");
        List<String> param = new ArrayList<>();
        param.add("1");
        param.add("10");
        valueGeneratorRandom1.setParameter(param);
        valueGeneratorRandom1.generate(10);
        System.out.println(valueGeneratorRandom1.getGeneratedValue().toString());
    }

    @Test
    void generateFloat() {

        ValueGeneratorRandom valueGeneratorRandom = new ValueGeneratorRandom("Float");
        valueGeneratorRandom.setSelectedScenario("Free");
        valueGeneratorRandom.generate(10);
        System.out.println(valueGeneratorRandom.getGeneratedValue().toString());

        ValueGeneratorRandom valueGeneratorRandom1 = new ValueGeneratorRandom("Float");
        valueGeneratorRandom1.setSelectedScenario("Range");
        List<String> param = new ArrayList<>();
        param.add("1");
        param.add("10");
        valueGeneratorRandom1.setParameter(param);
        valueGeneratorRandom1.generate(10);
        System.out.println(valueGeneratorRandom1.getGeneratedValue().toString());
    }

    @Test
    void generateBoolean() {

        ValueGeneratorRandom valueGeneratorRandom = new ValueGeneratorRandom("Boolean");
        valueGeneratorRandom.generate(10);
        System.out.println(valueGeneratorRandom.getGeneratedValue().toString());
    }
}