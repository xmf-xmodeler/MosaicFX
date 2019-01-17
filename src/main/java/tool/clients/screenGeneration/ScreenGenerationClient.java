package tool.clients.screenGeneration;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class ScreenGenerationClient extends Client {

	public static final int HIGH_RESOLUTION_FACTOR_OLD = 2;
	
	private static HashMap<String, CommandableScreenElement>  elementRegistry;

	private static ScreenGenerationClient theClient;
	
	public static int getDeviceZoomPercent() {
		return XModeler.getDeviceZoomPercent();
	}



	public static void select() {
		// for (ToolItem item : toolBar.getItems())
		// item.dispose();
		// CTabItem selectedItem = tabFolder.getSelection();
		// for (String id : tabs.keySet()) {
		// if (tabs.get(id) == selectedItem) {
		// for (ToolItem item : toolBar.getItems())
		// item.dispose();
		// FormTools formTools = FormsClient.theClient().getFormTools(id);
		// formTools.populateToolBar(toolBar);
		// }
		// }
		// toolBar.pack();
	}

	public static void start(Object tabFolder) {
//		ScreenGenerationClient.tabFolder = tabFolder;
//		tabFolder.addSelectionListener(new SelectionListener() {
//			public void widgetDefaultSelected(SelectionEvent event) {
//			}
//
//			public void widgetSelected(SelectionEvent event) {
//				select();
//			}
//		});
		Window.start(tabFolder);
	}

	public static ScreenGenerationClient theClient() {
		return theClient;
	}

	public static Message extractMessage(Message message) {
		String messageName = message.args[1].strValue();
		Value[] args = message.args[2].values;

		Message newMessage = new Message(messageName, args.length - 1);
		newMessage.args = args;
		return newMessage;
	}

	public static final int TAB = 1;

	public ScreenGenerationClient() {
		super("screenGeneration");
//		theClient = this;
//		tabFolder.addCTabFolder2Listener(this);
//		elementRegistry = new HashMap<String, CommandableScreenElement>(); 
	}

	public Value callMessage(Message message) {
		// if (message.hasName("getText"))
		// return getText(message);
		// else if (message.hasName("getTextDimension"))
		// return getTextDimension(message);
		// else
		return super.callMessage(message);
	}

	private Window getScreen(String id) {
//		for (Window window : windows)
//			if (window.getId().equals(id))
//				return window;
		return null;
	}

	public boolean processMessage(Message message) {
		return false;
	}

//	private void selectScreen(final String id) {
//		runOnDisplay(new Runnable() {
//			public void run() {
//				if (tabs.containsKey(id))
//					tabFolder.setSelection(tabs.get(id));
//				else
//					System.err.println("cannot find form: " + id);
//				select();
//			}
//		});
//	}

	public void sendMessage(final Message message) {
	/*	if (message.hasName("testIt"))
			System.out.println(message);
		else if (message.hasName("newWindow"))
			newWindow(message);
		else if (message.hasName("messageForward"))
			messageForward(message);
		else */if(message.hasName("newElement"))
			newElement(message);
		else if(message.hasName("command"))
			command(message);
		else if(message.hasName("deleteElement"))
			deleteElement(message);
		else 
			super.sendMessage(message);
		
	} 

	private void command(String id, String command, Value[] values) {
		runOnDisplay(new Runnable() {
			public void run() {
				elementRegistry.get(id).command(command, values);
			}});
	}
	
	private void command(Message message) {
		String id = message.args[0].strValue();
		String command = message.args[1].strValue();
		Value[] values = message.args[2].values;
		command(id, command, values);
	}
	
	private void deleteElement(String idParent, String deleteMessage, String idDelete, Value[] values) {
		runOnDisplay(new Runnable() {
			public void run() {
				CommandableScreenElement cse = elementRegistry.get(idDelete);
				//if(idParent == null){
					if(deleteMessage.equals("closeWindow")){
						cse.close();
//						windows.remove((Window)cse);
					//}
				}else{
					elementRegistry.get(idParent).deleteElement(deleteMessage, cse,values);
				}
				elementRegistry.remove(idDelete);
			}});
	}
	
	private void newElement(String idParent, String newMessage, String idNew, Value[] values) {
		runOnDisplay(new Runnable() {
			public void run() {
				CommandableScreenElement cse = null; 
				if (newMessage.equals("newWindow"))
					cse = newWindow(idNew,values);
				else
					cse = elementRegistry.get(idParent).createNewElement(newMessage, idNew,values);
				elementRegistry.put(idNew, cse);
			}});
	}
	
	private void deleteElement(Message message) {
		String idParent = message.args[0].strValue();
		String deleteMessage = message.args[1].strValue();
		String idDelete = message.args[2].strValue();
		Value[] values = message.args[3].values;
		deleteElement(idParent, deleteMessage,idDelete, values);
	}
	
	private void newElement(Message message) {
		String idParent = message.args[0].strValue();
		String newMessage = message.args[1].strValue();
		String idNew = message.args[2].strValue();
		Value[] values = message.args[3].values;
		newElement(idParent, newMessage,idNew, values);
	}
	
	private void newWindow(Message message) {
		String id = message.args[0].strValue();
		int type = message.args[1].intValue;
		String label = message.args[2].strValue();
		newWindow(id, type, label, true);
	}

	private CommandableScreenElement newWindow(String idNew, Value[] values) {
		int type = values[0].intValue;
		String label = values[1].strValue();
		return newWindow(idNew, type, label, true);
	}
	
	private CommandableScreenElement newWindow(final String id, final int type, final String label, final boolean selected) {
		Window w = Window.windowFactory(id, this, type, label, selected);
//		windows.add(w);
		return w;
	}

	private void messageForward(Message message) {
		String id = message.args[0].strValue();

//		for (Window window : windows) {
//			if (window.getId().equals(id)) {
//				window.sendMessage(extractMessage(message));
//			}
//		}
	}

	//TODO
//	private void setVisible(Message message) {
//		String id = message.args[0].strValue();
//		selectScreen(id);
		// runOnDisplay(new Runnable() {
		// public void run() {
		// select();
		// }
		// });
//	}

	public void writeXML(PrintStream out) {
		out.print("<ScreenGeneration>");
		// for (Screen screen : screens)
		// screen.writeXML(out, tabFolder.getSelection() ==
		// tabs.get(screen.getId()), tabs.get(screen.getId()).getText());
		out.print("</ScreenGeneration>");
	}

//	public void close(CTabFolderEvent event) {
//		CTabItem item = (CTabItem) event.item;
//		String id = getId(item);
//		if (id != null && getScreen(id) != null) {
//			EventHandler handler = getHandler();
//			Message message = handler.newMessage("screenClosed", 1);
//			message.args[0] = new Value(id);
//			handler.raiseEvent(message);
//			windows.remove(getScreen(id));
//			tabs.remove(id);
//		}
//	}
//
//	private String getId(CTabItem item) {
//		for (String id : tabs.keySet())
//			if (tabs.get(id).equals(item))
//				return id;
//		return null;
//	}
//
//	public void maximize(CTabFolderEvent event) {
//
//	}
//
//	public void minimize(CTabFolderEvent event) {
//
//	}
//
//	public void restore(CTabFolderEvent event) {
//
//	}
//
//	public void showList(CTabFolderEvent event) {
//
//	}

	public void doubleClick(String id) {
		EventHandler handler = getHandler();
		Message message = handler.newMessage("doubleSelected", 1);
		message.args[0] = new Value(id);
		handler.raiseEvent(message);
	}
}