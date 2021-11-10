package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.transform.Affine;

public class AbstractSyntax extends NodeGroup{
	
	protected File file;

	public void save() {
		throw new RuntimeException("Not yet implemented!");
	}
	
	public static AbstractSyntax load(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        factory.setFeature("http://xml.org/sax/features/namespaces", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
		Element root = document.getDocumentElement();
		NodeList nl = root.getChildNodes();
		Vector<NodeElement> vec = new Vector<>();	
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if("SVG".equals(n.getNodeName())) {
				Element e = (Element) n;
				double xx = e.hasAttribute("xx")?Double.parseDouble(e.getAttribute("xx")):1;
				double xy = e.hasAttribute("xy")?Double.parseDouble(e.getAttribute("xy")):0;
				double yx = e.hasAttribute("yx")?Double.parseDouble(e.getAttribute("yx")):0;
				double yy = e.hasAttribute("yy")?Double.parseDouble(e.getAttribute("yy")):1;
				double tx = e.hasAttribute("tx")?Double.parseDouble(e.getAttribute("tx")):0;
				double ty = e.hasAttribute("ty")?Double.parseDouble(e.getAttribute("ty")):0;
				Affine transform = new Affine(xx,xy,tx,yx,yy,ty);
				SVGGroup svg = SVGReader.readSVG(new File(file.getParentFile(),e.getAttribute("path")), transform);
				vec.add(svg);
			} else {
				//System.err.println("Child not recognized: " + root + ":" + n);
			}
		}
		
		AbstractSyntax object; 
		
		if("ConcreteSyntax".equals(root.getNodeName())) {
			object = ConcreteSyntax.load2(file,root);
		} else {
			object = new AbstractSyntax();
		}
		
		object.nodeElements = vec;
		object.file=file;
		return object;
	}
	
	@Override
	public String toString() {
		return "G"+ (id==null?"":("("+id+")"));
	}	

	
}
