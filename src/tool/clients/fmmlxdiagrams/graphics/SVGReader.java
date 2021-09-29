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

public class SVGReader {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		readSVG("C:\\circle.svg");
	}

	public static NodeGroup readSVG(String fileName) throws ParserConfigurationException, SAXException, IOException {
		return readSVG(new File(fileName));
	}

	private static NodeGroup readSVG(File file) throws ParserConfigurationException, SAXException, IOException {
		long start = System.currentTimeMillis(); 
		System.err.println(System.currentTimeMillis());
		Document doc = null;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		System.err.println(System.currentTimeMillis() - start);
		doc = dBuilder.parse(file);
		System.err.println(System.currentTimeMillis() - start);
		Node svgNode = (Node) doc.getDocumentElement();

		if (!"svg".equals(svgNode.getNodeName()))
			throw new IllegalArgumentException();

		Vector<NodeElement> children = readChildren(svgNode);

		NodeGroup g = new NodeGroup();

		g.addAllNodeElements(children);
		System.err.println(System.currentTimeMillis() - start);
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
			} else {
				System.out.println("Child not recognized: " + parentNode + ":" + n);

			}

		}

		return vec;

	}

}
