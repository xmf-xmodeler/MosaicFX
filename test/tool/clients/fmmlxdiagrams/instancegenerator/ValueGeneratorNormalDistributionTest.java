package tool.clients.fmmlxdiagrams.instancegenerator;

import org.junit.jupiter.api.Test;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorNormalDistribution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorNormalDistributionTest {

    @Test
    void getType() {

        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution = new ValueGeneratorNormalDistribution("Integer");
        assertEquals(valueGeneratorNormalDistribution.getAttributeType(), "Integer");

        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution1 = new ValueGeneratorNormalDistribution("Float");
        assertEquals(valueGeneratorNormalDistribution1.getAttributeType(), "Float");
    }

    @Test
    void getParameterInteger() {

        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution = new ValueGeneratorNormalDistribution("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("4.0");
        parameter.add("1");
        parameter.add("1");
        parameter.add("10.0");
        valueGeneratorNormalDistribution.setParameter(parameter);
        List<String> expected = new ArrayList<>();
        expected.add("4");
        expected.add("1");
        expected.add("1");
        expected.add("10");

        System.out.println(valueGeneratorNormalDistribution.getParameter());

        assertEquals(expected, valueGeneratorNormalDistribution.getParameter());
    }

    @Test
    void getParameterFloat() {
        FmmlxDiagram diagram = new FmmlxDiagram(null, 0, "", "");
        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution = new ValueGeneratorNormalDistribution( "Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("4.5");
        parameter.add("1");
        parameter.add("1.0");
        parameter.add("10.6");
        valueGeneratorNormalDistribution.setParameter(parameter);
        List<String> expected = new ArrayList<>();
        expected.add("4.5");
        expected.add("1.0");
        expected.add("1.0");
        expected.add("10.6");

        System.out.println(valueGeneratorNormalDistribution.getParameter());

        assertEquals(expected, valueGeneratorNormalDistribution.getParameter());
    }

    @Test
    void generateInteger() {
        FmmlxDiagram diagram = new FmmlxDiagram(null, 0, "", "");
        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution = new ValueGeneratorNormalDistribution("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("5.0");
        parameter.add("1");
        parameter.add("1.0");
        parameter.add("10");
        valueGeneratorNormalDistribution.setParameter(parameter);
        valueGeneratorNormalDistribution.generate(10);
        System.out.println(valueGeneratorNormalDistribution.getGeneratedValue().toString());
    }

    @Test
    void generateFloat() {
        FmmlxDiagram diagram = new FmmlxDiagram(null, 0, "", "");
        ValueGeneratorNormalDistribution valueGeneratorNormalDistribution = new ValueGeneratorNormalDistribution("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("5.2");
        parameter.add("1");
        parameter.add("1.3");
        parameter.add("10");
        valueGeneratorNormalDistribution.setParameter(parameter);
        valueGeneratorNormalDistribution.generate(10);
        System.out.println(valueGeneratorNormalDistribution.getGeneratedValue().toString());
    }
}