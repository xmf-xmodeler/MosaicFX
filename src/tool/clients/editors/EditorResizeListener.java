package tool.clients.editors;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.widgets.Shell;

import tool.xmodeler.XModeler;

 
public class EditorResizeListener implements CTabFolder2Listener {
  
	
	@Deprecated
public void close(CTabFolderEvent arg0) {
  }
	
	@Deprecated
  public void maximize(CTabFolderEvent event) {
//    XModeler.maximiseEditors();
  }
	
	@Deprecated
  public void minimize(CTabFolderEvent event) {
//    XModeler.minimiseEditors();
  }
	
	@Deprecated
  public void restore(CTabFolderEvent arg0) {
    
  }
	
	@Deprecated
  public void showList(CTabFolderEvent arg0) {
    
  }

}
