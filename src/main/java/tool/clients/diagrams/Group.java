package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.Vector;

import javafx.scene.control.TreeItem;
import xos.Value;

public class Group extends TreeItem<String>{

//  public static FontData getDefaultFont() {
//    return defaultFont;
//  }

//  static FontData defaultFont = new FontData("Monaco", 10, SWT.NORMAL);
//  Composite       buttonContainer;
//  ExpandItem      item;
  Palette         palette;
  String          name;
  Vector<Tool>    tools       = new Vector<Tool>();

  public Group(String name) {
	  super(name);
	  this.name = name;
  }
  
//  @Deprecated
//  public Group(Palette palette, ToolBar parent, String name) {
//    this.palette = palette;
//    this.name = name;
////    buttonContainer = new Composite(parent, SWT.BORDER);
////    item = new ExpandItem(parent, SWT.NONE);
//    item.setControl(buttonContainer);
//    item.setText(name);
//    GridLayout layout = new GridLayout(1, true);
//    layout.marginHeight = 0;
//    layout.horizontalSpacing = 0;
//    layout.verticalSpacing = 0;
//    layout.marginWidth = 0;
//    buttonContainer.setLayout(layout);
//    GridData buttonData = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true, true);
//    buttonData.horizontalSpan = 2;
//    buttonContainer.setLayoutData(buttonData);
////    setFont("fonts/DejaVuSans.ttf", "DejaVu Sans");
//  }

//	public final void setFont(String fileName, String name) {
//		int oldHeight = defaultFont == null ? 13 : defaultFont.getHeight();
//		FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
//		defaultFont = fontData[0];
//		XModeler.getXModeler().getDisplay().loadFont(fileName);
//		defaultFont.setName(name);
//		defaultFont.setHeight(oldHeight);
//	}
	
  public String getName() {
    return name;
  }

  public Value asValue(String name) {
    Value[] bs = new Value[tools.size() + 1];
    bs[0] = new Value(name);
    int i = 1;
    for (Tool tool : tools) {
      bs[i++] = new Value(new Value[] { new Value(tool.getId()), new Value(tool.getType()) });
    }
    return new Value(bs);
  }

  public void delete() {
    for (Tool tool : tools) {
      tool.delete();
    }
//    item.dispose();
//    buttonContainer.dispose();
  }

  public void deselect() {
    for (Tool tool : tools) {
      tool.reset();
    }
  }

//  public Composite getButtonContainer() {
//    return buttonContainer;
//  }
//
//  public ExpandItem getItem() {
//    return item;
//  }

  public Palette getPalette() {
    return palette;
  }

  public Tool getToolLabelled(String label) {
    for (Tool tool : tools)
      if (tool.getLabel().equals(label)) return tool;
    return null;
  }

  public void newToggle(Diagram diagram, String label, String toolId, boolean state, String iconTrue, String iconFalse) {
	ToggleTool tool = new ToggleTool(diagram, label, toolId, state, iconTrue, iconFalse);
    tools.add(tool);
    getChildren().add(tool.getButton());
//    item.setHeight(buttonContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
//    item.setExpanded(true);
  }

  public void newAction(Diagram diagram, String label, String toolId, String icon) {
    Tool tool = getTool(label);
    if (tool != null) removeTool(label);
    
    ActionTool actionTool = new ActionTool(diagram, label, toolId, icon);
    tools.add(actionTool);
    getChildren().add(actionTool.getButton());
//    item.setHeight(buttonContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
//    item.setExpanded(true);
  }

  private void removeTool(String label) {
    Tool tool = getTool(label);
    removeTool(tool);
  }
  
  void removeTool(Tool tool) {
	    if (tool != null) {
	      tools.remove(tool);
	      tool.delete();
//	      item.setHeight(buttonContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
//	      item.setExpanded(true);
	    }
	  }

  private Tool getTool(String label) {
    for (Tool tool : tools)
      if (tool.getLabel().equals(label)) return tool;
    return null;
  }

  public void newTool(Diagram diagram, String label, String toolId, boolean isEdge, String icon) {
	  
	for(Tool tool : tools) { // Quickfix for Copenhagen/MULTI2018, prevent repeated creation of tools
		if(tool.label.equals(label)) return;
	}
	  
    if (isEdge) {
    	Tool edge = new EdgeCreationTool(diagram, label, toolId, icon);
        tools.add(edge); 
        getChildren().add(edge.getButton());
    } else {
    	Tool node = new NodeCreationTool(diagram, label, toolId, icon);
    	tools.add(node);
        getChildren().add(node.getButton());
    }
//    item.setHeight(buttonContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
//    item.setExpanded(true);
  }

  public void resetButtons() {
    for (Tool tool : tools) {
      tool.reset();
    }
  }

//  public String toString() {
//    return "Group(" + buttonContainer + "," + item + "," + tools + ")";
//  }

  public void writeXML(PrintStream out) {
    out.print("<Group name='" + name + "'>");
    for (Tool tool : tools)
      tool.writeXML(out);
    out.print("</Group>");
  }
}
