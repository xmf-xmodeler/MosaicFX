package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	  private Map<String, CheckBox> checkBoxMap;

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
	
	  public void start() {
	        Stage stage = new Stage();
	        VBox root = new VBox(10);

	        // Search Term Input
	        TextField searchTerms = new TextField();
	        searchTerms.setPromptText("Enter search terms");

	        // Initialize the map to store CheckBoxes
	        checkBoxMap = new HashMap<>();

	        VBox searchOptions = new VBox(5);
	        List<String> checkBoxLabels = List.of(
	            "Project name", "Diagram name", "Object name", 
	            "Association name", "Operation signature", 
	            "Constraint name", "Attribute name"
	        );

	        Map<String, String> elementMapping = new HashMap<>();
	        elementMapping.put("Project name", "Project/@name");
	        elementMapping.put("Diagram name", "Diagram/@label");
	        elementMapping.put("Object name", "addMetaClass/@name");
	        elementMapping.put("Association name", "Edge/@name");
	        elementMapping.put("Operation signature", "Operation/@signature");
	        elementMapping.put("Constraint name", "Constraint/@name");
	        elementMapping.put("Attribute name", "Attribute/@name");

	        checkBoxLabels.forEach(label -> {
	            CheckBox checkBox = new CheckBox(label);
	            checkBox.setSelected(true);
	            addCheckBoxToMapAndVBox(checkBox, searchOptions);
	        });

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
	        levelOptions.getChildren().addAll(levelInputLabel, levelInput);
	        levelInput.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (!newValue.matches("\\d*")) {
	                levelInput.setText(newValue.replaceAll("[^\\d]", ""));
	            }
	        });

	        HBox searchAndFilter = new HBox(20, searchOptions, filterOptions, levelOptions);

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
	            String results = null;
	            try {
	                results = performSearch(query, filterLevel.getValue(), levelInput.getText(), elementMapping);
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	            outputArea.setText(results);
	        });

	        startSearchButton.setOnAction(e -> {
	            String query = searchTerms.getText();
	            String results = null;
	            try {
	                results = performSearch(query, filterLevel.getValue(), levelInput.getText(), elementMapping);
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	            outputArea.setText(results);
	        });

	        root.getChildren().addAll(searchTerms, searchAndFilter, outputArea, searchButton, startSearchButton);
	        Scene scene = new Scene(root, 600, 400);
	        stage.setScene(scene);
	        stage.setTitle("Search Console");
	        stage.show();
	    }

	    /**
	     * Adds a CheckBox to the specified VBox and stores it in the checkBoxMap.
	     *
	     * @param checkBox the CheckBox to be added
	     * @param vBox     the VBox to which the CheckBox will be added
	     */
	    private void addCheckBoxToMapAndVBox(CheckBox checkBox, VBox vBox) {
	        checkBoxMap.put(checkBox.getText(), checkBox);
	        vBox.getChildren().add(checkBox);
	    }

	    /**
	     * Performs a search based on the provided query, selected CheckBox options, filter, and level.
	     *
	     * @param searchTerm the search query entered by the user
	     * @param filter     the filter level selected by the user
	     * @param level      the level input provided by the user
	     * @param elementMapping a map of CheckBox labels to corresponding XML element names
	     * @return a String representing the search results
	     * @throws IOException 
	     * @throws BaseXException 
	     */
	    private String performSearch(String searchTerm, String filter, String level, Map<String, String> elementMapping) throws BaseXException, IOException {
	        XMLDatabaseQuerys querys = new XMLDatabaseQuerys();
	        String searchQuery = querys.searchDocumentsQuery(this.db_name, searchTerm, checkBoxMap, filter, elementMapping);
	        String result = executeQuery(searchQuery);
	        System.err.print("\n Ergebniss: " + result);
	        StringBuilder searchInfo = new StringBuilder("Search results for: " + searchTerm + "\nFilter: " + filter + "\nLevel: " + level + "\nSearch Options:\n");
	        for (String key : checkBoxMap.keySet()) {
	            if (checkBoxMap.get(key).isSelected()) {
	                searchInfo.append(key).append("\n");
	            }
	        }
	        return searchInfo.toString();
	    }

	    /**
	     * The method checks whether there is an Internet connection. 
	     * If this is the case, the database is opened and the query passed is executed 
	     * @param query
	     * @return
	     * @throws BaseXException
	     * @throws IOException
	     */
	    private String executeQuery(String query) throws BaseXException, IOException {
	        if (!isInternetAvailable()) {
	            System.err.print("Keine Internet Verbindung");
	            throw new IOException("Internet not available");
	        }
	        try (ClientSession session = new ClientSession(hostname, port, user, password)) {
	            session.execute(new Open(this.db_name));
	            ClientQuery clientQuery = session.query(query);

	            StringBuilder resultBuilder = new StringBuilder();
	            while (clientQuery.more()) {
	                String result = clientQuery.next();
	                resultBuilder.append(result).append("\n");
	                System.err.print(result + "\n");
	            }

	            clientQuery.close();
	            System.err.print(hostname + "\n" + port + "\n" + user + "\n" + password + "\n");
	            System.err.print(query);

	            return resultBuilder.toString();
	        }
	    }

	    private void testConnection() throws IOException, BaseXException {
	        try (ClientSession session = new ClientSession(hostname, port, user, password)) {
	            session.execute("LIST");
	        }
	    }
}
