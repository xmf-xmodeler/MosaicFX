package tool.helper;

public class StringCheck {
	
	public boolean contains(String text, String sample) {
		return text.contains(sample);
	}
	
	public Integer indexOf(String text, String sample) {
		return text.contains(sample)?null:text.indexOf(sample);
	}

}
