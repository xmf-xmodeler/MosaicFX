package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;
import org.basex.core.cmd.Open;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * @author Nicolas Engel
 *
 */
public class XMLDatabaseConsole extends XMLDatabase {
	private List<CheckBox> checkBoxList;

//	   public void startConsole() {
//	        Stage stage = new Stage();
//	        VBox root = new VBox();
//	        TextField inputField = new TextField();
//	        TextArea outputArea = new TextArea();
//	        outputArea.setWrapText(true);
//	        Button executeButton = new Button("Execute");
//
//	        executeButton.setOnAction(e -> {
//	            String query = inputField.getText();
//	            try {
//	                String result = executeQuery(query);
//	                outputArea.setText(result);
//	            } catch (BaseXException ex) {
//	                outputArea.setText("BaseX Query Error: " + ex.getMessage());
//	                showError("BaseX Query Error: Failed to execute the query. "
//	                		+ "Please check your query syntax and database connection. Error details: " + ex.getMessage());
//	                System.err.print(ex.getMessage());
//	            } catch (IOException ex) {
//	                outputArea.setText("IO Error: " + ex.getMessage());
//	                showError("IO Error: An input/output error occurred while executing the query. "
//	                		+ "Please check your network or file access permissions. Error details: " + ex.getMessage());
//	            } catch (Exception ex) {
//	                outputArea.setText("Error: " + ex.getMessage());
//	                showError("General Error: An unexpected error occurred. "
//	                		+ "Please contact support if the problem persists. Error details: " + ex.getMessage());
//	            }
//	        });
//
//	        root.getChildren().addAll(inputField, executeButton, outputArea);
//	        Scene scene = new Scene(root, 600, 400);
//	        stage.setScene(scene);
//	        stage.setTitle("XML Database Console");
//	        stage.show();
//
//	        // Test the connection when the console starts
//	        try {
//	            testConnection();
//	            outputArea.setText("Connected to database successfully.");
//	        } catch (Exception ex) {
//	        	showError("Failed to connect to database: " + ex.getMessage());
//	            outputArea.setText("Failed to connect to database: " + ex.getMessage());
//	        }
//	    }
	
	/**
     * Starts the JavaFX Stage
     *
     * @param stage the primary stage for this application
     */
	public void start() {
		Stage stage = new Stage();
		VBox root = new VBox(10);

		// Search Term Input
		TextField searchTerms = new TextField();
		searchTerms.setPromptText("Enter search terms");

		// Initialize the list to store CheckBoxes
		checkBoxList = new ArrayList<>();

		// Search Options
		VBox searchOptions = new VBox(5);
		CheckBox projectName = new CheckBox("Project name");
		CheckBox diagramName = new CheckBox("Diagram name");
		CheckBox objectName = new CheckBox("Object name");
		CheckBox associationName = new CheckBox("Association name");
		CheckBox operationSignature = new CheckBox("Operation signature");
		CheckBox constraintName = new CheckBox("Constraint name");
		CheckBox attributeName = new CheckBox("Attribute name");

		// Add CheckBoxes to the VBox and the List
		addCheckBoxToListAndVBox(projectName, searchOptions);
		addCheckBoxToListAndVBox(diagramName, searchOptions);
		addCheckBoxToListAndVBox(objectName, searchOptions);
		addCheckBoxToListAndVBox(associationName, searchOptions);
		addCheckBoxToListAndVBox(operationSignature, searchOptions);
		addCheckBoxToListAndVBox(constraintName, searchOptions);
		addCheckBoxToListAndVBox(attributeName, searchOptions);

		// Filter Options
		VBox filterOptions = new VBox(5);
		Label filterLabel = new Label("Filter");
		ComboBox<String> filterLevel = new ComboBox<>();
		filterLevel.getItems().addAll("Specific Project");
		filterOptions.getChildren().addAll(filterLabel, filterLevel);
		
		 // Level Input
		VBox levelOptions = new VBox(6);
		Label levelInputLabel = new Label("Level");
        TextField levelInput = new TextField();
        levelInput.setPromptText("Enter level (integer)");
        levelOptions.getChildren().addAll(levelInputLabel,levelInput);
        levelInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                levelInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

		HBox searchAndFilter = new HBox(20, searchOptions, filterOptions,levelOptions);

		// Output Area
		TextArea outputArea = new TextArea();
		outputArea.setWrapText(true);
		outputArea.setPromptText("Hier erscheinen die Suchergebnisse");
		outputArea.setEditable(false);

		  // Search Buttons
        Button searchButton = new Button("Weitere Ergebnisse anzeigen");
        Button startSearchButton = new Button("Suche starten");

        searchButton.setOnAction(e -> {
            String query = searchTerms.getText();
            String results = performSearch(query, checkBoxList, filterLevel.getValue(), levelInput.getText());
            outputArea.setText(results);
        });

        startSearchButton.setOnAction(e -> {
            String query = searchTerms.getText();
            String results = performSearch(query, checkBoxList, filterLevel.getValue(), levelInput.getText());
            outputArea.setText(results);
        });

		root.getChildren().addAll(searchTerms, searchAndFilter, outputArea, searchButton);
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Search Console");
		stage.show();
	}
	
	/**
     * Adds a CheckBox to the specified VBox and stores it in the checkBoxList.
     *
     * @param checkBox the CheckBox to be added
     * @param vBox     the VBox to which the CheckBox will be added
     */
	private void addCheckBoxToListAndVBox(CheckBox checkBox, VBox vBox) {
		checkBoxList.add(checkBox);
		vBox.getChildren().add(checkBox);
	}

    /**
     * Performs a search based on the provided query, selected CheckBox options, filter, and level.
     *
     * @param query     the search query entered by the user
     * @param checkBoxes the list of CheckBoxes representing the search options
     * @param filter    the filter level selected by the user
     * @param level     the level input provided by the user
     * @return a String representing the search results
     */
    private String performSearch(String query, List<CheckBox> checkBoxes, String filter, String level) {
        // Implement search logic here
        StringBuilder searchInfo = new StringBuilder("Search results for: " + query + "\nFilter: " + filter + "\nLevel: " + level + "\nSearch Options:\n");
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                searchInfo.append(checkBox.getText()).append("\n");
            }
        }
        return searchInfo.toString();
    }

	private String executeQuery(String query) throws BaseXException, IOException {
		if (!isInternetAvailable()) {
			throw new IOException("Internet not available");
		}
		try (ClientSession session = new ClientSession(hostname, port, user, password)) {
			session.execute(new Open("database"));
			ClientQuery clientQuery = session.query(query);
			String result = clientQuery.execute();
			clientQuery.close();
			return result;
		}
	}

	private void testConnection() throws IOException, BaseXException {
		try (ClientSession session = new ClientSession(hostname, port, user, password)) {
			session.execute("LIST");
		}
	}
}
