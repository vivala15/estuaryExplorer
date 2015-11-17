package entities;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import bulletphysics.Physical;
import bulletphysics.WaterPhysical;
import models.TexturedModel;
import toolbox.Maths;

public abstract class PhysicalEntity extends Entity implements WaterPhysical{

	private CollisionShape collisionShape;
	private RigidBody body;
	private MotionState motionState; //sent back from Physical World
	
	private float massPerDensityPoint;
	private float volumePerDensityPoint;
	private javax.vecmath.Vector3f[] densityPositions;
	
	
	private float mass;
	private Vector3f scale;
	private Transform readout;
	private Matrix4f transformMatrix;
	private javax.vecmath.Matrix4f bulletOutTransformMatrix;
	private Matrix4f transformShapeToRender;
	
	
	public PhysicalEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float mass, CollisionShape collisionShape) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		readout = new Transform();
		transformMatrix = Maths.createTransformationMatrix(position, rotX, rotY, rotZ, scale);
		bulletOutTransformMatrix = new javax.vecmath.Matrix4f();
		this.collisionShape = collisionShape;
		this.scale = new Vector3f(scale,scale,scale);
	}

	public PhysicalEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float mass, CollisionShape collisionShape) {
		super(model, position, rotX, rotY, rotZ, scale);
		readout = new Transform();
		transformMatrix = Maths.createTransformationMatrix(position, rotX, rotY, rotZ, scale);
		bulletOutTransformMatrix = new javax.vecmath.Matrix4f();
		this.collisionShape = collisionShape;
		this.scale = new Vector3f(scale,scale,scale);
	}
	
	
	public CollisionShape getCollisionShape(){
		return this.collisionShape;
	}
	public float getMass(){
		return this.mass;
	}
	public void setMass(float m){
		this.mass = m;
	}
	
	public void setTheMotionState(MotionState ms){
		this.motionState = ms;
	}
	
	public void setBody(RigidBody body){
		this.body = body;
		this.motionState = body.getMotionState();
	}
	
	public RigidBody getBody(){
		return body;
	}
	
	public Matrix4f getInitialTransformationMatrix(){
		return transformMatrix;
	}
	
	public Matrix4f getTransformShapeToRender(){
		return transformShapeToRender;
	}
	
	public void setTransformShapeToRender(Matrix4f transformShapeToRender){
		this.transformShapeToRender = transformShapeToRender;
	}
	
	@Override
	public Vector3f getPosition(){
		Matrix4f.mul(transformShapeToRender, transformMatrix, transformMatrix);
		return super.getPosition();
	}
	
	public void applyForce(javax.vecmath.Vector3f force,javax.vecmath.Vector3f rel_pos){
		this.body.applyForce(force, rel_pos);
	}
	
	
	/**
	 * Return lwjgl matrix4f for 
	 */
	public Matrix4f getTransformationMatrix(){
		//temporary fix, proper way to use this
		//.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		body.activate();
		body.getMotionState().getWorldTransform(readout);
		readout.getMatrix(bulletOutTransformMatrix);
//		
		Quat4f rot = new Quat4f();
		readout.getRotation(rot);
		
		float y = rot.getY();
		float x = rot.getX();
		float z = rot.getZ();
		float w = rot.getW();
		 float pitch = (float) Math.atan2(2*x*w - 2*y*z, 1 - 2*x*x - 2*z*z);
		 float yaw  = (float) Math.atan2(2*y*w - 2*x*z, 1 - 2*y*y - 2*z*z);
		 float roll   =  (float) Math.asin(2*x*y + 2*z*w);
		
		 Matrix4f offsetTrans = Maths.createTransformationMatrix(new Vector3f(0,0,0), pitch*57.2958f,
				 roll*57.2958f, yaw*57.2958f, 1.0f);
		
		Maths.convertBulletToOpenGL(bulletOutTransformMatrix, transformMatrix);
		
		//bullet doens't inherently scale, so scale now
		transformMatrix.scale(this.scale);
		//Account for graphical offset from the physical collision shape
		//issue is rotating graphical image from its 0 pos, whichis different from origin of collision
		//obj
		//to solve shift transformMatrix center to rigid body center, apply, shift back
//		Vector4f position4 = new Vector4f();
//		Maths.vector3flwjglTovector4flwjgl(super.getPosition(), position4);
//		Vector4f.sub(lwjglBodyCenter, position4, lwjglBodyCenter);
//		Vector3f lwjglBodyCenter3 = new Vector3f();
//		Maths.vector4flwjglTovector3flwjgl(lwjglBodyCenter, lwjglBodyCenter3);
//		//transformMatrix.translate(lwjglBodyCenter3);
//		System.out.println("What are thecenters");
//		Vector4f outp2 = new Vector4f(0f, 0f , 0f,1.0f);
//		Matrix4f.transform(transformMatrix, outp2, outp2);
//		System.out.println(outp2);
//		javax.vecmath.Vector4f outp = new javax.vecmath.Vector4f(0, 0 , 0, 1.0f);
//		bulletOutTransformMatrix.transform( outp);
//		System.out.println( outp);
		//try moving this before transformation...
		//Why does THIS FUCK UP THE ROTATION - because it is independent axes, not object axes...
		//rotate the transform before transforming....
		//Matrix4f.mul(transformMatrix, transformShapeToRender, transformShapeToRender);
		//System.out.println(offsetTrans);
		Matrix4f.mul(offsetTrans, transformShapeToRender, offsetTrans);
//		System.out.println(offsetTrans);
		Vector4f testPoint = new Vector4f(0,0,0,1f);
		Vector3f testP = new Vector3f();
		Matrix4f.transform(offsetTrans, testPoint, testPoint);
//		System.out.println(testPoint);
		//Matrix4f.mul(offsetTrans, transformMatrix, transformMatrix);
		Maths.vector4flwjglTovector3flwjgl(testPoint, testP);
		Matrix4f.translate(testP, transformMatrix, transformMatrix);
//		System.out.println("Pitch: " + Float.toString(pitch));
//		System.out.println("Roll: " + Float.toString(roll));
//		System.out.println("yaw: " + Float.toString(yaw));
	
//		Matrix4f.mul(transformShapeToRender, transformMatrix, transformMatrix);
		
		//lwjglBodyCenter3.scale(-1.0f);
		//transformMatrix.translate(lwjglBodyCenter3);
		return transformMatrix;
	}

	public float getMassPerDensityPoint() {
		return massPerDensityPoint;
	}

	public void setMassPerDensityPoint(float massPerDensityPoint) {
		this.massPerDensityPoint = massPerDensityPoint;
	}

	public javax.vecmath.Vector3f[] getDensityPositions() {
		return densityPositions;
	}

	public void setDensityPositions(javax.vecmath.Vector3f[] densityPositions) {
		this.densityPositions = densityPositions;
	}
	
	public float getVolumePerDensityPoint(){
		return this.volumePerDensityPoint;
	}
	public void setVolumePerDensityPoint(float volumePerDensityPoint){
		this.volumePerDensityPoint = volumePerDensityPoint;
	}
}
