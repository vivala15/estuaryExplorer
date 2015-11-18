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
import entities.BoatPhysicalEntity;
import entities.Entity;
import entities.PhysicalEntity;
import models.TexturedModel;
import terrains.Terrain;
import textures.ModelTexture;

public class AirBoat extends BoatPhysicalEntity{

	
	private static float mass = 100.0f;
	private float maxSpeed;
	
	public AirBoat(TexturedModel model, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale, mass);
		
		Matrix4f transformShape = new Matrix4f();
		transformShape.setIdentity();
		transformShape.translate(new Vector3f(-10.0f, 2f, 5f));
		//resolve discrepencies between model and physical position
		super.setTransformShapeToRender(transformShape);
		super.setDragCoefficient(50.0f);
		//Thrust over drag coefficient
		this.maxSpeed = 20000/100f/4f;
	
	}

	@Override
	public void setMass(float mass) {
		this.mass = mass;
	}

	public float getMass(){
		return this.mass;
	}

	@Override
	public void generalSubClassBehavior() {
		float length = this.getVelocity().length();
		this.setRotX(-20f*Math.min(1.0f,length/this.maxSpeed));
	}






}
