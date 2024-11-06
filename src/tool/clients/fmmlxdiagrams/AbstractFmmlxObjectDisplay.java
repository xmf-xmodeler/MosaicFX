package tool.clients.fmmlxdiagrams;

import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.SVGGroup;
import tool.clients.fmmlxdiagrams.graphics.SVGReader;

public abstract class AbstractFmmlxObjectDisplay {
	
	protected final FmmlxDiagram diagram;
	protected final FmmlxObject object;

	public AbstractFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		this.object = object;
		this.diagram = diagram;
	}
	
	protected SVGGroup getCogWheelExplicitIcon() {
		try {
			return SVGReader.readSVG(new java.io.File("resources/svg/cogwheel.svg"), new Affine());
		} catch(Exception any) {
			System.err.println("Cannot read file for cogwheel.");
			any.printStackTrace();
			return null;
		}
	}
	
	protected SVGGroup getCogWheelImplicitIcon() {
		try {
			return SVGReader.readSVG(new java.io.File("resources/svg/cogwheel2.svg"), new Affine());
		} catch(Exception any) {
			System.err.println("Cannot read file for cogwheel.");
			any.printStackTrace();
			return null;
		}
	}
	
}
