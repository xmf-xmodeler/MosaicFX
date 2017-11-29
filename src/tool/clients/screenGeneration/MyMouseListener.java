package tool.clients.screenGeneration;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

class MyMouseListener implements MouseListener {
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if(e.widget instanceof Text){
			Text t = (Text) e.widget;
			System.out.println("Selected text: "+t.getText());
		}else if(e.widget instanceof List){
			List l = (List) e.widget;
			System.out.println("Selected list element: "+l.getSelection()[0]);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {				
	}

	@Override
	public void mouseUp(MouseEvent e) {	
	}
}