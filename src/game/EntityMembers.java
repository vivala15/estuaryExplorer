package game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import controller.AssetLoader;
import controller.MainController;
import models.TexturedModel;

public enum EntityMembers {

	AirBoat{

		@Override
		public void factoryFromFile(TexturedModel textureModel, String texture_meta, String entity_meta,
				MainController mc) {
			//Analyze texture_meta and adjustsettings of texture
			//read entity_meta for entity creation
			List<AirBoat> entities = new ArrayList<AirBoat>();
			String[] lineSplit = entity_meta.split("\n");
			System.out.println("Airboat entrants");
			System.out.println(lineSplit.length);
			for(String line : lineSplit){
				String[] lineSep = line.split("\\s+");
				Vector3f pos = new Vector3f(
						Float.valueOf(lineSep[0]),
						Float.valueOf(lineSep[1]),
						Float.valueOf(lineSep[2])
						);
				float rotx = Float.valueOf(lineSep[3]);
				float roty = Float.valueOf(lineSep[4]);
				float rotz = Float.valueOf(lineSep[5]);
				float scale = Float.valueOf(lineSep[6]);
				AirBoat airboat = new AirBoat(
						textureModel,
						pos,
						rotx,
						roty,
						rotz,
						scale
						);
				System.out.println("Adding Airboat");
				entities.add(airboat);
			}
			//Add entities to physical world and renderer
			mc.getEntities().addAll(entities);
			for(AirBoat boat: entities){
				mc.getPhysicsWorld().addPhysicalEntityObject(boat);
			}
			
		}
		
	},
	VisualEntity{

		@Override
		public void factoryFromFile(TexturedModel textureModel, String texture_meta, String entity_meta,
				MainController mc) {
			//Analyze texture_meta and adjustsettings of texture

			//read entity_meta for entity creation
			List<VisualEntity> entities = new ArrayList<VisualEntity>();
			String[] lineSplit = entity_meta.split("\n");
			if(!entity_meta.isEmpty()){
			for(String line : lineSplit){
				String[] lineSep = line.split("\\s+");
				Vector3f pos = new Vector3f(
						Float.valueOf(lineSep[0]),
						Float.valueOf(lineSep[1]),
						Float.valueOf(lineSep[2])
						);
				float rotx = Float.valueOf(lineSep[3]);
				float roty = Float.valueOf(lineSep[4]);
				float rotz = Float.valueOf(lineSep[5]);
				float scale = Float.valueOf(lineSep[6]);
				VisualEntity entity = new VisualEntity(
						textureModel,
						pos,
						rotx,
						roty,
						rotz,
						scale
						);
				entities.add(entity);
			}
			//Add entities to physical world and renderer
			//check if its normal mapped or not
			if(AssetLoader.isNormalMapped(texture_meta)){
				mc.getNormalMapEntities().addAll(entities);
			}else{
				mc.getEntities().addAll(entities);
			}
			//do NOT  add to physical world
			}
			
		}
		
	};
	
	
	public abstract void factoryFromFile(TexturedModel textureModel, String texture_meta, String entity_meta, MainController mc);
}
