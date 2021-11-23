package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class ConcreteSyntaxWizard extends Application {

	
	
	static final String RESOURCES_ABSTRACT_SYNTAX_REPOSITORY = "resources/abstract-syntax-repository/";
	private ListView<String> listView = new ListView<String>();
	private SplitPane splitPane;
	private VBox leftControl;
	private HBox rightControl;
	private MyCanvas myCanvas;
	private final TreeView<NodeElement> SVGtree = new TreeView<NodeElement>();
	private DirectoryChooser directoryChooser;
	private AbstractSyntax selectedSyntax;
	
	Spinner<Double> yPosition = new Spinner<Double>(-1000.,1000.,0.);
	Spinner<Double> xPosition = new Spinner<Double>(-1000.,1000.,0.);
		
	Spinner<Double> scale = new Spinner<>(new SpinnerValueFactory<Double>() {
        double STEP = Math.pow(2, 0.25);              
        @Override public void decrement(int steps) { setValue(Math.pow(STEP, -steps) * getValue()); }
        @Override public void increment(int steps) { setValue(Math.pow(STEP, steps) * getValue()); }});

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
		

		TextField directoryTextField = new TextField(new File(RESOURCES_ABSTRACT_SYNTAX_REPOSITORY).toString());
		directoryTextField.setDisable(true);
		directoryTextField.setMinWidth(300);
		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(RESOURCES_ABSTRACT_SYNTAX_REPOSITORY));
		
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
		Label space = new Label("");
		Label xLabel = new Label("X: ");
		xPosition.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		xPosition.setEditable(true);
		Label yLabel = new Label("Y: ");
		yPosition.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);
		yPosition.setEditable(true);
		Label scaleLabel = new Label("Scale: ");
		
		scale.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		scale.setEditable(true);
		scale.getValueFactory().setValue(1.);
		Image saveIcon = new Image(new File("resources/gif/img/save.gif").toURI().toString());
		ImageView imageViewSaveIcon = new ImageView(saveIcon);
		Button freezeSVG = new Button("Freeze");
		freezeSVG.setGraphic(imageViewSaveIcon);
		freezeSVG.setOnAction(e -> {
			
			if (selectedSyntax !=null) {
				selectedSyntax.save();
			}
		
		});
		
		VBox properties = new VBox(propertiesLabel, space, xLabel,xPosition,yLabel,yPosition,scaleLabel,scale,freezeSVG);
				
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

	ChangeListener<Double> xListener, yListener, scaleListener;
	
	private void setCurrentGraphicElement(NodeElement item) {
		paint(item,myCanvas.zoom);
		if(xListener!=null)xPosition.valueProperty().removeListener(xListener);
		if(yListener!=null)yPosition.valueProperty().removeListener(yListener);
		if(scaleListener!=null)scale.valueProperty().removeListener(scaleListener);
		if(item instanceof SVGGroup || item instanceof NodeLabel || item instanceof NodeGroup) {
			xPosition.setDisable(false);
			yPosition.setDisable(false);
			scale.setDisable(false);
			xPosition.getValueFactory().setValue(item.myTransform.getTx());
			yPosition.getValueFactory().setValue(item.myTransform.getTy());
			scale.getValueFactory().setValue(Math.sqrt(item.myTransform.getMxx()*item.myTransform.getMyy()));
			
			xListener =(a,b,x)->{item.myTransform.setTx(x);paint(item,myCanvas.zoom);};
			yListener =(a,b,y)->{item.myTransform.setTy(y);paint(item,myCanvas.zoom);};
			scaleListener =(a,b,s)->{item.myTransform.setMxx(s);item.myTransform.setMyy(s);paint(item,myCanvas.zoom);};
			
			xPosition.valueProperty().addListener(xListener);
			yPosition.valueProperty().addListener(yListener);
			scale.valueProperty().addListener(scaleListener);
			
			
		} else {
			xPosition.setDisable(true);
			yPosition.setDisable(true);
			scale.setDisable(true);
		}
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
					item4Bounds.bounds.getMinX(),item4Bounds.bounds.getMinY(),item4Bounds.bounds.getWidth(),item4Bounds.bounds.getHeight());
			item4Bounds.paintOn(myCanvas, false);
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.web("#ffffff88"));
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
//			myCanvas.getCanvas().getGraphicsContext2D().fillRect(
//					item4Bounds.bounds.getMinX(),item4Bounds.bounds.getMinY(),item4Bounds.bounds.getWidth(),item4Bounds.bounds.getHeight());
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
		
		
//		myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
	}

	private void getConcreteSyntax(String path) {
		selectedSyntax = null;
		String newPath= RESOURCES_ABSTRACT_SYNTAX_REPOSITORY+path;
		
		try {
			AbstractSyntax group = AbstractSyntax.load(new File(newPath));
			selectedSyntax = group;
			group.paintOn(myCanvas, false);
			setTree(group);
			SVGtree.getSelectionModel().select(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//		try {
//			NodeGroup group = SVGReader.readSVG(newPath, new Affine());
//			
//		} catch (ParserConfigurationException | SAXException | IOException e1) {
//			e1.printStackTrace();
//		}
	}


	private void loadConcreteSyntax() {
		File initialDirectory = new File(RESOURCES_ABSTRACT_SYNTAX_REPOSITORY);
		if (initialDirectory.isDirectory()) {
			File[] files = initialDirectory.listFiles();
			for (File fileSearch : files) {
				if (fileSearch.isFile()) {
					if(fileSearch.getName().endsWith(".xml")) {
						listView.getItems().add(fileSearch.getName());
					}
				}
			}
		}		
	}


	private void setTree(NodeGroup group) {
		TreeItem<NodeElement> rootElement = new TreeItem<NodeElement>(group);
		SVGtree.setRoot(rootElement);
		setListener(group,rootElement);
		
		for (NodeElement child : group.nodeElements) {
			addToTree(child,rootElement);
		}
		rootElement.setExpanded(true);
	}


	private void setListener(NodeElement e, TreeItem<NodeElement> item) {
		
	}


	private void addToTree(NodeElement element, TreeItem<NodeElement> parentItem) {
		TreeItem<NodeElement> item = new TreeItem<NodeElement>(element);
		parentItem.getChildren().add(item);
		setListener(element,item);
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
		
	}
}
