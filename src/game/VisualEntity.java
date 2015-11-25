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

	@Override
	public void deleteSelfFromWorld(MainController mc) {
		if(!mc.getEntities().remove(this)){
			mc.getNormalMapEntities().remove(this);
		}
		
	}

 
}
