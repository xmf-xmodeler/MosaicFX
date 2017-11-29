package tool.clients.screenGeneration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import xos.Message;
import xos.Value;

public class Checkbox extends Content implements SelectionListener{
	
	private Button checkbox;
	
	public Checkbox(String id, Composite c,String name,Boolean checked) {
		super(id);
		checkbox = new Button(c, SWT.CHECK); 
		checkbox.setText(name);
		checkbox.setSelection(checked);
		GridData rd = new GridData();
		rd.horizontalSpan = 2;
		checkbox.setLayoutData(rd);
		checkbox.addSelectionListener(this);
	}

	@Override
	public void changeValue(Value[] values) {
		Boolean checked = values[0].boolValue;
		checkbox.setSelection(checked);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		Message m = ScreenGenerationClient.theClient().getHandler().newMessage("command", 3);
		m.args[0] = new Value(this.getId());
		m.args[1] = new Value("valueChanged");
		Value[] values = new Value[1];
		values[0] = new Value(this.checkbox.getSelection());		
		m.args[2] = new Value(values);
		ScreenGenerationClient.theClient().getHandler().raiseEvent(m);
		
	}



}
