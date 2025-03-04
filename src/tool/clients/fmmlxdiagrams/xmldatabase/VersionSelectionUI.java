package tool.clients.fmmlxdiagrams.xmldatabase;

import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code VersionSelectionUI} class provides a user interface for selecting
 * projects, branches, and versions from an XML database. It allows users to navigate
 * through projects, select a branch, and load a specific version for further use.
 */
public class VersionSelectionUI extends XMLDatabase {

    private final BranchManager branchManager = new BranchManager();

    /**
     * Starts the user interface for version selection.
     * Sets up the layout, event listeners, and data loading logic.
     */
    public void start() {
        Stage stage = new Stage();
        stage.setTitle("Version Selection");

        // Layouts for the UI
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10, 10, 10, 10));

        // ListViews for Projects, Branches, and Versions
        ListView<String> mainDocumentList = new ListView<>();
        ListView<String> branchList = new ListView<>();
        ListView<String> versionList = new ListView<>();
        Button loadButton = new Button("Load Selected Version");

        // Project list layout
        VBox leftPane = new VBox(10, new Label("Projects"), mainDocumentList);
        VBox middlePane = new VBox(10, new Label("Branches"), branchList);
        VBox rightPane = new VBox(10, new Label("Versions"), versionList);
        leftPane.setPadding(new Insets(10));
        middlePane.setPadding(new Insets(10));
        rightPane.setPadding(new Insets(10));

        HBox centerPane = new HBox(20, leftPane, middlePane, rightPane);
        root.setCenter(centerPane);

        HBox bottomPane = new HBox(loadButton);
        bottomPane.setAlignment(Pos.BOTTOM_RIGHT);
        bottomPane.setPadding(new Insets(10));
        root.setBottom(bottomPane);

        // Load initial data
        loadMainDocuments(mainDocumentList, branchList, versionList);

        // Set listeners for project and branch selections
        mainDocumentList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadBranches(newSelection, branchList);
                branchList.getSelectionModel().select("main");
                loadVersions(newSelection, "main", versionList);
            } else {
                branchList.getItems().clear();
                versionList.getItems().clear();
            }
        });

        branchList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String selectedMainDocument = mainDocumentList.getSelectionModel().getSelectedItem();
                loadVersions(selectedMainDocument, newSelection, versionList);
            } else {
                versionList.getItems().clear();
            }
        });

        versionList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            loadButton.setDisable(newSelection == null);
        });

        loadButton.setDisable(true);
        loadButton.setOnAction(event -> {
            String selectedMainDocument = mainDocumentList.getSelectionModel().getSelectedItem();
            String selectedBranch = branchList.getSelectionModel().getSelectedItem();
            String selectedVersion = versionList.getSelectionModel().getSelectedItem();

            if (selectedMainDocument != null && selectedBranch != null && selectedVersion != null) {
                loadSelectedVersion(selectedMainDocument, selectedVersion);
            } else {
                showWarning("Selection Error", "No Project, Branch, or Version Selected",
                        "Please select a project, branch, and version to proceed.");
            }
        });

        stage.setScene(new Scene(root, 800, 400));
        stage.show();
    }

    /**
     * Loads the main documents (projects) from the database and initializes default selections.
     *
     * @param mainDocumentList the ListView for displaying main documents (projects)
     * @param branchList       the ListView for displaying branches
     * @param versionList      the ListView for displaying versions
     */
    private void loadMainDocuments(ListView<String> mainDocumentList, ListView<String> branchList, ListView<String> versionList) {
        try {
            List<String> mainDocuments = getProjectDocumentNames();
            if (mainDocuments != null && !mainDocuments.isEmpty()) {
                mainDocumentList.getItems().addAll(mainDocuments);

                mainDocumentList.getSelectionModel().select(0);
                String firstProject = mainDocumentList.getSelectionModel().getSelectedItem();

                loadBranches(firstProject, branchList);
                branchList.getSelectionModel().select("main");
                loadVersions(firstProject, "main", versionList);
            } else {
                showInfo("No Projects Found", "No projects were found in the database.");
            }
        } catch (Exception e) {
            showError("Error Fetching Projects", e.getMessage());
        }
    }

    /**
     * Loads the branches for the given project.
     *
     * @param mainDocumentName the name of the selected main document (project)
     * @param branchList       the ListView for displaying branches
     */
    private void loadBranches(String mainDocumentName, ListView<String> branchList) {
        try {
            List<String> branches = new ArrayList<>(branchManager.getAllBranches(mainDocumentName));
            if (!branches.contains("main")) {
                branches.add(0, "main");
            }
            branches.replaceAll(String::trim);
            branchList.getItems().setAll(branches);
            
        } catch (Exception e) {
            showError("Error Fetching Branches", e.getMessage());
            branchList.getItems().setAll("main");
        }
    }

    /**
     * Loads the versions for the selected branch.
     *
     * @param mainDocumentName the name of the selected main document (project)
     * @param branchName       the name of the selected branch
     * @param versionList      the ListView for displaying versions
     */
    private void loadVersions(String mainDocumentName, String branchName, ListView<String> versionList) {
        try {
            versionList.getItems().clear();

            List<String> versions;

            if ("main".equals(branchName)) {
                versions = getMainBranchVersions(mainDocumentName);
            } else {
                versions = branchManager.getVersionsOfBranch(mainDocumentName, branchName);
                versions.replaceAll(String::trim);
            }

            versionList.getItems().setAll(versions);
            System.out.println("Versions for branch '" + branchName + "': " + versions);

        } catch (Exception e) {
            System.err.println("Error loading versions for branch '" + branchName + "': " + e.getMessage());
            versionList.getItems().clear();
        }
    }

    /**
     * Loads the versions stored in the main branch.
     *
     * @param mainDocumentName the name of the main document (project)
     * @return a list of version references in the main branch
     * @throws IOException if an error occurs during database interaction
     */
    private List<String> getMainBranchVersions(String mainDocumentName) throws IOException {
        String query = "let $doc := db:open('" + db_name + "', '" + mainDocumentName + "') " +
                "return $doc//VersionsContainer/Version/@ref/string()";
        String result = executeQuery(query);
        return Arrays.asList(result.trim().split("\n"));
    }

    /**
     * Loads the selected version into the system.
     *
     * @param mainDocumentName the name of the main document (project)
     * @param selectedVersion  the name of the selected version
     */
    private void loadSelectedVersion(String mainDocumentName, String selectedVersion) {
        try {
            String xmlQuery = "db:open('" + db_name + "', '" + selectedVersion+ "')";
            String xmlString = executeQuery(xmlQuery);

            File tempFile = createFileFromString(xmlString);
            loadXMFFile(tempFile);

            showInfo("Version Loaded", "Successfully loaded version: " + selectedVersion);
        } catch (Exception e) {
            showError("Error Loading Version", e.getMessage());
        }
    }

    /**
     * Shows an informational alert.
     *
     * @param title   the title of the alert
     * @param content the content of the alert
     */
    private void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     *
     * @param title   the title of the alert
     * @param content the content of the alert
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert.
     *
     * @param title   the title of the alert
     * @param header  the header text of the alert
     * @param content the content of the alert
     */
    private void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
