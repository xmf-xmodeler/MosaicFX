package tool.communication.java_to_python;

public enum PythonFunction {

	CALL_EXECUTION("perform_promotion_process_from_java", 1, ReturnType.STRING),
	IMPORT_XML("import_XML",0,ReturnType.STRING);

	
	
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