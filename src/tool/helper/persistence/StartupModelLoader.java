package tool.helper.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javafx.application.Platform;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.userProperties.PropertyManager;
import tool.helper.userProperties.UserProperty;

public class StartupModelLoader {

	public void loadModelsFromSavedModelsPath() {
	
		ReturnCall<FmmlxDiagramCommunicator> onCommunicatorAvailable = communicator -> {
			Platform.runLater(() -> {
				String savedModelsPath = PropertyManager.getProperty(UserProperty.MODELS_DIR.toString());
				if (savedModelsPath == null) {
					return;
				}
				Path dir = Paths.get(savedModelsPath);
				if (!dir.toFile().exists()) {
					PropertyManager.deleteProperty(UserProperty.MODELS_DIR.toString());
					return;
				}
				Stream<Path> filesFromSavedModelsPath = getFilesFromSavedModelsPath(dir);
				loadXmlFilesFromStream(filesFromSavedModelsPath, communicator);
				Stream<Path> visibleSubfolder = loadListOfVisibleSubfolder(dir);
				loadXmlFilesFromStream(visibleSubfolder, communicator);
			});
		};
		FmmlxDiagramCommunicator.getCommunicatorWhenReady(onCommunicatorAvailable);
	}
	
	// FH 04.03.2024
	// loadModels to Control Center from a specific path
	public void loadModelsFromPath(String path) {
		ReturnCall<FmmlxDiagramCommunicator> onCommunicatorAvailable = communicator -> {
			Platform.runLater(() -> {
				if (path == null)
					return;
				Path dir = Paths.get(path);
				Stream<Path> filesFromSavedModelsPath = getFilesFromSavedModelsPath(dir);
				loadXmlFilesFromStream(filesFromSavedModelsPath, communicator);
				Stream<Path> visibleSubfolder = loadListOfVisibleSubfolder(dir);
				loadXmlFilesFromStream(visibleSubfolder, communicator);
			});
		};
		FmmlxDiagramCommunicator.getCommunicatorWhenReady(onCommunicatorAvailable);

	}

	private void loadXmlFilesFromStream(Stream<Path> stream, FmmlxDiagramCommunicator communicator) {		
		stream.forEach(path -> {
			try {
				Files.walk(path).filter(p -> p.toFile().isFile()).filter(p -> p.toString().endsWith(".xml"))
						.forEach(p -> {
							File inputFile = new File(p.toString());
							XMLParser parser = new XMLParser(inputFile);
					    	parser.parseXMLDocument();
						});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private Stream<Path> getFilesFromSavedModelsPath(Path dir) {
		Stream<Path> filesFromSavedModelsPath = null;
		try {
			filesFromSavedModelsPath = Files.walk(dir, 1).filter(p -> p.toFile().isFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filesFromSavedModelsPath;
	}

	private Stream<Path> loadListOfVisibleSubfolder(Path dir) {
		Stream<Path> notHiddenFolder = null;
		try {
			notHiddenFolder = Files.walk(dir, 1).filter(p -> p.toFile().isDirectory())
					.filter(p -> !p.getFileName().toString().startsWith("."));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// used to remove parent directory from Stream
		notHiddenFolder = notHiddenFolder.skip(1);
		return notHiddenFolder;
	}
}