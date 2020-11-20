package tool.xmodeler;

import java.util.Vector;

public class ControlCenterClient {

	public Vector<String> getAllCategories() {
		Vector<String> v = new Vector<String>();
		v.add("Sales");
		v.add("Buying");
		return v;
	}

}
