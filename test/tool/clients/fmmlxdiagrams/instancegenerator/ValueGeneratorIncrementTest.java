package tool.clients.fmmlxdiagrams.instancegenerator;

import org.junit.jupiter.api.Test;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorIncrement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorIncrementTest {

    @Test
    void getType() {

        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement("Integer");
        assertEquals(valueGeneratorIncrement.getAttributeType(), "Integer");

        ValueGeneratorIncrement valueGeneratorIncrement1 = new ValueGeneratorIncrement("Float");
        assertEquals(valueGeneratorIncrement1.getAttributeType(), "Float");
    }

    @Test
    void possibleGeneratedInstanceInteger() {

        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3");
        parameter.add("2");

        valueGeneratorIncrement.setParameter(parameter);
        assertEquals( 2, valueGeneratorIncrement.possibleGeneratedInstance());
    }

    @Test
    void possibleGeneratedInstanceFloat() {

        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement( "Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3.0");
        parameter.add("2.0");
        valueGeneratorIncrement.setParameter(parameter);
        assertEquals( 2, valueGeneratorIncrement.possibleGeneratedInstance());

    }

    @Test
    void getParametersInteger() {
        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3.0");
        parameter.add("2");
        valueGeneratorIncrement.setParameter(parameter);

        assertEquals("1", valueGeneratorIncrement.getParameter().get(0));
        assertEquals("3", valueGeneratorIncrement.getParameter().get(1));
        assertEquals("2", valueGeneratorIncrement.getParameter().get(2));
    }

    @Test
    void getParametersFloat() {
        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.2");
        parameter.add("3");
        parameter.add("2");
        valueGeneratorIncrement.setParameter(parameter);

        assertEquals("1.2", valueGeneratorIncrement.getParameter().get(0));
        assertEquals("3.0", valueGeneratorIncrement.getParameter().get(1));
        assertEquals("2.0", valueGeneratorIncrement.getParameter().get(2));
    }

    @Test
    void generate() {
        ValueGeneratorIncrement valueGeneratorIncrement = new ValueGeneratorIncrement("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3");
        parameter.add("2");
        valueGeneratorIncrement.setParameter(parameter);
        valueGeneratorIncrement.generate(7);
        System.out.println(valueGeneratorIncrement.getGeneratedValue());
    }
}