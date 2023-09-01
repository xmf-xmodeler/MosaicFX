package tool.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import xos.Value;

public class RequestLogManager {

	private FileWriter logWriter;
	private static RequestLogManager INSTANCE;
	private List<RequestLog> logList = new ArrayList<RequestLog>(); 
	private boolean debugModus;

	private void initLogManagement() {
		try {
			File logFile = new File("sessionLogs/" + getFormattedDateTime() + ".log");
			logFile.createNewFile();
			logWriter = new FileWriter(logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private RequestLogManager() {
		super();
		initLogManagement();
		boolean diagramCommunicatorDebug = FmmlxDiagramCommunicator.isDebug();
		if (diagramCommunicatorDebug) {
			debugModus = true;
		}
	}
	
	public static RequestLogManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RequestLogManager();
		}
		return INSTANCE;
	}
	
	public void addLog(RequestLog log) {
		logList.add(log);
		writeRequestStarToFile(log);
	}
	
	public void setLogReturned(int id, Vector<Object> msgAsVec) {
		var log = getLog(id);
		log.setCallbackExecutionTime(System.currentTimeMillis());
		log.setReturned();
		log.setReturnedMessageVector(msgAsVec);
		writeRequestReturnToFile(log);
	}

	private void writeRequestStarToFile(RequestLog log) {
		String output = buildMessage(log, "start");
		writeToFile(output);
	}
	
	public void writeRequestReturnToFile(RequestLog log) {
		String output = buildMessage(log, "return");
		writeToFile(output);
	}
	
	private String buildMessage(RequestLog log, String type) {
		String datetime = getFormattedDateTime();
		String action = null; 
		switch (type) {
		case "start":
			action = "send";
			break;
			
		case "return":
			action = "received";
			break;
		}
		String requestId = String.valueOf(log.getRequestId());
		String handel = String.valueOf(log.getHandel());
		String functionName = log.getCalledFunction();
		
		StringBuilder builder = new StringBuilder();
		String formatString = String.format("[%s] %s Request %s with handle %s. Called function:\"%s\". ", datetime, action, requestId, handel, functionName);
		builder.append(formatString);	
		
		switch (type) {
		case "start":
			builder.append("Used parameters:\"");
			for (Value value : log.getSendeFunctionArgs()) {
				builder.append(value.toString());
			}						
			break;
		case "return":
			builder.append("Returned Vector:\"");
			builder.append(log.getReturnedMessageVector());
			builder.append("\". Returntime:\"" + log.calculateXMFProcessingTime());
			break;
		}
		builder.append("\n");
		String output = builder.toString();
		return output;
	}

	private void writeToFile(String output) {
		try {
			logWriter.write(output);
			logWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RequestLog getLog(int requestId) {
		for (RequestLog requestLog : logList) {
			if (requestLog.getRequestId() == requestId) {
				return requestLog;
			}
		}
		return null;
	}
	
	private String getFormattedDateTime() {
		LocalDateTime localDateTime = LocalDateTime.now();
		String localDateTimeString = localDateTime.toString().replace(":", "_");
		return localDateTimeString;
	}
}