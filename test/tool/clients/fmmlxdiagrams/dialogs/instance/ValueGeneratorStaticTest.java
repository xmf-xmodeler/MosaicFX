package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorStaticTest {

    @Test
    void getType() {
        ValueGeneratorStatic valueGeneratorStatic = new ValueGeneratorStatic("Integer");
        assertEquals("Integer", valueGeneratorStatic.getType());

        ValueGeneratorStatic valueGeneratorStatic1 = new ValueGeneratorStatic("Float");
        assertEquals("Float", valueGeneratorStatic1.getType());

        ValueGeneratorStatic valueGeneratorStatic2 = new ValueGeneratorStatic("Boolean");
        assertEquals("Boolean", valueGeneratorStatic2.getType());

        ValueGeneratorStatic valueGeneratorStatic3 = new ValueGeneratorStatic("String");
        assertEquals("String", valueGeneratorStatic3.getType());
    }

    @Test
    void generate() {
        ValueGeneratorStatic valueGeneratorStatic = new ValueGeneratorStatic("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("3.0");
        valueGeneratorStatic.setParameter(parameter);
        System.out.println(valueGeneratorStatic.generate(3));

        ValueGeneratorStatic valueGeneratorStatic1 = new ValueGeneratorStatic("Integer");
        List<String> parameter1 = new ArrayList<>();
        parameter1.add("3");
        valueGeneratorStatic1.setParameter(parameter1);
        System.out.println(valueGeneratorStatic1.generate(3));

        ValueGeneratorStatic valueGeneratorStatic2 = new ValueGeneratorStatic("Float");
        List<String> parameter2 = new ArrayList<>();
        parameter2.add("3");
        valueGeneratorStatic2.setParameter(parameter2);
        System.out.println(valueGeneratorStatic2.generate(3));

        ValueGeneratorStatic valueGeneratorStatic3 = new ValueGeneratorStatic("Float");
        List<String> parameter3 = new ArrayList<>();
        parameter3.add("3.0");
        valueGeneratorStatic3.setParameter(parameter3);
        System.out.println(valueGeneratorStatic3.generate(3));

        ValueGeneratorStatic valueGeneratorStatic4 = new ValueGeneratorStatic("String");
        List<String> parameter4 = new ArrayList<>();
        parameter4.add("Hallo World");
        valueGeneratorStatic4.setParameter(parameter4);
        System.out.println(valueGeneratorStatic4.generate(3));

        ValueGeneratorStatic valueGeneratorStatic5 = new ValueGeneratorStatic("Boolean");
        List<String> parameter5 = new ArrayList<>();
        parameter5.add("true");
        valueGeneratorStatic5.setParameter(parameter5);
        System.out.println(valueGeneratorStatic5.generate(3));
    }
}