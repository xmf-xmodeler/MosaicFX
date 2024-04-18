package tool.clients.fmmlxdiagrams;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.scene.transform.Translate;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntax;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntaxPattern;
import tool.clients.fmmlxdiagrams.graphics.SvgConstant;
import tool.clients.fmmlxdiagrams.graphics.View;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.clients.fmmlxdiagrams.menus.DefaultContextMenu;
import tool.clients.fmmlxdiagrams.menus.DiagramViewHeadToolBar;
import tool.clients.fmmlxdiagrams.newpalette.FmmlxPalette;
import tool.clients.xmlManipulator.XmlHandler;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLUtil;

public class FmmlxDiagram extends AbstractPackageViewer{

	enum MouseMode {
		MULTISELECT, STANDARD, DRAW_EDGE
	}

	public static final boolean SHOW_MENUITEMS_IN_DEVELOPMENT = false;

	// The elements which the diagram consists of GUI-wise
	private SplitPane splitPane;
	private SplitPane splitPane2;
	private SplitPane splitPane3;
	private ScrollPane scrollPane;
	private VBox mainView;
	private TableView<Issue> tableView;
	private Vector<DiagramEdgeLabel<?>> labels = new Vector<>();
	private TabPane tabPane;
	private DiagramViewPane zoomView;
	public  static final Font FONT;
	static{
		FONT = Font.font(Font.getDefault().getFamily(), FontPosture.REGULAR, 14);
	}
	DiagramViewHeadToolBar diagramViewToolbar;
	DiagramDisplayModel diagramViewToolBarModel;

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
		
	@Override protected boolean loadOnlyVisibleObjects() { return false; }	// Did not work. Attributes from invisible classes did not cause slots on visible classes
	public final String diagramName;
	private final FmmlxPalette newFmmlxPalette;
	private String filePath;
	public String updateID = null;
	String edgeCreationType = null;
	String nodeCreationType = null;
	public LevelColorScheme levelColorScheme = new LevelColorScheme.FixedBlueLevelColorScheme();
	public final static FmmlxDiagram NullDiagram = new FmmlxDiagram();
	public final HashMap<String, ConcreteSyntax> syntaxes = new HashMap<>();
	private Vector<DiagramViewPane> views = new Vector<>();
	private final Set<KeyCode> pressedKeys = new HashSet<>();

	private FmmlxDiagram() {
		super(null,-1,null);
		this.newFmmlxPalette = null;
		this.diagramName = null;
	}

	public DiagramDisplayModel getDiagramViewToolBarModel() {
		return diagramViewToolBarModel;
	}

