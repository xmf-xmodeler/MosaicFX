package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ConcreteSyntax extends AbstractSyntax{
	
	public Vector<Modification> modifications = new Vector<>();
	public String classPath;
	public int level;

	
	public static ConcreteSyntax load2(File arg, Element root) {
		ConcreteSyntax object = new ConcreteSyntax();
		
		if (!root.hasAttribute("classPath")) {
			throw new RuntimeException("ClassPath not found!");
		}
		object.classPath = root.getAttribute("classPath");
		
		try {
			object.level = Integer.parseInt(root.getAttribute("level"));
		} catch (Exception e) {
			throw new RuntimeException("Level not found!");
		}
		
		NodeList nl = root.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if("Modification".equals(n.getNodeName())) {
				Element e = (Element) n;
				Modification m = new Modification(e);
				object.modifications.add(m);
			} else {
				//System.err.println("Child not recognized: " + root + ":" + n);
			}
		}
	return object;	
	}
	
	@Override
	public void save() {
		
		try {
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        documentBuilderFactory.setValidating(true);
	        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.newDocument();
	        Element root = document.createElement("ConcreteSyntax");
	        document.appendChild(root);
	        root.setAttribute("classPath", classPath);
	        root.setAttribute("level",  "" + level);
	        
	        saveChildren(document, nodeElements,root);
	               
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        DOMSource domSource = new DOMSource(document);
	        StreamResult streamResult = new StreamResult(file);
	        transformer.transform(domSource, streamResult);
	        
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void saveChildren(Document document, Vector<NodeElement> nodeElements, Element parent) {
		 for (NodeElement element : nodeElements) {
	        	if (element instanceof SVGGroup) {
	        		parent.appendChild(((SVGGroup)element).save(document));
	        		
	        	} else if (element instanceof NodeLabel) {
	        		parent.appendChild(((NodeLabel)element).save(document));
	        	} else if (element instanceof NodeGroup){
	        		parent.appendChild(((NodeGroup)element).save(document));
	        	}
	        }
	}
	
	@Override
	public String toString() {
		return "Concrete Syntax for " + classPath + "@" + level;
	}	
	
	
	public NodeGroup createInstance(final FmmlxObject object) {
		NodeGroup instance = createInstance(object, modifications);
		instance.myTransform = new Affine(1, 0, object.getX(), 0, 1, object.getY());
		return instance;
	}

	
}
