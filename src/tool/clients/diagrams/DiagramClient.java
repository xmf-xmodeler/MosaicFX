package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class DiagramClient extends Client {

  public static void start(TabPane tabPane) {
    DiagramClient.tabPane = tabPane;
  }

  public static DiagramClient theClient() {
    return theClient;
  }

  static DiagramClient               theClient;
  static TabPane                  	 tabPane;
  private static Hashtable<String, Tab> tabs = new Hashtable<String, Tab>();
  private static Vector<Diagram>             diagrams          = new Vector<Diagram>();
  transient static Vector<Diagram>   newlyCreatedDiagrams;
  static Font                        diagramFont       ;//= new Font(XModeler.getXModeler().getDisplay(), new FontData("Courier New", 12, SWT.NO));
  static javafx.scene.text.Font      diagramFontFX       ;//= new Font(XModeler.getXModeler().getDisplay(), new FontData("Courier New", 12, SWT.NO));
  static Font                        diagramItalicFont ;//= new Font(XModeler.getXModeler().getDisplay(), new FontData("Courier New", 12, SWT.ITALIC)); 
  // static java.awt.Font diagramFont_AWT = new java.awt.Font("Courier New", java.awt.Font.PLAIN, 16);

  public DiagramClient() {
    super("com.ceteva.diagram");
    theClient = this;
//    tabFolder.addCTabFolder2Listener(this);
  }

  public Value callMessage(Message message) {
    if (message.hasName("getTextDimension"))
      return getTextDimension(message);
//    else if (message.hasName("getTextDimensionWithFont"))
//      return getTextDimensionWithFont(message);
//    else if (message.hasName("getPalette"))
//      return getPalette(message);
    else return super.callMessage(message);
  }
/*
  public void close(CTabFolderEvent event) {
    CTabItem item = (CTabItem) event.item;
    String id = getId(item);
    Diagram diagram = getDiagram(id);
    if (diagram != null) {
      EventHandler handler = getHandler();
      Message message = handler.newMessage("diagramClosed", 1);
      message.args[0] = new Value(id);
      handler.raiseEvent(message);
      diagrams.remove(diagram);
      tabs.remove(id);
    }
  }
  private void copyToClipboard(Message message) {
    String id = message.args[0].strValue();
    copyToClipboard(id);
  }

  private void copyToClipboard(final String id) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.copyToClipboard(id);
      }
    });
  }

  private void delete(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          diagram.delete(id.strValue());
        }
      }
    });
  }

  private void deleteGroup(Message message) {
    final String id = message.args[0].strValue();
    final String name = message.args[1].strValue();
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams) {
          if (diagram.getId().equals(id)) {
            diagram.deleteGroup(name);
            diagram.redraw();
          }
        }
      }
    });
  }

  private void editText(Message message) {
    final Value id = message.args[0];
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.editText(id.strValue());
      }
    });
  }
*/
  private Diagram getDiagram(String id) {
    for (Diagram diagram : diagrams)
      if (diagram.getId().equals(id)) return diagram;
    return null;
  }
/*
  private String getId(CTabItem item) {
    for (String id : tabs.keySet())
      if (tabs.get(id) == item) return id;
    return null;
  }

  private Value getPalette(Message message) {
    String id = message.args[0].strValue();
    for (Diagram diagram : diagrams)
      if (diagram.getId().equals(id)) { return diagram.getPalette().asValue(); }
    return new Value(new Value[0]);
  }

  // private Diagram getSelectedDiagram() { UNUSED?
  // CTabItem item = tabFolder.getSelection();
  // for (String id : tabs.keySet()) {
  // if (tabs.get(id) == item) { return getDiagram(id); }
  // }
  // throw new Error("cannot find the current diagram");
  // }
 */
  /*
   * These functions calculate the size for a String for use in XMF's layouts. Unfortunately, hi-dpi changes the font size, so we need to get that factor out of the dimensions again. The value sent to XMF are those AS IF the font size were at 100%
   */
  private Value getTextDimension(final Message message) {
    final Value[] result = new Value[1];
//    runOnDisplay(new Runnable() {
//      public void run() {
        Value text = message.args[0];

        // FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        // double awtWidth = diagramFont_AWT.getStringBounds(text.strValue(), frc).getWidth()+2;
        // double awtHeight = diagramFont_AWT.getStringBounds(text.strValue(), frc).getHeight();

        // Value italics = message.args[1];
        javafx.geometry.Point2D extent = textDimension(text.strValue(), diagramFontFX);

        // FontDescriptor myDescriptor = FontDescriptor.createFrom(DiagramClient.diagramFont).setHeight(12 * 100 / XModeler.getDeviceZoomPercent());
        // Font zoomFont = myDescriptor.createFont(XModeler.getXModeler().getDisplay());
        //
        // Point extentTest = textDimension(text.strValue(), zoomFont);
        // System.err.println("width: " + extent.x + "\nadjusted to: " + (extent.x*100/XModeler.getDeviceZoomPercent())
        // + "\nwould be readjusted to: " + extentTest.x+ "\nfont: " + diagramFont
        // + "\nawt size: " + awtWidth);
        Value width = new Value((int)(extent.getX()+.5));// *100/XModeler.getDeviceZoomPercent());
        Value height = new Value((int)(extent.getY()+.5));// *100/XModeler.getDeviceZoomPercent());
        System.err.println("getTextDimension -> " + extent.getX() + "x" + extent.getY()); 
        // Value width = new Value((int)(awtWidth+.5));
        // Value height = new Value((int)(awtHeight+.5));
        result[0] = new Value(new Value[] { width, height });
//      }
//    });
    return result[0];
  }
  /*
  private Value getTextDimensionWithFont(final Message message) {
    final Value[] result = new Value[1];
    runOnDisplay(new Runnable() {
      public void run() {
        Value text = message.args[0];
        Value fontData = message.args[1];
        Point extent = textDimension(text.strValue(), new Font(XModeler.getXModeler().getDisplay(), new FontData(fontData.strValue())));
        Value width = new Value(extent.x * 100 / XModeler.getDeviceZoomPercent());
        Value height = new Value(extent.y * 100 / XModeler.getDeviceZoomPercent());
        result[0] = new Value(new Value[] { width, height });
      }
    });
    return result[0];
  }

  private void globalRenderOff() {
    for (Diagram diagram : diagrams)
      diagram.renderOff();
  }

  private void globalRenderOn() {
    for (Diagram diagram : diagrams)
      diagram.renderOn();
  }

  private void inflateBox(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    int curve = Integer.parseInt(XModeler.attributeValue(node, "curve"));
    boolean top = XModeler.attributeValue(node, "top").equals("true");
    boolean right = XModeler.attributeValue(node, "right").equals("true");
    boolean bottom = XModeler.attributeValue(node, "bottom").equals("true");
    boolean left = XModeler.attributeValue(node, "left").equals("true");
    int lineRed = Integer.parseInt(XModeler.attributeValue(node, "lineRed"));
    int lineGreen = Integer.parseInt(XModeler.attributeValue(node, "lineGreen"));
    int lineBlue = Integer.parseInt(XModeler.attributeValue(node, "lineBlue"));
    int fillRed = Integer.parseInt(XModeler.attributeValue(node, "fillRed"));
    int fillGreen = Integer.parseInt(XModeler.attributeValue(node, "fillGreen"));
    int fillBlue = Integer.parseInt(XModeler.attributeValue(node, "fillBlue"));
    newBox(parentId, id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateNodeElement(id, children.item(i));
  }

  private void inflateTool(String diagramId, String groupId, Node tool) {
    String toolType = tool.getNodeName();
    if (toolType.equals("EdgeCreationTool"))
      inflateEdgeCreationTool(diagramId, groupId, tool);
    else if (toolType.equals("NodeCreationTool"))
      inflateNodeCreationTool(diagramId, groupId, tool);
    else if (toolType.equals("ToggleTool"))
      inflateToggleTool(diagramId, groupId, tool);
    else if (toolType.equals("ActionTool"))
      inflateActionTool(diagramId, groupId, tool);
    else System.err.println("unknown type of tool: " + toolType);
  }

  private void inflateEdgeCreationTool(String diagramId, String groupId, Node tool) {
    String label = XModeler.attributeValue(tool, "label");
    String id = XModeler.attributeValue(tool, "id");
    String icon = XModeler.attributeValue(tool, "icon");
    newTool(diagramId, groupId, label, id, true, icon);
  }

  private void inflateNodeCreationTool(String diagramId, String groupId, Node tool) {
    String label = XModeler.attributeValue(tool, "label");
    String id = XModeler.attributeValue(tool, "id");
    String icon = XModeler.attributeValue(tool, "icon");
    newTool(diagramId, groupId, label, id, false, icon);
  }

  private void inflateToggleTool(String diagramId, String groupId, Node tool) {
    String label = XModeler.attributeValue(tool, "label");
    String id = XModeler.attributeValue(tool, "id");
    boolean state = XModeler.attributeValue(tool, "state").equals("true");
    String icon = XModeler.attributeValue(tool, "icon");
    String icon2 = icon;
    try {
      icon2 = XModeler.attributeValue(tool, "icon2");
    } catch (Exception e) {
    }
    newToggle(diagramId, groupId, label, id, state, icon, icon2);
  }

  private void inflateActionTool(String diagramId, String groupId, Node tool) {
    String label = XModeler.attributeValue(tool, "label");
    String id = XModeler.attributeValue(tool, "id");
    String icon = XModeler.attributeValue(tool, "icon");
    newAction(diagramId, groupId, label, id, icon);
  }

  private void inflateDiagram(Node diagram) {
    String id = XModeler.attributeValue(diagram, "id");
    String label = XModeler.attributeValue(diagram, "label");
    boolean magnetic = XModeler.attributeValue(diagram, "magnetic").equals("true");
    float zoom = Float.parseFloat(XModeler.attributeValue(diagram, "zoom", "1.0"));
    newDiagram(id, label);
    Diagram d = getDiagram(id);
    d.setMagneticWaypoints(magnetic);
    d.renderOff();
    d.setZoom(zoom);
    NodeList children = diagram.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateDiagramElement(id, children.item(i));
    d.align();
    d.deselectAll();
    d.renderOn();
    d.resetPalette();
  }

  private void inflateDiagramEdge(String parentId, Node edge) {
    String id = XModeler.attributeValue(edge, "id");
    // int refx = Integer.parseInt(XModeler.attributeValue(edge, "refx"));
    // int refy = Integer.parseInt(XModeler.attributeValue(edge, "refy"));
    // String source = XModeler.attributeValue(edge, "source");
    // String target = XModeler.attributeValue(edge, "target");
    String sourcePort = XModeler.attributeValue(edge, "sourcePort");
    String targetPort = XModeler.attributeValue(edge, "targetPort");
    int sourceHead = Integer.parseInt(XModeler.attributeValue(edge, "sourceHead"));
    int targetHead = Integer.parseInt(XModeler.attributeValue(edge, "targetHead"));
    int lineStyle = Integer.parseInt(XModeler.attributeValue(edge, "lineStyle"));
    int red = Integer.parseInt(XModeler.attributeValue(edge, "red"));
    int green = Integer.parseInt(XModeler.attributeValue(edge, "green"));
    int blue = Integer.parseInt(XModeler.attributeValue(edge, "blue"));
    Integer sourceX = null;
    Integer sourceY = null;
    Integer targetX = null;
    Integer targetY = null;
    try {
      sourceX = Integer.parseInt(XModeler.attributeValue(edge, "sourceX"));
      sourceY = Integer.parseInt(XModeler.attributeValue(edge, "sourceY"));
      targetX = Integer.parseInt(XModeler.attributeValue(edge, "targetX"));
      targetY = Integer.parseInt(XModeler.attributeValue(edge, "targetY"));
    } catch (Exception ex) {
    }
    if (sourceX == null || sourceY == null || targetX == null || targetY == null) System.err.println("No edge termination points specified. Using centre instead");
    newEdge(parentId, id, sourcePort, targetPort, -1, -1, sourceHead, targetHead, lineStyle, red, green, blue, sourceX, sourceY, targetX, targetY);
    NodeList children = edge.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateEdgeElement(id, children.item(i));
  }

  private void inflateDiagramElement(String id, Node node) {
    if (node.getNodeName().equals("Palette"))
      inflatePalette(id, node);
    else if (node.getNodeName().equals("Node"))
      inflateDiagramNode(id, node);
    else if (node.getNodeName().equals("Edge"))
      inflateDiagramEdge(id, node);
    else if (node.getNodeName().equals("Box"))
      inflateBox(id, node);
    else if (node.getNodeName().equals("Text"))
      inflateText(id, node);
    else if (node.getNodeName().equals("MultilineText"))
      inflateMultilineText(id, node);
    else if (node.getNodeName().equals("Ellipse"))
      inflateEllipse(id, node);
    else if (node.getNodeName().equals("Image"))
      inflateImage(id, node);
    else System.err.println("Unknown type of diagram node " + node.getNodeName());
  }

  private void inflateDiagramNode(String diagramId, Node node) {
    String nodeId = XModeler.attributeValue(node, "id");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    boolean selectable = XModeler.attributeValue(node, "selectable").equals("true");
    newNode(diagramId, nodeId, x, y, width, height, selectable);
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
      inflateNodeElement(nodeId, children.item(i));
  }

  private void inflateEdgeElement(String id, Node node) {
    if (node.getNodeName().equals("Waypoint"))
      inflateWaypoint(id, node);
    else if (node.getNodeName().equals("Label"))
      inflateLabel(id, node);
    else System.err.println("Unknown type of edge element " + node.getNodeName());
  }

  private void inflateEllipse(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    boolean showOutline = XModeler.attributeValue(node, "showOutline").equals("true");
    int lineRed = Integer.parseInt(XModeler.attributeValue(node, "lineRed"));
    int lineGreen = Integer.parseInt(XModeler.attributeValue(node, "lineGreen"));
    int lineBlue = Integer.parseInt(XModeler.attributeValue(node, "lineBlue"));
    int fillRed = Integer.parseInt(XModeler.attributeValue(node, "fillRed"));
    int fillGreen = Integer.parseInt(XModeler.attributeValue(node, "fillGreen"));
    int fillBlue = Integer.parseInt(XModeler.attributeValue(node, "fillBlue"));
    newEllipse(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
  }

  private void inflateGroup(String id, Node group) {
    String name = XModeler.attributeValue(group, "name");
    newGroup(id, name);
    NodeList buttons = group.getChildNodes();
    for (int i = 0; i < buttons.getLength(); i++)
      inflateTool(id, name, buttons.item(i));
  }

  private void inflateImage(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    String fileName = XModeler.attributeValue(node, "fileName");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    newImage(parentId, id, fileName, x, y, width, height);
  }

  private void inflateLabel(String edgeId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    String text = XModeler.attributeValue(node, "text");
    String pos = XModeler.attributeValue(node, "pos");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    boolean editable = XModeler.attributeValue(node, "editable", "true").equals("true");
    boolean underline = XModeler.attributeValue(node, "underline").equals("true");
    boolean condense = XModeler.attributeValue(node, "condense").equals("true");
    int red = Integer.parseInt(XModeler.attributeValue(node, "red"));
    int green = Integer.parseInt(XModeler.attributeValue(node, "green"));
    int blue = Integer.parseInt(XModeler.attributeValue(node, "blue"));
    boolean border = "true".equals(XModeler.attributeValue(node, "border"));
    int borderRed = border ? 0 : Integer.parseInt(XModeler.attributeValue(node, "borderRed"));
    int borderGreen = border ? 0 : Integer.parseInt(XModeler.attributeValue(node, "borderGreen"));
    int borderBlue = border ? 0 : Integer.parseInt(XModeler.attributeValue(node, "borderBlue"));
    int arrow = 0;
    try {
      arrow = Integer.parseInt(XModeler.attributeValue(node, "arrow"));
    } catch (Exception e) {
    }
    String font = XModeler.attributeValue(node, "font");
    boolean hidden = false;
    try {
      hidden = "true".equals(XModeler.attributeValue(node, "hidden"));
    } catch (Exception e) {
    }
    boolean fill = false;
    try {
      fill = "true".equals(XModeler.attributeValue(node, "fill"));
    } catch (Exception e) {
    }
    newLabel(edgeId, id, text, pos, x, y, editable, underline, condense, red, green, blue, border, borderRed, borderGreen, borderBlue, font, arrow, hidden, fill);
  }

  private void inflateMultilineText(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    String text = XModeler.attributeValue(node, "text");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    boolean editable = XModeler.attributeValue(node, "editable").equals("true");
    int lineRed = Integer.parseInt(XModeler.attributeValue(node, "lineRed"));
    int lineGreen = Integer.parseInt(XModeler.attributeValue(node, "lineGreen"));
    int lineBlue = Integer.parseInt(XModeler.attributeValue(node, "lineBlue"));
    int fillRed = Integer.parseInt(XModeler.attributeValue(node, "fillRed"));
    int fillGreen = Integer.parseInt(XModeler.attributeValue(node, "fillGreen"));
    int fillBlue = Integer.parseInt(XModeler.attributeValue(node, "fillBlue"));
    String font = XModeler.attributeValue(node, "font");
    newMultilineText(parentId, id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
  }

  private void inflateNodeElement(String id, Node node) {
    if (node.getNodeName().equals("Port"))
      inflatePort(id, node);
    else if (node.getNodeName().equals("Box"))
      inflateBox(id, node);
    else if (node.getNodeName().equals("Text"))
      inflateText(id, node);
    else if (node.getNodeName().equals("MultilineText"))
      inflateMultilineText(id, node);
    else if (node.getNodeName().equals("Ellipse"))
      inflateEllipse(id, node);
    else if (node.getNodeName().equals("Image"))
      inflateImage(id, node);
    else System.err.println("Unknown type of node element " + node.getNodeName());
  }

  private void inflatePalette(String id, Node node) {
    NodeList groups = node.getChildNodes();
    for (int i = 0; i < groups.getLength(); i++)
      inflateGroup(id, groups.item(i));
  }

  private void inflatePort(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    int width = Integer.parseInt(XModeler.attributeValue(node, "width"));
    int height = Integer.parseInt(XModeler.attributeValue(node, "height"));
    newPort(parentId, id, x, y, width, height);
  }

  private void inflateText(String parentId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    String text = XModeler.attributeValue(node, "text");
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    boolean editable = XModeler.attributeValue(node, "editable").equals("true");
    boolean underline = XModeler.attributeValue(node, "underline").equals("true");
    boolean italicise = XModeler.attributeValue(node, "italicise").equals("true");
    int red = Integer.parseInt(XModeler.attributeValue(node, "red"));
    int green = Integer.parseInt(XModeler.attributeValue(node, "green"));
    int blue = Integer.parseInt(XModeler.attributeValue(node, "blue"));
    newText(parentId, id, text, x, y, editable, underline, italicise, red, green, blue);
  }

  private void inflateWaypoint(String edgeId, Node node) {
    String id = XModeler.attributeValue(node, "id");
    int index = Integer.parseInt(XModeler.attributeValue(node, "index"));
    int x = Integer.parseInt(XModeler.attributeValue(node, "x"));
    int y = Integer.parseInt(XModeler.attributeValue(node, "y"));
    newWaypoint(edgeId, id, index, x, y, true);
  }

  public void inflateXML(Document doc) {
    NodeList diagramClients = doc.getElementsByTagName("Diagrams");
    if (diagramClients.getLength() == 1) {
      Node diagramClient = diagramClients.item(0);
      NodeList diagrams = diagramClient.getChildNodes();
      for (int i = 0; i < diagrams.getLength(); i++) {
        Node diagram = diagrams.item(i);
        inflateDiagram(diagram);
      }
    } else System.err.println("expecting exactly 1 diagram client got: " + diagramClients.getLength());
  }

  private void italicise(Message message) {
    String id = message.args[0].strValue();
    boolean italics = message.args[1].boolValue;
    italicise(id, italics);
  }

  private void italicise(final String id, final boolean italics) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.italicise(id, italics);
      }
    });
  }

  public void maximize(CTabFolderEvent event) {
  }

  public void minimize(CTabFolderEvent event) {

  }

  private void move(Message message) {
    Value id = message.args[0];
    Value x = message.args[1];
    Value y = message.args[2];
    // System.err.println("DiagramClient->move(" + id.strValue() +", "+ x.intValue +", "+ y.intValue +")");
    for (Diagram diagram : diagrams)
      diagram.move(id.strValue(), x.intValue, y.intValue);
  }
*/
  private void newBox(Message message) {
    final String parentId = message.args[0].strValue();
    final String id = message.args[1].strValue();
    final int x = message.args[2].intValue;
    final int y = message.args[3].intValue;
    final int width = message.args[4].intValue;
    final int height = message.args[5].intValue;
    final int curve = message.args[6].intValue;
    final boolean top = message.args[7].boolValue;
    final boolean right = message.args[8].boolValue;
    final boolean bottom = message.args[9].boolValue;
    final boolean left = message.args[10].boolValue;
    final int lineRed = message.args[11].intValue;
    final int lineGreen = message.args[12].intValue;
    final int lineBlue = message.args[13].intValue;
    final int fillRed = message.args[14].intValue;
    final int fillGreen = message.args[15].intValue;
    final int fillBlue = message.args[16].intValue;
    newBox(parentId, id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
  }

  private void newBox(String parentId, String id, int x, int y, int width, int height, int curve, boolean top, boolean right, boolean bottom, boolean left, int lineRed, int lineGreen, int lineBlue, int fillRed, int fillGreen, int fillBlue) {
    for (Diagram diagram : diagrams) {
      diagram.newBox(parentId, id, x, y, width, height, curve, top, right, bottom, left, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
    }
  }

  private void newDiagram(Message message) {
    final Value id = message.args[0];
    final Value label = message.args[1];
    newDiagram(id.strValue(), label.strValue());
  }
/*
  private void newNestedDiagram(Message message) {
    final Value parentId = message.args[0];
    final Value groupId = message.args[1];
    final Value x = message.args[2];
    final Value y = message.args[3];
    final Value width = message.args[4];
    final Value height = message.args[5];
    newlyCreatedDiagrams = new Vector<Diagram>(); // to avoid java.util.ConcurrentModificationException
    newNestedDiagram(parentId.strValue(), groupId.strValue(), x.intValue, y.intValue, width.intValue, height.intValue);
    diagrams.addAll(newlyCreatedDiagrams);
    newlyCreatedDiagrams = null;
  }
*/
	private void newDiagram(final String id, final String label) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			System.err.println("Create Diagram...");
		
			Diagram diagram = new Diagram(id, null);
			Tab tab = new Tab(label);
			tab.setContent(diagram.getView());
			tab.setClosable(true);			
	        tabs.put(id, tab);    
	        diagrams.add(diagram);
	        tabPane.getTabs().add(tab);
	        tabPane.getSelectionModel().selectLast();

	        l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
/*
  private void newNestedDiagram(final String parentId, final String id, int x, int y, int width, int height) {
    // System.err.println("diagramClient->newNestedDiagram("+parentId+"->"+id+")");
    for (Diagram diagram : diagrams) {
      diagram.newNestedDiagram(parentId, id, x, y, width, height, null);
    }
  }

  private void newEdge(final Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String sourceId = message.args[2].strValue();
    String targetId = message.args[3].strValue();
    int refx = message.args[4].intValue;
    int refy = message.args[5].intValue;
    int sourceHead = message.args[6].intValue;
    int targetHead = message.args[7].intValue;
    int lineStyle = message.args[8].intValue;
    int red = message.args[9].intValue;
    int green = message.args[10].intValue;
    int blue = message.args[11].intValue;
    Integer sourceX = message.arity > 12 ? message.args[12].intValue : null;
    Integer sourceY = message.arity > 13 ? message.args[13].intValue : null;
    Integer targetX = message.arity > 14 ? message.args[14].intValue : null;
    Integer targetY = message.arity > 15 ? message.args[15].intValue : null;
    if (sourceX == null || sourceY == null || targetX == null || targetY == null) System.err.println("No edge termination points specified. Using centre instead");
    newEdge(parentId, id, sourceId, targetId, refx, refy, sourceHead, targetHead, lineStyle, red, green, blue, sourceX, sourceY, targetX, targetY);
  }

  private void newEdge(String parentId, String id, String sourceId, String targetId, int refx, int refy, int sourceHead, int targetHead, int lineStyle, int red, int green, int blue, int sourceX, int sourceY, int targetX, int targetY) {
    for (Diagram diagram : diagrams) {
      if (diagram.getId().equals(parentId)) {
        diagram.newEdge(id, sourceId, targetId, refx, refy, sourceHead, targetHead, lineStyle, red, green, blue, sourceX, sourceY, targetX, targetY);
      }
    }
  }

  private void newEllipse(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int x = message.args[2].intValue;
    int y = message.args[3].intValue;
    int width = message.args[4].intValue;
    int height = message.args[5].intValue;
    boolean showOutline = message.args[6].boolValue;
    int lineRed = message.args[7].intValue;
    int lineGreen = message.args[8].intValue;
    int lineBlue = message.args[9].intValue;
    int fillRed = message.args[10].intValue;
    int fillGreen = message.args[11].intValue;
    int fillBlue = message.args[12].intValue;
    newEllipse(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
  }

  private void newShape(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int x = message.args[2].intValue;
    int y = message.args[3].intValue;
    int width = message.args[4].intValue;
    int height = message.args[5].intValue;
    boolean showOutline = message.args[6].boolValue;
    int lineRed = message.args[7].intValue;
    int lineGreen = message.args[8].intValue;
    int lineBlue = message.args[9].intValue;
    int fillRed = message.args[10].intValue;
    int fillGreen = message.args[11].intValue;
    int fillBlue = message.args[12].intValue;
    int[] points = new int[message.args[13].values.length];

    for (int i = 0; i < message.args[13].values.length; i++) {
      points[i] = message.args[13].values[i].intValue;
    }

    newShape(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, points);
  }

  private void newEllipse(final String parentId, final String id, final int x, final int y, final int width, final int height, final boolean showOutline, final int lineRed, final int lineGreen, final int lineBlue, final int fillRed, final int fillGreen, final int fillBlue) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram d : diagrams)
          d.newEllipse(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue);
      }
    });
  }

  private void newShape(final String parentId, final String id, final int x, final int y, final int width, final int height, final boolean showOutline, final int lineRed, final int lineGreen, final int lineBlue, final int fillRed, final int fillGreen, final int fillBlue, final int[] points) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram d : diagrams)
          d.newShape(parentId, id, x, y, width, height, showOutline, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, points);
      }
    });
  }
*/
  private void newGroup(final String diagramId, final String name) {
    if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.newGroup(name);
        }
      });
    } else System.err.println("cannot find diagram " + diagramId);
  }/*

  private void newImage(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String fileName = message.args[2].strValue();
    int x = message.args[3].intValue;
    int y = message.args[4].intValue;
    int width = message.args[5].intValue;
    int height = message.args[6].intValue;
    newImage(parentId, id, fileName, x, y, width, height);
  }

  private void newImage(final String parentId, final String id, final String fileName, final int x, final int y, final int width, final int height) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.newImage(parentId, id, fileName, x, y, width, height);
      }
    });
  }

  private void newLabel(final Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String text = message.args[2].strValue();
    String position = message.args[3].strValue();
    int x = message.args[4].intValue;
    int y = message.args[5].intValue;
    Boolean editable = message.args[6].boolValue;
    Boolean underline = message.args[7].boolValue;
    Boolean condense = message.args[8].boolValue;
    int red = message.args[9].intValue;
    int green = message.args[10].intValue;
    int blue = message.args[11].intValue;
    String font = message.args[12].strValue();
    boolean border = message.args[13].boolValue;
    int borderRed = message.args[14].intValue;
    int borderGreen = message.args[15].intValue;
    int borderBlue = message.args[16].intValue;
    int arrow = message.args[17].intValue;
    boolean hidden = false;
    try {
      hidden = message.args[18].boolValue;
    } catch (Exception e) {
    }
    boolean fill = false;
    try {
      fill = message.args[19].boolValue;
    } catch (Exception e) {
    }
    newLabel(parentId, id, text, position, x, y, editable, underline, condense, red, green, blue, border, borderRed, borderGreen, borderBlue, font, arrow, hidden, fill);
  }

  private void newLabel(String parentId, String id, String text, String position, int x, int y, Boolean editable, Boolean underline, Boolean condense, int red, int green, int blue, boolean border, int borderRed, int borderGreen, int borderBlue, String font, int arrow, boolean hidden, boolean fill) {
    for (Diagram diagram : diagrams) {
      for (Edge edge : diagram.getEdges()) {
        if (edge.getId().equals(parentId)) {
          edge.addLabel(id, text, position, x, y, editable, underline, condense, red, green, blue, border, borderRed, borderGreen, borderBlue, font, arrow, hidden, fill);
        }
      }
    }
  }

  private void newMultilineText(Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    String text = message.args[2].strValue();
    int x = message.args[3].intValue;
    int y = message.args[4].intValue;
    int width = message.args[5].intValue;
    int height = message.args[6].intValue;
    boolean editable = message.args[6].boolValue;
    int lineRed = message.args[7].intValue;
    int lineGreen = message.args[8].intValue;
    int lineBlue = message.args[9].intValue;
    int fillRed = message.args[10].intValue;
    int fillGreen = message.args[11].intValue;
    int fillBlue = message.args[12].intValue;
    String font = message.args[13].strValue();
    newMultilineText(parentId, id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
  }

  private void newMultilineText(final String parentId, final String id, final String text, final int x, final int y, final int width, final int height, final boolean editable, final int lineRed, final int lineGreen, final int lineBlue, final int fillRed, final int fillGreen, final int fillBlue, final String font) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram d : diagrams)
          d.newMultilineText(parentId, id, text, x, y, width, height, editable, lineRed, lineGreen, lineBlue, fillRed, fillGreen, fillBlue, font);
      }
    });
  }
*/
  private void newNode(Message message) {
    Value parentId = message.args[0];
    Value id = message.args[1];
    Value x = message.args[2];
    Value y = message.args[3];
    Value width = message.args[4];
    Value height = message.args[5];
    Value selectable = message.args[6];
    newNode(parentId.strValue(), id.strValue(), x.intValue, y.intValue, width.intValue, height.intValue, selectable.boolValue);
  }

  protected void newNode(String type, String id, int x, int y) {
    Message m = getHandler().newMessage("newNode", 4);
    m.args[0] = new Value(type);
    m.args[1] = new Value(id);
    m.args[2] = new Value(x);
    m.args[3] = new Value(y);
    getHandler().raiseEvent(m);
  }

  private void newNode(String parentId, String id, int x, int y, int width, int height, boolean selectable) {
    if (getDiagram(parentId) != null) {
      Diagram diagram = getDiagram(parentId);
      diagram.newNode(id, x, y, width, height, selectable);
    } else System.err.println("cannot find diagram " + parentId);
  }
