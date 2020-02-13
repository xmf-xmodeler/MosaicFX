package tool.clients.fmmlxdiagrams.classbrowser;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class ClassBrowserClient{
	
	public static ClassBrowserStage stage;
	public static ClassBrowserClient classBrowserClientInstance;

	public ClassBrowserClient() {
//		super("com.ceteva.modelBrowser");
	    classBrowserClientInstance = this;
	}

//	@Override
//	public boolean processMessage(Message message) {
//		return false;
//	}

	public static void start() {
		ClassBrowserClient.stage = new ClassBrowserStage();
	}
	
	public static ClassBrowserClient getInstance() {
		if(classBrowserClientInstance==null) {
			classBrowserClientInstance = new ClassBrowserClient();
		}
	    return classBrowserClientInstance;
	 }
	
//	public static void show(Object handle) {
//		stage.show();
//		stage.toFront();
//	}

	public static void show(FmmlxDiagram diagram) {
		stage.show();
		stage.toFront();
		stage.updateDiagram(diagram);
	}

}
