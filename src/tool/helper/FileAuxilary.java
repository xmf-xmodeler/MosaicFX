package tool.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

}
