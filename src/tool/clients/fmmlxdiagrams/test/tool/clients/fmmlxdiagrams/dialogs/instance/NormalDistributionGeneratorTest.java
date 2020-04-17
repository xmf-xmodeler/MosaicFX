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
        normalDistributionGenerator.setParameters("4.0", "1","1", "10.0");
        List<String> expected = new ArrayList<>();
        expected.add("4");
        expected.add("1");
        expected.add("1");
        expected.add("10");

        System.out.println(normalDistributionGenerator.getParameters());

        assertEquals(expected, normalDistributionGenerator.getParameters());
    }

    @Test
    void getParameterFloat() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Float");
        normalDistributionGenerator.setParameters("4.5", "1","1.0", "10.6");
        List<String> expected = new ArrayList<>();
        expected.add("4.5");
        expected.add("1.0");
        expected.add("1.0");
        expected.add("10.6");

        System.out.println(normalDistributionGenerator.getParameters());

        assertEquals(expected, normalDistributionGenerator.getParameters());
    }

    @Test
    void generateInteger() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");
        normalDistributionGenerator.setParameters("5.0","1","1.0","10");

        System.out.println(normalDistributionGenerator.generate(10).toString());
    }

    @Test
    void generateFloat() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Float");
        normalDistributionGenerator.setParameters("5.2","1","1.3","10");

        System.out.println(normalDistributionGenerator.generate(10).toString());
    }
}