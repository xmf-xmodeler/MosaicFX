package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;

import org.reactfx.util.Try;
import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.AbstractSyntax;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntax;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntaxWizard;
import tool.clients.fmmlxdiagrams.graphics.SvgConstant;
import tool.clients.fmmlxdiagrams.graphics.View;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;
import tool.clients.fmmlxdiagrams.newpalette.FmmlxPalette;
import tool.clients.serializer.FmmlxDeserializer;
import tool.clients.serializer.XmlManager;
import tool.clients.xmlManipulator.XmlHandler;
import tool.xmodeler.PropertyManager;
import tool.xmodeler.XModeler;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

public class FmmlxDiagram extends AbstractPackageViewer{

	enum MouseMode {
		MULTISELECT, STANDARD, DRAW_EDGE
	}

	public static final boolean SHOW_MENUITEMS_IN_DEVELOPMENT = false;

	// The elements which the diagram consists of GUI-wise
	private SplitPane splitPane;
	private SplitPane splitPane2;
	private VBox mainView;
//	private VBox vBox;
//	private Menu menu;
//	private MenuBar menuBar;
//	private MenuItem loadXML;
//	private MenuItem saveXML;
	// The communication to the xmf and other actions

	// The elements representing the model which is displayed in the GUI

	private Vector<DiagramEdgeLabel<?>> labels = new Vector<>();
	private TabPane tabPane;


	// Temporary variables storing the current state of user interactions
	private transient Vector<CanvasElement> selectedObjects = new Vector<>();
	private transient ContextMenu activeContextMenu;
	public  transient boolean objectsMoved = false;
	private transient PropertyType drawEdgeType = null;
	private transient Point2D dragStart;
	private transient Point2D lastPointPressed;
	private transient Point2D currentPointMoving;
	private transient Affine dragAffine = new Affine();
	private transient MouseMode mouseMode = MouseMode.STANDARD;
	private transient FmmlxObject newEdgeSource;
	private transient FmmlxProperty lastHitProperty = null;
	private transient boolean diagramRequiresUpdate = false;

//	private static final Point2D CANVAS_RAW_SIZE = new Point2D(1400, 1000);
	public  static final Font FONT;
	Palette palette = new Palette(this);
	private boolean showOperations = true;
	private boolean showOperationValues = true;
	private boolean showSlots = true;
	private boolean showGetterAndSetter = true;
	private boolean showDerivedOperations=true;
	private boolean showDerivedAttributes=true;
	private boolean showMetaClassName = false;
	private boolean showConstraints = true;
	private boolean showConstraintReports = true;
	private DiagramViewPane zoomView;
	@Override protected boolean loadOnlyVisibleObjects() { return false; }	// Did not work. Attributes from invisible classes did not cause slots on visible classes

	public final String diagramName;
	private final FmmlxPalette newFmmlxPalette;
	private String filePath;

	public String updateID = null;

	String edgeCreationType = null;
	String nodeCreationType = null;

	public LevelColorScheme levelColorScheme = new LevelColorScheme.FixedBlueLevelColorScheme();

	public final static FmmlxDiagram NullDiagram = new FmmlxDiagram();

	static{
		FONT = Font.font(Font.getDefault().getFamily(), FontPosture.REGULAR, 14);
	}	

	public final HashMap<String, ConcreteSyntax> syntaxes = new HashMap<>();
	
	@Deprecated private DiagramViewPane mainViewPane; 
	private Vector<DiagramViewPane> views = new Vector<>(); 

