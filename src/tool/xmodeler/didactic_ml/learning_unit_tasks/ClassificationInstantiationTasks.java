package tool.xmodeler.didactic_ml.learning_unit_tasks;

import java.util.Map;

public class ClassificationInstantiationTasks extends LearningUnitTasks {

	public ClassificationInstantiationTasks() {
		super("ClassificationInstantiation");
	}

	@Override
	public void init() {
		tasks = Map.ofEntries(
				Map.entry("BASIC_ONE", 1),
				Map.entry("BASIC_TWO", 2),
				Map.entry("BASIC_THREE", 3));
	}
}