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
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class ConcreteSyntaxWizard extends Application {

	private GridPane gridPane;
	private MyCanvas canvas;
	private final TreeView<String> SVGtree = new TreeView<String>();
	private final ListView<String> listView = new ListView<String>();
	private FileChooser fileChooser;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		gridPane = new GridPane();
		gridPane.setPadding(new Insets(10,10,10,10));
		gridPane.setHgap(10.0);
		gridPane.setVgap(10.0);
		
		canvas = new MyCanvas();
		canvas.getCanvas().getGraphicsContext2D().setFill(Color.FLORALWHITE);
		canvas.getCanvas().getGraphicsContext2D().fillRect(0, 0, 500, 500);
		
		
		TreeItem firstTreeItem = new TreeItem("ASD");
		SVGtree.setRoot(firstTreeItem);
		
		File file = new File("");
		file=new File(file.toURI()).getParentFile();
		TextField directoryTextField = new TextField(new File(file, "/MosaicFX/resources/abstract-syntax-repository/Orga/").toString());
		directoryTextField.setDisable(true);
		//directoryTextField.setMaxWidth(300);
		fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(file, "/MosaicFX/resources/abstract-syntax-repository/Orga/"));
		
		Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
	    ImageView imageView = new ImageView(icon);
	    Button fileDirectory = new Button();
		fileDirectory.setGraphic(imageView);
		fileDirectory.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            directoryTextField.setText(selectedFile.toString());
            try {
				NodeGroup group = SVGReader.readSVG(selectedFile, new Affine());
				group.paintOn(canvas, false);
			} catch (ParserConfigurationException | SAXException | IOException e1) {
				e1.printStackTrace();
			}
            
        });
		
	    
		gridPane.add(fileDirectory, 0, 0);
		gridPane.setHalignment(fileDirectory, HPos.RIGHT);
		gridPane.add(directoryTextField, 1, 0);
		gridPane.add(SVGtree, 0, 1);
		gridPane.add(canvas.getCanvas(), 1, 1);
		
		
		Scene scene = new Scene(gridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Concrete Syntax Wizard");
		primaryStage.show();
	}

	
	public class MyCanvas implements View{
		
		Canvas myCanvas; 
		Affine affine;
		
		public MyCanvas() {
			myCanvas = new Canvas(500,500);
			affine = new Affine();
		}
		
		
		@Override
		public Canvas getCanvas() {
			return myCanvas;
		}

		@Override
		public Affine getCanvasTransform() {
			return affine;
		}
		
	}
	
}