/*
  private void newPort(Message message) {
    Value parentId = message.args[0];
    Value id = message.args[1];
    Value x = message.args[2];
    Value y = message.args[3];
    Value width = message.args[4];
    Value height = message.args[5];
    newPort(parentId.strValue(), id.strValue(), x.intValue, y.intValue, width.intValue, height.intValue);
  }

  private void newPort(String parentId, String id, int x, int y, int width, int height) {
    for (Diagram diagram : diagrams) {
      diagram.newPort(parentId, id, x, y, width, height);
    }
  }
*/
  private void newText(Message message) {
    final String parentId = message.args[0].strValue();
    final String id = message.args[1].strValue();
    final String text = message.args[2].strValue();
    final int x = message.args[3].intValue;
    final int y = message.args[4].intValue;
    final boolean editable = message.args[5].boolValue;
    final boolean underline = message.args[6].boolValue;
    final boolean italicise = message.args[7].boolValue;
    final int red = message.args[8].intValue;
    final int green = message.args[9].intValue;
    final int blue = message.args[10].intValue;
    newText(parentId, id, text, x, y, editable, underline, italicise, red, green, blue);
  }

  private void newText(final String parentId, final String id, final String text, final int x, final int y, final boolean editable, final boolean underline, final boolean italicise, final int red, final int green, final int blue) {
    for (Diagram diagram : diagrams) {
      diagram.newText(parentId, id, text, x, y, editable, underline, italicise, red, green, blue);
    }
  }
