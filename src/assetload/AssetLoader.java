package assetload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import controller.MainController;
import entities.Entity;
import game.AirBoat;
import game.EntityMembers;
import game.VisualEntity;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import renderengine.Loader;
import renderengine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Pair;
import water.WaterTile;

public class AssetLoader {

	
	private String rootFolder;
	private String levelAppendage;
	private MainController mc;
	private Loader loader;
	
	private List<Pair<EntityMembers, String>> classAndLocation = new ArrayList<>();
	
	public AssetLoader(String rootFolder, MainController mc){
		this.rootFolder = rootFolder;
		this.mc = mc;
		this.loader = mc.getLoader();
		loadUpClassFilePairs();

	
	}


	public void loadLevel(int level){
		this.levelAppendage = String.valueOf(level);
		System.out.println("Loading Terrain");
		List<Terrain> terrains = loadTerrain();
		if(terrains.isEmpty()){
			System.err.println("Terrains returned empty - check file structure...");
		}
		System.out.println("Loading Water");
		//Load water
		List<WaterTile> watertiles = loadWater();
		//within method handles asset placement within renderer and physicalsWorld
		System.out.println("Loading Assets");
		loadAssets();
		
		this.mc.setTerrains(terrains);
		this.mc.setWaters(watertiles);
		
	}
	
	private List<WaterTile> loadWater(){
		List<WaterTile> watertiles = new ArrayList<WaterTile>();
		String waterFileName = rootFolder  + "level" + levelAppendage + "/terrain/water.txt";
		String waterContents = FileAccess.readTextFile(waterFileName, true);
		String[] waterSplit = waterContents.split("\n");
		for(int w =0 ; w < waterSplit.length ; w++){
			if(!waterSplit[w].isEmpty()){
			String[] contents = waterSplit[w].split("\\s+");
			watertiles.add(
					new WaterTile(
							Float.valueOf(contents[0]),
							Float.valueOf(contents[1]),
							Float.valueOf(contents[2])
							)
					);
			}
			
		}
		return watertiles;
	}
	
	private List<Terrain> loadTerrain(){
		//open directory
		String terrainFolder = rootFolder + "level" + levelAppendage + "/terrain/";
		System.out.println("Loading terrain from: " + terrainFolder);
		List<File> terrainFolders = FileAccess.listFolders(new File(terrainFolder));
		List<Terrain> terrainUnits = new ArrayList<Terrain>();
		if(terrainFolders.isEmpty()){
			System.err.println("Error: failed to read terrain folders from: " + terrainFolder);
			return terrainUnits;
		}
		//Load up terrain
		for(File folder: terrainFolders){
			Terrain t = loadTerrainUnit(folder);
			if(t != null){
				terrainUnits.add(t);
			}
		}
		return terrainUnits;	
	}
	
