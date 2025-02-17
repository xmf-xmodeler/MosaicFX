package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.xmldatabase.BranchManager;
import tool.clients.fmmlxdiagrams.xmldatabase.DefaultBranchManager;
import tool.clients.fmmlxdiagrams.xmldatabase.XMLDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RenameProjektDialog {

    private final BranchManager branchManager = new BranchManager();

    /**
     * Starts the dialog for handling existing or non-existing projects.
     *
     * @param diagram        The diagram to be saved.
     * @param db             The database handler.
     * @param existingProjects A list of existing project names in the database.
     */
    public void start(AbstractPackageViewer diagram, XMLDatabase db, List<String> existingProjects) {
        String projectName = diagram.getPackagePath().substring(6) + "_versions.xml";
//        existingProjects.replaceAll(String::trim);

        // Check if the project exists in the database
        if (existingProjects.contains(projectName)) {
            // Project exists, handle branch selection
            try {
                showBranchSelectionDialog(diagram, db);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Project does not exist, save it normally
            try {
                db.writeToDB((FmmlxDiagram) diagram);
                showInfo("Project Saved", "The project has been saved as a new project.");
            } catch (IOException e) {
                showError("Error Saving Project", e.getMessage());
            }
        }
    }

    /**
     * Displays the branch selection dialog for existing projects.
     */
    private void showBranchSelectionDialog(AbstractPackageViewer diagram, XMLDatabase db) throws IOException {
        Stage branchStage = new Stage();
        branchStage.setTitle("Select Branch");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Label and ListView for branches
        Label branchLabel = new Label("Select or create a branch:");
        ListView<String> branchList = new ListView<>();
        branchList.setPrefHeight(200);

        // Label for showing the default branch
        Label defaultBranchLabel = new Label();
        defaultBranchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;"); // Styling for visibility

        // Extract mainDocumentName from diagram
        String mainDocumentName = diagram.getPackagePath().substring(6) + "_versions.xml";

        // Load existing branches, convert to modifiable list
        List<String> branches = new ArrayList<>(branchManager.getAllBranches(mainDocumentName));
        if (!branches.contains("main")) {
            branches.add(0, "main"); // Add 'main' to the top of the list
        }
        branches.replaceAll(String::trim);

        branchList.getItems().addAll(branches);

        // Check if the Default Branch is in the list
        String defaultBranch = DefaultBranchManager.getInstance().getDefaultBranch();
        if (branches.contains(defaultBranch)) {
            defaultBranchLabel.setText("Default Branch: " + defaultBranch);
        } else {
            defaultBranchLabel.setText("Default Branch: None");
        }

        // Buttons for branch actions
        Button createBranchButton = new Button("Create New Branch");
        Button saveButton = new Button("Save");
        saveButton.setDisable(true); // Disabled until a branch is selected

        // Input for new branch name (hidden initially)
        HBox newBranchBox = new HBox(10);
        TextField newBranchNameField = new TextField();
        newBranchNameField.setPromptText("Enter branch name...");
        Button confirmBranchButton = new Button("OK");
        newBranchBox.getChildren().addAll(newBranchNameField, confirmBranchButton);
        newBranchBox.setVisible(false);

        // Show the input field when "Create New Branch" is clicked
        createBranchButton.setOnAction(event -> newBranchBox.setVisible(true));

        newBranchNameField.setOnAction(event -> confirmBranchButton.fire());

        // Create the new branch when "OK" is clicked
        confirmBranchButton.setOnAction(event -> {
            String newBranchName = newBranchNameField.getText().trim();
            if (!newBranchName.isEmpty()) {
                try {
                    branchManager.createBranch(diagram.getPackagePath().substring(6) + "_versions.xml", newBranchName);
                    branchList.getItems().add(newBranchName);
                    newBranchBox.setVisible(false);
                    newBranchNameField.clear();

                    // Update the Default Branch label if the new branch is set as default
                    if (newBranchName.equals(DefaultBranchManager.getInstance().getDefaultBranch())) {
                        defaultBranchLabel.setText("Default Branch: " + newBranchName);
                    }
                } catch (IOException e) {
                    showError("Error Creating Branch", e.getMessage());
                }
            } else {
                showError("Invalid Input", "Branch name cannot be empty.");
            }
        });

        // Enable the save button when a branch is selected
        branchList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            saveButton.setDisable(newSelection == null);
        });

        // Save the diagram to the selected branch
        saveButton.setOnAction(event -> {
            String selectedBranch = branchList.getSelectionModel().getSelectedItem();
            try {
                if ("main".equals(selectedBranch)) {
                    // Save to the main project
                    db.writeToDB((FmmlxDiagram) diagram);
                } else {
                    // Save to the selected branch
                    branchManager.addDiagramToBranch((FmmlxDiagram) diagram, diagram.getPackagePath().substring(6) + "_versions.xml", selectedBranch.trim());
                }

                // Update the Default Branch label
                DefaultBranchManager.getInstance().setDefaultBranch(selectedBranch);
                defaultBranchLabel.setText("Default Branch: " + selectedBranch);

                branchStage.close();
            } catch (IOException e) {
                showError("Error Saving Diagram", e.getMessage());
            }
        });

        // Layout
        root.getChildren().addAll(branchLabel, branchList, createBranchButton, newBranchBox, saveButton, defaultBranchLabel);

        Scene scene = new Scene(root, 400, 300);
        branchStage.setScene(scene);
        branchStage.show();
    }


    private String setNewName(AbstractPackageViewer diagram) {
        String projectPath = diagram.getPackagePath().substring(6);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Name for Project");
        dialog.setHeaderText("Enter a new name for the project and save again:");
        Optional<String> result = dialog.showAndWait();
        String projectName = "";
        if (result.isPresent()) {
            projectName = result.get();
        }
        return projectName;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