/*
  private void removeAny(Message message) {
    final Value diagramId = message.args[0];
    final Value toolId = message.args[1];
    removeAny(diagramId.strValue(), toolId.strValue());
  }

  private void renameAny(Message message) {
    final Value diagramId = message.args[0];
    final Value newName = message.args[1];
    final Value oldName = message.args[2];
    renameAny(diagramId.strValue(), newName.strValue(), oldName.strValue());
  }
*/
  private void newTool(Message message) {
    final Value diagramId = message.args[0];
    final Value groupId = message.args[1];
    final Value label = message.args[2];
    final Value toolId = message.args[3];
    final Value isEdge = message.args[4];
    final Value icon = message.args[5];
    newTool(diagramId.strValue(), groupId.strValue(), label.strValue(), toolId.strValue(), isEdge.boolValue, icon.strValue());
  }

  private void newToggle(Message message) {
    final Value diagramId = message.args[0];
    final Value groupId = message.args[1];
    final Value label = message.args[2];
    final Value toolId = message.args[3];
    final Value state = message.args[4];
    final Value iconTrue = message.args[5];
    final Value iconFalse = message.args[6];
    newToggle(diagramId.strValue(), groupId.strValue(), label.strValue(), toolId.strValue(), state.boolValue, iconTrue.strValue(), iconFalse.strValue());
  }

  private void newAction(Message message) {
    final Value diagramId = message.args[0];
    final Value groupId = message.args[1];
    final Value label = message.args[2];
    final Value toolId = message.args[3];
    final Value icon = message.args[4];
    newAction(diagramId.strValue(), groupId.strValue(), label.strValue(), toolId.strValue(), icon.strValue());
  }
