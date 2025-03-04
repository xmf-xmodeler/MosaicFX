package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.IOException;

import org.basex.core.BaseXException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class XMLDatabaseConsoleCustom extends XMLDatabaseConsole{
    public VBox createContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        TextField inputField = new TextField();
        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        Button executeButton = new Button("Execute");

        executeButton.setOnAction(e -> {
            String query = inputField.getText();
            try {
                String result = executeQuery(query);
                outputArea.setText(result);
            } catch (BaseXException ex) {
                outputArea.setText("BaseX Query Error: " + ex.getMessage());
                showError("BaseX Query Error: Failed to execute the query. "
                        + "Please check your query syntax and database connection. Error details: " + ex.getMessage());
                System.err.print(ex.getMessage());
            } catch (IOException ex) {
                outputArea.setText("IO Error: " + ex.getMessage());
                showError("IO Error: An input/output error occurred while executing the query. "
                        + "Please check your network or file access permissions. Error details: " + ex.getMessage());
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
                showError("General Error: An unexpected error occurred. "
                        + "Please contact support if the problem persists. Error details: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(inputField, executeButton, outputArea);

        // Test the connection when the console starts
        try {
            testConnection();
            outputArea.setText("Connected to database successfully.");
        } catch (Exception ex) {
            showError("Failed to connect to database: " + ex.getMessage());
            outputArea.setText("Failed to connect to database: " + ex.getMessage());
        }

        return root;
    }
}

