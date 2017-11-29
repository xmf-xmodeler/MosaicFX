package tool.clients.screenGeneration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;

import xos.Message;
import xos.Value;

public class Tab extends Window {

	private CTabItem tab;
	private ScrolledComposite content;
	private CommandableScreenElement rootElement;

	public Tab(final String id, final ScreenGenerationClient client, final int type, final String label,
			final boolean selected) {
		super(id, client);
//		runOnDisplay(new Runnable() {
//			public void run() {

				tab = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				tab.setText(label);

//				content = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//				content.setVisible(true);
//				content.setExpandHorizontal(true);
//				content.setExpandVertical(true);

//				FillLayout fillLayout1 = new FillLayout();
//				fillLayout1.type = SWT.HORIZONTAL;
//				content.setLayout(fillLayout1);

				tab.setControl(content);
				tab.setShowClose(true);
				if (selected)
					tabFolder.setSelection(tab);
//			}
//		});
	}

	public CommandableScreenElement newFormView(final String id) {
//		runOnDisplay(new Runnable() {
//			public void run() {
				rootElement = new EditContainer(id,tabFolder,tab);
//			}
//		});
				return rootElement;
	}

	public CommandableScreenElement newMultiView(final String id) {
//		runOnDisplay(new Runnable() {
//			public void run() {
				rootElement = new MultilevelEditContainer(id,tabFolder,tab);
//				content.setContent(rootElement.getContent());
//				rootElement.getContent().setSize(rootElement.getContent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//		});
				return rootElement;
	}
	
	public CommandableScreenElement createNewElement(String message, String id, Value[] values){
		if (message.equals("newFormView"))
			return newFormView(id);
		else if (message.equals("newMultiView"))
			return newMultiView(id);
		else
			return super.createNewElement(message,id,values);
		
	}
	
	public void command(String message, Value[] values){
		if (message.equals("refresh"))
			refresh();
		else if (message.equals("changeText"))
			changeText(values);
		else if (message.equals("close"))
			close();
		super.command(message, values);
	}
	
	public void changeText(Value[] values){
		String text = values[0].strValue();
		changeText(text);
	}
	
	public void changeText(String text){
		tab.setText(text);
	}
	
	public void close(){
		tab.dispose();
		
	}
	
//	@Override
//	public void sendMessage(Message message) {
//		if (message.hasName("testIt"))
//			System.out.println(message);
//		else if (message.hasName("newFormView"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			newFormView(message);
//				}});
//		else if (message.hasName("newMultiView"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			newMultiView(message);
//				}});
//		else if (message.hasName("refresh"))
//			runOnDisplay(new Runnable() {
//				public void run() {
//			refresh();
//				}});
//		else if (message.hasName("messageForward"))
//			messageForward(message);
//		else
//			super.sendMessage(message);
//	}

//	private void newFormView(Message message) {
//		String id = message.args[0].strValue();
//		newFormView(id);
//	}
//
//	private void newMultiView(Message message) {
//		String id = message.args[0].strValue();
//		newMultiView(id);
//	}
//	
//	private void messageForward(Message message) {
//		String id = message.args[0].strValue();
//		if (rootElement.getId().equals(id)) {
//			rootElement.sendMessage(ScreenGenerationClient.extractMessage(message));
//		}
//		runOnDisplay(new Runnable() {
//			public void run() {
//				System.out.println(rootElement.getContent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
//				rootElement.getContent().setSize(rootElement.getContent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
//				content.setContent(((MultilevelEditContainer)rootElement).getScroll());
//			}
//		});
//	}

	@Override
	public Value callMessage(Message message) {
		return super.callMessage(message);
	}

	@Override
	public void refresh() {
		if( content != null)
		
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		tab.setControl(content);
	}
	
	
}
