package entities;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import bulletphysics.Physical;
import models.TexturedModel;
import terrains.Terrain;
import toolbox.Maths;

public abstract class PhysicalEntity extends Entity implements Physical {

	private float dragCoefficient = 0.0f;

	private float mass;
	private Matrix4f transformMatrix;
	private Matrix4f transformShapeToRender;

	// distinct from entity position used for rendering...
	private Vector4f physicalPosition;
	private Vector3f velocity = new Vector3f(0f, 0f, 0f);

	private Vector3f forceSum = new Vector3f(0f, 0f, 0f);

	//not sure if this is correct, but equate 
	//pitch, roll, yaw, with rx, ry, rz respectively
	
	private static float GRAVITY = -9.8f * 1f;

	private Matrix4f currentTransformMatrix;

	public PhysicalEntity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float mass) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		transformMatrix = Maths.createTransformationMatrix(position, rotX, rotY, rotZ, scale);
		this.mass = mass;
		this.physicalPosition = new Vector4f(position.x, position.y, position.z, 1.0f);
		currentTransformMatrix = super.getTransformationMatrix();
	}

	public PhysicalEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			float mass) {
		super(model, position, rotX, rotY, rotZ, scale);
		transformMatrix = Maths.createTransformationMatrix(position, rotX, rotY, rotZ, scale);
		this.mass = mass;
		this.physicalPosition = new Vector4f(position.x, position.y, position.z, 1.0f);
		currentTransformMatrix = super.getTransformationMatrix();
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(float m) {
		this.mass = m;
	}

	public float getDragCoefficient() {
		return dragCoefficient;
	}

	public void setDragCoefficient(float dragCoefficient) {
		this.dragCoefficient = dragCoefficient;
	}
	
	public void setPhysicalPosition(Vector4f phys) {
		this.physicalPosition = phys;
	}

	public Matrix4f getInitialTransformationMatrix() {
		return transformMatrix;
	}

	public Matrix4f getTransformShapeToRender() {
		return transformShapeToRender;
	}

	public void setTransformShapeToRender(Matrix4f transformShapeToRender) {
		this.transformShapeToRender = transformShapeToRender;
	}

	// @Override
	// public Vector3f getPosition(){
	// Matrix4f.mul(transformShapeToRender, transformMatrix, transformMatrix);
	// return super.getPosition();
	// }

	public void applyExternalForce(Vector3f externalForce) {
		Vector3f.add(this.forceSum, externalForce, this.forceSum);
	}

	private void applyDragForce() {
		this.forceSum.x -= this.dragCoefficient * this.velocity.x;
		this.forceSum.y -= this.dragCoefficient * this.velocity.y;
		this.forceSum.z -= this.dragCoefficient * this.velocity.z;
	}

	private void applyGravityForce() {
		this.forceSum.y += this.mass * GRAVITY;
	}

	private void integrateForce(float dt) {
		this.velocity.x += this.forceSum.x * dt / mass;
		this.velocity.y += this.forceSum.y * dt / mass;
		this.velocity.z += this.forceSum.z * dt / mass;
	}

	/**
	 * Do the physics here
	 * 
	 * @param dt
	 */
	public void physicsTick(float dt) {
		// External forces set, apply gravity and drag
		applyDragForce();
		applyGravityForce();
		// Integrate forces into velocities
		integrateForce(dt);
		// Update positions
		this.physicalPosition.x = this.physicalPosition.x + this.velocity.x * dt;
		this.physicalPosition.y += this.velocity.y * dt;
		this.physicalPosition.z += this.velocity.z * dt;
		// Resolve collisions
		// TODO IN PHYSICS WORLD -- THIS SHOULD NOT SEE OTHER OBJECTS
		// Check for terrain
		// TODO IN PHYSICS WORLD -- THIS SHOULD NOT SEE TERRAIN

		// finally clear forces to be reapplied next turn
		this.forceSum.x = 0f;
		this.forceSum.y = 0f;
		this.forceSum.z = 0f;
		
		
	}

	private Vector4f dummyRenderPosition = new Vector4f();

	public void updateRenderedPosition() {
//		System.out.println("Physical");
//		System.out.println(this.getPhysicalPosition());
		Matrix4f.transform(getTransformShapeToRender(), this.getPhysicalPosition(), dummyRenderPosition);
		super.getPosition().x = dummyRenderPosition.x;
		super.getPosition().y = dummyRenderPosition.y;
		super.getPosition().z = dummyRenderPosition.z;
//		System.out.println("render");
//		System.out.println(super.getPosition());
		// this.getPosition().set(10f, 10f, 10f);
	}

	/**
	 * Takes relative force and rotates it to apply in the correct frame
	 * 
	 * @param relativeForce
	 */
	private Vector4f dummyRelativeForceVector = new Vector4f(0f, 0f, 0f, 1f);
	private Vector3f dummyRelativeForceVector3 = new Vector3f(0f, 0f, 0f);
	private Vector4f dummyOrigin = new Vector4f(0f, 0f, 0f, 1f);

	public void applyRelativeForce(Vector3f relativeForce) {
		// Rotate force vector
		dummyRelativeForceVector.x = relativeForce.x;
		dummyRelativeForceVector.y = relativeForce.y;
		dummyRelativeForceVector.z = relativeForce.z;
		Matrix4f.transform(this.getCurrentTransformationMatrix(), dummyRelativeForceVector, dummyRelativeForceVector);
		Matrix4f.transform(this.getCurrentTransformationMatrix(), dummyOrigin, dummyOrigin);
		dummyRelativeForceVector3.x = dummyRelativeForceVector.x - dummyOrigin.x;
		dummyRelativeForceVector3.y = dummyRelativeForceVector.y - dummyOrigin.y;
		dummyRelativeForceVector3.z = dummyRelativeForceVector.z - dummyOrigin.z;
		this.applyExternalForce(dummyRelativeForceVector3);
		// Reset origin
		dummyOrigin.x = 0f;
		dummyOrigin.y = 0f;
		dummyOrigin.z = 0f;
		dummyOrigin.w = 1f;
	}

	/**
	 * Still a stub
	 */
	public void resolveCollisions() {

	}

	public abstract void resolveTerrainCollision(Terrain t, float waterHeight);

	public abstract void generalSubClassBehavior();
	/**
	 * Return lwjgl matrix4f for
	 */
	public Matrix4f getTransformationMatrix() {
		// return transformationMatrix;
		currentTransformMatrix = super.getTransformationMatrix();
		return currentTransformMatrix;
	}

	public Matrix4f getCurrentTransformationMatrix() {
		// return transformationMatrix;
		return currentTransformMatrix;
	}

	public Vector4f getPhysicalPosition() {
		return this.physicalPosition;
	}
	public Vector3f getVelocity() {
		return velocity;
	}
}
