package tool.clients.fmmlxdiagrams.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import tool.clients.diagrams.Label;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.dialogs.results.ShowCertainLevelDialogResult;

public class ShowCertainLevelDialog extends CustomDialog<ShowCertainLevelDialogResult>{

    private Label chooseLevel;
    private ListView<String> levelListView;

    private AbstractPackageViewer diagram;

    public ShowCertainLevelDialog(AbstractPackageViewer diagram) {
        super();
        this.diagram = diagram;

        DialogPane dialogPane = new DialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialogPane.setHeaderText("Show Certain Level");
        
    }
}
