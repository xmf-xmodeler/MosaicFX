package tool.clients.screenGeneration;

import java.util.ArrayList;

import xos.Message;
import xos.Value;

public class Contentbox extends PlacableCommandableScreenElement{
	
//	private Group group;
	private ArrayList<Content> contents = new ArrayList<Content>();

	public Contentbox(String id, Object c, String headline) {
		super(id);
//		//group = new Group(c,SWT.NONE);
//		//group.setText(headline);
//		
//		Group group = new Group(c,SWT.NONE);
//		group.setText(headline);
//		content = group;
//		
//		GridLayout fl = new GridLayout(2,false);
//		fl.horizontalSpacing = 10;
//		fl.verticalSpacing = 10;
//		fl.marginWidth = 10;
//		group.setLayout(fl);
	}

	public void addContent(Content c){
		contents.add(c);
	}

//	public Composite getComp() {
//		return content;
//	}

	public void addTextBox(String id, String name, String text){
		runOnDisplay(new Runnable() {
			public void run() {
				TextBox t = new TextBox(id, content, name, text);
				contents.add(t);
			}
		});
	}
	
	public void addCheckBox(String id, String name, boolean checked){
//		runOnDisplay(new Runnable() {
//			public void run() {
//				Checkbox t = new Checkbox(id, content, name, checked);
//				contents.add(t);
//			}
//		});
	}
	
	public void addSelectBox(String id, String name,String[] items){
		runOnDisplay(new Runnable() {
			public void run() {
				SelectBox t = new SelectBox(id, content, name, items);
				contents.add(t);
			}
		});
	}
	
	@Override
	public void sendMessage(Message message) {
		if (message.hasName("addTextBox"))
			addTextBox(message);
		else if (message.hasName("addCheckBox"))
			addCheckBox(message);
		else if (message.hasName("addSelectBox"))
			addSelectBox(message);
		else
			super.sendMessage(message);
	}
	
	public void addTextBox(Message message){
		String id = message.args[0].strValue();
		String label = message.args[1].strValue();
		String text = message.args[2].strValue();
		addTextBox(id, label, text);
	}
	
	public void addCheckBox(Message message){
		String id = message.args[0].strValue();
		String label = message.args[1].strValue();
		boolean checked = message.args[2].boolValue;
		addCheckBox(id, label, checked);
	}
	
	public void addSelectBox(Message message){
		String id = message.args[0].strValue();
		String name = message.args[1].strValue();
		Value[] values = message.args[2].values;
		String[] items = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			items[i] = values[i].strValue();
		}
		addSelectBox(id, name, items);
	}
	
}
