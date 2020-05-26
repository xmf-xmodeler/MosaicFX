package tool.clients.fmmlxdiagrams.instancegenerator;

import org.junit.jupiter.api.Test;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGeneratorStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueGeneratorStaticTest {

    @Test
    void getType() {

        ValueGeneratorStatic valueGeneratorStatic = new ValueGeneratorStatic("Integer");
        assertEquals("Integer", valueGeneratorStatic.getAttributeType());

        ValueGeneratorStatic valueGeneratorStatic1 = new ValueGeneratorStatic("Float");
        assertEquals("Float", valueGeneratorStatic1.getAttributeType());

        ValueGeneratorStatic valueGeneratorStatic2 = new ValueGeneratorStatic("Boolean");
        assertEquals("Boolean", valueGeneratorStatic2.getAttributeType());

        ValueGeneratorStatic valueGeneratorStatic3 = new ValueGeneratorStatic("String");
        assertEquals("String", valueGeneratorStatic3.getAttributeType());
    }

    @Test
    void generate() {

        ValueGeneratorStatic valueGeneratorStatic = new ValueGeneratorStatic("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("3.0");
        valueGeneratorStatic.setParameter(parameter);
        valueGeneratorStatic.generate(3);
        System.out.println(valueGeneratorStatic.getGeneratedValue().toString());

        ValueGeneratorStatic valueGeneratorStatic1 = new ValueGeneratorStatic("Integer");
        List<String> parameter1 = new ArrayList<>();
        parameter1.add("3");
        valueGeneratorStatic1.setParameter(parameter1);
        valueGeneratorStatic1.generate(3);
        System.out.println(valueGeneratorStatic1.getGeneratedValue().toString());

        ValueGeneratorStatic valueGeneratorStatic2 = new ValueGeneratorStatic("Float");
        List<String> parameter2 = new ArrayList<>();
        parameter2.add("3");
        valueGeneratorStatic2.setParameter(parameter2);
        valueGeneratorStatic2.generate(3);
        System.out.println(valueGeneratorStatic2.getGeneratedValue().toString());

        ValueGeneratorStatic valueGeneratorStatic3 = new ValueGeneratorStatic("Float");
        List<String> parameter3 = new ArrayList<>();
        parameter3.add("3.0");
        valueGeneratorStatic3.setParameter(parameter3);
        valueGeneratorStatic3.generate(3);
        System.out.println(valueGeneratorStatic3.getGeneratedValue().toString());

        ValueGeneratorStatic valueGeneratorStatic4 = new ValueGeneratorStatic( "String");
        List<String> parameter4 = new ArrayList<>();
        parameter4.add("Hallo World");
        valueGeneratorStatic4.setParameter(parameter4);
        valueGeneratorStatic4.generate(3);
        System.out.println(valueGeneratorStatic4.getGeneratedValue().toString());

        ValueGeneratorStatic valueGeneratorStatic5 = new ValueGeneratorStatic("Boolean");
        List<String> parameter5 = new ArrayList<>();
        parameter5.add("true");
        valueGeneratorStatic5.setParameter(parameter5);
        valueGeneratorStatic5.generate(3);
        System.out.println(valueGeneratorStatic5.getGeneratedValue().toString());
    }
}
