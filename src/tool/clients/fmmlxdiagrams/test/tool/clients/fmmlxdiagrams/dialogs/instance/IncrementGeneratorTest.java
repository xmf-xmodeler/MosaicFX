package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

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
        incrementGenerator.setParameter("1", "3","2");
        assertEquals( 2, incrementGenerator.possibleGeneratedInstance());

        incrementGenerator.setParameter("1", "5","2");
        assertEquals(3, incrementGenerator.possibleGeneratedInstance());

        incrementGenerator.setParameter("3", "5","5");
        assertEquals(1, incrementGenerator.possibleGeneratedInstance());

        incrementGenerator.setParameter("1", "10","1");
        assertEquals(10, incrementGenerator.possibleGeneratedInstance());
    }

    @Test
    void possibleGeneratedInstanceFloat() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        incrementGenerator.setParameter("1.0", "3.0","2.0");
        assertEquals( 2, incrementGenerator.possibleGeneratedInstance());

        incrementGenerator.setParameter("1.0", "5.0","0.5");
        assertEquals(9, incrementGenerator.possibleGeneratedInstance());
    }

    @Test
    void getParametersInteger() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Integer");
        incrementGenerator.setParameter("1.0", "3.0","2");

        assertEquals("1", incrementGenerator.getParameters().get(0));
        assertEquals("3", incrementGenerator.getParameters().get(1));
        assertEquals("2", incrementGenerator.getParameters().get(2));
    }

    @Test
    void getParametersFloat() {

        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        incrementGenerator.setParameter("1.2", "3","2");

        assertEquals("1.2", incrementGenerator.getParameters().get(0));
        assertEquals("3.0", incrementGenerator.getParameters().get(1));
        assertEquals("2.0", incrementGenerator.getParameters().get(2));
    }

    @Test
    void generate() {
        IncrementGenerator incrementGenerator = new IncrementGenerator("Float");
        incrementGenerator.setParameter("1.0", "3","2");

        System.out.println(incrementGenerator.generate(7));
    }
}