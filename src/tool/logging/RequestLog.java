package tool.logging;

import java.util.Arrays;
import java.util.Vector;

import xos.Value;

public class RequestLog {
	
	private int requestId;
	private long requestTime;
	private long callbackExecutionTime;
	private boolean returned;
	private String calledFunction;
	private int handel;
	private Value[] sendeFunctionArgs;
	private boolean synchron;
	
	public String getCalledFunction() {
		return calledFunction;
	}

	public void setCalledFunction(String calledFunction) {
		this.calledFunction = calledFunction;
	}

	public int getHandel() {
		return handel;
	}

	public void setHandel(int handel) {
		this.handel = handel;
	}

	public Value[] getSendeFunctionArgs() {
		return sendeFunctionArgs;
	}

	public void setSendeFunctionArgs(Value[] sendeFunctionArgs) {
		this.sendeFunctionArgs = sendeFunctionArgs;
	}

	private Vector<Object> returnedMessageVector;
	
	
	public RequestLog(int requestId, boolean synchron, long requestTime, String calledFunction, int handel, Value[] sendeFunctionArgs) {
		super();
		this.synchron = synchron; 
		this.requestId = requestId;
		this.requestTime = requestTime;
		this.calledFunction = calledFunction;
		this.handel = handel;
		this.sendeFunctionArgs = sendeFunctionArgs;
		returned = false;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public long getCallbackExecutionTime() {
		return callbackExecutionTime;
	}

	public void setCallbackExecutionTime(long callbackExecutionTime) {
		this.callbackExecutionTime = callbackExecutionTime;
	}

	public boolean isReturned() {
		return returned;
	}

	//There is no case where you would set a log to not returned
	void setReturned() {
		this.returned = true;
	}

	public Vector<Object> getReturnedMessageVector() {
		return returnedMessageVector;
	}

	public void setReturnedMessageVector(Vector<Object> msgAsVec) {
		this.returnedMessageVector = msgAsVec;
	}
	
	long calculateXMFProcessingTime() {
		return callbackExecutionTime - requestTime;
	}
	
	public boolean isSynchron() {
		return synchron;
	}

	public void setSynchron(boolean synchron) {
		this.synchron = synchron;
	}

	@Override
	public String toString() {
		return "RequestLog [requestId=" + requestId + ", requestTime=" + requestTime + ", callbackExecutionTime="
				+ callbackExecutionTime + ", returned=" + returned + ", calledFunction=" + calledFunction + ", handel="
				+ handel + ", sendeFunctionArgs=" + Arrays.toString(sendeFunctionArgs) + ", synchron=" + synchron
				+ ", returnedMessageVector=" + returnedMessageVector + "]";
	}	
}