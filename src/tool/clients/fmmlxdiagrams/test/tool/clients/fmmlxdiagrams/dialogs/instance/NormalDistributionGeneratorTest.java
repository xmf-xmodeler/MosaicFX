package tool.clients.fmmlxdiagrams.dialogs.instance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalDistributionGeneratorTest {

    @Test
    void getValue() {


        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");
        normalDistributionGenerator.setValue(normalDistributionGenerator.getType(),"0","1","1","2");

        System.out.println(normalDistributionGenerator.getValue().toString());
        System.out.println("generated Value : "+ normalDistributionGenerator.generate());

        System.out.println("");

        NormalDistributionGenerator normalDistributionGenerator2 = new NormalDistributionGenerator("Float");
        normalDistributionGenerator2.setValue(normalDistributionGenerator2.getType(), "0","1","1","2");

        System.out.println(normalDistributionGenerator2.getValue().toString());
        System.out.println("generated Value : "+ normalDistributionGenerator2.generate());
    }

    @Test
    void generateValueInt() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Integer");

        for (int i =0 ; i<5; i++){
            int generatedValue = Integer.parseInt(normalDistributionGenerator.generateValue("Integer", "7","2",1,11));
            System.out.println("generated Value : " +generatedValue);
            assertTrue(generatedValue<=11 && generatedValue>=1);
            System.out.println(" ");
        }
    }

    @Test
    void generatedValueFloat() {
        NormalDistributionGenerator normalDistributionGenerator = new NormalDistributionGenerator("Float");

        for (int i =0 ; i<5; i++){
            float generatedValue = Float.parseFloat(normalDistributionGenerator.generateValue("Float", "7","2",1,11));
            System.out.println("generated Value : " +generatedValue);
            assertTrue(generatedValue<=11.0 && generatedValue>=1.0);
            System.out.println(" ");
        }
    }
}