package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.io.File;
import java.util.Vector;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.graphics.*;

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
	private final ConcreteSyntaxTree concreteSyntaxTreeView = new ConcreteSyntaxTree(this);
	private DirectoryChooser directoryChooser;
	private AffineController affineController = new AffineController();
	private ScrollPane syntaxScrollGrid = new ScrollPane();
	private PreviewGrid<AbstractSyntax> syntaxGrid;
	private Vector<AbstractSyntax> syntaxes = new Vector<>();
	private AbstractPackageViewer model;
	private NodeElement selectedNodeElement = null;
	
	private AbstractSyntax selectedSyntax;
	private FmmlxObject selectedClass;	
	private Integer selectedLevel;
	
	private Button newButton;
	
	private enum EditMode {NO_MODEL, MODEL_NO_CLASS}
	private EditMode editMode;
	private ComboBox<FmmlxObject> classSelectionBox;
	private ComboBox<Integer> levelSelectionBox;
	private TextField classSelectionField;
	private TextField levelSelectionField;
	
	private Button editModButton;
    private Button removeModButton;
    private Button editActionButton;
    private Button removeActionButton;

//	private Insets defaultPadding = new Insets(5.);
	private double defaultSpacing= 5.;
	
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
		editMode = model == null?EditMode.NO_MODEL:EditMode.MODEL_NO_CLASS;
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
		modelBox.setAlignment(Pos.BASELINE_LEFT);
		modelBox.setSpacing(defaultSpacing);
		leftControl.getChildren().add(modelBox);
		
		Node selectedClassNode = null;
		Node selectedLevelNode = null;
		
		if(editMode == EditMode.NO_MODEL) {
			classSelectionField = new TextField("N/A");
			classSelectionField.setDisable(true);
			levelSelectionField = new TextField("N/A");
			levelSelectionField.setMaxWidth(50);
			levelSelectionField.setDisable(true);
			selectedClassNode = classSelectionField;
			selectedLevelNode = levelSelectionField;
		} else {
			classSelectionBox = new ComboBox<FmmlxObject>();
			classSelectionBox.getItems().addAll(model.getObjects());
			levelSelectionBox = new ComboBox<Integer>();
			selectedClassNode = classSelectionBox;
			selectedLevelNode = levelSelectionBox;
		} 
		
		newButton = new Button("new"); 
		newButton.setOnAction(a -> {
			if(editMode == EditMode.NO_MODEL) {
				// TODO ...
			} else {
				newButton.setDisable(true);
				if(selectedClass != null && selectedLevel != null) { // && syntaxGrid.selectedIndex == -1) {
					ConcreteSyntax newSyntax = new ConcreteSyntax();
					newSyntax.classPath = selectedClass.getPath();
					newSyntax.level = selectedLevel;
					String s = selectedClass.getPath() + "." + selectedLevel;
					s = s.replace("::", ".") + ".xml";
					if(s.startsWith("Root.")) {
						s = s.substring(5);
					} else {
						System.err.println("Unexpected Path: " + s);
					}
					File dir = new File(RESOURCES_CONCRETE_SYNTAX_REPOSITORY, "WizardFiles");
					File f = new File(dir, s);
					newSyntax.setFile(f);
					System.err.println("File: " + f);
					syntaxes.add(newSyntax);
					syntaxGrid.select(newSyntax);
				}
			}
		});
		
		HBox classBox = new HBox(
				new Label("Selected Class"), 
				selectedClassNode,
				new Label("@"), 
				selectedLevelNode,
				newButton);
		classBox.setAlignment(Pos.BASELINE_LEFT);
		classBox.setSpacing(defaultSpacing);
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
			
		syntaxGrid = new PreviewGrid<AbstractSyntax>(syntaxes);
		syntaxGrid.setOnSelectionChanged(newSelection -> newSyntaxSelectedInPreview(newSelection));
        syntaxScrollGrid = new ScrollPane(syntaxGrid);
        syntaxScrollGrid.setMaxHeight(300);
        syntaxScrollGrid.setMinWidth(530);
        syntaxScrollGrid.setPrefWidth(530);
        leftControl.getChildren().add(syntaxScrollGrid);
        
		TreeItem<NodeElement> rootTreeItem = new TreeItem<>();
		concreteSyntaxTreeView.setRoot(rootTreeItem);
		concreteSyntaxTreeView.getSelectionModel().selectedItemProperty().addListener((a,b,item)->{
			if (item!=null) {
				setCurrentGraphicElement(item.getValue());	
			}			
		});	
		concreteSyntaxTreeView.setMaxHeight(300);
		
		Label labelTreeView = new Label("TreeView");
        leftControl.getChildren().add(labelTreeView);
        leftControl.getChildren().add(concreteSyntaxTreeView);

        editModButton = new Button("edit"); editModButton.setDisable(true);
        removeModButton = new Button("remove"); removeModButton.setDisable(true);
        removeModButton.setOnAction(e->removeSelectedModification()); 
        HBox modLabelsAndButtons = new HBox(
        		new Label("Modifications"),
        		editModButton,
        		removeModButton);
        modLabelsAndButtons.setAlignment(Pos.BASELINE_LEFT);
        modLabelsAndButtons.setSpacing(defaultSpacing);    
        leftControl.getChildren().add(modLabelsAndButtons);
        leftControl.getChildren().add(modificationList);
        modificationList.setMaxHeight(200);
        modificationList.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,newVal)->newModificationItemSelected(oldVal,newVal));

        editActionButton = new Button("edit"); editActionButton.setDisable(true);
        removeActionButton = new Button("remove"); removeActionButton.setDisable(true);
        removeActionButton.setOnAction(e->removeSelectedAction()); 
        HBox actionLabelsAndButtons = new HBox(
        		new Label("Actions"),
        		editActionButton,
        		removeActionButton);  
        actionLabelsAndButtons.setAlignment(Pos.BASELINE_LEFT);
        actionLabelsAndButtons.setSpacing(defaultSpacing);  
        leftControl.getChildren().add(actionLabelsAndButtons);
        leftControl.getChildren().add(actionList);
        actionList.setMaxHeight(200);
        actionList.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,newVal)->newActionItemSelected(oldVal,newVal));
		
		/////////		

		Label propertiesLabel = new Label("Properties");
		Button freezeSVG = new Button("Freeze");
		freezeSVG.setGraphic(imageViewSaveIcon);
		freezeSVG.setOnAction(e -> { if (selectedSyntax !=null)  selectedSyntax.save(); });
		VBox propertiesVBox = new VBox(propertiesLabel, affineController.getMatrixPane(), affineController.getEditPane(), freezeSVG);
		propertiesVBox.setSpacing(5.);
		propertiesVBox.setPadding(new Insets(5.));	
		propertiesVBox.setMinWidth(200);	
		
		/////////		

		rightControl  = new HBox(myCanvas,propertiesVBox);
		splitPane = new SplitPane(leftControl, rightControl);
		splitPane.setDividerPosition(0, 0.2);
		
		/////////		
		
		if(classSelectionBox != null) {
			classSelectionBox.valueProperty().addListener((obs, oldVal, newVal) -> {
				if(oldVal != newVal && newVal != null) {
					selectedClass = newVal;
					levelSelectionBox.getItems().clear();
					for(int i = 0; i < selectedClass.getLevel(); i++) {
						levelSelectionBox.getItems().add(i);
					}
					if(selectedLevel != null && levelSelectionBox.getItems().size() > 0) {
						if(levelSelectionBox.getItems().contains(selectedLevel))
							levelSelectionBox.getSelectionModel().select((Integer) selectedLevel); 
						else
							levelSelectionBox.getSelectionModel().select((Integer) 0); 
					}
				}
			});
		}
		
		if(levelSelectionBox != null) {
			levelSelectionBox.valueProperty().addListener((obs, oldVal, newVal) -> {
				if(newVal != null) {
					selectedLevel = newVal;
					if(selectedClass != null) {
						selectConcreteSyntaxInPreviewList();
					}
				}
				checkNewButtonStatus();
			});
		}

		if(selectedClass != null) {
			classSelectionBox.getSelectionModel().select(selectedClass);
			levelSelectionBox.getSelectionModel().select(selectedLevel);
			checkNewButtonStatus();
		}
		
		Scene scene = new Scene(splitPane);
		primaryStage.setHeight(800);
		primaryStage.setWidth(1100);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Concrete Syntax Wizard");
		primaryStage.setMaximized(true);
		primaryStage.show();
	}	

	private void newSyntaxSelectedInPreview(AbstractSyntax selectedGroup) {
		if(editMode == EditMode.MODEL_NO_CLASS && selectedGroup instanceof ConcreteSyntax) {
			ConcreteSyntax cs = (ConcreteSyntax) selectedGroup;
			boolean found = false;
			for(FmmlxObject obj : classSelectionBox.getItems()) {
				if(obj.getPath().equals(cs.classPath)) {
					classSelectionBox.getSelectionModel().select(obj);
					levelSelectionBox.getSelectionModel().select((Integer) cs.level);
					found = true;
				}
			}
			if(!found) {
				classSelectionBox.getSelectionModel().select(-1);
				levelSelectionBox.getSelectionModel().select(-1);
			}
		}
		if(editMode == EditMode.MODEL_NO_CLASS && !(selectedGroup instanceof ConcreteSyntax)) {
			classSelectionBox.getSelectionModel().select(-1);
			levelSelectionBox.getSelectionModel().select(-1);
		}
		setSelectedSyntax(selectedGroup);
	}

	private void newModificationItemSelected(Modification oldVal, Modification newVal) {
		removeModButton.setDisable(newVal == null);
	}
	
	private void newActionItemSelected(ActionInfo oldVal, ActionInfo newVal) {
		removeActionButton.setDisable(newVal == null);
	}

	private void removeSelectedModification() {
		Modification m = modificationList.getSelectionModel().getSelectedItem();
		if(m != null && selectedSyntax != null && selectedSyntax instanceof ConcreteSyntax) {
			((ConcreteSyntax) selectedSyntax).removeModification(m);
			
			modificationList.getItems().clear();
			modificationList.getItems().addAll(selectedSyntax.getModifications());
		}
	}
	
	private void removeSelectedAction() {
		ActionInfo a = actionList.getSelectionModel().getSelectedItem();
		if(a != null && selectedSyntax != null && selectedSyntax instanceof ConcreteSyntax) {
			((ConcreteSyntax) selectedSyntax).removeAction(a);
			
			actionList.getItems().clear();
			actionList.getItems().addAll(selectedSyntax.getActions());
		}
	}
	
	private void checkNewButtonStatus() {
		boolean found = false;
		
		for(AbstractSyntax syntax : syntaxes) {
			if(syntax instanceof ConcreteSyntax) {
				ConcreteSyntax cs = (ConcreteSyntax) syntax;
				if(cs.classPath.equals(selectedClass.getPath()) 
						&& selectedLevel != null 
						&& cs.level == selectedLevel) {
					found = true;
				}
			}
		}
		newButton.setDisable(found);
	}

	private void selectConcreteSyntaxInPreviewList() {
		for(AbstractSyntax syntax : syntaxes) {
			if(syntax instanceof ConcreteSyntax) {
				ConcreteSyntax cs = (ConcreteSyntax) syntax;
				if(cs.classPath.equals(selectedClass.getPath()) 
						&& selectedLevel != null 
						&& cs.level == selectedLevel) {
					syntaxGrid.select(cs);
				}
			}
		}
	}

	private void setCurrentGraphicElement(NodeElement item) {
		selectedNodeElement = item;
		paint(item, myCanvas.zoom);
		/** an item is editable if it is any kind of a group or a label.
		 * It further must not be inside an svg nor must it be the overall root
		 * editable means that the transformation can be changed.
		 */
		boolean editable = 
				(item instanceof NodeLabel || item instanceof NodeGroup) && 
				(!item.isInsideSVG()) && item.getRoot() != item;
		affineController.setEditable(editable);
		affineController.setAffine(item.getMyTransform());
		affineController.setListener(() -> {
			if(editable) {
				item.setMyTransform(affineController.getAffine());
				paint(item, myCanvas.zoom);
			}
		});
	}

	private void paint(NodeElement item, double zoom) {
		myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
		myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.BLACK);
		myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
		if(item == null) return;
		NodeElement item4Bounds = concreteSyntaxTreeView.getRoot().getValue();
		item4Bounds.updateBounds();
		if(item4Bounds.getBounds() != null) {
			myCanvas.canvasTransform = new Affine(zoom,0, 
				myCanvas.getCanvas().getWidth() / 2
				-zoom*(item4Bounds.getBounds().getMinX() + item4Bounds.getBounds().getWidth() / 2),
				0,zoom, 
				myCanvas.getCanvas().getHeight() / 2
				-zoom*(item4Bounds.getBounds().getMinY() + item4Bounds.getBounds().getHeight() / 2));
			
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(myCanvas.canvasTransform);
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.WHITE);
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(
					item4Bounds.getBounds().getMinX(),
					item4Bounds.getBounds().getMinY(),
					item4Bounds.getBounds().getWidth(),
					item4Bounds.getBounds().getHeight());
			item4Bounds.paintOn(myCanvas, false);
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.web("#ffffff88"));
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
		}		
		
		item.paintOn(myCanvas, false);
		
		try{ // paint x/y-axis
			Affine a = item4Bounds.getTotalTransform(myCanvas.canvasTransform);
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
		
		{
			GraphicsContext g = myCanvas.getCanvas().getGraphicsContext2D();
			g.setTransform(new Affine());
			g.setFill(Color.YELLOW);
			g.fillText("MouseMode: " + myCanvas.mouseMode, 10, 20);
			g.fillText("Mouse: " + myCanvas.startMousePosition, 10, 40);
			g.fillText("Mouse: " + myCanvas.currentMousePosition, 10, 60);
			g.fillText("Pivot: " + myCanvas.pivot, 10, 80);
			g.fillText("Drag Transform: " + myCanvas.dragAffine, 10, 100);
		}
		
	}
	
	private void setSelectedSyntax(AbstractSyntax group) {
		selectedSyntax = null;		
		try {
			selectedSyntax = group;
			group.paintOn(myCanvas, false);
			concreteSyntaxTreeView.setTree(group);
			concreteSyntaxTreeView.getSelectionModel().select(0);
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

	public static Vector<SVGGroup> loadSVGs() {
		Vector<SVGGroup> svgs = new Vector<>();
		File initialDirectory = new File(RESOURCES_CONCRETE_SYNTAX_REPOSITORY);
		if (initialDirectory.isDirectory()) {
			
			Vector<File> directories = new Vector<>();
			directories.add(initialDirectory);
			while(!directories.isEmpty()) {
				File dir = directories.remove(0);
				for (File file : dir.listFiles()) {
					if(file.isDirectory()) directories.add(file); else
					if(file.getName().endsWith(".svg")) {
						try {
							SVGGroup svgGroup = SVGReader.readSVG(file, new Affine());
							svgs.add(svgGroup);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return svgs;
	}
	

	private enum MouseMode {TRANSLATE, SCALE, NONE};
	
	private class MyCanvas extends Pane implements View {
		
		Canvas canvas; 
		Affine canvasTransform;
		private MouseMode mouseMode = MouseMode.NONE;
		private transient Point2D pivot;
		private transient Point2D currentMousePosition;
		private transient Point2D startMousePosition;
		private transient Affine dragAffine;
		
		public MyCanvas() {
			canvas = new Canvas(1000,800);
			canvasTransform = new Affine();
			getChildren().add(canvas);
			canvas.widthProperty().bind(this.widthProperty());
			canvas.heightProperty().bind(this.heightProperty());
			setPrefSize(1400, 1000);
			canvas.addEventFilter(ScrollEvent.ANY, this::handleScroll);
			canvas.setOnMousePressed(e->mousePressed(e));
			canvas.setOnMouseReleased(e->mouseReleased(e));
			canvas.setOnMouseDragged(e->mouseDragged(e));
		}
		
		private void mousePressed(MouseEvent e) {
			startMousePosition = new Point2D(e.getX(), e.getY());
			currentMousePosition = startMousePosition;
			mouseMode = MouseMode.NONE;
			if(selectedNodeElement == null) return;
			
			{   // TODO: COPYPASTE see above, avoid redundancy
				/** an item is editable if it is any kind of a group or a label.
				 * It further must not be inside an svg nor must it be the overall root
				 * editable means that the transformation can be changed.
				 */
				boolean editable = 
						(selectedNodeElement instanceof NodeLabel || selectedNodeElement instanceof NodeGroup) && 
						(!selectedNodeElement.isInsideSVG()) && selectedNodeElement.getRoot() != selectedNodeElement;
				
				if(!editable) return;
			}
			
			if(e.isPrimaryButtonDown()) {
				if(e.isControlDown()) {
					mouseMode = MouseMode.SCALE;
					setPivot(e);
					repaintCanvas();
				} else {
					mouseMode = MouseMode.TRANSLATE;
					repaintCanvas();
				}
			}
		}
		
		private void repaintCanvas() {
			if(selectedNodeElement != null)
			paint(selectedNodeElement, zoom);
		}

		private void setPivot(MouseEvent e) {
			try{
				Affine selectedElementAffine = selectedNodeElement.getTotalTransform(canvasTransform);
				Point2D relativePoint = selectedElementAffine.inverseTransform(new Point2D(e.getX(), e.getY()));
				pivot = relativePoint;
			} catch (NonInvertibleTransformException niEx) {
				System.err.println("setPivot cancelled: NonInvertibleTransformException");
				mouseMode = MouseMode.NONE;
			}
		}

		private void mouseReleased(MouseEvent e) {
			try{
				currentMousePosition = new Point2D(e.getX(), e.getY());
				if(mouseMode == MouseMode.TRANSLATE || mouseMode == MouseMode.SCALE) {
					selectedNodeElement.drop();
					updateUI(selectedNodeElement);
				} 
			} finally {
				mouseMode = MouseMode.NONE;
				dragAffine = null;
				repaintCanvas();
			}
		}
		
		private void mouseDragged(MouseEvent e) {
			try{
				currentMousePosition = new Point2D(e.getX(), e.getY());
				if(mouseMode == MouseMode.TRANSLATE) {
					
					Affine b = new Affine(Transform.translate(e.getX() - startMousePosition.getX(), e.getY() - startMousePosition.getY()));
					b.prepend(selectedNodeElement.getMyTransform().createInverse());
					b.append(selectedNodeElement.getMyTransform());
					
					Affine a = new Affine(canvasTransform);
					a.prepend(b);
					a.prepend(canvasTransform.createInverse());
					
					dragAffine = a;
					selectedNodeElement.dragTo(dragAffine);
					repaintCanvas();
				} else if(mouseMode == MouseMode.SCALE) {
					double mouseRight = e.getX() - startMousePosition.getX();
					double exponent = mouseRight / 20;
					double scale = Math.pow(2, exponent);
					Affine a = new Affine(Affine.scale(scale, scale, pivot.getX(), pivot.getY()));
					dragAffine = a;
					selectedNodeElement.dragTo(dragAffine);
					repaintCanvas();
				}
			} catch (NonInvertibleTransformException niEx) {
				System.err.println("mouseDragged cancelled: NonInvertibleTransformException");
				mouseMode = MouseMode.NONE;
				repaintCanvas();
			}
		}

		@Override
		public Canvas getCanvas() {
			return canvas;
		}

		@Override
		public Affine getCanvasTransform() {
			return canvasTransform;
		}
		
		private double zoom = 1.;
		
		private void handleScroll(ScrollEvent e) {
			double delta = e.getDeltaY();
			zoom = zoom * Math.pow(Math.pow(2, 1/3.), delta > 0 ? 1 : -1);	
			if(concreteSyntaxTreeView.getSelectionModel().getSelectedItem() != null)
				paint(concreteSyntaxTreeView.getSelectionModel().getSelectedItem().getValue(),zoom);
			else {
				System.err.println("Cannot paint! Nothing selected...");
			}
		}

		@Override public void centerObject() {}
		@Override public void centerObject(FmmlxObject affectedObject) {}
	}
		

	
	public static String getNextAvailableID(String prefix, NodeGroup group) {
		int n = 0;
		while(null != group.getElement(prefix + n)) {
			n++;
		}
		return prefix + n;
	}

	public static String getRelativePath(File dir, File file) {
		String filePath = file.toURI().normalize().getPath();
		String relFilePath = dir.toURI().normalize().relativize(file.toURI().normalize()).getPath();
		
		if(!relFilePath.equals(filePath)) {
			return relFilePath;
		} else {
			File parentDir = dir.getParentFile();
			if(parentDir == dir || parentDir == null) {
				System.err.println("fail: reached top of file system!");
				throw new RuntimeException("Cannot relativize Path!");
			} else {
				return "../" + getRelativePath(parentDir, file);
			}
		}
	}

	public void updateUI(NodeElement item) {
		concreteSyntaxTreeView.setTree(item.getRoot());
		setCurrentGraphicElement(item.getRoot());
		syntaxGrid.updateContent();

		if(selectedSyntax != null) {
			modificationList.getItems().clear();
			modificationList.getItems().addAll(selectedSyntax.getModifications());
			actionList.getItems().clear();
			actionList.getItems().addAll(selectedSyntax.getActions());
		}
	}

	public FmmlxObject getSelectedClass() {
		return selectedClass;
	}

	public Integer getSelectedLevel() {
		return selectedLevel;
	}
}