	public FmmlxDiagram(FmmlxDiagramCommunicator comm, int diagramID, String name, String packagePath, Vector<Vector<Object>> listOfViews, 
			Vector<Vector<Object>> listOfOptions, boolean umlMode) {
		super(comm,diagramID,packagePath);
		this.umlMode = umlMode; // <- TODO move to abstract, change to enum anyway
		diagramViewToolbar = new DiagramViewHeadToolBar(this);
		diagramViewToolBarModel = diagramViewToolbar.getModel();
//		diagramViewToolBarModel.setProperties(listOfOptions);
		
		this.diagramName = name;
		splitPane = new SplitPane();
		splitPane2 = new SplitPane();
		mainView = new VBox();
		
		tableView = new TableView<Issue>();
		TableColumn<Issue, FmmlxObject> objectColumn = new TableColumn<>("Object");
		TableColumn<Issue, Issue> issueColumn = new TableColumn<>("Issue");
		issueColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        objectColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        issueColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
		tableView.getColumns().add(objectColumn);
		tableView.getColumns().add(issueColumn);
		tableView.getSelectionModel().setCellSelectionEnabled(true);
		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					getActiveDiagramViewPane().centerObject(tableView.getSelectionModel().getSelectedItem().getAffectedObject(FmmlxDiagram.this));
				}
			}
		});
		
		objectColumn.setCellValueFactory(new Callback<CellDataFeatures<Issue,FmmlxObject>, ObservableValue<FmmlxObject>>() {

			@Override
			public ObservableValue<FmmlxObject> call(CellDataFeatures<Issue, FmmlxObject> f) {
				try {
				return new ReadOnlyObjectWrapper<FmmlxObject>(f.getValue().getAffectedObject(FmmlxDiagram.this));
				} catch(Exception e) {
					return null;
				}
			}			
		});
		objectColumn.setCellFactory((listView) -> {
			return new TableCell<Issue,FmmlxObject>() {

				@Override
	            protected void updateItem(FmmlxObject o, boolean empty) {
	                super.updateItem(o, empty);
	                if (o != null) {
	                	if(o.isAbstract()) setText("(" + o.getName() + " ^"+ o.getMetaClassName() + "^ " + ")"); else setText(o.getName()+ " ^"+ o.getMetaClassName() + "^");
	                	
	                    setGraphic(ModelBrowser.getClassLevelGraphic(o.getLevel().getMinLevel()));
	                } else { setText(""); setGraphic(null); }
	            }
	        };
	    });
		
		issueColumn.setCellValueFactory(new Callback<CellDataFeatures<Issue,Issue>, ObservableValue<Issue>>() {

			@Override
			public ObservableValue<Issue> call(CellDataFeatures<Issue, Issue> f) {
				try {
				return new ReadOnlyObjectWrapper<Issue>(f.getValue());
				} catch(Exception e) {
					return null;
				}
			}			
		});
		
		
		issueColumn.setCellFactory((listView) -> {
			return new TableCell<Issue,Issue>() {
				@Override
	            protected void updateItem(Issue issue, boolean empty) {
	                super.updateItem(issue, empty);
	                if(issue!=null) {
                		if(Issue.Severity.FATAL.equals(issue.getSeverity())) {
                			setGraphic(new ImageView(new javafx.scene.image.Image(new File("resources/gif/Classify/error.gif").toURI().toString())));
                		}
                		if(Issue.Severity.NORMAL.equals(issue.getSeverity())) {
                			setGraphic(new ImageView(new javafx.scene.image.Image(new File("resources/gif/Classify/error.gif").toURI().toString())));
                		}
                		if(Issue.Severity.BAD_PRACTICE.equals(issue.getSeverity())) {
                			setGraphic(new ImageView(new javafx.scene.image.Image(new File("resources/gif/User/Warning.gif").toURI().toString())));
                		}
                		if(Issue.Severity.USER_DEFINED.equals(issue.getSeverity())) {
                			setGraphic(new ImageView(new javafx.scene.image.Image(new File("resources/gif/MDC/Listener.gif").toURI().toString())));
                		}
                		setText(issue.getText());
                		} else { setText(""); setGraphic(null); }
	            }
	        };
	    });
		
		newFmmlxPalette = new FmmlxPalette(this);
		
        tabPane = new TabPane();
        
        for(Vector<Object> view : listOfViews) {
        	DiagramViewPane dvp = new DiagramViewPane((String) view.get(0), false);
        	float xx = 1.0f,  tx = 0.0f, ty = 0.0f;
        	try{ xx = (float) view.get(1); } catch (Exception e) {System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");}
        	try{ tx = (float) view.get(2); } catch (Exception e) {System.err.println("Cannot read tx: " + e.getMessage() + " Using default instead");}
        	try{ ty = (float) view.get(3); } catch (Exception e) {System.err.println("Cannot read tx: " + e.getMessage() + " Using default instead");}
        	dvp.canvasTransform = new Affine(xx, 0, tx, 0, xx, ty);
        	tabPane.getTabs().add(new MyTab(dvp));
        }
        
        if(listOfViews.size() == 0) {
        	DiagramViewPane dvp = new DiagramViewPane("default view", false);
        	tabPane.getTabs().add(new MyTab(dvp));
        }

        tabPane.getTabs().add(new MyTab());
        zoomView = new DiagramViewPane("", true);
        
        tabPane.setFocusTraversable(true);
		tabPane.setOnKeyReleased(keyEvent -> {
			pressedKeys.remove(keyEvent.getCode());

			if (keyEvent.isControlDown()) {
				FmmlxDiagramControlKeyHandler handler = new FmmlxDiagramControlKeyHandler(
						FmmlxDiagramCommunicator.getDiagram(diagramID));
				handler.handle(keyEvent.getCode());
			}

			if (keyEvent.getCode() == javafx.scene.input.KeyCode.F5) {
				getComm().triggerUpdate();
			}

			if (keyEvent.getCode() == javafx.scene.input.KeyCode.DELETE) {
				Vector<CanvasElement> hitObjects = getSelectedObjects();
				for (CanvasElement element : hitObjects) {
					if (element instanceof FmmlxObject) {
						new DiagramActions(FmmlxDiagram.this).removeDialog((FmmlxObject) element, PropertyType.Class);
					}
				}
			}

		});
		tabPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				pressedKeys.add(e.getCode());
				if (getPressedKeys().contains(KeyCode.CONTROL) &&
					getPressedKeys().contains(KeyCode.A)) {
					selectAll();
				}
				if (getPressedKeys().contains(KeyCode.CONTROL) && getPressedKeys().contains(KeyCode.S)) {
					//new XMLCreator().createAndSaveXMLRepresentation(packagePath, FmmlxDiagram.this);
					}
				if (getPressedKeys().contains(KeyCode.F5)) {
						getComm().triggerUpdate();
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
        
        scrollPane = new ScrollPane(tableView);
         
		mainView.getChildren().addAll(diagramViewToolbar, tabPane);
		
		splitPane2.setOrientation(Orientation.VERTICAL);
		splitPane2.setDividerPosition(0, 0.8);
		splitPane2.getItems().addAll(newFmmlxPalette.getToolBar(), zoomView);
		SplitPane.setResizableWithParent(zoomView, false);

		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.setDividerPosition(0, 0.2);
		splitPane.getItems().addAll(splitPane2, mainView);
		SplitPane.setResizableWithParent(splitPane2, false);
		
		splitPane.setOnKeyReleased(new javafx.event.EventHandler<javafx.scene.input.KeyEvent>() {
			/**
			 * Handles KeyEvent on the hole Stage
			 * @param event KeyEvent that is handled
			 */
			@Override
			public void handle(javafx.scene.input.KeyEvent event) {
				if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
					getActiveDiagramViewPane().escapeCreationMode();	
				}

			}
		});
		
		switchTableOnAndOffForIssues();
		Thread t = new Thread( () -> {
			this.fetchDiagramData( a -> { } );
			updateConcreteSyntaxes();
		});
		t.start();		
	}

	private void updateConcreteSyntaxes() {
		File syntaxDir = new File(ConcreteSyntaxWizard.RESOURCES_CONCRETE_SYNTAX_REPOSITORY); //TODO: recursively searching all 
		if (syntaxDir.isDirectory()) {
			Vector<File> directories = new Vector<>();
			Vector<File> files = new Vector<>();
			directories.add(syntaxDir);
			while(!directories.isEmpty()) {
				File dir = directories.remove(0);
				for (File file : dir.listFiles()) {
					if(file.isDirectory()) directories.add(file); else
					if(file.getName().endsWith(".xml")) files.add(file);
				}
			}
			for (File file : files) {
				try {
					ConcreteSyntaxPattern group = ConcreteSyntaxPattern.load(file);
					if(group instanceof ConcreteSyntax) {
						ConcreteSyntax c = ((ConcreteSyntax) group);
						syntaxes.put(c.classPath + "@" + c.level, c);
					}
				} catch (Exception e) {
					System.err.println("reading " + file.getName() + " failed (" + e.getMessage() + "). Ignoring..."); 
				}
			}
		}		
	}

	public void deselectPalette() {
		edgeCreationType = null;
		nodeCreationType = null;
		// if the palette is not updated no new actions could be performed
		getPalette().update();
	}
	
	public FmmlxPalette getPalette() {
		return newFmmlxPalette;
	}

	public void setEdgeCreationType(String edgeCreationType) {
		this.edgeCreationType = edgeCreationType;
		this.nodeCreationType= null;
		// TODO getCanvas().setCursor(Cursor.CROSSHAIR);
	}

	public void setNodeCreationType(String nodeCreationType) {
		this.nodeCreationType = nodeCreationType;
		this.edgeCreationType = null;
		// TODO getCanvas().setCursor(Cursor.CROSSHAIR);
	}
	
	public void activateNoteCreationMode() {
		setNodeCreationType("Note");
		Image noteImage = new Image(new File("resources/png/note.16.png").toURI().toString());
		Cursor noteCursor = new ImageCursor(noteImage);
		setPaneCursor(noteCursor);
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
	
	public Set<KeyCode> getPressedKeys () {
		  return pressedKeys;
	}

// Only used to set the diagram into the tab. Find a better solution
	@Deprecated
	public javafx.scene.Node getView() {
		return splitPane;
	}

	private void updateDiagramLater() {
		diagramRequiresUpdate = true;
	}
	
	public void redraw() {
		if(fetchingData) return;
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			// we are on the right Thread already:
			for(DiagramViewPane view : views) {
				view.paintOn();
			}			
		} else { // create a new Thread
//			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				for(DiagramViewPane view : views) {
					view.paintOn();
				}	
//				l.countDown();
			});
