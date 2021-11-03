package tool.clients.editors.texteditor;

import tool.clients.editors.EditorClient;
import xos.Message;
import xos.Value;

public class Action {

  String   name;
  Object[] args;
  int      charStart;
  int      charEnd;

  public Action(String name, Object[] args, int charStart, int charEnd) {
    super();
    this.name = name;
    this.args = args;
    this.charStart = charStart;
    this.charEnd = charEnd;
  }

  public boolean containsOffset(int offset) {
    return charStart <= offset && charEnd >= offset;
  }

  public void perform(int x, int y) {
    if (name.equals("edit") && args.length == 1 && args[0] instanceof String) {
      String name = (String) args[0];
      Message message = EditorClient.theClient().getHandler().newMessage("openHomeFile", 1);
      message.args[0] = new Value(name);
      EditorClient.theClient().getHandler().raiseEvent(message);
    }
  }
}
