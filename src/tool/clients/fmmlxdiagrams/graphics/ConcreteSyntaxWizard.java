package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;

/**
 * This "wizard" consists of the following parts:
 * 
 * * A preview pane in the centre
 * 
 * * A listview where all defined syntaxes are shown. 
 *   There is one xml-file per syntax. 
 *   All files in the stated directory and below are shown
 * * A treeview of all elements in the selected syntax file
 *   * NOT YET: a menu allowing to add/remove elements of the tree
 * * NOT YET: A place to assign the syntax to a class and a level
 * 
 * * A property pane on the right including 
 *   * an affine-controller for aligning the element
 *   * NOT YET: A place to assign modifications
 *   * NOT YET: A place to assign Identifiers if necessary
 *   * A freeze-button
 * 
 */

public class ConcreteSyntaxWizard extends Application {
	
	public static final String RESOURCES_CONCRETE_SYNTAX_REPOSITORY = "resources/concreteSyntaxRepository/";
	private ListView<Modification> modificationList = new ListView<Modification>();
	private ListView<ActionInfo> actionList = new ListView<ActionInfo>();
	private SplitPane splitPane;
	private HBox rightControl;
	private MyCanvas myCanvas;
	private final TreeView<NodeElement> SVGtree = new TreeView<NodeElement>();
	private DirectoryChooser directoryChooser;
	private AbstractSyntax selectedSyntax;
	private AffineController affineController = new AffineController();
	private ScrollPane syntaxScrollGrid = new ScrollPane();
	private Vector<AbstractSyntax> syntaxes = new Vector<>();
	private AbstractPackageViewer model;
	private FmmlxObject selectedClass;	
	private Integer selectedLevel;
	private enum EditMode {NO_MODEL, MODEL_NO_CLASS, MODEL_AND_CLASS}
	private EditMode editMode; 
	/**
	 * Creates a wizard without connection to a model. 
	 * Used for quickly checking the layout of the wizard.
	 * Also allows all existing concrete syntaxes to be viewed
	 */
	public ConcreteSyntaxWizard() {
		this(null, null, null);
	}
	
	/**
	 * Creates a wizard with access to one model.
	 * @param model
	 */
	public ConcreteSyntaxWizard(AbstractPackageViewer model) {
		this(model, null, null);
	}
	
