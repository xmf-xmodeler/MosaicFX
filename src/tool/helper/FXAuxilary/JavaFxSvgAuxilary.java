package tool.helper.FXAuxilary;

import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class JavaFxSvgAuxilary {
	
	public static Region buildSvgShape(String vektorPath, double height, double width) {
		Region region = buildSvgShape(vektorPath);
		region.setPrefSize(height, width);
		return region;
	}
	
	public static Region buildSvgShape(String vektorPath) {
		SVGPath path = parseToPath(vektorPath);
		Region shape = parseToRegion(path);
		shape.setStyle("-fx-background-color: black;");
		shape.setPrefSize(20,20);
		return shape;
	}
	
	private static SVGPath parseToPath(String vektorPath) {
		SVGPath path = new SVGPath();
		path.setContent(vektorPath);
		return path;
	}
	
	private static Region parseToRegion(SVGPath svgPath) {
		Region svgShape = new Region();
		svgShape.setShape(svgPath);
		return svgShape;
	}
}
