package tool.helper;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class IconGenerator {
    public static ImageView getImageView(String iconName) {
        return new ImageView(getImage(iconName));
    }

    public static Image getImage(String iconName) {
        return new Image(getIconUrl(iconName));
    }

    public static String getIconUrl(String iconName) {
        return new File("resources/gif/"+iconName+".gif").toURI().toString();
    }
}