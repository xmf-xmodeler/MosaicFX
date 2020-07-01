package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.Vector;

import javafx.collections.ObservableList;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class DeleteEnumerationDialogResult {
	
	private ObservableList<FmmlxEnum> enumList;
	
	public DeleteEnumerationDialogResult(ObservableList<FmmlxEnum> observableList) {
		this.enumList=observableList;
	}

	public Vector<String> getEnumList() {
		if (enumList!=null) {
			Vector<String> result= new Vector<String>();
			for (FmmlxEnum tmp : enumList) {
				result.add(tmp.getName());
			}
			return result;
		}
		return null;
	}

}
