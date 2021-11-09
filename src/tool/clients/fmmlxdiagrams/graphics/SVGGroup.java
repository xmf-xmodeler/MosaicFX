package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;

import javafx.scene.transform.Affine;

public class SVGGroup extends NodeGroup {
	
	public File file;
	
	public SVGGroup(File file,Affine affine) {
		super(affine);
		this.file=file;
	}
	
	public String toString() {
		return "SVG(" + file.toString() + ")";
	};
}
