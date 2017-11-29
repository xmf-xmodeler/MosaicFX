package tool.clients.screenGeneration;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import xos.Value;

public class MultilevelEditContainer extends CommandableScreenElement{// PlacableCommandableScreenElement{

	ArrayList<TextBox> singleFieldList = new ArrayList<TextBox>();
	
	private ArrayList<Content> contents_left = new ArrayList<Content>();
	private ArrayList<Contentbox> contentboxes = new ArrayList<Contentbox>();
	private ArrayList<Content> contents_right= new ArrayList<Content>();
//	private Contentbox navigation;
	
	private Composite content;
	private ScrolledComposite scrollContent ;
	private Composite comp_left;
	private Composite comp_right;
	private Group slots;
	private Group navigation;
	
	public MultilevelEditContainer(String id, CTabFolder tabFolder, CTabItem tabItem) {
		super(id);
//		scrollContent = new ScrolledComposite(c, SWT.V_SCROLL);
		
		scrollContent = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		scrollContent.setVisible(true);
		scrollContent.setExpandHorizontal(true);
//		scrollContent.setExpandVertical(true);
		
		content = new Composite(scrollContent, SWT.NONE);
		scrollContent.setContent(content);
		
		FillLayout contentLayout = new FillLayout();
		contentLayout.type = SWT.HORIZONTAL;
		contentLayout.spacing = 10;
		content.setLayout(contentLayout);
		
//		GridData gd = new GridData();
//		gd.grabExcessHorizontalSpace = true;
		
		comp_left = new Composite(content, SWT.NONE);
//		RowLayout Layout_left = new RowLayout();
		FillLayout Layout_left = new FillLayout();
//		Layout_left.fill = true;
//		Layout_left.justify = true;
//		Layout_left.wrap = false;
		Layout_left.type = SWT.VERTICAL;
		Layout_left.spacing = 10;
		//GridLayout Layout_left = new GridLayout(2, false);
		comp_left.setLayout(Layout_left);
//		comp_left.setLayoutData(gd);
		
		slots = new Group(comp_left, SWT.NONE);
		slots.setText("Slots");
		GridLayout fl = new GridLayout(2,false);
		slots.setLayout(fl);
//		slots.setLayoutData(gd);
		
		comp_right = new Composite(content, SWT.NONE);
		FillLayout Layout_right = new FillLayout();
		Layout_right.type = SWT.VERTICAL;
		Layout_right.spacing = 10;
		comp_right.setLayout(Layout_right);
		
		navigation = new Group(comp_right, SWT.NONE);
		navigation.setText("Navigation");
		GridLayout gln = new GridLayout(2,false);
		navigation.setLayout(gln);
		
		tabItem.setControl(scrollContent);
	}
	

	public void addContent(Contentbox contentElement){
		contentboxes.add(contentElement);
//		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public Composite getNavigationComp() {
		return comp_right;
	}

	public Composite getContentComp() {
		return comp_left;
	}
	
