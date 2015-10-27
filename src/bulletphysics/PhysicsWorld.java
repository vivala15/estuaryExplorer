package bulletphysics;

import java.util.HashMap;
import java.util.HashSet;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

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
import terrains.Terrain;
import toolbox.Maths;

public class PhysicsWorld {

	HashSet<CollisionShape> shapeCheck = new HashSet<CollisionShape>();
	private ObjectArrayList<CollisionShape> collisionShapes = 
			new ObjectArrayList<CollisionShape>();
	private ObjectArrayList<CollisionObject> collObjects = new ObjectArrayList<CollisionObject>();
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private DiscreteDynamicsWorld dynamicsWorld;
	private int DEBUG_MODE;
	private BvhTriangleMeshShape terrainMeshShape = new BvhTriangleMeshShape();
	private Transform originGetter = new Transform();
	private TriangleIndexVertexArray triangleIndexedVertexArray = new TriangleIndexVertexArray();
	public PhysicsWorld(){
		
	}

	public void addTerrainObject(Terrain terrain){
		TriangleIndexVertexArray triangleIndexedVertexArray = new TriangleIndexVertexArray();
		triangleIndexedVertexArray.addIndexedMesh(terrain.getIndexedMeshShape());
		//not sure what implication of booleans are? - this is the collisionShape
		terrainMeshShape = new BvhTriangleMeshShape(triangleIndexedVertexArray, true, true);
		MotionState groundMotionState = new DefaultMotionState(
				new Transform(new Matrix4f(
						new Quat4f(0,0,0,1), //rot
						new Vector3f(0,0,0), 1.0f))); // pos
		
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0,
				groundMotionState, terrainMeshShape,
				new Vector3f(0,0,0));
		groundBodyConstructionInfo.restitution = .25f;
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		dynamicsWorld.addRigidBody(groundRigidBody);
		collisionShapes.add(terrainMeshShape);
		collObjects.add(groundRigidBody);
		
	}
	
	public void addPhysicalEntityObject(Physical entity){
	
		float mass = entity.getMass();
		CollisionShape collShape= entity.getCollisionShape();
		//do we have the collision shape for this entity type already
		if(!shapeCheck.contains(collShape)){
			collisionShapes.add(collShape);
		}
		
		//collShape.setLocalScaling(new Vector3f(entity.get));
		// rigid body is dynamic if and only if mass is non zero, otherwise static
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			collShape.calculateLocalInertia(mass, localInertia);
		}
		
		org.lwjgl.util.vector.Matrix4f opengl = entity.getInitialTransformationMatrix();
		Matrix4f bullet = new Matrix4f();
		Maths.convertOpenGLToBullet(opengl, bullet);
		Transform startTransform = new Transform(bullet);
		//Create motion state and add to the world
		// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, collShape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		//body.activate(true);
		body.setAngularFactor(.4f);
		body.setFriction(.1f);
		body.setRestitution(.8f);
		entity.setBody(body);
		
		// add the body to the dynamics world
		dynamicsWorld.addRigidBody(body);
		collObjects.add(body);
		
	}
	
	public void initPhysics() {
		// collision configuration contains default setup for memory, collision setup
		collisionConfiguration = new DefaultCollisionConfiguration();

		// use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		broadphase = new DbvtBroadphase();

		// the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;
	
		// TODO: needed for SimpleDynamicsWorld
		//sol.setSolverMode(sol.getSolverMode() & ~SolverMode.SOLVER_CACHE_FRIENDLY.getMask());

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0f, -35f, 0f));
	}
	
	public void takeStep(float dt){
		dynamicsWorld.stepSimulation(dt, 2);
	}
	
	public void basicPlaneGround(){
		//vector of plane, second const is buffer
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0),0.25f /* m */);
		//consider 
		MotionState groundMotionState = new DefaultMotionState(
				new Transform(new Matrix4f(
						new Quat4f(0,0,0,1), //rot
						new Vector3f(0,0,0), 1.0f))); // pos
		
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0,
				groundMotionState, groundShape,
				new Vector3f(0,0,0));
		groundBodyConstructionInfo.restitution = .25f;
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		dynamicsWorld.addRigidBody(groundRigidBody);
		collisionShapes.add(groundShape);
		
		
	}
	
	public void applyForce(){
		RigidBody groundRigidBody = null;
		Transform controlTrans = new Transform();
		groundRigidBody.getMotionState().getWorldTransform(controlTrans);
		Vector3f bodyLoca = controlTrans.origin;
		Vector3f anotherPosition = new Vector3f(); //pull object to anotherPosition
		Vector3f force = new Vector3f();
		force.sub(anotherPosition, bodyLoca);
		//may need to activate rigid body body.activate(true);
		//larger vector larger force in this method
		groundRigidBody.applyCentralForce(force);
	}
	
	public void attachDebugWorld(IDebugDraw debugDraw){
		this.dynamicsWorld.setDebugDrawer(debugDraw);
	}
	public int getDebugMode(){
		return DEBUG_MODE;
	}
	public void setDebugMode(int debugMode){
		DEBUG_MODE = debugMode;
		this.dynamicsWorld.getDebugDrawer().setDebugMode(debugMode);
	}
	public void disableDebugWorld(){
		DEBUG_MODE = DebugDrawModes.NO_DEBUG;
		this.dynamicsWorld.getDebugDrawer().setDebugMode(DebugDrawModes.NO_DEBUG);
	}
	
	/**
	 * This 
	 */
	public void drawDebug(){
		//because this is unimplemented in in jbullet, time to write my own!
		//this.dynamicsWorld.debugDrawWorld();
		
		//get collision shapes and figure something out
		for(CollisionObject co: collObjects){
			CollisionShape cs = co.getCollisionShape();
			//cast as PlyhedralConvexShape, draw the edges...?
			Vector3f pt1 = new Vector3f();
			Vector3f pt2 = new Vector3f();
			boolean tMesh = true;
			if(cs instanceof PolyhedralConvexShape){
				
				PolyhedralConvexShape poly = (PolyhedralConvexShape)cs;
				int edgeNum = poly.getNumEdges();
				for(int i =0; i <edgeNum; i++){
					poly.getEdge(i, pt1, pt2);
					co.getWorldTransform(originGetter);
					originGetter.transform(pt1);
					originGetter.transform(pt2);
					this.dynamicsWorld.getDebugDrawer().drawLine(pt1, pt2, new Vector3f(255f,0f,0f));
				}
			}else if(cs instanceof BvhTriangleMeshShape && tMesh){
				BvhTriangleMeshShape triMesh = (BvhTriangleMeshShape)cs;
				triMesh.getMeshInterface().unLockVertexBase(0);
				
				
				co.getWorldTransform(originGetter);
				//originGetter.transform(v);
				//change it...
				int numV = triMesh.getMeshInterface().getNumSubParts();
				Vector3f scaleSet = new Vector3f(1f,1f,1f);
				Vector3f[] triang = new Vector3f[3];
				triang[0] = new Vector3f();
				triang[1] = new Vector3f();
				triang[2] = new Vector3f();
				for(int i =0; i < numV; i++){
					triMesh.getMeshInterface().unLockVertexBase(i);
					VertexData d = triMesh.getMeshInterface().getLockedVertexIndexBase(i);
//					System.out.println(d.getVertexCount());
					//there are 65k vertices, or one third what there should be...
//					System.out.println(d.getIndexCount());
					int indCount = d.getIndexCount();
					//int indCount = d.getVertexCount();
					for(int ind = 0; ind < indCount; ind ++){
						if(ind % 400 == 0){
						//first index, scale, triangle
						d.getTriangle(ind, scaleSet, triang);
						//d.getTriangle(d.getIndex(ind), scaleSet, triang);
						Vector3f pt1t = new Vector3f(triang[0]);
						Vector3f pt2t = new Vector3f(triang[1]);
						Vector3f pt3t = new Vector3f(triang[2]);
						originGetter.transform(pt1t);
						originGetter.transform(pt2t);
						originGetter.transform(pt3t);
						this.dynamicsWorld.getDebugDrawer().drawLine(pt1t, pt2t, new Vector3f(255f,0f,0f));
						this.dynamicsWorld.getDebugDrawer().drawLine(pt2t, pt3t, new Vector3f(255f,0f,0f));
						this.dynamicsWorld.getDebugDrawer().drawLine(pt1t, pt3t, new Vector3f(255f,0f,0f));
					
						}
					}
				}
			}
			
		}
	}
}
