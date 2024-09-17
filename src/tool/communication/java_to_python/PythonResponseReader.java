package tool.communication.java_to_python;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class reads the response of Python. The response is stored from Python to the file system.
 * The response is read and afterwards deleted from there to avoid storage problems on the user side.
 */
public class PythonResponseReader {

	private static final Logger logger = LogManager.getLogger(PythonResponseReader.class);
	private final String messageId;
	/**
	 * This variable represents the response Java can get from a function call of
	 * Python. Normally one line is expected as result but because right now not all
	 * use cases can be anticipated a list is used as an abstraction over possible
	 * results. So later every line of an response can be handled like an entry in
	 * an array.
	 */
	private List<String> response;

	//path used as location to store the response of Python	
	Path jtopOutDirPath = Paths.get(System.getProperty("java.io.tmpdir"), "XModeler\\JtopOut");

	/**
	 * While instantiation of PythonResponseReader the response from Python is read from the file system. 
	 * If there is no message this function will show an error to the user. Because we can not handle the loss and there is no 
	 * further information about the reason the user only gets a generic error message.
	 * @param messageId
	 */
	public PythonResponseReader(String messageId) {
		this.messageId = messageId;
		try {
			readResponse();
		} catch (MissingPythonRespondException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			Platform.runLater(()->{
				Alert missingResponseAlert = new Alert(AlertType.ERROR);
				missingResponseAlert.setTitle("Oops!");
				missingResponseAlert.setHeaderText("Soemthing went wrong. Please try again.");
				missingResponseAlert.showAndWait();			
			});		
		}
	}

	/**
	 * It was decided not to return the response by this function. The response
	 * could be grabbed by the different response-gettern of the class. This is due
	 * to the possible requirements of return types of function. Depending on the
	 * return type of the called function the response could be casted to the right
	 * data type
	 * 
	 * @throws FileNotFoundException
	 */
	private void readResponse() throws MissingPythonRespondException {
		Path messagePath = Paths.get(jtopOutDirPath.toString(), messageId);
		if (!Files.exists(messagePath)) {
			throw new MissingPythonRespondException("Java could not find the response from Python for the messageId:" + messageId);
		}
		try {
			response = Files.readAllLines(messagePath);
			logger.debug(String.format("Java reads response '%s'", response));
		} catch (IOException e) {
			e.printStackTrace();
		}
		deleteMessageFile(messagePath);
	}

	/**
	 * This function is used to clean the file system after the message was read to protect the user against a unused file flood.
	 * The more temp files are stored the higher is the possibility, that the name will not be unique.
	 * @param messagePath name of file to be deleted
	 */
	private void deleteMessageFile(Path messagePath) {
		try {
			Files.delete(messagePath);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("File of message with the id '" + messageId + "' could not be deleted", e);
		}
	}

	/**
	 * Reads first line of response and converts it to a String. 
	 * Even if the file has more then one line this is not read. 
	 * @return String representation of response.
	 */
	public String getResponseAsString() {
		if (response.size() > 1) {
			throw new IllegalArgumentException(
					"For the Python response with the messageId '" + messageId + "' was assumed a wrong data type.");
		}
		return response.get(0);
	}

	/**
	 * This function is used to offer the right read function for an response. Depending on the expected data type the specific 
	 * read function will return the matching data type. If you want to introduce new data typed to not forget to add a switch statement here.
	 * 
	 * Architectural comment: If over time there will be a bunch of different implementations it should be worth to refactor this function to a ReturnTypeFactory
	 * 
	 * @param function Enum that defines the data type of the return value
	 * @return response converted to matching data type
	 */
	public Object getResponse(PythonFunction function) {
		switch (function.getReturnType()) {
		case STRING:
			return getResponseAsString();
		}
		return null;
	}
}