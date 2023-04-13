package tool.helper.fXAuxilary;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JavaFxImageViewAuxilary {
	
	public static ImageView createImageFromRelativePath(String relativePath) {
		File file = new File(relativePath);
		String localUrl = null;
		try {
			localUrl = file.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image image = new Image(localUrl);
		ImageView view = new ImageView(image);		
		return view;
	}
	
	public static ImageView createImageFromRelativePath(String relativePath, Double hight, Double width) {
		ImageView view = createImageFromRelativePath(relativePath);
		if (hight != null && width != null) {
			setFitSize(view, hight, width);
		}
		return view;
	}
	
	
	
	public static void setFitSize(ImageView view, double hight, double width) {
		view.setFitHeight(hight);
		view.setFitWidth(width);
	}
}