/*
  private void removeAny(final String diagramId, final String toolId) {
    if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.removeAny(toolId);
        }
      });
    } else System.err.println("cannot find diagram " + diagramId);
  }

  private void renameAny(final String diagramId, final String newName, final String oldName) {
    if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.renameAny(newName, oldName);
        }
      });
    } else System.err.println("cannot find diagram " + diagramId);
  }
*/
  private void newTool(final String diagramId, final String groupId, final String label, final String toolId, final boolean isEdge, final String icon) {
	  if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.newTool(groupId, label, toolId, isEdge, icon);
        }
      });
    } else System.err.println("cannot find diagram " + diagramId);
  }

  private void newToggle(final String diagramId, final String groupId, final String label, final String toolId, final boolean state, final String iconTrue, final String iconFalse) {
    if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.newToggle(groupId, label, toolId, state, iconTrue, iconFalse);
        }
      });
    } else System.err.println("cannot find diagram " + diagramId);
  }

  private void newAction(final String diagramId, final String groupId, final String label, final String toolId, final String icon) {
    if (getDiagram(diagramId) != null) {
      runOnDisplay(new Runnable() {
        public void run() {
          Diagram diagram = getDiagram(diagramId);
          diagram.newAction(groupId, label, toolId, icon);
        }
      });
      if (label.equals("Update")) {
        getDiagram(diagramId).updateID = toolId;
        // addUpdateTimer(diagramId, toolId);
      }
    } else System.err.println("cannot find diagram " + diagramId);
  }
