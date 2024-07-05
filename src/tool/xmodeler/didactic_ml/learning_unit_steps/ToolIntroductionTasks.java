package tool.xmodeler.didactic_ml.learning_unit_steps;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This enum is used in the DiagramViewIntroduction. There different states of
 * the diagramView are needed to help new users to deal with the XModeler. The
 * state defines what the user can see. It is passed through the different
 * frontend parts of the diagramView to create a matching frontend.
 */
public class ToolIntroductionTasks {

	
	//TODO doku, map starts with 1
	static Map<String, Integer> tasks = Map.ofEntries(Map.entry("CREATE_CLASS_MOVIE", 1),
			Map.entry("ADD_ATTRIBUTES_TO_MOVIE", 2), Map.entry("CREATE_CLASS_MOVIE_SHOWING", 3),
			Map.entry("ADD_ASSOC_BETWEEN_MOVIE_AND_SHOWING", 4),
			Map.entry("ADD_LINK_BETWEEN_MOVIEINST_AND_SHOWINGINST", 5), Map.entry("CHANGE_CARDINALITY_OF_BUYS", 6),
			Map.entry("ADD_RATING_ENUM", 7), Map.entry("CHANGE_DATATYPE_TO_ENUM", 8),
			Map.entry("GET_REQUIRED_AGE_FUN_IS_ADDED", 9), Map.entry("CONSTRAINT_IS_ADD", 10),
			Map.entry("FULL_GUI", 100));

	/**
	 * The gui is build consecutive. So the next gui needs all elements of the gui
	 * before. This is used for not replicate code. The full gui is defined for the
	 * value 100. In between all states can be inserted.
	 */
	



	public static int getPrecedence(String taskName) {
		return tasks.get(taskName);
	}
	
	

	public static int getNextPrecedence(String taskName) {
		return getPrecedence(taskName) + 1;
	}

	String getViewStatusFromPrecedence(int precedence) {
		for (Entry<String, Integer> step : tasks.entrySet()) {
			if (step.getValue() == precedence) {
				return step.getKey();
			}
		}
		throw new IllegalArgumentException("No DiagramViewState for the precedenceValue" + precedence);
	}

	/**
	 * request the task description needed for the current state. The descriptions
	 * are stores in the source folder
	 * 
	 * @return current task description as String
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
			throw new RuntimeException("Can not find file for DiagramViewState " + getPrecedence(taskName));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * helper function that returns the path of a file where the description is
	 * stored
	 * @param taskName 
	 * 
	 * @return filepath of task description
	 */
	private static String buildTaskDescriptionPath(String taskName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("resources/html/ToolIntroduction/"); // basic path
		stringBuilder.append(getPrecedence(taskName)); // append number of current state
		stringBuilder.append(".html");
		return stringBuilder.toString();
	}
	
	public static String getTaskName(int precedence) {
		for (Entry<String, Integer> entry : tasks.entrySet()) {
            if (entry.getValue().equals(precedence)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("No entry with the precedence " + precedence);
	}
	
	public static String getFirstTaskDescription() {
		String firstTask = getTaskName(1);
		return getTaskDescritpion(firstTask);
	}
}