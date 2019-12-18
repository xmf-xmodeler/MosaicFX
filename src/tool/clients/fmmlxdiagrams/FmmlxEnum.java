package tool.clients.fmmlxdiagrams;

import java.util.Collections;
import java.util.Vector;

public class FmmlxEnum {

	private final String name;
	private final Vector<String> items;
	
	public FmmlxEnum(String name, Vector<String> items) {
		super();
		this.name = name;
		this.items = items;
		Collections.sort(this.items);
	}

	public String getName() {
		return name;
	}

	public Vector<String> getItems() {
		return items;
	}
}
