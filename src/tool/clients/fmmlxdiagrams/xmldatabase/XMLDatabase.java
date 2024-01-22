package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.basex.api.client.ClientSession;
import org.w3c.dom.Document;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLUtil;

public class XMLDatabase {
	private String document;
	private FmmlxDiagram diagram;

	/**
	 * Uses the ReturnCall to get an XML representation of the model. The XML
	 * representation is then written to the database.
	 * 
	 * @param diagram
	 */
	public XMLDatabase(FmmlxDiagram diagram) {
		this.diagram = diagram;
		XMLCreator creator = new XMLCreator();
		ReturnCall<Document> onDocumentCreated = (doc) -> {

			String xmlDoc = (XMLUtil.getStringFromDocument(doc));

			document = xmlDoc;
			System.err.print("test");
			try {
				connectToDatabase();
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
	 *
	 * @throws Exception If an error occurs during the database connection or
	 *                   document storage.
	 */
	private void connectToDatabase() throws Exception {
		try (ClientSession session = new ClientSession("localhost", 1984, "testuser", "testuser")) {
			// Create a new database
			session.execute("CREATE DB database");
			writeToDB(session, this.diagram);
			
		} catch (Exception e) {
			// Handle any exceptions that occur during database connection or document
			// storage
			e.printStackTrace();
		}
	}
	private void writeToDB(ClientSession session, FmmlxDiagram diagram) throws IOException
	{
		String diagramName = diagram.getPackagePath().substring(6);
		// Print information about the session
		System.err.println(session.info());
		// Convert the document string to an input stream
		InputStream stream = new ByteArrayInputStream(document.getBytes(StandardCharsets.UTF_8));
		// Add the XML document to the database with the name 'test'
		session.add(diagramName, stream);
		
		System.err.print("XML hinzugefügt \n");
	}
}
