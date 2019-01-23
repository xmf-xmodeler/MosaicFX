package tool.clients.graphviz;

public class Menu extends Element {

  String   text;
  String[] options;

  public Menu(String text, String[] options) {
    super();
    this.text = text;
    this.options = options;
  }

  public String getDotSource() {
    String s = "<div class='dropdown'>";
    s = s + "<input type='submit' class='dropbtn' onclick='myFunction()' name='ignore' value='" + text + "'>";
    s = s + "<div id='myDropdown' class='dropdown-content'>";
    s = s + "<table cellborder='0'>";
    for (String option : options)
      s = s + "<tr><td><input type='submit' name='" + text + "' value='" + option + "'</td></td>";
    return s + "</table><div></div>";
  }

}
