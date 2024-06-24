package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RenameProjektDialog {
    public void start() {
    	Stage primaryStage = new Stage();
        // Erstelle ein Label als Eingabeaufforderung
        Label promptLabel = new Label("Enter new project name:");

        // Erstelle ein Textfeld f�r die Benutzereingabe
        TextField inputField = new TextField();

        // Erstelle einen OK-Button
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            // Handle die Eingabe und schlie�e das Fenster oder f�hre andere Aktionen aus
            String newProjectName = inputField.getText();
            System.out.println("New project name: " + newProjectName);
            // Hier kannst du die weitere Logik hinzuf�gen, z.B. das Senden des neuen Namens an einen Server oder das Aktualisieren des Projekts.
            primaryStage.close();
        });

        // Erstelle ein Layout und f�ge die Steuerelemente hinzu
        VBox layout = new VBox(10); // 10 ist der Abstand zwischen den Elementen
        layout.getChildren().addAll(promptLabel, inputField, okButton);

        // Erstelle eine Szene und f�ge sie zur B�hne hinzu
        Scene scene = new Scene(layout, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rename Project");
        primaryStage.show();
    }
    
    private void setNewName(String newName)
    {
    	
    }

}
