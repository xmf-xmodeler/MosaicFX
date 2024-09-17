package tool.communication.java_to_python;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents messages that are send to Python. Each message has an unique id. This id is set by Java to ensure that there will be no naming conflicts.
 * The message itself is represented as file on the file system. The content of the file represents the arguments used in the Python function the user wants to call. 
 */
public class PythonMessage {

	private static final Logger logger = LogManager.getLogger(PythonMessage.class);
	private String messageId;
	// jtop abbreviation for "java to python"
	private static File jtopInDir;
	
	static {
		createTempFolders();
	}
	
	public PythonMessage(String[] args) {
		createMessage(args);
	}

	/**
	 * PythonMessages are stored to the file system. For this purpose in the user temp dir a subfolder 'XModeler' is created.
	 * Inside this subfolder are the input and outputfolder for the communication created. 
	 */
	private static void createTempFolders()  {
		final String userTempPath = System.getProperty("java.io.tmpdir");

		Path xModelerTempFolderPath = Paths.get(userTempPath, "XModeler\\");
		createDir(xModelerTempFolderPath);

		Path jtopInDirPath = Paths.get(xModelerTempFolderPath.toString(), "JtopIn");
		jtopInDir = createDir(jtopInDirPath);

		Path jtopOutDirPath = Paths.get(xModelerTempFolderPath.toString(), "JtopOut");
		createDir(jtopOutDirPath);
	}

	/**
	 * Helper function used to create directories
	 * @param dirPath path at which the new dir should be created
	 * @return directory to be created as file
	 */
	private static File createDir(Path dirPath) {
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectory(dirPath);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Needed directory could not be created" ,e);
			}
		}
		return dirPath.toFile();
	}

	private void createMessage(String[] args) {
		File message = createTempFile();
		setArguments(message, args);
	}

	/**
	 * Write function args to the file which represents the message to Python. 
	 * Every array filed in args is written to a new line.
	 * @param message the file that represents the message.
	 * @param args the function args that should be used in the Python function call.
	 */
	private void setArguments(File message, String[] args) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(message))) {
			for (String arg : args) {
				writer.write(arg);
				writer.newLine();
			}			
			logger.debug(" Arguments of Message with id '" + messageId + "' was set: {}", args);
		} catch (IOException e) {
			logger.error("Could write to Java message with id '" + messageId + "'.", e);
			e.printStackTrace();
		}
	}

	/**
	 * This function is used to create a unique file in the jtopInDir. The file is later read by Python and contains the function arguments.
	 * @return newly created file that serves as message and contains function args
	 */
	private File createTempFile() {
		File message = null;
		try {
			//it was assumed, that most of the time model representations in the .xml format are shared. So this file extension was choose to define messages. 
			message = File.createTempFile("jtop", ".xml", jtopInDir);
			messageId = message.getName();
			logger.debug(String.format("File with path '%s' was created as PythonMessage.", message.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Could not create Java message.", e);
		}
		return message;
	}

	public String getMessageId() {
		return messageId;
	}
}