package tool.clients.screenGeneration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import xos.Message;
import xos.Value;

public class SelectBox extends Content implements MouseListener{
	
	private Label label;
	private List list;
	
	public SelectBox(String id, Composite c,String name, String[] items) {
		super(id);
		GridData rd = new GridData(GridData.FILL_HORIZONTAL);
		rd.horizontalSpan = 2;

		label = new Label(c, SWT.NONE);
		label.setText(name);
		label.setLayoutData(rd);
		
		list = new List(c, SWT.SINGLE | SWT.BORDER);
		list.setItems(items);
		rd = new GridData(GridData.FILL_BOTH);
		rd.horizontalSpan = 2;
		list.setLayoutData(rd);
		list.addMouseListener(this);
		
		list.setSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT).x,list.getItemHeight()*3);
	}

	@Override
	public void changeValue(Value[] values) {
		String[] strValues = new String[values[0].values.length];
		for (int i = 0; i < values[0].values.length; i++) {
			strValues[i] = values[0].values[i].strValue();
		}
		list.setItems(strValues);				
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		Message m = ScreenGenerationClient.theClient().getHandler().newMessage("command", 3);
		m.args[0] = new Value(this.getId());
		m.args[1] = new Value("doubleClick");
		m.args[2] = new Value(new Value[]{new Value(this.list.getSelection()[0])});
		ScreenGenerationClient.theClient().getHandler().raiseEvent(m);
	}

	@Override
	public void mouseDown(MouseEvent arg0) {
		//nothing
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		//nothing
	}
}
