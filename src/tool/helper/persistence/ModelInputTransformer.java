package tool.helper.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ModelInputTransformer {

	public File transform(File inputFile, int version) {
		switch (version) {
		case 2:
			File transformedToV3 = new File("tempV3.xml");
			transformedToV3 = transformV2to3(inputFile);
			version = 3;
			return transform(transformedToV3, version);
		case 3:
			File transformedToV4 = new File("tempV4.xml");
			transformedToV4 = transformV3to4(inputFile);
			version = 4;
			return transformedToV4;
		default:
			throw new IllegalArgumentException("Input Document has unexpacted version. Version =\"" + version + "\"");
		}
	}

	private File transformV2to3(File inputFile) {
		File transformationFile = new File("resources/xslt/ModelInputTransfromer[v2_to_v3].xslt");
		return processTransforming(inputFile, transformationFile);
	}

	private File transformV3to4(File inputFile) {
		File transformationFile = new File("resources/xslt/ModelInputTransfromer[v3_to_v4].xslt");
		return processTransforming(inputFile, transformationFile);
	}

	private File processTransforming(File inputFile, File transformationFile) {
		File transformedFile = null;
	
		try {
			//2023-08-31 TS added current time milis because otherwise java thinks it is writing to the "same" file
			transformedFile = new File("temp" + System.currentTimeMillis() + ".xml");
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(transformationFile));
			FileOutputStream fileOutputStream = new FileOutputStream(transformedFile);
			transformer.transform(new StreamSource(inputFile), new StreamResult(fileOutputStream));
			//2023-09-01 TS need to close this line otherwise the file can not be deleted
			fileOutputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return transformedFile;
	}
	
	public static void deleteTempFiles() {
		try {
			Stream<Path> stream = Files.list(Paths.get("."));
			stream.forEach((e) -> {
				if (e.getFileName().toString().startsWith("temp")) {
					try {
						Files.delete(e);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});

		} catch (IOException e) {
			// TODO TS add logging
			e.printStackTrace();
		}
	}
}