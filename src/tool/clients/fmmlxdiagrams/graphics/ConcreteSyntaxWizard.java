package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class ConcreteSyntaxWizard extends Application {

	
	
	private ListView<String> listView = new ListView<String>();
	private SplitPane splitPane;
	private VBox leftControl, rightControl;
	private MyCanvas myCanvas;
	private final TreeView<NodeElement> SVGtree = new TreeView<NodeElement>();
	private DirectoryChooser directoryChooser;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		splitPane = new SplitPane();
		splitPane.setPadding(new Insets(10,10,10,10));
			
		myCanvas = new MyCanvas();		
		
		TreeItem<NodeElement> rootTreeItem = new TreeItem<>();
		SVGtree.setRoot(rootTreeItem);
		SVGtree.getSelectionModel().selectedItemProperty().addListener((a,b,item)->{
			if(item == null) return;
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.DARKGRAY);
			myCanvas.getCanvas().getGraphicsContext2D().setTransform(new Affine());
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, myCanvas.getCanvas().getWidth(), myCanvas.getCanvas().getHeight());
			NodeElement item4Bounds = SVGtree.getRoot().getValue();
			item4Bounds.updateBounds();
			if(item4Bounds.bounds != null) {
			myCanvas.affine = new Affine(1,0, 
					myCanvas.getCanvas().getWidth() / 2
					-(item4Bounds.bounds.getMinX() + item4Bounds.bounds.getWidth() / 2),
					0,1, 
					myCanvas.getCanvas().getHeight() / 2
					-(item4Bounds.bounds.getMinY() + item4Bounds.bounds.getHeight() / 2));
//			myCanvas.getCanvas().getGraphicsContext2D().setTransform(myCanvas.affine);
			myCanvas.getCanvas().getGraphicsContext2D().setFill(Color.WHITE);
			myCanvas.getCanvas().getGraphicsContext2D().fillRect(
					myCanvas.getCanvas().getWidth() / 2
					-item4Bounds.bounds.getWidth() / 2, 
					myCanvas.getCanvas().getHeight() / 2
					-item4Bounds.bounds.getHeight() / 2,
					item4Bounds.bounds.getWidth(), 
					item4Bounds.bounds.getHeight());
			}
			item.getValue().paintOn(myCanvas, false);
		});
		
		
		File file = new File("");
		file=new File(file.toURI()).getParentFile();
		TextField directoryTextField = new TextField(new File(file, "/MosaicFX/resources/abstract-syntax-repository/Orga/").toString());
		directoryTextField.setDisable(true);
		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(file, "/MosaicFX/resources/abstract-syntax-repository/Orga/"));
		
		Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
	    ImageView imageView = new ImageView(icon);
	    Button fileDirectory = new Button();
		fileDirectory.setGraphic(imageView);
		fileDirectory.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            directoryTextField.setText(selectedDirectory.toString());          
        });	
		
		HBox directoryBox = new HBox(fileDirectory, directoryTextField);
		Label labelListView = new Label("ListView");
		Label labelTreeView = new Label("TreeView");
		leftControl  = new VBox(directoryBox,labelListView, listView, labelTreeView, SVGtree);
		rightControl  = new VBox(myCanvas);
		splitPane.getItems().addAll(leftControl, rightControl);
		
		
		Scene scene = new Scene(splitPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Concrete Syntax Wizard");
		primaryStage.show();
		loadConcreteSyntax();
		
		listView.getSelectionModel().selectedItemProperty().addListener((prop, old, NEWW)->getConcreteSyntax(listView.getSelectionModel().getSelectedItem()));
	}

	
	private void getConcreteSyntax(String path) {
		String newPath= "resources/abstract-syntax-repository/Orga/"+path;
		try {
			NodeGroup group = SVGReader.readSVG(newPath, new Affine());
			group.paintOn(myCanvas, false);
			setTree(group);
			SVGtree.getSelectionModel().select(0);
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
	}


	private void loadConcreteSyntax() {
		File file = new File("");
		file=new File(file.toURI()).getParentFile();
		File initialDirectory = new File(file, "/MosaicFX/resources/abstract-syntax-repository/Orga/");
		if (initialDirectory.isDirectory()) {
			File[] files = initialDirectory.listFiles();
			for (File fileSearch : files) {
				if (fileSearch.isFile()) {
					if(fileSearch.getName().endsWith("csd.svg")) {
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
		}
		
		
		@Override
		public Canvas getCanvas() {
			return canvas;
		}

		@Override
		public Affine getCanvasTransform() {
			return affine;
		}
		
	}
	
}