	private Terrain loadTerrainUnit(File folder){
		Terrain terrain = null;
		//make sure it is a terrainFolder
		if(folder.getName().startsWith("terrain")){
			String currentDirectory = folder.getPath()  +"/";
			List<File> folderContents = FileAccess.listFiles(folder);
			//if multiple terrain get xz
			String[] nameBreak = folder.getName().split("_");
			int x = Integer.parseInt(nameBreak[1],10); //point on x grid
			int z = Integer.parseInt(nameBreak[2],10); //point on z grid
			
			//crate TexturePack
			try{
				
				TerrainTexture backgroundTexture = 
						new TerrainTexture(loader.loadTexture(currentDirectory + FileAccess.matchFile(folderContents,".*bgTexture.*")));
				TerrainTexture rTexture = 
						new TerrainTexture(loader.loadTexture(currentDirectory + FileAccess.matchFile(folderContents,".*rTexture.*")));
				TerrainTexture gTexture = 
						new TerrainTexture(loader.loadTexture(currentDirectory + FileAccess.matchFile(folderContents,".*gTexture.*")));
				TerrainTexture bTexture = 
						new TerrainTexture(loader.loadTexture(currentDirectory + FileAccess.matchFile(folderContents,".*bTexture.*")));
				
				TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(currentDirectory + FileAccess.matchFile(folderContents,".*blend_map.*")));
				
				TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
				
				System.out.println(x);
				terrain = new Terrain(x,z,loader,texturePack, blendMap, "height_map");
				System.out.println("Adding Terrain: ");
				System.out.println("x: " + Integer.toString(x) + "z: " + Integer.toString(z));
				boolean defined = rTexture!=null && gTexture!=null && bTexture!=null && backgroundTexture!=null;
				System.out.println("Four textures defined?: " + Boolean.toString(defined));
				System.out.println("BlendMap defined? :" + Boolean.toString(blendMap != null));
			
			}catch(Exception e){
				System.err.println("Error: building terrains, could not load TerrainTextures from: " + currentDirectory);
				//return what we do have... 
				return null;
			}
		}
		return terrain;
		
	}
	
	
	private void loadAssets(){
		//only use level for level specific info rather than copying redundant info
		//ie objs and textures
		String assetFolder = rootFolder  + "assets/";
		System.out.println("Load assets from: " + assetFolder);
		System.out.println("Number of asset groups: " + String.valueOf(classAndLocation.size()));
		for(Pair<EntityMembers, String> p: classAndLocation){
//			System.out.println(p.getR());
//			System.out.println(p.getL());
			loadEntityUnit(p.getL(), new File(assetFolder + p.getR()));
		}
		
	}
	
	private void loadEntityUnit(EntityMembers instance, File folder) {
		//do essentially model load up here before object specifics come up
		List<File> files = FileAccess.listFiles(folder);
		List<File> meta_files = FileAccess.listFiles(
				new File(rootFolder + "level" + this.levelAppendage
				+ "/asset_meta/"));
		String texture_meta = FileAccess.readTextFile(folder.getPath()+"/" + FileAccess.matchFile(files, ".*meta.*"),true);
		String entity_meta = FileAccess.readTextFile(this.rootFolder + "level" + this.levelAppendage +
				"/asset_meta/" + FileAccess.matchFile(meta_files, folder.getName()+".*meta.*"),true);
		//check meta if normalMapped obj
		RawModel rawModel;
		boolean normalMapped = isNormalMapped(texture_meta);
		if(normalMapped){
			//probably want these models to be shinder and more reflective to be more noticeable...
			rawModel = NormalMappedObjLoader.loadOBJ(folder.getPath() + "/rawModel.obj", loader);
		}else{
			rawModel = OBJLoader.loadObjModel(folder.getPath() + "/rawModel.obj", loader);
		}
		
		TexturedModel textureModel  = new TexturedModel(rawModel,
				new ModelTexture(loader.loadTexture(folder.getPath()+"/" + FileAccess.matchFile(files, ".*texture.*"))));
		
		if(normalMapped){
			textureModel.getTexture().setNormalMap(loader.loadTexture(folder.getPath()+"/" + FileAccess.matchFile(files, ".*normal.*")));
			textureModel.getTexture().setReflectivity(.5f);
			textureModel.getTexture().setShineDamper(5);
		}
		//meta to see if anything to do to texture
//		System.out.println(this.rootFolder + "level" + this.levelAppendage +
//				"/asset_meta/");
//		System.out.println("entity meta");
//		System.out.println(files);
//		System.out.println(folder.getName()+".*meta.*");
//		System.out.println(entity_meta);
		System.out.println( this.rootFolder + "level" + this.levelAppendage +
				"/asset_meta/" + FileAccess.matchFile(meta_files, folder.getName()+".*meta.*"));
		instance.factoryFromFile(textureModel, texture_meta, entity_meta,  mc);
	}

	public static boolean isNormalMapped(String texture_meta){
		String[] perLine = texture_meta.split("\n");
		for(String li: perLine){
			if(li.contains("normalMapped") && li.contains("true")){
				return true;
			}
		}
		return false;
	}
	
	private void loadUpClassFilePairs() {
			classAndLocation.add(new Pair<EntityMembers, String>( EntityMembers.AirBoat, "airboat"));
			classAndLocation.add(new Pair<EntityMembers, String>( EntityMembers.VisualEntity, "fern"));
			classAndLocation.add(new Pair<EntityMembers, String>( EntityMembers.VisualEntity, "grass"));
			classAndLocation.add(new Pair<EntityMembers, String>( EntityMembers.VisualEntity, "boulder"));
		
	}

}
