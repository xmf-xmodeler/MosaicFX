package tool.clients.screenGeneration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import xos.Message;
import xos.Value;

public class TextBox extends Content implements ModifyListener, MouseListener{
	
	private Label label;
	private Text textfield;
	
	public TextBox(String id, Composite c,String name, String text) {
		super(id);
		GridData rd = new GridData();
		
		label = new Label(c, SWT.NONE);
		label.setText(name);
		
		rd.grabExcessHorizontalSpace = true;
		textfield = new Text(c, SWT.BORDER); 
		textfield.setText(text);
		textfield.setLayoutData(rd);
		textfield.addModifyListener(this);
		textfield.addMouseListener(this);
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
						
		textfield.removeModifyListener(this);
		textfield.removeMouseListener(this);
		textfield.setText(value);
		textfield.addModifyListener(this);
		textfield.addMouseListener(this);
	}

	public void setEditable(boolean editable){
		textfield.setEditable(editable);
	}
	
	@Override
	public void modifyText(ModifyEvent arg0) {
		Message m = ScreenGenerationClient.theClient().getHandler().newMessage("command", 3);
		m.args[0] = new Value(this.getId());
		m.args[1] = new Value("valueChanged");
		Value[] values = new Value[1];
		values[0] = new Value(this.textfield.getText());		
		m.args[2] = new Value(values);
		ScreenGenerationClient.theClient().getHandler().raiseEvent(m);
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		Message m = ScreenGenerationClient.theClient().getHandler().newMessage("command", 3);
		m.args[0] = new Value(this.getId());
		m.args[1] = new Value("doubleClick");
		m.args[2] = new Value(new Value[0]);
		ScreenGenerationClient.theClient().getHandler().raiseEvent(m);
	}

	@Override
	public void mouseDown(MouseEvent arg0) {
		//Nothing
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		//Nothing		
	}
	
	
	
}
