package tool.clients.fmmlxdiagrams.instancegenerator;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class InstanceGenerator {
	
	public static InstanceGeneratorStage stage;
	public static InstanceGenerator instanceGeneratorInstance;

	public InstanceGenerator(FmmlxDiagram diagram, FmmlxObject object) {
	    instanceGeneratorInstance = this;

	}

	public static void start() {
		InstanceGenerator.stage = new InstanceGeneratorStage();
	}
	
	public static InstanceGenerator getInstance() {
		if(instanceGeneratorInstance!=null) {
			return instanceGeneratorInstance;
		} else {
			return null;
		}
	 }

	public static void show(FmmlxObject object) {
		stage.show();
		stage.toFront();
		stage.update(object);
	}

}

