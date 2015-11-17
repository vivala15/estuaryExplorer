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
	private static float totalVolume = 120f;
	private static CollisionShape collisionShape = new BoxShape(new javax.vecmath.Vector3f(6f, 1f,10f));
	private javax.vecmath.Vector3f[] densityPositions;
	
	public AirBoat(TexturedModel model, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale, mass, collisionShape);
		
		Matrix4f transformShape = new Matrix4f();
		transformShape.setIdentity();
		transformShape.translate(new Vector3f(-10.0f, -4f, 5f));
		
		float hwidth = 5f;
		float hlength = 5f;
		float hheight = 2f;
		
		float baseHeight  = 2f;
		densityPositions = new javax.vecmath.Vector3f[11];
		densityPositions[0] = new javax.vecmath.Vector3f(hwidth,-hheight +baseHeight,hlength);
		densityPositions[1] = new javax.vecmath.Vector3f(hwidth, -hheight+baseHeight, -hlength);
		densityPositions[2] = new javax.vecmath.Vector3f(-hwidth,-hheight+baseHeight, hlength);
		densityPositions[3] = new javax.vecmath.Vector3f(-hwidth,-hheight+baseHeight, -hlength);
		densityPositions[4] = new javax.vecmath.Vector3f(0,-hheight+baseHeight, 0);
		densityPositions[5] = new javax.vecmath.Vector3f(hwidth,hheight+baseHeight,hlength);
		densityPositions[6] = new javax.vecmath.Vector3f(hwidth,hheight+baseHeight,-hlength);
		densityPositions[7] = new javax.vecmath.Vector3f(-hwidth,hheight+baseHeight,hlength);
		densityPositions[8] = new javax.vecmath.Vector3f(-hwidth,hheight+baseHeight,-hlength);
		densityPositions[9] = new javax.vecmath.Vector3f(0,hheight+baseHeight, 0);
		//one near front so it buoyancies more
		densityPositions[10] = new javax.vecmath.Vector3f(0,hheight, hlength);
		

//		densityPositions[0] = new javax.vecmath.Vector3f(hwidth,-hheight,hlength);
//		densityPositions[1] = new javax.vecmath.Vector3f(hwidth,-hlength,-hheight);
//		densityPositions[2] = new javax.vecmath.Vector3f(-hwidth,hlength,-hheight);
//		densityPositions[3] = new javax.vecmath.Vector3f(-hwidth,-hlength,-hheight);
//		densityPositions[4] = new javax.vecmath.Vector3f(0,0, -hheight);
//		densityPositions[5] = new javax.vecmath.Vector3f(hwidth,hlength,hheight);
//		densityPositions[6] = new javax.vecmath.Vector3f(hwidth,-hlength,hheight);
//		densityPositions[7] = new javax.vecmath.Vector3f(-hwidth,hlength,hheight);
//		densityPositions[8] = new javax.vecmath.Vector3f(-hwidth,-hlength,hheight);
//		densityPositions[9] = new javax.vecmath.Vector3f(0,0, hheight);
		
		super.setTransformShapeToRender(transformShape);
		super.setDensityPositions(densityPositions);
		super.setVolumePerDensityPoint(totalVolume/densityPositions.length);
		super.setMassPerDensityPoint(mass/ densityPositions.length);
		
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
