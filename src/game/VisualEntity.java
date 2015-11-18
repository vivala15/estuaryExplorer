package game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import controller.MainController;
import entities.Entity;
import models.TexturedModel;

public class VisualEntity extends Entity<VisualEntity>{

	public VisualEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}

	public VisualEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale) {
		// TODO Auto-generated constructor stub
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public static VisualEntity factoryFromFile(File file) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public VisualEntity factoryFromFile(TexturedModel textureModel,String texture_meta, String entity_meta, MainController mc) {
//		//Analyze texture_meta and adjustsettings of texture
//		
//		//read entity_meta for entity creation
//		List<VisualEntity> entities = new ArrayList<VisualEntity>();
//		String[] lineSplit = entity_meta.split("\n");
//		for(String line : lineSplit){
//			String[] lineSep = line.split("\\s+");
//			Vector3f pos = new Vector3f(
//					Float.valueOf(lineSep[0]),
//					Float.valueOf(lineSep[1]),
//					Float.valueOf(lineSep[2])
//					);
//			float rotx = Float.valueOf(lineSep[3]);
//			float roty = Float.valueOf(lineSep[4]);
//			float rotz = Float.valueOf(lineSep[5]);
//			float scale = Float.valueOf(lineSep[6]);
//			VisualEntity airboat = new VisualEntity(
//					textureModel,
//					pos,
//					rotx,
//					roty,
//					rotz,
//					scale
//					);
//			entities.add(airboat);
//		}
//		//Add entities to physical world and renderer
//		mc.getEntities().addAll(entities);
//		//do NOT  add to physical world
//		
//		
//		return null;
//	}
 
}
