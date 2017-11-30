package tool.xmodeler;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MyMenuBar extends javafx.scene.control.MenuBar {
	
	public MyMenuBar() {
		
		// Create Menues
		
		// --- Menu File ---
		Menu menuFile = new Menu("File");
		
		// Submenu Import
		Menu subMenuImport = new Menu("Import");
		MenuItem itemCSVData = new MenuItem("CSV Data...");
		subMenuImport.getItems().add(itemCSVData);
			
		// Item Open File
		MenuItem itemOpenFile = new MenuItem("Open File...");
		
		// Item Welcome Page
		MenuItem itemWelcomePage = new MenuItem("Show Welcome Page...");
		
		// Submenu Browse
		Menu subMenuBrowse = new Menu("Browse");
		MenuItem itemFileBrowser = new MenuItem("Open File Browser...");
		subMenuBrowse.getItems().add(itemFileBrowser);
		
		// Item Save Image
		MenuItem itemSaveImage = new MenuItem("Save Image...");
		
		// Item New Project
		MenuItem itemNewProject = new MenuItem("New Project");
		
		// Item Open Project
		MenuItem itemOpenProject = new MenuItem("Open Project...");
		
		// Item Load Image
		//MenuItem itemLoadImage = new MenuItem("Load Image...");
		
		menuFile.getItems().addAll(
				subMenuImport,
				itemOpenFile,
				itemWelcomePage,
				subMenuBrowse,
				itemSaveImage,
				itemNewProject,
				itemOpenProject//,
				//itemLoadImage
				);
		
		// --- Menu Browse ---
		Menu menuBrowse = new Menu("Browse");
		
		// Item Open File Browser
		MenuItem itemOpenFileBrowser = new MenuItem("Open File Browser...");
		
		// Item XTools Manager
		MenuItem itemXToolsManager = new MenuItem("XTools Manager");
		
		// Item Compile All
		MenuItem itemCompileAll = new MenuItem("Compile All");
		
		// Item System Projects
		MenuItem itemSystemProjects = new MenuItem("System Projects");
		
		// Item My Projects
		MenuItem itemMyProjects = new MenuItem("MyProjects");
		
		menuBrowse.getItems().addAll(
				itemOpenFileBrowser,
				itemXToolsManager,
				itemCompileAll,
				itemSystemProjects,
				itemMyProjects);
		
		// --- Menu Multilevel ---
		Menu menuMultiLevel = new Menu("MultiLevel");
		
		// Item Multilevel Projects
		MenuItem itemMLProjects = new MenuItem("Multilevel Projects");
		
		menuMultiLevel.getItems().add(
				itemMLProjects);
		
		
//		// --- Menu Debug ---
//		Menu menuDebug = new Menu("Debug");
//		
//		// Item VM Panic
//		MenuItem itemVMPanic = new MenuItem("VM Panic");
//		
//		menuDebug.getItems().add(
//				itemVMPanic);
		
		// Add Menus to MenuBar
		getMenus().addAll(menuFile, menuBrowse, menuMultiLevel);//, menuDebug);
	}
}