/*
  // public static int updateID = -1;

  // private void addUpdateTimer(final String diagramId, final String toolId) {
  // final int time = 2500;
  // runOnDisplay(new Runnable() {
  // public void run() {
  // Runnable timer = new Runnable() {
  // public void run() {
  // getDiagram(diagramId).action(toolId);
  // XModeler.getXModeler().getDisplay().timerExec(time, this);
  // }
  // };
  // XModeler.getXModeler().getDisplay().timerExec(time, timer);
  // }});
  // }
*/
  private void newToolGroup(Message message) {
    final Value diagramId = message.args[0];
    final Value name = message.args[1];
    newGroup(diagramId.strValue(), name.strValue());
  }
/*
  private void newWaypoint(final Message message) {
    String parentId = message.args[0].strValue();
    String id = message.args[1].strValue();
    int index = message.args[2].intValue;
    int x = message.args[3].intValue;
    int y = message.args[4].intValue;
    boolean skipSelection = false;
    try {
      skipSelection = message.args[5].boolValue;
    } catch (Exception e) {
      System.err.println("newWaypoint command message sent without 'skipSelection' parameter. Check in XMF!");
    }
    newWaypoint(parentId, id, index, x, y, skipSelection);
  }

  private void newWaypoint(String parentId, String id, int index, int x, int y, boolean skipSelection) {
    for (Diagram diagram : diagrams) {
      diagram.newWaypoint(parentId, id, index, x, y, skipSelection);
    }
  }

  public boolean processMessage(Message message) {
    return false;
  }

  private void resize(Message message) {
    final Value id = message.args[0];
    final Value width = message.args[1];
    final Value height = message.args[2];
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.resize(id.strValue(), width.intValue, height.intValue);
      }
    });
  }

  public void restore(CTabFolderEvent event) {

  }

*/

  public void sendMessage(final Message message) {
	
    if (message.hasName("newDiagram"))
      newDiagram(message); 
    /*else if (message.hasName("newGroup"))
      newNestedDiagram(message);
    else if (message.hasName("removeAny"))
      removeAny(message);
    else if (message.hasName("renameAny"))
      renameAny(message);*/
    else if (message.hasName("newToolGroup"))
      newToolGroup(message);/*
    else if (message.hasName("removeToolGroup"))
      removeAny(message);*/
    else if (message.hasName("newTool"))
      newTool(message);/*
    else if (message.hasName("removeTool"))
      removeAny(message);*/
    else if (message.hasName("newToggle"))
      newToggle(message);/*
    else if (message.hasName("removeToggle"))
      removeAny(message);*/
    else if (message.hasName("newAction"))
      newAction(message);/*
    else if (message.hasName("removeAction"))
      removeAny(message);*/
    else if (message.hasName("newNode"))
      newNode(message);/*
    else if (message.hasName("newPort"))
      newPort(message);*/
    else if (message.hasName("newBox"))
      newBox(message);
    else if (message.hasName("newText"))
      newText(message);/*
    else if (message.hasName("resize"))
      resize(message);
    else if (message.hasName("move"))
      move(message);
    else if (message.hasName("editText"))
      editText(message);
    else if (message.hasName("setBorder"))
      setBorder(message);
    else if (message.hasName("setFill"))
      setFill(message);
    else if (message.hasName("setText"))
      setText(message);
    else if (message.hasName("setTextColor"))
      setTextColor(message);
    else if (message.hasName("setName"))
      setName(message);
    else if (message.hasName("globalRenderOff"))
      globalRenderOff();
    else if (message.hasName("globalRenderOn"))
      globalRenderOn();
    else if (message.hasName("startRender"))
      startRender(message);
    else if (message.hasName("stopRender"))
      stopRender(message);
    else if (message.hasName("setFocus"))
      setFocus(message);
    else if (message.hasName("newEdge"))
      newEdge(message);
    else if (message.hasName("setRefPoint"))
      setRefPoint(message);
    else if (message.hasName("setEdgeStyle"))
      setEdgeStyle(message);
    else if (message.hasName("setColor"))
      setEdgeColor(message);
    else if (message.hasName("newEdgeText"))
      newLabel(message);
    else if (message.hasName("newWaypoint"))
      newWaypoint(message);
    else if (message.hasName("delete"))
      delete(message);
    else if (message.hasName("setEdgeTarget"))
      setEdgeTarget(message);
    else if (message.hasName("setEdgeSource"))
      setEdgeSource(message);
    else if (message.hasName("newMultilineText"))
      newMultilineText(message);
    else if (message.hasName("copyToClipboard"))
      copyToClipboard(message);
    else if (message.hasName("setFillColor"))
      setFillColor(message);
    else if (message.hasName("italicise"))
      italicise(message);
    else if (message.hasName("newEllipse"))
      newEllipse(message);
    else if (message.hasName("newShape"))
      newShape(message);
    else if (message.hasName("newImage"))
      newImage(message);
    else if (message.hasName("deleteGroup"))
      deleteGroup(message);
    else if (message.hasName("setFont"))
      setFont(message);
    else if (message.hasName("setMagneticWaypoints"))
      setMagneticWaypoints(message);
    else if (message.hasName("zoomIn"))
      zoomIn(message);
    else if (message.hasName("zoomOut"))
      zoomOut(message);
    else if (message.hasName("zoomOne"))
      zoomOne(message);
    else if (message.hasName("nestedZoomTo") || message.hasName("zoomTo"))
      zoomTo(message);
    else if (message.hasName("hide"))
      hide(message);
    else if (message.hasName("show"))
      show(message);
    else if (message.hasName("resetErrors"))
      resetErrors(message);
    else if (message.hasName("error"))
      error(message);
    else if (message.hasName("showEdges"))
      showEdges(message); // Bj�rn
    else if (message.hasName("setEditable"))
      setEditable(message); // Bj�rn */
    else 
    	System.err.println("send message to diagram Client: " + message);
//    else super.sendMessage(message);
  }

