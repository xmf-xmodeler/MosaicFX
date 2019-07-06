package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class LevelList {
	public static final ObservableList<Integer> levelList = FXCollections.observableArrayList(1, 2, 3, 4, 5);

	public static final ObservableList<Integer> getLevelInterval(FmmlxObject object) {
		int maxLevel = object.getLevel();
		int startInt = 1;
		
		ArrayList<Integer> levelArrayList = new ArrayList<Integer>();
		while(startInt<=maxLevel) {
			levelArrayList.add(startInt);
			startInt++;
		}
		
		return FXCollections.observableList(levelArrayList);
	}
}
