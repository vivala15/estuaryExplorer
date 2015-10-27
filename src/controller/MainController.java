package controller;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

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
	
	
	public void loadUp(int level){
		//may want to do graphical load screen here...
		
		//load stuffs
		assetLoader.loadLevel(level);
		
	}
	
	public void run(){
		System.out.println("Begin Run");
		Light light = new Light(new Vector3f(0,3000,-200), new Vector3f(1.f,1.0f, 1.0f)); // position, color = white
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		
		Camera camera = new Camera();
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		//Player player = new AirboatPlayer(camera, boat_entity);
		System.out.println(entities.size());
		System.out.println(waters.size());
		System.out.println(terrains.size());
		
		AirBoat boat_entity = (AirBoat) entities.get(0);
		Player player = new AirboatPlayer(camera, boat_entity);
		//Player player = new GodPlayer(camera);
		
		for(Terrain t: terrains){
			this.physicsWorld.addTerrainObject(t);
		}
		while(!Display.isCloseRequested()){
			//Read input
			camera.move();
//			lights.get(0).setPosition(camera.getPosition());
			player.checkInputs();
			player.shiftCamera();
			//Take physics step(s)
			this.physicsWorld.takeStep(DisplayManager.getFrameTimeSeconds());
			//Render
			renderer.renderScene(entities, normalMapEntities, terrains, lights, waters, camera, new Vector4f(0,0,0,1));
			
			
			renderer.renderDebugger(camera);
			DisplayManager.updateDisplay();
		}
		cleanUp();
		
	}
	
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
