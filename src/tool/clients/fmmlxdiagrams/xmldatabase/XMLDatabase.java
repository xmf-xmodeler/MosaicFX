package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.api.client.Session;
import org.basex.core.BaseXException;
import org.basex.util.Prop;
import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import tool.clients.diagrams.DiagramClient;
import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLParser;
import tool.helper.persistence.XMLUtil;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;
import xos.XmfIOException;

/**
 * 
 * @author Nicolas Engel
 *
 */
public class XMLDatabase {
	private String document;
	private FmmlxDiagram diagram;
	private XMLCreator creator;
	private XMLDatabaseQuerys querrys;
	protected String hostname;
	protected int port;
	protected String user;
	protected String password;
	protected String db_name;

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
		this.db_name = PropertyManager.getProperty("databaseName");
		this.querrys = new XMLDatabaseQuerys();

	}

	/**
	 * Connects to the XML database and stores the XML document. "localhost", 1984,
	 * "testuser", "testuser"
	 * 
	 * @throws Exception If an error occurs during the database connection or
	 *                   document storage.
	 */
	public void writeToDB(FmmlxDiagram diagram) throws IOException {
		if (!isInternetAvailable()) {
			showError("Unable to establish an internet connection. "
					+ "Please check your network settings and try again. "
					+ "Ensure that your firewall or network policies do not block access to essential services.");
			return;
		}
		ReturnCall<Document> onDocumentCreated = (doc) -> {

			String xmlDoc = XMLUtil.getStringFromDocument(doc);
			this.document = xmlDoc;
			try (ClientSession session = new ClientSession(hostname, port, user, password)) {

				session.execute("OPEN " + this.db_name);

				String diagramName = diagram.getPackagePath().substring(6);
				String mainDocumentName = diagramName + "_versions.xml";

				boolean mainDocumentExists = false;
				try {
					mainDocumentExists = session.query(querrys.mainDocumentExistsQuery(this.db_name, mainDocumentName))
							.execute().equals("true");
				} catch (BaseXException e) {
					// Das Dokument existiert nicht, mainDocumentExists bleibt false
					mainDocumentExists = false;
				}
				int newVersionNumber;

				if (!mainDocumentExists) {
					// Hauptdokument erstellen
					newVersionNumber = 0;
					createNewMainDoc(session, diagramName, mainDocumentName);
				} else {
					// Höchste Versionsnummer ermitteln
					newVersionNumber = getHighestVersion(mainDocumentName, session) + 1;
					// Neuen Verweis im Hauptdokument hinzufügen
					String updateMainDocumentQuery = querrys.updateMainDocumentQuery(this.db_name, mainDocumentName, diagramName, newVersionNumber);
					session.query(updateMainDocumentQuery).execute();
				}

				// Neue Version als separates Dokument hinzufügen
				String newVersionDocumentName = diagramName + "_version_" + newVersionNumber + ".xml";
				session.add(newVersionDocumentName, new ByteArrayInputStream(xmlDoc.getBytes(StandardCharsets.UTF_8)));

				showInfo("Version " + newVersionNumber + " für " + diagramName + " hinzugefügt.");

			} catch (Exception e) {
				showError("Failed to write to database");
				e.printStackTrace();

			}
		};
		this.creator.getXmlRepresentation(diagram.getPackagePath(), onDocumentCreated);
	}
	
	public void writeAsToDB (FmmlxDiagram diagram) throws IOException{
		if (!isInternetAvailable()){
			showError("Unable to establish an internet connection. "
					+ "Please check your network settings and try again. "
					+ "Ensure that your firewall or network policies do not block access to essential services.");
			return;
		}
		ReturnCall<Document> onDocumentCreated = (doc) -> {
			String xmlDoc = XMLUtil.getStringFromDocument(doc);
			this.document = xmlDoc;
		};
	}

	public void getDiagramsFromDB() {
		if (!isInternetAvailable()) {
			return;
		}
		DiagramClient client = new DiagramClient();
		List<File> files = getAllDocuments();
		for (File file : files) {
			loadXMFFile(file);
		}
	}
	

	/**
	 * Checks whether an Internet connection is available.
	 * 
	 * @return 	true if a connection to google.com can be established,
	 * 			false otherwise.
	 */
	protected boolean isInternetAvailable() {
		try {
			final URL url = new URL("https://www.google.com");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			con.setConnectTimeout(5000); // 5 Sekunden Timeout
			con.setReadTimeout(5000); // 5 Sekunden Timeout
			int status = con.getResponseCode();
			return (status == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			showError("Unable to connect to the internet. Please check your network connection and try again.");
			return false;
		}
	}

	
	/**
	 * Displays an error message in a modal dialog with an 'OK' button. This dialog
	 * blocks further input until the user dismisses it.
	 *
	 * @param message The error message to display in the dialog.
	 */
	protected void showError(String message) {
		Platform.runLater(() -> {
			new javafx.scene.control.Alert(AlertType.ERROR, message, ButtonType.OK).showAndWait();
		});
	}


	/**
	 * Displays an informational message in a modal dialog with an 'OK' button. This
	 * dialog blocks further input until the user dismisses it.
	 *
	 * @param message The information message to display in the dialog.
	 */
	protected void showInfo(String message) {
		Platform.runLater(() -> {
			new javafx.scene.control.Alert(AlertType.INFORMATION, message, ButtonType.OK).showAndWait();
		});

	}
	
	/**
	 * Counts the number of projects based on XML documents in a database.
	 * This method constructs an XQuery to count documents whose names end with '_versions.xml'
	 * in a specified database. It then executes this query in a provided session.
	 *
	 * @param session The session object used to execute the database query.
	 * @return The number of projects, represented by XML documents ending with '_versions.xml'.
	 * @throws Exception If there is an error during query execution or parsing the result.
	 */
	private int countProjects(Session session) {
		try 
		{
			String countQuery = querrys.countQuery(this.db_name);
				return Integer.parseInt(session.execute(countQuery));
		}
		catch (Exception e) {
			System.err.print("\n"+e.getMessage()+"\n count failed");
		}
		return 0;
	}
	
	/**
	 * Retrieves a list of document names from the database that end with '_versions.xml'.
	 * This method constructs an XQuery to filter and return document names from a specified database
	 * based on the given naming convention. It then executes this query using the provided session.
	 *
	 * @param session The session object used to execute the database query.
	 * @return A list of strings representing the names of the documents that match the criteria.
	 * @throws Exception If there is an error during the execution of the query.
	 */
	private List<String> getProjectDocumentNames(Session session) throws Exception {
	    List<String> documentNames = new ArrayList<>();
	    try {
	        // Construct the XQuery that retrieves all document names ending with '_versions.xml'
	        String query = querrys.getProjectDocumentNamesQuery(this.db_name);

	        // Execute the query and process the results
	        String result = session.execute(query);
	        // Assume that the returned result is a newline-separated list of document names
	        if (result != null && !result.isEmpty()) {
	            documentNames = Arrays.asList(result.split("\n"));
	        }
	    } catch (Exception e) {
	        // Print the error message to standard error
	        System.err.println("Error retrieving document names: " + e.getMessage());
	        // Optionally rethrow the exception to signal upper layers of an issue
	        throw e;
	    }
	    return documentNames;
	}

	/**
	 * Creates a new XML document in the database to serve as a version container
	 * for a given document or diagram.
	 *
	 * @param session          The session object representing the database session.
	 * @param diagramName      The name of the diagram which will be used in the XML
	 *                         content.
	 * @param mainDocumentName The name of the main document in the database.
	 * @throws IOException If there is an I/O error during the document creation
	 *                     process.
	 */
	private void createNewMainDoc(Session session, String diagramName, String mainDocumentName) throws IOException {
		String initialMainDocumentContent = querrys.initialMainDocumentContentQuery(diagramName);
		session.add(mainDocumentName,
				new ByteArrayInputStream(initialMainDocumentContent.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Retrieves the highest version number of a document stored in the database.
	 *
	 * @param mainDocumentName The name of the main document whose highest version
	 *                         number is to be retrieved.
	 * @param session          The session object representing the database session.
	 * @return The highest version number as an integer.
	 * @throws Exception If an error occurs during the database query or processing
	 *                   of the query results.
	 */
	private int getHighestVersion(String mainDocumentName, Session session) throws Exception {
		String highestVersionQuery = querrys.highestVersionQuery(this.db_name, mainDocumentName);
		String highestVersionRef = session.query(highestVersionQuery).execute();
		return Integer.parseInt(highestVersionRef);
	}
	
	/**
	 * Retrieves all the latest version documents from the database. This method
	 * opens a session with the database, queries for documents ending with
	 * '_versions.xml', and then retrieves the latest version of the documents of
	 * each of these documents.
	 *
	 * @return A list of {@link File} objects representing the latest versions of
	 *         each document found. Each file is constructed from the XML content of
	 *         the latest document version.
	 * @throws Exception If there is any issue during the database session,
	 *                   including problems with querying the database, processing
	 *                   results, or creating file objects from XML content.
	 *                   Specific error messages are printed to the standard error
	 *                   stream and user notifications are provided via showError
	 *                   method calls.
	 */
	private List<File> getAllDocuments() {
		List<File> files = new ArrayList<>();
		try (ClientSession session = new ClientSession(this.hostname, this.port, this.user, this.password)) {
			
			List<String> a = getProjectDocumentNames(session);
			for (String s : a) {
				System.err.print(s);
			}
			
			System.err.print(countProjects(session));
			if (countProjects(session) >=50)
			{
				
			}

			// Ermittle alle Hauptdokumente
			String versionsQuery = querrys.getVersionDocsQuery(this.db_name);
			ClientQuery versionsResult = session.query(versionsQuery);

			while (versionsResult.more()) {
				String versionDocumentPath = versionsResult.next();
				String mainDocumentName = new File(versionDocumentPath).getName();

				try {
					// Retrieves the highest version number for the current document
					int highestVersion = getHighestVersion(mainDocumentName, session);
					String projectName = mainDocumentName.substring(0, mainDocumentName.lastIndexOf("_versions.xml"));
					String latestVersionDocumentName = projectName + "_version_" + highestVersion + ".xml";

					// Queries for the latest version as an XML string and creates a File object
					String xmlQuery = "db:open('" + this.db_name + "', '" + latestVersionDocumentName + "')";
					ClientQuery xmlString = session.query(xmlQuery);
					File file = createFileFromString(xmlString.execute());

					files.add(file);
				} catch (Exception e) {
					// Handles cases where no versions are found or an error occurs
					System.err.println("Fehler beim Ermitteln der neuesten Version für: " + mainDocumentName + " - "
							+ e.getMessage());
					showError("Fehler beim Ermitteln der neuesten Version für: " + mainDocumentName + " - "
							+ e.getMessage());
				}
			}
			versionsResult.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * Retrieves a specific document from the database based on its name and the
	 * highest version available. This method establishes a session with the
	 * database, queries for the highest available version of the specified
	 * document, and retrieves that version as a File object.
	 *
	 * @param specificFileName The base name of the document to retrieve. This name
	 *                         should include the "_versions.xml" suffix to match
	 *                         the document naming convention in the database.
	 * @return A File object representing the latest version of the specified
	 *         document. Returns null if no versions are found or if an error occurs
	 *         during retrieval.
	 *
	 * @throws Exception Throws an exception if there are issues with the database
	 *                   connection or query execution. Errors are logged to the
	 *                   standard error stream and user is notified through the
	 *                   showError method, which needs to be implemented to handle
	 *                   user-visible error notifications appropriately.
	 */
	public File getSpecificDocument(String specificFileName) {
		File file = null;
		try (ClientSession session = new ClientSession(this.hostname, this.port, this.user, this.password)) {

			// Retrieve the highest version number for the specific document
			int highestVersion = getHighestVersion(specificFileName, session);
			if (highestVersion == -1) {
				System.err.println("No versions found for: " + specificFileName);
				showError("No versions found for: " + specificFileName);
				return null;
			}

			String projectName = specificFileName.substring(0, specificFileName.lastIndexOf("_versions.xml"));
			String latestVersionDocumentName = projectName + "_version_" + highestVersion + ".xml";

			// Query for the latest version as an XML string and create a File object
			String xmlQuery = "db:open('" + this.db_name + "', '" + latestVersionDocumentName + "')";
			ClientQuery xmlString = session.query(xmlQuery);
			file = createFileFromString(xmlString.execute());

		} catch (Exception e) {
			e.printStackTrace();
			showError(
					"Failed to retrieve the specific document from the database. Please check the database connection settings and ensure the database is accessible. Error details: "
							+ e.getMessage());
		}
		return file;
	}

	public void loadXMFFile(File file) {
		System.err.println(file);
		Platform.runLater(() -> {
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
