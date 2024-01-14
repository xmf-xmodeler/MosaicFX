package tool.communication.java_python_bridge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TODO Logging
//TODO Documentation
//TODO Error Handling -> best practice IO-Exception
//TODO Implement Check PYthon

public class PythonMessage {

	private String messageId;
	// jtop abbreviation for "java to python"
	private File jtopInDir;
	private File jtopOutDir;

	public PythonMessage(String content) {
		checkForPython();
		createTempFolders();
		createMessage(content);
	}

	private void createTempFolders() {
		final String userTempPath = System.getProperty("java.io.tmpdir");

		Path xModelerTempFolderPath = Paths.get(userTempPath, "XModeler\\");
		createDir(xModelerTempFolderPath);

		Path jtopInDirPath = Paths.get(xModelerTempFolderPath.toString(), "JtopIn");
		jtopInDir = createDir(jtopInDirPath);

		Path jtopOutDirPath = Paths.get(xModelerTempFolderPath.toString(), "JtopOut");
		jtopOutDir = createDir(jtopOutDirPath);
	}

	private File createDir(Path dirPath) {
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectory(dirPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dirPath.toFile();
	}

	private void createMessage(String content) {
		File message = createTempFile();
		messageId = message.getName();
		appendMessage(message, content);
	}

	private void appendMessage(File message, String content) {
		try (FileWriter writer = new FileWriter(message)) {
			writer.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File createTempFile() {
		File message = null;
		try {
			message = File.createTempFile("jtop", ".xml", jtopInDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	private void checkForPython() {
		// TODO Auto-generated method stub

	}
}