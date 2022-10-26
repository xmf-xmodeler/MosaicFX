package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
	private ListView<String> listView = new ListView<String>();
	private SplitPane splitPane;
	private VBox leftControl;
	private HBox rightControl;
	private MyCanvas myCanvas;
	private final TreeView<NodeElement> SVGtree = new TreeView<NodeElement>();
	private DirectoryChooser directoryChooser;
	private AbstractSyntax selectedSyntax;
	private AffineController affineController = new AffineController();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		splitPane = new SplitPane();
		splitPane.setPadding(new Insets(10,10,10,10));
			
		myCanvas = new MyCanvas();		
		
		TreeItem<NodeElement> rootTreeItem = new TreeItem<>();
		SVGtree.setRoot(rootTreeItem);
		SVGtree.getSelectionModel().selectedItemProperty().addListener((a,b,item)->{
			if (item!=null) {
				setCurrentGraphicElement(item.getValue());	
			}			
		});		

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
		Label labelListView = new Label("ListView");
		Label labelTreeView = new Label("TreeView");
		
		Label propertiesLabel = new Label("Properties");
		Image saveIcon = new Image(new File("resources/gif/img/save.gif").toURI().toString());
		ImageView imageViewSaveIcon = new ImageView(saveIcon);
		Button freezeSVG = new Button("Freeze");
		freezeSVG.setGraphic(imageViewSaveIcon);
		freezeSVG.setOnAction(e -> {
			
			if (selectedSyntax !=null) {
				selectedSyntax.save();
			}
		
		});
		
		VBox properties = new VBox(propertiesLabel, affineController.getMatrixPane(), affineController.getEditPane(), freezeSVG);
		properties.setMinWidth(200);
		leftControl  = new VBox(directoryBox,labelListView, listView, labelTreeView, SVGtree);
		rightControl  = new HBox(myCanvas,properties);
		splitPane.getItems().addAll(leftControl, rightControl);
		splitPane.setDividerPosition(0, 0.2);
		
		Scene scene = new Scene(splitPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Concrete Syntax Wizard");
		primaryStage.show();
		loadConcreteSyntax();
		
		listView.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->getConcreteSyntax(listView.getSelectionModel().getSelectedItem()));
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

	private void getConcreteSyntax(String path) {
		selectedSyntax = null;		
		try {
			AbstractSyntax group = AbstractSyntax.load(new File(path));
			selectedSyntax = group;
			group.paintOn(myCanvas, false);
			setTree(group);
			SVGtree.getSelectionModel().select(0);
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
					if(file.getName().endsWith(".xml")) listView.getItems().add(file.getAbsolutePath());
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
	
	public class MyCanvas extends Pane implements View{
		
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
}
