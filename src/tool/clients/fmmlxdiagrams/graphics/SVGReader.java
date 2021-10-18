package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javafx.scene.transform.Affine;

public class SVGReader {

	public static void main(String[] args) throws Exception {
		
	}

	public static NodeGroup readSVG(String fileName, Affine affine) throws ParserConfigurationException, SAXException, IOException {
		return readSVG(new File(fileName), affine);
	}

	private static NodeGroup readSVG(File file, Affine affine) throws ParserConfigurationException, SAXException, IOException {
		long start = System.currentTimeMillis(); 
		//System.err.println(System.currentTimeMillis());
//		Document doc = null;
//
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		dbFactory.setNamespaceAware(false);
//        dbFactory.setValidating(false);
//        dbFactory.setFeature("http://xml.org/sax/features/namespaces", false);
//        dbFactory.setFeature("http://xml.org/sax/features/validation", false);
//        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
//        dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		//System.err.println(System.currentTimeMillis() - start);
//		doc = dBuilder.parse(file);
//		//System.err.println(System.currentTimeMillis() - start);
//		Node svgNode = (Node) doc.getDocumentElement();
//		
//		if (!"svg".equals(svgNode.getNodeName()))
//			throw new IllegalArgumentException();
		
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//		URI uri = new URI("resources/abstract-syntax-repository/Orga/Sachbearbeiter.svg"); // the URI of your SVG document
//		URI uri = new URI("file:///a://svgs/fov.svg"); // the URI of your SVG document
		Document doc = f.createDocument(file.toURI().toString());
		
		UserAgent      userAgent;
		DocumentLoader loader;
		BridgeContext  ctx;
		GVTBuilder     builder;
		RootGraphicsNode   rootGN;
		             
		userAgent = new UserAgentAdapter();
		loader    = new DocumentLoader(userAgent);
		ctx       = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		builder   = new GVTBuilder();
		rootGN    = (RootGraphicsNode) builder.build(ctx, doc);
		SVGOMSVGElement myRootSVGElement = (SVGOMSVGElement) doc.getDocumentElement();
		
		NodeGroup g = new NodeGroup(affine);
		Vector<NodeElement> children = readChildren(myRootSVGElement, myRootSVGElement);
		g.addAllNodeElements(children);
		//System.err.println(System.currentTimeMillis() - start);
		return g;		
	}

//	public static Vector<NodeElement> readChildren(Node parentNode) {
//
//		Vector<NodeElement> vec = new Vector<>();
//
//		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
//
//			Node n = parentNode.getChildNodes().item(i);
//
//			if ("#text".equals(n.getNodeName())) {
//				// ignore !!!
//			} else if ("g".equals(n.getNodeName())) {
//				NodeGroup g = new NodeGroup(n);
//				vec.add(g);
//			} else if("path".equals(n.getNodeName())) {
//				NodePath nP = new NodePath(n);
//				vec.add(nP);
//			} else if ("circle".contentEquals(n.getNodeName())) {
//				NodeEllipse nE = NodeEllipse.circle(n);
//				vec.add(nE);
//			} else if ("ellipse".contentEquals(n.getNodeName())) {
//				NodeEllipse nE = NodeEllipse.ellipse(n);
//				vec.add(nE);
//			} else if ("polygon".contentEquals(n.getNodeName())) {
//				NodePath nP = NodePath.polygon(n);
//				vec.add(nP);
//			} else if ("rect".contentEquals(n.getNodeName())) {
//				NodeRectangle nR = NodeRectangle.rectangle(n);
//				vec.add(nR);
//			} else if("text".contentEquals(n.getNodeName())) {
//				NodeText nT = new NodeText(n);
//				vec.add(nT);
//			} else {
//				System.out.println("Child not recognized: " + parentNode + ":" + n);
//
//			}
//
//		}
//
//		return vec;
//
//	}
	
	public static Vector<NodeElement> readChildren(SVGOMElement parentNode, SVGOMSVGElement rootNode) {

		Vector<NodeElement> vec = new Vector<>();

		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {

			Node n = parentNode.getChildNodes().item(i);

			if ("#text".equals(n.getNodeName())) {
				// ignore !!!
			} else if ("g".equals(n.getNodeName())) {
				System.err.println("Class: " + n.getClass());
				SVGOMGElement n2= (SVGOMGElement) n;
				NodeGroup g = new NodeGroup(n2, rootNode);
				vec.add(g);
			} else if("path".equals(n.getNodeName())) {
				NodePath nP = new NodePath((SVGOMElement)n, rootNode);
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
		Node transformNode = n.getAttributes().getNamedItem("transform");
		return transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
	}

}
