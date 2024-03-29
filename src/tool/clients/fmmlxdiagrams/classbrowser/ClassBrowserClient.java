package tool.clients.fmmlxdiagrams.classbrowser;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.helper.IconGenerator;

public class ClassBrowserClient{
	
	public static ModellBrowserStage stage;
	public static ClassBrowserClient classBrowserClientInstance;

	public ClassBrowserClient() {
	    classBrowserClientInstance = this;
	}

	//public static void start() {
	//	ClassBrowserClient.stage = new ModelBrowser();
	//}
	
	public static ClassBrowserClient getInstance() {
		if(classBrowserClientInstance==null) {
			classBrowserClientInstance = new ClassBrowserClient();
		}
	    return classBrowserClientInstance;
	 }

	public static void show(AbstractPackageViewer diagram) {
		ClassBrowserClient.stage = new ModellBrowserStage();
		stage.show();
		stage.toFront();
		stage.initData(diagram);
		stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
	}

}
