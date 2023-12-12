package tool.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

public class RequestLogManager {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(FmmlxDiagramCommunicator.class);
	private static RequestLogManager instance;
	private Vector<RequestLog> logList = new Vector<>(); 

	public static RequestLogManager getInstance() {
		if (instance == null) {
			instance = new RequestLogManager();
		}
		return instance;
	}
	
	public void addLog(RequestLog log) {
		//2023-09-08 TS restricts the size of the logList to 1000 Elements. Was inserted to restrict memory usage of the list.
		if (logList.size() > 1000) {
			logList.remove(0);
		} 
		logList.add(log);			
	}
	
	public void setLogReturned(int id, Vector<Object> msgAsVec) {
		RequestLog log = getLog(id);
		log.setCallbackExecutionTime(System.currentTimeMillis());
		log.setReturned();
		log.setReturnedMessageVector(msgAsVec);
		logger.debug("Request returned {}", log);
	}

	public RequestLog getLog(int requestId) {
		for (RequestLog requestLog : new Vector<>(logList)) {
			if (requestLog.getRequestId() == requestId) {
				return requestLog;
			}
		}
		return null;
	}
}