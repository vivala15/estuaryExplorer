package renderengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.linearmath.IDebugDraw;

import bulletphysics.PhysicsWorld;
import entities.Camera;
import entities.Entity;
import entities.Light;
import game.AirBoat;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import waterdynamic.DynamicWaterRenderer;
import waterdynamic.DynamicWaterShader;
import waterdynamic.OceanModel;

public class MasterRenderer{

	private static final float FOV = 70; //field of view
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 2500f; //advised to make this encompass skybox!cx
	
	public static final float RED = 0.5f;
	public static final float GREEN = 0.6f;
	public static final float BLUE = 0.6f;
	
	private Matrix4f projectionMatrix;
	
	
	//Entities
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	//Terrain
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	//Debugging
	private BulletDebugRenderer debugRenderer;
	
	//map entities to what model they use, this way we can group when rendering
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Light> lights = new ArrayList<Light>();
	
	//Skybox
	private SkyboxRenderer skyboxRenderer;
	
	//Water
	WaterFrameBuffers buffers = new WaterFrameBuffers();
	WaterShader waterShader = new WaterShader();
	WaterRenderer waterRenderer;
	
	//Better Water
	//WaterFrameBuffers dynamicWaterBuffers = new WaterFrameBuffers();
	DynamicWaterShader dynamicWaterShader = new DynamicWaterShader();
	DynamicWaterRenderer dynamicWaterRenderer;
	OceanModel oceanModel;
	
	//NormalMapping Renderer
	private NormalMappingRenderer normalMapRenderer;
	
	private PhysicsWorld physicsWorld;
	
	
	public MasterRenderer(Loader loader, PhysicsWorld pw){
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		debugRenderer  = new BulletDebugRenderer(pw, loader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, waterShader, this.getProjectionMatrix(),buffers);
		dynamicWaterRenderer = new DynamicWaterRenderer(
				loader, dynamicWaterShader, this.getProjectionMatrix(),buffers);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);

	}

	//Prep opengl to render frame, call once per tick
	public void prepare(){
		//give red background
		//test which are on top
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		//determine sky color
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	
	public static void enableCulling(){
		//don't render back of textures... improves performance
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling(){
		//render back of textures... improves performance
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	
	/**
	 *  For use in water tutorial - i will rewrite more general model for engine
	 * @param entities
	 * @param terrains
	 * @param lights
	 * @param camera
	 */
	//why include terrains we have one???
	//public void renderScene(List<Entity> entities, List<Terrain> terrains, List<Light> lights,

	public void renderScene(List<Entity> entities, List<Entity> normalEntities, List<Terrain> terrains, List<Light> lights,
			List<WaterTile> waters, OceanModel oceanModel, Camera camera, Vector4f clipPlane){
		for (Terrain terrain : terrains){
			processTerrain(terrain);
		}
		for (Entity entity : entities){
//			if(entity instanceof AirBoat){
//				System.out.println("Its being processed");
//			}
			processEntity(entity);
		}
		for(Entity entity : normalEntities){
			processNormalMapEntity(entity);
		}
		
		this.oceanModel = oceanModel;
		
		
//		if(!waters.isEmpty()){
//			
//			for(WaterTile water: waters){
				GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
				buffers.bindReflectionFrameBuffer();
				float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
				camera.getPosition().y -= distance;
				camera.invertPitch();
				//not using roll, but if was would need camera.invertRoll() as well
				//+1f removes remaining edge glitch due to distortion though will add occasional reflection that shoouldn't happen
				render(lights, camera, new Vector4f(0,1,0, -waters.get(0).getHeight()+1f));
				camera.getPosition().y += distance;
				camera.invertPitch();
				//render refraction texture
				buffers.bindRefractionFrameBuffer();
				render(lights, camera, new Vector4f(0,-1,0,waters.get(0).getHeight())); //this clip plane less
				buffers.unbindCurrentFrameBuffer();

				//render to screen
				//render(lights, camera, clipPlane);
				//some drivers ignore this... if this happens hack is to set lower clip plane really far off
				GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
				//render scene
//			}
//		}
		//		this.debugRenderer.render(camera);
		render(lights, camera, clipPlane);
		//water's must be AFTER render so that they capture the scenes in buffers
		waterRenderer.render(waters, camera, lights.get(0));
		
		
		//dynamicWaterRenderer.render(oceanModel, camera,  lights.get(0));
		this.terrains.clear();
		this.entities.clear(); //remember or they build up and it grows.
		normalMapEntities.clear();
	}
	/**
	 * 
	 * GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//render reflection texture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			//not using roll, but if was would need camera.invertRoll() as well
			//+1f removes remaining edge glitch due to distortion though will add occasional reflection that shoouldn't happen
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,1,0, -water.getHeight()+1f));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			
			//render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,-1,0,water.getHeight()));
			
			buffers.unbindCurrentFrameBuffer();
			
			//render to screen
			//some drivers ignore this... if this happens hack is to set lower clip plane really far off
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			//render scene
			waterRenderer.render(waters, camera, light);
	 * 
	 * 
	 */
	
	
	
	/**
	 * Renders the given seen, one should have already provided a list of entities and 
	 * list of terrain et cet for processing. Start shaders, load in some uniform 
	 * render etc.
	 * @param lights
	 * @param camera
	 */
	private void render(List<Light> lights, Camera camera, Vector4f clipPlane){
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(RED, GREEN, BLUE); //update each frame to allow for change ie day/night cycle
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		//make sure other shader's not running
		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
		
		
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		
		//clearing them elsewhere due to multiple rener calls 
		//terrains.clear();
		//entities.clear(); //remember or they build up and it grows.
		//this.debugRenderer.render(camera);
	}
	
	//for when decide to store lights in this class
//	public void processLight(Light light){
//		lights.add(light)
//	}
	public void renderDebugger( Camera camera){
		this.debugRenderer.render(camera);
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	public BulletDebugRenderer getDebugRenderer(){
		return this.debugRenderer;
	}
	
	/**
	 * Sort the entity into the texture batches
	 * Creates a new batch if new texture, else added to existing list
	 * Helps to optimize rendering
	 * @param entity
	 */
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	
	/**
	 * Sort the entity into the texture batches
	 * Creates a new batch if new texture, else added to existing list
	 * Helps to optimize rendering
	 * @param entity
	 */
	public void processNormalMapEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	
	public Matrix4f getProjectionMatrix(){
		return projectionMatrix;
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	/**
	 * Clean up shader
	 */
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
	}

	public void setDebugMode(int debug) {
		this.debugRenderer.setDebugMode(debug);
	}
	
}
