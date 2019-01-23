package tool.console;

public class AutoComplete {

  public static AutoComplete newDefault() {
    return new AutoComplete();
  }

  private boolean MAIN_SWITCH             = true;
  private boolean DISPLAY_OPTIONS         = true;  // Display options based on the type of the input.
  private boolean COLON_ADD_PATH          = true;  // We might have a :: where there is a path to the left ...
  private boolean ARROW_FILL_PATTERNS     = false; // We might have a -> and can fill in the standard patterns ...
  private boolean SQUARE_START_COLLECTION = false; // Are we starting a collection?
  private boolean COMPLETE_PARENTHESIS    = true;  // Insert the corresponding parenthesis...
  private boolean COMPLETE_QUOTES         = false; // Insert the corresponding close string...

  public void toggleMainSwitch() {
    MAIN_SWITCH = !MAIN_SWITCH;
  }

  public boolean isDisplayOptions() {
    return MAIN_SWITCH && DISPLAY_OPTIONS;
  }

  public boolean isColonAddPath() {
    return MAIN_SWITCH && COLON_ADD_PATH;
  }

  public boolean isRightArrowFillPatterns() {
    return MAIN_SWITCH && ARROW_FILL_PATTERNS;
  }

  public boolean isSquareStartCollection() {
    return MAIN_SWITCH && SQUARE_START_COLLECTION;
  }

  public boolean isNineAddParenthesis() {
    return MAIN_SWITCH && COMPLETE_PARENTHESIS;
  }

  public boolean isApostropheAddQuotes() {
    return MAIN_SWITCH && COMPLETE_QUOTES;
  }

}
