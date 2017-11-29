package tool.console;

import java.util.Collections;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import xos.Message;
import xos.Value;

public class AutoCompleteBox extends Dialog {

	Vector<Suggestion> labels = new Vector<Suggestion>();
	Text searchField;
	List listOfSuggestions; 
	boolean searchFieldInitialised = false;
	String result = null;
	String oldKey = "";
	boolean warning = false;
	
	public AutoCompleteBox(Shell owner, Message message) {
		super(owner);
		
	    Value[] pairs = message.args[0].values;
	    for (Value value : pairs) {
	      Value[] pair = value.values;
	      String label = pair[1].strValue();
	      Suggestion newSuggestion = new Suggestion(label);
	      if(!labels.contains(newSuggestion)) labels.add(newSuggestion);
	    }
	    
//	    if(labels.size() <= 0) {
//	    	labels.add(new Suggestion("Aardvark"));
//	    	labels.add(new Suggestion("Bee"));
//	    	labels.add(new Suggestion("Cat"));
//	    	labels.add(new Suggestion("Dog"));
//	    	labels.add(new Suggestion("Elephant"));
//	    	labels.add(new Suggestion("Fox"));
//	    	labels.add(new Suggestion("Mouse"));
//	    	labels.add(new Suggestion("Unicorn"));
//	    	labels.add(new Suggestion("Wolpertinger"));
//	    	labels.add(new Suggestion("Yeti"));
//	    }
	    
	}

	
	public String show(Point displayPoint) {
		if(labels.size() <= 0) {return "";}
		
        Shell parent = getParent();
        Shell shell = new Shell(parent, SWT.RESIZE | SWT.APPLICATION_MODAL);//SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("getText()");
        shell.setSize(150, 200);
        shell.setLocation(displayPoint.x, displayPoint.y-250);
        
        shell.setLayout(new GridLayout(1, false));

        searchField = new Text(shell, SWT.SINGLE | SWT.BORDER);
        searchField.setText("Search here...");
        searchField.setForeground(new Color(Display.getCurrent (), 100, 100, 100));
        GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		searchField.setLayoutData(gridData);
		searchField.addKeyListener(new MySearchListener());
		
        listOfSuggestions = new List(shell, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		listOfSuggestions.setLayoutData(gridData);
		listOfSuggestions.addMouseListener(new MyListListener());
		
		addAllToListSortedBy("");
        
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed() && result == null) {
//        	System.err.println("sleeping: " + result);
            if (!display.readAndDispatch()) display.sleep();
        }
        if(result != null) shell.dispose();
        return result;
	}

	private void addAllToListSortedBy(String key) {
		listOfSuggestions.removeAll();
		Suggestion.key = key;
		Collections.sort(labels);
		for(int i = 0; i < labels.size(); i++) {
			if(labels.get(i).likelihood > .1) listOfSuggestions.add(labels.get(i).text);// + " (" + labels.get(i).likelihood + " " + labels.get(i).lastKey + ")");
		}
		
		if (listOfSuggestions.getItemCount() > 0) {
			listOfSuggestions.setSelection(0);
			listOfSuggestions.showSelection();
			warning = false;
		} else {
			if(warning) {
				result = searchField.getText();
			} else {
				warning = true;
			}
		}
		
		
	}

	private static class Suggestion implements Comparable<Suggestion> {
		final String text;
		Double likelihood = 1.;
		String lastKey;
		static String key;
		
		private Suggestion(String text) {
			this.text = text;
		}
		
		@Override
		public int compareTo(Suggestion that) {
			calculateLikelihood();
			that.calculateLikelihood();
			int c = -this.likelihood.compareTo(that.likelihood);
			if(c != 0) return c;
			return this.text.compareToIgnoreCase(that.text);
		}

		private void  calculateLikelihood() {
			if (key != lastKey) {
				likelihood = 0.;
				if (text.toLowerCase().contains(key.toLowerCase()))
					likelihood += .5;
				if (text.toLowerCase().startsWith(key.toLowerCase()))
					likelihood += .5;
				lastKey = key;
			} 
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Suggestion other = (Suggestion) obj;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}
	}
	
	private class MySearchListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(!searchFieldInitialised) {
				searchField.setForeground(new Color(Display.getCurrent (), 0, 0, 0));
				searchField.setText("");
				searchFieldInitialised = true;
			}
//			System.err.println(e.keyCode);
			if(e.keyCode == 0x01_00_00_01) { // UP 
				int index = listOfSuggestions.getSelectionIndex();
				index--;
				if(index >= 0 && index < listOfSuggestions.getItemCount()) {
					listOfSuggestions.setSelection(index);
				}
				e.doit = false;
			}
			if(e.keyCode == 0x01_00_00_02) { // DOWN 
				int index = listOfSuggestions.getSelectionIndex();
				index++;
				if(index >= 0 && index < listOfSuggestions.getItemCount()) {
					listOfSuggestions.setSelection(index);
				}
				e.doit = false;
			}
			if(e.keyCode == 0x01_00_00_50 || // ENTER 
					e.keyCode == 13) { // RETURN 
			    if(listOfSuggestions.getSelectionIndex() != -1) {
			    	result = listOfSuggestions.getSelection()[0];
			    } else {
			    	result = searchField.getText();
			    }
				e.doit = false;
			}
		}
	
		@Override 
		public void keyReleased(KeyEvent arg0) {
			if(searchFieldInitialised) {
				if(!oldKey.equals(searchField.getText())) {
					oldKey = searchField.getText();
					addAllToListSortedBy(oldKey);
					searchField.setForeground(new Color(Display.getCurrent (), warning?255:0, 0, 0));
				}
			}
		}
	}
	
	private class MyListListener extends MouseAdapter {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		    if(listOfSuggestions.getSelectionIndex() != -1) {
		    	result = listOfSuggestions.getSelection()[0];
		    }
		}
	}
}