/*
  private void error(Message message) {
    String id = message.args[0].strValue();
    String error = message.args[1].strValue();
    if (error.length() > 0) {
      runOnDisplay(new Runnable() {
        public void run() {
          for (Diagram diagram : diagrams)
            diagram.error(id, error);
        }
      });
    }
  }

  private void resetErrors(Message message) {
    String id = message.args[0].strValue();
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          if (diagram.getId().equals(id)) diagram.resetErrors();
      }
    });
  }

  private void setEditable(Message message) { // Bj�rn
    String id = message.args[0].strValue();
    boolean editable = message.args[1].boolValue;
    setEditable(id, editable);
  }

  private void setEditable(final String id, final boolean editable) { // Bj�rn
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.setEditable(id, editable);
      }
    });
  }

  private void showEdges(Message message) { // Bj�rn
    String id = message.args[0].strValue();
    boolean top = message.args[1].boolValue;
    boolean bottom = message.args[2].boolValue;
    boolean left = message.args[3].boolValue;
    boolean right = message.args[4].boolValue;
    showEdges(id, top, bottom, left, right);
  }

  private void showEdges(final String id, final boolean top, final boolean bottom, final boolean left, final boolean right) { // Bj�rn
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.showEdges(id, top, bottom, left, right);
      }
    });
  }

  private void setTextColor(Message message) {
    String id = message.args[0].strValue();
    int red = message.args[1].intValue;
    int green = message.args[2].intValue;
    int blue = message.args[3].intValue;
    setTextColor(id, red, green, blue);
  }

  private void hide(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          diagram.hide(id.strValue());
          diagram.redraw();
        }
      }
    });
  }

  private void setTextColor(final String id, final int red, final int green, final int blue) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.setFillColor(id, red, green, blue);
      }
    });
  }

  private void show(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          diagram.show(id.strValue());
          diagram.redraw();
        }
      }
    });
  }

  private void zoomIn(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          if (diagram.getId().equals(id.strValue())) {
            diagram.zoomIn();
            diagram.redraw();
          }
        }
      }
    });
  }

  private void zoomOut(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          if (diagram.getId().equals(id.strValue())) {
            diagram.zoomOut();
            diagram.redraw();
          }
        }
      }
    });
  }

  private void zoomOne(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          if (diagram.getId().equals(id.strValue())) {
            diagram.zoomOne();
            diagram.redraw();
          }
        }
      }
    });
  }

  private void zoomTo(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams) {
          if (diagram.getId().equals(id.strValue())) {
            diagram.zoomTo(message.args[1].floatValue);
            diagram.redraw();
          }
        }
      }
    });
  }

  private void setMagneticWaypoints(Message message) {
    String id = message.args[0].strValue();
    boolean state = message.args[1].boolValue;
    for (Diagram d : diagrams) {
      if (d.getId().equals(id)) d.setMagneticWaypoints(state);
    }
  }

  private void setEdgeColor(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        Value red = message.args[1];
        Value green = message.args[2];
        Value blue = message.args[3];
        for (Diagram diagram : diagrams) {
          for (Edge edge : diagram.getEdges()) {
            if (edge.getId().equals(id.strValue())) {
              edge.setRed(red.intValue);
              edge.setGreen(green.intValue);
              edge.setBlue(blue.intValue);
            }
            for (Label label : edge.labels) {
              label.setTextColor(id.strValue(), red.intValue, green.intValue, blue.intValue);
            }
          }
          diagram.redraw();
        }
      }
    });
  }

  private void setEdgeSource(Message message) {
    String edgeId = message.args[0].strValue();
    String portId = message.args[1].strValue();
    setEdgeSource(edgeId, portId);
  }

  private void setEdgeSource(final String edgeId, final String portId) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram d : diagrams) {
          d.setEdgeSource(edgeId, portId);
        }
      }
    });
  }

  private void setEdgeStyle(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        Value style = message.args[1];
        for (Diagram diagram : diagrams) {
          for (Edge edge : diagram.getEdges()) {
            if (edge.getId().equals(id.strValue())) {
              edge.setLineStyle(style.intValue);
            }
          }
        }
      }
    });
  }

  private void setEdgeTarget(Message message) {
    String edgeId = message.args[0].strValue();
    String portId = message.args[1].strValue();
    setEdgeTarget(edgeId, portId);
  }

  private void setEdgeTarget(final String edgeId, final String portId) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram d : diagrams) {
          d.setEdgeTarget(edgeId, portId);
        }
      }
    });
  }

  private void setFillColor(Message message) {
    String id = message.args[0].strValue();
    int red = message.args[1].intValue;
    int green = message.args[2].intValue;
    int blue = message.args[3].intValue;
    setFillColor(id, red, green, blue);
  }

  private void setFillColor(final String id, final int red, final int green, final int blue) {
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams)
          diagram.setFillColor(id, red, green, blue);
      }
    });
  }

  private void setFocus(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (String tabId : tabs.keySet()) {
          if (tabId.equals(id.strValue())) tabFolder.setSelection(tabs.get(tabId));
        }
      }
    });
  }

  private void setFont(Message message) {
    final String id = message.args[0].strValue();
    final String fontData = message.args[1].strValue();
    runOnDisplay(new Runnable() {
      public void run() {
        for (Diagram diagram : diagrams) {
          diagram.setFont(id, fontData);
        }
      }
    });
  }

  private void setName(Message message) {
    final Value id = message.args[0];
    final Value name = message.args[1];
    runOnDisplay(new Runnable() {
      public void run() {
        for (String diagramId : tabs.keySet())
          if (id.strValue().equals(diagramId)) {
            tabs.get(diagramId).setText(name.strValue());
          }
      }
    });
  }

  private void setRefPoint(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        Value refx = message.args[1];
        Value refy = message.args[2];
        for (Diagram diagram : diagrams) {
          for (Edge edge : diagram.getEdges()) {
            if (edge.getId().equals(id.strValue())) {
              // edge.setRefx(refx.intValue);
              // edge.setRefy(refy.intValue);
            }
          }
        }
      }
    });
  }

  private void setText(Message message) {
    final Value id = message.args[0];
    final Value text = message.args[1];
    for (Diagram diagram : diagrams)
      diagram.setText(id.strValue(), text.strValue());
  }

  private void setBorder(Message message) {
    final Value id = message.args[0];
    final Value border = message.args[1];
    for (Diagram diagram : diagrams)
      diagram.setBorder(id.strValue(), border.boolValue);
  }

  private void setFill(Message message) {
    final Value id = message.args[0];
    final Value fill = message.args[1];
    for (Diagram diagram : diagrams)
      diagram.setFill(id.strValue(), fill.boolValue);
  }

  public void showList(CTabFolderEvent event) {

  }

  private void startRender(final Message message) {
    runOnDisplay(new Runnable() {
      public void run() {
        Value id = message.args[0];
        for (Diagram diagram : diagrams)
          if (diagram.getId().equals(id.strValue())) diagram.renderOn();
      }
    });
  }

  private void stopRender(Message message) {
    Value id = message.args[0];
    for (Diagram diagram : diagrams)
      if (diagram.getId().equals(id.strValue())) diagram.renderOff();
  }
*/
  public javafx.geometry.Point2D textDimension(String text, javafx.scene.text.Font font) {

	javafx.scene.text.Text t = new javafx.scene.text.Text(text);
	if(font != null) t.setFont(font); else System.err.println("calculating text dimension without font");
	t.applyCss();
	
	final double width = t.getLayoutBounds().getWidth();
	final double height = t.getLayoutBounds().getHeight();
	javafx.geometry.Point2D extent = new javafx.geometry.Point2D(width, height);
	
    return extent;
  }
  /*
  public void writeXML(PrintStream out) {
    out.print("<Diagrams>");
    for (Diagram diagram : diagrams)
      diagram.writeXML(tabs.get(diagram.getId()).getText(), out);
    out.print("</Diagrams>");
  }
  */

@Override
public boolean processMessage(Message message) {
	return false;
}

}