package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.util.Prop;
import org.w3c.dom.Document;

import tool.clients.diagrams.DiagramClient;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLParser;
import tool.helper.persistence.XMLUtil;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;

public class XMLDatabase {
	private String document;
	private FmmlxDiagram diagram;
	private ClientSession session;

	/**
	 * Uses the ReturnCall to get an XML representation of the model. The XML
	 * representation is then written to the database.
	 * 
	 * @param diagram
	 */
	public XMLDatabase() {
		this.diagram = diagram;
		XMLCreator creator = new XMLCreator();
		ReturnCall<Document> onDocumentCreated = (doc) -> {

			String xmlDoc = (XMLUtil.getStringFromDocument(doc));

			document = xmlDoc;
			System.err.print("test");
			
			try (ClientSession session = new ClientSession(PropertyManager.getProperty("hostname"), 
					Integer.valueOf(PropertyManager.getProperty("port")), 
					PropertyManager.getProperty("user"), 
					PropertyManager.getProperty("password")))
			{
				this.session = session;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.print("\n Error \n");
				e.printStackTrace();
			}
			try {
				writeToDB(this.diagram);
				System.err.print("done");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.print("\n Error \n");
				e.printStackTrace();
			}
		};
		creator.getXmlRepresentation(diagram.getPackagePath(), onDocumentCreated);

	}

	/**
	 * Connects to the XML database and stores the XML document.
	 *"localhost", 1984, "testuser", "testuser"
	 * @throws Exception If an error occurs during the database connection or
	 *                   document storage.
	 */
	
	public void writeToDB(FmmlxDiagram diagram) throws IOException
	{
		this.session.execute("CREATE DB database");
		String diagramName = diagram.getPackagePath().substring(6);
		// Print information about the session
		System.err.println(session.info());
		// Convert the document string to an input stream
		InputStream stream = new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
		// Add the XML document to the database with the name 'test'
		session.add(diagramName, stream);
		
		System.err.print("XML hinzugefügt \n");
	}
	
	public void getDiagramsFromDB()
	{
		DiagramClient client = new DiagramClient();
		XMLParser parser = new XMLParser();
		List<File> files = getAllDocuments();
		for(File file : files)
		{
			client.writeXML(null);
			
		}
	}
	
	
    private List<File> getAllDocuments() {
        List<File> files = new ArrayList<>();
        try {
            // Führe eine XQuery-Abfrage aus, um alle Dokumente aus der Datenbank abzurufen
            String query = "for $doc in collection() return base-uri($doc)";
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
    
    
    private File createFileFromString(String xmlString) {
        // Erstellen Sie ein temporäres File-Objekt mit dem XML-Inhalt als ByteArrayInputStream
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        return new java.io.File(inputStream.toString());
    }
}
