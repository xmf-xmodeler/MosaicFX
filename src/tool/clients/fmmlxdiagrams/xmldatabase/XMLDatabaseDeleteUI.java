package tool.clients.fmmlxdiagrams.xmldatabase;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * A UI class for managing project deletion in the XML database.
 * Inherits from XMLDatabase to access its methods directly.
 */
public class XMLDatabaseDeleteUI extends XMLDatabase {

    private final XMLDatabaseQuerys querys = new XMLDatabaseQuerys();

    /**
     * Starts the UI by displaying the VBox in a new Stage.
     * 
     * @throws Exception If an error occurs while retrieving project names.
     */
    public void start() throws Exception {
        // Create the root UI element
        VBox root = new VBox(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        // Fetch project names from the database
        List<String> projectNames = getProjectDocumentNames();

        // Create a ComboBox and populate it with project names
        ComboBox<String> projectDropdown = new ComboBox<>();
        projectDropdown.getItems().addAll(projectNames);
        projectDropdown.setPromptText("Select a project to delete");

        // Create a delete button
        Button deleteButton = new Button("Delete Project");
        deleteButton.setDisable(true); // Initially disable the button

        // Enable the button when a project is selected
        projectDropdown.setOnAction(event -> {
            deleteButton.setDisable(projectDropdown.getValue() == null);
        });

        // Set button action to show confirmation dialog
        deleteButton.setOnAction(event -> {
            String selectedMainDocument = projectDropdown.getValue();
            if (selectedMainDocument != null) {
                // Hier wird die Methode mit der ComboBox aufgerufen
                showConfirmationDialog(selectedMainDocument, selectedMainDocument, projectDropdown);
            }
        });



        // Add the ComboBox and Button to the VBox
        root.getChildren().addAll(projectDropdown, deleteButton);

        // Create and show a new Stage
        Stage stage = new Stage();
        stage.setTitle("Delete Project");
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    /**
     * Shows a confirmation dialog before deleting the selected project.
     * 
     * @param projectName The name of the project to delete.
     */
    private void showConfirmationDialog(String projectName, String mainDocumentName, ComboBox<String> projectDropdown) {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirm Deletion");
        confirmationDialog.setHeaderText("Are you sure you want to delete this project?");
        confirmationDialog.setContentText("Project: " + projectName + "\nMain Document: " + mainDocumentName);

        // Warte auf die Benutzeraktion
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteProject(mainDocumentName, projectDropdown);
        }
    }




    private void deleteProject(String mainDocumentName, ComboBox<String> projectDropdown) {
        try {
            // Schritt 1: Lösche referenzierte Versionsdokumente
            String deleteReferencedVersionsQuery = querys.deleteReferencedVersionsQuery(super.db_name, mainDocumentName);
            executeQuery(deleteReferencedVersionsQuery);

            // Schritt 2: Lösche das Hauptdokument
            String deleteMainDocumentQuery = querys.deleteMainDocumentQuery(super.db_name, mainDocumentName);
            executeQuery(deleteMainDocumentQuery);

            // Schritt 3: Prüfe, ob das Hauptdokument noch existiert
            String checkMainDocumentQuery = querys.checkMainDocumentExistsQuery(super.db_name, mainDocumentName);
            String mainDocExists = executeQuery(checkMainDocumentQuery);

            // Schritt 4: Prüfe, ob noch Referenzen existieren
            String checkReferencesQuery = querys.getReferencedVersionsQuery(super.db_name, mainDocumentName);
            String referencesResult = executeQuery(checkReferencesQuery);

            if (mainDocExists.trim().equals("false") && (referencesResult == null || referencesResult.isEmpty())) {
                // Alles erfolgreich gelöscht
                Alert successDialog = new Alert(AlertType.INFORMATION);
                successDialog.setTitle("Deletion Successful");
                successDialog.setHeaderText(null);
                successDialog.setContentText("The main document '" + mainDocumentName + "' and all its referenced versions have been deleted successfully.");
                successDialog.showAndWait();

                // Aktualisiere das Dropdown-Menü
                projectDropdown.getItems().remove(mainDocumentName);
            } else {
                // Es existieren noch Daten, die nicht gelöscht wurden
                Alert warningDialog = new Alert(AlertType.WARNING);
                warningDialog.setTitle("Deletion Warning");
                warningDialog.setHeaderText("Some documents could not be deleted.");
                warningDialog.setContentText("It seems some references or the main document still exist in the database.");
                warningDialog.showAndWait();
            }
        } catch (Exception e) {
            // Fehlermeldung anzeigen
            Alert errorDialog = new Alert(AlertType.ERROR);
            errorDialog.setTitle("Deletion Failed");
            errorDialog.setHeaderText("An error occurred while deleting the main document and its references.");
            errorDialog.setContentText(e.getMessage());
            errorDialog.showAndWait();

            e.printStackTrace();
        }
    }







}
