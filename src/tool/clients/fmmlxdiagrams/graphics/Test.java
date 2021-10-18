package tool.clients.fmmlxdiagrams.graphics;

import java.net.URI;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGMatrix;

public class Test {
	
	public static void main(String[] args) throws Exception {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
//		URI uri = new URI("file:///b://Betriebssystem1.svg"); // the URI of your SVG document
		URI uri = new URI("resources/abstract-syntax-repository/Orga/Comment.svg"); // the URI of your SVG document
		Document doc = f.createDocument(uri.toString());
		
		UserAgent      userAgent;
		DocumentLoader loader;
		BridgeContext  ctx;
		GVTBuilder     builder;
		GraphicsNode   rootGN;
		             
		userAgent = new UserAgentAdapter();
		loader    = new DocumentLoader(userAgent);
		ctx       = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		builder   = new GVTBuilder();
		rootGN    = builder.build(ctx, doc);
		
		SVGOMSVGElement myRootSVGElement = (SVGOMSVGElement) doc.getDocumentElement();
		 
		//I want all the "path" elements for example
		NodeList nl = myRootSVGElement.getElementsByTagName("path");
		 
		for(int i=0;i<nl.getLength();++i){
		    Element elt = (Element)nl.item(i);
		 
		    //I am interested in the "fill" value of the current path element
		    String fillvalue = myRootSVGElement.getComputedStyle(elt, null).getPropertyValue("fill");
		    SVGMatrix matrix = myRootSVGElement.getTransformToElement((SVGElement) elt);
		    SVGElement S = (SVGElement) elt;
		    
		    System.err.print(S.getNodeName() + ": " + S.getAttribute("id") + " ");
		    
		    System.err.println(nl + " -fill: " + fillvalue);
		    if(matrix != null) {
		    	try{
			    System.err.println("matrix: [" + matrix.getA() + ", " + matrix.getC() + ", " + matrix.getE() + "]");
			    System.err.println("        [" + matrix.getB() + ", " + matrix.getD() + ", " + matrix.getF() + "]");
		    	} catch (Exception e) {}
		    }
		    //If I want to parse the "d" attribute
		    String toParse = elt.getAttribute("d");
		    //This string can then be fed to a PathParser if you want to create the shapes yourself
		}
	}
}
