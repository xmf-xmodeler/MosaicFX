package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
/**
 * 
 * @author Nicolas Engel
 *
 */
public class EditorElements {

    private Stage stage;

    public EditorElements() {
        stage = new Stage();
        stage.setTitle("Exemplary Illustration");

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
        String imagePath = "/png/Editor Elements.png";
        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream == null) {
            throw new IllegalArgumentException("Image resource not found: " + imagePath);
        }

        Image image = new Image(imageStream);

        if (image.isError()) {
            throw new IllegalArgumentException("Image could not be loaded: " + imagePath);
        }

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
