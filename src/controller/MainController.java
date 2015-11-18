package controller;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import assetload.AssetLoader;
import bulletphysics.PhysicsWorld;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import game.AirBoat;
import game.AirboatPlayer;
import game.GodPlayer;
import renderengine.DisplayManager;
import renderengine.Loader;
import renderengine.MasterRenderer;
import terrains.Terrain;
import toolbox.MousePicker;
import water.WaterTile;
import waterdynamic.OceanModel;

public class MainController {

	private static MainController controller = new MainController();
	private AssetLoader assetLoader;
	private Loader loader = new Loader();
	private PhysicsWorld physicsWorld = new PhysicsWorld();
	private MasterRenderer renderer;
	
	//Hold stuffs - may be relevant for later optimizations
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Entity> normalMapEntities = new ArrayList<Entity>();


	private boolean DEBUG_DRAW_ENABLED = false;
	private MainController(){
		physicsWorld.initPhysics();
		renderer = new MasterRenderer(new Loader(), physicsWorld);
		assetLoader = new AssetLoader("res/testing_folder/", this);
		
	}
	
	public PhysicsWorld getPhysicsWorld(){
		return this.physicsWorld;
	}
	
	public Loader getLoader(){
		return this.loader;
	}
	
	public static MainController getMainController(){
		return controller;
	}
	
	/**
	 * Load in assets from file directory
	 * @param level
	 */
	public void loadUp(int level){
		//TODO: may want to do graphical load screen here...
		
		//load stuffs
		assetLoader.loadLevel(level);
		
	}
	
	/**
	 * Run game - main while loop
	 */
	public void run(){
		System.out.println("Begin Run");
		Light light = new Light(new Vector3f(0,3000,-200), new Vector3f(1.f,1.0f, 1.0f)); // position, color = white
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		
		Camera camera = new Camera();
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		//Player player = new AirboatPlayer(camera, boat_entity);
		System.out.println("Number of Entities:  " + entities.size());
		System.out.println("Number of Normal Entities:  " + normalMapEntities.size());
		System.out.println("Number of Water tiles:  " + waters.size());
		System.out.println("Number of Terrains:  " +terrains.size());
		
		AirBoat boat_entity = (AirBoat) entities.get(0);
		Player player = new AirboatPlayer(camera, boat_entity);
		//Player player = new GodPlayer(camera);
		
		for(Terrain t: terrains){
			this.physicsWorld.addTerrainObject(t);
		}
		
		//OceanModel ocean = new OceanModel(loader,renderer.getDebugRenderer());
		OceanModel ocean = null;



		while(!Display.isCloseRequested()){	
			//Read input
			camera.move();
			player.checkInputs();
			player.shiftCamera();
			//Take physics step(s)
			this.physicsWorld.takeStep(DisplayManager.getFrameTimeSeconds());
			//ocean.tick(DisplayManager.getFrameTimeSeconds());	
			//Render - buffer cleared in this call so all drawing must be in this or after
			renderer.renderScene(entities, normalMapEntities, terrains, lights, waters, ocean, camera, new Vector4f(0,0,0,1));
			if(DEBUG_DRAW_ENABLED){
				physicsWorld.drawDebug(); //this is where thyeare added
				renderer.renderDebugger(camera);
			}
			
			//picker.update();
			//System.out.println(picker.getCurrentRay());
			
			DisplayManager.updateDisplay();
		}
		cleanUp();
		
	}
	
	/**
	 * Close window
	 */
	private void cleanUp(){
		
		
		
		DisplayManager.closeDisplay();
	}
	
	public List<WaterTile> getWaters() {
		return waters;
	}


	public void setWaters(List<WaterTile> waters) {
		this.waters = waters;
	}


	public List<Terrain> getTerrains() {
		return terrains;
	}


	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}


	public List<Entity> getEntities() {
		return entities;
	}


	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public List<Entity> getNormalMapEntities() {
		return normalMapEntities;
	}

	public void setNormalMapEntities(List<Entity> normalMapEntities) {
		this.normalMapEntities = normalMapEntities;
	}
	
	
}
