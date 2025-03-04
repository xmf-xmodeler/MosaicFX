package tool.clients.fmmlxdiagrams.xmldatabase;

import org.basex.api.client.ClientSession;
import org.w3c.dom.Document;

import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.helper.persistence.XMLUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles branch-related operations in the XML database.
 * It extends XMLDatabase to reuse existing database functionality.
 */
public class BranchManager extends XMLDatabase {

    private final XMLDatabaseQuerys queries;

    public BranchManager() {
        super(); // Ruft den Konstruktor von XMLDatabase auf
        this.queries = new XMLDatabaseQuerys();
    }

    /**
     * Creates a new branch in the main document.
     *
     * @param mainDocumentName The name of the main document.
     * @param branchName       The name of the new branch.
     */
    public void createBranch(String mainDocumentName, String branchName) throws IOException {
        String query = queries.createBranchQuery(db_name, mainDocumentName, branchName);
        executeQuery(query);
    }

    /**
     * Adds a new version of a diagram to the specified branch.
     *
     * @param diagram          The diagram to store.
     * @param mainDocumentName The name of the main document.
     * @param branchName       The branch to which the version will be added.
     */
    public void addDiagramToBranch(FmmlxDiagram diagram, String mainDocumentName, String branchName) throws IOException {
        if (!isInternetAvailable()) {
            showError("Unable to establish an internet connection. "
                    + "Please check your network settings and try again. "
                    + "Ensure that your firewall or network policies do not block access to essential services.");
            return;
        }

        ReturnCall<Document> onDocumentCreated = (doc) -> {
            String xmlDoc = XMLUtil.getStringFromDocument(doc);
            try (ClientSession session = new ClientSession(hostname, port, user, password)) {
                session.execute("OPEN " + db_name);

                // Höchste Versionsnummer im Branch ermitteln
                int newVersionNumber = getHighestVersionInBranch(mainDocumentName, branchName) + 1;

                // Neue Benennungslogik
                String newVersionName;
                if ("main".equals(branchName)) {
                    newVersionName = diagram.getPackagePath().substring(6) + "_version_" + newVersionNumber + ".xml";
                } else {
                    newVersionName = diagram.getPackagePath().substring(6) + "_" + branchName + "_" + newVersionNumber + ".xml";
                }

                // Neue Version als separates Dokument hinzufügen
                session.add(newVersionName, new ByteArrayInputStream(xmlDoc.getBytes(StandardCharsets.UTF_8)));

                // Branch im Hauptdokument aktualisieren
                String query = queries.addVersionToBranchQuery(db_name, mainDocumentName, branchName, newVersionName);
                executeQuery(query);

                // Erfolgsnachricht
                showInfo("Saved version to branch: " + branchName + " with name: " + newVersionName);
            } catch (Exception e) {
                // Fehlermeldung
                showError("Failed to add diagram to branch: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Erzeugt die XML-Repräsentation des Diagramms und ruft den ReturnCall auf
        this.creator.getXmlRepresentation(diagram.getPackagePath(), onDocumentCreated);
        DefaultBranchManager branchManager = DefaultBranchManager.getInstance();
        branchManager.setDefaultBranch(branchName);
    }



    /**
     * Retrieves the highest version number in a specified branch.
     *
     * @param mainDocumentName The name of the main document.
     * @param branchName       The branch to check.
     * @return The highest version number.
     */
    public int getHighestVersionInBranch(String mainDocumentName, String branchName) throws IOException {
        String query = queries.getHighestVersionInBranchQuery(db_name, mainDocumentName, branchName);
        String result = executeQuery(query).trim();

        if (result.isEmpty()) {
            // Falls keine Version vorhanden ist, starte mit 0
            return 0;
        }

        return Integer.parseInt(result);
    }


    /**
     * Retrieves all branches in the main document. If no branches exist, returns 'main'.
     *
     * @param mainDocumentName The name of the main document.
     * @return A list of branch names.
     * @throws IOException If an error occurs during query execution.
     */
    public List<String> getAllBranches(String mainDocumentName) throws IOException {
        String query = queries.getAllBranchesQuery(db_name, mainDocumentName);
        String result = executeQuery(query);

        // Wenn keine Branches gefunden wurden, füge 'main' als Standard hinzu
        if (result == null || result.trim().isEmpty()) {
            return List.of("main");
        }
        return Arrays.asList(result.trim().split("\n"));
    }


    /**
     * Executes a query on the BaseX database.
     *
     * @param query The query to execute.
     * @return The result of the query execution.
     * @throws IOException If an error occurs during query execution.
     */
    @Override
    protected String executeQuery(String query) throws IOException {
        try (ClientSession session = new ClientSession(hostname, port, user, password)) {
            return session.query(query).execute();
        }
    }
    
    /**
     * Retrieves all versions of the specified branch in the main document.
     *
     * @param mainDocumentName The name of the main document.
     * @param branchName       The name of the branch.
     * @return A list of version references.
     * @throws IOException If an error occurs during query execution.
     */
    public List<String> getVersionsOfBranch(String mainDocumentName, String branchName) throws IOException {
        String query = queries.getVersionsOfBranchQuery(db_name, mainDocumentName, branchName.trim());
        String result = executeQuery(query);
        return Arrays.asList(result.trim().split("\n"));
    }
    
    /**
     * Retrieves all versions from the main branch (root level of VersionsContainer).
     *
     * @param mainDocumentName The name of the main document.
     * @return A list of version references from the main branch.
     * @throws IOException If an error occurs during query execution.
     */
    public List<String> getMainBranchVersions(String mainDocumentName) throws IOException {
        String query = "let $doc := db:get('" + db_name + "', '" + mainDocumentName + "') " +
                       "return $doc//VersionsContainer/Version/@ref/string()";
        String result = executeQuery(query);
        return Arrays.asList(result.trim().split("\n"));
    }


}
