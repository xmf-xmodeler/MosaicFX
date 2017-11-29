package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

import xos.Value;

public class Palette {

  ExpandBar     groupContainer;
  Vector<Group> groups = new Vector<Group>();

  public Palette(Composite parent, Diagram diagram) {
    groupContainer = new ExpandBar(parent, SWT.V_SCROLL);
    newGroup("Diagram");
    newTool(diagram, "Diagram", "Select", "Select", false, "Select.gif");
  }

  public Value asValue() {
    Value[] g = new Value[groups.size()];
    int i = 0;
    for (Group group : groups)
      g[i++] = group.asValue(group.getName());
    return new Value(g);
  }

  public void deleteGroup(String name) {
    Group group = getGroup(name);
    if (group != null) {
      groups.remove(group);
      group.delete();
    }
  }

  private Group getGroup(String name) {
    for (Group group : groups)
      if (group.getName().equals(name)) return group;
    return null;
  }

  public boolean hasGroup(String name) {
    return getGroup(name) != null;
  }

  public void newGroup(String name) {
    Group group = new Group(this, groupContainer, name);
    groups.add(group);
  }

  public void newToggle(Diagram diagram, String groupName, String label, String toolId, boolean state, String iconTrue, String iconFalse) {
    Group group = getGroup(groupName);
    if (group != null) {
      group.newToggle(diagram, label, toolId, state, iconTrue, iconFalse);
      groupContainer.layout();
    } else System.err.println("cannot find group " + groupName);
  }

  public void newAction(Diagram diagram, String groupName, String label, String toolId, String icon) {
    Group group = getGroup(groupName);
    if (group != null) {
      group.newAction(diagram, label, toolId, icon);
      groupContainer.layout();
    } else System.err.println("cannot find group " + groupName);
  }

  public void newTool(Diagram diagram, String groupName, String label, String toolId, boolean isEdge, String icon) {
    Group group = getGroup(groupName);
    if (group != null) {
      group.newTool(diagram, label, toolId, isEdge, icon);
      groupContainer.layout();
    } else System.err.println("cannot find group " + groupName);
  }

  public void reset() {
    for (Group group : groups)
      group.resetButtons();
    getGroup("Diagram").getToolLabelled("Select").select();
  }

  public void writeXML(PrintStream out) {
    out.print("<Palette>");
    for (Group group : groups) {
      if (!group.getName().equals("Diagram")) {
        group.writeXML(out);
      }
    }
    out.print("</Palette>");
  }

  public void deselect() {
    for (Group group : groups)
      group.deselect();
  }
  
  public void removeAny(Diagram diagram, String toolId) {
	for (Group group : groups) {
	  Tool toolToBeRemoved = null;
	  for(Tool tool : group.tools) {
		if(tool.id.equals(toolId)) {
	      toolToBeRemoved = tool; break;
		}
	  }
	  group.removeTool(toolToBeRemoved);
	}
  }

  public void renameAny(Diagram diagram, final String newName, final String oldName) {
	for (Group group : groups) {
	  for(Tool tool : group.tools) {
		if(tool.id.equals(oldName)) {
			tool.setID(newName);
		}
	  }
	}
  }
  
}