	private FmmlxDiagram() {
		super(null,-1,null);
		this.newFmmlxPalette = null;
		this.diagramName = null;
	}

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, 
			int diagramID, String name, String packagePath, 
			Vector<Vector<Object>> listOfViews, 
			Vector<Vector<Object>> listOfOptions) {
		super(comm,diagramID,packagePath);
		this.diagramName = name;
		splitPane = new SplitPane();
		splitPane2 = new SplitPane();
		mainView = new VBox();
		
		
		Palette palette2 = new Palette(this,2);
		newFmmlxPalette = new FmmlxPalette(this);
		
        tabPane = new TabPane();
        
        for(Vector<Object> view : listOfViews) {
        	DiagramViewPane dvp = new DiagramViewPane((String) view.get(0), false);
        	double xx = 1., tx = 0., ty = 0.;
        	try{ xx = (float) view.get(1); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
        	try{ tx = (float) view.get(2); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
        	try{ ty = (float) view.get(3); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
        	dvp.canvasTransform = new Affine(xx, 0, tx, 0, xx, ty);
        	tabPane.getTabs().add(new MyTab(dvp));
        	if(mainViewPane == null) mainViewPane = dvp;
        }
        
        if(listOfViews.size() == 0) {
        	DiagramViewPane dvp = new DiagramViewPane("default view", false);
        	tabPane.getTabs().add(new MyTab(dvp));
        	if(mainViewPane == null) mainViewPane = dvp;
        }
        
        for(Vector<Object> option : listOfOptions) {
        	String  key   = (String)  option.get(0);
        	Boolean value = (Boolean) option.get(1);
        	if("showDerivedAttributes".equals(key)) setShowDerivedAttributes(value);
        	if("showDerivedOperations".equals(key)) setShowDerivedOperations(value);
        	if("showGettersAndSetters".equals(key)) setShowGettersAndSetters(value);
        	if("showOperations".equals(key)) setShowOperations(value);
        	if("showOperationValues".equals(key)) setShowOperationValues(value);
        	if("showSlots".equals(key)) setShowSlots(value);
        	if("showConstraintReports".equals(key)) setConstraintReportsInDiagram(value);
        	if("showConstraints".equals(key)) setConstraintsInDiagram(value);
        }        

        tabPane.getTabs().add(new MyTab());
        zoomView = new DiagramViewPane("", true);
        
        tabPane.setFocusTraversable(true);
        tabPane.setOnKeyReleased(new javafx.event.EventHandler<javafx.scene.input.KeyEvent>() {

            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if(event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.M) {
                	getActiveTab().canvasTransform.prependScale(-1, 1, new Point2D(getActiveTab().canvas.getWidth()/2 , getActiveTab().canvas.getHeight()/2));
                	redraw();
                }
                if(event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.R) {
                	getActiveTab().canvasTransform.prependRotation(10, new Point2D(getActiveTab().canvas.getWidth()/2 , getActiveTab().canvas.getHeight()/2));
                	redraw();
                }
            }
        });
        
        tabPane.getSelectionModel().selectedItemProperty().addListener((foo,goo,newTabItem)-> {
        	if(newTabItem.getContent() == null) {
        		// pane with star selected
        		tabPane.getTabs().add(new MyTab());
        		final DiagramViewPane newView = new DiagramViewPane("new View", false);
        		((MyTab)newTabItem).setText("");
        		((MyTab)newTabItem).setView(newView);
        		final java.util.Timer timer = new java.util.Timer();
        		timer.schedule(new java.util.TimerTask() {
        			@Override public void run() {
        				redraw();
        				if(newView.getCanvas().getWidth() > 0) timer.cancel();
        			}
        		}, 100, 100);      		
        	} else {
        		redraw();
			}
        });
		
        // Resize of Canvas on rescale
        tabPane.heightProperty().addListener( ( observable, x, y ) -> redraw() );
        tabPane.widthProperty().addListener( ( observable, x, y ) -> redraw() );
        
		mainView.getChildren().addAll(palette, palette2, tabPane);//scrollerCanvas);
		
		splitPane2.setOrientation(Orientation.VERTICAL);
		splitPane2.setDividerPosition(0, 0.8);
		splitPane2.getItems().addAll(newFmmlxPalette.getToolBar(), zoomView);
		SplitPane.setResizableWithParent(zoomView, false);

		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.2);
		splitPane.getItems().addAll(splitPane2, mainView);
		SplitPane.setResizableWithParent(splitPane2, false);
		
		new Thread(this::fetchDiagramData).start();

//		java.util.Timer timer = new java.util.Timer();
//		timer.schedule(new java.util.TimerTask() {
//
//			@Override
//			public void run() {
//				redraw();
//			}
//		}, 200, 200);
		
		File syntaxDir = new File(ConcreteSyntaxWizard.RESOURCES_CONCRETE_SYNTAX_REPOSITORY); //TODO: recursively searching all 
		if (syntaxDir.isDirectory()) {
			File[] files = syntaxDir.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					if(file.getName().endsWith(".xml")) {
						try {
							AbstractSyntax group = AbstractSyntax.load(file);
							if(group instanceof ConcreteSyntax) {
								ConcreteSyntax c = ((ConcreteSyntax) group);
								syntaxes.put(c.classPath, c);
							}
						} catch (Exception e) {
							System.err.println("reading " + file.getName() + " failed (" + e.getMessage() + "). Ignoring..."); 
						}
					}
				}
			}
		}
	}

	// Only used to set the mouse pointer. Find a better solution
	@Deprecated
	public Canvas getCanvas() {
		return mainViewPane.canvas;
	}

	public void deselectPalette() {
		edgeCreationType = null;
		nodeCreationType = null;
		//newFmmlxPalette.clearSelection();
	}
	
	public FmmlxPalette getPalette() {
		return newFmmlxPalette;
	}

	public void setEdgeCreationType(String edgeCreationType) {
		this.edgeCreationType = edgeCreationType;
		this.nodeCreationType= null;
		getCanvas().setCursor(Cursor.CROSSHAIR);
	}

	public void setNodeCreationType(String nodeCreationType) {
		this.nodeCreationType = nodeCreationType;
		this.edgeCreationType = null;
		getCanvas().setCursor(Cursor.CROSSHAIR);
	}
	
	public String getEdgeCreationType() {
		return edgeCreationType;
	}
	
	public String getNodeCreationType() {
		return nodeCreationType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void savePNG(){
//		double zoom = this.zoom;
		mainViewPane.setMaxZoom();

	    FileChooser fileChooser = new FileChooser();

	    String initalDirectory = PropertyManager.getProperty("fileDialogPath", "");
    	if (!initalDirectory.equals("")) {
    		File dir = new File(initalDirectory);
    		if(dir.exists()) {
    			fileChooser.setInitialDirectory(dir);
    		}
    	}

    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
    	fileChooser.setTitle("Save Diagram to PNG");
	    File file = fileChooser.showSaveDialog(XModeler.getStage());

	    if(file != null){
	        WritableImage wi = new WritableImage((int)mainViewPane.canvas.getWidth(),(int)mainViewPane.canvas.getHeight());
	        try {
	        	ImageIO.write(SwingFXUtils.fromFXImage(mainViewPane.canvas.snapshot(null,wi),null),"png",file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

//	    setZoom(zoom);
	}
//	// Only used to set the diagram into the tab. Find a better solution
	@Deprecated
	public javafx.scene.Node getView() {
		return splitPane;
	}

	private void updateDiagramLater() {
		diagramRequiresUpdate = true;
	}

	public void redraw() {
		if (fetchingData) {
			return;}

		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			// we are on the right Thread already:
			for(DiagramViewPane view : views) {
				view.paintOn();
			}			
		} else { // create a new Thread
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				for(DiagramViewPane view : views) {
					view.paintOn();
				}	
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}



	private void drawMultiSelectRect(GraphicsContext g) {
			double x = Math.min(lastPointPressed.getX(), currentPointMoving.getX());
			double y = Math.min(lastPointPressed.getY(), currentPointMoving.getY());
			g.strokeRect(x, y, Math.abs(currentPointMoving.getX() - lastPointPressed.getX()), Math.abs(currentPointMoving.getY() - lastPointPressed.getY()));
	}

	private void drawNewEdgeLine(GraphicsContext g) {
		if (mouseMode == MouseMode.DRAW_EDGE && lastPointPressed != null && currentPointMoving != null) {
			g.strokeLine(lastPointPressed.getX(), lastPointPressed.getY(), currentPointMoving.getX(), currentPointMoving.getY());
		}
	}

	public void triggerOverallReLayout() {
		
		// TODO evil hack. not kosher
		for(int i = 0; i < 2; i++) { 
			for(FmmlxObject o : new Vector<>(objects)) {
				o.layout(this);
			}
			for(Edge<?> edge : new Vector<>(edges)) {
				edge.align();
				edge.layoutLabels(this);
			}
		}
	}
	private void mouseReleasedStandard() {
		for (Edge<?> e : edges) e.removeRedundantPoints();

		if (objectsMoved) {
			for (CanvasElement s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					o.drop();
					comm.sendCurrentPosition(this.getID(), o.getPath(), (int)Math.round(o.getX()), (int)Math.round(o.getY()), o.hidden);
					for(Edge<?> e : edges) {
						if(e.isSourceNode(o) || e.isTargetNode(o)) {
							comm.sendCurrentPositions(this.getID(), e);
						}
					}
				} else if (s instanceof Edge) {
					comm.sendCurrentPositions(this.getID(), (Edge<?>) s);
				} else if (s instanceof DiagramEdgeLabel) {
					DiagramEdgeLabel<?> del = (DiagramEdgeLabel<?>) s;
					del.drop();
					del.owner.updatePosition(del);
					comm.storeLabelInfo(this, del);
				}
		}
		objectsMoved = false;
	}

	private boolean isLeftButton(MouseEvent e) {return e.getButton() == MouseButton.PRIMARY;}
	private boolean isRightButton(MouseEvent e) {return e.getButton() == MouseButton.SECONDARY;}
	private boolean isCenterButton(MouseEvent e) {return e.getButton() == MouseButton.MIDDLE;}

	private final double ZOOM_STEP = Math.sqrt(Math.sqrt(Math.sqrt(2)));

	/* Setters for MouseMode */

	public void setDrawEdgeMouseMode(PropertyType type, FmmlxObject newEdgeSource) {
		drawEdgeType = type;
		mouseMode = MouseMode.DRAW_EDGE;
		this.newEdgeSource = newEdgeSource;
	}

	public void setStandardMouseMode() {
		mouseMode = MouseMode.STANDARD;
	}

	public void setDrawEdgeMode(FmmlxObject source, PropertyType type) {
		setSelectedObject(source);
		setDrawEdgeMouseMode(type, source);
		Point2D p = getActiveView().getCanvasTransform().transform(new Point2D(source.getCenterX(), source.getCenterY()));
		storeLastClick(p.getX(), p.getY());
		deselectAll();
	}

	////////////////////////////////////////////////////////////////////

	private void storeLastClick(double x, double y) {
		try{
			View view = getActiveView();
			lastPointPressed = view.getCanvasTransform().inverseTransform(new Point2D(x, y));
		} catch (Exception ex) {
			lastPointPressed = new Point2D(x, y);
		}		
	}

	private void storeCurrentPoint(double x, double y) {
		try{
			View view = getActiveView();
			currentPointMoving = view.getCanvasTransform().inverseTransform(new Point2D(x, y));
		} catch (Exception ex) {
			currentPointMoving = new Point2D(x, y);
		}				
	}

	public boolean isSelected(CanvasElement element) {
		return selectedObjects.contains(element);
	}

	void deselectAll() {
		deselectPalette();
		selectedObjects.clear();
	}

	public void setSelectedObject(FmmlxObject source) {
		deselectAll();
		selectedObjects.add(source);
	}

	@Override public void setSelectedObjectAndProperty(FmmlxObject o, FmmlxProperty p) { setSelectedObject(o); }

	private void select(FmmlxObject o) {
		if (!selectedObjects.contains(o)) {
			selectedObjects.add(o);
		}
	}
	
	public FmmlxProperty getSelectedProperty() {
		return lastHitProperty;
	}

	@Deprecated
	//needs filter
	/**
	 * Calculates the height of the text. Because that depends of the font size and the screen resolution
	 * @return the text height
	 */
	public static double calculateTextHeight() {
		Text t = new Text("TestText");
		t.setFont(FONT);
		return t.getLayoutBounds().getHeight();
	}

	public static double calculateTextWidth(String text) {
		Text t = new Text(text);
		t.setFont(FONT);
		return t.getLayoutBounds().getWidth();
	}

	// TODO: delete and use method with level
	public ObservableList<FmmlxObject> getPossibleAssociationEnds() {
		Vector<FmmlxObject> objectList = new Vector<>();

		if (!objects.isEmpty()) {
			for (FmmlxObject object : objects) {
				if (object.getLevel() != 0) {
					objectList.add(object);
				}
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	public void addLabel(DiagramEdgeLabel<?> diagramLabel) {
		Integer index = null;
		for(int i = 0; i < labels.size(); i++) {
			DiagramEdgeLabel<?> label = labels.get(i);
			if(label.owner == diagramLabel.owner && label.localID == diagramLabel.localID) {
				index = i;
			}
		}
		if(index == null) {
			labels.add(diagramLabel);
		} else {
			labels.set(index, diagramLabel);
		}
	}

	////////////////////////////////////////////////////////////////////
	////					Messages to XMF							////
	////////////////////////////////////////////////////////////////////


	// Some useful methods for queries:

	public String getDiagramLabel() {
		return diagramName;
	}

	public InheritanceEdge getInheritanceEdge(FmmlxObject child, FmmlxObject parent) {
		for(Edge<?> e : edges) {
			if(e instanceof InheritanceEdge) {
				InheritanceEdge i = (InheritanceEdge) e;
				if(i.isSourceNode(child) && i.isTargetNode(parent)) return i;
			}
		}
		return null;
	}

	public Vector<DiagramEdgeLabel<?>> getLabels() {
		return new Vector<>(labels); // read-only
	}

	public Vector<FmmlxObject> getObjectsByLevel(int level){
		Vector<FmmlxObject> result = new Vector<>();

		for (FmmlxObject object : objects) {
			if (object.getLevel()==level) {
				result.add(object);
			}
		}
		return result;
	}

	public Object getAllMetaClass() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : getObjects()) {
			if (object.getLevel() != 0) {
				result.add(object);
			}
		}
		return result;
	}

	public void setCursor(Cursor c) {
		mainViewPane.canvas.setCursor(c);
	}

	public void setShowOperations(boolean show) {this.showOperations = show;}
	public void setShowOperationValues(boolean show) {this.showOperationValues = show;}
	public void setShowSlots(boolean show) {this.showSlots = show;}
	public void setShowGettersAndSetters(boolean show) {this.showGetterAndSetter = show;}
	public void setShowDerivedOperations(boolean show) {this.showDerivedOperations = show;}
	public void setShowDerivedAttributes(boolean show) {this.showDerivedAttributes = show;}
	public void setMetaClassNanmeInPalette(boolean show) {this.showMetaClassName = show;} 
	public void setConstraintsInDiagram(boolean show) {this.showConstraints = show;} 
	public void setConstraintReportsInDiagram(boolean show) {this.showConstraintReports = show;} 

	public boolean isShowOperations() {return this.showOperations;}
	public boolean isShowOperationValues() {return this.showOperationValues;}
	public boolean isShowSlots() {return this.showSlots;}
	public boolean isShowGetterAndSetter() {return this.showGetterAndSetter;}
	public boolean isShowDerivedOperations() {return this.showDerivedOperations;}
	public boolean isShowDerivedAttributes() {return this.showDerivedAttributes;}
	public boolean isMetaClassNameInPalette() {return this.showMetaClassName;}
	public boolean isConstraintsInDiagram() {return this.showConstraints;}
	public boolean isConstraintReportsInDiagram() {return this.showConstraintReports;} 

	public Vector<String> getEnumItems(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e.getItems();
		}
		return null;
	}

	public ObservableList<FmmlxEnum> getEnumsObservableList() {
		Vector<FmmlxEnum> objectList = new Vector<>();

		if (!enums.isEmpty()) {
			for (FmmlxEnum fmmlxEnum : enums) {
				objectList.add(fmmlxEnum);
			}
		}
		return FXCollections.observableArrayList(objectList);
	}

	public synchronized void updateEnums() {
		try {
			enums.clear();
			enums = comm.fetchAllEnums(this); }
		catch (TimeOutException e) {
			e.printStackTrace();
		}
	}

	public FmmlxEnum getEnum(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e;
		}
		return null;
	}

	public int getMaxLevel() {
		int level = 0;
		for (FmmlxObject tmp : objects) {
			if(tmp.getLevel()>level) {
				level=tmp.getLevel();
			}
		}
		return level;
	}

	public Vector<Point2D> findEdgeIntersections(Point2D a, Point2D b) { // only interested in a-b horizontal crossing c-d vertical
		Vector<Point2D> result = new Vector<>();
		for(Edge<?> e : new Vector<>(edges)) {
			if(e.isVisible()) {
				Vector<Point2D> otherPoints = e.getAllPoints();
				for(int i = 0; i < otherPoints.size()-1; i++) {
					Point2D c = otherPoints.get(i);
					Point2D d = otherPoints.get(i+1);
					if(a != c && b != d && a != d && b != c) {
						if(a.getY() == b.getY()) { // possibly redundant
							if(c.getX() == d.getX()) {
								// check for intersection
								if((c.getY() < a.getY()) != (d.getY() < a.getY())) { // if c and d are on different sides of a/b (y)
									if((a.getX() < c.getX()) != (b.getX() < c.getX())) { // if a and b are on different sides of c/d (x)
										result.add(new Point2D(c.getX(), a.getY()));
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public void setShowOperations(CheckBox box) {
		boolean show = box.isSelected();
		setShowOperations(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowOperations(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowGettersAndSetters(CheckBox box) {
		boolean show = box.isSelected();
		setShowGettersAndSetters(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowGettersAndSetters(show);
		}
		triggerOverallReLayout();
		redraw();		
	}


	public void setShowOperationValues(CheckBox box) {
		boolean show = box.isSelected();
		setShowOperationValues(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowOperationValues(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowSlots(CheckBox box) {
		boolean show = box.isSelected();
		setShowSlots(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowSlots(show);
		}
		triggerOverallReLayout();
		redraw();
	}

	public void setShowDerivedOperations(CheckBox box) {
		boolean show = box.isSelected();
		setShowDerivedOperations(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowDerivedOperations(show);
		}
		triggerOverallReLayout();
		redraw();

	}

	public void setShowDerivedAttributes(CheckBox box) {
		boolean show=box.isSelected();
		setShowDerivedAttributes(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowDerivedAttributes(show);
		}
		triggerOverallReLayout();
		redraw();
	}
	
	public void setShowConstraints(CheckBox box) {
		boolean show = box.isSelected();
		setConstraintsInDiagram(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowConstraints(show);
		}
		triggerOverallReLayout();
		redraw();
	}
	
	public void setShowConstraintReports(CheckBox box) {
		boolean show = box.isSelected();
		setConstraintReportsInDiagram(show);
		for (FmmlxObject o : getObjects()) {
			o.setShowConstraintReports(show);
		}
		triggerOverallReLayout();
		redraw();
	}
	
	public void setMetaClassNameInPalette(CheckBox metaClassName) {
		boolean show=metaClassName.isSelected();
		setMetaClassNanmeInPalette(show);
		if(show==true) {
			newFmmlxPalette.setShowMetaClassName(true);
			newFmmlxPalette.update();
		} else if (show==false) {
			newFmmlxPalette.setShowMetaClassName(false);
			newFmmlxPalette.update();
		}		
	}
	
	

	@Override
	protected void clearDiagram_specific() {
		labels.clear();
	}

	@Override
	protected void fetchDiagramDataSpecific() throws TimeOutException {
		for(FmmlxObject o : objects) {
			o.layout(this);
		}
		comm.sendViewOptions(diagramID); // make sure it is at least transferred regularly
	}

	@Override
	protected void fetchDiagramDataSpecific2() {
		
		triggerOverallReLayout();

		newFmmlxPalette.update();

		if(filePath !=null && filePath.length()>0){
			if(justLoaded){
				justLoaded = false;
				FmmlxDeserializer deserializer = new FmmlxDeserializer(new XmlManager(filePath));
				org.w3c.dom.Node positionInfo = getComm().getPositionInfo(getID());
				if(positionInfo != null) {
					deserializer.alignElements(this, (org.w3c.dom.Element) positionInfo);
					triggerOverallReLayout();
				}
				redraw();
				updateDiagram();
			}
		} else {		
			Issue nextIssue = null;
			for(int i = 0; i < issues.size() && nextIssue == null; i++) {
				if(issues.get(i).isSoluble()) nextIssue = issues.get(i);
			}
	
			if(nextIssue != null) {
				final Issue ISSUE = nextIssue;
				Platform.runLater(() -> ISSUE.performResolveAction(this));
			}
		}
		
		palette.updateToolbar(this);
		redraw();
	}
	
	

	@Override
	protected void updateViewerStatusInGUI(ViewerStatus newStatus) {
		// TODO Auto-generated method stub
	}

	public void paintToSvg(XmlHandler xmlHandler, double extraHeight){
		Vector<CanvasElement> objectsToBePainted = new Vector<>();
		objectsToBePainted.addAll(objects);
		objectsToBePainted.addAll(labels);
		objectsToBePainted.addAll(edges);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objects) {
			o.updatePortOrder();
		}
		for(CanvasElement c : objectsToBePainted){
			c.paintToSvg(xmlHandler, this);
		}
		Element issueGroup = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_GROUP);
		issueGroup.setAttribute(SvgConstant.ATTRIBUTE_GROUP_TYPE, "issues");
		
				
		Element rect = xmlHandler.createXmlElement(SvgConstant.TAG_NAME_RECT);
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_X, 0+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_COORDINATE_Y, getBounds().getMaxY()+""); //mainViewPane.canvas.getHeight();
		rect.setAttribute(SvgConstant.ATTRIBUTE_HEIGHT, extraHeight+7+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, mainViewPane.canvas.getWidth()+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL, "black");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, 1 +"");
		xmlHandler.addXmlElement(issueGroup, rect);
		for(Issue issue : issues){
			issue.paintToSvg(xmlHandler, issueGroup, 14, 16, 0, getBounds().getMaxY()+issue.issueNumber*14);
		}
		xmlHandler.addXmlElement(xmlHandler.getRoot(), issueGroup); 
	}

	@Deprecated
	public Affine getCanvasTransform() {
		return mainViewPane.canvasTransform;
	}

	@Override
	public View getActiveView() {
		return (DiagramViewPane) tabPane.getSelectionModel().getSelectedItem().getContent();
	}
	
	public BoundingBox getBounds() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;	
		boolean valid = false;
		
		Vector<CanvasElement> elements = new Vector<CanvasElement>();
		elements.addAll(objects);
		elements.addAll(edges);
		//elements.addAll(labels);
		
		for(CanvasElement cE : elements) if (!cE.isHidden()) {
			
			Double left = cE.getLeftX();
			Double right = cE.getRightX();
			Double top = cE.getTopY();
			Double bottom = cE.getBottomY();
			
			if(left!=null && left   < minX) minX = left;
			if(right!=null && right  > maxX) maxX = right;
			if(top!=null && top    < minY) minY = top;
			if(bottom!=null && bottom > maxY) maxY = bottom;
			valid = true;
			//System.err.println("BoundingBox MinX: " + left  +" MinY: " + top + " MaxX: " + right + " MaxY: " + bottom);	
		}
		//System.err.println("BoundingBox MinX: " + minX  +" MinY: " + minY + " Width: " + (maxX-minX) + " Height: " + (maxY-minY));
		Double MARGIN = 5.;
		if(valid) return new BoundingBox(minX-MARGIN, minY-MARGIN, (maxX-minX)+2*MARGIN, (maxY-minY)+2*MARGIN);
		return new BoundingBox(0,0,100,100);
	}
		
	public class DiagramViewPane extends Pane implements View {
		
		private Canvas canvas;
		private double zoom = 1.;
		private Affine canvasTransform = new Affine();
		private final boolean isZoomView;
		private String name;
		
		private DiagramViewPane(String name, boolean isZoomView) {
			super();

			this.name = name;
			this.isZoomView = isZoomView;
			
			canvas = new Canvas();
			getChildren().add(canvas);
			canvas.widthProperty().bind(this.widthProperty());
			canvas.heightProperty().bind(this.heightProperty());
			setMaxSize(4096, 4096);
			setPrefSize(2048, 2048);
			
			if(isZoomView) {
				canvas.setOnMouseClicked(mE -> zoomClicked(mE));
			}
			else {
				canvas.setOnMousePressed(this::mousePressed);
				canvas.setOnMouseDragged(this::mouseDragged);
				canvas.setOnMouseReleased(this::mouseReleased);
				canvas.setOnMouseMoved(this::mouseMoved);
				canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);
			}
			
			views.add(this);			
		}

		////////////////////////////////////////////////////////////////////
		////						MouseListener						////
		////////////////////////////////////////////////////////////////////


		private void mousePressed(MouseEvent e) {
			if(fetchingData) return;
			clearContextMenus();

			if (isLeftButton(e) && !e.isAltDown()) {
				handleLeftPressed(e);
				dragStart = new Point2D(e.getX(), e.getY());
			}
			if (isRightButton(e)) {
				handleRightPressed(e);
			}
			if (isCenterButton(e) || (isLeftButton(e) && e.isAltDown())) {
				handleCenterPressed(e);
			}
		}

		private void mouseDragged(MouseEvent e) {
			if(isLeftButton(e) && !e.isAltDown()) {	
				if (mouseMode == MouseMode.MULTISELECT) {
					storeCurrentPoint(e.getX(), e.getY());
					redraw();
				}
				if (mouseMode == MouseMode.STANDARD) {
					if (selectedObjects.size() == 1 && selectedObjects.firstElement() instanceof Edge) {
						((Edge<?>) selectedObjects.firstElement()).setPointAtToBeMoved(new Point2D(e.getX(), e.getY()), canvasTransform);
	
					}
					mouseDraggedStandard(new Point2D(e.getX(), e.getY()));
				}
			}
			if (isCenterButton(e) || (isLeftButton(e) && e.isAltDown())) {
				handleCenterDragged(e);
			}
		}

		private transient CanvasElement lastElementUnderMouse = null;

		private void mouseMoved(MouseEvent e) {
			if (mouseMode == MouseMode.DRAW_EDGE) {
				storeCurrentPoint(e.getX(), e.getY());
				redraw();
			}

			CanvasElement elementUnderMouse = getElementAt(e.getX(), e.getY());
			if(elementUnderMouse != lastElementUnderMouse) {
				lastElementUnderMouse = elementUnderMouse;
				for (FmmlxObject o : objects)
					o.unHighlight();
				for (Edge<?> edge : edges)
					edge.unHighlight();
				for (DiagramEdgeLabel<?> l : labels)
					l.unHighlight();
			}

			if(elementUnderMouse != null) elementUnderMouse.highlightElementAt(new Point2D(e.getX(), e.getY()), canvasTransform);

		}

		private void mouseDraggedStandard(Point2D p) {
			final double DRAG_LIMIT=5 ,DRAG_STEP = 5;
			if(p.getX() < DRAG_LIMIT) {
				canvasTransform.prependTranslation(DRAG_STEP,0);
				dragStart = new Point2D(dragStart.getX()+DRAG_STEP, dragStart.getY());
			} else if (p.getX() > canvas.getWidth() - DRAG_LIMIT) {
				canvasTransform.prependTranslation(-DRAG_STEP,0);
				dragStart = new Point2D(dragStart.getX()-DRAG_STEP, dragStart.getY());
			}
			
			if(p.getY() < DRAG_LIMIT) {
				canvasTransform.prependTranslation(0,DRAG_STEP);
				dragStart = new Point2D(dragStart.getX(), dragStart.getY()+DRAG_STEP);
			} else if (p.getY() > canvas.getHeight() - DRAG_LIMIT) {
				canvasTransform.prependTranslation(0,-DRAG_STEP);
				dragStart = new Point2D(dragStart.getX(), dragStart.getY()-DRAG_STEP);
			}			
			
			try {
				Affine b = new Affine(Transform.translate(p.getX() - dragStart.getX(), p.getY() - dragStart.getY()));
				Affine a = new Affine(canvasTransform);
				a.prepend(b);
				a.prepend(canvasTransform.createInverse());
				dragAffine = a;
			} catch (NonInvertibleTransformException e1) {
				e1.printStackTrace();
			}
			
			for (CanvasElement s : selectedObjects)
				if (s instanceof FmmlxObject) {
					FmmlxObject o = (FmmlxObject) s;
					o.dragTo(dragAffine);
					for(Edge<?> e : edges) {
						if(e.isSourceNode(o) || e.isTargetNode(o)) e.align();
					}
				} else if (s instanceof DiagramEdgeLabel) {
					DiagramEdgeLabel<?> o = (DiagramEdgeLabel<?>) s;
					o.dragTo(dragAffine);
				} else { // must be edge
					Edge<?> e = (Edge<?>) s;
					e.moveTo(p.getX(), p.getY(), this);
				}
			objectsMoved = true;

			for(Edge<?> e : edges) {e.align();}

			redraw();
		}

		private void mouseReleased(MouseEvent e) {
			if(isLeftButton(e)){
				if(mouseMode == MouseMode.MULTISELECT) {
					handleMultiSelect();
				}
				if(mouseMode == MouseMode.STANDARD) {
					mouseReleasedStandard();
				}
				if(mouseMode!=MouseMode.DRAW_EDGE) {
					mouseMode=MouseMode.STANDARD;
				}
	
				for(Edge<?> edge : edges) {
					edge.dropPoint(FmmlxDiagram.this);
				}

				triggerOverallReLayout();

				if(diagramRequiresUpdate) {
					diagramRequiresUpdate = false;
					updateDiagram();
				}
			} else if(isCenterButton(e)) {
				sendViewStatus();
			}
			redraw();
		}
		
		private void handleScroll(ScrollEvent e) {
			double zoom = Math.pow(ZOOM_STEP, e.getDeltaY() > 0 ? 1 : -1);
			Point2D pivot = new Point2D(e.getX(), e.getY());				
			zoomBy(zoom, pivot);				
			redraw();
			sendViewStatus();
		}
		
		private void zoomBy(double zoomFactor, Point2D pivot) {
			try {
				Point2D pivot_ = canvasTransform.inverseTransform(pivot);
				canvasTransform.append(new Scale(zoomFactor, zoomFactor, pivot_.getX(), pivot_.getY()));	
			} catch (NonInvertibleTransformException e1) {
				e1.printStackTrace();
			}
		}

		private void handleLeftPressed(MouseEvent e) {
			CanvasElement hitObject = getElementAt(e.getX(), e.getY());
			Point2D unTransformedPoint = null;
			try{
				unTransformedPoint = getCanvasTransform().inverseTransform(new Point2D(e.getX(), e.getY()));
			} catch (javafx.scene.transform.NonInvertibleTransformException ex) {}
			

			if (nodeCreationType == null && edgeCreationType == null) {
				handleLeftPressedDefault(e, hitObject);

			} else if (edgeCreationType != null) {
				if (edgeCreationType.equals("association")) {
					if(hitObject instanceof FmmlxObject) {		
						setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.Association);
						canvas.setCursor(Cursor.DEFAULT);

					}
				} else if (edgeCreationType.equals("associationInstance")) {
					if(hitObject instanceof FmmlxObject) {
						setDrawEdgeMode((FmmlxObject) hitObject, PropertyType.AssociationInstance);
						canvas.setCursor(Cursor.DEFAULT);
					}
				} else if (edgeCreationType.equals("delegation")) {
					if(hitObject instanceof FmmlxObject) {
						setDrawEdgeMode((FmmlxObject)hitObject, PropertyType.Delegation);
						canvas.setCursor(Cursor.DEFAULT);
					}
				}
			} else {
				if (nodeCreationType.equals("MetaClass")) {
					actions.addMetaClassDialog(unTransformedPoint);
				} else {
					actions.addInstanceDialog(getObjectByPath((nodeCreationType)),unTransformedPoint);
				}
				canvas.setCursor(Cursor.DEFAULT);
				deselectAll();
			}
		}
		
		private void handlePressedOnNodeElement(Point2D p, CanvasElement hitObject) {
			if (hitObject instanceof FmmlxObject) {
				FmmlxObject obj = (FmmlxObject) hitObject;
				lastHitProperty = obj.handlePressedOnNodeElement(p, this, canvas.getGraphicsContext2D(), canvasTransform);
			}
		}

		private void handleDoubleClickOnNodeElement(Point2D p, CanvasElement hitObject) {
			if (hitObject instanceof FmmlxObject) {
				FmmlxObject obj = (FmmlxObject) hitObject;
				obj.performDoubleClickAction(p, canvas.getGraphicsContext2D(), canvasTransform, this);
			} else if (hitObject instanceof DiagramEdgeLabel) {
				DiagramEdgeLabel<?> l = (DiagramEdgeLabel<?>) hitObject;
				l.performAction();
			} else if (hitObject instanceof FmmlxAssociation) {
				actions.editAssociationDialog((FmmlxAssociation) hitObject);
			}
		}

		private void handleRightPressed(MouseEvent e) {
			CanvasElement hitObject = getElementAt(e.getX(), e.getY());
			if (hitObject != null) {
				if (hitObject instanceof FmmlxObject) {
					activeContextMenu = hitObject.getContextMenu(this, new Point2D(e.getX(), e.getY()));
				} else if (hitObject instanceof Edge) {
					activeContextMenu = hitObject.getContextMenu(this, new Point2D(e.getX(), e.getY()));
				}
				if (!selectedObjects.contains(hitObject)) {
					deselectAll();
					selectedObjects.add(hitObject);
				}
			} else {
				activeContextMenu = new DefaultContextMenu(this);
			}
			showContextMenu(e);
		}
		
		private void handleLeftPressedDefault(MouseEvent e, CanvasElement hitObject) {
			Point2D p = new Point2D(e.getX(), e.getY());

			if (hitObject != null) {
				if (mouseMode == MouseMode.DRAW_EDGE) {
					mouseMode = MouseMode.STANDARD;
					FmmlxObject newEdgeTarget = hitObject instanceof FmmlxObject ? (FmmlxObject) hitObject : null;
					switch (drawEdgeType) {
						case Association:
							actions.addAssociationDialog(newEdgeSource, newEdgeTarget);
	                        setStandardMouseMode();
							break;
						case AssociationInstance:
						{
							final FmmlxObject obj1 = newEdgeSource;
							final FmmlxObject obj2 = newEdgeTarget;
							Platform.runLater(() -> {
								actions.addAssociationInstance(obj1, obj2,null);
								updateDiagramLater();
								setStandardMouseMode();
							});
							break;
						}
						case Delegation:
						{
							final FmmlxObject delegateFrom = newEdgeSource;
							final FmmlxObject delegateTo = newEdgeTarget;
							Platform.runLater(() -> {
								actions.setDelegation(delegateFrom, delegateTo);
							});
							break;
						}
						case RoleFiller:
						{
							final FmmlxObject delegateFrom = newEdgeSource;
							final FmmlxObject delegateTo = newEdgeTarget;
							Platform.runLater(() -> {
								actions.setRoleFiller(delegateFrom, delegateTo);
							});
							break;
						}
						default:
							break;
					}
					deselectAll();
				}

				if (e.isControlDown()) {
					if (selectedObjects.contains(hitObject)) {
						selectedObjects.remove(hitObject);
					} else {
						selectedObjects.add(hitObject);
					}
				} else {
					if (!selectedObjects.contains(hitObject)) {
						selectedObjects.clear();
						selectedObjects.add(hitObject);
						highlightElementAt(hitObject, p);
					}
				}
				handlePressedOnNodeElement(p, hitObject);

				if (e.getClickCount() == 2) {
					handleDoubleClickOnNodeElement(p, hitObject);
				}
			} else {
				if (mouseMode == MouseMode.DRAW_EDGE) {
					switch (drawEdgeType) {
						case Association:
							mouseMode = MouseMode.STANDARD;
							actions.addAssociationDialog(newEdgeSource, null);
							break;
						case AssociationInstance:
							mouseMode = MouseMode.STANDARD;
							actions.addAssociationInstance(newEdgeSource, null,null);
							break;
						case Delegation:
						case RoleFiller:
							mouseMode = MouseMode.STANDARD;
							// no dialog if clicked into the void: actions.addDelegation(newEdgeSource, null);
							break;
						default:
							break;
					}
				} else {
					mouseMode = MouseMode.MULTISELECT;
					storeLastClick(e.getX(), e.getY());
					storeCurrentPoint(e.getX(), e.getY());
				}
			}
		}
		
		private void zoomClicked(MouseEvent e) {
			if(e.getButton() == MouseButton.PRIMARY) try {
				Point2D p = canvasTransform.inverseTransform(e.getX(), e.getY());
					// p is now the point which should appear 
					// in the active view in the centre 
					// with the zoom unchanged.
				View activeView = getActiveView();
				double zoom = activeView.getCanvasTransform().getMxx();
					// assuming that xx and yy are always equal.
					// (otherwise they will be from now on)				
				Affine a = new Affine(Affine.translate(-p.getX(), -p.getY()));
					// the point is moved to 0,0
				a.prependScale(zoom, zoom);
					// the canvas is scaled
				a.prependTranslation(activeView.getCanvas().getWidth()/2, activeView.getCanvas().getHeight()/2);
					// and moved by half a canvas
				((DiagramViewPane) activeView).canvasTransform = a;
				redraw();
				
			} catch (Exception E) {
				E.printStackTrace();
			}
		}
		
	    private void highlightElementAt(CanvasElement hitObject, Point2D mouse) {
			for (CanvasElement object : objects) {
				object.highlightElementAt(null, canvasTransform);
			}
			for (Edge<?> edge : edges) {
				edge.highlightElementAt(null, canvasTransform);
			}
			hitObject.highlightElementAt(mouse, canvasTransform);
		}
	    
//	    private int[][] h = new int[200][200];
		
		private void paintOn() {
			final GraphicsContext g = canvas.getGraphicsContext2D();
			// blank bg first:
			g.setTransform(new Affine());
			
			if(!isZoomView) {
				g.setFill(Color.LIGHTSKYBLUE);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				g.setStroke(Color.ROYALBLUE);
				g.setLineWidth(2.5);
				g.setFill(Color.WHITE);
				for(FmmlxObject o : objects) if (!o.hidden) {
					Point2D p = canvasTransform.transform(o.getCenterX(), o.getCenterY());
					g.strokeLine(canvas.getWidth()/2, canvas.getHeight()/2, p.getX(), p.getY()); 
				}
				g.fillRect(5, 5, canvas.getWidth()-10, canvas.getHeight()-10);
				//g.setFill(new Color(.8,.9,.8,1.));java.util.Random r = new java.util.Random(); h[r.nextInt(200)][r.nextInt(200)] = r.nextInt(0126)+03_01_01; for(int i = 0; i < canvas.getWidth()/016-1; i++) for(int j = 0; j < canvas.getHeight()/024-1; j++) g.fillText("" + (char)h[i][j], i*016+7, 025+j*024);
			} else {
				g.setFill(Color.WHITE);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				
				Affine newTransform = getZoomViewTransform();
				
				try{
					
				g.beginPath();
				g.moveTo(0, 0);
				g.lineTo(0, getHeight());
				g.lineTo(getWidth(), getHeight());
				g.lineTo(getWidth(), 0);
				g.lineTo(0, 0);

				Point2D p1 = getActiveView().getCanvasTransform().inverseTransform(new Point2D(0, 0));
				p1 = newTransform.transform(p1);

				Point2D p2 = getActiveView().getCanvasTransform().inverseTransform(new Point2D(getActiveView().getCanvas().getWidth(), getActiveView().getCanvas().getHeight()));
				p2 = newTransform.transform(p2);

				g.moveTo(p1.getX(), p1.getY());
				g.lineTo(p2.getX(), p1.getY());
				g.lineTo(p2.getX(), p2.getY());
				g.lineTo(p1.getX(), p2.getY());
				g.lineTo(p1.getX(), p1.getY());
				
				g.setFill(new Color(.5, .5, .5, .5));
				g.fill();
				
				} catch (Exception E) {}
				canvasTransform = newTransform;
				
			}
								
			if (objects.size() <= 0) {return;} // if no objects yet: out, avoid div/0 or similar
			
			// otherwise gather (first-level) objects to be painted
			Vector<CanvasElement> objectsToBePainted = new Vector<>();
			objectsToBePainted.addAll(objects);
			objectsToBePainted.addAll(labels);
			objectsToBePainted.addAll(edges);
			//reverse so that those first in the list are painted last
			Collections.reverse(objectsToBePainted);
			
			// Cleanup ports (to be moved somewhere else)
			for (FmmlxObject o : objects) {
				o.updatePortOrder();
			}	
			
			g.setFill(Color.BLACK);
			g.setTransform(canvasTransform);

			for (CanvasElement o : objectsToBePainted) {
				o.paintOn(g, canvasTransform, this);
			}
			
			g.setStroke(Color.BLACK);
			g.setLineWidth(1);
			g.setLineDashes(null);
			
			g.setTransform(canvasTransform);		
			if (mouseMode == MouseMode.MULTISELECT) {drawMultiSelectRect(g);}
			drawNewEdgeLine(g);
			
			g.setTransform(new Affine());	
			g.setFont(Font.font(FmmlxDiagram.FONT.getFamily(), FontWeight.NORMAL, FontPosture.REGULAR, 14));

//			g.setFill(Color.RED);
//			if (lastPointPressed!=null) {
//				
//			Point2D currentPointHover = lastPointPressed;
//			g.fillText(""+currentPointHover, currentPointHover.getX(), currentPointHover.getY());
//					
//			try {
//				Point2D hoverRaw = canvasTransform.inverseTransform(currentPointHover);
//				g.fillText(""+hoverRaw, currentPointHover.getX(), currentPointHover.getY()+15);
//			} catch (NonInvertibleTransformException e) {}
//			}
			//			
//			g.setFill(Color.PURPLE);
//			g.fillText(canvas.getWidth() + ":"  +canvas.getHeight(), 0, 20);
//
//			
//			g.setFill(Color.PURPLE);
//			try{g.fillText((int)(lastPointPressed.getX()) + ":"  +(int)(lastPointPressed.getY()), 0, 20);} catch (Exception E) {}
//			try{g.fillText((int)(currentPointMoving.getX()) + ":"  +(int)(currentPointMoving.getY()), 0, 40);} catch (Exception E) {}
//			
//			g.setStroke(Color.PURPLE);
//			g.strokeLine(0, 0, canvas.getWidth(), canvas.getHeight());
//			g.strokeLine(canvas.getWidth(), 0, 0, canvas.getHeight());
//			
//			g.setStroke(Color.RED);
//			g.setTransform(canvasTransform);
//			for(int x = 0; x <= 10; x++) {
//				g.strokeLine(x*100, 0, x*100, 1000);
//				g.strokeLine(0, x*100, 1000, x*100);
//			}
		}
		
		
		private Affine getZoomViewTransform() {
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;	
			boolean valid = false;
			
			for(FmmlxObject o : new Vector<>(objects)) if (!o.hidden) {
				if(o.getLeftX()   < minX) minX = o.getLeftX();
				if(o.getRightX()  > maxX) maxX = o.getRightX();
				if(o.getTopY()    < minY) minY = o.getTopY();
				if(o.getBottomY() > maxY) maxY = o.getBottomY();
				valid = true;
			}
			
			if(!valid) return new Affine();

			double xZoom = canvas.getWidth() / (maxX - minX); 
			double yZoom = canvas.getHeight() / (maxY - minY);
			double zoom = Math.min(xZoom, yZoom);
			
			return new Affine(zoom,0,-zoom*minX,0,zoom,-zoom*minY);
		}

		public double getZoom() {
			return zoom;
		}

		public void setMaxZoom(){
			double maxRight = 1;
			double maxBottom = 1;

			for (FmmlxObject object : objects) {
				maxRight = Math.max(maxRight, object.getRightX());
				maxBottom = Math.max(maxBottom, object.getBottomY());
			}
			for (Edge<?> edge : edges) {
				maxRight = Math.max(maxRight, edge.getMaxX());
				maxBottom = Math.max(maxBottom, edge.getMaxY());
			}

			double maxXzoom = 4096. / maxRight;
			double maxYzoom = 4096. / maxBottom;

			setZoom(Math.min(10,Math.min(maxXzoom, maxYzoom)));
		}

		public void setZoom(double zoom) {
			this.zoom = Math.min(10, Math.max(zoom, 1. / 8));

			canvasTransform = new Affine();
			canvasTransform.appendScale(zoom, zoom);
		}
		
		private CanvasElement getElementAt(double x, double y) {
			for (FmmlxObject o : new Vector<>(objects))
				if (o.isHit(x, y, canvas.getGraphicsContext2D(), canvasTransform, this))
					return o;
			for (Edge<?> e : new Vector<>(edges))
				if (e.isHit(x, y, canvas.getGraphicsContext2D(), canvasTransform, this))
					return e;
			for (DiagramEdgeLabel<?> l : new Vector<>(labels))
				if (l.isHit(x, y, canvas.getGraphicsContext2D(), canvasTransform, this))
					return l;
			return null;
		}
		
		public void zoomIn() {
			zoomBy(getZoom() * ZOOM_STEP, new Point2D(canvas.getWidth()/2, canvas.getHeight()/2));
			redraw();
			sendViewStatus();
		}

		public void zoomOut() {
			zoomBy(getZoom() / ZOOM_STEP, new Point2D(canvas.getWidth()/2, canvas.getHeight()/2));
			redraw();
			sendViewStatus();
		}

		public void zoomOne() {
			canvasTransform = new Affine();
			redraw();
			sendViewStatus();
		}
		
		private void clearContextMenus() {
			if (activeContextMenu != null && activeContextMenu.isShowing()) {
				activeContextMenu.hide();
			}
		}

		private void showContextMenu(MouseEvent p) {
			if (activeContextMenu != null) {
				activeContextMenu.show(canvas, Side.LEFT, p.getX(), p.getY());
			}
		}

		          public FmmlxDiagram getDiagram() { return FmmlxDiagram.this; }
		@Override public Affine getCanvasTransform() { return canvasTransform; }
		@Override public Canvas getCanvas() {	return canvas; }
		
		private transient Affine wheelDragStartAffine;
		private transient Point2D wheelDragStartPoint;
		
		private void handleCenterPressed(MouseEvent e) {
			wheelDragStartAffine = new Affine(canvasTransform);
			wheelDragStartPoint = new Point2D(e.getX(), e.getY());
		}
		
		private void sendViewStatus() {
			Vector<String> names = new Vector<>();
			Vector<Affine> transformations = new Vector<>();
			for(DiagramViewPane view : views) if(!view.isZoomView) {
				names.add(view.name);
				transformations.add(view.canvasTransform);
			}
			comm.sendViewStatus(diagramID, names, transformations);		
		}

		private void handleCenterDragged(MouseEvent e) {
			canvasTransform = new Affine(wheelDragStartAffine);
			canvasTransform.prependTranslation(e.getX() - wheelDragStartPoint.getX(), e.getY() - wheelDragStartPoint.getY());
			redraw();
		}
		
		private void handleMultiSelect() {
			double x = Math.min(lastPointPressed.getX(), currentPointMoving.getX());
			double y = Math.min(lastPointPressed.getY(), currentPointMoving.getY());
			double w = Math.abs(currentPointMoving.getX() - lastPointPressed.getX());
			double h = Math.abs(currentPointMoving.getY() - lastPointPressed.getY());

			Rectangle rec = new Rectangle(x, y, w, h);
			deselectAll();
			for (FmmlxObject o : objects) {
				if (isObjectContained(rec, o)) {
					select(o);
				}
			}

			mouseMode = MouseMode.STANDARD;
		}
		
		private boolean isObjectContained(Rectangle rec, FmmlxObject object) {
			Bounds bounds = object.rootNodeElement.getBounds();
			if(bounds == null) return false;
			Point2D p1 = new Point2D(bounds.getMinX(), bounds.getMinY());
			Point2D p2 = new Point2D(bounds.getMaxX(), bounds.getMaxY());
			return rec.contains(p1) && rec.contains(p2);
		}
	}

	public DiagramViewPane getActiveTab() {
		Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
		javafx.scene.Node activeNode = activeTab.getContent();
		DiagramViewPane activeView = (DiagramViewPane) activeNode;
		return activeView;
	}	
		
	private class MyTab extends Tab {
		final Label label;
		DiagramViewPane view;
		
		private MyTab(DiagramViewPane view) {
			super("", view);
			this.view = view;
			this.label = new Label(view.name);
			setLabel();
			setCloseListener();
		    
			
		}
		
		public void setCloseListener() {
			this.setOnCloseRequest(new EventHandler<Event>() {

		        public void handle(Event e) {
		        	Alert alert = new Alert(AlertType.CONFIRMATION);
		        	alert.setTitle("Confirmation Dialog");
		        	alert.setHeaderText("Close Tab");
		        	alert.setContentText("Press OK to close the tab!");

		        	Optional<ButtonType> result = alert.showAndWait();
		        	if (result.get() == ButtonType.OK){
		        	   views.remove(view);
		        	} else {
		        		e.consume();
		        	}
		        }
		    });
		}

		public void setView(DiagramViewPane newView) {
			this.view = newView;
    		setContent(newView);
    		label.setText(view.name);
			setLabel();
		}

		private void setLabel() {			
			setGraphic(label);
			label.setOnMouseClicked((event) -> {
				if (event.getClickCount() == 2) {
					TextInputDialog dialog = new TextInputDialog("new tab name");
					dialog.setTitle("Change tab name");
					dialog.setHeaderText("Change tab name");
					dialog.setContentText("Please enter the new name for this tab:");
					java.util.Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						view.name = result.get();
						label.setText(view.name);
					}
				}
			});
		}
		
//		protected void close() {
//	        Event.fireEvent(this, new Event(Tab.CLOSED_EVENT));
//	    }

		public MyTab() {
			super("*", null);
			this.label = new Label("void");
			setCloseListener();
			
		}		
	
	
	}
	
}
