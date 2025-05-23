package tool.clients.fmmlxdiagrams.xmldatabase;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class XMLDatabaseConsoleTabs {

    public void start() {
    	Stage primaryStage = new Stage();
        primaryStage.setTitle("XML Database Console");
        VBox root = new VBox();
        Scene scene = new Scene(root, 600, 400);

        TabPane tabPane = new TabPane();
        root.getChildren().add(tabPane);

        Tab xmlDatabaseTab = new Tab("XML Database");
        Tab customConsoleTab = new Tab("Custom Console");
        tabPane.getTabs().addAll(xmlDatabaseTab, customConsoleTab);

        buildXmlDatabaseTab(xmlDatabaseTab);
        buildCustomConsoleTab(customConsoleTab);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void buildXmlDatabaseTab(Tab xmlDatabaseTab) {
        XMLDatabaseConsole console = new XMLDatabaseConsole();
        VBox content = console.createContent();
        xmlDatabaseTab.setContent(content);
    }

    private void buildCustomConsoleTab(Tab customConsoleTab) {
        XMLDatabaseConsoleCustom customConsole = new XMLDatabaseConsoleCustom();
        VBox content = customConsole.createContent();
        customConsoleTab.setContent(content);
    }
}
