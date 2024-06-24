package tool.clients.fmmlxdiagrams.xmldatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.basex.api.client.Session;
import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;
import org.basex.core.cmd.Open;

import javafx.geometry.Insets;
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
		

	
		 public VBox createContent() {
	            VBox root = new VBox(10);
	            root.setPadding(new Insets(10, 10, 10, 10));

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

	            checkBoxLabels.forEach(label -> {
	                CheckBox checkBox = new CheckBox(label);
	                checkBox.setSelected(true);
	                addCheckBoxToMapAndVBox(checkBox, searchOptions);
	            });

	            // Filter Options
	            VBox filterOptions = new VBox(5);
	            Label filterLabel = new Label("Filter");
	            ComboBox<String> filterDropDown = new ComboBox<>();

	            try (ClientSession session = new ClientSession(hostname, port, user, password)) {
	                List<String> documentNames = new ArrayList<>();
	                documentNames = getProjectDocumentNames(session);
	                filterDropDown.getItems().addAll(documentNames);
	            } catch (Exception e1) {
	                e1.printStackTrace();
	            }

	            filterOptions.getChildren().addAll(filterLabel, filterDropDown);

	            HBox searchAndFilter = new HBox(20, searchOptions, filterOptions);

	            // Output Area
	            TextArea outputArea = new TextArea();
	            outputArea.setWrapText(true);
	            outputArea.setPromptText("The search results appear here");
	            outputArea.setEditable(false);

	            // Search Buttons
	            Button searchButton = new Button("Show more results");
	            Button startSearchButton = new Button("Start search");

	            startSearchButton.setOnAction(e -> {
	                String query = searchTerms.getText();
	                List<String> results = null;
	                try {
	                    results = performSearch(query, searchOptions, filterDropDown.getValue());
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	                outputArea.setText(String.join("\n", results));
	            });

	            root.getChildren().addAll(searchTerms, searchAndFilter, outputArea, searchButton, startSearchButton);
	            return root;
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
	     * Performs a search based on the provided search term, selected CheckBox options in the VBox, and filter.
	     *
	     * @param searchTerm The term to search for.
	     * @param checkBoxVBox The VBox containing CheckBox options.
	     */
	    public List<String> performSearch(String searchTerm, VBox checkBoxVBox,String filter) throws BaseXException, IOException {
	        List<String> results = new ArrayList<>();

	        for (int i = 0; i < checkBoxVBox.getChildren().size(); i++) {
	            CheckBox checkBox = (CheckBox) checkBoxVBox.getChildren().get(i);
	            if (checkBox.isSelected()) {
	                SearchOptions option = SearchOptions.values()[i];
	                String xpath = option.getXpath();
	                String query = constructQuery(db_name, searchTerm, xpath,filter);
	                String result = executeQuery(query);
//	                System.err.print(result);
	                if (result != "")
	                	results.add(option.name() + ": \n " + result);
	            }
	        }

	        return results;
	    }

	    /**
	     * Constructs an XQuery to search for documents based on the provided search term and options.
	     *
	     * @param db_name The name of the database.
	     * @param searchTerm The term to search for.
	     * @param xpath The XPath string corresponding to the search option.
	     * @param filter The filter to apply (if any).
	     * @return The XQuery string.
	     */
	    private String constructQuery(String db_name, String searchTerm, String xpath,String filter) {
	    		
	    	String query = XMLDatabaseQuerys.searchDocumentsQuery(db_name,xpath,searchTerm,filter);	
	    	return query;
	    }

	    /**
	     * The method checks whether there is an Internet connection. 
	     * If this is the case, the database is opened and the query passed is executed 
	     * @param query
	     * @return
	     * @throws BaseXException
	     * @throws IOException
	     */
	    protected String executeQuery(String query) throws BaseXException, IOException {
	        if (!isInternetAvailable()) {
	            
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
//	            System.err.print(hostname + "\n" + port + "\n" + user + "\n" + password + "\n");
	            System.err.print(query + "\n");
	            

	            return resultBuilder.toString();
	        }
	        catch (Exception e) {
				System.err.print("execute failed \n");// TODO: handle exception
				e.printStackTrace();
			}
			return null;
	    }

	    protected void testConnection() throws IOException, BaseXException {
	        try (ClientSession session = new ClientSession(hostname, port, user, password)) {
	            session.execute("LIST");
	        }
	    }
}
