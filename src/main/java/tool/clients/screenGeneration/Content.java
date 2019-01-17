package tool.clients.screenGeneration;

import xos.Value;

public abstract class Content extends CommandableScreenElement  {

	public Content(String id) {
		super(id);
	}

	public void command(String message, Value[] values){
		if (message.equals("changeValue")){
			changeValue(values);
		}else
			super.command(message, values);
		
	}
	
	public abstract void changeValue(Value[] values);
}
