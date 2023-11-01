package tool.clients.fmmlxdiagrams.dialogs.stringandvalue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import java.util.ArrayList;

public class AllValueList {
	public static final ObservableList<Integer> levelList = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5);
	public static final ObservableList<String> traditionalTypeList = FXCollections.observableArrayList("Integer", "String", "Float", "Boolean");
	public static final ObservableList<String> booleanList = FXCollections.observableArrayList("true", "false");
	
	public static final ObservableList<Integer> getLevelInterval(FmmlxObject object) {
		int maxLevel = object.getLevel().getMinLevel();// != null?object.getLevel().getMinLevel():2;
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
	
	public static final ObservableList<String> getTraditionalTypeList() {
	
		return traditionalTypeList;
	}
	
	
}
