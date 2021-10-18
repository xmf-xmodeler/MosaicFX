package tool.clients.fmmlxdiagrams.graphics;

import java.awt.geom.AffineTransform;
import java.net.URI;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.TransformListParser;
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
		URI uri = new URI("resources/abstract-syntax-repository/Orga/Sachbearbeiter.svg"); // the URI of your SVG document
//		URI uri = new URI("file:///a://svgs/fov.svg"); // the URI of your SVG document
		Document doc = f.createDocument(uri.toString());
		
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
		
//		CanvasGraphicsNode canvasGN = (CanvasGraphicsNode)rootGN.getChildren().get(0);
		
//		for(Object childGN : canvasGN.getChildren()) {
//			CompositeGraphicsNode cGN = (CompositeGraphicsNode) childGN; 
////			System.err.println();
//			for(Object child2GN : cGN.getChildren()) {
////				System.err.println(child2GN);
//				if(child2GN instanceof ShapeNode) {
//					ShapeNode sn = (ShapeNode) child2GN;
//					java.awt.Shape shape = sn.getShape();
//					if(shape instanceof ExtendedGeneralPath) {
//						ExtendedGeneralPath egp = (ExtendedGeneralPath) shape;
//						System.err.println(egp.getPathIterator(new AffineTransform()).);
//					}
//					
//				}
//			}
//		}
//		
		SVGOMSVGElement myRootSVGElement = (SVGOMSVGElement) doc.getDocumentElement();
		 
		//I want all the "path" elements for example
//		NodeList nl = myRootSVGElement.getElementsByTagName("g");
		NodeList nl = myRootSVGElement.getChildNodes();
		 
		for(int i=0;i<nl.getLength();++i){
			System.err.println(nl.item(i).getClass());
			if(true || ! ((nl.item(i)) instanceof SVGElement)) {} else {
//			    Element elt = (Element)nl.item(i);
			    SVGElement S = (SVGElement) nl.item(i);
			    
			    //I am interested in the "fill" value of the current path element
//			    String fillvalue = myRootSVGElement.getComputedStyle(S, null).getPropertyValue("fill");
			    SVGMatrix matrix = myRootSVGElement.getTransformToElement(S);
			    System.err.println(S.getNodeName() + ": " + S.getAttribute("id") + " ");
			    
//			    System.err.println(nl + " -fill: " + fillvalue);
			    if(matrix != null) {
			    	try{
			    		String transformString = S.getAttribute("transform");
			    		System.err.println(transformString);
			            TransformListParser p = new TransformListParser();
			            AWTTransformProducer tp = new AWTTransformProducer();
			            p.setTransformListHandler(tp);
			            p.parse(transformString);
			            AffineTransform m1 = tp.getAffineTransform();
			            double[] m2 = new double[6];
			            m1.getMatrix(m2);
			            
					    System.err.println("matrix 1: [" + m2[0] + ", " + m2[2] + ", " + m2[4] + "]");
					    System.err.println("          [" + m2[1] + ", " + m2[3] + ", " + m2[5] + "]");
			    	} catch (Exception e) {
			    		System.err.println("transform1 not found: " + S.getId());
			    	}
			    	try{		    		
					    System.err.println("matrix 2: [" + matrix.getA() + ", " + matrix.getC() + ", " + matrix.getE() + "]");
					    System.err.println("          [" + matrix.getB() + ", " + matrix.getD() + ", " + matrix.getF() + "]");
			    	} catch (Exception e) {
			    		System.err.println("transform2 not found: " + S.getId());
			    	}
			    }
			    
			    //If I want to parse the "d" attribute
			    String toParse = S.getAttribute("d");
			    //This string can then be fed to a PathParser if you want to create the shapes yourself
			}
		}
	}
}