	/**
	 * Creates a wizard with access to one model, 
	 * with one class and a level preselected which a concrete syntax can be assigned to.
	 * @param model
	 * @param selectedClass
	 */
	public ConcreteSyntaxWizard(AbstractPackageViewer model, FmmlxObject selectedClass, Integer level) {
		this.model = model;
		this.selectedClass = selectedClass;
		this.selectedLevel = level;
		editMode = model == null?EditMode.NO_MODEL:selectedClass==null?EditMode.MODEL_NO_CLASS:EditMode.MODEL_AND_CLASS;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		loadConcreteSyntax();
		splitPane = new SplitPane();
		splitPane.setPadding(new Insets(10,10,10,10));
			
		myCanvas = new MyCanvas();		
		
		Image saveIcon = new Image(new File("resources/gif/img/save.gif").toURI().toString());
		ImageView imageViewSaveIcon = new ImageView(saveIcon);
		
		/////////
		
		VBox leftControl = new VBox(5.);
		leftControl.setPadding(new Insets(5.));		

		HBox modelBox = new HBox(new Label("Selected Model"), new TextField(model==null?"NONE":model.getPackagePath()));
		leftControl.getChildren().add(modelBox);
		
		HBox classBox = new HBox(
				new Label("Selected Class"), 
				new TextField(selectedClass==null?"NONE":selectedClass.getName()),
				new Label("@"), 
				new TextField(selectedLevel==null?"NONE":(selectedLevel+"")));
		leftControl.getChildren().add(classBox);
				
		TextField directoryTextField = new TextField(new File(RESOURCES_CONCRETE_SYNTAX_REPOSITORY).toString());
		directoryTextField.setDisable(true);
		directoryTextField.setMinWidth(300);
		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(RESOURCES_CONCRETE_SYNTAX_REPOSITORY));
		
		Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
	    ImageView imageView = new ImageView(icon);
	    Button fileDirectory = new Button();
		fileDirectory.setGraphic(imageView);
		fileDirectory.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            directoryTextField.setText(selectedDirectory.toString());          
        });	
		
		HBox directoryBox = new HBox(fileDirectory, directoryTextField);
		directoryBox.setPadding(new Insets(10,10,10,10));
		leftControl.getChildren().add(directoryBox);
		Label labelListView = new Label("ListView");
		leftControl.getChildren().add(labelListView);
			
        syntaxScrollGrid = new ScrollPane(new ConcreteSyntaxGrid());
        syntaxScrollGrid.setMaxHeight(300);
        syntaxScrollGrid.setMinWidth(530);
        syntaxScrollGrid.setPrefWidth(530);
        leftControl.getChildren().add(syntaxScrollGrid);
        
		TreeItem<NodeElement> rootTreeItem = new TreeItem<>();
		SVGtree.setRoot(rootTreeItem);
		SVGtree.getSelectionModel().selectedItemProperty().addListener((a,b,item)->{
			if (item!=null) {
				setCurrentGraphicElement(item.getValue());	
			}			
		});	
		SVGtree.setMaxHeight(300);
		Label labelTreeView = new Label("TreeView");
        leftControl.getChildren().add(labelTreeView);
        leftControl.getChildren().add(SVGtree);

        Button addModButton = new Button("add"); addModButton.setDisable(true);
        Button editModButton = new Button("edit"); editModButton.setDisable(true);
        Button removeModButton = new Button("remove"); removeModButton.setDisable(true);
        HBox modLabelsAndButtons = new HBox(
        		new Label("Modifications"),
        		addModButton,
        		editModButton,
        		removeModButton);        
        leftControl.getChildren().add(modLabelsAndButtons);
        leftControl.getChildren().add(modificationList);
        modificationList.setMaxHeight(200);

        Button addActionButton = new Button("add"); addActionButton.setDisable(true);
        Button editActionButton = new Button("edit"); editActionButton.setDisable(true);
        Button removeActionButton = new Button("remove"); removeActionButton.setDisable(true);
        HBox actionLabelsAndButtons = new HBox(
        		new Label("Actions"),
        		addActionButton,
        		editActionButton,
        		removeActionButton);    
        leftControl.getChildren().add(actionLabelsAndButtons);
        leftControl.getChildren().add(actionList);
        actionList.setMaxHeight(200);
		
		/////////		

		Label propertiesLabel = new Label("Properties");
		Button freezeSVG = new Button("Freeze");
		freezeSVG.setGraphic(imageViewSaveIcon);
		freezeSVG.setOnAction(e -> { if (selectedSyntax !=null)  selectedSyntax.save(); });
		VBox properties = new VBox(propertiesLabel, affineController.getMatrixPane(), affineController.getEditPane(), freezeSVG);
		properties.setMinWidth(200);
		
		/////////		

		rightControl  = new HBox(myCanvas,properties);
		splitPane = new SplitPane(leftControl, rightControl);
		splitPane.setDividerPosition(0, 0.2);
		
		Scene scene = new Scene(splitPane);
		primaryStage.setHeight(800);
		primaryStage.setWidth(1100);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Concrete Syntax Wizard");
		primaryStage.show();
	}	
	
	private Affine getZoomViewTransform(AbstractSyntax o, Canvas canvas) {
		o.updateBounds();
		double minX = o.bounds.getMinX();
		double minY = o.bounds.getMinY();
		double maxX = o.bounds.getMaxX();
		double maxY = o.bounds.getMaxY();

		double xZoom = canvas.getWidth() / (maxX - minX); 
		double yZoom = canvas.getHeight() / (maxY - minY);
		double zoom = Math.min(xZoom, yZoom) * 0.7;
		

		return new Affine(zoom,    0, -zoom*(minX + maxX)/2 + canvas.getWidth()/2,
	                         0, zoom, -zoom*(minY + maxY)/2 + canvas.getHeight()/2);
	}

	private void setCurrentGraphicElement(NodeElement item) {
		paint(item, myCanvas.zoom);
		/** an item is editable if it is any kind of a group or a label.
		 * editable means that the transformation can be changed.
		 */
		boolean editable = item instanceof NodeLabel || item instanceof NodeGroup;
		affineController.setEditable(editable);
		affineController.setAffine(item.getMyTransform());
		affineController.setListener(() -> {
			if(editable) {
				item.myTransform = affineController.getAffine();
				paint(item, myCanvas.zoom);
			}
		});
	}

	private void paint(NodeElement item, double zoom) {
		myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
		myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.BLACK);
		myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
		if(item == null) return;
		NodeElement item4Bounds = SVGtree.getRoot().getValue();
		item4Bounds.updateBounds();
		if(item4Bounds.bounds != null) {
			myCanvas.affine = new Affine(zoom,0, 
				myCanvas.getCanvas().getWidth() / 2
				-zoom*(item4Bounds.bounds.getMinX() + item4Bounds.bounds.getWidth() / 2),
				0,zoom, 
				myCanvas.getCanvas().getHeight() / 2
				-zoom*(item4Bounds.bounds.getMinY() + item4Bounds.bounds.getHeight() / 2));
			
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(myCanvas.affine);
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.WHITE);
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(
					item4Bounds.bounds.getMinX(),
					item4Bounds.bounds.getMinY(),
					item4Bounds.bounds.getWidth(),
					item4Bounds.bounds.getHeight());
			item4Bounds.paintOn(myCanvas, false);
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.web("#ffffff88"));
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
		}		
		
		item.paintOn(myCanvas, false);
		
		try{ 
			Affine a = item4Bounds.getTotalTransform(myCanvas.affine);
			Point2D o = a.transform(new Point2D(0, 0));
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
			myCanvas.getCanvas().getGraphicsContext2D().setStroke(Color.GRAY);
			myCanvas.getCanvas().getGraphicsContext2D().setLineWidth(1);
			myCanvas.getCanvas().getGraphicsContext2D().setLineDashes(null);
			myCanvas.getCanvas().getGraphicsContext2D().strokeLine(0, o.getY(), myCanvas.getCanvas().getWidth(),  o.getY());
			myCanvas.getCanvas().getGraphicsContext2D().strokeLine(o.getX(), 0, o.getX(),  myCanvas.getCanvas().getHeight());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getConcreteSyntax(AbstractSyntax group) {
		selectedSyntax = null;		
		try {
			selectedSyntax = group;
			group.paintOn(myCanvas, false);
			setTree(group);
			SVGtree.getSelectionModel().select(0);
			modificationList.getItems().clear();
			modificationList.getItems().addAll(group.getModifications());
			actionList.getItems().clear();
			actionList.getItems().addAll(group.getActions());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadConcreteSyntax() {
		File initialDirectory = new File(RESOURCES_CONCRETE_SYNTAX_REPOSITORY);
		if (initialDirectory.isDirectory()) {
			
			Vector<File> directories = new Vector<>();
			directories.add(initialDirectory);
			while(!directories.isEmpty()) {
				File dir = directories.remove(0);
				for (File file : dir.listFiles()) {
					if(file.isDirectory()) directories.add(file); else
					if(file.getName().endsWith(".xml")) {
						try {
							AbstractSyntax group = AbstractSyntax.load(file);
							syntaxes.add(group);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}		
	}

	private void setTree(NodeGroup group) {
		TreeItem<NodeElement> rootElement = new TreeItem<NodeElement>(group);
		SVGtree.setRoot(rootElement);
		
		for (NodeElement child : group.nodeElements) {
			addToTree(child,rootElement);
		}
		rootElement.setExpanded(true);
	}

	private void addToTree(NodeElement element, TreeItem<NodeElement> parentItem) {
		TreeItem<NodeElement> item = new TreeItem<NodeElement>(element);
		parentItem.getChildren().add(item);
		for (NodeElement elm : element.getChildren()) {
			addToTree(elm,item);
		}
		item.setExpanded(true);
	}
	
	private class MiniCanvas extends Pane implements View {
		Canvas canvas; 
		Affine affine;		

		public MiniCanvas() {
			canvas = new Canvas(125,80);
			affine = new Affine();
			getChildren().add(canvas);
			setMinSize(125,80);
			setMaxSize(125,80);
			setPrefSize(125,80);
			canvas.widthProperty().bind(this.widthProperty());
			canvas.heightProperty().bind(this.heightProperty());
		}
		
		@Override public Canvas getCanvas() { return canvas; }
		@Override public Affine getCanvasTransform() { return affine; }
		@Override public void centerObject() {}
		@Override public void centerObject(FmmlxObject affectedObject) {}
	}
	
	private class MyCanvas extends Pane implements View {
		
		Canvas canvas; 
		Affine affine;
		
		public MyCanvas() {
			canvas = new Canvas(1000,800);
			affine = new Affine();
			getChildren().add(canvas);
			canvas.widthProperty().bind(this.widthProperty());
			canvas.heightProperty().bind(this.heightProperty());
			setPrefSize(1400, 1000);
			canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);
		}
		
		@Override
		public Canvas getCanvas() {
			return canvas;
		}

		@Override
		public Affine getCanvasTransform() {
			return affine;
		}
		
		private double zoom = 1.;
		
		private void handleScroll(ScrollEvent e) {
			double delta = e.getDeltaY();
			zoom = zoom * Math.pow(Math.pow(2, 1/3.), delta > 0 ? 1 : -1);	
			paint(SVGtree.getSelectionModel().getSelectedItem().getValue(),zoom);
		}

		@Override public void centerObject() {}
		@Override public void centerObject(FmmlxObject affectedObject) {}
	}
	
	private class ConcreteSyntaxGrid extends GridPane {
		int selectedIndex = -1;
		private ConcreteSyntaxGrid() {
			super();
			setMaxWidth(500);
			setMinWidth(500);
			setPrefWidth(500);
			updateContent();
		}
		
		private void updateContent() {
			getChildren().clear();
			for(int i = 0; i < syntaxes.size(); i++) {
				add(new ConcreteSyntaxTile(syntaxes.get(i)), i%4, i/4);
			}
		}
		
		private void fireSelectionChangedEvent(AbstractSyntax selectedGroup) {
			// TODO only if old != new then
			// TODO? use a listener
			getConcreteSyntax(selectedGroup);
		}

		private class ConcreteSyntaxTile extends VBox {
			private AbstractSyntax group;
			private ConcreteSyntaxTile(AbstractSyntax group) {
				this.group = group;
				setMinWidth(125);
				setMinHeight(125);
				setMaxWidth(125);
				setMaxHeight(125);

				final MiniCanvas canvas = new MiniCanvas();
				
				getChildren().add(canvas);
				getChildren().add(new Label(group.toString()));
				getChildren().add(new Label(group.file.getName()));
				group.paintOn(canvas, false);
				
				ChangeListener<Number> canvasChangeListener = (obs, oldVal, newVal) -> {
					Affine a = getZoomViewTransform(group, canvas.canvas);
					canvas.affine = a;
					if(ConcreteSyntaxGrid.this.getChildren().indexOf(this) == selectedIndex) {
						GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
						gc.setFill(Color.CORNFLOWERBLUE);
						gc.setTransform(new Affine());
						gc.fillRect(0, 0, canvas.canvas.getWidth(), canvas.canvas.getHeight());
					}
					group.paintOn(canvas, false);
				};
				
				canvas.widthProperty().addListener(canvasChangeListener);
				canvas.heightProperty().addListener(canvasChangeListener);
				
				setOnMouseClicked(me -> {
					if(me.getButton() == MouseButton.PRIMARY) {
						selectedIndex = ConcreteSyntaxGrid.this.getChildren().indexOf(this);
						updateContent();
						fireSelectionChangedEvent(this.group);
					}
				});
			}
		}
	}
}
