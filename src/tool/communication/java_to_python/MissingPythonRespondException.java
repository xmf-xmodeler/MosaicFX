package tool.communication.java_to_python;

/**
 * Used to indicate that a Python response was lost.  
 */
public class MissingPythonRespondException extends RuntimeException{

	public MissingPythonRespondException(String string) {
		super(string);
	}
}