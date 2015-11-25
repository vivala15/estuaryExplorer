package bulletphysics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.PolyhedralConvexShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.collision.shapes.VertexData;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import entities.PhysicalEntity;
import renderengine.BulletDebugRenderer;
import terrains.Terrain;
import toolbox.Maths;
import waterdynamic.OceanModel;

public class PhysicsWorld {

	private int DEBUG_MODE;
	private ArrayList<PhysicalEntity> physicalEntities = new ArrayList<PhysicalEntity>();
	private ArrayList<Terrain> terrains = new ArrayList<Terrain>();
 	private OceanModel waterModel;
	BulletDebugRenderer debuggerDraw;
	public PhysicsWorld(){
	
	}

	/**
	 * Add object to water, this will apply bouncy physics to object based
	 * on the dynamic water mesh...
	 * @param entity
	 */
	
	public void addWaterModel(OceanModel oceanModel){
		this.waterModel = oceanModel;
	}
	
	public void addTerrainObject(Terrain terrain){
		this.terrains.add(terrain);
	}
	
	/**
	 * 
	 * @param entity
	 */
	public void addPhysicalEntityObject(PhysicalEntity entity){
		physicalEntities.add(entity);
	}
	
	public boolean removePhysicalEntityObject(PhysicalEntity entity){
		return physicalEntities.remove(entity);
	}
	
	public void takeStep(float dt){
		//Iterate objects calling physics tick
		for(PhysicalEntity physicalEntity: this.physicalEntities){
			physicalEntity.physicsTick(dt);
			physicalEntity.resolveCollisions();
//			//TODO: searhc function to pull right Terrain object, right now
//			//there is only one
			physicalEntity.resolveTerrainCollision(this.terrains.get(0), 2f);
			physicalEntity.updateRenderedPosition();
			physicalEntity.generalSubClassBehavior();
		}
		
	}
	
	public int getDebugMode(){
		return DEBUG_MODE;
	}
	
	/**
	 * This 
	 */
	public void drawDebug(){
		//because this is unimplemented in in jbullet, time to write my own!
		//this.dynamicsWorld.debugDrawWorld();
		

	}

	public void initPhysics() {
		// TODO Auto-generated method stub
		
	}

	public void attachDebugWorld(BulletDebugRenderer bulletDebugRenderer) {
		debuggerDraw = bulletDebugRenderer;
		
	}
}
