package tool.clients.forms;

import java.io.PrintStream;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.widgets.ToolBar;

public class FormTools {

	String id;
	Vector<FormToolDef> tools = new Vector<FormToolDef>();
	// ConcurrentHashMap<String, FormToolDef> tools_new = new
	// ConcurrentHashMap<String, FormToolDef>();

	public FormTools(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Vector<FormToolDef> getTools() {
		return tools;
	}
	//
	// public ConcurrentHashMap<String, FormToolDef> getTools_new() {
	// return tools_new;
	// }

	public void setTools(final String toolName, final String id, final boolean enabled) {
		// if(tools_new.containsKey(toolName)){
		// FormsClient.theClient().runOnDisplay(new Runnable() {
		// public void run() {
		// tools_new.get(toolName).setEnabled(enabled);;
		// }
		// });
		// }else{
		// addTool(toolName, id, enabled);
		// }

		boolean found = false;
		for (FormToolDef formToolDef : tools) {
			if (formToolDef.event.equals(eventName(toolName)) && formToolDef.id.equals(id)) {
				found = true;
				final FormToolDef formToolDef_final = formToolDef;
				FormsClient.theClient().runOnDisplay(new Runnable() {
					public void run() {
						formToolDef_final.setEnabled(enabled);
					}
				});
			}
		}
		if (!found) {
			addTool(toolName, id, enabled);
		}

	}

	public void addTool(String toolName, String id, boolean enabled) {
		// tools.add(FormToolDef ftd = new FormToolDef(eventName(toolName), id,
		// iconFile(toolName), disabledIconFile(toolName)));
		FormToolDef ftd = new FormToolDef(eventName(toolName), id, iconFile(toolName), disabledIconFile(toolName),
				enabled);
		// ftd.setEnabled(enabled);
		tools.add(ftd);
		// tools_new.put(toolName, ftd);
	}

	public void addTool(String toolName, String id) {
		addTool(toolName, id, true);
	}

	public void writeXML(PrintStream out) {
		out.print("<FormTools id='" + getId() + "'>");
		for (FormToolDef def : tools)
			// for (FormToolDef def : tools_new.values())
			def.writeXML(out);
		out.print("</FormTools>");
	}

	private String iconFile(String toolName) {
		if (toolName.equals("browseAndClearHistory"))
			return "icons/Clear.gif";
		else if (toolName.equals("previousInHistory"))
			return "icons/Back.gif";
		else if (toolName.equals("nextInHistory"))
			return "icons/Forward.gif";
		else if (toolName.equals("lock"))
			return "icons/Unlocked.gif";
		else {
			System.err.println("unkown tool icon file for " + toolName);
			return "icons/Object.gif";
		}
	}

	private String disabledIconFile(String toolName) {
		if (toolName.equals("browseAndClearHistory"))
			return "icons/Clear.gif";
		else if (toolName.equals("previousInHistory"))
			return "icons/BackDisabled.gif";
		else if (toolName.equals("nextInHistory"))
			return "icons/ForwardDisabled.gif";
		else if (toolName.equals("lock"))
			return "icons/Locked.gif";
		else {
			System.err.println("unkown tool icon file for " + toolName);
			return "icons/Object.gif";
		}
	}

	public void populateToolBar(ToolBar toolBar) {
		boolean test = false;
		for (FormToolDef def : tools)
			// for (FormToolDef def : tools_new.values())
			def.populateToolBar(toolBar);
	}

	private String eventName(String toolName) {
		if (toolName.equals("browseAndClearHistory"))
			return "clearHistory";
		else if (toolName.equals("nextInHistory"))
			return "nextInHistory";
		else if (toolName.equals("previousInHistory"))
			return "previousInHistory";
		else if (toolName.equals("lock"))
			return "lockForm";
		// return "switchLockForm";
		else {
			System.err.println("unknown tool event name for " + toolName);
			return toolName;
		}
	}
}
