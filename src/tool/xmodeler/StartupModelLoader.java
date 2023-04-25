package tool.xmodeler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;

public class StartupModelLoader {
	
	boolean fmmlxDiagramCommunicatorAvailable = false;
	
	public StartupModelLoader() {
		//Wait for the building of ControlCenter
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (fmmlxDiagramCommunicatorAvailable == false) {
			checkAvailabilityOfCommunicator();	
		}
	}
	
	private void checkAvailabilityOfCommunicator() {
		
		try {
			FmmlxDiagramCommunicator.getCommunicator();
			fmmlxDiagramCommunicatorAvailable = true;
		} catch (Exception e) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
		
	public void loadModelsFromSavedModelsPath() {    	
	      	String savedModelsPath = PropertyManager.getProperty("savedModelsPath");
			if (savedModelsPath == null) {
				return;
			}
			Path dir = Paths.get(savedModelsPath);
			
			Stream<Path> filesFromSavedModelsPath = getFilesFromSavedModelsPath(dir);
			loadXmlFilesFromStream(filesFromSavedModelsPath);	
			Stream<Path> visibleSubfolder = loadListOfVisibleSubfolder(dir);
			loadXmlFilesFromStream(visibleSubfolder);
		}

		private void loadXmlFilesFromStream(Stream<Path> stream) {
			stream.forEach(path ->		
						{
							try {
								Files.walk(path)
								.filter(p -> p.toFile().isFile())
								.filter(p -> p.toString().endsWith(".xml"))
								.forEach(p -> {
									FmmlxDiagramCommunicator.getCommunicator().openXmlFile(p.toString());
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
		}

		private Stream<Path> getFilesFromSavedModelsPath(Path dir) {
			Stream<Path> filesFromSavedModelsPath = null;			
			try {
				filesFromSavedModelsPath = Files.walk(dir, 1)
						.filter(p -> p.toFile().isFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return filesFromSavedModelsPath;
		}

		private Stream<Path> loadListOfVisibleSubfolder(Path dir) {
			Stream<Path> notHiddenFolder = null;
			try {
				notHiddenFolder = Files.walk(dir, 1)
						.filter(p -> p.toFile().isDirectory())
						.filter(p -> !p.getFileName().toString().startsWith("."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			//used to remove parent directory from Stream
			notHiddenFolder = notHiddenFolder.skip(1);
			return notHiddenFolder;
		}
}