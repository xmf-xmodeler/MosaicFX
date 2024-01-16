package tool.clients.fmmlxdiagrams.menus;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.Note;

public class NoteContextMenu extends ContextMenu {
	
	public NoteContextMenu(FmmlxDiagram diagram,Note note) {
		DiagramActions actions = diagram.getActions();
		MenuItem changeItem = new MenuItem("Change Note");
		changeItem.setOnAction(e -> actions.editNote(note));
		
		MenuItem delItem = new MenuItem("Delete Note");
		delItem.setOnAction(e -> note.remove((FmmlxDiagram) actions.getDiagram()));	
		
		MenuItem hideItem = new MenuItem("Hide");
		hideItem.setOnAction(e -> note.hide(diagram));	
		
		getItems().addAll(changeItem, delItem, hideItem);
	}
}