	public CommandableScreenElement createNavigationClass(String id, String className){
//		runOnDisplay(new Runnable() {
//			public void run() {
				TextBox mclass = new TextBox(id, navigation, "Class:", className);
				mclass.setEditable(false);
				contents_right.add(mclass);
				comp_right.setSize(comp_right.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
		return mclass;
	}
	
	public CommandableScreenElement createNavigationInstances(String id, String[] instances){
//		runOnDisplay(new Runnable() {
//			public void run() {
				Content mclass = new SelectBox(id, navigation, "Instances:", instances);
				contents_right.add(mclass);
				comp_right.setSize(comp_right.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
		return mclass;
	}
	
	public CommandableScreenElement createNavigationSiblings(String id, String[] siblings){
//		runOnDisplay(new Runnable() {
//			public void run() {
				Content mclass = new SelectBox(id, navigation, "Siblings:", siblings);
				contents_right.add(mclass);
				comp_right.setSize(comp_right.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
				return mclass;
	}
	
	public CommandableScreenElement addContent(String id, String headline){
//		runOnDisplay(new Runnable() {
//			public void run() {
				Contentbox mclass = new Contentbox(id, comp_left, headline);
				contentboxes.add(mclass);
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//				scrollContent.setContent(content);
//			}
//		});
				return mclass;
	}
	
	public CommandableScreenElement addTextBox(String id, String name, String text){
//		runOnDisplay(new Runnable() {
//			public void run() {
				TextBox t = new TextBox(id, slots, name, text);
				contents_left.add(t);
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
				return t;
	}
	
	public CommandableScreenElement addComplexElementBox(String id, String name, String text){
				TextBox t = new TextBox(id, slots, name, text);
				t.setEditable(false);
				contents_left.add(t);
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				return t;
	}
	
	public CommandableScreenElement addCheckBox(String id, String name, boolean checked){
//		runOnDisplay(new Runnable() {
//			public void run() {
				Checkbox t = new Checkbox(id, slots, name, checked);
				contents_left.add(t);
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
		return t;
	}
	
	public CommandableScreenElement addSelectBox(String id, String name,String[] items){
//		runOnDisplay(new Runnable() {
//			public void run() {
				SelectBox t = new SelectBox(id, slots, name, items);
				contents_left.add(t);
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
		return t;
	}
	
	public CommandableScreenElement createNewElement(String message, String id, Value[] values){
		if (message.equals("createNavigationClass"))
			return createNavigationClass(id, values);
		else if (message.equals("createNavigationInstances"))
			return createNavigationInstances(id, values);
		else if (message.equals("createNavigationSiblings"))
			return createNavigationSiblings(id, values);
		else if (message.equals("addContent"))
			return addContent(id, values);
		else if (message.equals("addTextBox"))
			return addTextBox(id, values);
		else if (message.equals("addComplexElementBox"))
			return addComplexElementBox(id, values);
		else if (message.equals("addCheckBox"))
			return addCheckBox(id, values);
		else if (message.equals("addSelectBox"))
			return addSelectBox(id, values);
		else
			return super.createNewElement(message, id, values);
		
	}
	
//	@Override
//	public void sendMessage(Message message) {
//		if (message.hasName("createNavigationClass"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//				createNavigationClass(message);
//				}});
//		else if (message.hasName("createNavigationInstances"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			createNavigationInstances(message);
//				}});
//		else if (message.hasName("createNavigationSiblings"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			createNavigationSiblings(message);
//				}});
//		else if (message.hasName("addContent"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			addContent(message);
//				}});
//		else if (message.hasName("messageForward"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			messageForward(message);
//				}});
//		else if (message.hasName("addTextBox"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			addTextBox(message);
//				}});
//		else if (message.hasName("addCheckBox"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			addCheckBox(message);
//				}});
//		else if (message.hasName("addSelectBox"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			addSelectBox(message);
//				}});
//		else
//			super.sendMessage(message);
//
//	}

//	private void messageForward(Message message) {
//		String id = message.args[0].strValue();
//		for (Content content : contents_left) {
//			if (content.getId().equals(id)) {
//				content.sendMessage(ScreenGenerationClient.extractMessage(message));
//			}
//		}
//		for (Content content : contents_right) {
//			if (content.getId().equals(id)) {
//				content.sendMessage(ScreenGenerationClient.extractMessage(message));
//			}
//		}
//		for (Contentbox contentbox : contentboxes) {
//			if (contentbox.getId().equals(id)) {
//				contentbox.sendMessage(ScreenGenerationClient.extractMessage(message));
//			}
//		}
//		runOnDisplay(new Runnable() {
//			public void run() {
//				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
//	}
	
	private CommandableScreenElement createNavigationClass(String id, Value[] values) {
		String className = values[0].strValue();
		return createNavigationClass(id, className);
	}

//	private void createNavigationClass(Message message) {
//		String id = message.args[0].strValue();
//		String className = message.args[1].strValue();
//		createNavigationClass(id, className);
//	}
	
	private CommandableScreenElement createNavigationInstances(String id, Value[] values) {
		String[] instances = new String[values[0].values.length];
		for (int i = 0; i < values[0].values.length; i++) {
			instances[i] = values[0].values[i].strValue();
		}
		return createNavigationInstances(id, instances);
	}
	
//	private void createNavigationInstances(Message message) {
//		String id = message.args[0].strValue();
//		Value[] values = message.args[1].values;
//		System.out.println(id);
//		System.out.println(values);
//		String[] instances = new String[values.length];
//		for (int i = 0; i < values.length; i++) {
//			instances[i] = values[i].strValue();
//		}
//		createNavigationInstances(id, instances);
//	}
	
	private CommandableScreenElement createNavigationSiblings(String id, Value[] values) {
		String[] siblings = new String[values[0].values.length];
		for (int i = 0; i < values[0].values.length; i++) {
			siblings[i] = values[0].values[i].strValue();
		}
		return createNavigationSiblings(id, siblings);
	}
	
//	private void createNavigationSiblings(Message message) {
//		String id = message.args[0].strValue();
//		Value[] values = message.args[1].values;
//		String[] siblings = new String[values.length];
//		for (int i = 0; i < values.length; i++) {
//			siblings[i] = values[i].strValue();
//		}
//		createNavigationSiblings(id, siblings);
//	}

	private CommandableScreenElement addContent(String id, Value[] values) {
		String headline = values[0].strValue();
		return addContent(id, headline);
	}
	
//	private void addContent(Message message) {
//		String id = message.args[0].strValue();
//		String headline = message.args[1].strValue();
//		addContent(id, headline);
//	}
	
	public CommandableScreenElement addTextBox(String id, Value[] values){
		String label = values[0].strValue();
		String text = values[1].strValue();
		return addTextBox(id, label, text);
	}
	
	public CommandableScreenElement addComplexElementBox(String id, Value[] values){
		String label = values[0].strValue();
		String text = values[1].strValue();
		return addComplexElementBox(id, label, text);
	}
//	public void addTextBox(Message message){
//		String id = message.args[0].strValue();
//		String label = message.args[1].strValue();
//		String text = message.args[2].strValue();
//		addTextBox(id, label, text);
//	}
	
	public CommandableScreenElement addCheckBox(String id, Value[] values){
		String label = values[0].strValue();
		boolean checked = values[1].boolValue;
		return addCheckBox(id, label, checked);
	}
	
//	public void addCheckBox(Message message){
//		String id = message.args[0].strValue();
//		String label = message.args[1].strValue();
//		boolean checked = message.args[2].boolValue;
//		addCheckBox(id, label, checked);
//	}
	
	public CommandableScreenElement addSelectBox(String id, Value[] values){
		String name = values[0].strValue();
		Value[] boxValues = values[1].values;
		String[] items = new String[boxValues.length];
		for (int i = 0; i < boxValues.length; i++) {
			items[i] = boxValues[i].strValue();
		}
		return addSelectBox(id, name, items);
	}
	
//	public void addSelectBox(Message message){
//		String id = message.args[0].strValue();
//		String name = message.args[1].strValue();
//		Value[] values = message.args[2].values;
//		String[] items = new String[values.length];
//		for (int i = 0; i < values.length; i++) {
//			items[i] = values[i].strValue();
//		}
//		addSelectBox(id, name, items);
//	}
}
