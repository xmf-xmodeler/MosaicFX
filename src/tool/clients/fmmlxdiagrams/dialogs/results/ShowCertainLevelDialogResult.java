package tool.clients.fmmlxdiagrams.dialogs.results;

import javafx.collections.ObservableList;

import java.util.Vector;

public class ShowCertainLevelDialogResult {
    ObservableList<Integer> levels;

    public ShowCertainLevelDialogResult(ObservableList<Integer> levels) {
        this.levels = levels;
    }

    public Vector<Integer> getChosenLevels() {
        Vector<Integer> result = new Vector<>(levels);
        return result;
    }
}
