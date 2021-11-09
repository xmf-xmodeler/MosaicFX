package tool.clients.fmmlxdiagrams.graphics;

import java.io.File;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.scene.transform.Affine;

public class ConcreteSyntax extends AbstractSyntax{
	
	public Vector<Modification> modifications;
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
		System.err.println(nl);
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if("Modification".equals(n.getNodeName())) {
				Element e = (Element) n;
				Modification m=null;
				object.modifications.add(m);
			} else {
				//System.err.println("Child not recognized: " + root + ":" + n);
			}
		}
	return object;	
	}
	
	
	@Override
	public String toString() {
		return "Concrete Syntax for " + classPath + "@" + level;
	}	
}
