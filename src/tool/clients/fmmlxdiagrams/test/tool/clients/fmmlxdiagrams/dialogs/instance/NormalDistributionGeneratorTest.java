package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NormalDistributionGeneratorTest {

    @Test
    void getType() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");
        assertEquals(normalDistributionGenerator.getType(), "Integer");

        NormalDistributionGenerator normalDistributionGenerator1 = new NormalDistributionGenerator("Float");
        assertEquals(normalDistributionGenerator1.getType(), "Float");
    }

    @Test
    void getParameterInteger() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("4.0");
        parameter.add("1");
        parameter.add("1");
        parameter.add("10.0");
        normalDistributionGenerator.setParameter(parameter);
        List<String> expected = new ArrayList<>();
        expected.add("4");
        expected.add("1");
        expected.add("1");
        expected.add("10");

        System.out.println(normalDistributionGenerator.getParameter());

        assertEquals(expected, normalDistributionGenerator.getParameter());
    }

    @Test
    void getParameterFloat() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("4.5");
        parameter.add("1");
        parameter.add("1.0");
        parameter.add("10.6");
        normalDistributionGenerator.setParameter(parameter);
        List<String> expected = new ArrayList<>();
        expected.add("4.5");
        expected.add("1.0");
        expected.add("1.0");
        expected.add("10.6");

        System.out.println(normalDistributionGenerator.getParameter());

        assertEquals(expected, normalDistributionGenerator.getParameter());
    }

    @Test
    void generateInteger() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("5.0");
        parameter.add("1");
        parameter.add("1.0");
        parameter.add("10");
        normalDistributionGenerator.setParameter(parameter);

        System.out.println(normalDistributionGenerator.generate(10).toString());
    }

    @Test
    void generateFloat() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("5.2");
        parameter.add("1");
        parameter.add("1.3");
        parameter.add("10");
        normalDistributionGenerator.setParameter(parameter);

        System.out.println(normalDistributionGenerator.generate(10).toString());
    }
}