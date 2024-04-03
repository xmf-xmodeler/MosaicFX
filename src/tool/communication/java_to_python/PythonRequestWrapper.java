package tool.communication.java_to_python;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PythonRequestWrapper {

	private final PythonFunction function;
	private PythonResponseReader responseReader;
	private static final Logger logger = LogManager.getLogger(PythonRequestWrapper.class);
	private static PythonMessage message;
	private boolean successfulExecution = false;
	//geb die errors einfach komplett hoch, nicht lokal handeln , da es runtime exception sind, kann der entwickler selbst überlgen ob er sie handelt oder nicht
	//gilt zwar für response aber wie mache ich es für
	
	
	public PythonRequestWrapper(PythonFunction function, String[] args) {
		this.function = function;
		responseReader = null;
		
		try {
			PythonExecutor.isPythoninstalled();
		} catch (MissingPythonException missingPythonExcetion) {						
			String errorString = String.format("Users tries to execute Python function '%s' but has no local installation.", function.getFunctionName());
			logger.error(errorString, missingPythonExcetion);
			Platform.runLater(() -> new MissingPythonAlert().showAndWait());
			return;
		}
		
		message = new PythonMessage(args);
		if (!matchesExpectedNumberOfFunctionArgs(args)) {
			throw new IllegalArgumentException("The function '" + function.getFunctionName() + "' does take "
					+ function.getExpectedFunctionArgs() + " function args instead of " + args.length);
		}
					
		
	}
	
	public void execute() {
		Runnable defaultCatchStrategy = () ->{
			Platform.runLater(() -> {
				Alert illigealArgumentAlert = new Alert(AlertType.ERROR);
				illigealArgumentAlert.setTitle("Wrong input!");
				illigealArgumentAlert.setHeaderText("The function you have called needs another input. Try again ;)");
				illigealArgumentAlert.showAndWait();	
			});
		};
		execute(defaultCatchStrategy);
	}
	
	public void execute(Runnable chatchStrategy) {
		try {
			new PythonExecutor().executeAutoMlmFunction(function, message.getMessageId());			
			successfulExecution = true;
		} catch (IllegalArgumentException e) {
			logger.error("Python was executed with wrong values. The execution is aborted."); 
			chatchStrategy.run();
		}
	}
	
	public Object getResponse() {
		if (!successfulExecution) {
			return null;
		}
		responseReader = new PythonResponseReader(message.getMessageId());
		return responseReader.getResponse(function);
	}

	/**
	 * Compares the attribute expectedFunctionArgs of the function to the args-input
	 * 
	 * @param args
	 * @return true in case of matching numbers
	 */
	private boolean matchesExpectedNumberOfFunctionArgs(String[] args) {
		return (function.getExpectedFunctionArgs() == args.length);
	}
}