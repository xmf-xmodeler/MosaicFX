package tool.clients.screenGeneration;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import xos.Message;
import xos.Value;

public class EditContainer extends CommandableScreenElement implements SelectionListener {

	ArrayList<SingleField> singleFieldList = new ArrayList<SingleField>();
	ArrayList<MultipleField> multipleFieldList = new ArrayList<MultipleField>();

	private Combo combo;
	private List list;

	// private Composite content;
	private Composite comp_left;
	private Composite comp_right;

	private Composite content;
	private ScrolledComposite scrollContent ;
	
	public EditContainer(String id, CTabFolder tabFolder, CTabItem tabItem) {
		super(id);
		
		scrollContent = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		scrollContent.setVisible(true);
		scrollContent.setExpandHorizontal(true);
		
		content = new Composite(scrollContent, SWT.NONE);
//		content = new Composite(tabFolder, SWT.NONE);

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		fillLayout.spacing = 20;
		content.setLayout(fillLayout);

		comp_left = new Composite(content, SWT.NONE);
//		RowLayout Layout_left = new RowLayout();
//		Layout_left.type = SWT.VERTICAL;
//		Layout_left.fill = true;
		GridLayout Layout_left = new GridLayout(2, false);
		comp_left.setLayout(Layout_left);

		comp_right = new Composite(content, SWT.NONE);
		RowLayout Layout_right = new RowLayout();
		Layout_right.type = SWT.VERTICAL;
		Layout_right.fill = true;
		comp_right.setLayout(Layout_right);

		combo = new Combo(comp_right, SWT.READ_ONLY);
		combo.addSelectionListener(this);

		list = new List(comp_right, SWT.SINGLE + SWT.BORDER);
		list.setLayoutData(new RowData(400, 250));
		list.addMouseListener(new MyMouseListener());
		
//		tabItem.setControl(content);
		tabItem.setControl(scrollContent);
	}

	public void addSingleField(String id, String label, String text) {
		runOnDisplay(new Runnable() {
			public void run() {
				System.out.println("ID: "+id+", Label"+ label+", Text text" );
				singleFieldList.add(new SingleField(id, comp_left, label, text));
				comp_left.setSize(comp_left.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				comp_right.setSize(comp_right.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//				scrollContent.setContent(content);
			}
		});

	}

	public void addMultipleField(String id, String name, String[] values) {
		runOnDisplay(new Runnable() {
			public void run() {
				combo.add(name);
				if (multipleFieldList.isEmpty()) {
					combo.select(0);
					list.setItems(values);
				}
				multipleFieldList.add(new MultipleField(id, name, values));
				content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

	}

	public void changeText(String id, String text) {
		for (SingleField singleField : singleFieldList) {
			if (id.equals(singleField.getId())){
				
				runOnDisplay(new Runnable() {
					public void run() {
						singleField.changeText(text);
					}
				});
			}
		}
	}
	
	public Combo getCombo() {
		return combo;
	}


	public List getList() {
		return list;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		for (MultipleField m : multipleFieldList) {
			if (m.getName().equals(combo.getText())) {
				list.setItems(m.getValues());
			}
		}

	}

	@Override
	public void sendMessage(Message message) {
		if (message.hasName("addSingleElement"))
			addSingleElement(message);
		else if (message.hasName("addMultipleElement"))
			addMultipleElement(message);
		else if (message.hasName("changeText"))
			changeText(message);
		else
			super.sendMessage(message);
	}

	private void addSingleElement(Message message) {
		String id = message.args[0].strValue();
		String label = message.args[1].strValue();
		String text = message.args[2].strValue();
		addSingleField(id, label, text);
	}

	private void addMultipleElement(Message message) {
		String id = message.args[0].strValue();
		String name = message.args[1].strValue();
		Value[] values = message.args[2].values;
		String[] stringValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			stringValues[i] = values[i].strValue();
		}
		addMultipleField(id, name, stringValues);
	}

	private void changeText(Message message) {
		String id = message.args[0].strValue();
		String text = message.args[1].strValue();
		changeText(id, text);
	}
	
	@Override
	public Value callMessage(Message message) {
		return super.callMessage(message);
	}
}
