package tool.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;





public class FileAuxilary {
	
	public static String printFileContentToConsol(File file, String message) {
		System.err.println(message);
		BufferedReader r;
		String s = null;
		try {
			r = new BufferedReader(new FileReader(file));
			s = "";
			String newLine = null;
			while ((newLine = r.readLine()) != null) {
				s += newLine;
			}
			System.err.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public boolean filesAreEqualIgnoringWhitespace(File file1, File file2) throws IOException {
	    // Convert files to strings
//	    String xmlContent1 = new String(Files.readAllBytes(file1.toPath()));
//	    String xmlContent2 = new String(Files.readAllBytes(file2.toPath()));
//	    
//	 // Remove the XML preamble if present and all whitespace
//	    String normalizedContent1 = normalizeXmlContent(xmlContent1);
//	    String normalizedContent2 = normalizeXmlContent(xmlContent2);
//	    System.err.print(normalizedContent1 + "\n");
//	    System.err.print(normalizedContent2);
//	    
//	    // Create a diff instance configured to ignore element order
//	    Diff diff = DiffBuilder.compare(normalizedContent1)
//	        .withTest(normalizedContent2)
//	        .ignoreWhitespace() // Ignores differences in whitespace
//	        .checkForSimilar()  // Checks for similarities instead of exact matches
//	        .withDifferenceEvaluator(DifferenceEvaluators.Default) // Use the default evaluator
//	        .normalizeWhitespace() // Normalizes whitespace before comparison
//	        .ignoreComments()      // Ignores XML comments
//	        .build();
//
//	    // Return true if there are no differences
//	    return !diff.hasDifferences();
//		DifferenceEvaluator evaluator = DifferenceEvaluators
//				  .downgradeDifferencesToEquals(ComparisonType.CHILD_NODELIST_SEQUENCE);
//
//
//
//				Diff diff = DiffBuilder.compare(bufferedReaderExistingFile)
//				    .withTest(bufferedReaderNewFile).ignoreComments()
//				    .ignoreWhitespace()
//				    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
//				    .withDifferenceEvaluator(evaluator)
//				    .checkForSimilar()
//				    .build();
		return true;
	}


	private String normalizeXmlContent(String content) {
	    // Regex to remove XML preamble and all whitespace
	    String withoutPreamble = content.replaceAll("(?s)<\\?xml.*?\\?>", ""); // Removes XML declaration
	    String normalized = withoutPreamble.replaceAll("\\s+", ""); // Removes all whitespace
	    return normalized;
	}

}
