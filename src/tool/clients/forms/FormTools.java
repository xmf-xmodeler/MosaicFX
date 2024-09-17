package tool.clients.forms;

import java.io.PrintStream;
import java.util.Vector;

import javafx.application.Platform;

public class FormTools {

	String id;
	Vector<FormToolDef> tools = new Vector<FormToolDef>();

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

	public void setTools(final String toolName, final String id, final boolean enabled) {

		boolean found = false;
		for (FormToolDef formToolDef : tools) {
			if (formToolDef.event.equals(eventName(toolName)) && formToolDef.id.equals(id)) {
				found = true;
				final FormToolDef formToolDef_final = formToolDef;
				Platform.runLater(()->{
					formToolDef_final.setEnabled(enabled);
				});
			}
		}
		if (!found) {
			addTool(toolName, id, enabled);
		}

	}

	public void addTool(String toolName, String id, boolean enabled) {
		FormToolDef ftd = new FormToolDef(eventName(toolName), id, iconFile(toolName), disabledIconFile(toolName),
				enabled);
		tools.add(ftd);
	}

	public void addTool(String toolName, String id) {
		addTool(toolName, id, true);
	}

	public void writeXML(PrintStream out) {
		out.print("<FormTools id='" + getId() + "'>");
		for (FormToolDef def : tools)
			def.writeXML(out);
		out.print("</FormTools>");
	}

	private String iconFile(String toolName) {
		if (toolName.equals("browseAndClearHistory"))
			return "resources/gif/Clear.gif";
		else if (toolName.equals("previousInHistory"))
			return "resources/gif/Back.gif";
		else if (toolName.equals("nextInHistory"))
			return "resources/gif/Forward.gif";
		else if (toolName.equals("lock"))
			return "resources/gif/Unlocked.gif";
		else {
			System.err.println("Unknown tool icon file for " + toolName);
			return "resources/gif/Object.gif";
		}
	}

	private String disabledIconFile(String toolName) {
		if (toolName.equals("browseAndClearHistory"))
			return "resources/gif/Clear.gif";
		else if (toolName.equals("previousInHistory"))
			return "resources/gif/BackDisabled.gif";
		else if (toolName.equals("nextInHistory"))
			return "resources/gif/ForwardDisabled.gif";
		else if (toolName.equals("lock"))
			return "resources/gif/Locked.gif";
		else {
			System.err.println("Unknown tool icon file for " + toolName);
			return "resources/gif/Object.gif";
		}
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
		else {
			System.err.println("unknown tool event name for " + toolName);
			return toolName;
		}
	}
}
