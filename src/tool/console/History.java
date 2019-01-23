package tool.console;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class History.
 */
public class History extends Vector<String> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 0;

  /** The history size. */
  public static int historySize      = 10;

  /** The command pointer. */
  public int        commandPointer   = 0;

  /**
   * Adds the.
   *
   * @param input
   *          the input
   * @return
   */
  public boolean add(String input) {

    // if the new command is the same as the one just pushed
    // ignore it

    if (!(size() > 0 && input.equals(elementAt(size() - 1)) && input.equals(""))) {

      // if we've reached the maximum size for the history
      // get rid of the old history

      if (size() + 1 >= historySize) {
        while (size() + 1 != historySize)
          removeElementAt(0);
      }

      // add the element to the history

      addElement(input);
      // printState();
    }
    resetCommandPointer();
    return true;
  }

  /**
   * Gets the previous.
   *
   * @return the previous
   */
  public String getPrevious() {

    // if there is a previous command

    if (size() > 0) {
      if (commandPointer - 1 < 0) {
        commandPointer = size() - 1;
        // printState();
        return (String) elementAt(commandPointer);
      } else {
        commandPointer = commandPointer - 1;
        // printState();
        return (String) elementAt(commandPointer);
      }
    }
    return "";
  }

  /**
   * Gets the next.
   *
   * @return the next
   */
  public String getNext() {

    // if there is a next command

    if (size() > 0) {
      if (commandPointer + 1 >= size()) {
        commandPointer = 0;
        // printState();
        return (String) elementAt(commandPointer);
      } else {
        commandPointer = commandPointer + 1;
        // printState();
        return (String) elementAt(commandPointer);
      }
    }
    return "";
  }

  /**
   * Prints the state.
   */
  public void printState() {
    for (int i = 0; i < size(); i++) {
      String command = i + " : " + (String) elementAt(i);
      if (i == commandPointer) command = command + "<<<";
      System.out.println(command);
    }
  }

  /**
   * Reset command pointer.
   */
  public void resetCommandPointer() {
    commandPointer = size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Vector#setSize(int)
   */
  public void setSize(int size) {
    historySize = size;
  }
}