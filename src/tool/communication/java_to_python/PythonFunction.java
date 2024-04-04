package tool.communication.java_to_python;

public enum PythonFunction {

	//Example function that takes a string and returns it
	PROCESS_STRIGN("process_string", 1, ReturnType.STRING),
	//Function to test the exception handling for a lost response file
	SIMULATE_LOST_FILE("simulate_lost_file", 1, ReturnType.STRING),
	ILLEGAL_ARGUMENTS("illegal_arguments", 1, ReturnType.STRING),
	CALL_EXECUTION("perform_promotion_process_from_java", 1, ReturnType.STRING);

	
	
	/**
	 *Name of function that should be called in Python. !! Must exactly match. 
	 */
	private final String functionName;
	/**
	 * Defines the number of needed arguments to perform the function in Python
	 */
	private final int expectedFunctionArgs;
	private final ReturnType returnType;

	/**
	 * List of implemented return types
	 */
	public enum ReturnType {
		STRING
	}

	PythonFunction(String functionName, int expectedFunctionArgs, ReturnType returnType) {
		this.functionName = functionName;
		this.expectedFunctionArgs = expectedFunctionArgs;
		this.returnType = returnType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public ReturnType getReturnType() {
		return returnType;
	}

	public int getExpectedFunctionArgs() {
		return expectedFunctionArgs;
	}
}