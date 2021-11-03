package tool.clients.oleBridge;

//import org.eclipse.ui.IStartup;

// TODO: Auto-generated Javadoc
/**
 * The Class OleStartup.
 */
public class OleStartup { //implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		System.out.println("[ register com.ceteva.oleBridge ]");
//		OleBridgeClient client = new OleBridgeClient();
		// XmfPlugin.xos.newMessageClient("com.ceteva.oleBridge",client);
	}
}
