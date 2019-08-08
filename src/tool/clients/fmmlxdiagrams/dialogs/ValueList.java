package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ValueList {
	public static final ObservableList<Integer> valueList = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5);

	public static final ObservableList<Integer> getValueInterval(Integer minValue) {	
		int startInt = minValue;
		int maxValue = 5;
		
		ArrayList<Integer> valueArrayList = new ArrayList<Integer>();
		while (startInt <= maxValue) {
			valueArrayList.add(startInt);
			startInt++;
		}

		return FXCollections.observableList(valueArrayList);
	}


}
