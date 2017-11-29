/**
 * 
 */
package tool.clients.screenGeneration;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;

/**
 * @author Bjoern
 *
 */
public class FormDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(900, 400);
		
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
						
		Label name_label;
		Text name_textfield;
		
		name_label = new Label(shell, SWT.NONE);
		name_label.setText("Name:");
						
		name_textfield = new Text(shell, SWT.BORDER); 
		name_textfield.setText("");

		Label firstname_label;
		Text firstname_textfield;
		
		firstname_label = new Label(shell, SWT.NONE);
		firstname_label.setText("Firstname:");
						
		firstname_textfield = new Text(shell, SWT.BORDER); 
		firstname_textfield.setText("");
		
		
		Button isVIP_checkbox;								
		isVIP_checkbox = new Button(shell, SWT.CHECK); 
		isVIP_checkbox.setText("isVIP");
		
		shell.open();

		// run the event loop as long as the window is open
		while (!shell.isDisposed()) {
		    // read the next OS event queue and transfer it to a SWT event
		    if (!display.readAndDispatch())
		     {
		    // if there are currently no other OS event to process
		    // sleep until the next OS event is available
		        display.sleep();
		     }
		}

		// disposes all associated windows and their components
		display.dispose();
	}

}
