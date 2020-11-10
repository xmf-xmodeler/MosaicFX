package tool.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class PackageStatistics {
	
	public static HashMap<String, HashSet<String>> result = new HashMap<>();

	public static void main(String[] args) {
		File root = new File("../XMF/com.ceteva.xmf.system/xmf-src");
		System.err.println(root.getAbsolutePath() + " " + root.exists());
		
		Vector<File> allFiles = getAllFiles(root);
		
		System.err.println(allFiles.size());
		
		for(File f : allFiles) {
			investigateFile(f);
		}
		
		int a = 1;
		for(String key : result.keySet()) {
			System.err.println(a + ": " + key + " uses " + result.get(key)); a++;
		}
	}
	static int a = 1;
	private static void investigateFile(File f) {
//		System.err.println("Investigating " + f.getAbsolutePath());
		
		try {
			String text = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));

			HashSet<String> doubleColons = new HashSet<>();
			HashSet<String> imports = new HashSet<>();
			HashSet<String> parserImports = new HashSet<>();
			HashSet<String> contexts = new HashSet<>();
			
			
			// import, parserImport ...
			final String s0 = "context ";
			final String s1 = "parserImport ";
			final String s2 = "import ";
			final String s3 = "::";
			
			String[] lines = text.split("[\\r\\n]+");
			
			for(String line : lines ) {
				int i = 0;
				while(i < line.length()) {
					if(line.indexOf(s0, i) != -1) {
						i = line.indexOf(s0, i) + s0.length();
//						int j1 = line.indexOf("\n", i);
//						int j2 = line.indexOf("\r", i);
//						j1 = j1 == -1 ? Integer.MAX_VALUE : j1;
//						j2 = j2 == -1 ? Integer.MAX_VALUE : j2;
//						int j = j1 < j2 ? j1 : j2;
//						if(j != Integer.MAX_VALUE) {
							String text2 = line.substring(i).trim();
							contexts.add(text2);//System.err.println("C\t\t" + text2);
							i=Integer.MAX_VALUE;
						
					} else {
						i = line.length();
					}
				} i = 0;
				while(i < line.length()) {	
					if(line.indexOf(s1, i) != -1) {
						i = line.indexOf(s1, i) + s1.length();
						int j = line.indexOf(";", i);
						if(j != -1) {
							String text2 = line.substring(i, j).trim();
							parserImports.add(text2);//System.err.println("IP\t\t" + text2);
							i=j+1;
						}
					} else {
						i = line.length();
					}
				} i = 0;
				while(i < line.length()) {	
					if(line.indexOf(s2, i) != -1) {
						i = line.indexOf(s2, i) + s2.length();
						int j = line.indexOf(";", i);
						if(j != -1) {
							String text2 = line.substring(i, j).trim();
							imports.add(text2);//System.err.println("I\t\t" + text2);
							i=j+1;
						}
					} else {
						i = line.length();
					}
				} i = 0;
				while(i < line.length()) {	
					if(line.indexOf(s3, i) != -1) {
						int end = line.indexOf(" ", line.indexOf(s3, i)+2);
						int start = line.lastIndexOf(" ", line.indexOf(s3, i));
						i = line.indexOf(s3, i)+1;				
						if(end != -1 && start != -1) {
							String text2 = line.substring(start, end).trim();
							doubleColons.add(text2);//System.err.println("::\t\t" + text2);
							i = end;
						}
					} else {
						i = line.length();
					}
				} i = 0;
			}

			parserImports.remove("XOCL");
						
			if((parserImports.size() != 0 || imports.size() != 0 || doubleColons.size() != 0) && contexts.size() > 0) {
//				System.err.println(""+a+" in " + f.getAbsolutePath() + ":");
//				a++;
//				System.err.println(a +"\t" + contexts + " uses " + parserImports + ", " + imports + ", " + doubleColons);			
				
				HashSet<String> uses = new HashSet<String>();
				uses.addAll(parserImports);
				uses.addAll(imports);
				uses.addAll(doubleColons);
				for(String key : contexts) {
					result.put(key, uses);
				}
					
			}
			
		} catch (IOException e) {e.printStackTrace();}
		
	}

	private static Vector<File> getAllFiles(File root) {
		Vector<File> allFiles = new Vector<File>();
			for(File f : root.listFiles()) {
				if(f.getName().endsWith("Boot.xmf")) {} else
				if(f.getName().endsWith("Manifest.xmf")) {} else
				if(f.getName().endsWith("Makefile.xmf")) {} else
				if(f.getName().endsWith(".xmf")) {allFiles.add(f);} else
			    if(f.isDirectory() ) {allFiles.addAll(getAllFiles(f));}
			}
		
		return allFiles;
	}
}
