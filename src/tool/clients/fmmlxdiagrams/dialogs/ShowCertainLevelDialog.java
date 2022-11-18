package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;

import java.util.Vector;

public class ShowCertainLevelDialog extends CustomDialog<Vector<Integer>>{

    private ListView<Integer> levelListView;

    private final AbstractPackageViewer diagram;

    public ShowCertainLevelDialog(AbstractPackageViewer diagram) {
        super();
        this.diagram = diagram;

        DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setHeaderText("Show Certain Level");

        layout();

        dialogPane.setContent(flow);

        final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            if (!validateUserInput()) {
                e.consume();
            }
        });

        setResultConverter(dlgBtn -> {
            if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return new Vector<Integer>(levelListView.getSelectionModel().getSelectedItems());
            }
            return null;
        });
        
    }

    private boolean validateUserInput() {
        if(levelListView.getSelectionModel().getSelectedItems().size()==0){
            errorLabel.setText("Please select at least one");
            return false;
        }
        return true;
    }

    private void layout(){
        Vector<Integer> allLevels = diagram.getAllObjectLevel();

        levelListView = new ListView<>();
        levelListView.getItems().addAll(allLevels);
        levelListView.setEditable(false);
        levelListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        grid.add(levelListView, 0,0);
    }
}
