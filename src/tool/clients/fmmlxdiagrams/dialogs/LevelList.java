package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.ArrayList;

public class LevelList {
	public static final ObservableList<Integer> levelList = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5);

	public static final ObservableList<Integer> getLevelInterval(FmmlxObject object) {
		int maxLevel = object.getLevel()-1;
		int startInt = 0;
		
		ArrayList<Integer> levelArrayList = new ArrayList<Integer>();
		while (startInt <= maxLevel) {
			levelArrayList.add(startInt);
			startInt++;
		}

		return FXCollections.observableList(levelArrayList);
	}

	public static final ObservableList<Integer> generateLevelListToThreshold(int start, int threshold) {
		ArrayList<Integer> levelList = new ArrayList<>();
		while (start < threshold) {
			levelList.add(start);
			start++;
		}
		return FXCollections.observableList(levelList);
	}
}