//			try {
//				l.await();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
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
//		long start = System.currentTimeMillis();
		// TODO evil hack. not kosher
		for(int i = 0; i < 2; i++) { 
			for(Node o : getVisibleObjectsReadOnly()) {
				o.layout(this, diagramViewToolBarModel.getDisplayPropertiesMap());
//				System.err.println("layout node " + o.name + ":"+ i + "->" +(System.currentTimeMillis()-start));
			}
//			System.err.println("layout nodes " + i + "->" +(System.currentTimeMillis()-start));
			for(Edge<?> edge : new Vector<>(edges)) {
				edge.align();
				edge.layoutLabels(this);
			}
//			System.err.println("layout edges " + i + "->" +(System.currentTimeMillis()-start));

		}
//		System.err.println("done "+(System.currentTimeMillis()-start));
	}
	
	public Vector<CanvasElement> getSelectedObjects() {
		return new Vector<>(selectedObjects);
	}

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
		Point2D p = getActiveDiagramViewPane().getCanvasTransform().transform(new Point2D(source.getCenterX(), source.getCenterY()));
		storeLastClick(p.getX(), p.getY());
		deselectAll();
	}

	////////////////////////////////////////////////////////////////////

	private void storeLastClick(double x, double y) {
		try{
			View view = getActiveDiagramViewPane();
			lastPointPressed = view.getCanvasTransform().inverseTransform(new Point2D(x, y));
		} catch (Exception ex) {
			lastPointPressed = new Point2D(x, y);
		}		
	}

	private void storeCurrentPoint(double x, double y) {
		try{
			View view = getActiveDiagramViewPane();
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
	
	void selectAll() {
		deselectAll();
		for (Node object : getObjectsReadOnly()) {
			selectedObjects.add(object);
			((DiagramViewPane) getActiveDiagramViewPane()).highlightElementAt(object,
					new Point2D(object.getCenterX(), object.getCenterY()));
		}
		for (Note note : notes) {
			select(note);
		}
		redraw(); 
	}

	public void setSelectedObject(CanvasElement source) {
		deselectAll();
		selectedObjects.add(source);
	}

	@Override public void setSelectedObjectAndProperty(FmmlxObject o, FmmlxProperty p) { setSelectedObject(o); }

	private void select(Node node) {
		if (!selectedObjects.contains(node)) {
			selectedObjects.add(node);
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
			for (FmmlxObject object : objects.values()) {
				if (object.getLevel().isClass()) {
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

		for (FmmlxObject object : objects.values()) {
			if (object.getLevel().getMinLevel()==level) {
				result.add(object);
			}
		}
		return result;
	}

	public Object getAllMetaClass() {
		Vector<FmmlxObject> result = new Vector<>();
		for (FmmlxObject object : getObjectsReadOnly()) {
			if (object.getLevel().isClass()) {
				result.add(object);
			}
		}
		return result;
	}

	public void setPaneCursor(Cursor c) {
		getActiveDiagramViewPane().canvas.setCursor(c);
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

	public void updateEnums() {
		comm.fetchAllEnums(this, enumsReceived -> {enums = enumsReceived;});
	}

	public FmmlxEnum getEnum(String enumName) {
		for (FmmlxEnum e : enums) {
			if(e.getName().equals(enumName)) return e;
		}
		return null;
	}

	public int getMaxLevel() {
		int level = 0;
		for (FmmlxObject tmp : objects.values()) {
			if(tmp.getLevel().getMinLevel()>level) {
				level=tmp.getLevel().getMinLevel();
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

	@Override
	protected void clearDiagram_specific() {
		labels.clear();
	}

	@Override
	protected void fetchDiagramDataSpecific() {
		for(FmmlxObject o : objects.values()) {
			o.layout(this, diagramViewToolBarModel.getDisplayPropertiesMap());
		}
		for(Note n : notes) {
			n.layout(this, diagramViewToolBarModel.getDisplayPropertiesMap());
		}
	}

	@Override
	protected void fetchDiagramDataSpecific2() {		
		triggerOverallReLayout();
		newFmmlxPalette.update();

		Issue nextIssue = null;
		for (int i = 0; i < issues.size() && nextIssue == null; i++) {
			if (issues.get(i).isSoluble() && !("BAD_PRACTICE".equals(issues.get(i).getSeverity().name())))
				nextIssue = issues.get(i);
		}
/*
		if (nextIssue != null) {
			final Issue ISSUE = nextIssue;
			Platform.runLater(() -> {
				System.err.println("performResolveAction");
				ISSUE.performResolveAction(this);
			});
		}
*/
		tableView.getItems().clear();
		tableView.refresh();
		tableView.getItems().addAll(issues);
		redraw();
	}

	public void paintToSvg(XmlHandler xmlHandler, double extraHeight){
		Vector<CanvasElement> objectsToBePainted = new Vector<>();
		objectsToBePainted.addAll(objects.values());
		objectsToBePainted.addAll(labels);
		objectsToBePainted.addAll(edges);
		Collections.reverse(objectsToBePainted);
		for (FmmlxObject o : objects.values()) {
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
		rect.setAttribute(SvgConstant.ATTRIBUTE_WIDTH, getActiveDiagramViewPane().canvas.getWidth()+"");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL, "black");
		rect.setAttribute(SvgConstant.ATTRIBUTE_FILL_OPACITY, 1 +"");
		xmlHandler.addXmlElement(issueGroup, rect);
		for(Issue issue : issues){
			issue.paintToSvg(xmlHandler, issueGroup, 14, 16, 0, getBounds().getMaxY()+issue.issueNumber*14);
		}
		xmlHandler.addXmlElement(xmlHandler.getRoot(), issueGroup); 
	}

	@Override
	public DiagramViewPane getActiveDiagramViewPane() {
		return (DiagramViewPane) tabPane.getSelectionModel().getSelectedItem().getContent();
	}
	
	public BoundingBox getBounds() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;	
		boolean valid = false;
		
		Vector<CanvasElement> elements = new Vector<CanvasElement>();
		elements.addAll(objects.values());
		elements.addAll(edges);
		
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
		}
		Double MARGIN = 5.;
		if(valid) return new BoundingBox(minX-MARGIN, minY-MARGIN, (maxX-minX)+2*MARGIN, (maxY-minY)+2*MARGIN);
		return new BoundingBox(0,0,100,100);
	}
		
	/*
	 * Does the same like the method below but allows to define a ReturnCall, that is executed after the Diagram is updated
	 */
	@Override
	public void updateDiagram(ReturnCall<Object> onDiagramUpdated) {
		super.updateDiagram(getView(), (ReturnCall<Object>) e -> {
			onDiagramUpdated.run(null);
		});
	}

	@Override
	public void updateDiagram() {
		//Performs the diagram update with empty return call
		super.updateDiagram(getView(), r -> {});	
  }
  
	public void switchTableOnAndOffForIssues() {
		mainView.getChildren().clear();
		if (diagramViewToolBarModel.getPropertieValue(DiagramDisplayProperty.ISSUETABLE)) {
			tableView.prefHeightProperty().bind(scrollPane.heightProperty());
	        tableView.prefWidthProperty().bind(scrollPane.widthProperty());
			splitPane3 = new SplitPane(tabPane, scrollPane);
			splitPane3.setOrientation(Orientation.VERTICAL);
			mainView.getChildren().addAll(diagramViewToolbar, splitPane3);
		} else {
			mainView.getChildren().addAll(diagramViewToolbar, tabPane);
		}
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			redraw();
		});
		t.start();
	}

	@Override
	protected void updateViewerStatusInGUI(ViewerStatus newStatus) {
		switch(newStatus) {
		case LOADING:
		case DIRTY:	
			diagramViewToolbar.toggleUpdateButton(true);
			break;
	
		default:
			diagramViewToolbar.toggleUpdateButton(false);
			break;
		}
		
	}
	
	public static Font getFont() {
		return FONT;
	}

	public class DiagramViewPane extends Pane implements View {
		
		Canvas canvas;
		private double zoom = 1.;
		Affine canvasTransform = new Affine();
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

		private boolean isLeftButton(MouseEvent e) {
			return e.getButton() == MouseButton.PRIMARY && !getPressedKeys().contains(KeyCode.ALT) && !getPressedKeys().contains(KeyCode.SPACE);}
		
		private boolean isRightButton(MouseEvent e) {
			return e.getButton() == MouseButton.SECONDARY;}
		
		private boolean isCenterButton(MouseEvent e) {
			return e.getButton() == MouseButton.MIDDLE || 
				   e.getButton() == MouseButton.PRIMARY && getPressedKeys().contains(KeyCode.ALT) ||
				   e.getButton() == MouseButton.PRIMARY && getPressedKeys().contains(KeyCode.SPACE);}

		private void mousePressed(MouseEvent e) {
			if(fetchingData) return;
			clearContextMenus();
			if (isLeftButton(e)) {
				handleLeftPressed(e);
				dragStart = new Point2D(e.getX(), e.getY());
			}
			if (isRightButton(e)) {
				handleRightPressed(e);
			}
			if (isCenterButton(e)) {
				handleCenterPressed(e);
			}
		}
		
		private void mouseDragged(MouseEvent e) {
			if (mouseMode == MouseMode.MULTISELECT) {
				storeCurrentPoint(e.getX(), e.getY());
				redraw();
			}
			if (mouseMode == MouseMode.STANDARD) {
				if (selectedObjects.size() == 1 && selectedObjects.firstElement() instanceof Edge) {
				((Edge<?>) selectedObjects.firstElement()).setPointAtToBeMoved(new Point2D(e.getX(), e.getY()),
				canvasTransform);
				}
				
				if (isLeftButton(e)) {
					mouseDraggedStandard(new Point2D(e.getX(), e.getY()));
					moveObjectsOnDrag(new Point2D(e.getX(), e.getY()));
				}
			}
			if (isCenterButton(e)){
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
				for (FmmlxObject o : objects.values())
					o.unHighlight();
				for (Edge<?> edge : edges)
					edge.unHighlight();
				for (DiagramEdgeLabel<?> l : labels)
					l.unHighlight();
			}

			if(elementUnderMouse != null) elementUnderMouse.highlightElementAt(new Point2D(e.getX(), e.getY()), canvasTransform);

		}

		private void mouseDraggedStandard(Point2D p) {
			// These constants define which amount of diagram edge is sensitive to objects 
			// being dragged out, and how "fast" the view will follow
			final double DRAG_LIMIT = 5 ,DRAG_STEP = 5;
			
			// If the mouse pointer is within the edge regions,
			// the canvas transform is adapted so that the view follows th dragged object
			// and the drag start is corrected by this amount, 
			// otherwise the dragged object would no longer follow the mouse pointer
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
			
			// Whether the view has followed the mouse or not
			// now the difference between the start of the drag and the current mouse position 
			// is used to calculate the drag transformation
			// (which is chained to the the other transformations of an object until it is dropped)
			try {
				Affine b = new Affine(Transform.translate(p.getX() - dragStart.getX(), p.getY() - dragStart.getY()));
				Affine a = new Affine(canvasTransform);
				a.prepend(b);
				a.prepend(canvasTransform.createInverse());
				dragAffine = a;
			} catch (NonInvertibleTransformException e1) {
				// Hopefully this will never happen.
				// Presumably this only happens if it's zoomed in or out infinitely far
				e1.printStackTrace();
			}			
		}

		private void moveObjectsOnDrag(Point2D p) {
			for (CanvasElement s : selectedObjects)
				if (s instanceof Node) {
					Node n = (Node)s;
					moveNode(n);
				} else if (s instanceof DiagramEdgeLabel) {
					DiagramEdgeLabel<?> o = (DiagramEdgeLabel<?>) s;
					o.dragTo(dragAffine);}
				else { // must be edge
					Edge<?> e = (Edge<?>) s;
					e.moveTo(p.getX(), p.getY(), this);
				}
			objectsMoved = true;
			for(Edge<?> e : edges) {e.align();}
			redraw();
		}

		private void moveNode(Node n) {
			n.dragTo(dragAffine);
			if (n instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) n;
				for (Edge<?> e : edges) {
					if (e.isSourceNode(o) || e.isTargetNode(o))
						e.align();
				}
			}
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
			} else if (nodeCreationType.equals("Note")) {
				actions.addNote(this.getDiagram(), unTransformedPoint);
				canvas.setCursor(Cursor.DEFAULT);
				deselectPalette();
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
			} else if (hitObject instanceof Note) {
				actions.editNote((Note) hitObject);
			}
		}

		private void handleRightPressed(MouseEvent e) {
			if (mouseMode == MouseMode.DRAW_EDGE || "Note".equals(nodeCreationType)) {
				escapeCreationMode();
				return;
			}
			CanvasElement hitObject = getElementAt(e.getX(), e.getY());
			if (hitObject != null) {
				if (hitObject instanceof FmmlxObject || hitObject instanceof Edge || hitObject instanceof InheritanceEdge || hitObject instanceof Note) {
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
//						highlightElementAt(hitObject, p);
					}
				}
				//Only implemented for FmmlxObject
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
		
		private void mouseReleasedStandard() {
			for (Edge<?> e : edges) e.removeRedundantPoints();
			if (objectsMoved) {
				releaseObjects();
			}
			objectsMoved = false;
		}

		private void releaseObjects() {
			for (CanvasElement s : selectedObjects)
				if (s instanceof Node) {
					Node n = (Node) s;
					releaseNode(n);
				} else if (s instanceof Edge) {
					comm.sendCurrentEdgePositions(diagramID, (Edge<?>) s);
				} else if (s instanceof DiagramEdgeLabel) {
					DiagramEdgeLabel<?> del = (DiagramEdgeLabel<?>) s;
					del.drop();
					del.owner.updatePosition(del);
					comm.storeLabelInfo(getDiagram(), del);
				}
		}
		
		private void releaseNode(Node n) {
			n.drop();
			n.updatePositionInBackend(diagramID);
			if (n instanceof FmmlxObject) {
				FmmlxObject o = (FmmlxObject) n;
				for (Edge<?> e : edges) {
					if (e.isSourceNode(o) || e.isTargetNode(o)) {
						comm.sendCurrentEdgePositions(diagramID, e);
					}
				}
			}
		
		}

		private void zoomClicked(MouseEvent e) {
			if(e.getButton() == MouseButton.PRIMARY) try {
				Point2D p = canvasTransform.inverseTransform(e.getX(), e.getY());
					// p is now the point which should appear 
					// in the active view in the centre 
					// with the zoom unchanged.
				View activeView = getActiveDiagramViewPane();
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
			for (CanvasElement object : objects.values()) {
				object.highlightElementAt(null, canvasTransform);
			}
			for (Edge<?> edge : edges) {
				edge.highlightElementAt(null, canvasTransform);
			}
			hitObject.highlightElementAt(mouse, canvasTransform);
		}
	    
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
				for(FmmlxObject o : objects.values()) if (!o.hidden) {
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

				Point2D p1 = getActiveDiagramViewPane().getCanvasTransform().inverseTransform(new Point2D(0, 0));
				p1 = newTransform.transform(p1);

				Point2D p2 = getActiveDiagramViewPane().getCanvasTransform().inverseTransform(new Point2D(getActiveDiagramViewPane().getCanvas().getWidth(), getActiveDiagramViewPane().getCanvas().getHeight()));
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
	
			if (objects.size() <= 0 && notes.isEmpty()) {return;} // if no objects yet: out, avoid div/0 or similar
			
			// otherwise gather (first-level) objects to be painted
			Vector<CanvasElement> objectsToBePainted = new Vector<>();
			//For some reason the notes should not be painted before the edges otherwise the edges are moved when the notes are moved
			objectsToBePainted.addAll(notes);
			objectsToBePainted.addAll(objects.values());
			objectsToBePainted.addAll(labels);
			objectsToBePainted.addAll(edges);
			
			//reverse so that those first in the list are painted last
			Collections.reverse(objectsToBePainted);
			
			// Cleanup ports (to be moved somewhere else)
			for (FmmlxObject o : objects.values()) {
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
		}
		
		
		private Affine getZoomViewTransform() {
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;	
			boolean valid = false;
			
			for(FmmlxObject o : new Vector<>(objects.values())) if (!o.hidden) {
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

			for (FmmlxObject object : objects.values()) {
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
			Vector<CanvasElement> all = new Vector();
			all.addAll(new Vector<>(objects.values()));
			all.addAll(new Vector<>(edges));
			all.addAll(new Vector<>(labels));
			all.addAll(new Vector<>(notes));
			
			for (CanvasElement canvasElement : all) {
				if (canvasElement.isHit(x, y, canvas.getGraphicsContext2D(), canvasTransform, this))
					return canvasElement ;
			}
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
			if(wheelDragStartAffine == null) return; // user was too fast, let them try again later
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
			
			for (Node node : getAllNodes()) {
				if (!node.isHidden() && isObjectContained(rec, node)) {
					select(node);
				}
			}
			mouseMode = MouseMode.STANDARD;
		}
		
		private boolean isObjectContained(Rectangle rec, Node node) {
			Bounds bounds = node.rootNodeElement.getBounds();
			if(bounds == null) return false;
			Point2D p1 = new Point2D(bounds.getMinX(), bounds.getMinY());
			Point2D p2 = new Point2D(bounds.getMaxX(), bounds.getMaxY());
			return rec.contains(p1) && rec.contains(p2);
		}
				
		public void centerObject(FmmlxObject object) {
			Point2D viewCenter = new Point2D(this.getWidth() / 2, this.getHeight() / 2);
			try {
				Point2D viewCenter2 = canvasTransform.inverseTransform(viewCenter);
				canvasTransform.append(new Translate(viewCenter2.getX() - object.getCenterX(),
						viewCenter2.getY() - object.getCenterY()));
			} catch (NonInvertibleTransformException e) {
				e.printStackTrace();
			}
			redraw();
		}
		/**
		 * If a user chooses to create something but then decides that he does not need it, the canvas can be reset to normal by this function call.
		 */
		public void escapeCreationMode() {
			mouseMode = MouseMode.STANDARD;			
			setPaneCursor(Cursor.DEFAULT);
			deselectPalette();
			redraw();
		}
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

		public MyTab() {
			super("*", null);
			this.label = new Label("void");
			setCloseListener();
			
		}		
	}


}
