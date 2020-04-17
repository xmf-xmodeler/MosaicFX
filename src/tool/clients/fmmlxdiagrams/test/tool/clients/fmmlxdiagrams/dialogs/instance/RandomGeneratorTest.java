package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {

    @Test
    void getType() {
        RandomGenerator randomGenerator = new RandomGenerator("Integer");
        assertEquals("Integer", randomGenerator.getType());

        RandomGenerator randomGenerator1 = new RandomGenerator("Float");
        assertEquals("Float", randomGenerator1.getType());

        RandomGenerator randomGenerator2 = new RandomGenerator("Boolean");
        assertEquals("Boolean", randomGenerator2.getType());
    }

    @Test
    void generateInteger() {
        RandomGenerator randomGenerator = new RandomGenerator("Integer");
        System.out.println(randomGenerator.generate(10));
    }

    @Test
    void generateFloat() {
        RandomGenerator randomGenerator = new RandomGenerator("Float");
        System.out.println(randomGenerator.generate(10));
    }

    @Test
    void generateBoolean() {
        RandomGenerator randomGenerator = new RandomGenerator("Boolean");
        System.out.println(randomGenerator.generate(10));
    }
}