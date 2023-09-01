package tool.helper.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtil {

	public static Document createDocument(String elementName) {
		DocumentBuilder dbdr = null;
		try {
			dbdr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = dbdr.newDocument();
		Element ele = doc.createElement(elementName);
		doc.appendChild(ele);
		return doc;
	}

	public static Element createChildElement(Element parentElement, String childName) {
		Element child = null;
		if (parentElement != null && (!"".equals(childName))) {
			child = parentElement.getOwnerDocument().createElement(childName);
			parentElement.appendChild(child);
		}
		return child;
	}

	public static Element getChildElement(Element element, String tagName) {

		return getChildElement(element, tagName, true);

	}

	public static Element getChildElement(Element element, String tagName, boolean create) {
		Node node = null;
		NodeList nodeList = element.getChildNodes();
		Element childElm = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getNodeName())) {
				childElm = (Element) node;
				break;
			}
		}

		if ((childElm == null) && (create)) {
			childElm = createChildElement(element, tagName);
		}

		return childElm;
	}

	public static List<Element> getChildElements(Element element, String tagName) {
		Node node = null;
		var elementList = new ArrayList<Element>();
		NodeList nodeList = element.getChildNodes();
		Element childElm = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && tagName.equals(node.getNodeName())) {
				childElm = (Element) node;
				elementList.add(childElm);
			}
		}

		return elementList;
	}

	public static Document getDocumentFromFile(File inputFile) {
		Document retVal = null;
		FileReader inFileReader = null;
		try {
			inFileReader = new FileReader(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			InputSource iSource = new InputSource(inFileReader);
			try {
				retVal = getDocument(iSource);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				inFileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	public static Document getDocument(InputSource inSource)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();

		DocumentBuilder dbdr = fac.newDocumentBuilder();

		return dbdr.parse(inSource);
	}

	public static String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void saveDocumentToFile(Document doc, File saveFile) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		FileWriter writer = null;
		try {
			writer = new FileWriter(saveFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StreamResult result = new StreamResult(writer);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}