package tool.xmodeler.didactic_ml.self_assessment_test_tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.xmodeler.didactic_ml.frontend.ResourceLoader;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

/**
 * This is a helper class to retrieve the data of the task that are contained in a learning unit. A Learning unit task is described by a name.
 * Learning unit task are strictly ordered. To maintain this order every task has a precedence expressed by an integer. The task are stored in a map. Depending on the learning unit you need to instantiate a subclass of this abstract class.
 * Do not forget to call the constructor before you use this class because in the constructor the static fields are set. You can only use one subclass-instance at a time otherwise you will override the static values.
 */
public abstract class SelfAssessmentTestTasks {

	/**
	 * The task map represents all tasks in a learning unit. The map stores the name
	 * of the task (used as keys) and an integer value. This value will be called
	 * precedence. Because all tasks are consecutive the precedence describes the
	 * order of the tasks.
	 */
	protected static Map<String, Integer> tasks = new HashMap<>();
	/**
	 * Defines the learning unit that is associated. This name needs to match the folder name of the file system where the tasksDescriptions are stored.
	 */
	protected static SelfAssessmentTest selfAssessmentTest;
	
	public static SelfAssessmentTest getSelfAssessmentTest() {
		return selfAssessmentTest;
	}

	/**
	 * The subclass must override this constructor with matching learning unit name. Please mind, that this name must also be used in the filesystem
	 * to store associated task description. Please override the tasks field in the constructor of the subclass. Please not that the first precedence is 1.
	 * For example see ToolIntroductionTask.
	 * 
	 * @param taskName represents associated learning unit and must match file system folder 
	 */
	protected SelfAssessmentTestTasks(SelfAssessmentTest selfAssessmentTest) {
		SelfAssessmentTestTasks.selfAssessmentTest = selfAssessmentTest;
	}
	
	/**
	 * Filters map for task name and returns the precedence of this task.
	 * 
	 * @param taskName you want to know the precedence of
	 * @return precedence of task
	 */
	public static int getPrecedence(String taskName) {
		System.err.println("taskName: " + taskName);
		return tasks.get(taskName);
	}

	/**
	 * Uses getPrecedence to return next precedence
	 * 
	 * @param taskName you want to know the precedence of
	 * @return next precedence of task
	 */
	public static int getNextPrecedence(String taskName) {
		return getPrecedence(taskName) + 1;
	}

	/**
	 * Returns name of tasks for given precedence
	 * 
	 * @param precedence you searching a key for
	 * @return name of task for given precedence
	 */
	public static String getTaskName(int precedence) {
		for (Entry<String, Integer> entry : tasks.entrySet()) {
			if (entry.getValue().equals(precedence)) {
				return entry.getKey();
			}
		}
		throw new IllegalArgumentException("No entry with the precedence " + precedence);
	}

	/**
	 * Returns the starting task for a learning unit
	 * @return starting task for a unit
	 */
	public static String getFirstTaskDescription() {
		String firstTask = getTaskName(1);
		return ResourceLoader.getTaskDescritpion(selfAssessmentTest, firstTask);
	}

	/**
	 * This function needs to be called before the task is first use. Please override the task var with matching values.
	 */
	public abstract void init();

	/**
	 * This method clears class informations. It is technically not needed but should avoid potential errors.
	 */
	public static void tearDown() {
		SelfAssessmentTestTasks.selfAssessmentTest = null;
		SelfAssessmentTestTasks.tasks = null;
	}
	
	/**
	 * Check the tasks for the highest precedence and return it. This values marks when the user has succeed the unit. 
	 * @return highest precedence of tasks
	 */
	public static int getHighestPrecedence() {
		int highestValue = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : tasks.entrySet()) {
            if (entry.getValue() > highestValue) {
                highestValue = entry.getValue();
            }
        }
        return highestValue;
	}
}