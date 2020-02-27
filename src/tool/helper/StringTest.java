package tool.helper;

public class StringTest {
	String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		System.out.println("text added: " + this.text);
		System.out.println("text length: " + this.text.length());
	}

	public void printOut() {System.out.println(text);}
	
	public void printErr() {System.err.println(text);}
	
}
