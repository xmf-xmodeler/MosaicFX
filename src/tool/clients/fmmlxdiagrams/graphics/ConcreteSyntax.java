package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

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
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class ConcreteSyntax extends ConcreteSyntaxPattern{
	
	private Vector<Modification> modifications = new Vector<>();
	public Vector<Modification> getModifications() {return new Vector<>(modifications);}
	private Vector<ActionInfo> actions = new Vector<>();
	public Vector<ActionInfo> getActions() {return new Vector<>(actions);}
	public String classPath;
	public int level;
	
	public static ConcreteSyntax load2(File arg, Element root) {
		ConcreteSyntax syntaxGroup = new ConcreteSyntax();
		
		if (!root.hasAttribute("classPath")) {
			throw new RuntimeException("ClassPath not found!");
		}
		syntaxGroup.classPath = root.getAttribute("classPath");
		
		try {
			syntaxGroup.level = Integer.parseInt(root.getAttribute("level"));
		} catch (Exception e) {
			throw new RuntimeException("Level not found!");
		}
		
		NodeList nl = root.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if("Modification".equals(n.getNodeName())) {
				Element e = (Element) n;
				Modification m = new Modification(e);
				syntaxGroup.modifications.add(m);
			} else if("Action".equals(n.getNodeName()))  {
				Element actionElement = (Element) n;
				String id = actionElement.getAttribute("id");
				String localId = actionElement.getAttribute("localId");
				String actionType = actionElement.getAttribute("type");
				syntaxGroup.actions.add(new ActionInfo(id, localId, actionType));
			} else {
				//System.err.println("Child not recognized: " + root + ":" + n);
			}
		}
	return syntaxGroup;	
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
	        
	        saveChildren(document, nodeElements, modifications, root, file.getParentFile());
	               
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
	
	public static void saveChildren(Document document, Vector<NodeElement> nodeElements, Vector<Modification> modifications, Element parent, File dir) {
		 for (NodeElement element : nodeElements) {
	        	if (element instanceof SVGGroup) {
	        		parent.appendChild(((SVGGroup)element).save(document, dir));	        		
	        	} else if (element instanceof NodeLabel) {
	        		parent.appendChild(((NodeLabel)element).save(document));
	        	} else if (element instanceof NodeGroup){
	        		parent.appendChild(((NodeGroup)element).save(document, dir));
	        	}
	        }
		 for (Modification modification: modifications) {
			 parent.appendChild(modification.save(document));
		 }
	}
	
	@Override
	public String toString() {
		return "Concrete Syntax for " + classPath + "@" + level;
	}	
	
	
	public NodeGroup createInstance(final FmmlxObject object, FmmlxDiagram diagram) {
		NodeGroup instance = createInstance(object, modifications, actions, diagram);
		instance.myTransform = new Affine(1, 0, object.getX(), 0, 1, object.getY());
		return instance;
	}

	public void addModification(Modification mod) {
		modifications.add(mod);		
	}
	
	public void removeModification(Modification mod) {
		modifications.remove(mod);		
	}

	public void addAction(ActionInfo a) {
		actions.add(a);		
	}
	
	public void removeAction(ActionInfo a) {
		actions.remove(a);		
	}
}
