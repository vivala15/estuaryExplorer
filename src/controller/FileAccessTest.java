package controller;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileAccessTest {

	File gameRoot = new File("res/testing_folder/");
	File samplefolder = new File("res/testing_folder/assets/airboat/");
	File samplefolder2 = new File("res/testing_folder/level1/asset_meta/");
	String sampleTextFile = "res/testing_folder/testingText.txt";
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMatchFile1() {
		List<File> files = FileAccess.listFiles(samplefolder);
		String out = FileAccess.matchFile(files, ".*texture.*");
		assertEquals(out, "texture.tga");
	}
	
	@Test
	public void testMatchFile2() {
		List<File> files = FileAccess.listFiles(samplefolder2);
		String out = FileAccess.matchFile(files, "airboat.*meta.*");
		assertEquals(out, "airboat_meta.txt");
	}

	@Test
	public void testListFolders() {
		List<File> folders = FileAccess.listFolders(gameRoot);
		assertEquals(folders.size(),2);
		assertEquals(true, folders.get(0).getName().trim().matches("level1") ||
				folders.get(1).getName().trim().matches("level1"));
		
	}

	@Test
	public void testListFiles() {
		List<File> files = FileAccess.listFiles(samplefolder);
		assertEquals(3, files.size());
	}

	@Test
	public void testReadTextFile() {
		String fileRead = FileAccess.readTextFile(sampleTextFile);
		String[] lineSplit = fileRead.split("\n");
		assertEquals(lineSplit.length, 2);
		
	}

}
