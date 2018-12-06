package tool.clients.fmmlxdiagrams;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.clients.workbench.WorkbenchClient;
import xos.Value;

public class FmmlxDiagramCommunicator {
	private int handler;
	private JButton button;
	public String test() {return "Test works!";}
	
	public void setHandle(final int handler) {
		this.handler = handler;
		System.err.println("handler="+handler);
		JFrame f = new JFrame("TestFrame");
		f.setSize(300, 300);
		f.setLocation(800, 100);
		JPanel p = new JPanel();
		button = new JButton("Test");
		p.add(button);
		f.setContentPane(p);
		f.setVisible(true);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonvalue++;
			    WorkbenchClient.theClient().send(handler, "fibo", new Value(buttonvalue));
			}
		});	
	}
	
	int buttonvalue = 0;
	
	public void sendPackageToJava(Object o) {
		if(o instanceof java.util.Vector){
			@SuppressWarnings("rawtypes")
			java.util.Vector v = (java.util.Vector) o;
//			if(v.type == Value.VECTOR) {
				if(v.get(0).equals("fibo")) {
					button.setText(v.get(1)+"");
				}
//			} 
		}
		System.err.println("o: " + o + "(" + o.getClass() + ")");
	}
	
//	private void paintPackage(String packageId) {
//		String name = client.getPackageName(packageId);
//	}
}
 