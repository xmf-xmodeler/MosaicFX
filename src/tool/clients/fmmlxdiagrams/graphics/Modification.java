package tool.clients.fmmlxdiagrams.graphics;

public class Modification {
	
	private static enum Consequence{
		SHOW_ALWAYS, SHOW_NEVER,
		SHOW_IF, SHOW_IF_NOT,
	}
	
	private Condition condition;
	private Consequence consequence;
	private NodeElement affectedElement;
		
}
