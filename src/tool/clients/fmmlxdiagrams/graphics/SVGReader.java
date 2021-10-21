package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
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
		//long start = System.currentTimeMillis(); 
		//System.err.println(System.currentTimeMillis());
		
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		Document doc = f.createDocument(file.toURI().toString());
		
		UserAgent          userAgent;
		DocumentLoader     loader;
		BridgeContext      ctx;
		GVTBuilder         builder;
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

	public static Vector<NodeElement> readChildren(SVGOMElement parentNode, SVGOMSVGElement rootNode) {

		Vector<NodeElement> vec = new Vector<>();

		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {

			Node n = parentNode.getChildNodes().item(i);

			if ("#text".equals(n.getNodeName()) ||  "defs".equals(n.getNodeName()) || "title".equals(n.getNodeName())) {
				// ignore !!!
			} else if ("g".equals(n.getNodeName())) {
				NodeGroup g = new NodeGroup((SVGOMGElement) n, rootNode);
				vec.add(g);
			} else if("path".equals(n.getNodeName())) {
				NodePath nP = new NodePath((SVGOMPathElement) n, rootNode);
				vec.add(nP);
			} else if ("circle".contentEquals(n.getNodeName())) {
				NodeEllipse nE = NodeEllipse.circle((SVGOMCircleElement) n, rootNode);
				vec.add(nE);
			} else if ("ellipse".contentEquals(n.getNodeName())) {
				NodeEllipse nE = NodeEllipse.ellipse((SVGOMEllipseElement) n, rootNode);
				vec.add(nE);
			} else if ("polygon".contentEquals(n.getNodeName())) {
				System.err.println("read SVG: " + n.getNodeName() + " of " + n.getClass().getSimpleName());
				NodePath nP = NodePath.polygon((SVGOMElement) n, rootNode);
				vec.add(nP);
			} else if("line".contentEquals(n.getNodeName())) {
				NodePath nP = NodePath.line((SVGOMLineElement) n, rootNode);
				vec.add(nP);			
			} else if ("rect".contentEquals(n.getNodeName())) {
				NodeRectangle nR = NodeRectangle.rectangle((SVGOMRectElement) n, rootNode);
				vec.add(nR);
			} else if("text".contentEquals(n.getNodeName())) {
				NodeText nT = new NodeText((SVGOMTextElement) n, rootNode);
				vec.add(nT);

			} else {
				System.out.println("Child ("+n.getNodeName()+") not recognized: " + parentNode + ":" + n + " of " + n.getClass().getSimpleName());
			}
			/// "line" --> org.apache.batik.anim.dom.SVGOMLineElement
		}

		return vec;

	}

	public static Affine readTransform(Node n) {
		Node transformNode = n.getAttributes().getNamedItem("transform");
		return transformNode==null?new Affine():TransformReader.getTransform(transformNode.getNodeValue());
	}
	
	public static double parseLength(String strokeWidth, Double percentBase){
		try{
			return Double.parseDouble(strokeWidth);
		} catch (NumberFormatException e) {
			if(strokeWidth.endsWith("%") && strokeWidth.length() > 1) {
				return Double.parseDouble(strokeWidth.substring(0,strokeWidth.length()-1)) / 100. * percentBase;
			}
			if(strokeWidth.length() < 2) throw new IllegalArgumentException("Cannot read length: " + strokeWidth);
			String subString = strokeWidth.substring(0,strokeWidth.length()-2);
			String unit = strokeWidth.substring(strokeWidth.length()-2);
			try{
				Double value = Double.parseDouble(subString);
				if("px".equals(unit)) return value;
				throw new IllegalArgumentException("Unknown unit: " + unit);
			} catch (NumberFormatException e2) {
				throw new IllegalArgumentException("Cannot read length: " + strokeWidth);
			}
		}
	}

}
