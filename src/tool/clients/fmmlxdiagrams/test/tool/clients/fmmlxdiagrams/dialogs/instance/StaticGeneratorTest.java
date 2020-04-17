package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StaticGeneratorTest {

    @Test
    void getType() {
        StaticGenerator staticGenerator = new StaticGenerator("Integer");
        assertEquals("Integer", staticGenerator.getType());

        StaticGenerator staticGenerator1 = new StaticGenerator("Float");
        assertEquals("Float", staticGenerator1.getType());

        StaticGenerator staticGenerator2 = new StaticGenerator("Boolean");
        assertEquals("Boolean", staticGenerator2.getType());

        StaticGenerator staticGenerator3 = new StaticGenerator("String");
        assertEquals("String", staticGenerator3.getType());
    }

    @Test
    void generate() {
        StaticGenerator staticGenerator = new StaticGenerator("Integer");
        List<String> parameter = new ArrayList<>();
        parameter.add("3.0");
        staticGenerator.setParameter(parameter);
        System.out.println(staticGenerator.generate(3));

        StaticGenerator staticGenerator1 = new StaticGenerator("Integer");
        List<String> parameter1 = new ArrayList<>();
        parameter1.add("3");
        staticGenerator1.setParameter(parameter1);
        System.out.println(staticGenerator1.generate(3));

        StaticGenerator staticGenerator2 = new StaticGenerator("Float");
        List<String> parameter2 = new ArrayList<>();
        parameter2.add("3");
        staticGenerator2.setParameter(parameter2);
        System.out.println(staticGenerator2.generate(3));

        StaticGenerator staticGenerator3 = new StaticGenerator("Float");
        List<String> parameter3 = new ArrayList<>();
        parameter3.add("3.0");
        staticGenerator3.setParameter(parameter3);
        System.out.println(staticGenerator3.generate(3));

        StaticGenerator staticGenerator4 = new StaticGenerator("String");
        List<String> parameter4 = new ArrayList<>();
        parameter4.add("Hallo World");
        staticGenerator4.setParameter(parameter4);
        System.out.println(staticGenerator4.generate(3));

        StaticGenerator staticGenerator5 = new StaticGenerator("Boolean");
        List<String> parameter5 = new ArrayList<>();
        parameter5.add("true");
        staticGenerator5.setParameter(parameter5);
        System.out.println(staticGenerator5.generate(3));
    }
}