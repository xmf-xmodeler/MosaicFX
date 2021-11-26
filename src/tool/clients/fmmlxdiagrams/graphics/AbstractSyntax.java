package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
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
			if ("#text".equals(n.getNodeName()) ||  "defs".equals(n.getNodeName()) || "title".equals(n.getNodeName())) {
				// ignore !!!
			} else if("SVG".equals(n.getNodeName())) {
				vec.add(readSVG(file.getParentFile(), (Element) n));
			} else if("Label".equals(n.getNodeName())){
				vec.add(readLabel((Element) n));
			} else if("Group".equals(n.getNodeName())){
				vec.add(readGroup((Element) n, file.getParentFile()));
			} else {
//				System.err.println("Child not recognized: " + root + ":" + n);
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

	private static NodeLabel readLabel(Element e) {
		Affine transform = readTransform(e);
		Pos alignment = Pos.BASELINE_LEFT;
		if(e.hasAttribute("align")) {
			String s = e.getAttribute("align");
			if("LEFT".equals(s)) alignment = Pos.BASELINE_LEFT;
			if("CENTER".equals(s)) alignment = Pos.BASELINE_CENTER;
			if("RIGHT".equals(s)) alignment = Pos.BASELINE_RIGHT;
		}
		NodeLabel label = new NodeLabel(
				alignment, 
				transform.getTx(), transform.getTy(), 
				Color.BLACK, Color.YELLOWGREEN, 
				null, ()->{}, 
				"Situs vilat einis esa bernit!", 
				false, -1);
		if(e.hasAttribute("id")) label.id = e.getAttribute("id");
		return label;
	}
	
	private static NodeGroup readGroup(Element parent, File dir) throws ParserConfigurationException, SAXException, IOException {
		Affine transform = readTransform(parent);
		NodeGroup ng = new NodeGroup(transform);
		
		NodeList nl = parent.getChildNodes();
		Vector<NodeElement> vec = new Vector<>();	
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if ("#text".equals(n.getNodeName()) ||  "defs".equals(n.getNodeName()) || "title".equals(n.getNodeName())) {
				// ignore !!!
			} else if("SVG".equals(n.getNodeName())) {
				vec.add(readSVG(dir, (Element) n));
			} else if("Label".equals(n.getNodeName())){
				vec.add(readLabel((Element) n));
			} else if("Group".equals(n.getNodeName())){
				vec.add(readGroup((Element) n, dir));
			}else {
				System.err.println("Child not recognized: " + parent + ":" + n);
			}
		}
		
		ng.addAllNodeElements(vec);
		if(parent.hasAttribute("id")) ng.id = parent.getAttribute("id");
		return ng;		
	}

	private static SVGGroup readSVG(File dir, Element e)
			throws ParserConfigurationException, SAXException, IOException {

		Affine transform = readTransform(e);
		SVGGroup svg = SVGReader.readSVG(new File(dir, e.getAttribute("path")), transform);
		if(e.hasAttribute("id")) svg.id = e.getAttribute("id");
		return svg;
	}
	
	private static Affine readTransform(Element e) {
		double xx = e.hasAttribute("xx")?Double.parseDouble(e.getAttribute("xx")):1;
		double xy = e.hasAttribute("xy")?Double.parseDouble(e.getAttribute("xy")):0;
		double yx = e.hasAttribute("yx")?Double.parseDouble(e.getAttribute("yx")):0;
		double yy = e.hasAttribute("yy")?Double.parseDouble(e.getAttribute("yy")):1;
		double tx = e.hasAttribute("tx")?Double.parseDouble(e.getAttribute("tx")):0;
		double ty = e.hasAttribute("ty")?Double.parseDouble(e.getAttribute("ty")):0;
		return new Affine(xx,xy,tx,yx,yy,ty);
	}

	@Override
	public String toString() {
		return "G"+ (id==null?"":("("+id+")"));
	}	

	
}
