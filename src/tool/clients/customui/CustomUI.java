package tool.clients.customui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public class CustomUI {
	// Java FX references
	private Stage stage;
	private Scene scene;

	// Object representing the "Custom User Interface" instance
	private String customUIobjectName;
	private AbstractPackageViewer diagram; // further context

	// Meta data about the UI
	private Map<String, String> eventToID; // Events registered for ID
	private Parent customGUI;
	
	public AbstractPackageViewer getDiagram() {
		return this.diagram;
	}
	
	public String getCustomUIObjectName() {
		return this.customUIobjectName;
	}
	
	public Parent getCustomUI() {
		return this.customGUI;
	}
	
	public CustomUI(AbstractPackageViewer diagram, FmmlxObject customUIobject) {
		// COntext of custom UI
		this.customUIobjectName = customUIobject.getName();
		this.diagram = diagram;

		// Setup JavaFX Stage
		this.stage = new Stage();
		stage.setMaximized(true);
		// stage.setFullScreen(true);

		// Obtain title from UI object
		FmmlxSlot title = customUIobject.getSlot("titleOfUI");
		stage.setTitle(title.getValue());

		// Obtail icon from UI object
		FmmlxSlot icon = customUIobject.getSlot("pathToIconOfWindow");
		Image imageIcon = new Image(icon.getValue());
		stage.getIcons().add(imageIcon);
		
		// Obtain FXML file from UI object
		FmmlxSlot filePath = customUIobject.getSlot("pathToFXML");
		File fxmlFile = new File(filePath.getValue());
		
		this.scene = new Scene(loadUI(fxmlFile)); // Loaded file represents the scene for the stage
		this.stage.setScene(scene);

		stage.show();
	}

	// Load Custom UI
	private Parent loadUI(File fxmlFile) {
		// Pick the file, which shall be read
		// FileChooser fileChooser = new FileChooser();
		// fileChooser.setTitle("Open FXML file als custom GUI for the selected
		// instance");
		// fileChooser.getExtensionFilters().addAll(
		// new ExtensionFilter("JavaFX as XML", "*.fxml"),
		// new ExtensionFilter("All Files", "*.*"));
		// File selectedFile = fileChooser.showOpenDialog(stage);

		Parent loadedFXML = new Pane();

		if (fxmlFile != null) {
			try {
				// First step: Read FXML file
				SAXReader reader = new SAXReader();
				Document document = reader.read(fxmlFile);
				String fxml = "";

				// Second step: Read events defined in XML file as the information is lost after
				// loading the file
				this.eventToID = eventToID(document);

				// Third step: Create a string from the FXML document
				StringWriter stringWriter = new StringWriter();
				XMLWriter writer = new XMLWriter(stringWriter);
				writer.write(document);
				writer.close();
				writer.flush();
				fxml = stringWriter.toString();

				// Fourth step: Load FXML UI
				FXMLLoader loader = new FXMLLoader();

				// Controller factory in case we want to use the controller from the FXML file
				loader.setControllerFactory(controller -> {
					return new CustomGUIController(loader, eventToID, this);
				});

				loadedFXML = loader.load(new ByteArrayInputStream(fxml.getBytes()));
				this.customGUI = loadedFXML;
				
				// Set default controller to prevent linking the controller in the file
				CustomGUIController controller = new CustomGUIController(loader, eventToID, this);
				loader.setController(controller);
				controller.initialize(); // has to be called manually if not called during load process

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return loadedFXML;
	}

	private Map<String, String> eventToID(Document fxmlFile) {
		Map<String, String> eventToID = new HashMap<>();

		try {
			// Get all Nodes with an Id
			List<org.dom4j.Node> elements = fxmlFile.selectNodes("//*[@fx:id]");
			for (org.dom4j.Node node : elements) {
				Element element = (Element) node;

				// Get all attributes of nodes with id
				Iterator<Attribute> currAttributes = element.attributeIterator();
				String id = "";
				while (currAttributes.hasNext()) {
					Attribute currAtt = currAttributes.next();
					String att = currAtt.getName();

					if (att.equals("id")) {
						id = currAtt.getValue();
					} else if (att.substring(0, 2).equals("on")) {
						// Only event attributes are starting with the prefix on
						// Build entry for every event in map
						if (!id.equals("")) {
							String name = currAtt.getValue();
							eventToID.put(id + "/" + currAtt.getName().toUpperCase().substring(2),
									currAtt.getValue().substring(1));
							currAttributes.remove(); // remove element from iterator
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return eventToID;
	}
}