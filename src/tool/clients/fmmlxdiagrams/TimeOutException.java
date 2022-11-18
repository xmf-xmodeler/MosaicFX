package tool.clients.fmmlxdiagrams;

public class TimeOutException extends Exception {
	private static final long serialVersionUID = 1L;

	public TimeOutException(String info) {
		super("Did not receive answer in time! ("+info+")");
	}
}
