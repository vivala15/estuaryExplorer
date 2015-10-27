package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Due to size and difficulty testing AssetLoader - some methods moved over here
 * 
 * @author chris
 *
 */
public class FileAccess {

	
	
	public static String matchFile(List<File> files, String regex){
		for(File f: files){
			String fileName = f.getName();
			if(fileName.matches(regex)){
				return fileName;
			}
		}
		return null;
	}
	
	public static List<File> listFolders(final File folder){
		ArrayList<File> folders = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            folders.add(fileEntry);
	        }
	    }
		return folders;
	}
	public static List<File> listFiles(final File folder){
		ArrayList<File> files = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	            files.add(fileEntry);
	        }
	    }
		return files;
	}
	
	public static String readTextFile(String fileName, boolean skipComments){
		String everything = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		    	String testline = line.trim();
		    	if(!testline.startsWith("#") && skipComments){
		    		sb.append(line);
		    		sb.append(System.lineSeparator());
		    	}
		    	line = br.readLine();
		    }
		    everything = sb.toString();
		    br.close();
		} catch(IOException e){
			System.err.println("Error thrown while reading file: " + fileName);
			//if error thrown just return empty string - do not stop program...
		}
		return everything;
	}
	
	public static String readTextFile(String fileName){
		return readTextFile(fileName, false);
	}
}
