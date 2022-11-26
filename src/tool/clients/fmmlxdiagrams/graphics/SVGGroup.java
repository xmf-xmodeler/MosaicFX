package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;

public class SVGGroup extends NodeGroup {
	
	public File file;
	
	public SVGGroup(File file,Affine affine) {
		super(affine);
		this.file = file;
	}
	
	public String toString() {
		return "SVG(" + file.toString() + ")";
	}

	public Node save(Document document) {
		Element myElement = document.createElement("SVG");
		myElement.setAttribute("path", new File(ConcreteSyntaxWizard.RESOURCES_CONCRETE_SYNTAX_REPOSITORY).toURI().relativize(file.toURI()).getPath());
		myElement.setAttribute("xx", myTransform.getMxx()+"");
		myElement.setAttribute("yy", myTransform.getMyy()+"");
		myElement.setAttribute("xy", myTransform.getMxy()+"");
		myElement.setAttribute("yx", myTransform.getMyx()+"");
		myElement.setAttribute("tx", myTransform.getTx()+"");
		myElement.setAttribute("ty", myTransform.getTy()+"");
		if(!("".equals(id) || id == null)) {
			myElement.setAttribute("id", id );
		}
				
		return myElement;
	};
}
