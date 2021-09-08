package tool.clients.importer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class ConflictsDialog extends CustomDialog {
    private Label conflictTypeLabel;
    private Label descriptionLabel;
    private Label whereLabel;
    private ListView<Conflict> conflictTypeList;
    private ListView<Conflict> descriptionsListView;
    private ListView<Conflict> whereListView;
    private final List<Conflict> conflicts;

    public ConflictsDialog(List<Conflict> conflicts){
        super();
        this.conflicts = conflicts;

        DialogPane dialogPane = getDialogPane();
        dialogPane.setHeaderText(ImporterStrings.DIALOG_TITLE);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);

        setLayoutContent();
        dialogPane.setContent(flow);

    }

    private void setLayoutContent() {
        conflictTypeLabel = new Label(ImporterStrings.ConflictDialogString.TYPE);
        descriptionLabel = new Label(ImporterStrings.ConflictDialogString.DESCRIPTION);
        whereLabel = new Label(ImporterStrings.ConflictDialogString.WHERE);

        conflictTypeList = initializeConflictListView(getConflictTypeList(conflicts), SelectionMode.SINGLE, "t");
        conflictTypeList.setEditable(false);
        descriptionsListView = initializeConflictListView(getConflictTypeList(new ArrayList<>()), SelectionMode.SINGLE, "d");
        descriptionsListView.getItems().clear();
        descriptionsListView.setEditable(false);
        whereListView = initializeConflictListView(getConflictTypeList(new ArrayList<>()), SelectionMode.SINGLE, "w");
        whereListView.getItems().clear();
        whereListView.setEditable(false);

        conflictTypeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            descriptionsListView.getItems().clear();
            whereListView.getItems().clear();
            descriptionsListView.getItems().addAll(getConflictDescriptionByType(newValue));
            descriptionsListView.getSelectionModel().select(0);
        });
        descriptionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Conflict> whereList = showConflictWhereByDescription(newValue);
            whereListView.getItems().clear();
            whereListView.getItems().addAll(whereList);
        });
        conflictTypeList.getSelectionModel().select(0);

        List<Node> typeNode = new ArrayList<>();
        List<Node> descriptionNode = new ArrayList<>();
        List<Node> whereNode = new ArrayList<>();

        typeNode.add(conflictTypeLabel);
        typeNode.add(conflictTypeList);

        descriptionNode.add(descriptionLabel);
        descriptionNode.add(descriptionsListView);

        whereNode.add(whereLabel);
        whereNode.add(whereListView);

        addNodesToGrid(typeNode, 0);
        addNodesToGrid(descriptionNode, 1);
        addNodesToGrid(whereNode, 2);
    }

    private ObservableList<Conflict> showConflictWhereByDescription(Conflict neww) {
        List<Conflict> conflictList = new ArrayList<>();

        if(!this.conflicts.isEmpty() && neww!=null){
            for(Conflict conflict: this.conflicts){
                if(conflict.getType().equals(neww.getType()) && conflict.getDescription().equals(neww.getDescription())){
                    conflictList.add(conflict);
                }
            }
        }
        return FXCollections.observableArrayList(conflictList);
    }

    private ObservableList<Conflict> getConflictDescriptionByType(Conflict neww) {
        List<Conflict> conflictList = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();

        if(!this.conflicts.isEmpty()){
            for(Conflict conflict: this.conflicts){
                if(conflict.getType().equals(neww.getType()) && !descriptions.contains(conflict.getDescription())){
                    conflictList.add(conflict);
                    descriptions.add(conflict.getDescription());
                }
            }
        }
        return FXCollections.observableArrayList(conflictList);
    }

    private ObservableList<Conflict> getConflictTypeList(List<Conflict> conflicts) {
        List<Conflict> conflictList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();

        for(Conflict conflict: conflicts){
            if(!typeList.contains(conflict.getType())){
                conflictList.add(conflict);
                typeList.add(conflict.getType());
            }
        }
        return FXCollections.observableArrayList(conflictList);
    }
}
