package tool.clients.screenGeneration;

import xos.Message;
import xos.Value;

public class SingleField extends CommandableScreenElement{

	public SingleField(String id, Object c, String name, String text) {
		super(id);
////		GridData rd = new GridData(GridData.FILL_HORIZONTAL);
//	
//		label = new Label(c, SWT.NONE);
//		label.setText(name);
//						
//		textfield = new Text(c, SWT.BORDER); 
//		textfield.setText(text);
////		textfield.setLayoutData(rd);
//		textfield.addModifyListener(this);
//		//textfield.addMouseListener(new MyMouseListener());
	}

	public void changeText(String text){
//		textfield.setText(text);
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

//	@Override
//	public void modifyText(ModifyEvent me) {
//		Message m = ScreenGenerationClient.theClient().getHandler().newMessage("textChanged", 2);
//		m.args[0] = new Value(this.getId());
//		m.args[1] = new Value(this.textfield.getText());
//		ScreenGenerationClient.theClient().getHandler().raiseEvent(m);
//	
//	}
	
	
}
