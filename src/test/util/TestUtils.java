package test.util;

import java.util.Random;

import tool.xmodeler.ControlCenterClient;

public class TestUtils {

	static ControlCenterClient controlCenterClient = new ControlCenterClient();

	private TestUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void waitWithoutCatch(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns random string containing only small letters. While using this the
	 * probability that a name will not be unique raises. Test with UUIDs have
	 * proven, that this leads to problem to use in the XModeler. Below is a list of
	 * possible different variants
	 * 
	 * length = 6 -> 308,915,776 length = 7 -> 8,031,810,176 length = 8 ->
	 * 208,827,064,576
	 * 
	 * @param length of returned string
	 * @return random string containing small letters
	 */
	public static String getRandomId(int length) {
		final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < 12; i++) {
			int randomIndex = new Random().nextInt(ALLOWED_CHARACTERS.length());
			char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	/**
	 * To keep the computation time for strings short for the creation of projects
	 * and diagrams a string of the length 6 is chosen.
	 * 
	 * @return random Id with length 6
	 */
	public static String getShortRandomId() {
		return getRandomId(6);
	}

	/**
	 * Use this method inside a test to keep the state after the test alive If you
	 * call this only the test tread will sleep so you could use XModeler. Could be
	 * useful to do some manual test after a specific set of actions have been
	 * performed to the XModeler. This implementation will keep the stage one hour
	 * alive. If you find a smarter solution go for it!
	 */
	public static void keepAliveAfterTest() {
		System.err.println("Testbody was executed. Application will stay alive for one hour.");
		int hourToMilisec = 3600000;
		waitWithoutCatch(hourToMilisec);
	}
}