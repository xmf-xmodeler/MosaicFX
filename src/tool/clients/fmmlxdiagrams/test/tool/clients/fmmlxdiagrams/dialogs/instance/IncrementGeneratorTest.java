package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncrementGeneratorTest {

    @Test
    void getType() {
        IncrementGenerator incrementGenerator = new IncrementGenerator("Integer");
        assertEquals(incrementGenerator.getType(), "Integer");

        IncrementGenerator incrementGenerator1 = new IncrementGenerator("Float");
        assertEquals(incrementGenerator1.getType(), "Float");
    }

    @Test
    void possibleGeneratedInstanceInteger() {
        IncrementGenerator incrementGenerator = new IncrementGenerator("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3");
        parameter.add("2");

        incrementGenerator.setParameter(parameter);
        assertEquals( 2, incrementGenerator.possibleGeneratedInstance());

    }

    @Test
    void possibleGeneratedInstanceFloat() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3.0");
        parameter.add("2.0");
        incrementGenerator.setParameter(parameter);
        assertEquals( 2, incrementGenerator.possibleGeneratedInstance());

    }

    @Test
    void getParametersInteger() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3.0");
        parameter.add("2");
        incrementGenerator.setParameter(parameter);

        assertEquals("1", incrementGenerator.getParameter().get(0));
        assertEquals("3", incrementGenerator.getParameter().get(1));
        assertEquals("2", incrementGenerator.getParameter().get(2));
    }

    @Test
    void getParametersFloat() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.2");
        parameter.add("3");
        parameter.add("2");
        incrementGenerator.setParameter(parameter);

        assertEquals("1.2", incrementGenerator.getParameter().get(0));
        assertEquals("3.0", incrementGenerator.getParameter().get(1));
        assertEquals("2.0", incrementGenerator.getParameter().get(2));
    }

    @Test
    void generate() {
        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        List<String> parameter = new ArrayList<>();
        parameter.add("1.0");
        parameter.add("3");
        parameter.add("2");
        incrementGenerator.setParameter(parameter);

        System.out.println(incrementGenerator.generate(7));
    }
}