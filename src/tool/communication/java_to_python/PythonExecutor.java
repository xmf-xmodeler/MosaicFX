package tool.communication.java_to_python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class uses the ProcessBuilder class to perform Python calls. 
 * !!User needs to have a local installation of Python. 
 */
public class PythonExecutor {

	private static final Logger logger = LogManager.getLogger(PythonExecutor.class);
	private static final boolean PYTHON_INSTALLED;

	static {
		PYTHON_INSTALLED = pythonInstalled();
	}

	public static void isPythoninstalled() throws MissingPythonException {
		if (!PYTHON_INSTALLED) {
			throw new MissingPythonException();
		}
	}

	public void executeAutoMlmFunction(PythonFunction function, String messageId) throws IllegalArgumentException {
		final String autoMlmBridgePath = "AutoMLM\\src\\java_communication\\call_receiver.py";
		ProcessBuilder processBuilder = buildProcessBuilder(autoMlmBridgePath, function, messageId);
		executePythonFunction(processBuilder);
	}

	private static boolean pythonInstalled() {
		if (!osHasPythonInstallation()) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if user has local installation of Python. The check validated depending on the return code of the shell.
	 * If there is a version the number of the version is logged.
	 * @return true if Python is installed
	 */
	private static boolean osHasPythonInstallation() {
		ProcessBuilder processBuilder = new ProcessBuilder("python", "--version");
		Process process;
		try {
			process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				return false;
			}
			logPythonVersion(process);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static void logPythonVersion(Process process) throws IOException {
		String pythonVersion = null;
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			pythonVersion = line;
		}
		logger.info("Python version of user: {}", pythonVersion);
	}

	private void executePythonFunction(ProcessBuilder processBuilder) throws IllegalArgumentException {
		Process process;
		try {
			process = processBuilder.start();
			readInputStream(process);
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * This function redirects all inputstreams to Java. So std-prints and error-prints can be read in Java console.
	 * @param process of which the streams should be redirected
	 * 
	 * @throws IOException 
	 * @throws IllegalArgumentException
	 */
	private void readInputStream(Process process) throws IOException, IllegalArgumentException {
		logger.debug("Start logging Python");
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.contains("ValueError")) {
				throw new IllegalArgumentException("Wrong input for Python");
			}
			logger.debug(line);
			System.err.println(line);
		}
		logger.debug("Stop logging Python");
	}
	        
	private ProcessBuilder buildProcessBuilder(String autoMlmBridgePath, PythonFunction function, String messageId) {
		ProcessBuilder processBuilder = new ProcessBuilder("python", autoMlmBridgePath, function.getFunctionName(), messageId);
		processBuilder.redirectErrorStream(true);
		return processBuilder;
	}
}