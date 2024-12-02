package tool.clients.fmmlxdiagrams.xmldatabase;

import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.ReturnCall;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.basex.core.BaseXException;

/**
 * A user interface class for selecting and loading versions of projects stored in an XML database.
 */
public class VersionSelectionUI extends XMLDatabase {

    private final XMLDatabaseQuerys querys = new XMLDatabaseQuerys();
    private FmmlxDiagramCommunicator communicator = FmmlxDiagramCommunicator.getCommunicator();

    /**
     * Starts the user interface for selecting and loading versions.
     */
    public void start() {
        Stage stage = new Stage();
        stage.setTitle("Version Selection");

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));

        // Left list: main documents
        ListView<String> mainDocumentList = new ListView<>();
        VBox leftPane = new VBox(10, new Label("Projects"), mainDocumentList);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        // Right list: versions
        ListView<String> versionList = new ListView<>();
        VBox rightPane = new VBox(10, new Label("Versions"), versionList);
        rightPane.setPadding(new Insets(10, 10, 10, 10));

        // Load all main documents into the left list
        loadMainDocuments(mainDocumentList);

        // React to selection of a main document
        mainDocumentList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadVersions(newSelection, versionList);
            } else {
                versionList.getItems().clear();
            }
        });

        // Load button in the bottom-right corner
        Button loadButton = new Button("Load Selected Version");
        loadButton.setDisable(true); // Initially disabled
        loadButton.setOnAction(event -> {
            String selectedMainDocument = mainDocumentList.getSelectionModel().getSelectedItem();
            String selectedVersion = versionList.getSelectionModel().getSelectedItem();
            
            String projectName = selectedMainDocument.replace("_versions.xml", "");

            if (selectedMainDocument != null && selectedVersion != null) {
                checkForNameConflict(projectName, () -> AddProject(selectedVersion));
            } else {
                Alert noSelectionAlert = new Alert(AlertType.WARNING);
                noSelectionAlert.setTitle("Selection Error");
                noSelectionAlert.setHeaderText("No Project or Version Selected");
                noSelectionAlert.setContentText("Please select a project and a version to proceed.");
                noSelectionAlert.showAndWait();
            }
        });

        // Enable the button when a version is selected
        versionList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            loadButton.setDisable(newSelection == null);
        });

        // Assemble layout
        HBox centerPane = new HBox(20, leftPane, rightPane);
        root.setCenter(centerPane);

        HBox bottomPane = new HBox(loadButton);
        bottomPane.setAlignment(Pos.BOTTOM_RIGHT);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        root.setBottom(bottomPane);

        // Scene and stage
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    

	private void AddProject(String selectedVersion) {
		
		int startIndex = selectedVersion.indexOf("\"") + 1; 
		int endIndex = selectedVersion.lastIndexOf("\"");  
		selectedVersion = selectedVersion.substring(startIndex, endIndex);
		String xmlQuery = "db:open('" + this.db_name + "', '" + selectedVersion + "')";
		try {
			String xmlString = executeQuery(xmlQuery);
			File temp = createFileFromString(xmlString);
			loadXMFFile(temp);
		} catch (BaseXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	/**
     * Loads the main documents into the left list.
     *
     * @param mainDocumentList The list to populate with main documents.
     */
    private void loadMainDocuments(ListView<String> mainDocumentList) {
        try {
            // Retrieve the list of project document names
            List<String> mainDocuments = getProjectDocumentNames();

            // If data exists, add it to the list
            if (mainDocuments != null && !mainDocuments.isEmpty()) {
                mainDocumentList.getItems().addAll(mainDocuments);
            } else {
                // Show a notification if no projects are found
                Alert noProjectsAlert = new Alert(AlertType.INFORMATION);
                noProjectsAlert.setTitle("No Projects Found");
                noProjectsAlert.setHeaderText(null);
                noProjectsAlert.setContentText("No projects were found in the database.");
                noProjectsAlert.showAndWait();
            }
        } catch (Exception e) {
            // Show an error message if an error occurs
            Alert errorDialog = new Alert(AlertType.ERROR);
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText("An error occurred while fetching projects.");
            errorDialog.setContentText(e.getMessage());
            errorDialog.showAndWait();
        }
    }

    /**
     * Loads the versions of a main document into the right list.
     *
     * @param mainDocumentName The name of the main document.
     * @param versionList      The list to populate with versions.
     */
    private void loadVersions(String mainDocumentName, ListView<String> versionList) {
        try {
            String query = querys.getAvailableVersionsQuery(super.db_name, mainDocumentName);
            String result = executeQuery(query);

            versionList.getItems().clear();
            if (result != null && !result.isEmpty()) {
                String[] versions = result.split("\n");
                versionList.getItems().addAll(versions);
            } else {
                Alert noVersionsAlert = new Alert(AlertType.INFORMATION);
                noVersionsAlert.setTitle("No Versions Found");
                noVersionsAlert.setHeaderText(null);
                noVersionsAlert.setContentText("No versions were found for the selected project.");
                noVersionsAlert.showAndWait();
            }
        } catch (Exception e) {
            Alert errorDialog = new Alert(AlertType.ERROR);
            errorDialog.setTitle("Error");
            errorDialog.setHeaderText("An error occurred while fetching versions.");
            errorDialog.setContentText(e.getMessage());
            errorDialog.showAndWait();
        }
    }

    /**
     * Checks for name conflicts and proceeds if no conflict exists.
     *
     * @param projectName The name of the project to check.
     * @param onConflictResolved The callback to execute if no conflict exists.
     */
    private void checkForNameConflict(String projectName, Runnable onConflictResolved) {
        ReturnCall<Vector<Object>> onProjectNamesReturned = projectNamesVec -> {
            Vector<String> projectNames = (Vector) projectNamesVec.get(0);
            for (String existingProjectName : projectNames) {
                if (existingProjectName.equals(projectName)) {
                    showNameConflictAlert(projectName);
                    return;
                }
            }
            onConflictResolved.run();
        };

        communicator.xmfRequestAsync(0, 0, "getAllProjectNames", onProjectNamesReturned);
    }



    /**
     * Displays an alert indicating a name conflict for the project.
     *
     * @param projectName The conflicting project name.
     */
    private void showNameConflictAlert(String projectName) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Name Conflict");
        alert.setHeaderText("Project Name Conflict");
        alert.setContentText("A project with the name '" + projectName + "' already exists. Please use a different name.");
        alert.showAndWait();
    }


}
