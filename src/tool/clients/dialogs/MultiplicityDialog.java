package tool.clients.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiplicityDialog extends org.eclipse.jface.dialogs.Dialog{

	private Text txtMin;
	private Text txtMax;
	private Button boxOrdered;
	private Button boxNavigable;
	
	private int min, max;
	private boolean ordered, navigable;

	private transient boolean blocked;
	private transient boolean finished;
	private transient boolean okPressed;
	String s;
	
	public static void main(String[] args) {
		System.err.println(show(1,2,true,false));
	}
	
	public static String show(final int min, final int max, final boolean ordered, final boolean navigable) {
		final MultiplicityDialog m = new MultiplicityDialog((Shell) null, min, max, ordered, navigable);
		return m.showLocal();
	}
		
	private String showLocal() {
		new Thread(new Runnable() {
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						open();
					}
				});
			}
		}).start();
		while(!finished) {
			blocked = true;
//			System.err.println("Wait for Button");
			while(blocked) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(okPressed) {
//				System.err.println("OK");
				new Thread(new Runnable() {
					public void run() {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								try{
									int a = Integer.parseInt(txtMin.getText());
									if(!"*".equals(txtMax.getText())) {
										int b = Integer.parseInt(txtMax.getText());
										if(b < 1) throw new IllegalArgumentException();
										if(b < a) throw new IllegalArgumentException();
									}
									s = txtMin.getText() + ".." + txtMax.getText() + (boxOrdered.getSelection()?"$":"");
									if(s.endsWith("1$")) s = s.substring(0, s.length()-1);
									if("1..1".equals(s)) s = "!";
									if("0..1".equals(s)) s = "1";								
								} catch (Exception e) {
									s = null;
								};
								blocked = false;							
							}
						});
					}
				}).start();
				blocked = true;
				while(blocked) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(s != null) {
					dispose();
					finished = true;
					return s;
				}
			} else {
//				System.err.println("Cancel");
				finished = true;
				dispose();
				return null;
			}
		}
		return null;
	}

	private void dispose() {
		new Thread(new Runnable() {
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						dispose2();
					}
				});
			}
		}).start();
	}

	private void dispose2() {
		super.okPressed();		
	}

	protected MultiplicityDialog(Shell parentShell, int min, int max, boolean ordered, boolean navigable) { 
		super(parentShell); 
		this.min = min;
		this.max = max;
		this.ordered = ordered;
		this.navigable = navigable;
	}
//    protected MultiplicityDialog(IShellProvider parentShell) { super(parentShell); }
    
    @Override
    protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    GridLayout layout = new GridLayout(4, false);
	    container.setLayout(layout);

	    Button defaultR1 = new Button(container, SWT.RADIO); 
	    defaultR1.setText("0..1"); 
	    GridData gd = new GridData();
	    gd.horizontalSpan=2;
	    defaultR1.setLayoutData(gd);
	    
	    defaultR1.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent arg0) {setValues("0","1",false);}
			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("0","1",false);}
		});
	    
	    Label label1 = new Label(container, SWT.NONE); 
	    label1.setText("Zero or one");
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    label1.setLayoutData(gd);
	    
	    Button defaultR2 = new Button(container, SWT.RADIO); 
	    defaultR2.setText("1..1"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    defaultR2.setLayoutData(gd);
	    
	    defaultR2.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent arg0) {setValues("1","1",false);}
			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("1","1",false);}
		});
	    
	    Label label2 = new Label(container, SWT.NONE); 
	    label2.setText("Exactly one"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    label2.setLayoutData(gd);
	    
	    Button defaultR3 = new Button(container, SWT.RADIO); 
	    defaultR3.setText("0..*"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    defaultR3.setLayoutData(gd);
	    
	    defaultR3.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent arg0) {setValues("0","*",false);}
			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("0","*",false);}
		});
	    
	    Label label3 = new Label(container, SWT.NONE); 
	    label3.setText("Any number including zero"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    label3.setLayoutData(gd);
	    
	    Button defaultR4 = new Button(container, SWT.RADIO); 
	    defaultR4.setText("1..*"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    defaultR4.setLayoutData(gd);
	    
	    defaultR4.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent arg0) {setValues("1","*",false);}
			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("1","*",false);}
		});
	    
	    Label label4 = new Label(container, SWT.NONE); 
	    label4.setText("Any number, at least one"); 
	    gd = new GridData();
	    gd.horizontalSpan=2;
	    label4.setLayoutData(gd);
	    
	    
	    Button defaultR5 = new Button(container, SWT.RADIO); defaultR5.setText("");
	    txtMin = new Text(container, SWT.BORDER);
	    txtMin.setText(min+"");
	    Label dummy1 = new Label(container, SWT.NONE); dummy1.setText("..");
        txtMax = new Text(container, SWT.BORDER);
	    txtMax.setText(max<0?"*":(max+""));
	    
	    defaultR5.setSelection(true);
	    defaultR5.addSelectionListener(new SelectionListener() {
			@Override public void widgetSelected(SelectionEvent arg0) {enableUserDef();}
			@Override public void widgetDefaultSelected(SelectionEvent arg0) {enableUserDef();}
		});
	    

	    boxOrdered = new Button(container, SWT.CHECK); boxOrdered.setText("Ordered"); boxOrdered.setSelection(ordered);
	    boxNavigable = new Button(container, SWT.CHECK); boxNavigable.setText("Navigable"); boxNavigable.setSelection(navigable);
	    
	    GridData textFieldGridData = new GridData();
	    textFieldGridData.horizontalSpan = 4;
	    boxOrdered.setLayoutData(textFieldGridData); 
	    
	    textFieldGridData = new GridData();
	    textFieldGridData.horizontalSpan = 4;
	    boxNavigable.setLayoutData(textFieldGridData);

      return container;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
    @Override
    protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setText("Selection dialog");
    }
    
    private void setValues(String min, String max, boolean ordered) {
    	txtMin.setText(min);
    	txtMax.setText(max);
    	boxOrdered.setSelection(ordered);
    	txtMin.setEnabled(false);
    	txtMax.setEnabled(false);
    	boxOrdered.setEnabled(false);
    } 
    
    private void enableUserDef() {
    	txtMin.setEnabled(true);
    	txtMax.setEnabled(true);
    	boxOrdered.setEnabled(true);
    }
    
	@Override
	protected void okPressed() {
		okPressed = true;
		blocked = false;
//		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		okPressed = false;
		blocked = false;
//		super.cancelPressed();
	}

}
