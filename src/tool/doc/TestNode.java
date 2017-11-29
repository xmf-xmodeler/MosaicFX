package tool.doc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.w3c.dom.Node;


import javax.swing.GroupLayout.Alignment;

public class TestNode extends MyTreeNode{
	private static final long serialVersionUID = 1L;
	
	public TestNode(String userObject) {
		super(userObject);
		title = userObject;
	}	
	
	public TestNode(Node node) {
		super(node.getAttributes().getNamedItem("name").getNodeValue());
		title = node.getAttributes().getNamedItem("name").getNodeValue();
		load(node);
	}

	public String getType() {
		return "test";
	}
	
	String title;
	double priority;
	long lastTestedOn;
	String lastResult;
	
	String preConditions;
	String actions;
	String postconditions;
	boolean hasProblem;
	
	public ImageIcon getIcon(Image defaultIcon) {
		try {
			Image icon = new ImageIcon("icons/Forms/List.gif").getImage();
			if(hasProblem) icon = MyTreeCellRenderer.addProblem(icon).getImage();
			if(checkIsDue()) icon = MyTreeCellRenderer.addClock(icon).getImage();
			return new ImageIcon(icon);
		} catch (Exception e) {
			e.printStackTrace();
			return new ImageIcon("icons/Tools/Delete.gif") ;
		}
 	}
	
	protected boolean checkIsDue() {
		return System.currentTimeMillis() - lastTestedOn > 1000000000;
	}

	protected boolean hasProblem() {
		return hasProblem;
	}

	
	public JPanel createPanel() {
		TestPanel p = new TestPanel();
			
		return p;
	}
	
	JTextArea preField;
	JTextArea actionField;
	JTextArea postField;
	
	JTextField lastTestedOnField;
	JTextArea lastTestedResultField;
	JTextField priorityField;
		
		private class TestPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private TestPanel() {
			preField = new JTextArea(preConditions);
			actionField = new JTextArea(actions);
			postField = new JTextArea(postconditions);

			lastTestedOnField = new JTextField();
			lastTestedOnField.setEditable(false);
			setlastTestedOnDate();
			lastTestedResultField = new JTextArea(lastResult);
			priorityField = new JTextField(priority+"");

			JScrollPane preScroll = new JScrollPane(preField);
			JScrollPane actionScroll = new JScrollPane(actionField);
			JScrollPane postScroll = new JScrollPane(postField);
			JScrollPane resultScroll = new JScrollPane(lastTestedResultField);

			JLabel preLabel = new JLabel("Preconditions");
			JLabel actionLabel = new JLabel("Action");
			JLabel postLabel = new JLabel("Postconditions");

			JLabel lastTestedOnLabel = new JLabel("Last tested");
			JLabel lastTestedResultLabel = new JLabel("Result");
			JLabel priorityLabel = new JLabel("Priority");

			JButton reportTestButton = new JButton("Report Test Result");
			
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			final int GAP = 3;
			

			final int BOXWIDTH = 470;
			
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addComponent(preLabel)
							.addComponent(actionLabel)
							.addComponent(postLabel)
							.addComponent(lastTestedOnLabel)
							.addComponent(lastTestedResultLabel)
							.addComponent(priorityLabel))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addComponent(preScroll, BOXWIDTH, BOXWIDTH, BOXWIDTH)
							.addComponent(actionScroll, BOXWIDTH, BOXWIDTH, BOXWIDTH)
							.addComponent(postScroll, BOXWIDTH, BOXWIDTH, BOXWIDTH)
							.addComponent(lastTestedOnField, BOXWIDTH/3, BOXWIDTH/3, BOXWIDTH/3)
							.addComponent(resultScroll, BOXWIDTH, BOXWIDTH, BOXWIDTH)
							.addComponent(priorityField, BOXWIDTH/3, BOXWIDTH/3, BOXWIDTH/3)
							.addComponent(reportTestButton))
					.addGap(GAP)
					);
			
			final int BOXHEIGHT = 80;
			
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(preLabel)
							.addComponent(preScroll, BOXHEIGHT, BOXHEIGHT, BOXHEIGHT))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(actionLabel)
							.addComponent(actionScroll, BOXHEIGHT, BOXHEIGHT, BOXHEIGHT))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(postLabel)
							.addComponent(postScroll, BOXHEIGHT, BOXHEIGHT, BOXHEIGHT))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lastTestedOnLabel)
							.addComponent(lastTestedOnField))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lastTestedResultLabel)
							.addComponent(resultScroll, BOXHEIGHT, BOXHEIGHT, BOXHEIGHT))
					.addGap(GAP)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
							.addComponent(priorityLabel)
							.addComponent(priorityField))
					.addGap(GAP)
					.addComponent(reportTestButton)
					.addGap(GAP)
					);
			
			reportTestButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(
							TestPanel.this, 
							"Were the results as predicted?", 
							"Report Test Result", 
							JOptionPane.YES_NO_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE, null);
					switch(result) {
					case JOptionPane.YES_OPTION : {
						lastTestedOn = System.currentTimeMillis();
						setlastTestedOnDate();
						lastResult = "Success";
						lastTestedResultField.setText(lastResult);
						hasProblem = false;
						break;
					}
					case JOptionPane.NO_OPTION : {
						lastTestedOn = System.currentTimeMillis();
						setlastTestedOnDate();
						lastResult = "Fail";
						lastTestedResultField.setText(lastResult);
						hasProblem = true;
						break;
					}
					}
					
				}
			});
		}

		private void setlastTestedOnDate() {
			lastTestedOnField.setText(new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(new Date(lastTestedOn)));			
		}
		
	}
	
	@Override
		public String toString() {
			return title;
		}

	public void storeValues() {
		try{
			priority = Double.parseDouble(priorityField.getText());
		} catch (Exception e) {priority = 1; System.err.println("\t\t\t\tNumber not recognized.");}
		
		lastResult = lastTestedResultField.getText();
		preConditions = preField.getText();
		actions = actionField.getText();
		postconditions = postField.getText();
	}
	
	public void save(PrintStream out) {
		out.print(" preConditions = \""+XMLHelper.protectSpecialCharacters(preConditions)+"\"");
		out.print(" actions = \""+XMLHelper.protectSpecialCharacters(actions)+"\"");
		out.print(" postconditions = \""+XMLHelper.protectSpecialCharacters(postconditions)+"\"");
		out.print(" lastResult = \""+XMLHelper.protectSpecialCharacters(lastResult)+"\"");
		out.print(" priority = \""+priority+"\"");
		out.print(" lastTestedOn = \""+lastTestedOn+"\"");
		out.print(" hasProblem = \""+(hasProblem?"YES":"NO")+"\"");
	}
	
	public void load(Node node) {
		preConditions = node.getAttributes().getNamedItem("preConditions").getNodeValue();
		actions = node.getAttributes().getNamedItem("actions").getNodeValue();
		postconditions = node.getAttributes().getNamedItem("postconditions").getNodeValue();
		lastResult = node.getAttributes().getNamedItem("lastResult").getNodeValue();
		priority = Double.parseDouble(node.getAttributes().getNamedItem("priority").getNodeValue());
		lastTestedOn = Long.parseLong(node.getAttributes().getNamedItem("lastTestedOn").getNodeValue());
		hasProblem = node.getAttributes().getNamedItem("hasProblem") != null && "YES".equals(node.getAttributes().getNamedItem("hasProblem").getNodeValue());
	}
	
	public void setName(String name2) {
		title = name2;
	}
}
