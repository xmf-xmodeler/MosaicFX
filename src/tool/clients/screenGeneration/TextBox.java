package tool.clients.screenGeneration;

import xos.Message;
import xos.Value;

public class TextBox extends Content{
	
//	private Label label;
//	private Text textfield;
	
	public TextBox(String id, Object c,String name, String text) {
		super(id);
	}

	@Override
	public void changeValue(Value[] values) {
		String value;

		if (values[0].type == Value.INT)
			value = values[0].intValue +"" ;
		else if (values[0].type == Value.FLOAT)
			value = values[0].floatValue +""; 
		else if (values[0].type == Value.STRING)
			value = values[0].strValue();
		else value = "";

	}

	public void setEditable(boolean editable){
	}
	
	
}
