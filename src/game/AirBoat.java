package game;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;

import controller.MainController;
import entities.Entity;
import entities.PhysicalEntity;
import models.TexturedModel;
import textures.ModelTexture;

public class AirBoat extends PhysicalEntity{

	
	private static float mass = 1000.0f;
	private static CollisionShape collisionShape = new BoxShape(new javax.vecmath.Vector3f(6f, 1f,10f));
	
	public AirBoat(TexturedModel model, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale, mass, collisionShape);
		
		Matrix4f transformShape = new Matrix4f();
		transformShape.setIdentity();
		transformShape.translate(new Vector3f(-10.0f, -4f, 5f));
		
		
		super.setTransformShapeToRender(transformShape);
		
	}

	@Override
	public float setMass() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMass(){
		return this.mass;
	}





}
