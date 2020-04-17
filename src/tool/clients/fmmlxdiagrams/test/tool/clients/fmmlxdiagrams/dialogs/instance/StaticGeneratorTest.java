package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

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
        staticGenerator.setValue("3.0");
        System.out.println(staticGenerator.generate(3));

        StaticGenerator staticGenerator1 = new StaticGenerator("Integer");
        staticGenerator1.setValue("3");
        System.out.println(staticGenerator1.generate(3));

        StaticGenerator staticGenerator2 = new StaticGenerator("Float");
        staticGenerator2.setValue("3");
        System.out.println(staticGenerator2.generate(3));

        StaticGenerator staticGenerator3 = new StaticGenerator("Float");
        staticGenerator3.setValue("3.0");
        System.out.println(staticGenerator3.generate(3));

        StaticGenerator staticGenerator4 = new StaticGenerator("String");
        staticGenerator4.setValue("Hallo World");
        System.out.println(staticGenerator4.generate(3));

        StaticGenerator staticGenerator5 = new StaticGenerator("Boolean");
        staticGenerator5.setValue("true");
        System.out.println(staticGenerator5.generate(3));
    }
}