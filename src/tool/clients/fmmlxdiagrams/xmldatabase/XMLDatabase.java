package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.util.Prop;
import org.w3c.dom.Document;

import javafx.application.Platform;
import tool.clients.diagrams.DiagramClient;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLParser;
import tool.helper.persistence.XMLUtil;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;
import xos.XmfIOException;

public class XMLDatabase {
	private String document;
	private FmmlxDiagram diagram;
	private ClientSession session;
	private XMLCreator creator;
	private String hostname;
	private int port;
	private String user;
	private String password;

	/**
	 * Uses the ReturnCall to get an XML representation of the model. The XML
	 * representation is then written to the database.
	 * 
	 * @param diagram
	 */
	public XMLDatabase() {
		this.hostname = PropertyManager.getProperty("hostname");
		this.port = Integer.valueOf(PropertyManager.getProperty("port"));
		this.user = PropertyManager.getProperty("user");
		this.password = PropertyManager.getProperty("password");
		this.diagram = diagram;
		this.creator = new XMLCreator();
	}

	/**
	 * Connects to the XML database and stores the XML document.
	 *"localhost", 1984, "testuser", "testuser"
	 * @throws Exception If an error occurs during the database connection or
	 *                   document storage.
	 */
	
	public void writeToDB(FmmlxDiagram diagram) throws IOException {

		ReturnCall<Document> onDocumentCreated = (doc) -> {

			String xmlDoc = (XMLUtil.getStringFromDocument(doc));
			document = xmlDoc;
			try (ClientSession session = new ClientSession(PropertyManager.getProperty("hostname"), 
					Integer.valueOf(PropertyManager.getProperty("port")), 
					PropertyManager.getProperty("user"), 
					PropertyManager.getProperty("password"))) {
				
				try
				{
					session.execute("OPEN database");
				}
				catch (Exception e) {
					e.printStackTrace();
					System.err.print("This database does not exist");
					return;
				}
				
				String diagramName = diagram.getPackagePath().substring(6);
				// Print information about the session
				System.err.println(session.info());
				// Convert the document string to an input stream
				InputStream stream = new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
				// Add the XML document to the database with the name 'test'
				
				session.add(diagramName, stream);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.err.print("XML hinzugefügt \n");
		};
		this.creator.getXmlRepresentation(diagram.getPackagePath(), onDocumentCreated);
	}

	
	public void getDiagramsFromDB() {
		DiagramClient client = new DiagramClient();
		 List<File> files = getAllDocuments();
		    for (File file : files) {
		        loadXMFFile(file);
//		        System.err.println(file);
		    }
	}
		
    private List<File> getAllDocuments() {
        List<File> files = new ArrayList<>();
        try (ClientSession session = new ClientSession(this.hostname,this.port,this.user,this.password)){
            // Führe eine XQuery-Abfrage aus, um alle Dokumente aus der Datenbank abzurufen
            String query = "for $doc in collection() return base-uri($doc)";
            String query1 = "db:get";
            
            session.execute("OPEN database");
            
            ClientQuery result = session.query(query);

            // Verarbeite die Ergebnisse der Abfrage
            while (result.more()) {
                String path = result.next();
                ClientQuery xmlString = session.query("doc('" + path + "')");
                File file = createFileFromString(xmlString.execute());
                files.add(file);
            }
            result.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    
    public void loadXMFFile(File file)
    {
    	System.err.println(file);
    	Platform.runLater(()->{
    	XMLParser parser = new XMLParser(file);
    	parser.parseXMLDocument();
    	});
    }
    
    
    private File createFileFromString(String xmlString) {
        try {
            // Erstellen einer temporären Datei
            File tempFile = File.createTempFile("tempfile", ".xml");
            // Löschen der Datei beim Beenden des Programms
            tempFile.deleteOnExit();
            // Schreiben des XML-Strings in die temporäre Datei
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(xmlString);
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
