package tool.clients.fmmlxdiagrams.graphics;

import java.util.HashMap;

import javafx.scene.paint.Color;

public class Style {

	HashMap<String, String> styles = new HashMap<>();
	
	public Style(String style) {
		for(String s : style.split(";")) {
			String[] t = s.split(":");
			if(t.length==2){styles.put(t[0], t[1]);}
		}
	}

	public Color getFill() {
		String value = styles.get("fill");
		if ("none".equals(value)) {
			return Color.TRANSPARENT;
		}
		if (value!=null) {
			try { return Color.web(value);
			} catch(IllegalArgumentException e) {
				System.err.println("Color: " + value +  " not recognized!");
			}
		}
		return null;
	}
	
	public Color getStrokeColor() {
		String value = styles.get("stroke");
		if ("none".equals(value)) {
			return Color.TRANSPARENT;
		}
		if (value!=null) {
			try { return Color.web(value);
			} catch(IllegalArgumentException e) {
				System.err.println("Color: " + value +  " not recognized!");
			}
		}
		return null;
	}
	
}
