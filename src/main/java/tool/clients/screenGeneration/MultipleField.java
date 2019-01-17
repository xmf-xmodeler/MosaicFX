package tool.clients.screenGeneration;

import xos.Message;
import xos.Value;

public class MultipleField extends CommandableScreenElement{
	
	private String name;
	private String[] values;

	public MultipleField(String id, String name, String[] values) {
		super(id);
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public String[] getValues() {
		return values;
	}
	
	@Override
	public void sendMessage(Message message) {
		super.sendMessage(message);
	}

	@Override
	public Value callMessage(Message message) {
		// TODO Auto-generated method stub
		return super.callMessage(message);
	}
}
