package tool.xmodeler.didactic_ml.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

/**
 * Helper class used to load contents from file system.
 */
public class ResourceLoader {
	
	/**
	 * Defines path for all didactic resources
	 */
	private static final String folderPath = "resources/didacticMlm/";

	public ResourceLoader() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Every LearningUnitTask is described by a task description. These tasks are
	 * stored on the file system as html documents. This function returns the
	 * content of the file as string.
	 * 
	 * @param taskName identifies which file will be retrieved from the file system
	 * @return description of this task as string
	 */
	public static String getTaskDescritpion(SelfAssessmentTest test, String taskName) {
		String taskDescriptionPath = buildTaskDescriptionPath(test, taskName);
		try {
			return readFromFile(taskDescriptionPath);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Can not find file for task description " + SelfAssessmentTestTasks.getPrecedence(taskName)
							+ " from the learning unit " + test.getLearningUnit().getPrettyName()
							+ ". the files were expected under the path: " + taskDescriptionPath);
		}
	}
	
	private static String readFromFile(String path) throws FileNotFoundException {
		StringBuilder contentBuilder = new StringBuilder();
		try (Reader reader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)) {
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null) {
				contentBuilder.append(line);
			}
			return contentBuilder.toString();
		}
		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * Helper function that returns the path of a file where the description is
	 * stored. If you add new descriptions, please maintain the file structure to
	 * not break the code.
	 * 
	 * @param test
	 * 
	 * @param learningUnitName used to navigate to the right folder
	 * @param taskName         used to identify the right file name
	 * @return filepath of task description
	 */
	private static String buildTaskDescriptionPath(SelfAssessmentTest test, String taskName) {
		StringBuilder stringBuilder = new StringBuilder(folderPath); 
		stringBuilder.append(test.getLearningUnit().getPathName()); // append learningUnitName
		stringBuilder.append("/");
		stringBuilder.append(SelfAssessmentTestTasks.getPrecedence(taskName)); // append number of current state
		stringBuilder.append(".html");
		return stringBuilder.toString();
	}

	/**
	 * @return absolute path of css that should be used for all didactic related content.
	 */
	public static String getDidacticCssPath() {
		String customCssPath = "resources/didacticMlm/didactic.css";
		File customCssFile = new File(customCssPath);
		return customCssFile.getAbsolutePath();
	}

	/**
	 * Build path for html that represents learningGoals
	 * @param LearningUnit that defines which file should be loaded
	 * @return relative path of learningGoals-html
	 */
	public static String getLearningGoals(LearningUnit lu) {
		String path = getLearningUnitGoalsPath(lu);
		try {
			return readFromFile(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cant find learningGoals.html for " + lu.getPrettyName());
		}
	}
	
	private static String getLearningUnitGoalsPath(LearningUnit lu) {
		StringBuilder sB = new StringBuilder(folderPath);
		sB.append(lu.getPathName());
		sB.append("/");
		sB.append("learningGoals.html");
		return sB.toString();
	}

	public static String getTheoreticalBackground(LearningUnit lu) {
		String path = getTheoreticalBackgroundPath(lu);
		try {
			return readFromFile(path);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cant find theoreticalBackground.html for " + lu.getPrettyName());
		}
	}

	private static String getTheoreticalBackgroundPath(LearningUnit lu) {
		StringBuilder sB = new StringBuilder(folderPath);
		sB.append(lu.getPathName());
		sB.append("/");
		sB.append("theoreticalBackground.html");
		return sB.toString();
	}
}