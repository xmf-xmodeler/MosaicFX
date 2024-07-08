package tool.xmodeler.didactic_ml.learning_unit_steps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class LearningUnitTasks {

	//TODO: Assumption there can be only one description at a time. Otherwise the static fields will override each other.
	
	
	/**
	 * The task map represents all tasks in a learning unit. The map stores the name
	 * of the task (used as keys) and an integer value. This value will be called
	 * precedence. Because all tasks are consecutive the precedence describes the
	 * order of the tasks.
	 */
	protected static Map<String, Integer> tasks = new HashMap<String, Integer>();
	protected static String learningUnitName;
	
	
	//TODO doku, map starts with 1 -> contract of constructor 
	/**
	 * Contract subclasses must call this with matching name
	 * @param taskName
	 */
	public LearningUnitTasks(String learningUnitName) {
		LearningUnitTasks.learningUnitName = learningUnitName;
	}
	
	

	/**
	 * Filters map for task name and returns the precedence of this task.
	 * 
	 * @param taskName you want to know the precedence of
	 * @return precedence of task
	 */
	public static int getPrecedence(String taskName) {
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
		return getTaskDescritpion(firstTask);
	}

	/**
	 * Helper function that returns the path of a file where the description is
	 * stored. If you add new descriptions, please maintain the file structure to not break the code.
	 * 
	 * @param learningUnitName used to navigate to the right folder
	 * @param taskName used to identify the right file name
	 * @return filepath of task description
	 */
	private static String buildTaskDescriptionPath(String taskName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("resources/html/"); // basic path
		stringBuilder.append(learningUnitName); // append learningUnitName
		stringBuilder.append("/");
		stringBuilder.append(getPrecedence(taskName)); // append number of current state
		stringBuilder.append(".html");
		return stringBuilder.toString();
	}
	
	/**
	 * Every LearningUnitTask is described by a task description. These tasks are stored on the file system as html documents.
	 * This function returns the content of the file as string.
	 * 
	 * @param taskName identifies which file will be retrieved from the file system
	 * @return description of this task as string
	 */
	public static String getTaskDescritpion(String taskName) {
		String taskDescriptionPath = buildTaskDescriptionPath(taskName);
		StringBuilder contentBuilder = new StringBuilder();
		try (Reader reader = new InputStreamReader(new FileInputStream(taskDescriptionPath), StandardCharsets.UTF_8)) {
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line);
			}
			return contentBuilder.toString();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can not find file for task description " + getPrecedence(taskName) + " from the learning unit " + learningUnitName);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}