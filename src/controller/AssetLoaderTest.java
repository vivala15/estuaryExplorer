package controller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssetLoaderTest {

	
	MainController mc = MainController.getMainController();
	AssetLoader ac = new AssetLoader("res/testing_folder/", mc);
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadLevel() {
		ac.loadLevel(1);
		fail("Not yet implemented");
	}

}
