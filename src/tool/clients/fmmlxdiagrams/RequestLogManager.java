package tool.clients.fmmlxdiagrams;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class RequestLogManager {

	private static RequestLogManager INSTANCE;
	private List<RequestLog> logList = new ArrayList<RequestLog>(); 
	
	private RequestLogManager() {
		super();
	}
	
	public static RequestLogManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RequestLogManager();
		}
		return INSTANCE;
	}
	
	public void addLog(RequestLog log) {
		logList.add(log);
	}
	
	public RequestLog getLog(int requestId) {
		for (RequestLog requestLog : logList) {
			if (requestLog.getRequestId() == requestId) {
				return requestLog;
			}
		}
		return null;
	}
}
