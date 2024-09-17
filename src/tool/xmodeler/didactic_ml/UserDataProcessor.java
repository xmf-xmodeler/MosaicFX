package tool.xmodeler.didactic_ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

/**
 * Class used to read and write data concerned users in regard to the DidacticMl
 * functions.
 */
public class UserDataProcessor {

	private UserDataProcessor() {
		throw new IllegalStateException("Utility class");
	}

	private static final String USER_DATA_FILE_PATH;
	private static final String LEARNING_UNITS = "learningUnits";
	private static final String LEARNING_UNIT_NAME = "learningUnitName";
	private static final String FINISHED_SELF_ASSESSMENT_TESTS = "finishedSelfAssessmentTests";

	static {
		String envVariableValue = System.getenv("XMODELER_STAGE");
		USER_DATA_FILE_PATH = (envVariableValue != null && envVariableValue.equals("dev"))
				? "data\\dev\\didacticMlmUserData.json"
				: "data\\prod\\didacticMlmUserData.json";
	}

	/**
	 * Writes SelfAssessmentTest to UserJson. Call this function if user has
	 * finished a test.
	 * 
	 * @param test that should be stores to JSON because it is finished.
	 */
	public static void appendSelfAssessmentTest(SelfAssessmentTest test) {
		JsonObject doc = getUserData();
		Optional<JsonObject> learningUnit = getLearningUnitObject(doc, test);
		learningUnit.ifPresentOrElse(foundItem -> appendTest(doc, foundItem, test),
				() -> createNewLearningUnitObject(doc, learningUnit, test));
	}

	private static void appendTest(JsonObject doc, JsonObject learningUnit, SelfAssessmentTest test) {
		JsonArray finishedTests = learningUnit.getAsJsonArray(FINISHED_SELF_ASSESSMENT_TESTS);
		if (containsValue(finishedTests, test.getPrettyName())) {
			return;
		} else {
			finishedTests.add(test.getPrettyName());
		}
		printData(doc);
	}

	private static boolean containsValue(JsonArray jsonArray, String searchValue) {
		for (JsonElement element : jsonArray) {
			if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
				String value = element.getAsString();
				if (value.equals(searchValue)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void createNewLearningUnitObject(JsonObject doc, Optional<JsonObject> learningUnit,
			SelfAssessmentTest test) {
		JsonObject learningUnitO = new JsonObject();
		learningUnitO.addProperty(LEARNING_UNIT_NAME, test.getLearningUnit().getPrettyName());
		JsonArray finishedTests = new JsonArray();
		finishedTests.add(test.getPrettyName());
		learningUnitO.add(FINISHED_SELF_ASSESSMENT_TESTS, finishedTests);
		JsonArray units = getUserData().getAsJsonArray(LEARNING_UNITS);
		units.add(learningUnitO);
		doc.entrySet().clear();
		doc.add(LEARNING_UNITS, units);
		printData(doc);
	}

	private static Optional<JsonObject> getLearningUnitObject(JsonObject doc, SelfAssessmentTest test) {
		JsonArray units = doc.getAsJsonArray(LEARNING_UNITS);
		for (JsonElement unit : units) {
			JsonObject item = unit.getAsJsonObject();
			if (item.get(LEARNING_UNIT_NAME).getAsString().equals(test.getLearningUnit().getPrettyName())) {
				return Optional.of(item);
			}
		}
		return Optional.empty();
	}

	private static JsonObject getUserData() {
		return JsonParser.parseString(readUserData()).getAsJsonObject();
	}

	private static void printData(JsonElement jsonObject) {
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

		try (FileWriter file = new FileWriter(USER_DATA_FILE_PATH)) {
			prettyGson.toJson(jsonObject, file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readUserData() {
		StringBuilder contentBuilder = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(USER_DATA_FILE_PATH))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				contentBuilder.append(currentLine).append("\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
		return contentBuilder.toString();
	}

	/**
	 * This will set the learning unit array to null. In the frontend then all self
	 * assessments test will be presented as not finished.
	 */
	public static void resetTestStatistics() {
		JsonObject doc = getUserData();
		doc.entrySet().clear();
		JsonArray emptyArray = new JsonArray();
		doc.add(LEARNING_UNITS, emptyArray);
		printData(doc);
	}

	/**
	 * Checks the user data file if specific test is already finished
	 * @param test that you want to check if it is finished
	 * @return 
	 * @return true in case user has finished test
	 */
	public static boolean userHasFinishedTest(SelfAssessmentTest test) {
		AtomicBoolean hasFinished = new AtomicBoolean(false);
		JsonObject doc = getUserData();
		Optional<JsonObject> learningUnit = getLearningUnitObject(doc, test);
		learningUnit.ifPresent(foundItem -> {
			JsonArray finishedTests = foundItem.getAsJsonArray(FINISHED_SELF_ASSESSMENT_TESTS);
			if (containsValue(finishedTests, test.getPrettyName())) {
				hasFinished.set(true);}});	
		return hasFinished.get();
	}
}