package tool.xmodeler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class PropertyManager {

	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;
	Document backup;
	File xmlFile;
	String filePath;

	final String type_int = "int";
	final String type_bool = "Boolean";
	final String type_String = "String";
	final String type_double = "double";

	Stage stage;
	GridPane propGrid;

	/*
	 * 
	 */
	public PropertyManager(String filePath) {

		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			this.filePath = filePath;
			xmlFile = new File(filePath);
			if (!xmlFile.exists()) {
				doc = dBuilder.newDocument();
				Element rootElement = doc.createElement("properties");
				doc.appendChild(rootElement);
				writeXMLFile();

				// setting standard values
				setIntProperty("TOOL_X", 100);
				setIntProperty("TOOL_Y", 100);
				setIntProperty("TOOL_WIDTH", 1200);
				setIntProperty("TOOL_HEIGHT", 900);
			} else {
				doc = dBuilder.parse(xmlFile);
				backup = dBuilder.parse(xmlFile);
			}

		} catch (Exception e) {
			System.err.println("Loading XML File failed");
			System.err.println(e.getMessage());
		}
	}

	/*
	 * returns the value if property with given key exists returns defaultValue
	 * otherwise
	 */
	private String getProperty(String key, String defaultValue, String type) {

		if (key.isEmpty()) {
			System.err.println("Input must not be empty");
			return defaultValue;
		}
		try {
			// File xmlFile = new File(filePath);
			if (xmlFile.exists()) {
				Node n = contains(key, type);
				if (n != null) {
					return n.getAttributes().getNamedItem("value").getNodeValue();
				}
			}
		} catch (Exception e) {
			System.err.println("Error at PropertyManager.getProperty(): " + e.getMessage());
		}
		return defaultValue;
	}

	public int getIntProperty(String key, int defaultValue) {
		String result = getProperty(key, Integer.toString(defaultValue), type_int);
		try {
			return Integer.parseInt(result);
		} catch (NumberFormatException e) {
			System.out.println("Error at PropertyManager.getIntProperty" + e.getMessage());
			return defaultValue;
		}

	}

	public double getDoubleProperty(String key, double defaultValue) {
		String result = getProperty(key, Double.toString(defaultValue), type_double);
		try {
			return Double.parseDouble(result);
		} catch (NumberFormatException e) {
			System.out.println("Error at PropertyManager.getDoubleProperty" + e.getMessage());
			return defaultValue;
		}

	}

	public String getStringProperty(String key, String defaultValue) {
		return getProperty(key, defaultValue, type_String);
	}

	public Boolean getBooleanProperty(String key, Boolean defaultValue) {
		String result = getProperty(key, Boolean.toString(defaultValue), type_bool);
		if (result.equalsIgnoreCase("true") || result.equalsIgnoreCase("false")) {
			return Boolean.valueOf(result);
		} else
			return defaultValue;
	}

	/*
	 * creates property with given parameters if key does not exist changes value
	 * and type of existing key otherwise
	 */

	private void setProperty(String name, String value, String type) {
		if (name.isEmpty()) {
			System.err.println("Input must not be empty");
			return;
		}
		try {
			// File xmlFile = new File(filePath);
			if (xmlFile.exists()) {
				// Document doc = dBuilder.parse(xmlFile);
				Element root = doc.getDocumentElement();
				// checks if Element exists already
				Node n = contains(name);

				if (n != null) {
					Attr attr = doc.createAttribute("value");
					attr.setValue(value);
					n.getAttributes().setNamedItem(attr);
				} else {
					// create new Element
					// setting key
					Element e = doc.createElement("property");
					Attr attr = doc.createAttribute("key");
					attr.setValue(name);
					e.setAttributeNode(attr);
					// setting value
					attr = doc.createAttribute("value");
					attr.setValue(value);
					e.setAttributeNode(attr);
					// setting type
					attr = doc.createAttribute("type");
					attr.setValue(type);
					e.setAttributeNode(attr);
					// append element
					root.appendChild(e);

				}
					writeXMLFile();
				if (stage != null)
				if (stage.isShowing()) {
					createPropGrid();
				}
			}

		} catch (Exception e) {

		}
	}

	public void setIntProperty(String key, int value) {
		setProperty(key, Integer.toString(value), type_int);
	}

	public void setDoubleProperty(String key, double value) {
		setProperty(key, Double.toString(value), type_double);
	}

	public void setStringProperty(String key, String value) {
		setProperty(key, value, type_String);
	}

	public void setBooleanProperty(String key, Boolean value) {
		setProperty(key, Boolean.toString(value), type_bool);
	}

	/*
	 * Removes Property with given name returns true if successful returns false
	 * otherwise
	 */
	public Boolean deleteProperty(String key) {
		if (key.isEmpty()) {
			System.err.println("PropertyManager.deleteProperty(): Input must not be empty");
			return false;
		}
		try {
			// File xmlFile = new File(filePath);
			// Document doc = dBuilder.parse(xmlFile);
			Node n = contains(key);
			if (n == null) {
				System.err.println("PropertyManager.deleteProperty(): \"" + key + "\" not found");
				return false;
			} else {
				NodeList nL = doc.getDocumentElement().getElementsByTagName("property");
				for (int i = 0; i < nL.getLength(); i++) {
					Node e = nL.item(i);
					String eKey = e.getAttributes().getNamedItem("key").getNodeValue();
					if (key.equals(eKey)) {
						(doc.getDocumentElement()).removeChild(e);
					}
					writeXMLFile();
				}

				return true;
			}
		} catch (Exception e) {
			System.err.println("Error at PropertyManager.deleteProperty(): " + e.getMessage());
		}
		return false;
	}

	/*
	 * write the content into xml file
	 */
	public void writeXMLFile() {
		try {
			// File xmlFile = new File(filePath);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			if (stage != null)
			if (stage.isShowing()) {
				createPropGrid();
			}
		} catch (TransformerException e) {
			System.err.println("Error at PropertyManager.writeXMLFile(): " + e.getMessage());
		}
	}

	// returns XML-file to a String

	public String printXMLFile() {
		String result = "";
		try {
			// File xmlFile = new File(filePath);
			// Document doc = dBuilder.parse(xmlFile);
			result = result.concat("Root Element: " + doc.getDocumentElement().getTagName() + "\n");
			NodeList nL = doc.getElementsByTagName("property");
			for (int i = 0; i < nL.getLength(); ++i) {
				result = result.concat("Property: \"" + nL.item(i).getAttributes().getNamedItem("key").getNodeValue()
						+ "\" | Value: \"" + nL.item(i).getAttributes().getNamedItem("value").getNodeValue() + "\"\n");
			}
		} catch (Exception e) {
			System.err.println("Error in PropertyManager.printXMLFile()");
			System.err.println(e.getMessage());
		}
		return result;

	}

	/*
	 * searches for an element with given key returns Node if found returns null
	 * otherwise
	 */
	private Node contains(String key) {
		try {
			// File xmlFile = new File(filePath);
			// Document doc = dBuilder.parse(xmlFile);
			NodeList nL = doc.getElementsByTagName("property");
			for (int i = 0; i < nL.getLength(); i++) {
				Node e = nL.item(i);
				String eKey = e.getAttributes().getNamedItem("key").getNodeValue();
				if (eKey != null)
					if (eKey.equals(key)) {
						return e;
					}
			}
		} catch (Exception e) {
			System.err.println("Error at PropertyManager.contains()");
			System.err.println(e.getMessage());
		}
		return null;
	}

	private Node contains(String key, String type) {
		try {
			// File xmlFile = new File(filePath);
			// Document doc = dBuilder.parse(xmlFile);
			NodeList nL = doc.getElementsByTagName("property");
			for (int i = 0; i < nL.getLength(); i++) {
				Node e = nL.item(i);
				String eKey = e.getAttributes().getNamedItem("key").getNodeValue();
				String eType = e.getAttributes().getNamedItem("type").getNodeValue();
				if (eKey != null && eType != null)
					if (eKey.equals(key) && eType.equals(type)) {
						return e;
					}
			}
		} catch (Exception e) {
			System.err.println("Error at PropertyManager.contains()");
			System.err.println(e.getMessage());
		}
		return null;
	}

	public void getInterface() {
		try {
			if (stage != null) {
				stage.close();
			}
			
			propGrid = new GridPane();
			createPropGrid();
			propGrid.setPadding(new Insets(10));
			propGrid.setHgap(10);
			propGrid.setVgap(10);

			Label lblCaption = new Label("Properties:");

			Button btnOk = new Button("OK");
			btnOk.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					writeXMLFile();
					stage.close();
				}
			});

			Button btnApply = new Button("Apply");
			btnApply.setDisable(true);
			btnApply.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub

				}
			});

			Button btnCancel = new Button("Cancel");
			btnCancel.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					stage.close();
				}
			});

			HBox buttons = new HBox(btnOk, btnApply, btnCancel);
			buttons.setSpacing(10);
			buttons.setAlignment(Pos.CENTER_RIGHT);

			VBox vb = new VBox(lblCaption, new ScrollPane(propGrid), buttons);
			vb.setPadding(new Insets(10));
			vb.setSpacing(10);

			Scene scene = new Scene(vb);
			stage = new Stage();
			stage.setTitle("Property Manager");

			stage.setScene(scene);
			stage.show();

		} catch (Exception e) {
			System.err.println("Exception: getInterface(): " +e.getMessage());
		}

	}

	private void createPropGrid() {
		propGrid.getChildren().clear();
		try {
			// File xmlFile = new File(filePath);
			// Document doc = dBuilder.parse(xmlFile);
			NodeList nL = doc.getElementsByTagName("property");
			for (int i = 0; i < nL.getLength(); i++) {
				NamedNodeMap attributes = nL.item(i).getAttributes();
				Label key = new Label(attributes.getNamedItem("key").getTextContent());
				TextField value = new TextField(attributes.getNamedItem("value").getTextContent());
				Label type = new Label(attributes.getNamedItem("type").getTextContent());
				propGrid.addRow(i, key, value, type);

			}
		} catch (Exception e) {
			// TODO: handle exception

		}
	}

}
