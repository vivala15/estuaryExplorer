package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;

import bulletphysics.PhysicsWorld;
import controller.MainController;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import game.AirBoat;
import game.AirboatPlayer;
import game.GodPlayer;
import game.VisualEntity;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderengine.DisplayManager;
import renderengine.EntityRenderer;
import renderengine.Loader;
import renderengine.MasterRenderer;
import renderengine.OBJLoader;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop{


	public static void main(String[] args){
		
		boolean testLoader = true;
		
		if(testLoader){
			DisplayManager.createDisplay();
			MainController mc = MainController.getMainController();
			System.out.println("Load up");
			mc.loadUp(1);
			mc.run();
			System.out.println("Should hvae run");
			
		}else{
		
		
		
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		//*********************TERRAIN STUFF *************
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass",true));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud",true));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flower",true));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path",true));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blend_map",true));
		//**************************************************

			
//		ModelData data = OBJFileLoader.loadOBJ("airboat_stripped"); //still! cuts out at faces
//		RawModel boatModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		RawModel boatModel = OBJLoader.loadObjModel("air_boat_exported", loader);
		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		RawModel fernModel = OBJLoader.loadObjModel("grassModel", loader);
		RawModel feModel = OBJLoader.loadObjModel("fern", loader);
		//RawModel model = loader.laodToVAO(vertices,textureCoords, indices);
		
		//atlas example
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_atlas",true));
		fernTextureAtlas.setNumberOfRows(2); //2 by 2 - this doesn't seem togeneral to all
		
		TexturedModel boatTextureModel  = new TexturedModel(boatModel, new ModelTexture(loader.loadTextureTGA("obj_files/Airboat/Texture/Airboat001", true)));
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("flag_belgium_square-256",true))); 
		TexturedModel grassTextModel = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("gass_trans",true)));
		TexturedModel fernTexturedModel = new TexturedModel(feModel, fernTextureAtlas);
		
		ModelTexture texture = staticModel.getTexture();
		ModelTexture fern_texture = grassTextModel.getTexture();
		//fern_texture.setHasTransparency(true);
		fern_texture.setUseFakeLighting(true);
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		VisualEntity entity = new VisualEntity(staticModel, new Vector3f(0,-5,-25), 0,0,0,1);
		VisualEntity entity2 = new VisualEntity(staticModel, new Vector3f(10,-5,-25), 0,0,0,1);
		AirBoat boat_entity = new AirBoat(boatTextureModel, new Vector3f(15f, 20f, -15f), 0, 0, 0 ,.2f);
		//System.out.println(boatTextureModel.getRawModel().getVertexCount());
		VisualEntity fern_entity = new VisualEntity(grassTextModel, new Vector3f(7,0,-15), 0,0,0,1);
		VisualEntity fernAtlasedEntity = new VisualEntity(fernTexturedModel, 0, new Vector3f(20,0,-15),0,0,0,1);
		
		Light light = new Light(new Vector3f(0,300,-200), new Vector3f(1.0f,1.0f,1.0f)); // position, color = white
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		//lights.add(new Light(new Vector3f(20,10,-2), new Vector3f(4,0,0), new Vector3f(1, .01f, 0.002f)));
		//lights.add(new Light(new Vector3f(-20,10,-20), new Vector3f(0,0,4),new Vector3f(1, .01f, 0.002f)));
		
		Terrain terrain = new Terrain(0,-1,loader,texturePack, blendMap, "height_map");
		Terrain terrain2 = new Terrain(1,-1,loader, texturePack, blendMap, "height_map");
		
		Camera camera = new Camera();
		
		
		
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiTexture guitm = new GuiTexture(loader.loadTexture("thin_matrix",true), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guiTextures.add(guitm);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		entities.add(entity2);
		entities.add(fern_entity);
		entities.add(fernAtlasedEntity);
		entities.add(boat_entity);
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		//terrains.add(terrain2);
		
		
		
		
//		GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guiTextures.add(refraction);
		//guiTextures.add(reflection);
		
		PhysicsWorld pw = new PhysicsWorld();
		pw.initPhysics();
		//pw.basicPlaneGround();
		pw.addTerrainObject(terrain);
		pw.addPhysicalEntityObject(boat_entity);
		//pw.addPhysicalEntityObject(air_boat);
		
		
		MasterRenderer renderer = new MasterRenderer(loader,pw);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		
		
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(),buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, 0);
		waters.add(water);
		
		
		Player player = new AirboatPlayer(camera, boat_entity);
		//Player player = new GodPlayer(camera);
		//renderer.setDebugMode(DebugDrawModes.DRAW_WIREFRAME);
		
		while(!Display.isCloseRequested()){
			camera.move();
			player.checkInputs();
			player.shiftCamera();
			picker.update();
			
			//renderer.renderScene(entities, terrains, lights, waters, camera, new Vector4f(0,0,0,1));
			//guiRenderer.render(guiTextures);
			
			//renderer.renderDebugger(camera);
		
			pw.takeStep(DisplayManager.getFrameTimeSeconds());
			
			DisplayManager.updateDisplay();
			
			
		}
		
		buffers.cleanUp();
		guiRenderer.cleanUp();
//		shader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp(); //clean up once done
		DisplayManager.closeDisplay();
		}
	}

}



/***


GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

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
renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0,1,0,0));
waterRenderer.render(waters, camera, light);


**/