package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javafx.scene.transform.Affine;

public class SVGReader {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		readSVG("resources/abstract-syntax-repository/Orga/Comment_txt.svg", new Affine());
	}

	public static NodeGroup readSVG(String fileName, Affine affine) throws ParserConfigurationException, SAXException, IOException {
		return readSVG(new File(fileName), affine);
	}

	private static NodeGroup readSVG(File file, Affine affine) throws ParserConfigurationException, SAXException, IOException {
		long start = System.currentTimeMillis(); 
		//System.err.println(System.currentTimeMillis());
		Document doc = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(false);
        dbFactory.setValidating(false);
        dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        dbFactory.setFeature("http://xml.org/sax/features/validation", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		//System.err.println(System.currentTimeMillis() - start);
		doc = dBuilder.parse(file);
		//System.err.println(System.currentTimeMillis() - start);
		Node svgNode = (Node) doc.getDocumentElement();
		
		if (!"svg".equals(svgNode.getNodeName()))
			throw new IllegalArgumentException();
		NodeGroup g = new NodeGroup(affine);
		Vector<NodeElement> children = readChildren(svgNode);
		g.addAllNodeElements(children);
		//System.err.println(System.currentTimeMillis() - start);
		return g;		
	}

	public static Vector<NodeElement> readChildren(Node parentNode) {

		Vector<NodeElement> vec = new Vector<>();

		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {

			Node n = parentNode.getChildNodes().item(i);

			if ("#text".equals(n.getNodeName())) {
				// ignore !!!
			} else if ("g".equals(n.getNodeName())) {
				NodeGroup g = new NodeGroup(n);
				vec.add(g);
			} else if("path".equals(n.getNodeName())) {
				NodePath nP = new NodePath(n);
				vec.add(nP);
			} else if ("circle".contentEquals(n.getNodeName())) {
				NodeEllipse nE = NodeEllipse.circle(n);
				vec.add(nE);
			} else if ("ellipse".contentEquals(n.getNodeName())) {
				NodeEllipse nE = NodeEllipse.ellipse(n);
				vec.add(nE);
			} else if ("polygon".contentEquals(n.getNodeName())) {
				NodePath nP = NodePath.polygon(n);
				vec.add(nP);
			} else if ("rect".contentEquals(n.getNodeName())) {
				NodeRectangle nR = NodeRectangle.rectangle(n);
				vec.add(nR);
			} else if("text".contentEquals(n.getNodeName())) {
				NodeText nT = new NodeText(n);
				vec.add(nT);
			} else {
				System.out.println("Child not recognized: " + parentNode + ":" + n);

			}

		}

		return vec;

	}

	public static Affine readTransform(Node n) {
		return new Affine();
	}

}
