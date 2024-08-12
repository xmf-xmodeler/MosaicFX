package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
/**
 * 
 * @author Nicolas Engel
 *
 */
public class EditorElements {

    private Stage stage;

    public EditorElements() {
        stage = new Stage();
        stage.setTitle("Editor elements");

        ImageView imageView = buildContent();
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root);

        stage.setScene(scene);

        // Set the size of the dialog to fit within the screen bounds
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
    }

    private ImageView buildContent() {
        String imagePath = "resources/png/Editor Elements.png";
        File imageFile = new File(imagePath);
        
        Image image = new Image(imageFile.toURI().toString());
        
        ImageView imageView = new ImageView(image);
        
        

        // Get the screen bounds
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();

        // Configure the ImageView to fit within the screen bounds while preserving the aspect ratio
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(screenBounds.getWidth());
        imageView.setFitHeight(screenBounds.getHeight());

        return imageView;
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
